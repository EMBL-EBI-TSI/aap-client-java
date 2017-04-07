package uk.ac.ebi.tsc.aap.client.test;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.ResponseCreator;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Taken from https://objectpartners.com/2013/01/09/rest-client-testing-with-mockrestserviceserver/
 */
public class TimeoutResponseCreator implements ResponseCreator {
    @Override
    public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
        throw new SocketTimeoutException("Mocking timeout exception");
    }
    public static TimeoutResponseCreator withTimeout() {
        return new TimeoutResponseCreator();
    }
}