package uk.ac.ebi.tsc.aap.client.exception;

/**
 * Created by felix on 19/06/2018.
 * Responsible to throw custom error message to the user
 */
public class AAPException extends RuntimeException {

    public AAPException(Exception e){
        super(e);
    }
    public AAPException(String message){
        super(message);
    }
    public AAPException(String message, Exception e){
        super(message,e);
    }
}
