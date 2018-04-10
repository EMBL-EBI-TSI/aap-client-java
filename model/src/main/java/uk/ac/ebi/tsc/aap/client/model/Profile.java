package uk.ac.ebi.tsc.aap.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Data model for an AAP profile
 */
public class Profile {

    private String reference;
    private User user;
    private Domain domain;
    private Map<String, String> attributes = new HashMap<>();

    private Profile() {}

    public Profile(String reference, User user, Domain domain, Map<String, String> attributes) {
        this.reference = reference;
        this.user = user;
        this.domain = domain;
        this.attributes = attributes;
    }

    public String getReference() {
        return reference;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public User getUser() {
        return user;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Domain getDomain() {
        return domain;
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

    @JsonIgnore
    public String getDomainName() {
        return this.domain != null ?  this.domain.getDomainName() : null;
    }

    @JsonIgnore
    public String getDomainReference() {
        return this.domain != null ?  this.domain.getDomainReference() : null;
    }

    @JsonIgnore
    public String getUserReference() {
        return this.user != null ? this.user.getUserReference() : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        Profile profile;

        /**
         * @see Profile#builder()
         */
        @Deprecated
        public Builder() {
            profile = new Profile();
        }

        /**
         * @see Profile#builder()#withReference(String)
         */
        @Deprecated
        public Builder(String reference) {
            profile = new Profile();
            profile.reference = reference;
        }

        public Builder withReference(String reference) {
            profile.reference = reference;
            return this;
        }

        public Builder withUser(User user) {
            profile.user = user;
            return this;
        }

        public Builder withUser(String userReference) {
            if (userReference != null) {
                profile.user = User.builder().withReference(userReference).build();
            }
            return this;
        }

        public Builder withDomain(String domainReference) {
            if (domainReference != null) {
                profile.domain = Domain.builder().withReference(domainReference).build();
            }
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
