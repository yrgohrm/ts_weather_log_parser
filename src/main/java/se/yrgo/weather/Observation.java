package se.yrgo.weather;

import java.time.*;
import java.util.*;

/**
 * A meteorological observation at a specific station at a specific time.
 * 
 * If any of the observations are faulty or outside range NaN will be used to
 * denote this.
 * 
 */
public record Observation(
        WigosStationIdentifier stationId,
        Instant timestamp,
        double temperature,
        double humidity) {
    public Observation {
        Objects.requireNonNull(stationId);
        Objects.requireNonNull(timestamp);

        if (temperature < -100 || temperature > 70) {
            temperature = Double.NaN;
        }

        if (humidity < 0 || humidity > 100) {
            temperature = Double.NaN;
        }
    }

    /**
     * Returns true if all readings in this observation is invalid/faulty.
     * 
     * @return true if all readings are invalid, false otherwise.
     */
    public boolean isInvalid() {
        return Double.isNaN(temperature) && Double.isNaN(humidity);
    }

    /**
     * Returns true if some of the readings, but not all, in this observation is invalid/faulty.
     * 
     * @return true if some, but not all, readings are invalid, false otherwise.
     */
    public boolean isPartial() {
        return !isValid() && !isInvalid();
    }

    
    /**
     * Returns true if all readings in this observation is valid.
     * 
     * @return true if all readings are valid, false otherwise.
     */
    public boolean isValid() {
        return !Double.isNaN(temperature) && !Double.isNaN(humidity);
    }
}
