package uk.ac.ebi.tsc.aap.client.security;

import org.mockito.Mockito;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.tsc.aap.client.model.User;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 21/07/2016.
 */
public class TokenHandlerTest {

    private TokenHandler subject;
    static final private String username = "user1";
    private PrivateKey signingKey;
    private PublicKey verifyingKey;
    private User mockUser;

    @Before
    public void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        KeyPair testKeyPair = keyGen.generateKeyPair();
        signingKey = testKeyPair.getPrivate();
        verifyingKey = testKeyPair.getPublic();
        mockUser = Mockito.mock(User.class);
        subject = new TokenHandler();
        subject.setJwtConsumer(verifyingKey);
        when(mockUser.getUsername()).thenReturn(username);
    }

    @Test public void
    extracts_user_full_name_from_token() throws Exception {
        JwtClaims claims = minClaims();
        claims.setClaim("name", "Alice Wonderland");
        String validToken = JWTHelper.build(claims, signingKey, AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);

        User user = subject.parseUserFromToken(validToken);

        assertThat(user.getFullName()).isEqualTo("Alice Wonderland");
    }

    @Test public void
    extracted_users_have_accounts_non_locked() throws Exception {
        JwtClaims claims = minClaims();
        claims.setClaim("name", "Alice Wonderland");
        String validToken = JWTHelper.build(claims, signingKey, AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);

        User user = subject.parseUserFromToken(validToken);

        assertTrue("A token-authenticated user should have an unlocked account setting",
                    user.isAccountNonLocked());
    }

    @Test public void
    detects_tampered_token() throws Exception {
        String validToken = JWTHelper.token();
        String encodedPayload = validToken.split("\\.")[1];
        String decodedPayload = new String(Base64.getDecoder().decode(encodedPayload));
        String tamperedPayload = decodedPayload.replace(username, "bob");
        String tamperedEncodedPayload = new String(Base64.getEncoder().encode(tamperedPayload.getBytes()));
        String tamperedToken = validToken.replace(encodedPayload, tamperedEncodedPayload);

        Throwable thrown = catchThrowable(() -> subject.parseUserFromToken(tamperedToken));
        assertThat(thrown);
    }

    @Test public void
    rejects_token_signed_with_different_private_key() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        PrivateKey privateKey = keyGen.generateKeyPair().getPrivate();
        String tokenFromUntrusted = JWTHelper.token(privateKey, AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);

        Throwable thrown = catchThrowable(() -> subject.parseUserFromToken(tokenFromUntrusted));
        assertThat(thrown);
    }

    @Test public void
    rejects_token_signed_with_different_algorithm() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        PrivateKey privateKey = keyGen.generateKeyPair().getPrivate();
        String tokenFromUntrusted = JWTHelper.token(privateKey, AlgorithmIdentifiers.RSA_USING_SHA256);

        Throwable thrown = catchThrowable(() -> subject.parseUserFromToken(tokenFromUntrusted));

        assertThat(thrown);
    }

    @Test public void
    rejects_expired_tokens() {
        String expiredToken = expiredToken();
        Throwable thrown = catchThrowable(() -> subject.parseUserFromToken(expiredToken));

        assertThat(thrown);
    }

    private String expiredToken() {
        // past must be earlier than skew allowed by TokenHandler jwtConsumer
        long past = - (60 * 1000);
        JwtClaims claims = minClaims(past);
        return JWTHelper.build(claims, signingKey, AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);
    }

    private JwtClaims minClaims() {
        return minClaims(2 * 1000);
    }

    private JwtClaims minClaims(long expiresIn) {
        long now = new Date().getTime();
        NumericDate expiry = NumericDate.fromMilliseconds(now + expiresIn);
        JwtClaims claims = new JwtClaims();
        claims.setSubject(username);
        claims.setExpirationTime( expiry);
        return claims;
    }


}