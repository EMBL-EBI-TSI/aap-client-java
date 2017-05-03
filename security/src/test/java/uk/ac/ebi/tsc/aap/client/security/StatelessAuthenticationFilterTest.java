package uk.ac.ebi.tsc.aap.client.security;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.mock;

/**
 * @author Uppendra Kumbham  <ukumbham@ebi.ac.uk>
 * @since 06/10/2016.
 */
public class StatelessAuthenticationFilterTest {

    private StatelessAuthenticationFilter subject;

    @Before
    public void setUp() throws Exception {
        TokenAuthenticationService mockAuthService = mock(TokenAuthenticationService.class);
        subject = new StatelessAuthenticationFilter(mockAuthService);
    }

    @After
    public void tearDown() throws Exception {

    }

    private MockHttpServletResponse doFilter(MockHttpServletRequest request) throws IOException, ServletException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        subject.doFilter(request, response, chain);
        return response;
    }

    @Test public void
    jwtFilterValidTest() throws IOException, ServletException, NoSuchAlgorithmException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://somesecuredomain.com");
        String token = "Bearer " + JWTHelper.token();
        request.addHeader("Authorization", token);
        MockHttpServletResponse response = doFilter(request);
        Assert.assertEquals(200, response.getStatus());
    }
}

