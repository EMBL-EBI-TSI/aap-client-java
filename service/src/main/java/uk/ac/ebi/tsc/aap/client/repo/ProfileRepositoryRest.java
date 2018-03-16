package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.tsc.aap.client.model.Profile;

import java.util.Map;

import static uk.ac.ebi.tsc.aap.client.util.TokenHeaderBuilder.createHeaders;

/**
 * @author aniewielska
 * @since 16/03/2018
 */
@Component
public class ProfileRepositoryRest implements ProfileRepository {

    private final RestTemplate template;

    public ProfileRepositoryRest(
            @Value("${aap.profiles.url}") String domainsApiUrl,
            @Value("${aap.timeout:180000}") int timeout,
            RestTemplateBuilder clientBuilder) {
        this.template = clientBuilder
                .rootUri(domainsApiUrl)
                .setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();
    }


    @Override
    public Profile createProfile(Profile profile, String token) {
        HttpEntity<Profile> entity = new HttpEntity<>(profile, createHeaders(token));
        ResponseEntity<Profile> response = template.exchange(
                "/profiles", HttpMethod.POST,
                entity, Profile.class);
        return response.getBody();

    }

    @Override
    public Profile getProfile(String profileReference, String token) {
        HttpEntity<?> entity = new HttpEntity<>(null, createHeaders(token));
        ResponseEntity<Profile> response = template.exchange(
                "/profiles/{profileReference}",
                HttpMethod.GET, entity, Profile.class, profileReference);
        return response.getBody();
    }

    @Override
    public String getProfileAttribute(String profileReference, String attributeName, String token) {
        HttpEntity<String> entity = new HttpEntity<>("parameters", createHeaders(token));
        ResponseEntity<String> response = template.exchange(
                "/profiles/{profileReference}/{attributeName}",
                HttpMethod.GET, entity, String.class, profileReference, attributeName);
        return response.getBody();
    }

    @Override
    public Profile updateProfile(String profileReference, Map<String, String> attributes, String token) {
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(attributes, createHeaders(token));
        ResponseEntity<Profile> response = template.exchange(
                "/profiles/{profileReference}", HttpMethod.PUT,
                entity, Profile.class, profileReference);
        return response.getBody();
    }


    @Override
    public Profile patchProfile(String profileReference, Map<String, String> attributes, String token) {
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(attributes, createHeaders(token));
        ResponseEntity<Profile> response = template.exchange(
                "/profiles/{profileReference}", HttpMethod.PATCH,
                entity, Profile.class, profileReference);
        return response.getBody();
    }

    @Override
    public Profile deleteProfileAttribute(String profileReference, String attributeName, String token) {
        HttpEntity<?> entity = new HttpEntity<>(null, createHeaders(token));
        ResponseEntity<Profile> response = template.exchange(
                "/profiles/{profileReference}/{attributeName}", HttpMethod.DELETE,
                entity, Profile.class, profileReference, attributeName);
        return response.getBody();
    }

    @Override
    public Profile getUserProfile(String userReference, String token) {
        HttpEntity<?> entity = new HttpEntity<>(null, createHeaders(token));
        ResponseEntity<Profile> response = template.exchange(
                "/users/{userReference}/profile",
                HttpMethod.GET, entity, Profile.class, userReference);
        return response.getBody();
    }

    @Override
    public String getUserProfileAttribute(String userReference, String attributeName, String token) {
        HttpEntity<?> entity = new HttpEntity<>(null, createHeaders(token));
        ResponseEntity<String> response = template.exchange(
                "/users/{userReference}/profile/{attributeName}",
                HttpMethod.GET, entity, String.class, userReference, attributeName);
        return response.getBody();
    }

    @Override
    public Profile getDomainProfile(String domainReference, String token) {
        HttpEntity<?> entity = new HttpEntity<>(null, createHeaders(token));
        ResponseEntity<Profile> response = template.exchange(
                "/domains/{domainReference}/profile",
                HttpMethod.GET, entity, Profile.class, domainReference);
        return response.getBody();
    }

    @Override
    public String getDomainProfileAttribute(String domainReference, String attributeName, String token) {
        HttpEntity<?> entity = new HttpEntity<>(null, createHeaders(token));
        ResponseEntity<String> response = template.exchange(
                "/domains/{domainReference}/profile/{attributeName}",
                HttpMethod.GET, entity, String.class, domainReference, attributeName);
        return response.getBody();
    }

}


