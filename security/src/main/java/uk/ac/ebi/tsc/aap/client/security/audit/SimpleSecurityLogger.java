package uk.ac.ebi.tsc.aap.client.security.audit;

import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple security audit logging mechanism (for consideration!).
 * <p>
 * Inspired by https://help.sap.com/doc/saphelp_nw73ehp1/7.31.19/en-US/4b/6013583840584ae10000000a42189c/content.htm?no_cache=true
 * 
 * @author geoff
 */
@Component
public class SimpleSecurityLogger implements SecurityLogger {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSecurityLogger.class);

    private static final String fmtActOnOther = PREFIX_SECURITY_AUDIT.concat("Action: {} by {} [{}] on {} [{}] : {}");
    private static final String fmtActOnSelf = PREFIX_SECURITY_AUDIT.concat("Action: {} by {} [{}] on self : {}");

    @Override
    public void logAction(@NotNull final Severity severity, @NotNull final Action action,
                          @NotNull final Actor actionBy, @NotNull final String actionById,
                          final Actor actionOn, final String actionOnId,
                          final Map<String, Object> additionalData) {

        // TODO : Too simplistic - needs more thought.
        // Default to assuming actionBy is acting (e.g. an update request) on themself.
        boolean actionOnSelf = true;
        if (actionOn != null && actionOnId != null) {
            // Determine who is the recipient of the action.
            if (actionBy.compareTo(actionOn) != 0 || !actionById.equals(actionOnId)) {
                // The recipient is not actionBy.
                actionOnSelf = false;
            }
        }

        // TODO : Code smell! Too much repetition.
        switch (severity) {
            case WARN :
                if (!actionOnSelf) {
                    logger.warn(fmtActOnOther,
                                action.toString(), actionBy.toString(), actionById,
                                actionOn.toString(), actionOnId, convertWithStream(additionalData));
                } else {
                    logger.warn(fmtActOnSelf,
                                action.toString(), actionBy.toString(), actionById,
                                convertWithStream(additionalData));
                }
                break;
            case INFO :
                if (!actionOnSelf) {
                    logger.info(fmtActOnOther,
                                action.toString(), actionBy.toString(), actionById,
                                actionOn.toString(), actionOnId, convertWithStream(additionalData));
                } else {
                    logger.info(fmtActOnSelf,
                                action.toString(), actionBy.toString(), actionById,
                                convertWithStream(additionalData));
                }
                break;
            case DEBUG :
                if (!actionOnSelf) {
                    logger.debug(fmtActOnOther,
                                 action.toString(), actionBy.toString(), actionById,
                                 actionOn.toString(), actionOnId, convertWithStream(additionalData));
                } else {
                    logger.debug(fmtActOnSelf,
                                 action.toString(), actionBy.toString(), actionById,
                                 convertWithStream(additionalData));
                }
                break;
            case TRACE :
                if (!actionOnSelf) {
                    logger.trace(fmtActOnOther,
                                 action.toString(), actionBy.toString(), actionById,
                                 actionOn.toString(), actionOnId, convertWithStream(additionalData));
                } else {
                    logger.trace(fmtActOnSelf,
                                 action.toString(), actionBy.toString(), actionById,
                                 convertWithStream(additionalData));
                }
                break;
            default :
                logger.error(PREFIX_SECURITY_AUDIT.concat("Error - Can't write Security Audit!!"));
        }
    }

    @Override
    public void logActionOnSelf(final Severity severity, final Action action, final Actor actionBy,
                                final String actionById, final Map<String, Object> additionalData) {
        this.logAction(severity, action, actionBy, actionById, null, null, additionalData);
    }

    // https://www.baeldung.com/java-map-to-string-conversion
    private String convertWithStream(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        String mapAsString = map.keySet().stream()
          .map(key -> key + "=" + map.get(key))
          .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }
}