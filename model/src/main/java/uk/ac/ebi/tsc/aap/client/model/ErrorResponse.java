package uk.ac.ebi.tsc.aap.client.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 131231231;

    String timestamp;
    String status;
    String error;
    String message;
    String path;
    String exception;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorResponse() {
        this.message = "";
        this.error = "UNKNOWN";
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
    }
}
