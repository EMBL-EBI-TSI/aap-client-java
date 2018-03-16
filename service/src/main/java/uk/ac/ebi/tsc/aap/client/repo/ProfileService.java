package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.stereotype.Component;
import uk.ac.ebi.tsc.aap.client.model.Profile;

import java.util.Map;

/**
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
        Profile profile = new Profile.Builder().withDomain(domainReference).withAttributes(attributes).build();
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
        return this.repo.getUserProfileAttribute(domainReference, attributeName, token);
    }
}
