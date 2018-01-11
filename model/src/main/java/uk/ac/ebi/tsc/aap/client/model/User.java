package uk.ac.ebi.tsc.aap.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Data model for an AAP user
 */
public class User implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    private String userName;
    private String email;
    private String userReference;
    private String fullName;
    private Set<Domain> domains;

    public User(){}

    public User(String userName, String email, String userReference, String fullName, Set<Domain> domains) {
        this.userName = userName;
        this.email = email;
        this.userReference = userReference;
        this.fullName = fullName;
        this.domains = domains;
    }

    public String getUserName() {
        return userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    public String getUserReference() {
        return userReference;
    }

    void setUserReference(String userReference) {
        this.userReference = userReference;
    }

    public String getFullName() {
        return fullName;
    }

    void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDomains(Set<Domain> domains) {
        this.domains = domains;
    }

    public Set<Domain> getDomains() {
        return domains;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userName != null ? !userName.equals(user.userName) : user.userName != null)
            return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return userReference != null ? userReference.equals(user.userReference) : user.userReference == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (userReference != null ? userReference.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.domains;}

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return userReference;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class Builder {

        private User user;

        public Builder(String reference) {
            user = new User();
            user.setUserReference(reference);
        }

        public Builder withUsername(String username) {
            user.setUserName(username);
            return this;
        }

        public Builder withEmail(String email) {
            user.setEmail(email);
            return this;
        }

        public Builder withFullName(String name) {
            user.setFullName(name);
            return this;
        }

        public Builder withDomains(Set<Domain> domains) {
            user.setDomains(domains);
            return this;
        }

        public Builder withDomains(String... domains) {
            Set<Domain> domainsSet = new HashSet<>();
            Arrays.asList(domains).forEach(name->domainsSet.add(new Domain(name,null,null)));
            user.setDomains(domainsSet);
            return this;
        }

        public User build() {
            return user;
        }
    }
}
