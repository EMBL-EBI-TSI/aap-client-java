package uk.ac.ebi.tsc.aap.client.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Data model for an AAP domain
 */
public class Domain implements Serializable, GrantedAuthority {

    private static final Logger LOGGER = LoggerFactory.getLogger(Domain.class);
    private static final long serialVersionUID = 1L;

    private String domainName;
    private String domainDesc;
    private String domainReference;

    private Set<User> users = new HashSet<>();

    public Domain() {
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

    @Override
    public String toString() {
        return "Domain{" +
                ", domainName='" + domainName + '\'' +
                ", domainDesc='" + domainDesc + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Domain)){
            return false;
        }
        Domain domain = (Domain) o;
        if (!domainName.equals(domain.domainName)) {
            return false;
        }
        return domainDesc.equals(domain.domainDesc);
    }

    @Override
    public int hashCode() {
        int result = domainName != null ? domainName.hashCode() : 0;
        result = (31 * result) + (domainDesc != null ?domainDesc.hashCode():0);
        result = (31 * result) + (domainReference != null ?domainReference.hashCode():0);
        return result;
    }

    @Override
    public String getAuthority() {
        return "ROLE_"+domainName;
    }
}
