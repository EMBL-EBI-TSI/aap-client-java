package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.stereotype.Component;
import uk.ac.ebi.tsc.aap.client.model.Profile;

import java.util.Map;

/**
 * All methods that accept a token as an input parameter can throw
 *  <ul>
 *  <li>TokenNotSuppliedException if empty token is passed,</li>
 *  <li>InvalidJWTTokenException if invalid JWT token string or token from different server is passed,</li>
 *  <li>TokenExpiredException if expired token is passed.</li>
 *  </ul>
 *
 * @author aniewielska
 * @since 16/03/2018
 */
@Component
public class ProfileService {

    private final ProfileRepository repo;

    public ProfileService(ProfileRepository repository) {
        this.repo = repository;
    }

    public Profile createDomainProfile(String domainReference, Map<String, String> attributes, String token) {
        Profile profile = Profile.builder().withDomain(domainReference).withAttributes(attributes).build();
        return this.repo.createProfile(profile, token);
    }

    public Profile getProfile(String profileReference, String token) {
        return this.repo.getProfile(profileReference, token);
    }

    public String getProfileAttribute(String profileReference, String attributeName, String token) {
        return this.repo.getProfileAttribute(profileReference, attributeName, token);
    }

    public Profile updateProfile(String profileReference, Map<String, String> attributes, String token) {
        return this.repo.updateProfile(profileReference, attributes, token);
    }

    /**
     *
     * Currently uses _method query parameter as a workaround for executing PATCH
     * In case of problem with the methos, please let us know
     * or call API endpoint directly
     * @param profileReference
     * @param attributes
     * @param token
     * @return
     */
    public Profile patchProfile(String profileReference, Map<String, String> attributes, String token) {
        return this.repo.patchProfile(profileReference, attributes, token);
    }

    public Profile deleteProfileAttribute(String profileReference, String attributeName, String token) {
        return this.repo.deleteProfileAttribute(profileReference, attributeName, token);
    }

    public Profile getUserProfile(String userReference, String token) {
        return this.repo.getUserProfile(userReference, token);
    }

    public String getUserProfileAttribute(String userReference, String attributeName, String token) {
        return this.repo.getUserProfileAttribute(userReference, attributeName, token);
    }

    public Profile getDomainProfile(String domainReference, String token) {
        return this.repo.getDomainProfile(domainReference, token);
    }

    public String getDomainProfileAttribute(String domainReference, String attributeName, String token) {
        return this.repo.getDomainProfileAttribute(domainReference, attributeName, token);
    }
}
