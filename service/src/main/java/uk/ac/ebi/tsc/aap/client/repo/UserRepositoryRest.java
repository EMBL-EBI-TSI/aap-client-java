package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.tsc.aap.client.model.LocalAccount;

import java.time.Duration;

/**
 * Created by Felix on 24/07/2018.
 */
@Component
public class UserRepositoryRest implements UserRepository {

    private final RestTemplate template;

    public UserRepositoryRest(@Value("${aap.domains.url}") String domainsApiUrl,
                              @Value("${aap.timeout:180000}") int timeout,
                              RestTemplateBuilder clientBuilder) {
            this.template = clientBuilder
                    .rootUri(domainsApiUrl)
                    .setConnectTimeout(Duration.ofMillis(timeout))
                    .setReadTimeout(Duration.ofMillis(timeout))
                    .errorHandler(new AAPResponseErrorHandler())
                    .build();
    }

    /**
     * Create local token using username and password.
     * @param localAccount
     * @return user reference
     *
     */
    @Override
    public String createLocalAccount(LocalAccount localAccount) {
        ResponseEntity<String> response = null;
        HttpEntity<LocalAccount> entity = new HttpEntity<>(localAccount,createHttpHeaders());
        response = this.template.exchange("/auth",
            HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    private static HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        return headers;
    }
}
