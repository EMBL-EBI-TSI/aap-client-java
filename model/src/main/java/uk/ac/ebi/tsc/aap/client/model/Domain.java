package uk.ac.ebi.tsc.aap.client.model;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Data model for an AAP domain
 */
public class Domain implements Serializable, GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private String domainName;
    private String domainDesc;
    private String domainReference;

    private Set<User> users = new HashSet<>();
    private Set<User> managers = new HashSet<>();

    /**
     * @see Domain#builder()
     */
    @Deprecated
    public Domain() {
    }

    public Domain(String domainReference) {
        this(null, null, domainReference);
    }

    public Domain(String domainName, String domainDesc, String domainReference) {
        this.domainName = domainName;
        this.domainDesc = domainDesc;
        this.domainReference = domainReference;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainDesc() {
        return domainDesc;
    }

    public void setDomainDesc(String domainDesc) {
        this.domainDesc = domainDesc;
    }

    public String getDomainReference() {
        return domainReference;
    }

    public void setDomainReference(String domainReference) {
        this.domainReference = domainReference;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Set<User> getManagers() {return managers;}

    @Override
    public String toString() {
        return "Domain{" +
                ", domainName='" + domainName + '\'' +
                ", domainDesc='" + domainDesc + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain domain = (Domain) o;

        if (domainName != null ? !domainName.equals(domain.domainName) : domain.domainName != null) return false;
        if (domainDesc != null ? !domainDesc.equals(domain.domainDesc) : domain.domainDesc != null) return false;
        return domainReference != null ? domainReference.equals(domain.domainReference) : domain.domainReference == null;
    }

    @Override
    public int hashCode() {
        int result = domainName != null ? domainName.hashCode() : 0;
        result = 31 * result + (domainDesc != null ? domainDesc.hashCode() : 0);
        result = 31 * result + (domainReference != null ? domainReference.hashCode() : 0);
        return result;
    }

    @Override
    public String getAuthority() {
        return "ROLE_"+domainName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Domain domain;

        Builder() {
            domain = new Domain();
        }

        public Builder withReference(String reference) {
            domain.domainReference = reference;
            return this;
        }

        public Builder withName(String name) {
            domain.domainName = name;
            return this;
        }

        public Builder withDescription(String description) {
            domain.domainDesc = description;
            return this;
        }

        public Builder withUser(User member) {
            domain.users.add(member);
            return this;
        }

        public Builder withUser(String userReference) {
            domain.users.add(User.builder().withReference(userReference).build());
            return this;
        }

        public Builder withUsers(Set<User> users) {
            domain.users.addAll(users);
            return this;
        }

        public Builder withManager(User manager) {
            domain.managers.add(manager);
            return this;
        }

        public Builder withManager(String managerReference) {
            domain.managers.add(User.builder().withReference(managerReference).build());
            return this;
        }

        public Builder withManagers(Set<User> managers) {
            domain.managers.addAll(managers);
            return this;
        }

        public Domain build() {
            return domain;
        }
    }
}
