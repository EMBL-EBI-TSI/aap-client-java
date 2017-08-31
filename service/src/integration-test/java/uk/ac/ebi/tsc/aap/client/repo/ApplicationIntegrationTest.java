package uk.ac.ebi.tsc.aap.client.repo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.tsc.aap.client.model.Domain;

import java.util.Collection;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
    private static TestRestTemplate testRestTemplate = new TestRestTemplate();
    @Autowired
    private DomainService domainService;
    private static String token;

    @BeforeClass
    public static void setUp() {
        token = getToken(System.getenv("AAP_TEST_USERNAME"),
                         System.getenv("AAP_TEST_PASSWORD"));
    }

    @Test
    public void can_get_logged_in_user_membership_domains() throws Exception{
        LOGGER.trace("[ApplicationIntegrationTest] - can_get_logged_in_user_membership_domains");
        Collection<Domain> myDomains = domainService.getMyDomains(token);
        assertNotNull(myDomains);
        assertTrue(myDomains.stream().anyMatch(domain -> "aap.domain.view".equals(domain.getDomainName())));
    }

    @Test
    public void can_get_logged_in_user_management_domains() throws Exception{
        Collection<Domain> myManagementDomains = domainService.getMyManagementDomains(token);
        assertNotNull(myManagementDomains);
        assertTrue(myManagementDomains.stream().anyMatch(domain -> "self.karo-domain".equals(domain.getDomainName())));
    }

    private static String getToken(String username, String password) {
        LOGGER.trace("Getting token");
        ResponseEntity<String> response = testRestTemplate.withBasicAuth(username, password)
                .getForEntity("https://dev.api.aap.tsi.ebi.ac.uk"+"/auth", String.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        return response.getBody();
    }
}
