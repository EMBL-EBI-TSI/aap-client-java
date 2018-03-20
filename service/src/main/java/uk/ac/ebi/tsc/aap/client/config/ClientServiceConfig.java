package uk.ac.ebi.tsc.aap.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author aniewielska
 * @since 20/03/2018
 *
 * Enables having both separate aap.domains.url and aap.profiles.url properties
 * as well as a common one: aap.url
 */
@Configuration
@PropertySource(value = "classpath:/application-service.properties")
public class ClientServiceConfig {

}
