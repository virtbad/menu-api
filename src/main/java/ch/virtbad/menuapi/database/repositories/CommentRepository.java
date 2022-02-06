package ch.virtbad.menuapi.database.repositories;

import ch.virtbad.menuapi.database.Comment;
import ch.virtbad.menuapi.database.Menu;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends CrudRepository<Comment, UUID> {

    List<Comment> findCommentsByMenu_IdOrderByCreated(UUID menu);

}
