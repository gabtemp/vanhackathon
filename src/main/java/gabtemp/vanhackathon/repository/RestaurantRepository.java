package gabtemp.vanhackathon.repository;

import gabtemp.vanhackathon.domain.Restaurant;
import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {

}
