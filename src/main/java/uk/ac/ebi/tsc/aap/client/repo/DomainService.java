package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    public Collection<String> getDomains(User user, String token) {
        return repo.getDomains(user, token);
    }
}
