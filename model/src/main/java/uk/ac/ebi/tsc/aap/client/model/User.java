package uk.ac.ebi.tsc.aap.client.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserReference() {
        return userReference;
    }

    public void setUserReference(String userReference) {
        this.userReference = userReference;
    }

    public String getFullName() {
        return fullName;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.domains;}

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userReference;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
