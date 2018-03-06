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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
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
		String expectedUrl = String.format("/users/%s/domains", "usr-"+userReference);
		String mockResponse =
				" [" +
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
		User user = user(userReference);

		Collection<Domain> domains = subject.getDomains(user, "a-token");

		this.domainsApi.verify();
		assertThat(domains.size(), equalTo(2));
		assertThat(domains, contains(
				hasProperty("domainName", equalTo("foo")),
				hasProperty("domainName", equalTo("bar"))
				));
	}

	@Test public void
	can_retrieves_the_list_of_domain_from_the_api_with_prefix() {
		String userReference = "usr-foo-bar-buzz";
		String expectedUrl = String.format("/users/%s/domains", userReference);
		String mockResponse =
				" [" +
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
		User user = user(userReference);

		Collection<Domain> domains = subject.getDomains(user, "a-token");

		this.domainsApi.verify();
		assertThat(domains.size(), equalTo(2));
		assertThat(domains, contains(
				hasProperty("domainName", equalTo("foo")),
				hasProperty("domainName", equalTo("bar"))
		));
	}


	@Test public void
	can_create_a_domain() {
		Domain domain = domain("foo", "The Foo");
		String mockResponse =
				"{" +
						"  \"domainReference\" : \"usr-abcdef\"" +
						"}, " ;
		this.domainsApi.expect(requestTo("/domains/")).andExpect(method(HttpMethod.POST))
		.andRespond(withStatus(HttpStatus.CREATED).body(mockResponse).contentType(MediaType.APPLICATION_JSON));

		Domain created = subject.createDomain(domain, "a-token");

		this.domainsApi.verify();
		assertThat(created.getDomainReference(), equalTo("usr-abcdef"));
	}

	@Test(expected = RuntimeException.class) public void
	returns_suitable_message_on_creation_error() {
		this.domainsApi.expect(requestTo("/domains/")).andExpect(method(HttpMethod.POST))
		.andRespond(withServerError());

		subject.createDomain(aDomain(), "a-token");

		this.domainsApi.verify();
	}

	@Test(expected = RuntimeException.class) public void
	detects_backend_down() {
		this.domainsApi.expect(requestTo("/domains/")).andExpect(method(HttpMethod.POST))
		.andRespond(withTimeout());

		subject.createDomain(aDomain(), "a-token");

		this.domainsApi.verify();
	}

	private Domain domain(String name, String description) {
		return new Domain(name, description, null);
	}

	private Domain aDomain() {
		return domain("wonderland", "The Wonderland");
	}

	@Test public void
	can_delete_domain() {
		String domainReference = "foo-bar";
		Domain toDelete = new Domain(null, null, domainReference);
		String expectedUrl = String.format("/domains/%s", "dom-"+domainReference);
		this.domainsApi.expect(requestTo(expectedUrl))
		.andRespond(withSuccess().body("{\"domainReference\" : \""+domainReference+"\"}").contentType(MediaType.APPLICATION_JSON));

		Domain deleted = subject.deleteDomain(toDelete,"a-token");

		this.domainsApi.verify();
		assertThat(deleted, notNullValue());
	}

	@Test public void
	can_delete_domain_with_prefixed_reference(){
		String domainReference = "dom-foo-bar";
		Domain toDelete = new Domain(null, null, domainReference);
		String expectedUrl = String.format("/domains/%s", domainReference);
		this.domainsApi.expect(requestTo(expectedUrl))
				.andRespond(withSuccess().body("{\"domainReference\" : \""+domainReference+"\"}").contentType(MediaType.APPLICATION_JSON));

		Domain deleted = subject.deleteDomain(toDelete,"a-token");

		this.domainsApi.verify();
		assertThat(deleted, notNullValue());
	}


	@Test public void
	can_add_user_to_a_domain() {
		Domain toJoin = new Domain(null, null, "the-foo");
		User toAdd = user("the-bar");
		String expectedUrl = String.format("/domains/%s/%s/user", "dom-"+toJoin.getDomainReference(), "usr-"+toAdd.getUserReference());
		String mockResponse =
				"{" +
						"  \"domainReference\" : \"d7087264-9111-4693-9ea6-233898d91025\"," +
						"  \"adminDomainReference\" : \"c06688f3-1ee8-4a84-b8c3-7ef735f6a18c\"," +
						"  \"domainName\" : \"Test Test\"," +
						"  \"domainDesc\" : \"Test Domain\"," +
						"  \"users\" : [ {" +
						"    \"userReference\" : \"12845ac8-e71e-45d4-8e85-3dee7d98664e\"," +
						"    \"userName\" : \"someone\"," +
						"    \"email\" : \"test@example.com\"" +
						"  } ]" +
						"}";
		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.PUT))
		.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));

		Domain updated = subject.addUserToDomain(toJoin, toAdd, "a-token");

		this.domainsApi.verify();
		assertThat(updated.getUsers(), notNullValue());
	}

	@Test public void
	can_add_user_to_domain_managers(){
		Domain toJoin = new Domain(null, null, "dom-the-foo");
		User toAdd = user("usr-the-bar");
		String expectedUrl = String.format("/domains/%s/managers/%s", toJoin.getDomainReference(), toAdd.getUserReference());
		String mockResponse = "{\n" +
				"  \"domainReference\": \"dom-the-foo\",\n" +
				"  \"domainName\": \"self.foo\",\n" +
				"  \"domainDesc\": \"The Foo\",\n" +
				"  \"users\": [],\n" +
				"  \"managers\": [\n" +
				"    {\n" +
				"      \"userName\": \"karo\",\n" +
				"      \"domains\": null,\n" +
				"      \"userReference\": \"usr-the-bar\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.PUT))
				.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));

		Domain updated = subject.addManagerToDomain(toJoin, toAdd, "a-token");

		this.domainsApi.verify();
		assertThat(updated.getManagers(), notNullValue());
	}

	@Test public void
	can_add_user_to_a_domain_with_prefix_reference() {
		Domain toJoin = new Domain(null, null, "dom-the-foo");
		User toAdd = user("usr-the-bar");
		String expectedUrl = String.format("/domains/%s/%s/user", toJoin.getDomainReference(), toAdd.getUserReference());
		String mockResponse =
				"{" +
						"  \"domainReference\" : \"d7087264-9111-4693-9ea6-233898d91025\"," +
						"  \"adminDomainReference\" : \"c06688f3-1ee8-4a84-b8c3-7ef735f6a18c\"," +
						"  \"domainName\" : \"Test Test\"," +
						"  \"domainDesc\" : \"Test Domain\"," +
						"  \"users\" : [ {" +
						"    \"userReference\" : \"12845ac8-e71e-45d4-8e85-3dee7d98664e\"," +
						"    \"userName\" : \"someone\"," +
						"    \"email\" : \"test@example.com\"" +
						"  } ]" +
						"}";
		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.PUT))
				.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));

		Domain updated = subject.addUserToDomain(toJoin, toAdd, "a-token");

		this.domainsApi.verify();
		assertThat(updated.getUsers(), notNullValue());
	}


	@Test 
	public void can_get_domain_reference(){
		String domainReference = "f15266eb-0f76-4e75-9b9f-f9689f8c06b8";
		String expectedUrl = String.format("/domains/dom-%s", domainReference);

		String mockResponse = "{\n" +
				"  \"domainReference\" : \"f15266eb-0f76-4e75-9b9f-f9689f8c06b8\",\n" +
				"  \"adminDomainReference\" : \"7760cc46-4c27-4c36-aefc-478ba06e774a\",\n" +
				"  \"domainName\" : \"self.TEAM_domain_test_PORTAL\",\n" +
				"  \"domainDesc\" : \"Domain TEAM_domain_test_PORTAL created\",\n" +
				"  \"users\" : null,\n" +
				"  \"managers\" : null\n" +
				"}";

		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.GET))
		.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));

		Domain updated = subject.getDomainByReference(domainReference, "token");
		this.domainsApi.verify();
		assertThat(updated.getDomainReference(), is(domainReference));
	}
	
	@Test
	public void can_delete_user_from_domain(){
		
		Domain toJoin = new Domain(null, null, "the-foo");
		User toAdd = user("the-bar");
		
		String mockResponse = "{\n  \"domainReference\" : \"f15266eb-0f76-4e75-9b9f-f9689f8c06b8\",\n "
				+ " \"adminDomainReference\" : \"7760cc46-4c27-4c36-aefc-478ba06e774a\",\n  "
				+ "\"domainName\" : \"self.TEAM_domain_test_PORTAL\",\n "
				+ " \"domainDesc\" : \"Domain TEAM_domain_test_PORTAL created\",\n  \"users\" : [ ],\n  \"managers\" : null\n}";
				
		String expectedUrl = String.format("/domains/dom-%s/usr-%s/user",  toJoin.getDomainReference(), toAdd.getUserReference());
		
		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.DELETE))
		.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));
		
		toJoin = subject.removeUserFromDomain(toAdd, toJoin, "token");
		this.domainsApi.verify();
		
		assertThat(toJoin.getUsers().size(), is(0));
	}

	@Test
	public void can_delete_manager_from_domain(){

		Domain toJoin = new Domain(null, null, "the-foo");
		User toAdd = user("the-bar");

		String mockResponse = "{\n  \"domainReference\" : \"f15266eb-0f76-4e75-9b9f-f9689f8c06b8\",\n "
				+ " \"adminDomainReference\" : \"7760cc46-4c27-4c36-aefc-478ba06e774a\",\n  "
				+ "\"domainName\" : \"self.TEAM_domain_test_PORTAL\",\n "
				+ " \"domainDesc\" : \"Domain TEAM_domain_test_PORTAL created\",\n  \"users\" : [ ],\n  \"managers\" : [ ]\n}";

		String expectedUrl = String.format("/domains/dom-%s/managers/usr-%s",  toJoin.getDomainReference(), toAdd.getUserReference());

		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));

		toJoin = subject.removeManagerFromDomain(toAdd, toJoin, "token");
		this.domainsApi.verify();

		assertThat(toJoin.getManagers().size(), is(0));
	}
	
	@Test
	public void can_get_list_of_users_for_domain(){
		
		String domainReference = "domainReference";
		
		String mockResponse = "[ {\n  \"userReference\" : \"57d94c6d-565c-46b8-aabe-49c9461fc707\",\n "
				+ " \"userName\" : \"946838e27f8831afd866ff6f66ad37f075626ae3\",\n  \"email\" :"
				+ " \"navispretheeba@gmail.com\",\n  \"mobile\" : null,\n  \"domains\" : null,\n  "
				+ "\"links\" : [ {\n    \"rel\" : \"self\",\n   "
				+ " \"href\" : \"https://dev.api.aap.tsi.ebi.ac.uk/users/usr-57d94c6d-565c-46b8-aabe-49c9461fc707\"\n  } ]\n} ]";
				
		String expectedUrl = String.format("/domains/dom-%s/users", domainReference);	
		
		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.GET))
		.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));
		

		Collection<User> users = subject.getAllUsersFromDomain(domainReference, "token");
		
		this.domainsApi.verify();
		
		assertThat(users.size(), is(1));
		
	}

	@Test
	public void can_get_list_of_managers_for_domain(){

		String domainReference = "domainReference";

		String mockResponse = "[ {\n  \"userReference\" : \"57d94c6d-565c-46b8-aabe-49c9461fc707\",\n "
				+ " \"userName\" : \"946838e27f8831afd866ff6f66ad37f075626ae3\",\n  \"email\" :"
				+ " \"navispretheeba@gmail.com\",\n  \"mobile\" : null,\n  \"domains\" : null,\n  "
				+ "\"links\" : [ {\n    \"rel\" : \"self\",\n   "
				+ " \"href\" : \"https://dev.api.aap.tsi.ebi.ac.uk/users/usr-57d94c6d-565c-46b8-aabe-49c9461fc707\"\n  } ]\n} ]";

		String expectedUrl = String.format("/domains/dom-%s/managers", domainReference);

		this.domainsApi.expect(requestTo(expectedUrl)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));


		Collection<User> users = subject.getAllManagersFromDomain(domainReference, "token");

		this.domainsApi.verify();

		assertThat(users.size(), is(1));

	}

	@Test
	public void should_retrieve_list_of_membership_domains(){
		String mockResponse =
				" [" +
						"{" +
						"  \"domainName\" : \"foo\"," +
						"  \"domainDesc\" : \"The Foo\"" +
						"}, " +
						"{" +
						"  \"domainName\" : \"bar\"," +
						"  \"domainDesc\" : \"The Bar\"" +
						"}" +
						"]";
		this.domainsApi.expect(requestTo("/my/domains")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));
		Collection<Domain> domains = subject.getMyDomains("a-token");

		this.domainsApi.verify();
		assertThat(domains.size(), equalTo(2));
		assertThat(domains, contains(
				hasProperty("domainName", equalTo("foo")),
				hasProperty("domainName", equalTo("bar"))
		));
	}

	@Test(expected = RuntimeException.class)
	public void should_return_server_error_if_something_goes_wrong_while_getting_membership_domains(){
		this.domainsApi.expect(requestTo("/my/domains")).andExpect(method(HttpMethod.GET))
				.andRespond(withServerError());
		subject.getMyDomains("a-token");
		this.domainsApi.verify();
	}

	@Test
	public void should_retrieve_list_of_management_domains_for_logged_in_user(){
		String mockResponse =
				" [" +
						"{" +
						"  \"domainName\" : \"foo\"," +
						"  \"domainDesc\" : \"The Foo\"" +
						"}, " +
						"{" +
						"  \"domainName\" : \"bar\"," +
						"  \"domainDesc\" : \"The Bar\"" +
						"}" +
						"]";
		this.domainsApi.expect(requestTo("/my/management")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));
		Collection<Domain> domains = subject.getMyManagementDomains("a-token");
		this.domainsApi.verify();
		assertThat(domains.size(), equalTo(2));
		assertThat(domains, contains(
				hasProperty("domainName", equalTo("foo")),
				hasProperty("domainName", equalTo("bar"))
		));
	}

	@Test
	public void should_retrieve_empty_domains_if_logged_in_user_does_not_have(){
		String mockResponse = " ["+"]";
		this.domainsApi.expect(requestTo("/my/management")).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess().body(mockResponse).contentType(MediaType.APPLICATION_JSON));
		Collection<Domain> domains = subject.getMyManagementDomains("a-token");
		this.domainsApi.verify();
		assertThat(domains.size(), equalTo(0));

	}

	@Test(expected = RuntimeException.class)
	public void should_return_server_error_if_something_goes_wrong_while_getting_management_domains(){
		this.domainsApi.expect(requestTo("/my/management")).andExpect(method(HttpMethod.GET))
				.andRespond(withServerError());
		subject.getMyManagementDomains("a-token");
		this.domainsApi.verify();
	}

	private User user(String userReference) {
		return new User(null, null, userReference, null, null);
	}
}
