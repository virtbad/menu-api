package ch.virtbad.menuapi.endpoints;

import ch.virtbad.menuapi.database.repositories.MenuRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This endpoint does return statistics about the site
 */
@RequestMapping("/stats")
@RestController
@RequiredArgsConstructor
public class StatisticsEndpoint {

    private final MenuRepository menus;

    @GetMapping("/menu")
    public AmountObject getMenuAmount() {
        return new AmountObject(menus.count());
    }
    @AllArgsConstructor
    private static class AmountObject {
        private long amount;
    }

}
