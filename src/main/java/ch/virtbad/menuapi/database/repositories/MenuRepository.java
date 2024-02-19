package ch.virtbad.menuapi.database.repositories;

import ch.virtbad.menuapi.database.Menu;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MenuRepository extends CrudRepository<Menu, UUID> {

    @Query("SELECT m.id FROM Menu m")
    List<UUID> findAllIds();

    List<Menu> findAll(Pageable page);
    List<Menu> findAllByDateBetween(Date start, Date end);
    List<Menu> findAllByDateBetweenOrderByChannelAsc(Date start, Date end);
    List<Menu> findAllByDateGreaterThanOrderByDateAscChannelAsc(Date date);
    List<Menu> findAllByDate(Date date);
    @Transactional
    void deleteAllByDateAndChannel(Date date, int channel);
}
