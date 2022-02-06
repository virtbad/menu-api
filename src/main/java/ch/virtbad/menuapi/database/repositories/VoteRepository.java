package ch.virtbad.menuapi.database.repositories;

import ch.virtbad.menuapi.database.Menu;
import ch.virtbad.menuapi.database.User;
import ch.virtbad.menuapi.database.Vote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends CrudRepository<Vote, Vote.VoteJoinKey> {

    boolean existsByIdMenuAndIdUser(Menu id_menu, User id_user);
    Optional<Vote> findByIdMenuAndIdUser(Menu id_menu, User id_user);
    @Transactional
    void removeByIdMenuAndIdUser(Menu id_menu, User id_user);
}
