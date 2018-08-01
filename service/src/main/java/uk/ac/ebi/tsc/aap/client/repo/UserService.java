package uk.ac.ebi.tsc.aap.client.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.tsc.aap.client.model.LocalAccount;
import uk.ac.ebi.tsc.aap.client.model.User;

@Component
public class UserService {

    private UserRepository repo;

    @Autowired
    public UserService(UserRepository userRepository){
        repo = userRepository;
    }

    public String createLocalAccount(User user,String password)
    {
        LocalAccount localAccount =  new LocalAccount(user.getUserName(),user.getPassword(),user.getEmail(),user.getFullName(),user.getOrganization());
        localAccount.setPassword(password);
        localAccount.setConfirmPwd(password);
        return repo.createLocalAccount(localAccount);
    }
}
