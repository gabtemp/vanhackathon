package gabtemp.vanhackathon.resource;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

import com.google.common.collect.Iterables;
import gabtemp.vanhackathon.domain.Restaurant;
import gabtemp.vanhackathon.repository.RestaurantRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Component
@Path("/restaurant")
public class RestaurantResource {

    private final Logger LOG = Logger.getLogger(RestaurantResource.class);

    @Resource
    private RestaurantRepository repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        LOG.info("Finding all registered restaurants.");
        Iterable<Restaurant> all = repository.findAll();
        LOG.info(Iterables.size(all) + " registered restaurants found.");
        return Response.ok(all).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id) {
        LOG.info("Finding registered restaurant with id " + id + ".");
        Optional<Restaurant> restaurant = repository.findById(id);
        if (restaurant.isPresent()) {
            LOG.info("Registered restaurant with id " + id + " found.");
            return Response.ok(restaurant.get()).build();
        } else {
            LOG.info("Registered restaurant with id " + id + " not found.");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("name") String name, @FormParam("address") String address,
            @FormParam("pickUpDays") List<String> days, @FormParam("pickUpTime") String time) {
        LOG.info("Registering new restaurant with the following parameters: " +
                "Name=" + name + ", Address=" + address +
                ", PickUpDays=" + days + ", PickUpTime=" + time);

        if (name == null) {
            LOG.warn("Failed to register a new restaurant: 'Form parameter 'name' is mandatory'");
            return Response.status(BAD_REQUEST).entity("Form parameter 'name' is mandatory").build();
        }

        if (address == null) {
            LOG.warn("Failed to register a new restaurant: 'Form parameter 'address' is mandatory'");
            return Response.status(BAD_REQUEST).entity("Form parameter 'address' is mandatory").build();
        }

        if (time == null) {
            LOG.warn("Failed to register a new restaurant: 'Form parameter 'pickUpTime' is mandatory'");
            return Response.status(BAD_REQUEST).entity("Form parameter 'pickUpTime' is mandatory").build();
        }

        Set<DayOfWeek> resolvedDays;
        if (days == null || days.isEmpty()) {
            LOG.info("Form parameter 'pickUpDays' not provided. Using all values.");
            resolvedDays = new HashSet<>(Arrays.asList(DayOfWeek.values()));
        } else {
            resolvedDays = days.stream().map(DayOfWeek::valueOf).collect(Collectors.toSet());
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setAvailablePickUpDays(resolvedDays);
        try {
            restaurant.setPickUpTime(LocalTime.parse(time, DateTimeFormatter.ISO_TIME));
        } catch (DateTimeParseException e) {
            String message = "Invalid time format (" + time + ") for the 'pickUpTime' field. The allowed format is 'hh:mm'," +
                    " where 'hh' is the hour from 0 to 23 and 'mm' are the minutes from 0 to 59.";
            LOG.error(message, e);
            return Response.status(BAD_REQUEST).entity(message).build();
        }

        Restaurant saved = repository.save(restaurant);
        LOG.info("New restaurant registered with id " + saved.getId());
        return Response.created(URI.create("/restaurant/" + saved.getId())).entity(saved).build();
    }
}
