package uk.ac.ebi.tsc.aap.client.repo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        String userReference = null;

        DecodedJWT decodedJwt = JWT.decode(token);
        userReference = decodedJwt.getSubject();

        return userReference;
    }

}
