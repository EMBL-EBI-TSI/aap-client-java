package uk.ac.ebi.tsc.aap.client.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;

/**
 * Apply custom authentication
 *
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 18/07/2016.
 */
public class UserAuthentication implements Authentication {

    private User user;
    private boolean authenticated = true;

    public UserAuthentication(){ }

    UserAuthentication(final User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public UserDetails  getDetails() {
        return user;
    }

    @Override
    public Object getPrincipal() {
        return user.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
