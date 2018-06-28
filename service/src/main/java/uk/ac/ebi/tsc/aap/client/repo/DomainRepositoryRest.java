package uk.ac.ebi.tsc.aap.client.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainRepositoryRest.class);

    public DomainRepositoryRest(
            @Value("${aap.domains.url}") String domainsApiUrl,
            @Value("${aap.timeout:180000}") int timeout,
            RestTemplateBuilder clientBuilder) {
        this.template = clientBuilder
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .rootUri(domainsApiUrl)
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .errorHandler(new AAPResponseErrorHandler())
                .build();
    }

    @Override
    public Collection<Domain> getDomains(User user, String token) {
            String userReference = addPrefixToUserReferenceIfNotContains(user.getUserReference());
            HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
            ResponseEntity<List<Domain>> response = template.exchange(
                    "/users/{reference}/domains",
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<Domain>>() {},
                    userReference);
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
    public Domain deleteDomain(Domain toDelete, String token)  {
            String domainReference = addPrefixToDomainReferenceIfNotContains(toDelete.getDomainReference());
            HttpEntity<Domain> entity = new HttpEntity<>(toDelete, createHeaders(token));
            ResponseEntity<Domain> response = template.exchange(
                    "/domains/{domainReference}", HttpMethod.DELETE,
                    entity, Domain.class, domainReference);
            return response.getBody();
    }

    @Override
    public Domain addUserToDomain(Domain toJoin, User toAdd, String token) {
            String userReference = addPrefixToUserReferenceIfNotContains(toAdd.getUserReference());
            String domainReference = addPrefixToDomainReferenceIfNotContains(toJoin.getDomainReference());
            HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
            ResponseEntity<Domain> response = template.exchange(
                    "/domains/{domainReference}/{userReference}/user",
                    HttpMethod.PUT, entity, Domain.class, domainReference, userReference);
            return response.getBody();
    }

    @Override
    public Domain addManagerToDomain(Domain toJoin, User toAdd, String token) {
            String userReference = addPrefixToUserReferenceIfNotContains(toAdd.getUserReference());
            String domainReference = addPrefixToDomainReferenceIfNotContains(toJoin.getDomainReference());
            HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
            ResponseEntity<Domain> response = template.exchange(
                    "/domains/{domainReference}/managers/{userReference}",
                    HttpMethod.PUT, entity, Domain.class, domainReference, userReference);
            return response.getBody();
    }
    
    @Override
    public Domain getDomainByReference(String reference, String token) {
            String domainReference = addPrefixToDomainReferenceIfNotContains(reference);
            HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
            ResponseEntity<Domain> response = template.exchange(
                    "/domains/{domainReference}",
                    HttpMethod.GET, entity,new ParameterizedTypeReference<Domain>() {}, domainReference);
            return response.getBody();
    }
    
    @Override
    public Domain removeUserFromDomain(User toBeRemoved, Domain toBeUpdated, String token) {
            String userReference = addPrefixToUserReferenceIfNotContains(toBeRemoved.getUserReference());
            String domainReference = addPrefixToDomainReferenceIfNotContains(toBeUpdated.getDomainReference());
            HttpEntity<User> entity = new HttpEntity<>(toBeRemoved, createHeaders(token));
            ResponseEntity<Domain> response = template.exchange(
                    "/domains/{domainReference}/{userReference}/user", HttpMethod.DELETE,
                    entity, Domain.class, domainReference, userReference);
            return response.getBody();
    }

    @Override
    public Domain removeManagerFromDomain(User toBeRemoved, Domain toBeUpdated, String token) {
            String userReference = addPrefixToUserReferenceIfNotContains(toBeRemoved.getUserReference());
            String domainReference = addPrefixToDomainReferenceIfNotContains(toBeUpdated.getDomainReference());
            HttpEntity<User> entity = new HttpEntity<>(toBeRemoved, createHeaders(token));
            ResponseEntity<Domain> response = template.exchange(
                    "/domains/{domainReference}/managers/{userReference}", HttpMethod.DELETE,
                    entity, Domain.class, domainReference, userReference);
            return response.getBody();
    }
    
    @Override
    public Collection<User> getAllUsersFromDomain(String domainReference, String token) {
            String reference = addPrefixToDomainReferenceIfNotContains(domainReference);
            HttpEntity<?> entity = new HttpEntity<>(createHeaders(token));
            ResponseEntity<List<User>> response = template.exchange(
                    "/domains/{domainReference}/users",
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {},
                    reference);
            return response.getBody();
    }

    @Override
    public Collection<User> getAllManagersFromDomain(String domainReference, String token) {
            String reference = addPrefixToDomainReferenceIfNotContains(domainReference);
            HttpEntity<?> entity = new HttpEntity<>(createHeaders(token));
            ResponseEntity<List<User>> response = template.exchange(
                    "/domains/{domainReference}/managers",
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {},
                    reference);
            return response.getBody();
    }

    /***
     * Get logged in user membership domains
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

    /**
     * Gets the logged in user management domains
     *
     * @param token - user token
     * @return List<Domain></Domain> - management domains
     */
    @Override
    public Collection<Domain> getMyManagementDomains(String token) {
            HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
            ResponseEntity<List<Domain>> response = template.exchange(
                    "/my/management",
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<Domain>>() {});
            return response.getBody();
    }

    private HttpHeaders createHeaders(String token){
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set( "Authorization", authHeader );
        }};
    }

    /**
     * This checks for prefix like dom-.Reference not contains corresponding prefix it adds prefix.
     * If prefix already exist in the reference just ignores to add again.
     * If Every thing is fine we will remove this backward compatibility code.
     * @param domainReference
     * @return String - domain reference
     */
    private String addPrefixToDomainReferenceIfNotContains(String domainReference){
        if(domainReference!=null){
           if(!domainReference.startsWith("dom-")){
               return "dom-"+domainReference;
           }
           else return domainReference;
        }
        else return domainReference;
    }

    /**
     * This checks for prefix like usr-.Reference not contains corresponding prefix it adds prefix.
     * If prefix already exist in the reference just ignores to add again.
     * If Every thing is fine we will remove this backward compatibility code.
     * @param userReference
     * @return String - user reference
     */
    private String addPrefixToUserReferenceIfNotContains(String userReference){
        if(userReference!=null){
            if(!userReference.startsWith("usr-")){
                return "usr-"+userReference;
            }
            else return userReference;
        }
        else return userReference;
    }

}
