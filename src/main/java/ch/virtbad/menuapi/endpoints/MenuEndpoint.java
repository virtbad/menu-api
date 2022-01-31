package ch.virtbad.menuapi.endpoints;

import ch.virtbad.menuapi.database.Menu;
import ch.virtbad.menuapi.database.Price;
import ch.virtbad.menuapi.database.repositories.MenuRepository;
import ch.virtbad.menuapi.database.repositories.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/menu")
@RestController
public class MenuEndpoint {

    private final MenuRepository menus;
    private final PriceRepository prices;

    @GetMapping("")
    public List<UUID> getAllMenus() {
        return menus.findAllIds();
    }

    @GetMapping("/{id}")
    public Menu getMenu(@PathVariable UUID id) {
        Optional<Menu> menu = menus.findById(id);
        if (menu.isEmpty()) throw new MenuNotFound();
        return menu.get();
    }

    @PostMapping("")
    public void pushMenu(HttpServletRequest request, @RequestBody RequestMenu menu) {
        if (!"127.0.0.1".equals(request.getRemoteAddr())) throw new NotLocalHost();

        if (!menu.validate()) throw new NotAllProvided();

        List<Price> prices = new ArrayList<>();
        for (RequestMenu.RequestPrice price : menu.prices) {
            Price db = this.prices.findFirstByTagAndPrice(price.tag, price.price);
            if (db == null) db = this.prices.save(new Price(price.tag.toUpperCase(), price.price));
            prices.add(db);
        }

        Menu dbMenu = new Menu(menu.title, menu.description, menu.date, menu.channel, menu.label, prices);

        menus.save(dbMenu);
        System.out.println("Saved menu: " + menu.title);
    }
    private static class RequestMenu {
        private String title;
        private String description;
        private Date date;
        private Integer channel;
        private Integer label;
        private List<RequestPrice> prices;

        private boolean validate() {
            return title != null && description != null && date != null && channel != null && label != null && prices != null;
        }

        private static class RequestPrice {
            private String tag;
            private float price;
        }
    }

    /**
     * This exception is thrown when a menu is not found.
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Menu not found.")
    private static class MenuNotFound extends RuntimeException { }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    private static class NotLocalHost extends RuntimeException { }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private static class NotAllProvided extends RuntimeException { }

}
