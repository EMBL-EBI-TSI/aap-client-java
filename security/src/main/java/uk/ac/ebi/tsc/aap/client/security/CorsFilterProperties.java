package uk.ac.ebi.tsc.aap.client.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author aniewielska
 * @since 20/11/2019
 */
@Configuration
@ConfigurationProperties("aap-client.cors")
public class CorsFilterProperties {
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
