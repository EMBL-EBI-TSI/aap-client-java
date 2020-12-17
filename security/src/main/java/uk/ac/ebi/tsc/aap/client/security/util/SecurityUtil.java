package uk.ac.ebi.tsc.aap.client.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import uk.ac.ebi.tsc.aap.client.model.Domain;

/**
 * Controller security utility.
 *  
 * @author geoff
 */
public class SecurityUtil {

    private SecurityUtil() {}

    // https://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/new.html
    public static final String AAP_ADMIN_ROLE = "ROLE_aap.admin";

    /**
     * Retrieve {@link Authentication} object from security context holder in local thread.
     * 
     * @return {@link Authentication} if found, otherwise {@code null};
     */
    public static Authentication retrieveAuthentication() {
        // Security context retrieved from local thread
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }

    /**
     * Is the application user authenticated with an AAP admin role.
     * 
     * @return {@code true} if user roles (see code in {@link Domain#getAuthority()})
     *         include {@value #AAP_ADMIN_ROLE}, otherwise {@code false}.
     */
    public static boolean hasAdminRole() {
        final Authentication authentication = retrieveAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                             .stream()
                             .map(r -> (Domain) r)
                             // Note: Domain#getAuthority() prepends "ROLE_" to domain name.
                             .anyMatch(r -> r.getAuthority().equals(AAP_ADMIN_ROLE));
    }

    /**
     * Retrieve authenticated user's {@linkplain Authentication} name.
     * <p>
     * In the AAP API ecosystem the authenticated name is <b>not</b> the username, name, nickname, etc.,
     * it's the user's full reference, i.e. {@code usr-<UUID>}.
     * 
     * @return (Spring Security) user's name, aka. the user's full reference. {@code null} if not
     *         available -- although it never should be.
     */
    public static String retrieveAuthenticatedName() {
        final Authentication authentication = retrieveAuthentication();
        if (authentication == null) {
            return null;
        }

        return authentication.getName();
    }
}