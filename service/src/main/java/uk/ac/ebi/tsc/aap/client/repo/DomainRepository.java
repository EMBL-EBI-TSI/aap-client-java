package uk.ac.ebi.tsc.aap.client.repo;

import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;

/**
 * @author Amelie Cornelis <ameliec@ebi.ac.uk>
 * @since v0.0.1
 */
public interface DomainRepository {

    Collection<Domain> getDomains(User user, String token);

    Domain createDomain(Domain toAdd, String token);

    Domain deleteDomain(Domain toDelete, String token);

    Domain addUserToDomain(Domain toJoin, User toAdd, String token);
}
