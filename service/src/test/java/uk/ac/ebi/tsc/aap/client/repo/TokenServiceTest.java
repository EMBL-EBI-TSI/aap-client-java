package uk.ac.ebi.tsc.aap.client.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TokenService.class)
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @MockBean
    private TokenRepository tokenRepository;

    @Test
    public void can_get_user_ref_from_token(){
        //this is the example token from https://jwt.io/
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.XbPfbIHMI6arZ3Y922BhjWgQzWXcXNrz0ogtVhfEd2o";
        String expectedUserReference = "1234567890";

        String actualUserReference = tokenService.getUserReference(token);

        assertThat(actualUserReference, equalTo(expectedUserReference));
    }

    @Test
    public void get_null_when_user_ref_absent(){
        //this is the example token from https://jwt.io/, but with the subject deleted
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiSm9obiBEb2UiLCJpYXQiOjE1MTYyMzkwMjJ9.8nYFUX869Y1mnDDDU4yL11aANgVRuifoxrE8BHZY1iE";

        String actualUserReference = tokenService.getUserReference(token);

        assertThat(actualUserReference, nullValue());
    }

}


