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
import uk.ac.ebi.tsc.aap.client.model.User;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

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
@SpringBootTest(classes = {DomainService.class,TokenService.class})
@TestPropertySource(properties = {"aap.domains.url=https://dev.api.aap.tsi.ebi.ac.uk"})
public class ApplicationIntegrationTest {
    //logger initialised
    private static final Logger LOGGER = LoggerFactory.getLogger
            (ApplicationIntegrationTest.class);
    private static TestRestTemplate testRestTemplate = new TestRestTemplate();
    @Autowired
    private DomainService domainService;
    @Autowired
    private TokenService tokenService;
    private static String token;

    @BeforeClass
    public static void setUp() {
        String username = System.getenv("AAP_TEST_USERNAME");
        String password = System.getenv("AAP_TEST_PASSWORD");
        assertThat("Missing environment variable AAP_TEST_USERNAME", username, notNullValue());
        assertThat("Missing environment variable AAP_TEST_PASSWORD", password, notNullValue());
        token = getToken(username, password);
    }

    @Test
    public void can_get_user_domains_by_domain_reference() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_get_user_domains_by_domain_reference");
        String userReference = "usr-d8749acf-6a22-4438-accc-cc8d1877ba36";
        User user = user(userReference);
        Collection<Domain> userDomains = domainService.getDomains(user, token);
        assertNotNull(userDomains);
    }

    @Test
    public void can_create_a_domain_and_delete_the_domain() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_create_a_domain_and_delete_the_domain");
        String uniqueName = "Iam not client "+ new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        Domain created = domainService.createDomain(uniqueName, "aap client java integration test", token);
        assertNotNull(created);
        Domain deleted = domainService.deleteDomain(created, token);
        assertNotNull(deleted);
    }

    @Test
    public void can_get_domain_by_reference() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_get_domain_by_reference");
        String domainReference = "dom-0a22160b-563c-45a1-b497-9bff5b69a204";
        Domain domain = domainService.getDomainByReference(domainReference, token);
        assertNotNull(domain);
        Collection<User> users =  domainService.getAllUsersFromDomain(domain.getDomainReference(),token);
        assertNotNull(users);
    }

    @Test
    public void can_add_user_to_a_domain() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_add_user_to_a_domain-"+token);
        Domain domain = new Domain(null, null, "dom-0a22160b-563c-45a1-b497-9bff5b69a204");
        User user = user("usr-cebc0e02-24e1-40a6-b0a5-75c10ddffb86");
        Domain result = domainService.addUserToDomain(domain, user, token);
        assertNotNull(result);
        Domain deletedResult = domainService.removeUserFromDomain(user,domain,token);
        assertNotNull(deletedResult);
    }

    @Test
    public void can_add_manager_to_a_domain() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_add_manager_to_a_domain-"+token);
        Domain domain = new Domain(null, null, "dom-0a22160b-563c-45a1-b497-9bff5b69a204");
        User user = user("usr-cebc0e02-24e1-40a6-b0a5-75c10ddffb86");
        Domain result = domainService.addManagerToDomain(domain, user, token);
        assertNotNull(result);
        Domain deletedResult = domainService.removeManagerFromDomain(user,domain,token);
        assertNotNull(deletedResult);
    }

    @Test
    public void can_get_all_users_from_domain() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_get_all_users_from_a_domain");
        String domainReference = "dom-0a22160b-563c-45a1-b497-9bff5b69a204";
        Collection<User> users = domainService.getAllUsersFromDomain(domainReference, token);
        assertNotNull(users);
    }

    @Test
    public void can_get_all_managers_from_domain() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_get_all_managers_from_a_domain");
        String domainReference = "dom-0a22160b-563c-45a1-b497-9bff5b69a204";
        Collection<User> users = domainService.getAllManagersFromDomain(domainReference, token);
        assertNotNull(users);
    }

    @Test
    public void can_get_logged_in_user_membership_domains() throws Exception {
        LOGGER.trace("[ApplicationIntegrationTest] - can_get_logged_in_user_membership_domains");
        Collection<Domain> myDomains = domainService.getMyDomains(token);
        assertNotNull(myDomains);
        //assertTrue(myDomains.stream().anyMatch(domain -> "aap.domain.view".equals(domain.getDomainName())));
    }

    @Test
    public void can_get_logged_in_user_management_domains() throws Exception {
        Collection<Domain> myManagementDomains = domainService.getMyManagementDomains(token);
        assertNotNull(myManagementDomains);
        //assertTrue(myManagementDomains.stream().anyMatch(domain -> "self.karo-domain".equals(domain.getDomainName())));
    }

    @Test
    public void can_get_a_aap_token_with_username_and_password(){
        LOGGER.trace("[ApplicationIntegrationTest] - can_get_a_aap_token_with_username_and_password");
        String response = tokenService.getAAPToken(System.getenv("AAP_TEST_USERNAME"),
                                                   System.getenv("AAP_TEST_PASSWORD"));
        assertNotNull(response);
    }

    private static String getToken(String username, String password) {
        LOGGER.trace("Getting token");
        ResponseEntity<String> response = testRestTemplate.withBasicAuth(username, password)
                .getForEntity("https://dev.api.aap.tsi.ebi.ac.uk"+"/auth", String.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        return response.getBody();
    }

    private User user(String userReference) {
        return new User(null, null, userReference, null, null);
    }
}
