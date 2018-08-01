package uk.ac.ebi.tsc.aap.client.model;

import java.io.Serializable;

public class LocalAccount implements Serializable {

    private static final long serialVersionUID = 13235666323l;

    private String username;
    private String password;
    private String confirmPwd;
    private String email;
    private String name;
    private String organization;

    public LocalAccount(String username, String password, String email, String name, String organization){
        this.username = username;
        this.password = password;
        this.confirmPwd = password;
        this.email = email;
        this.name = name;
        this.organization = organization;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPwd() {
        return confirmPwd;
    }

    public void setConfirmPwd(String confirmPwd) {
        this.confirmPwd = confirmPwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
