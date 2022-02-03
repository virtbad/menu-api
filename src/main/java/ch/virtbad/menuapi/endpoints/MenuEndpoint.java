package ch.virtbad.menuapi.endpoints;

import ch.virtbad.menuapi.database.Menu;
import ch.virtbad.menuapi.database.Price;
import ch.virtbad.menuapi.database.repositories.MenuRepository;
import ch.virtbad.menuapi.database.repositories.PriceRepository;
import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequestMapping("/menu")
@RestController
public class MenuEndpoint {

    private final MenuRepository menus;
    private final PriceRepository prices;

    private final FullTextEntityManager search;
    private final QueryBuilder searchBuilder;
    private final CriteriaBuilder searchCriteria;

    // TODO: Make config property
    private int searchResultAmount = 10;

    public MenuEndpoint(MenuRepository menus, PriceRepository prices, EntityManagerFactory factory) throws InterruptedException {
        this.menus = menus;
        this.prices = prices;

        search = Search.getFullTextEntityManager(factory.createEntityManager());
        search.createIndexer().startAndWait();

        searchBuilder = search.getSearchFactory().buildQueryBuilder().forEntity(Menu.class).get();

        searchCriteria = search.getCriteriaBuilder();
    }

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

    @GetMapping("/search")
    public List<Menu> searchMenus(@RequestParam(name = "query") String input, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "-1") int channel, @RequestParam(defaultValue = "-1") int label, @RequestParam(defaultValue = "0") long start, @RequestParam(defaultValue = "32503676400000") long end) {
        // Apply basic text search query
        Query text = searchBuilder.keyword().fuzzy().onFields("title", "description").matching(input).createQuery(); // TODO: Optimize for better menu discoverability, e.g. burger and vegiburger

        // Apply other criteria like date or channel or whatever
        Criteria criteria = search.unwrap(Session.class).createCriteria(Menu.class); // TODO: Find a way to use jpa criteria queries together with full text queries.

        if (channel != -1) criteria.add(Restrictions.eq("channel", channel));
        if (label != -1) criteria.add(Restrictions.eq("label", label));
        if (start != Long.MIN_VALUE || end != Long.MAX_VALUE) criteria.add(Restrictions.between("date", new Date(start), new Date(end)));

        // Create final query
        FullTextQuery query = search.createFullTextQuery(text).setCriteriaQuery(criteria).setMaxResults(searchResultAmount).setFirstResult(searchResultAmount * page);

        // Execute
        return query.getResultList();
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
