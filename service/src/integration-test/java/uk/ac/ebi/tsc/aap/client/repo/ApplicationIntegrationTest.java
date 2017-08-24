package uk.ac.ebi.tsc.aap.client.repo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.tsc.aap.client.model.Domain;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ukumbham on 21/08/2017.
 */
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DomainService.class)
@TestPropertySource(properties = {"aap.domains.url=https://dev.api.aap.tsi.ebi.ac.uk"})
public class ApplicationIntegrationTest {
    //logger initialised
    private static final Logger LOGGER = LoggerFactory.getLogger
            (ApplicationIntegrationTest.class);
    private String TEST_USERNAME = System.getenv("AAP_TEST_USERNAME");
    private String TEST_PASSWORD = System.getenv("AAP_TEST_PASSWORD");
    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private HttpHelper http;
    private @Value("${aap.domains.url}") String apiBaseUrl;

    @Before
    public void setUp() {
        assertThat("AAP_TEST_USERNAME not set", TEST_USERNAME, notNullValue());
        assertThat("AAP_TEST_PASSWORD not set", TEST_PASSWORD, notNullValue());
        String testToken = getToken(TEST_USERNAME, TEST_PASSWORD);
        http = new HttpHelper(testToken);
    }

    @Test
    public void can_get_logged_in_user_membership_domains() throws Exception{
        HttpEntity<String> entity = new HttpEntity<>("parameters", http.createHeaders());
        ResponseEntity<List<Domain>> response = testRestTemplate.exchange(
                apiBaseUrl +"/my/domains",
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Domain>>() {});
        assertNotNull(response.getBody());
    }

    @Test
    public void can_get_logged_in_user_management_domains() throws Exception{
        HttpEntity<String> entity = new HttpEntity<>("parameters", http.createHeaders());
        ResponseEntity<String> response = testRestTemplate.exchange(
                apiBaseUrl +"/my/management",
                HttpMethod.GET, entity, String.class);
        assertNotNull(response.getBody());
    }

    private String getToken(String username, String password) {
        ResponseEntity<String> response = testRestTemplate.withBasicAuth(username, password)
                .getForEntity(apiBaseUrl+"/auth", String.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        return response.getBody();
    }
}
