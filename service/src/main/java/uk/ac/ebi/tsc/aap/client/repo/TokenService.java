package uk.ac.ebi.tsc.aap.client.repo;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.jose4j.jwt.JwtClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Created by ukumbham on 21/09/2017.
 */
@Component
public class TokenService {

    private TokenRepository tokenServiceRepo;

    @Autowired
    public TokenService(TokenRepository tokenServiceRepo) {
        this.tokenServiceRepo = tokenServiceRepo;
    }

    public String getAAPToken(String username, String password) {
        return tokenServiceRepo.getAAPToken(username, password);
    }

    public String getUserReference(String token) {
        try {
            String base64Payload = token.split("\\.")[1];
            String payload = StringUtils.newStringUtf8(Base64.getDecoder().decode(base64Payload));
            return JwtClaims.parse(payload).getSubject();
        } catch (Throwable t) {
            throw new RuntimeException("Error while getting user reference", t);
        }
    }

}
