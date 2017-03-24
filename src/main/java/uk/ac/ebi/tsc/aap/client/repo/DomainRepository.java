package uk.ac.ebi.tsc.aap.client.repo;

import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;

/**
 * @author Amelie Cornelis <ameliec@ebi.ac.uk>
 * @since v0.0.1
 */
public interface DomainRepository {

    Collection<String> getDomains(User user, String token);
}
