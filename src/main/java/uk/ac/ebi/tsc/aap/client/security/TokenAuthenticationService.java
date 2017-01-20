package uk.ac.ebi.tsc.aap.client.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import uk.ac.ebi.tsc.aap.client.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Extracts user authentication details from HTTP request
 *
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 18/07/2016.
 */
public class TokenAuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationService.class);
    private static final String TOKEN_HEADER_KEY = "Authorization";
    private static final String TOKEN_HEADER_VALUE_PREFIX = "Bearer";

    private final TokenHandler tokenHandler;

    @Autowired
    public TokenAuthenticationService(TokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
    }

    Authentication getAuthentication(HttpServletRequest request){
        LOGGER.debug("In the[TokenAuthenticationService] - getAuthentication");
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
            User user = tokenHandler.parseUserFromToken(token);
            return new UserAuthentication(user);
        }
        catch (IllegalArgumentException e){
            LOGGER.error(e.getMessage());
            LOGGER.trace("", e);
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.trace("", e);
            return null;
        }

    }
}
