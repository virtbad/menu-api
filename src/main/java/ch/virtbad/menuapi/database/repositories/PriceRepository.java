package ch.virtbad.menuapi.database.repositories;

import ch.virtbad.menuapi.database.Price;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface
PriceRepository extends CrudRepository<Price, Integer> {

    Price findFirstByTagAndPrice(String tag, float price);

}
