package uk.ac.ebi.tsc.aap.client.security;

import org.mockito.Mockito;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
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
    private UserDetails mockUser;

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
        long now = new Date().getTime();
        // past must be earlier than skew allowed by TokenHandler jwtConsumer
        NumericDate past = NumericDate.fromMilliseconds(now - (60 * 1000));

        JwtClaims claims = new JwtClaims();
        claims.setSubject(username);
        claims.setExpirationTime(past);
        return JWTHelper.build(claims, signingKey, AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);
    }


}