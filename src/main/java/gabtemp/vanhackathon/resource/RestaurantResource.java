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
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Iterables;
import gabtemp.vanhackathon.domain.Restaurant;
import gabtemp.vanhackathon.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Service for CRUD operations on Restaurant resource
 */
@Component
@Path("/restaurant")
public class RestaurantResource {

    private final Logger LOG = LoggerFactory.getLogger(RestaurantResource.class);

    @Resource
    private RestaurantRepository repository;

    /**
     * Finds all registered restaurants
     *
     * @return a response with the list of all restaurants. Produces a 200 HTTP status code.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        LOG.info("Finding all registered restaurants.");
        Iterable<Restaurant> all = repository.findAll();
        LOG.info(Iterables.size(all) + " registered restaurants found.");
        return Response.ok(all).build();
    }

    /**
     * Find a registered restaurant by the provided id
     *
     * @param id the id of the restaurant
     * @return a response with the restaurant and a HTTP status 200 if it exists. Returns a 404 otherwise
     */
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
            String message = "Registered restaurant with id " + id + " not found.";
            LOG.info(message);
            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }
    }

    /**
     * Register a new restaurant using the provided fields.
     *
     * @param name    the name of the restaurant (required)
     * @param address the address of the restaurant (required)
     * @param pickUpDays    the week days the restaurant is available for leftovers pick-up. if empty the restaurant is
     *                available all days of the week (optional)
     * @param time    the time of day the restaurant is available for leftovers pick-up (required)
     * @return a response with the restaurant and a HTTP status 201 if the restaurant is successfully created with a location
     * header pointing to the newly created resource. Returns a 400 if the mandatory parameters are not provided
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("name") String name, @FormParam("address") String address,
            @FormParam("pickUpDays") List<String> pickUpDays, @FormParam("pickUpTime") String time) {
        LOG.info("Registering new restaurant with the following parameters: " +
                "Name=" + name + ", Address=" + address +
                ", PickUpDays=" + pickUpDays + ", PickUpTime=" + time);

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
        if (pickUpDays == null || pickUpDays.isEmpty()) {
            LOG.info("Form parameter 'pickUpDays' not provided. Using all values.");
            resolvedDays = new HashSet<>(Arrays.asList(DayOfWeek.values()));
        } else {
            resolvedDays = pickUpDays.stream().map(DayOfWeek::valueOf).collect(Collectors.toSet());
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

    /**
     * Updates a registered restaurant with the provided information. Only the non-null fields are updated
     *
     * @param id      the id of the restaurant to update
     * @param name    the name of the restaurant (required)
     * @param address the address of the restaurant (required)
     * @param pickUpDays    the week days the restaurant is available for leftovers pick-up. if empty the restaurant is
     *                available all days of the week (optional)
     * @param time    the time of day the restaurant is available for leftovers pick-up (required)
     * @return a response with the restaurant and a HTTP status 200 if the restaurant is successfully updated with the new information.
     * Returns a 404 HTTP status if the restaurant is not found
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, @FormParam("name") String name,
            @FormParam("address") String address, @FormParam("pickUpDays") List<String> pickUpDays,
            @FormParam("pickUpTime") String time) {

        LOG.info("Updating existing restaurant with the following parameters: " +
                "Name=" + name + ", Address=" + address +
                ", PickUpDays=" + pickUpDays + ", PickUpTime=" + time);

        Optional<Restaurant> restaurant = repository.findById(id);
        if (!restaurant.isPresent()) {
            String message = "Registered restaurant with id " + id + " not found.";
            LOG.info(message);
            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }

        Restaurant updated = restaurant.get();
        if (name != null) {
            updated.setName(name);
        }

        if (address != null) {
            updated.setAddress(address);
        }

        if (pickUpDays != null && !pickUpDays.isEmpty()) {
            Set<DayOfWeek> resolvedDays = pickUpDays.stream().map(DayOfWeek::valueOf).collect(Collectors.toSet());
            updated.setAvailablePickUpDays(resolvedDays);
        }

        if (time != null) {
            try {
                updated.setPickUpTime(LocalTime.parse(time, DateTimeFormatter.ISO_TIME));
            } catch (DateTimeParseException e) {
                String message = "Invalid time format (" + time + ") for the 'pickUpTime' field. The allowed format is 'hh:mm'," +
                        " where 'hh' is the hour from 0 to 23 and 'mm' are the minutes from 0 to 59.";
                LOG.error(message, e);
                return Response.status(BAD_REQUEST).entity(message).build();
            }

        }

        Restaurant saved = repository.save(updated);
        return Response.ok(saved).build();
    }

    /**
     * Deletes a registered restaurant with the provided id
     *
     * @param id the id of the restaurant
     * @return a response with the deleted restaurant and a HTTP status 200 if the restaurant is successfully deleted.
     * Returns a 404 HTTP status if the restaurant is not found
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        LOG.info("Deleting registered restaurant with id " + id + ".");
        Optional<Restaurant> restaurant = repository.findById(id);
        if (!restaurant.isPresent()) {
            String message = "Registered restaurant with id " + id + " not found.";
            LOG.info(message);
            return Response.status(Response.Status.NOT_FOUND).entity(message).build();
        }
        repository.delete(restaurant.get());
        LOG.info("Registered restaurant with id " + id + " deleted.");
        return Response.ok(restaurant.get()).build();
    }
}
