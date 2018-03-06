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

    Domain addManagerToDomain(Domain toJoin, User toAdd, String token);
    
    Domain getDomainByReference(String reference, String token);
    
    Domain removeUserFromDomain(User toBeRemoved, Domain toBeUpdated, String token);

    Domain removeManagerFromDomain(User toBeRemoved, Domain toBeUpdated, String token);
    
    Collection<User> getAllUsersFromDomain(String domainReference, String token);

    Collection<User> getAllManagersFromDomain(String domainReference, String token);


    /***
     * Get loggedin user membership domains
     * @param token - user token
     * @return list of membership domains
     */
    Collection<Domain> getMyDomains(String token);

    /**
     * Gets the logged in user management domains
     * @param token - user token
     * @return List<Domain></Domain> - management domains
     */
    Collection<Domain> getMyManagementDomains(String token);

}
