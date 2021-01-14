package uk.ac.ebi.tsc.aap.client.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import uk.ac.ebi.tsc.aap.client.exception.AAPException;
import uk.ac.ebi.tsc.aap.client.exception.AAPLockedException;
import uk.ac.ebi.tsc.aap.client.exception.AAPUsernameNotFoundException;
import uk.ac.ebi.tsc.aap.client.exception.InvalidJWTTokenException;
import uk.ac.ebi.tsc.aap.client.exception.TokenExpiredException;
import uk.ac.ebi.tsc.aap.client.exception.TokenNotSuppliedException;
import uk.ac.ebi.tsc.aap.client.model.ErrorResponse;
import uk.ac.ebi.tsc.aap.client.util.RestUtil;

@Component
public class AAPResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AAPResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        LOGGER.error("Error code : {}", response.getStatusCode());
        String result = new BufferedReader(new InputStreamReader(response.getBody()))
                .lines().collect(Collectors.joining("\n"));
        LOGGER.error("Error response : {}",result);
        ObjectMapper mapper = new ObjectMapper();
        ErrorResponse errorResponse = mapper.readValue(result,ErrorResponse.class);
        throwException(errorResponse);

    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }

    private void throwException(ErrorResponse errorResponse){
        String error = errorResponse.getError();
        String message = errorResponse.getMessage();
        if(errorResponse.getStatus()==HttpStatus.UNAUTHORIZED.value()){
            switch (error){
                case "INVALID_JWT" :
                    throw new InvalidJWTTokenException(message);
                case "TOKEN_EXPIRED" :
                    throw new TokenExpiredException(message);
                case "NO_TOKEN" :
                    throw new TokenNotSuppliedException(message);
                case AAPLockedException.CODE_ACCOUNT_LOCKED:
                    throw new AAPLockedException(message);
                case AAPUsernameNotFoundException.CODE_USERNAME_NOT_FOUND:
                    throw new AAPUsernameNotFoundException(message);
                default:
                    throw new InvalidJWTTokenException(message);
            }
        }
        throw new AAPException(errorResponse.getMessage(),errorResponse.getStatus());
    }
}
