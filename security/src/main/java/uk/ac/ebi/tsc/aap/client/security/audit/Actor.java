package uk.ac.ebi.tsc.aap.client.security.audit;

/**
 * Security auditing options for recipient of the {@link Action}.
 * 
 * @author geoff
 */
public enum Actor {
    TOKEN("aap-client-java Token"),
    USER("aap-users-api repo.User @Entity");

    // Textual description.
    private final String description;

    // Initialising constructor.
    private Actor(final String description) {
        this.description = description;
    }

    /**
     * Get the optional textual description of the enum value.
     * 
     * @return Textual description (or {@code null} if not assigned).
     */
    public String getDescription() {
        return this.description;
    }
}