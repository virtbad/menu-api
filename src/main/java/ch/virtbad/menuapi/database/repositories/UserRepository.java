package ch.virtbad.menuapi.database.repositories;

import ch.virtbad.menuapi.database.Price;
import ch.virtbad.menuapi.database.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
