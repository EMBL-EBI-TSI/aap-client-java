package uk.ac.ebi.tsc.aap.client.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserBuilder {

    private User user;

    public UserBuilder(String reference) {
        user = new User();
        user.setUserReference(reference);
    }

    public UserBuilder withUsername(String username) {
        user.setUserName(username);
        return this;
    }

    public UserBuilder withEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder withFullName(String name) {
        user.setFullName(name);
        return this;
    }

    public UserBuilder withDomains(Set<Domain> domains) {
        user.setDomains(domains);
        return this;
    }

    public UserBuilder withDomains(String... domains) {
        Set<Domain> domainsSet = new HashSet<>();
        Arrays.asList(domains).forEach(name->domainsSet.add(new Domain(name,null,null)));
        user.setDomains(domainsSet);
        return this;
    }

    public User build() {
        return user;
    }
}
