package gabtemp.vanhackathon.resource;

import java.util.Collections;
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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
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
}