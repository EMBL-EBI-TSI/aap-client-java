package uk.ac.ebi.tsc.aap.client.model;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class UserBuilderTest {

    @Test public void
    can_build_minimalist_user() {
        UserBuilder subject = new UserBuilder("foo");
        User actual = subject.build();
        assertThat(actual.getUserReference(), equalTo("foo"));
    }

    @Test public void
    can_build_user_with_domain_names() {
        UserBuilder subject = new UserBuilder("anything");
        subject.withDomains("one", "two");

        User actual = subject.build();

        assertThat(actual.getDomains(), hasItem(hasProperty("domainName", equalTo("one"))));
        assertThat(actual.getDomains(), hasItem(hasProperty("domainName", equalTo("two"))));
    }
}
