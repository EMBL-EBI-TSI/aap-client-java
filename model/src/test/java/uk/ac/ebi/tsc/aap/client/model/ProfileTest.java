package uk.ac.ebi.tsc.aap.client.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileTest {

    private boolean dummyAccountNonLocked;
    @Mock
    private Domain mockDomain;
    private Map<String, String> dummyAttributes;
    private String dummyProfileReference;
    private String dummyUserReference;
    @Mock
    private User mockUser;

    @Test public void
    can_build_minimalistic_profile() {
        Profile.Builder subject = Profile.builder().withReference("bar");
        Profile actual = subject.build();
        assertThat(actual.getReference(), equalTo("bar"));
    }

    @Test public void
    can_build_profile_with_given_attribute() {
        Profile.Builder subject = Profile.builder()
                .withAttribute("given", "attribute");
        Profile actual = subject.build();
        assertThat(actual.getAttribute("given"), equalTo("attribute"));
    }

    @Test public void
    can_build_profile_with_several_attributes() {
        Profile.Builder subject = Profile.builder()
                .withAttribute("given", "attribute")
                .withAttribute("other", "one");

        Profile actual = subject.build();

        assertThat(actual.getAttributeNames().size(), equalTo(2));
        assertThat(actual.getAttributeNames(), hasItem("given"));
        assertThat(actual.getAttributeNames(), hasItem("other"));
    }

    @Test public void
    can_get_all_attributes() {
        Profile profile = Profile.builder()
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
        Profile profile = Profile.builder()
                .withUser(userReference)
                .build();
        assertNull(profile.getUser());
    }

    @Test public void
    is_empty_domain_reference_ignored() {
        String domainReference = null;
        Profile profile = Profile.builder()
                .withDomain(domainReference)
                .build();
        assertNull(profile.getDomain());
    }

    @Test public void
    can_build_profile_with_domain() {
        Profile profile = Profile.builder()
                .withDomain("domRefe")
                .build();
        assertThat(profile.getDomain().getDomainReference(), is("domRefe"));
    }

    @Test public void
    can_build_profile_without_reference() {
        Profile profile = Profile.builder()
                .withUser("abc")
                .build();
        assertNull(profile.getReference());
    }

    @Test public void
    can_define_a_schema() {
        Profile.Builder builder = Profile.builder()
                .withSchema("something!");
        Profile actual = builder.build();
        assertThat(actual.getSchema(), is("something!"));
    }

    @Test
    public void isUserProfile() {
        // It's neither a user nor a domain profile!
        Profile profile = Profile.builder().build();
        assertFalse(profile.isUserProfile());

        // It's a domain profile
        profile = new Profile(dummyProfileReference, null, mockDomain, dummyAttributes);
        assertFalse(profile.isUserProfile());

        // It's a user profile
        profile = new Profile(dummyProfileReference, mockUser, null, dummyAttributes);
        dummyUserReference = "dummyUserReference";
        when(mockUser.getUserReference()).thenReturn(dummyUserReference);

        assertTrue(profile.isUserProfile());

        verify(mockUser, times(1)).getUserReference();
        verifyNoMoreInteractions(mockUser);
    }

    @Test
    public void retrieveUserAccountNonLockedThrowsExceptionIfNotAUserProfile() {
        // It's neither a user nor a domain profile!
        Profile profile = Profile.builder().build();
        try {
            profile.retrieveUserAccountNonLocked();
            fail("Should throw IllegalStateException if not a user profile");
        } catch (final IllegalStateException illegalStateException) {}

        // It's a domain profile
        profile = new Profile(dummyProfileReference, null, mockDomain, dummyAttributes);
        try {
            profile.retrieveUserAccountNonLocked();
            fail("Should throw IllegalStateException if not a user profile");
        } catch (final IllegalStateException illegalStateException) {}
    }

    @Test
    public void retrieveUserAccountNonLocked() {
        // It's a user profile
        Profile profile = new Profile(dummyProfileReference, mockUser, null, dummyAttributes);
        dummyUserReference = "dummyUserReference";
        dummyAccountNonLocked = true;
        when(mockUser.getUserReference()).thenReturn(dummyUserReference);
        when(mockUser.isAccountNonLocked()).thenReturn(dummyAccountNonLocked);

        assertTrue(profile.retrieveUserAccountNonLocked());

        verify(mockUser, times(1)).getUserReference();
        verify(mockUser, times(1)).isAccountNonLocked();
        verifyNoMoreInteractions(mockUser);

        reset(mockUser);

        dummyAccountNonLocked = false;
        when(mockUser.getUserReference()).thenReturn(dummyUserReference);
        when(mockUser.isAccountNonLocked()).thenReturn(dummyAccountNonLocked);

        assertFalse(profile.retrieveUserAccountNonLocked());

        verify(mockUser, times(1)).getUserReference();
        verify(mockUser, times(1)).isAccountNonLocked();
        verifyNoMoreInteractions(mockUser);
    }
}
