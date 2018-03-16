package uk.ac.ebi.tsc.aap.client.util;

import org.springframework.http.HttpHeaders;

public class TokenHeaderBuilder {

    public static HttpHeaders createHeaders(String token) {
        return new HttpHeaders() {{
            String authHeader = "Bearer " + token;
            set("Authorization", authHeader);
        }};
    }
}
