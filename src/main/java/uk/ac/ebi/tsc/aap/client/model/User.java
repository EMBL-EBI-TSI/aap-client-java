package uk.ac.ebi.tsc.aap.client.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;

/**
 * Data model for an AAP user
 */
public class User implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);
    private static final long serialVersionUID = 1L;

    private String userName;
    private String email;
    private String userReference;

    public User(){}

    public User(String userName, String email, String userReference) {
        this.userName = userName;
        this.email = email;
        this.userReference = userReference;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserReference() {
        return userReference;
    }

    public void setUserReference(String userReference) {
        this.userReference = userReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userName != null ? !userName.equals(user.userName) : user.userName != null)
            return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return userReference != null ? userReference.equals(user.userReference) : user.userReference == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (userReference != null ? userReference.hashCode() : 0);
        return result;
    }
}
