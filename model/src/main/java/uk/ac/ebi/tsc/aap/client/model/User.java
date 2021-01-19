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
 * <p>
 * It's important to note that this class implements {@link UserDetails} and as such exposes some
 * AAP-specific properties as Spring Security authentication properties, e.g. :
 * <ul>
 *   <li>AAP {@code User#userReference} -> Spr. Sec. {@link UserDetails#getUsername()}</li>
 *   <li>AAP {@code User#domains} -> Spr. Sec. {@link UserDetails#getAuthorities()}</li>
 * </ul>
 */
public class User implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    private String userName;
    private String email;
    private String userReference;
    private String fullName;
    private Set<Domain> domains;
    private String organization;
    // Performs same function as identical property in org.springframework.security.core.userdetails.User
    private boolean accountNonLocked;

    /**
     * @see User#builder()
     */
    @Deprecated
    public User(){}

    /**
     * Partially initialising constructor.
     * <p>
     * It's important to note that this constructor does not assign a value to {@code accountNonLocked}
     * and therefore constructs {@code User} objects which are represented with their accounts in a
     * <b>locked</b> state.<br>
     * This "partial" construction may be necessary on an API endpoint receiving a JWT token, which
     * does not convey account state information, and may necessitate the true account state needing
     * to be updated using {@link #setAccountNonLocked(boolean)}.
     * 
     * @param userName
     * @param email
     * @param userReference
     * @param fullName
     * @param domains
     * @see User#User(String, String, String, String, Set, boolean)
     */
    public User(String userName, String email, String userReference, String fullName, Set<Domain> domains) {
        this.userName = userName;
        this.email = email;
        this.userReference = userReference;
        this.fullName = fullName;
        this.domains = domains;
    }

    /**
     * Depending on whether this is a local or federated account, this will retrieve :
     * <ul>
     *   <li>Federated account : An identifier provided by the federating system</li>
     *   <li>Local account: The user's chosen (i.e. login) name</li>
     * </ul>
     * <p>
     * If you want the user's reference for {@link UserDetails} purposes, use {@link #getUsername()}!
     * 
     * @return User's supplied user name (i.e. not their full name, reference or nickname) if a
     *         local account, otherwise the federated account identifier.
     */
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

    public void setOrganization(String organization) { this.organization = organization; }

    public void setDomains(Set<Domain> domains) {
        this.domains = domains;
    }

    public Set<Domain> getDomains() {
        return domains;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * Assign account non-locked status.
     * 
     * @param accountNonLocked {@code true} to set account as not locked, otherwise {@code false}.
     */
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    
    @Override
    public String toString() {
        return "User [userName=" + userName + ", email=" + email + ", userReference=" + userReference + ", fullName="
                + fullName + ", domains=" + domains + ", organization=" + organization + ", accountNonLocked="
                + accountNonLocked + "]";
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

    /**
     * {@inheritDoc}
     * <p>
     * In the case of {@code aap-client-java} the authorities are represented by the User's
     * {@link #domains}.
     * 
     * @see User#getDomains()
     */
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
    // Not to be confused with getUserName() -- this is overriding UserDetails#getUsername()!
    public String getUsername() {
        return userReference;
    }

    @JsonIgnore
    public String getOrganization() { return organization; }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private User user;

        /**
         * @see User#builder()
         */
        @Deprecated
        public Builder() {
            user = new User();
        }

        /**
         * @see User#builder()#withReference(String)
         */
        @Deprecated
        public Builder(String reference) {
            this();
            withReference(reference);
        }

        public Builder withReference(String reference) {
            user.setUserReference(reference);
            return this;
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

        public Builder withDomains(String... domainNames) {
            Set<Domain> domains = new HashSet<>();
            Arrays.asList(domainNames).forEach(name->domains.add(Domain.builder().withName(name).build()));
            user.setDomains(domains);
            return this;
        }

        /**
         * Assign account non-locked status.
         * 
         * @param accountNonLocked {@code true} to set account as not locked, otherwise {@code false}.
         * @return Builder
         */
        public Builder withAccountNonLocked(final boolean accountNonLocked) {
            user.setAccountNonLocked(accountNonLocked);
            return this;
        }

        public User build() {
            return user;
        }
    }
}
