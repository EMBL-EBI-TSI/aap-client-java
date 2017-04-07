package uk.ac.ebi.tsc.aap.client.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static uk.ac.ebi.tsc.aap.client.test.TimeoutResponseCreator.withTimeout;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=DomainRepositoryRest.class)
@RestClientTest(DomainRepositoryRest.class)
@TestPropertySource(properties = {"aap.domains.url=somewhere", "aap.timeout=5000"})
public class DomainRepositoryRestTest {

    @Autowired
    private MockRestServiceServer domainsApi;

    @Autowired
    private DomainRepositoryRest subject;

    @Test public void
    retrieves_the_list_of_domain_from_the_api() {
        String userReference = "foo-bar-buzz";
        String expectedUrl = String.format("/users/%s/domains", userReference);
        String mockResponse = "{" +
                "  \"userReference\" : \"foo-bar-buz\"," +
                "  \"userName\" : \"someone\"," +
                "  \"email\" : \"someone@somewhere.com\"," +
                "  \"mobile\" : \"1234\"," +
                "  \"domains\" : [" +
                "{" +
                "  \"domainName\" : \"foo\"," +
                "  \"domainDesc\" : \"The Foo\"" +
                "}, " +
                "{" +
                "  \"domainName\" : \"bar\"," +
                "  \"domainDesc\" : \"The Bar\"" +
                "}" +
                "]}";
        this.domainsApi.expect(requestTo(expectedUrl))
            .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));
        User user = user(userReference);

        Collection<String> domains = subject.getDomains(user, "a-token");

        this.domainsApi.verify();
        assertThat(domains.size(), equalTo(2));
        assertThat(domains, hasItem("foo"));
        assertThat(domains, hasItem("bar"));
    }

    @Test public void
    can_create_a_domain() {
        String mockResponse =
            "{" +
                "  \"domainReference\" : \"usr-abcdef\"" +
            "}, " ;
        this.domainsApi.expect(requestTo("/domains/")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED).body(mockResponse).contentType(MediaType.APPLICATION_JSON));

        Domain created = subject.createDomain("foo", "The Foo", "a-token");

        this.domainsApi.verify();
        assertThat(created.getDomainReference(), equalTo("usr-abcdef"));
    }

    @Test(expected = RuntimeException.class) public void
    returns_suitable_message_on_creation_error() {
        this.domainsApi.expect(requestTo("/domains/")).andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        subject.createDomain("bar", "The Bar", "a-token");

        this.domainsApi.verify();
    }

    @Test(expected = RuntimeException.class) public void
    detects_backend_down() {
        this.domainsApi.expect(requestTo("/domains/")).andExpect(method(HttpMethod.POST))
                .andRespond(withTimeout());

        subject.createDomain("bar", "The Bar", "a-token");

        this.domainsApi.verify();
    }

    private User user(String userReference) {
        return new User(null, null, userReference, null);
    }
}
