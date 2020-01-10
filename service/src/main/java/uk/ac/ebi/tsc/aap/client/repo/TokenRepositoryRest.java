package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import uk.ac.ebi.tsc.aap.client.exception.UserNameOrPasswordWrongException;

import java.time.Duration;

/**
 * Created by ukumbham on 21/09/2017.
 */
@Component
public class TokenRepositoryRest implements TokenRepository {

    private final RestTemplate template;

    public TokenRepositoryRest(
            @Value("${aap.domains.url}") String domainsApiUrl,
            @Value("${aap.timeout:180000}") int timeout,
            RestTemplateBuilder clientBuilder) {
            this.template = clientBuilder
                .rootUri(domainsApiUrl)
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                .build();
    }

    /**
     * Gets local token using username and password.
     * @param username
     * @param password
     * @return token
     * @throws UserNameOrPasswordWrongException
     */
    @Override
    public String getAAPToken(String username, String password) throws UserNameOrPasswordWrongException{
        ResponseEntity<String> response = null;
        try{
            HttpEntity<String> entity = new HttpEntity<>(createHttpHeaders(username,password));
            response = this.template.exchange("/auth",
                                                HttpMethod.GET, entity, String.class);
        }
        catch (HttpClientErrorException e){
         throw new UserNameOrPasswordWrongException(String.format("username/password wrong. Please check username or password to get token"),e);
        }
        catch (Exception e){
            throw new RuntimeException("Error while getting AAP token",e);
        }
        return response.getBody();
    }

    private static HttpHeaders createHttpHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic "
                + Base64Coder.encodeString(username + ":" + password));
        return headers;
    }
}
