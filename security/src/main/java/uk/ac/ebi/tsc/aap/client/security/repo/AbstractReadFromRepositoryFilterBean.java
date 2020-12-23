package uk.ac.ebi.tsc.aap.client.security.repo;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.GenericFilterBean;

import uk.ac.ebi.tsc.aap.client.security.UserAuthentication;
import uk.ac.ebi.tsc.aap.client.security.audit.SecurityLogger;
import uk.ac.ebi.tsc.aap.client.security.util.SecurityUtil;

/**
 * Filter which handles transferring persisted state data into an {@linkplain Authentication}
 * which has already been populated by the {@link uk.ac.ebi.tsc.aap.client.security.TokenAuthenticationService}
 * (or subclasses).
 * <p>
 * This filter <b>must</b> appear after the {@link uk.ac.ebi.tsc.aap.client.security.StatelessAuthenticationFilter}
 * in the Spring Security filter chain.
 * 
 * @author geoff
 */
public abstract class AbstractReadFromRepositoryFilterBean extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(AbstractReadFromRepositoryFilterBean.class);

    private final SecurityLogger securityLogger;

    /* Cannot use the UserService here, cos it is probably protected by authorization, so it would
       (rightly)fail as no authentication has been set up yet (that is what this class is in the
       middle of doing) */
    private final GenericUserRepository userRepository;

    /**
     * Initialising constructor.
     * 
     * @param securityLogger Security logging facility.
     * @param userRepository User repository.
     */
    protected AbstractReadFromRepositoryFilterBean(final SecurityLogger securityLogger,
                                                final GenericUserRepository userRepository) {
        this.securityLogger = securityLogger;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     * 
     * Note that this routine is also likely to be throwing potentially component-specific exceptions
     * for situations such as if the user cannot be found.
     * 
     * @throws LockedException If token-presenting user has a locked account.
     */
    // TODO : Make sure UserNotFoundException, UsernameNotFoundException and LockedException is being caught and handled appropriately elsewhere.
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain)
                         throws IOException, ServletException {
        logger.trace("~doFilter() : Invoked by {} ", SecurityUtil.retrieveAuthenticatedName());
        final Authentication authentication = SecurityUtil.retrieveAuthentication();

        if (authentication != null &&
            (authentication instanceof UserAuthentication)) {
            loadUserDetails(authentication);
        } else {
            // Request not requiring post-token authentication -- let it pass through.
            logger.trace("~doFilter() : Not reading user repo -- Authentication is {}", authentication);
        }

        chain.doFilter(request, response);
    }

    /**
     * Perform the loading of persisted user details into the authentication object.
     * 
     * @param authentication Authentication object to load details into.
     */
    public abstract void loadUserDetails(Authentication authentication);

    protected SecurityLogger getSecurityLogger() {
        return securityLogger;
    }

    protected GenericUserRepository getUserRepository() {
        return userRepository;
    }

}