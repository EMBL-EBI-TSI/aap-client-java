package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;

/**
 * @author Amelie Cornelis <ameliec@ebi.ac.uk>
 * @since v0.0.1
 */
@Component
public class DomainRepositoryRest implements DomainRepository {

    private final RestTemplate template;

    public DomainRepositoryRest(
            @Value("${aap.domains.url}") String domainsApiUrl,
            @Value("${aap.timeout:180000}") int timeout,
            RestTemplateBuilder clientBuilder) {
        this.template = clientBuilder
                .rootUri(domainsApiUrl)
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();
    }

    @Override
    public Collection<Domain> getDomains(User user, String token) {
        HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
        ResponseEntity<User> response = template.exchange(
                "/users/{reference}/domains",
                HttpMethod.GET, entity, User.class, user.getUserReference());
        return response.getBody().getDomains();
    }

    @Override
    public Domain createDomain(Domain toAdd, String token) {
        HttpEntity<Domain> entity = new HttpEntity<>(toAdd, createHeaders(token));
        ResponseEntity<Domain> response = template.exchange(
                "/domains/", HttpMethod.POST,
                entity, Domain.class);
        return response.getBody();
    }

    @Override
    public Domain deleteDomain(Domain toDelete, String token) {
        HttpEntity<Domain> entity = new HttpEntity<>(toDelete, createHeaders(token));
        ResponseEntity<Domain> response = template.exchange(
                "/domains/{domainReference}", HttpMethod.DELETE,
                entity, Domain.class, toDelete.getDomainReference());
        return response.getBody();
    }

    private HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

}
