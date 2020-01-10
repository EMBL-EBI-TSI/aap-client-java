package uk.ac.ebi.tsc.aap.client.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import uk.ac.ebi.tsc.aap.client.model.LocalAccount;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes=UserRepositoryRest.class)
@RestClientTest(UserRepositoryRest.class)
@ContextConfiguration(classes=UserRepositoryRest.class)
@TestPropertySource(properties = {"aap.domains.url=/somewhere", "aap.timeout=5000"})
public class UserRepositoryRestTest {

	@Autowired
	private MockRestServiceServer usersApi;

	@Autowired
	private UserRepositoryRest subject;

	@Test public void
	can_create_local_account() {
		LocalAccount account = new LocalAccount("test-user","secret","test@ebi.com","Foo Bar","org");
		String mockResponse = "usr-ref-12345";
		this.usersApi.expect(requestTo("/auth")).andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.OK).body(mockResponse).contentType(MediaType.TEXT_PLAIN));

		String user_ref = subject.createLocalAccount(account);
		this.usersApi.verify();
		assertThat(user_ref, equalTo("usr-ref-12345"));
	}
}
