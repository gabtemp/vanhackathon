package gabtemp.vanhackathon.resource;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import javax.ws.rs.core.Response;

import gabtemp.vanhackathon.domain.Restaurant;
import gabtemp.vanhackathon.repository.RestaurantRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantResourceTest {

    @Mock
    private RestaurantRepository repository;

    @InjectMocks
    private RestaurantResource resource;

    @Test
    public void testFindAllEmpty() {
        Response response = resource.findAll();
        assertThat("GET all empty - response code: ", response.getStatus(), is(200));
        assertThat("GET all empty - response entity: ", response.getEntity(), is(Collections.emptyList()));
    }

    @Test
    public void testFindAll() {
        Restaurant restaurant1 = new Restaurant();
        Restaurant restaurant2 = new Restaurant();

        when(repository.findAll()).thenReturn(asList(restaurant1, restaurant2));

        Response response = resource.findAll();
        assertThat("GET all - response code: ", response.getStatus(), is(200));
        assertThat("GET all- response entity: ", response.getEntity(), is(asList(restaurant1, restaurant2)));
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(14L)).thenReturn(Optional.empty());

        Response response = resource.findById(14L);
        assertThat("GET by ID Not Found - response code: ", response.getStatus(), is(404));
        assertThat("GET by ID Not Found - response entity: ", response.getEntity(), CoreMatchers.nullValue());
    }

    @Test
    public void testFindById() {
        Restaurant restaurant = new Restaurant();

        when(repository.findById(32L)).thenReturn(Optional.of(restaurant));

        Response response = resource.findById(32L);
        assertThat("GET by ID - response code: ", response.getStatus(), is(200));
        assertThat("GET by ID - response entity: ", response.getEntity(), is(restaurant));
    }

    @Test
    public void testCreateWithoutRequiredField() {
        Response response;
        response = resource.create(null, "address", singletonList("FRIDAY"), "23:15");
        assertThat("POST name missing - response code: ", response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat("POST name missing - response entity: ", response.getEntity(),
                is("Form parameter 'name' is mandatory"));

        response = resource.create("name", null, singletonList("FRIDAY"), "23:15");
        assertThat("POST address missing - response code: ", response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat("POST address missing - response entity: ", response.getEntity(),
                is("Form parameter 'address' is mandatory"));

        response = resource.create("name", "address", singletonList("FRIDAY"), null);
        assertThat("POST pickUpTime missing - response code: ", response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat("POST pickUpTime missing - response entity: ", response.getEntity(),
                is("Form parameter 'pickUpTime' is mandatory"));

        response = resource.create("name", "address", singletonList("FRIDAY"), "29:63");
        assertThat("POST pickUpTime invalid - response code: ", response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat("POST pickUpTime invalid - response entity: ", response.getEntity(),
                is("Invalid time format (29:63) for the 'pickUpTime' field. The allowed format is 'hh:mm', " +
                        "where 'hh' is the hour from 0 to 23 and 'mm' are the minutes from 0 to 59."));
    }

    @Test
    public void testCreateSuccessful() {
        when(repository.save(any(Restaurant.class))).thenAnswer(invocationOnMock -> {
            Restaurant argument = invocationOnMock.getArgument(0);
            argument.setId(1L);
            return argument;
        });

        Restaurant expected = new Restaurant();
        expected.setId(1L);

        Response response = resource.create("name", "address", singletonList("FRIDAY"), "23:15");
        assertThat("POST successful - response code: ", response.getStatus(), is(201));
        assertThat("POST successful - response entity: ", response.getEntity(), is(expected));
    }

    @Test
    public void testCreateDeafultDay() {
        when(repository.save(any(Restaurant.class))).thenAnswer(invocationOnMock -> {
            Restaurant argument = invocationOnMock.getArgument(0);
            argument.setId(1L);
            return argument;
        });

        Restaurant expected = new Restaurant();
        expected.setId(1L);
        expected.setAvailablePickUpDays(new HashSet<>(Arrays.asList(DayOfWeek.values())));

        Response response = resource.create("name", "address", null, "23:15");
        assertThat("POST successful - response code: ", response.getStatus(), is(201));
        assertThat("POST successful - response entity: ", response.getEntity(), is(expected));
        assertThat("POST successful - response entity: ", ((Restaurant) response.getEntity()).getAvailablePickUpDays(),
                is(expected.getAvailablePickUpDays()));
    }
}