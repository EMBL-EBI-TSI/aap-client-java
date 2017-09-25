package uk.ac.ebi.tsc.aap.client.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import uk.ac.ebi.tsc.aap.client.exception.UserNameOrPasswordWrongException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Created by ukumbham on 22/09/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= TokenRepositoryRest.class)
@RestClientTest(TokenRepositoryRest.class)
@TestPropertySource(properties = {"aap.domains.url=somewhere", "aap.timeout=5000"})
public class TokenRepositoryRestTest {

    @Autowired
    private MockRestServiceServer domainsApi;

    @Autowired
    private TokenRepositoryRest subject;

    @Test
    public void can_get_token_from_api(){
        String expectedUrl = String.format("/auth");
        String mockResponse = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3RzaS5lYmkuYWMudWsiLCJleHAiOjE1MDYwODE1MzYsImp0aSI6ImtWeldNUDNMbzN5REJXYk9hV3NmbEEiLCJpYXQiOjE1MDYwNzc5MzYsInN1YiI6InVzci1kODc0OWFjZi02YTIyLTQ0Mzgt" +
                               "YWNjYy1jYzhkMTg3N2JhMzYiLCJlbWFpbCI6ImVtYmwuZWJpLnRzaUBnbWFpbC5jb20iLCJuaWNrbmFtZSI6Imthcm8iLCJuYW1lIjoiS2FybyBUZXN0aW5nIiwiZG9tYWlucyI6WyJVU0lfbmVpbGciLCJzZWxmLnVzaGFEb21haW4iLCJhYXAuZG9tYWluLnZpZXciLCJhYXAtdXNlcnMtZG9tYWluIiwiYWFwLnVzZXIudmlldyJdfQ.UktqjHJRh6_L3980ZrsovksFc8IoPrLaBMLAJzC4G8mJSMr3DcVNkYSa0ga0mp23D7FedMbSmqmiD_SGf90qoNPo4kfpBbKG9-v1OXXmG1Wsmk5SisjHBF4rMPwMi-2VFEAKuJuxaEqllhdtxg3Pwx_JYJqPZtvtn4jYfTKZxmi56_DVbvPUllf2MsZLjX-jBtd6ff0QELoYTNG1ql_oOA8hsWSm0Q9rAHahbeQ9iYAr8B1HjXrG0jJDmZfRrwdYwUEEAk8sH0823oWaIzpMnYsJnqfW1M92g4b59-SERExSQq6m_K5aZAVGOuLgisP96eOH8Ke-Ei03DBcmD4AImg";
        this.domainsApi.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.TEXT_PLAIN));
        String token = subject.getAAPToken("test-user","secret");
        this.domainsApi.verify();
        assertThat(token, notNullValue());
    }

    @Test(expected = HttpServerErrorException.class)
    public void should_return_error_while_retrieving_token_on_server() {
        this.domainsApi.expect(requestTo("/auth")).andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());
        subject.getAAPToken("test-user","secret");
    }

    @Test(expected = UserNameOrPasswordWrongException.class)
    public void should_return_error_if_username_is_wrong(){
        this.domainsApi.expect(requestTo("/auth")).andExpect(method(HttpMethod.GET))
                .andRespond(withUnauthorizedRequest());
        subject.getAAPToken("wrong-user","secret");
    }

    @Test(expected = UserNameOrPasswordWrongException.class)
    public void should_return_error_if_password_is_empty(){
        this.domainsApi.expect(requestTo("/auth")).andExpect(method(HttpMethod.GET))
                .andRespond(withUnauthorizedRequest());
        subject.getAAPToken("correct-user","");
    }

}
