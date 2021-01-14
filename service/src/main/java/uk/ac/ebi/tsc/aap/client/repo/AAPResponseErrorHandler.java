package uk.ac.ebi.tsc.aap.client.repo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uk.ac.ebi.tsc.aap.client.exception.AAPException;
import uk.ac.ebi.tsc.aap.client.exception.AAPLockedException;
import uk.ac.ebi.tsc.aap.client.exception.AAPUsernameNotFoundException;
import uk.ac.ebi.tsc.aap.client.exception.InvalidJWTTokenException;
import uk.ac.ebi.tsc.aap.client.exception.TokenExpiredException;
import uk.ac.ebi.tsc.aap.client.exception.TokenNotSuppliedException;
import uk.ac.ebi.tsc.aap.client.model.ErrorResponse;
import uk.ac.ebi.tsc.aap.client.util.RestUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class AAPResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AAPResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        LOGGER.error("Error code : {}", response.getStatusCode());
        String result = new BufferedReader(new InputStreamReader(response.getBody()))
                .lines().collect(Collectors.joining("\n"));
        LOGGER.error("Error response : {}", result);
        ErrorResponse errorResponse = null;
        if (result != null && !result.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            errorResponse = mapper.readValue(result, ErrorResponse.class);
            throwException(errorResponse, response);
        } else {
            throw new AAPException(null, response.getStatusCode());
        }


    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }

    private void throwException(ErrorResponse errorResponse, ClientHttpResponse response) throws IOException {
        String error = errorResponse.getError();
        String message = errorResponse.getMessage();
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            switch (error) {
                case "INVALID_JWT":
                    throw new InvalidJWTTokenException(message);
                case "TOKEN_EXPIRED":
                    throw new TokenExpiredException(message);
                case "NO_TOKEN":
                    throw new TokenNotSuppliedException(message);
                case AAPLockedException.CODE_ACCOUNT_LOCKED:
                    throw new AAPLockedException(message);
                case AAPUsernameNotFoundException.CODE_USERNAME_NOT_FOUND:
                    throw new AAPUsernameNotFoundException(message);
                default:
                    throw new InvalidJWTTokenException(message);
            }
        }
        throw new AAPException(errorResponse.getMessage(), response.getStatusCode());
    }
}
