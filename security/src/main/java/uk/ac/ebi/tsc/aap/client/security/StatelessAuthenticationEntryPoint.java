package uk.ac.ebi.tsc.aap.client.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.tsc.aap.client.model.ErrorResponse;
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
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        // We should just send a 401 Unauthorized with proper response because there is no 'login page' to redirect to
        if(request.getAttribute("ERROR_RESPONSE")!=null){
            ErrorResponse errorResponse = (ErrorResponse)request.getAttribute("ERROR_RESPONSE");
            errorResponse.setPath(request.getRequestURI());
            errorResponse.setStatus(HttpStatus.UNAUTHORIZED.name());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(convertObjectToJson(errorResponse));
        }
        else response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) { return null; }
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
