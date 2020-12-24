package uk.ac.ebi.tsc.aap.client.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.ac.ebi.tsc.aap.client.model.User;
import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.tsc.aap.client.exception.InvalidJWTTokenException;
import uk.ac.ebi.tsc.aap.client.exception.TokenExpiredException;

/**
 * Extracts user authentication details from HTTP request
 *
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 18/07/2016.
 */
@Component //to support autowire
public class TokenAuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationService.class);
    private static final String TOKEN_HEADER_KEY = "Authorization";
    private static final String TOKEN_HEADER_VALUE_PREFIX = "Bearer";
    private final TokenHandler tokenHandler;

    @Autowired
    public TokenAuthenticationService(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    /**
     * Retrieve the {@link Authentication} object from a request header {@value #TOKEN_HEADER_KEY},
     * {@value #TOKEN_HEADER_VALUE_PREFIX} content value.
     * 
     * @param request Servlet request.
     * @return {@linkplain Authentication} object, or {@code null} if not found.
     * @throws InvalidJWTTokenException If problems processing token.
     * @throws TokenExpiredException If token has expired.
     */
    public Authentication getAuthentication(HttpServletRequest request)
                                            throws InvalidJWTTokenException, TokenExpiredException {
        LOGGER.trace("getAuthentication");
        final String token = extractToken(request);
        if (token == null)
            return null;
        User user = tokenHandler.parseUserFromToken(token);
        return new UserAuthentication(user);
    }

    public String extractToken(HttpServletRequest request) {
        try {
        final String header = request.getHeader(TOKEN_HEADER_KEY);
        if (header == null || !header.trim().startsWith(TOKEN_HEADER_VALUE_PREFIX.trim())) {
            LOGGER.trace("No {} header", TOKEN_HEADER_KEY);
            return null;
        }
        else if (!header.trim().startsWith(TOKEN_HEADER_VALUE_PREFIX.trim())) {
            LOGGER.trace("No {} prefix", TOKEN_HEADER_VALUE_PREFIX);
            return null;
        }
        final String token = header.substring(TOKEN_HEADER_VALUE_PREFIX.trim().length());
        if (StringUtils.isEmpty(token)) {
            LOGGER.trace("Missing jwt token");
            return null;
        }
        return token;
        }catch (Exception e){
            return null;
        }
    }
}
