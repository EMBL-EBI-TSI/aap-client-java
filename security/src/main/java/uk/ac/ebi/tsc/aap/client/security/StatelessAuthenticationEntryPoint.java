package uk.ac.ebi.tsc.aap.client.security;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Always return Unauthorized (401).
 *
 * @author Uppendra Kumbham  <ukumbham@ebi.ac.uk>
 * @since 29/09/2016.
 */
@Component
public class StatelessAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger
            (StatelessAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
        LOGGER.info("Error MSG : "+request.getAttribute("ERROR_MSG"));
        Object error_msg = request.getAttribute("ERROR_MSG");
        String message = "Unauthorized";
        if(error_msg == null)
            message = "Token not supplied";
        else if(error_msg.toString().indexOf("Unable to process JOSE object") > -1)
            message = "Token is not a valid JWT token";
        else if(error_msg.toString().indexOf("JWS signature is invalid") > -1)
            message = "Token is not valid for this server";
        else if(error_msg.toString().indexOf("JWT is no longer valid") > -1)
            message = "Token has been expired";
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

}
