package uk.ac.ebi.tsc.aap.client.security;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import uk.ac.ebi.tsc.aap.client.model.User;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Verify token validity and extract fields
 *
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 18/07/2016.
 */
public class TokenHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TokenHandler.class);

    private JwtConsumer jwtConsumer;
    @Value("${jwt.certificate}")
    private String certificatePath;

    @PostConstruct
    public void initPropertyDependentFields() throws Exception {
        LOGGER.trace("initPropertyDependentFields- certificatePath***** {}", certificatePath);
        InputStream inputStream = new DefaultResourceLoader().getResource(certificatePath).getInputStream();
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        final X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
        final PublicKey verifyingKey = certificate.getPublicKey();
        setJwtConsumer(verifyingKey);
    }

    void setJwtConsumer(PublicKey verifyingKey) {
        jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setVerificationKey(verifyingKey)
                .setRelaxVerificationKeyValidation()
                .build();
    }

    User parseUserFromToken(String token) {
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            String username = jwtClaims.getSubject();
            String nickname = jwtClaims.getStringClaimValue("nickname");
            String email = jwtClaims.getStringClaimValue("email");
            return new User(nickname, email, username);

        } catch (InvalidJwtException | MalformedClaimException e) {
            LOGGER.debug("cannot parse token", e);
            throw new RuntimeException("Cannot parse token", e);
        } catch (Exception e) {
            LOGGER.info("Exception: " + e.getMessage());
            throw new RuntimeException("Cannot parse token", e);
        }
    }

}
