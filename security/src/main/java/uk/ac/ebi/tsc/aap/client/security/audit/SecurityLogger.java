package uk.ac.ebi.tsc.aap.client.security.audit;

import java.util.Map;

/**
 * Security audit logging mechanism.
 * 
 * @author geoff
 */
public interface SecurityLogger {

    /**
     * Prefix for all security auditing messages.
     */
    public static final String PREFIX_SECURITY_AUDIT = "[SECURITY_AUDIT] ";

    /**
     * Log the security event.
     * 
     * @param severity Action severity.
     * @param action Action, e.g. {@link Action#DELETE}.
     * @param actionBy Person/Mechanism performing the action.
     * @param actionById Identifier (e.g. {@code usr-<UUID>}, or class name if application)
     *                   of {@code actionBy} (or class name if application).
     * @param actionOn Person/Mechanism receiving the action (or {@code null} if on self)
     * @param actionOnId Identifier (e.g. {@code usr-<UUID>}, or class name if application)
     *                   of {@code actionOn}, e.g. {@code usr-<UUID>}, or {@code null} if on self.
     * @param additionalData Further information.
     */
    void logAction(Severity severity, Action action, Actor actionBy, String actionById,
                   Actor actionOn, String actionOnId, Map<String, Object> additionalData);

    /**
     * Log the security event if an action is on themself.
     * 
     * @param severity Action severity.
     * @param action Action, e.g. {@link Action#DELETE}.
     * @param actionBy Person/Mechanism performing the action.
     * @param actionById Identifier (e.g. {@code usr-<UUID>}, or class name if application)
     *                   of {@code actionBy} (or class name if application).
     * @param additionalData Further information.
     */
    void logActionOnSelf(Severity severity, Action action, Actor actionBy, String actionById,
                         Map<String, Object> additionalData);
}