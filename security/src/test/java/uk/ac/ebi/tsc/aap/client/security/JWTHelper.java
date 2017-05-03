package uk.ac.ebi.tsc.aap.client.security;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

/**
 * @author Uppendra Kumbham  <ukumbham@ebi.ac.uk>
 * @since 06/10/2016.
 */
public class JWTHelper {
    static final long ttlMins = 5;
    static final String username = "user1";
    static final String issuer = "https://tsi.ebi.ac.uk";


    public static String token() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        PrivateKey privateKey = keyGen.generateKeyPair().getPrivate();
        return token(privateKey, AlgorithmIdentifiers.
                ECDSA_USING_P256_CURVE_AND_SHA256);
    }

    public static String token(PrivateKey privateKey, String alg) {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setExpirationTimeMinutesInTheFuture(ttlMins);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setSubject(username);

        return build(claims, privateKey, alg);
    }

    public static String build(JwtClaims claims, PrivateKey privateKey, String alg) {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(privateKey);
        jws.setAlgorithmHeaderValue(alg);

        String token;
        try {
            token = jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
        return token;
    }
}
