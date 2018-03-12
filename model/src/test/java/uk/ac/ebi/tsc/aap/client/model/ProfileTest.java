package uk.ac.ebi.tsc.aap.client.model;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ProfileTest {

    @Test public void
    can_build_minimalistic_profile() {
        Profile.Builder subject = new Profile.Builder("bar");
        Profile actual = subject.build();
        assertThat(actual.getReference(), equalTo("bar"));
    }

    @Test public void
    can_build_profile_with_given_attribute() {
        Profile.Builder subject = new Profile.Builder("irrelevant")
                .withAttribute("given", "attribute");
        Profile actual = subject.build();
        assertThat(actual.getAttribute("given"), equalTo("attribute"));
    }

    @Test public void
    can_build_profile_with_several_attributes() {
        Profile.Builder subject = new Profile.Builder("irrelevant")
                .withAttribute("given", "attribute")
                .withAttribute("other", "one");

        Profile actual = subject.build();

        assertThat(actual.getAttributeNames().size(), equalTo(2));
        assertThat(actual.getAttributeNames(), hasItem("given"));
        assertThat(actual.getAttributeNames(), hasItem("other"));
    }

    @Test public void
    can_get_all_attributes() {
        Profile profile = new Profile.Builder("foo")
                .withAttribute("one", "un")
                .withAttribute("two", "deux")
                .build();

        Map<String, String> attributes = profile.getAttributes();

        assertThat(attributes.size(), equalTo(2));
        assertThat(attributes.get("one"), equalTo("un"));
        assertThat(attributes.get("two"), equalTo("deux"));
    }

    @Test public void
    is_empty_user_reference_ignored() {
        String userReference = null;
        Profile profile = new Profile.Builder()
                .withUser(userReference)
                .build();
        assertNull(profile.getUser());
    }

    @Test public void
    is_empty_domain_reference_ignored() {
        String domainReference = null;
        Profile profile = new Profile.Builder()
                .withDomain(domainReference)
                .build();
        assertNull(profile.getDomain());
    }

    @Test public void
    can_build_profile_with_domain() {
        Profile profile = new Profile.Builder()
                .withDomain("domRefe")
                .build();
        assertThat(profile.getDomain().getDomainReference(), is("domRefe"));
    }

    @Test public void
    can_build_profile_without_reference() {
        Profile profile = new Profile.Builder()
                .withUser("abc")
                .build();
        assertNull(profile.getReference());
    }
}
