package uk.ac.ebi.tsc.aap.client.exception;

/**
 * Created by ukumbham on 25/09/2017.
 * Responsible to throw custom error message to the user
 */
public class UserNameOrPasswordWrongException extends RuntimeException {
    public UserNameOrPasswordWrongException(String message,Exception e){
        super(message,e);
    }
}
