package uk.ac.ebi.tsc.aap.client.repo;

/**
 * Created by ukumbham on 22/09/2017.
 */
public interface TokenRepository {
    String getAAPToken (String username, String password);
}
