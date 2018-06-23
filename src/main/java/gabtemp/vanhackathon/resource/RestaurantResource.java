package gabtemp.vanhackathon.resource;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gabtemp.vanhackathon.domain.Restaurant;
import gabtemp.vanhackathon.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

@Component
@Path("/restaurant")
public class RestaurantResource {

    @Resource
    private RestaurantRepository repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        Iterable<Restaurant> all = repository.findAll();
        return Response.ok(all).build();
    }

    @GET
    @Path("/{id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id) {
        Optional<Restaurant> restaurant = repository.findById(id);
        if (restaurant.isPresent()) {
            return Response.ok(restaurant.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("name") String name, @FormParam("address") String address,
            @FormParam("day") String day, @FormParam("time") String time) {

        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setAvailablePickUpDays(Collections.singleton(DayOfWeek.valueOf(day)));
        restaurant.setPickUpTime(LocalTime.parse(time, DateTimeFormatter.ISO_TIME));

        Restaurant saved = repository.save(restaurant);

        return Response.created(URI.create("/restaurant/" + saved.getId())).entity(saved).build();
    }
}
