package uk.ac.ebi.tsc.aap.client.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 21/07/2016
 */
public class TokenAuthenticationServiceTest {

    private TokenAuthenticationService subject;

    @Before
    public void setUp() throws Exception {
        UserDetails aUser = mock(UserDetails.class);
        when(aUser.getUsername()).thenReturn("pretend-user");

        TokenHandler mockHandler = mock(TokenHandler.class);
        when(mockHandler.parseUserFromToken("pretend-invalid-token")).thenThrow(new RuntimeException("Exception: Pretend Invalid Token"));
        subject = new TokenAuthenticationService(mockHandler);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test public void
    returns_auth_for_valid_token() {
        HttpServletRequest request = withAuthorizationHeader("Bearer pretend-valid-token");
        Authentication auth = subject.getAuthentication(request);
        assertNotNull(auth);
    }

    @Test public void
    detects_empty_authorization_header() {
        HttpServletRequest request = withAuthorizationHeader("");
        Authentication auth = subject.getAuthentication(request);
        assertNull(auth);
    }

    @Test public void
    detects_non_bearer_authorization_header() {
        HttpServletRequest request = withAuthorizationHeader("blah ");
        Authentication auth = subject.getAuthentication(request);
        assertNull(auth);
    }

    @Test public void
    detects_invalid_jwt() {
        HttpServletRequest request = withAuthorizationHeader("Bearer pretend-invalid-token");
        when(request.getHeader("Authorization").
                equals("Bearer pretend-invalid-token")).thenReturn(null);
        Authentication auth = subject.getAuthentication(request);
        assertNull(auth);
    }

    private HttpServletRequest withAuthorizationHeader(String value) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(value);
        System.out.println("request.getHeader(\"Authorization\")" + request.getHeader("Authorization"));
        return request;
    }

}