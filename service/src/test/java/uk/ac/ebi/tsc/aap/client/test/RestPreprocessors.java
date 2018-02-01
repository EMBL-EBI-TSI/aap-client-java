package uk.ac.ebi.tsc.aap.client.test;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;

public class RestPreprocessors {

    public static AuthorizationMaskingPreprocessor maskAuthorization() {
        return new AuthorizationMaskingPreprocessor();
    }

    /**
     * Replaces the Authorization Bearer/Basic header with a fixed value. We found we
     * needed this because the generic restdocs Preprocessors.replacePattern works on
     * the request body only, not the headers.
     *
     * The Bearer token gets replaced with a placeholder <code>eyJhb...h7HgQ</code>.
     * The Basic value gets replaced with <code>me:secret</code> base64-encoded.
     */
    public static class AuthorizationMaskingPreprocessor implements OperationPreprocessor {

        @Override
        public OperationRequest preprocess(OperationRequest request) {
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(request.getHeaders());
            if(request.getHeaders().containsKey("Authorization")) {
                String auth = request.getHeaders().getFirst("Authorization");
                if(auth.startsWith("Bearer ")) {
                    headers.set("Authorization", "Bearer eyJhb...h7HgQ");
                } else if (auth.startsWith("Basic ")) {
                    headers.set("Authorization", "Basic bWU6c2VjcmV0");
                }
            }
            return new OperationRequestFactory().create(request.getUri(),
                    request.getMethod(), request.getContent(), headers,
                    request.getParameters(), request.getParts());
        }

        @Override
        public OperationResponse preprocess(OperationResponse response) {
            return response;
        }
    }
}
