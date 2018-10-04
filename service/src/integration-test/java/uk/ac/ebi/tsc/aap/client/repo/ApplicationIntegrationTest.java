package uk.ac.ebi.tsc.aap.client.repo;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.collection.IsMapContaining;
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
import uk.ac.ebi.tsc.aap.client.exception.AAPException;
import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.Profile;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import uk.ac.ebi.tsc.aap.client.exception.TokenNotSuppliedException;
import uk.ac.ebi.tsc.aap.client.exception.InvalidJWTTokenException;
import uk.ac.ebi.tsc.aap.client.exception.TokenExpiredException;

/**
 * Created by ukumbham on 21/08/2017.
 */
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DomainService.class,TokenService.class, ProfileService.class})
@TestPropertySource(locations = {"classpath:application-service.properties"},
        properties = {"aap.url=https://dev.api.aap.tsi.ebi.ac.uk"})
public class ApplicationIntegrationTest {
    //logger initialised
    private static final Logger LOGGER = LoggerFactory.getLogger
            (ApplicationIntegrationTest.class);
    private static TestRestTemplate testRestTemplate = new TestRestTemplate();
    @Autowired
    private DomainService domainService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;
    private static String token;
    private static String AJAY_USERNAME;
    private static String AJAY_PASSWORD;
    public static final String DOMAIN_WHERE_TEST_USER_IS_A_MANAGER = "dom-311d5438-e546-43ce-8f91-c452a154ce5f";

    @BeforeClass
    public static void setUp() {
        String username = System.getenv("AAP_TEST_USERNAME");
        String password = System.getenv("AAP_TEST_PASSWORD");
        AJAY_USERNAME = System.getenv("AAP_AJAY_USERNAME");
        AJAY_PASSWORD = System.getenv("AAP_AJAY_PASSWORD");
        assertThat("Missing environment variable AAP_TEST_USERNAME", username, notNullValue());
        assertThat("Missing environment variable AAP_TEST_PASSWORD", password, notNullValue());
        assertThat("Missing environment variable AAP_AJAY_USERNAME", AJAY_USERNAME, notNullValue());
        assertThat("Missing environment variable AAP_AJAY_PASSWORD", AJAY_PASSWORD, notNullValue());
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
    public void can_create_a_local_account() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_create_a_local_account");
        String uniqueUserName = "user-"+ new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        User user = new User(uniqueUserName, "testuser@ebi.com", null, "Foo Bar", null);
        user.setOrganization("org");
        String userReference = userService.createLocalAccount(user,"secret");
        assertNotNull(userReference);
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

    @Test(expected = TokenNotSuppliedException.class)
    public void throw_exception_on_no_token() {
        LOGGER.trace("[ApplicationIntegrationTest] - throw_excpetion_on_no_token");
        String uniqueName = "Iam not client "+ new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        domainService.createDomain(uniqueName, "aap client java integration test", "");
    }

    @Test(expected = InvalidJWTTokenException.class)
    public void throw_exception_on_invalid_token() {
        LOGGER.trace("[ApplicationIntegrationTest] - throw_excpetion_on_no_token");
        String uniqueName = "Iam not client "+ new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        domainService.createDomain(uniqueName, "aap client java integration test", "INVALID_TOKEN");
    }

    @Test(expected = TokenExpiredException.class)
    public void throw_exception_on_expired_token() {
        LOGGER.trace("[ApplicationIntegrationTest] - throw_excpetion_on_no_token");
        String expiredToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hYXAudHNpLmViaS5hYy51ay9zcCIsImV4cCI6MTUyOTU5Mjc0" +
                "OCwianRpIjoiQWtzUWlzWVRNc21mZzllbEhZaWFwdyIsImlhdCI6MTUyOTU5MjY4OCwic3ViIjoidXNyLWQ4NzQ5YWNmLTZhMjItNDQz" +
                "OC1hY2NjLWNjOGQxODc3YmEzNiIsImVtYWlsIjoiZW1ibC5lYmkudHNpQGdtYWlsLmNvbSIsIm5pY2tuYW1lIjoia2FybyIsIm5hbWUi" +
                "OiJLYXJvIFRlc3RpbmciLCJkb21haW5zIjpbImFhcC5tZS5hZG1pbiIsInNlbGYudXNoYURvbWFpbiIsIlVTSV9uZWlsZyIsImFhcC5k" +
                "b21haW4udmlldyIsImFhcC11c2Vycy1kb21haW4iLCJhYXAudXNlci52aWV3Il19.BYsQ2mhlIHpLr_gyxj25QHO56UjMDPd_ZsdkfIU" +
                "D3eSUEOllQHKl2717tvX7UdD1Hwb7OCAPHmEybQlYgKrWei6MN0Hw9wVYbNlDueXSe3tO-ihCHnpAy-PlvdPLGd36SgGzrkvxfjwx_y" +
                "ve4USEckljHesbmhzfkNda83jnfxN-FD88U6z9z5lr0yUTI5jL-wXaxjHbAffMfXRlTZ9Ow1hztlzeHuVG_JNRsPRTEweAdpNsQf" +
                "akSa1DqkcBH3qlOjTHrK7W_IfPmXuo_CaYFFMM1wvO_1DCF_7bLXHME0M7I-uaOiIlo-VgmHx2ILNu_mtOpuQtsMPf3DnspOx6PQ";

        String uniqueName = "Iam not client "+ new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        domainService.createDomain(uniqueName, "aap client java integration test", expiredToken);
    }

    @Test
    public void can_create_domain_profile() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_create_domain_profile");
        String uniqueName = "Profile creation test "+ new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        Domain created = domainService.createDomain(uniqueName, "aap client java integration test", token);
        assertNotNull(created);
        Profile profile = profileService.createDomainProfile(created.getDomainReference(), ImmutableMap.of("colour", "blue", "fruit", "pineapple"), token);
        assertThat(profile.getDomainReference(), is(created.getDomainReference()));
        assertThat(profile.getAttributeNames().size(), is(2));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("fruit", "pineapple"));
        Domain deleted = domainService.deleteDomain(created, token);
        assertNotNull(deleted);
    }

    @Test
    public void can_change_domain_profile() {
        String uniqueName = "Profile modification test "+ new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        Domain created = domainService.createDomain(uniqueName, "aap client java integration test", token);
        assertNotNull(created);
        Profile profile = profileService.createDomainProfile(created.getDomainReference(), ImmutableMap.of("colour", "blue", "fruit", "pineapple"), token);
        assertThat(profile.getDomainReference(), is(created.getDomainReference()));
        assertThat(profile.getAttributeNames().size(), is(2));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("fruit", "pineapple"));
        profile = profileService.updateProfile(profile.getReference(), ImmutableMap.of("car", "Volvo"), token);
        assertThat(profile.getDomainReference(), is(created.getDomainReference()));
        assertThat(profile.getAttributeNames().size(), is(1));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("car", "Volvo"));

        profile = profileService.patchProfile(profile.getReference(), ImmutableMap.of("pet", "Turtle"), token);

        assertThat(profile.getDomainReference(), is(created.getDomainReference()));
        assertThat(profile.getAttributeNames().size(), is(2));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("car", "Volvo"));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("pet", "Turtle"));

        profile = profileService.deleteProfileAttribute(profile.getReference(), "car", token);
        assertThat(profile.getDomainReference(), is(created.getDomainReference()));
        assertThat(profile.getAttributeNames().size(), is(1));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("pet", "Turtle"));
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
    public void manager_cannot_get_domain_profile() {
        AAPException exception = null;
        String profileReference = "prf-746d461f-31d9-4751-8d3a-2256d03846b7";
        try {
            profileService.getProfile(profileReference, token);
        } catch (AAPException e) {
            exception = e;
        }
        assertThat(exception.getStatusCode(), is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void manager_cannot_get_domain_profile_by_domain_ref() {
        AAPException exception = null;
        String domainReference = "dom-311d5438-e546-43ce-8f91-c452a154ce5f";
        try {
            profileService.getDomainProfile(domainReference, token);
        } catch (AAPException e) {
            exception = e;
        }
        assertThat(exception.getStatusCode(), is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void user_can_get_domain_profile_and_attributes() {

        //add Ajay to domain managed by Karo
        String domainReference = "dom-311d5438-e546-43ce-8f91-c452a154ce5f";
        Domain domain = new Domain(null, null, domainReference);
        User user = user("usr-9832620d-ec53-43a1-873d-efdc50d34ad1");
        Domain result = domainService.addUserToDomain(domain, user, token);
        assertNotNull(result);
        String ajayToken = getToken(AJAY_USERNAME, AJAY_PASSWORD);
        String profileReference = "prf-746d461f-31d9-4751-8d3a-2256d03846b7";
        Profile profile = profileService.getProfile(profileReference, ajayToken);
        assertThat(profile.getReference(), is("prf-746d461f-31d9-4751-8d3a-2256d03846b7"));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("fruit", "Banana"));

        profile = profileService.getDomainProfile(domainReference, ajayToken);
        assertThat(profile.getReference(), is("prf-746d461f-31d9-4751-8d3a-2256d03846b7"));
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("fruit", "Banana"));

        String fruit = profileService.getProfileAttribute(profileReference, "fruit", ajayToken);
        assertThat(fruit, is("Banana"));

        String colour = profileService.getDomainProfileAttribute(domainReference, "colour", ajayToken);
        assertThat(colour, is("Black"));

        Domain deletedResult = domainService.removeUserFromDomain(user,domain,token);
        assertNotNull(deletedResult);
    }

    @Test
    public void user_can_get_own_profile_and_attributes() {

        String karoRef = "usr-d8749acf-6a22-4438-accc-cc8d1877ba36";
        Profile profile = profileService.getUserProfile("usr-d8749acf-6a22-4438-accc-cc8d1877ba36", token);
        assertThat(profile.getAttributes(), IsMapContaining.hasEntry("name", "Karo Testing"));

        String name = profileService.getUserProfileAttribute(karoRef, "name", token);
        assertThat(name, is("Karo Testing"));

    }

    @Test
    public void user_can_change_own_profile() {

        //Ajay
        String ajayToken = getToken(AJAY_USERNAME, AJAY_PASSWORD);
        String ajayReference = "usr-9832620d-ec53-43a1-873d-efdc50d34ad1";
        Profile origProfile = profileService.getUserProfile(ajayReference, ajayToken);
        assertNotNull(origProfile);
        assertThat(origProfile.getAttributes(), IsMapContaining.hasEntry("name", "Ajay Testing"));
        assertThat(origProfile.getAttributes(), IsMapContaining.hasEntry("email", "embl.ebi.tsi+ajay@gmail.com"));

        Profile updatedProfile = profileService.updateProfile(origProfile.getReference(), ImmutableMap.of("car", "Volvo"), ajayToken);
        assertThat(updatedProfile.getUserReference(), is(origProfile.getUserReference()));
        assertThat(updatedProfile.getAttributeNames().size(), is(1));
        assertThat(updatedProfile.getAttributes(), IsMapContaining.hasEntry("car", "Volvo"));

        updatedProfile = profileService.patchProfile(origProfile.getReference(), ImmutableMap.of("pet", "Turtle"), ajayToken);

        assertThat(updatedProfile.getUserReference(), is(origProfile.getUserReference()));
        assertThat(updatedProfile.getAttributeNames().size(), is(2));
        assertThat(updatedProfile.getAttributes(), IsMapContaining.hasEntry("car", "Volvo"));
        assertThat(updatedProfile.getAttributes(), IsMapContaining.hasEntry("pet", "Turtle"));

        updatedProfile = profileService.deleteProfileAttribute(origProfile.getReference(), "car", ajayToken);
        assertThat(updatedProfile.getUserReference(), is(origProfile.getUserReference()));
        assertThat(updatedProfile.getAttributeNames().size(), is(1));
        assertThat(updatedProfile.getAttributes(), IsMapContaining.hasEntry("pet", "Turtle"));
        //clean
        updatedProfile = profileService.updateProfile(origProfile.getReference(), origProfile.getAttributes(), ajayToken);
        assertNotNull(updatedProfile);

    }


    @Test
    public void can_add_user_to_a_domain() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_add_user_to_a_domain-"+token);
        Domain domain = new Domain(null, null, DOMAIN_WHERE_TEST_USER_IS_A_MANAGER);
        User user = user("usr-cebc0e02-24e1-40a6-b0a5-75c10ddffb86");
        Domain result = domainService.addUserToDomain(domain, user, token);
        assertNotNull(result);
        Domain deletedResult = domainService.removeUserFromDomain(user,domain,token);
        assertNotNull(deletedResult);
    }

    @Test
    public void can_add_manager_to_a_domain() {
        LOGGER.trace("[ApplicationIntegrationTest] - can_add_manager_to_a_domain-"+token);
        Domain domain = new Domain(null, null, DOMAIN_WHERE_TEST_USER_IS_A_MANAGER);
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
        Collection<User> users = domainService.getAllManagersFromDomain(DOMAIN_WHERE_TEST_USER_IS_A_MANAGER, token);
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
