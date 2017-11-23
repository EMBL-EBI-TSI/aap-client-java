package uk.ac.ebi.tsc.aap.client.security;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;
import uk.ac.ebi.tsc.aap.client.model.Domain;
import uk.ac.ebi.tsc.aap.client.model.User;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Verify token validity and extract fields
 *
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 18/07/2016.
 */
@Component //to support autowire in the API
public class TokenHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandler.class);

    public JwtConsumer jwtConsumer;
    @Value("${jwt.certificate}")
    private String certificatePath;

    @PostConstruct
    public void initPropertyDependentFields() throws Exception {
        LOGGER.trace("initPropertyDependentFields- certificatePath***** {}", certificatePath);
        setJwtConsumer(certificatePath);
    }

    void setJwtConsumer(String path) throws Exception {
        InputStream inputStream = new DefaultResourceLoader().getResource(path).getInputStream();
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

   public User parseUserFromToken(String token) {
        try {
            Set<Domain> domainsSet = new HashSet<>();
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            String userReference = jwtClaims.getSubject();
            String nickname = jwtClaims.getStringClaimValue("nickname");
            String email = jwtClaims.getStringClaimValue("email");
            List<String> domains = jwtClaims.getStringListClaimValue("domains");
            domains.forEach(name->domainsSet.add(new Domain(name,null,null)));
            return new User(nickname, email, userReference,domainsSet);
        } catch (InvalidJwtException | MalformedClaimException e) {
            throw new RuntimeException("Cannot parse token: "+e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse token: "+e.getMessage(), e);
        }
    }

}
