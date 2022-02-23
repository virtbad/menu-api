package ch.virtbad.menuapi.endpoints;

import ch.virtbad.menuapi.database.Menu;
import ch.virtbad.menuapi.database.Price;
import ch.virtbad.menuapi.database.repositories.MenuRepository;
import ch.virtbad.menuapi.database.repositories.PriceRepository;
import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.exception.EmptyQueryException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

/**
 * This collection of endpoints are used to manipulate or get large numbers of menus.
 */
@RequestMapping("/menu")
@RestController
public class MenuEndpoint {

    private final MenuRepository menus;
    private final PriceRepository prices;
    private final EntityManagerFactory factory;

    private FullTextEntityManager search;
    private QueryBuilder searchBuilder;

    private EntityManager manager;

    @Value("${custom.rest.pagesize:20}")
    private int pageSize;

    public MenuEndpoint(MenuRepository menus, PriceRepository prices, EntityManagerFactory factory) {
        this.menus = menus;
        this.prices = prices;
        this.factory = factory;

        createEntityManagement(true);
    }

    /**
     * Creates the entity managers and search providers
     * @param reindex whether to reindex the database
     */
    public void createEntityManagement(boolean reindex) {
        manager = factory.createEntityManager();

        search = Search.getFullTextEntityManager(manager);
        if (reindex) {
            try {
                search.createIndexer().startAndWait();
            } catch (InterruptedException e) {
                System.err.println("Failed to create indexes.");
                e.printStackTrace();
            }
        }

        searchBuilder = search.getSearchFactory().buildQueryBuilder().forEntity(Menu.class).get();
    }

    /**
     * This request returns information about a menu.
     */
    @GetMapping("/{id}")
    public Menu getMenu(@PathVariable UUID id) {
        Optional<Menu> menu = menus.findById(id);
        if (menu.isEmpty()) throw new MenuNotFound();
        return menu.get();
    }

    /**
     * This request is used to search in the database of menus.
     */
    @GetMapping("/search")
    public List<Menu> searchMenus(@RequestParam(name = "query") String input, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "-1") int channel, @RequestParam(defaultValue = "-1") int label, @RequestParam(defaultValue = "0") long start, @RequestParam(defaultValue = "32503676400000") long end) {
        System.out.println("Searching for: " + input);

        // Apply basic text search query
        Query text;
        try {
            // TODO: Optimize for better menu discoverability, e.g. burger and vegiburger
            text = searchBuilder.keyword().fuzzy().onFields("title", "description").matching(input).createQuery();
        } catch (EmptyQueryException e) {
            return new ArrayList<>();
        }

        // Apply other criteria like date or channel or whatever
        Criteria criteria = search.unwrap(Session.class).createCriteria(Menu.class); // TODO: Find a way to use jpa criteria queries together with full text queries.

        if (channel != -1) criteria.add(Restrictions.eq("channel", channel));
        if (label != -1) criteria.add(Restrictions.eq("label", label));
        if (start != Long.MIN_VALUE || end != Long.MAX_VALUE)
            criteria.add(Restrictions.between("date", new Date(start), new Date(end)));

        // Create final query
        FullTextQuery query = search.createFullTextQuery(text).setCriteriaQuery(criteria).setMaxResults(pageSize).setFirstResult(pageSize * page);

        // Execute
        try {
            return query.getResultList();
        } catch (Exception e) { // Catch sql exception
            createEntityManagement(false);
            return query.getResultList();
        }
    }

    /**
     * With this request, you can fetch all menus present.
     */
    @GetMapping("/all")
    public List<Menu> allMenus(@RequestParam(defaultValue = "0") int page) {
        return menus.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "date")));
    }

    /**
     * This request fetches all menus on a specific date.
     */
    @GetMapping("/date")
    public List<Menu> dateMenus(@RequestParam(defaultValue = "0") long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == 0 ? new Date() : new Date(date));

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date start = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date end = calendar.getTime();

        return menus.findAllByDateBetween(start, end);
    }

    /**
     * This request is used to push a new menu into the database.
     */
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

        try {
            manager.persist(dbMenu); // Insert over entity manager so that the index is updated
        } catch (Exception e) { // Catch sql exception
            createEntityManagement(false);
            manager.persist(dbMenu);
        }
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
    public static class MenuNotFound extends RuntimeException { }

    /**
     * This exception is thrown when a localhost only request is made from another host.
     */
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    private static class NotLocalHost extends RuntimeException { }

    /**
     * This exception is thrown when not all features of a body are provided.
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private static class NotAllProvided extends RuntimeException { }
}
