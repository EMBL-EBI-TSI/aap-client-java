package uk.ac.ebi.tsc.aap.client.repo;

import uk.ac.ebi.tsc.aap.client.model.Profile;

import java.util.Map;

public interface ProfileRepository {
    Profile createProfile(Profile profile, String token);

    Profile getProfile(String profileReference, String token);

    String getProfileAttribute(String profileReference, String attributeName, String token);

    Profile updateProfile(String profileReference, Map<String, String> attributes, String token);

    Profile patchProfile(String profileReference, Map<String, String> attributes, String token);

    Profile deleteProfileAttribute(String profileReference, String attributeName, String token);

    Profile getUserProfile(String userReference, String token);

    String getUserProfileAttribute(String userReference, String attributeName, String token);

    Profile getDomainProfile(String domainReference, String token);

    String getDomainProfileAttribute(String domainReference, String attributeName, String token);
}
