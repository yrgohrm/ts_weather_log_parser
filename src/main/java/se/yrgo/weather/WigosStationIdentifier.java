package se.yrgo.weather;

import java.util.Objects;

/**
 * Represents a WIGOS Station Identifier.
 * 
 * The format is:
 * WIGOSIdentifierSeries-IssuerOfIdentifier-IssueNumber-LocalIdentifier
 * 
 * Valid identifiers:
 *  WIGOSIdentifierSeries: 0
 *  IssuerOfIdentifier: 0-65534
 *  IssueNumber: 0-65534
 *  LocalIdentifier: 16 characters, no "-"
 * 
 * Example (SMHI Gunnarn): 0-20000-0-02126
 *
 */
public record WigosStationIdentifier(
        int wigosIdentifierSeries,
        int issuerOfIdentifier,
        int issueNumber,
        String localIdentifier) {

    public WigosStationIdentifier {
        if (wigosIdentifierSeries != 0) {
            throw new IllegalArgumentException("Series component of a WIGOS ID must be zero.");
        }

        if (issuerOfIdentifier < 0 || issueNumber < 0) {
            throw new IllegalArgumentException("Numeric components of a WIGOS ID cannot be negative.");
        }

        Objects.requireNonNull(localIdentifier, "Local identifier cannot be null.");
        if (localIdentifier.length() > 16
                || localIdentifier.isBlank()
                || localIdentifier.contains("-")) {
            throw new IllegalArgumentException("Local identifier cannot be blank or contain hyphens.");
        }
    }

    /**
     * Parses a string representation of a WIGOS Station Identifier.
     *
     * @param wigosId The identifier string, e.g., "0-20000-0-ABCDE".
     * @return A new WigosStationIdentifier instance.
     * @throws IllegalArgumentException if the string format is invalid.
     */
    public static WigosStationIdentifier parse(String wigosId) {
        Objects.requireNonNull(wigosId, "WIGOS ID string cannot be null.");
        String[] parts = wigosId.split("-");

        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "WIGOS ID string must have 4 parts separated by hyphens. Found: " + parts.length);
        }

        try {
            int series = Integer.parseInt(parts[0]);
            int issuer = Integer.parseInt(parts[1]);
            int issue = Integer.parseInt(parts[2]);
            String local = parts[3];

            return new WigosStationIdentifier(series, issuer, issue, local);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse numeric part of the WIGOS ID: " + wigosId, e);
        }
    }

    /**
     * Returns the canonical string representation of the identifier.
     *
     * @return The identifier formatted as a standard string.
     */
    @Override
    public String toString() {
        return String.format("%d-%d-%d-%s",
                wigosIdentifierSeries,
                issuerOfIdentifier,
                issueNumber,
                localIdentifier);
    }
}
