package uk.ac.ebi.tsc.aap.client.model;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
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

}
