package uk.ac.ebi.tsc.aap.client.exception;

/**
 * AAP representation of a {@linkplain org.springframework.security.core.userdetails.UsernameNotFoundException}
 * (to allow for integration of unrecognised user exceptions into the established AAP error
 * handling routines).
 * 
 * @author geoff
 */
public class AAPUsernameNotFoundException extends AAPException {

    private static final long serialVersionUID = 8860421081304812681L;

    public static final String CODE_USERNAME_NOT_FOUND = "USERNAME_NOT_FOUND";

    // No getCode() as exception derived from a non-AAP source 

    /**
     * Initialising constructor.
     * 
     * @param message Exception message.
     */
    public AAPUsernameNotFoundException(final String message) {
        super(message);
    }
}
