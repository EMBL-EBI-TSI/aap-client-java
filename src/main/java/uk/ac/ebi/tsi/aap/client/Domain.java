package uk.ac.ebi.tsi.aap.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Data model for an AAP domain
 */
public class Domain implements Serializable {

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

        //if (!domainId.equals(domain.domainId)) return false;
        if (!domainName.equals(domain.domainName)) {
            return false;
        }
        return domainDesc.equals(domain.domainDesc);
    }

    @Override
    public int hashCode() {
        int result = domainName.hashCode();
        result = 31 * result + domainDesc.hashCode();
        result = 31 * result + domainReference.hashCode();
        return result;
    }
}
