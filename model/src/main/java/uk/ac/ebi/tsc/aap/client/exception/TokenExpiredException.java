package uk.ac.ebi.tsc.aap.client.exception;

/**
 * Created by Felix on 20/06/2018.
 * Responsible to throw custom error message to the user
 */
public class TokenExpiredException extends AAPException {

    String code = "TOKEN_EXPIRED";
    public String getCode() {
        return code;
    }
    public TokenExpiredException(String message){ super(message); }
}
