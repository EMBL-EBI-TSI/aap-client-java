package uk.ac.ebi.tsc.aap.client.model;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class UserTest {

    @Test public void
    can_build_minimalist_user() {
        User.Builder subject = new User.Builder("foo");
        User actual = subject.build();
        assertThat(actual.getUserReference(), equalTo("foo"));
    }

    @Test public void
    can_build_user_with_domain_names() {
        User.Builder subject = new User.Builder("anything");
        subject.withDomains("one", "two");

        User actual = subject.build();

        assertThat(actual.getDomains(), hasItem(hasProperty("domainName", equalTo("one"))));
        assertThat(actual.getDomains(), hasItem(hasProperty("domainName", equalTo("two"))));
    }
}
