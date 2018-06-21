package uk.ac.ebi.tsc.aap.client.exception;

/**
 * Created by Felix on 20/06/2018.
 * Responsible to throw custom error message to the user
 */
public class InvalidJWTTokenException extends AAPException {
    public InvalidJWTTokenException(String message){
        super(message);
    }
}
