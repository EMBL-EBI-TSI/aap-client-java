package uk.ac.ebi.tsc.aap.client.security.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.security.UserAuthentication;

/**
 * Unit test Security utility.
 * 
 * @author geoff
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityUtilTest {

    @Mock
    private Domain mockNonAdminDomain;
    @Mock
    private Domain mockAdminDomain;
    private String dummyAuthenticationName = "dummyAuthenticationName";
    @Mock
    private UserAuthentication mockUserAuthentication;

    @SuppressWarnings("rawtypes")
    private final Collection dummyAuthoritiesWithAdmin = new ArrayList<GrantedAuthority>();
    @SuppressWarnings("rawtypes")
    private final Collection dummyAuthoritiesWithoutAdmin = new ArrayList<GrantedAuthority>();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        SecurityContextHolder.createEmptyContext();

        dummyAuthoritiesWithAdmin.add(mockAdminDomain);
        dummyAuthoritiesWithAdmin.add(mockNonAdminDomain);

        dummyAuthoritiesWithoutAdmin.add(mockNonAdminDomain);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void hasAdminRole() {
        // No authentication object
        SecurityContextHolder.getContext().setAuthentication(null);

        assertFalse(SecurityUtil.hasAdminRole());

        // Non-admin domain(s) in authentication object
        SecurityContextHolder.getContext().setAuthentication(mockUserAuthentication);

        when(mockUserAuthentication.getAuthorities()).thenReturn(dummyAuthoritiesWithoutAdmin);
        when(mockNonAdminDomain.getAuthority()).thenReturn("dummyAuthority");

        assertFalse(SecurityUtil.hasAdminRole());

        verifyZeroInteractions(mockAdminDomain);

        reset(mockUserAuthentication, mockAdminDomain, mockNonAdminDomain);

        // Admin domain in the authentication object
        when(mockUserAuthentication.getAuthorities()).thenReturn(dummyAuthoritiesWithAdmin);
        when(mockNonAdminDomain.getAuthority()).thenReturn("dummyAuthority");
        when(mockAdminDomain.getAuthority()).thenReturn(SecurityUtil.AAP_ADMIN_ROLE);

        assertTrue(SecurityUtil.hasAdminRole());

        verify(mockAdminDomain).getAuthority();
    }

    @Test
    public void nullifyAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(mockUserAuthentication);

        assertNotNull(SecurityContextHolder.getContext());

        SecurityUtil.nullifyAuthentication();

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void retrieveAuthenticatedName() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertNull(SecurityUtil.retrieveAuthenticatedName());

        verifyZeroInteractions(mockUserAuthentication, mockAdminDomain, mockNonAdminDomain);

        reset(mockUserAuthentication, mockAdminDomain, mockNonAdminDomain);

        SecurityContextHolder.getContext().setAuthentication(mockUserAuthentication);

        when(mockUserAuthentication.getName()).thenReturn(dummyAuthenticationName);

        assertTrue(dummyAuthenticationName.equals(SecurityUtil.retrieveAuthenticatedName()));

        verify(mockUserAuthentication).getName();
        verifyZeroInteractions(mockAdminDomain, mockNonAdminDomain);
    }

    @Test
    public void retrieveAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertNull(SecurityUtil.retrieveAuthentication());

        reset(mockUserAuthentication, mockAdminDomain, mockNonAdminDomain);

        SecurityContextHolder.getContext().setAuthentication(mockUserAuthentication);

        assertSame(mockUserAuthentication, SecurityUtil.retrieveAuthentication());

        verifyZeroInteractions(mockUserAuthentication, mockAdminDomain, mockNonAdminDomain);
    }
}