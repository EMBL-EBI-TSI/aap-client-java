package uk.ac.ebi.tsc.aap.client.exception;

/**
 * AAP representation of a {@linkplain org.springframework.security.authentication.LockedException}
 * (to allow for integration of locking exceptions into the established AAP error handling routines).
 * 
 * @author geoff
 */
public class AAPLockedException extends AAPException {

    private static final long serialVersionUID = 3633021718710695884L;

    public static final String CODE_ACCOUNT_LOCKED = "ACCOUNT_LOCKED";

    // No getCode() as exception derived from a non-AAP source 

    /**
     * Initialising constructor.
     * 
     * @param message Exception message.
     */
    public AAPLockedException(final String message) {
        super(message);
    }
}
