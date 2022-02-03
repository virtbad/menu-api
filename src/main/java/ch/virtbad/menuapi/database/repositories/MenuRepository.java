package ch.virtbad.menuapi.database.repositories;

import ch.virtbad.menuapi.database.Menu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface MenuRepository extends CrudRepository<Menu, UUID> {

    @Query("SELECT m.id FROM Menu m")
    List<UUID> findAllIds();

}
