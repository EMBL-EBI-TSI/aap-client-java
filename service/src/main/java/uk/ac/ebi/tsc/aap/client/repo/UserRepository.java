package uk.ac.ebi.tsc.aap.client.repo;

import uk.ac.ebi.tsc.aap.client.model.LocalAccount;

/**
 * Created by Felix on 24/07/2018.
 */
public interface UserRepository {
    public String createLocalAccount(LocalAccount localAccount);
}
