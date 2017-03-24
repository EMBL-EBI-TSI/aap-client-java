package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Amelie Cornelis <ameliec@ebi.ac.uk>
 * @since v0.0.1
 */
@Component
public class DomainRepositoryRest implements DomainRepository {

    private final RestTemplate template;

    public DomainRepositoryRest(@Value("${aap.domains.url}") String domainsApiUrl, RestTemplateBuilder clientBuilder) {
        this.template = clientBuilder
                .rootUri(domainsApiUrl)
                .build();
    }

    public Collection<String> getDomains(User user, String token) {
        HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
        ResponseEntity<List<Domain>> response = template.exchange(
                "/users/{username}/domains",
                HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<Domain>>() {},
                user.getUsername());
        return response.getBody().stream()
                .map(Domain::getDomainName)
                .collect(Collectors.toList());
    }

    private HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

}
