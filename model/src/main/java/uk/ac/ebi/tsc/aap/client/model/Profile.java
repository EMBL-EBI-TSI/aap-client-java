package uk.ac.ebi.tsc.aap.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Data model for an AAP profile
 */
public class Profile {

    private String reference;
    private User user;
    private Map<String, String> attributes = new HashMap<>();

    private Profile() {}

    public Profile(String reference, User user, Map<String, String> attributes) {
        this.reference = reference;
        this.user = user;
        this.attributes = attributes;
    }

    public String getReference() {
        return reference;
    }

    public User getUser() {
        return user;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    @JsonIgnore
    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    public Map<String,String> getAttributes() {
        return attributes;
    }

    public static class Builder {
        Profile profile;

        public Builder(String reference) {
            profile = new Profile();
            profile.reference = reference;
        }

        public Builder withUser(User user) {
            profile.user = user;
            return this;
        }

        public Builder withUser(String userReference) {
            profile.user = new User.Builder(userReference).build();
            return this;
        }

        public Builder withAttributes(Map<String, String> attributes) {
            profile.attributes = attributes;
            return this;
        }

        public Builder withAttribute(String name, String value) {
            profile.attributes.put(name, value);
            return this;
        }

        public Profile build() {
            return profile;
        }

    }
}
