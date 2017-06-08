package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;

/**
 * @author Amelie Cornelis <ameliec@ebi.ac.uk>
 * @since v0.0.1
 */
@Component
public class DomainService {

    private DomainRepository repo;

    @Autowired
    public DomainService(DomainRepository repository) {
        this.repo = repository;
    }

    public Collection<Domain> getDomains(User user, String token) {
        return repo.getDomains(user, token);
    }

    public Domain createDomain(String name, String description, String token) {
        Domain toAdd = new Domain(name, description, null);
        return repo.createDomain(toAdd, token);
    }

    public Domain deleteDomain(Domain toDelete, String token) {
        return repo.deleteDomain(toDelete, token);
    }
    
    public Domain getDomainByReference(String reference, String token){
    	return repo.getDomainByReference(reference, token);
    	
    }
    
    public Domain addUserToDomain(Domain toJoin, User toAdd, String token){
    	return repo.addUserToDomain(toJoin, toAdd, token);
    }
    
    public Domain removeUserFromDomain(User toBeRemoved, Domain toBeUpdated, String token){
    	return repo.removeUserFromDomain(toBeRemoved, toBeUpdated, token);
    }
}
