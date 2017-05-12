package uk.ac.ebi.tsc.aap.client.repo;


import org.springframework.http.ResponseEntity;

/**
 * Created by neilg on 11/05/2017.
 */
public interface LocalTokenService {
    public String getAAPToken (String username, String password);
}
