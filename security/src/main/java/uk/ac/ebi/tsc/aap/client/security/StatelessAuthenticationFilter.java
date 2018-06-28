package uk.ac.ebi.tsc.aap.client.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import uk.ac.ebi.tsc.aap.client.exception.InvalidJWTTokenException;
import uk.ac.ebi.tsc.aap.client.exception.TokenExpiredException;
import uk.ac.ebi.tsc.aap.client.exception.TokenNotSuppliedException;
import uk.ac.ebi.tsc.aap.client.model.ErrorResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Rejects HTTP requests unless they contain a valid token
 *
 * @author Amelie Cornelis  <ameliec@ebi.ac.uk>
 * @since 18/07/2016.
 */
public class StatelessAuthenticationFilter extends GenericFilterBean {

    public static final Logger LOGGER = LoggerFactory.getLogger
            (StatelessAuthenticationFilter.class);

    private final TokenAuthenticationService authenticationService;

    public StatelessAuthenticationFilter(
            TokenAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        LOGGER.trace("AuthenticationService: {}",
                authenticationService);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
        Authentication authentication = authenticationService.getAuthentication(httpRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
        }catch (TokenNotSuppliedException | InvalidJWTTokenException | TokenExpiredException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e.getCode());
            errorResponse.setMessage(e.getMessage());
            errorResponse.setException(e.getClass().getCanonicalName());
            httpRequest.setAttribute("ERROR_RESPONSE",errorResponse);
            filterChain.doFilter(request, response);
            SecurityContextHolder.getContext().setAuthentication(null);
        }

    }
}
