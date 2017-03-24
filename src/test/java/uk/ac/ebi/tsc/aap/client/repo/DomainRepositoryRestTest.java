package uk.ac.ebi.tsc.aap.client.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=DomainRepositoryRest.class)
@RestClientTest(DomainRepositoryRest.class)
@TestPropertySource(properties = {"aap.domains.url=irrelevant"})
public class DomainRepositoryRestTest {

    @Autowired
    private MockRestServiceServer domainsApi;

    @Autowired
    private DomainRepositoryRest subject;

    @Test public void
    retrieves_the_list_of_domain_from_the_api() {
        String username = "3b69024773063d161ca43ac855cf3798b3991366";
        String expectedUrl = String.format("/users/%s/domains", username);
        String mockResponse = "[" +
                "{" +
                "  \"domainName\" : \"foo\"," +
                "  \"domainDesc\" : \"The Foo\"" +
                "}, " +
                "{" +
                "  \"domainName\" : \"bar\"," +
                "  \"domainDesc\" : \"The Bar\"" +
                "}" +
                "]";
        this.domainsApi.expect(requestTo(expectedUrl))
            .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));
        User user = user(username);

        Collection<String> domains = subject.getDomains(user, "irrelevant");

        assertThat(domains.size(), equalTo(2));
        assertThat(domains, hasItem("foo"));
        assertThat(domains, hasItem("bar"));
    }

    private User user(String username) {
        return new User(username, null, null, null);
    }
}
