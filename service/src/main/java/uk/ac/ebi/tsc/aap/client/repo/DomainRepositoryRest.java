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
        ResponseEntity<List<Domain>> response = template.exchange(
                "/users/{reference}/domains",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Domain>>() {},
                user.getUserReference());
        return response.getBody();
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

    @Override
    public Domain addUserToDomain(Domain toJoin, User toAdd, String token) {
        HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
        ResponseEntity<Domain> response = template.exchange(
                "/domains/{dom-reference}/{usr-reference}/user",
                HttpMethod.PUT, entity, Domain.class, toJoin.getDomainReference(), toAdd.getUserReference());
        return response.getBody();
    }
    
    @Override
    public Domain getDomainByReference(String reference, String token){
    	HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
        ResponseEntity<Domain> response = template.exchange(
                "/domains/dom-{domainReference}",
                HttpMethod.GET, entity,new ParameterizedTypeReference<Domain>() {}, reference);
        return response.getBody();
    }
    
    @Override
    public Domain removeUserFromDomain(User toBeRemoved, Domain toBeUpdated, String token){
    	 HttpEntity<User> entity = new HttpEntity<>(toBeRemoved, createHeaders(token));
         ResponseEntity<Domain> response = template.exchange(
                 "/domains/dom-{domainReference}/usr-{userReference}/user", HttpMethod.DELETE,
                 entity, Domain.class, toBeUpdated.getDomainReference(), toBeRemoved.getUserReference());
         return response.getBody();
    }
    
    @Override
    public Collection<User> getAllUsersFromDomain(String domainReference, String token){
    	 HttpEntity<?> entity = new HttpEntity<>(createHeaders(token));
    	 ResponseEntity<List<User>> response = template.exchange(
                 "/domains/dom-{domainReference}/users",
                 HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {},
                 domainReference);
         return response.getBody();
    }

    /***
     * Get loggedin user membership domains
     * @param token - user token
     * @return list of membership domains
     */
    @Override
    public Collection<Domain> getMyDomains(String token) {
        HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
        ResponseEntity<List<Domain>> response = template.exchange(
                "/my/domains",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Domain>>() {});
        return response.getBody();
    }

    private HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

}
