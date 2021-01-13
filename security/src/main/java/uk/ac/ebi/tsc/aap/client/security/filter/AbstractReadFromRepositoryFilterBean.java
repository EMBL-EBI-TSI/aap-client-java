package uk.ac.ebi.tsc.aap.client.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import uk.ac.ebi.tsc.aap.client.security.UserAuthentication;
import uk.ac.ebi.tsc.aap.client.security.audit.Action;
import uk.ac.ebi.tsc.aap.client.security.audit.Actor;
import uk.ac.ebi.tsc.aap.client.security.audit.SecurityLogger;
import uk.ac.ebi.tsc.aap.client.security.audit.Severity;
import uk.ac.ebi.tsc.aap.client.security.repo.GenericUserRepository;
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

    // "Token-authenticated" in messages provides some additional detail in logs.
    private static final String exceptionMsgAccountLocked = "Token-authenticated user %s has locked account";
    private static final String exceptionMsgUserNotFound = "Token-authenticated user %s not found in repository";

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
        Assert.notNull(securityLogger, "Security Logger must not be null.");
        Assert.notNull(userRepository, "User Repository must not be null.");

        this.securityLogger = securityLogger;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws LockedException If token-presenting user has a locked account.
     * @throws UsernameNotFoundException If token-authenticated user is not found in the repository.
     */
    // TODO : Ensure all calling code handles LockedException and UsernameNotFoundException
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain)
                         throws IOException, ServletException, LockedException,
                                UsernameNotFoundException {
        logger.trace("~doFilter() : Invoked by {} ", SecurityUtil.retrieveAuthenticatedName());
        final Authentication authentication = SecurityUtil.retrieveAuthentication();

        if (authentication != null &&
            (authentication instanceof UserAuthentication)) {
            try {
                loadUserDetails(authentication);
            } catch (final LockedException lockedException) {
                // as StatelessAuthenticationFilter!
            } catch (final UsernameNotFoundException usernameNotFoundException) {
                // as 
            }
        } else {
            // Request not requiring post-token authentication -- let it pass through.
            logger.trace("~doFilter() : Not reading user repo -- Authentication is {}", authentication);
        }

        chain.doFilter(request, response);
    }

    /**
     * Perform the loading of persisted user details into the passed authentication object.
     * 
     * @param authentication Non-{@code null} {@link Authentication} object to load details into.
     * @throws LockedException If token-presenting user has a locked account.
     * @throws UsernameNotFoundException If token-authenticated user is not found in the repository.
     */
    protected abstract void loadUserDetails(Authentication authentication)
                                            throws LockedException, UsernameNotFoundException;

    /**
     * Retrieve the autowired security logger.
     * 
     * @return Security logger.
     */
    protected SecurityLogger getSecurityLogger() {
        return securityLogger;
    }

    /**
     * Retrieve the autowired user repository.
     * 
     * @return User repository.
     */
    protected GenericUserRepository getUserRepository() {
        return userRepository;
    }

    private void logSecurity(final String message, final String identifier) {
        final Map<String, Object> additionalData = new HashMap<String, Object>(1);
        additionalData.put("Activity", message);
        getSecurityLogger().logActionOnSelf(Severity.WARN, Action.INDETERMINABLE, Actor.USER,
                                            identifier, additionalData);
    }

    /**
     * Logs the locked account situation in the security logging and throws a
     * {@link LockedException}
     * 
     * @param userIdentifier Identification used to retrieve user from repository.
     * @throws LockedException In all circumstances.
     */
    protected void logAndThrowAccountLocked(final String userIdentifier)
                                            throws LockedException {
        final String message = String.format(exceptionMsgAccountLocked, userIdentifier);
        logSecurity(message, userIdentifier);
        throw new LockedException(message);
    }

    /**
     * Logs the a user not found situation in the security logging and throws a
     * {@link UsernameNotFoundException}.
     * 
     * @param userIdentifier Identification used to retrieve user from repository.
     * @throws UsernameNotFoundException In all circumstances.
     */
    protected void logAndThrowUsernameNotFound(final String userIdentifier)
                                               throws UsernameNotFoundException {
        final String message = String.format(exceptionMsgUserNotFound, userIdentifier);
        logSecurity(message, userIdentifier);
        throw new UsernameNotFoundException(message);
    }
}