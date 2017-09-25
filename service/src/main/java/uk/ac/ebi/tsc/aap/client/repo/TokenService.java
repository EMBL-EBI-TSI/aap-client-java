package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ukumbham on 21/09/2017.
 */
@Component
public class TokenService {

    private TokenRepository tokenServiceRepo;

    @Autowired
    public TokenService(TokenRepository tokenServiceRepo){
        this.tokenServiceRepo = tokenServiceRepo;
    }

    public String getAAPToken(String username,String password){
       return tokenServiceRepo.getAAPToken(username,password);
    }

}
