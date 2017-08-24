package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * Created by ameliec on 19-Apr-17.
 */
public class HttpHelper {

    String token;

    public HttpHelper(String token) {
        this.token = token;
    }

    protected HttpHeaders createHeaders() {
        return createHeaders(token);
    }

    protected HttpHeaders createHeaders(String aToken) {
        return new HttpHeaders() {{
            String authHeader = "Bearer " + aToken;
            set("Authorization", authHeader);
            set("Content-Type", "application/json");
            set("Accept", "application/hal+json");
        }};
    }

    protected HttpEntity<Object> entity() {
        return entity(null);
    }

    protected HttpEntity<Object> entity(Object object) {
        return new HttpEntity<>(object, createHeaders());
    }

}
