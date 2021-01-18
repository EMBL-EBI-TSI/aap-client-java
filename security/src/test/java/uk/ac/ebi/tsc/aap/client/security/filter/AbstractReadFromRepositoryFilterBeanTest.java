package uk.ac.ebi.tsc.aap.client.security.filter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;

import uk.ac.ebi.tsc.aap.client.exception.AAPLockedException;
import uk.ac.ebi.tsc.aap.client.exception.AAPUsernameNotFoundException;
import uk.ac.ebi.tsc.aap.client.model.ErrorResponse;
import uk.ac.ebi.tsc.aap.client.security.UserAuthentication;
import uk.ac.ebi.tsc.aap.client.security.audit.Action;
import uk.ac.ebi.tsc.aap.client.security.audit.Actor;
import uk.ac.ebi.tsc.aap.client.security.audit.SecurityLogger;
import uk.ac.ebi.tsc.aap.client.security.audit.Severity;
import uk.ac.ebi.tsc.aap.client.security.repo.GenericUserRepository;
import uk.ac.ebi.tsc.aap.client.security.util.SecurityUtil;

/**
 * Filter bean unit testing.
 * 
 * @author geoff
 */
@RunWith(PowerMockRunner.class)
// https://stackoverflow.com/questions/16520699/mockito-powermock-linkageerror-while-mocking-system-class
@PowerMockIgnore("javax.security.auth.Subject")
public class AbstractReadFromRepositoryFilterBeanTest {

    private static final String dummyUserIdentifier = "dummyUserIdentifier";
    private static final String dummyLockedMessage = String.format("Token-authenticated user %s has locked account",
                                                                   dummyUserIdentifier);
    private static final String dummyUsernameNotFoundMessage = String.format("Token-authenticated user %s not found in repository",
                                                                             dummyUserIdentifier);

    @Captor
    private ArgumentCaptor<ErrorResponse> errorResponseCaptor;
    @Captor
    private ArgumentCaptor<String> requestAttributeNameCaptor;
    @Mock
    private GenericUserRepository mockGenericUserRepository;
    private FakeReadFromRepositoryFilterBean fakeReadFromRepositoryFilterBean;
    @Mock
    private FilterChain mockFilterChain;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private SecurityLogger mockSecurityLogger;
    @Mock
    private UserAuthentication mockUserAuthentication;

    // Fake filter bean to test
    private class FakeReadFromRepositoryFilterBean extends AbstractReadFromRepositoryFilterBean {

        protected static final String THROW_LOCKED = "throw locked exception";
        protected static final String THROW_USERNAME_NOT_FOUND = "throw_username_not_found";

        protected FakeReadFromRepositoryFilterBean(final SecurityLogger securityLogger,
                                                   final GenericUserRepository userRepository) {
            super(securityLogger, userRepository);
        }

        @Override
        protected void loadUserDetails(final Authentication authentication)
                                       throws AAPLockedException, AAPUsernameNotFoundException {
            if (authentication != null) {
                // Fake exception throwing mechanism
                switch (authentication.getName()) {
                    case THROW_LOCKED :
                        logAndThrowAccountLocked(dummyUserIdentifier);
                    case THROW_USERNAME_NOT_FOUND :
                        logAndThrowUsernameNotFound(dummyUserIdentifier);
                    default :
                        // do nothing - filter bean works as expected!
                }
            }
        }
    }

    @Before
    public void setUp() {
        fakeReadFromRepositoryFilterBean = new FakeReadFromRepositoryFilterBean(mockSecurityLogger,
                                                                                mockGenericUserRepository);
        mockStatic(SecurityUtil.class);
    }

    @Test
    // https://github.com/powermock/powermock/issues/787 (+ https://github.com/powermock/powermock/issues/785#issuecomment-298344143)
    @PrepareForTest(SecurityUtil.class)
    public void constructorArgs() {
        try {
          new FakeReadFromRepositoryFilterBean(null, mockGenericUserRepository);
          fail("Should not accept a null security logger");
        } catch (IllegalArgumentException e) {}

        try {
            new FakeReadFromRepositoryFilterBean(mockSecurityLogger, null);
            fail("Should not accept a null generic user repository");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    @PrepareForTest(SecurityUtil.class)
    public void doFilter() {
        PowerMockito.when(SecurityUtil.retrieveAuthenticatedName()).thenReturn("inconsequential");
        PowerMockito.when(SecurityUtil.retrieveAuthentication()).thenReturn(mockUserAuthentication);
        when(mockUserAuthentication.getName()).thenReturn("");

        try {
            fakeReadFromRepositoryFilterBean.doFilter(mockRequest, mockResponse, mockFilterChain);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception!");
        }

        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthenticatedName();
        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthentication();

        verifyZeroInteractions(mockRequest, mockResponse, mockSecurityLogger, mockGenericUserRepository);
        try {
            verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception : " + e.getMessage());
        }
    }

    @Test
    @PrepareForTest(SecurityUtil.class)
    public void doFilterUserLockedExceptionHandled() {
        PowerMockito.when(SecurityUtil.retrieveAuthenticatedName()).thenReturn("inconsequential");
        PowerMockito.when(SecurityUtil.retrieveAuthentication()).thenReturn(mockUserAuthentication);
        when(mockUserAuthentication.getName()).thenReturn(FakeReadFromRepositoryFilterBean.THROW_LOCKED);
        // Should be hitting SecurityLogger.logActionOnSelf here
        doNothing().when(mockRequest)
                   .setAttribute(requestAttributeNameCaptor.capture(),
                                 errorResponseCaptor.capture());

        try {
            fakeReadFromRepositoryFilterBean.doFilter(mockRequest, mockResponse, mockFilterChain);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception!");
        }

        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthenticatedName();
        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthentication();

        verifyZeroInteractions(mockResponse);
        verify(mockUserAuthentication).getName();
        verify(mockSecurityLogger).logActionOnSelf(any(Severity.class), any(Action.class),
                                                   any(Actor.class), anyString(),
                                                   anyMapOf(String.class, Object.class));
        verify(mockRequest).setAttribute(anyString(), anyObject());
        verifyStatic(SecurityUtil.class);
        SecurityUtil.nullifyAuthentication();
        try {
            verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception : " + e.getMessage());
        }

        // Verify the error object being written to the response 
        assertTrue(AbstractReadFromRepositoryFilterBean.ERROR_RESPONSE
                  .equals(requestAttributeNameCaptor.getValue()));
        final ErrorResponse capturedErrorResponse = errorResponseCaptor.getValue();
        assertTrue(AAPLockedException.CODE_ACCOUNT_LOCKED.equals(capturedErrorResponse.getError()));
        assertTrue(dummyLockedMessage.equals(capturedErrorResponse.getMessage()));
        assertTrue(new AAPLockedException("").getClass().getCanonicalName()
                  .equals(capturedErrorResponse.getException() ));
    }

    @Test
    @PrepareForTest(SecurityUtil.class)
    public void doFilterUsernameNotFoundExceptionHandled() {
        PowerMockito.when(SecurityUtil.retrieveAuthenticatedName()).thenReturn("inconsequential");
        PowerMockito.when(SecurityUtil.retrieveAuthentication()).thenReturn(mockUserAuthentication);
        when(mockUserAuthentication.getName()).thenReturn(FakeReadFromRepositoryFilterBean.THROW_USERNAME_NOT_FOUND);
        // Should be hitting SecurityLogger.logActionOnSelf here
        doNothing().when(mockRequest)
                   .setAttribute(requestAttributeNameCaptor.capture(),
                                 errorResponseCaptor.capture());

        try {
            fakeReadFromRepositoryFilterBean.doFilter(mockRequest, mockResponse, mockFilterChain);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception!");
        }

        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthenticatedName();
        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthentication();

        verifyZeroInteractions(mockResponse);
        verify(mockUserAuthentication).getName();
        verify(mockSecurityLogger).logActionOnSelf(any(Severity.class), any(Action.class),
                                                   any(Actor.class), anyString(),
                                                   anyMapOf(String.class, Object.class));
        verify(mockRequest).setAttribute(anyString(), anyObject());
        verifyStatic(SecurityUtil.class);
        SecurityUtil.nullifyAuthentication();
        try {
            verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception : " + e.getMessage());
        }

        // Verify the error object being written to the response 
        assertTrue(AbstractReadFromRepositoryFilterBean.ERROR_RESPONSE
                  .equals(requestAttributeNameCaptor.getValue()));
        final ErrorResponse capturedErrorResponse = errorResponseCaptor.getValue();
        assertTrue(AAPUsernameNotFoundException.CODE_USERNAME_NOT_FOUND
                  .equals(capturedErrorResponse.getError()));
        assertTrue(dummyUsernameNotFoundMessage.equals(capturedErrorResponse.getMessage()));
        assertTrue(new AAPUsernameNotFoundException("").getClass().getCanonicalName()
                  .equals(capturedErrorResponse.getException() ));
    }

    @Test
    @PrepareForTest(SecurityUtil.class)
    public void doFilterNoAuthentication() {
        PowerMockito.when(SecurityUtil.retrieveAuthenticatedName()).thenReturn("inconsequential");
        PowerMockito.when(SecurityUtil.retrieveAuthentication()).thenReturn(null);

        try {
            fakeReadFromRepositoryFilterBean.doFilter(mockRequest, mockResponse, mockFilterChain);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception!");
        }

        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthenticatedName();
        verifyStatic(SecurityUtil.class);
        SecurityUtil.retrieveAuthentication();

        verifyZeroInteractions(mockRequest, mockResponse, mockSecurityLogger);
        try {
            verify(mockFilterChain).doFilter(mockRequest, mockResponse);
        } catch (IOException | ServletException e) {
            fail("Not supposed to throw an exception : " + e.getMessage());
        }
    }
}