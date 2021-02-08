package uk.ac.ebi.tsc.aap.client.security;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.InvalidJwtSignatureException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;
import uk.ac.ebi.tsc.aap.client.exception.InvalidJWTTokenException;
import uk.ac.ebi.tsc.aap.client.exception.TokenExpiredException;
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

   /**
    * Extract the {@link User} from the JWT token and assign their account status as non-locked.
    * 
    * @param token Token to parse.
    * @return {@link User} with details populated by token properties.
    * @throws InvalidJWTTokenException If any problems parsing token content.
    * @throws TokenExpiredException If token has expired.
    */
   public User parseUserFromToken(String token) throws InvalidJWTTokenException,
                                                       TokenExpiredException {
       try {
            Set<Domain> domainsSet = new HashSet<>();
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            String userReference = jwtClaims.getSubject();
            String nickname = jwtClaims.getStringClaimValue("nickname");
            String email = jwtClaims.getStringClaimValue("email");
            String fullName = jwtClaims.getStringClaimValue("name");
            List<String> domains = jwtClaims.getStringListClaimValue("domains");
            domains.forEach(name->domainsSet.add(new Domain(name,null,null)));
            final User user = new User(nickname, email, userReference, fullName, domainsSet);
            // We have a valid token by this point, so consider the account non-locked.
            user.setAccountNonLocked(true);
            return user;
       } catch (InvalidJwtSignatureException e) {
           LOGGER.error("JWT Error : "+e.getMessage());
           throw new InvalidJWTTokenException("Supplied Token is not valid for this server");
       } catch (MalformedClaimException e) {
           LOGGER.error("JWT Error : "+e.getMessage());
           throw new InvalidJWTTokenException(e.getMessage());
       } catch (InvalidJwtException e) {
           LOGGER.error("JWT Error : "+e.getMessage());
           if(e.getMessage().contains("Unable to process JOSE object"))
               throw new InvalidJWTTokenException("Supplied Token is not a valid JWT token");
           else if(e.getMessage().contains("JWT is no longer valid"))
               throw new TokenExpiredException("Supplied Token has been expired");
           else throw new InvalidJWTTokenException(e.getMessage());
       }
    }

}
