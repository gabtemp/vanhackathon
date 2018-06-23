package gabtemp.vanhackathon.config;

import gabtemp.vanhackathon.resource.RestaurantResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Jersey configuration class, used to register all the endpoints of the application
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(RestaurantResource.class);
    }
}
