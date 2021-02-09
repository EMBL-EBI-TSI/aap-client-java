package uk.ac.ebi.tsc.aap.client.test.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.Profile;
import uk.ac.ebi.tsc.aap.client.model.User;
import uk.ac.ebi.tsc.aap.client.test.value.AapClientVersion;

public class ClientTestUtil {

    /**
     * Authority of AAP admin user.
     */
    public static final String AAP_ADMIN_AUTHORITY = "aap.admin";

    public enum ErrorResponseProperty {
        TIMESTAMP,
        STATUS,
        ERROR,
        MESSAGE,
        PATH,
        EXCEPTION
    }

    /**
     * Create a json representation of a client user {@link Profile}.
     *  
     * @param version
     * @param profileReference
     * @param userName
     * @param email
     * @param userReference
     * @param fullName
     * @param domains
     * @param organization
     * @param accountNonLocked
     * @param attributes
     * @return Json representation.
     * @throws JsonProcessingException
     */
    public static String createClientUserProfileJson(final AapClientVersion version,
                                                     final String profileReference,
                                                     final String userName, final String email,
                                                     final String userReference, final String fullName,
                                                     final Set<Domain> domains, final Object organization,
                                                     final boolean accountNonLocked,
                                                     final Map<String, String> attributes)
                                                     throws JsonProcessingException {
        if (version == null) {
            throw new IllegalArgumentException("Specify a version!");
        }

        Profile profile = null;
        switch (version) {
             case V1_0_6_SNAPSHOT :
             case V1_0_7_SNAPSHOT :
                 /* TODO : This won't help creating legacy json representations if Profile
                  *        structure ever changes because we're using objects! */
                 final User user = User.builder().withUsername(userName)
                                                 .withEmail(email)
                                                 .withReference(userReference)
                                                 .withFullName(fullName)
                                                 .withDomains(domains)
                                                 .withAccountNonLocked(accountNonLocked)
                                                 .build();
                 profile = new Profile(profileReference, user, null, attributes);
                 break;
             default :
                 throw new UnsupportedOperationException("Sorry, version " + version + " not handled yet!");
        }
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(profile);
    }

    /**
     * Create the json representation of a {@link User} to pass to the various API endpoints.
     * <p>
     * {@code Object}s used as args to allow any value (including invalid representations) to be
     * sent.
     * 
     * @param version {@code aap-client-java} version to represent.
     * @param userName User username
     * @param email Email
     * @param userReference User reference
     * @param fullName Name
     * @param organization Organisation
     * @param accountNonLocked User account non-locked (only v1.0.7-SNAPSHOT onwards)
     * @return Json format of object.
     * @see #createUpdateBadUserRegistrationJson()
     */
    public static String createClientUserJson(final AapClientVersion version,
                                              final Object userName, final Object email,
                                              final Object userReference, final Object fullName,
                                              final Set<Domain> domains, final Object organization,
                                              final Object accountNonLocked) {
        if (version == null) {
            throw new IllegalArgumentException("Specify a version!");
        }

        final Map<String, Object> data = new HashMap<String, Object>();
        switch (version) {
            case V1_0_6_SNAPSHOT :
                if (accountNonLocked != null) {
                    throw new IllegalArgumentException("accountNonLocked not available in " + version);
                }
                data.put("userName", userName);
                data.put("email", email);
                data.put("userReference", userReference);
                data.put("fullName", fullName);
                data.put("organization", organization);
                break;
            case V1_0_7_SNAPSHOT :
                data.put("userName", userName);
                data.put("email", email);
                data.put("userReference", userReference);
                data.put("fullName", fullName);
                data.put("organization", organization);
                data.put("accountNonLocked", accountNonLocked);
                break;
            default :
                throw new UnsupportedOperationException("Sorry, version " + version + " not handled yet!");
        }
        return ClientJsonUtil.toJson(data);
    }

    /**
     * Retrieve the specified property from the AAP-specific {@link ErrorResponse} response.
     * 
     * @param errorResponse Error response.
     * @param property Property to extract (the lowercase equivalents are sought).
     * @return Extracted property, or {@code null} if no such property available.
     */
    public static String retrieveErrorResponseProp(final String errorResponse,
                                                   final ErrorResponseProperty property) {
        try {
            final JSONObject jsonObject = new JSONObject(errorResponse);
            return jsonObject.getString(property.toString().toLowerCase());
        } catch (JSONException e) {
            return null;
        }
    }
}