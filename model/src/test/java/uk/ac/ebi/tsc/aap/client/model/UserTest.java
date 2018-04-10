package uk.ac.ebi.tsc.aap.client.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class UserTest {

    @Test public void
    can_build_minimalist_user() {
        User.Builder subject = User.builder().withReference("foo");
        User actual = subject.build();
        assertThat(actual.getUserReference(), equalTo("foo"));
    }

    @Test public void
    can_build_user_with_domain_names() {
        User.Builder subject = User.builder();
        subject.withDomains("one", "two");

        User actual = subject.build();

        assertThat(actual.getDomains(), hasItem(hasProperty("domainName", equalTo("one"))));
        assertThat(actual.getDomains(), hasItem(hasProperty("domainName", equalTo("two"))));
    }

    @Test public void
    does_not_leak_authentication_details() throws IOException {
        User subject = User.builder()
                .withReference("bar")
                .withUsername("foo")
                .withEmail("foo@somewhere.com")
                .withFullName("Foo Test")
                .withDomains("something")
                .build();
        ObjectMapper jsoner = Jackson2ObjectMapperBuilder.json().build();
        String jsoned = jsoner.writeValueAsString(subject);
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};

        Map<String, Object> actual = jsoner.readValue(jsoned, typeRef);

        assertThat(actual.size(), is(5));
        assertThat(actual, hasEntry("userReference", "bar"));
        assertThat(actual, hasEntry("userName", "foo"));
        assertThat(actual, hasEntry("email", "foo@somewhere.com"));
        assertThat(actual, hasEntry("fullName", "Foo Test"));
        assertThat(actual, hasEntry(is("domains"), notNullValue()));
    }
}
