package uk.ac.ebi.tsc.aap.client.exception;

/**
 * Created by felix on 19/06/2018.
 * Responsible to throw custom error message
 */
public class AAPException extends RuntimeException {

    String code = "INTERNAL_ERROR";
    int statusCode;
    public String getCode() {
        return code;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public AAPException(String message){ super(message); }
    public AAPException(String message,int statusCode){
        super(message);
        this.statusCode = statusCode;
    }
    public AAPException(String message, Exception e){
        super(message,e);
    }
}
