package se.yrgo.weather;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public final class WeatherLogParser {

    public record ParserError(
            int lineNumber,
            String error,
            String line,
            WigosStationIdentifier stationIdentifier) {
    }

    public record ParsingResult(
            List<Observation> observations,
            List<ParserError> errors) {
        public ParsingResult {
            // Make sure to keep our own immutable copies
            observations = List.copyOf(observations);
            errors = List.copyOf(errors);
        }
    }

    private WeatherLogParser() {

    }

    /**
     * Parses a list of log file lines into structured WeatherReading objects.
     * It handles malformed lines gracefully by collecting error messages.
     *
     * The format is:
     * ISO_TIMESTAMP,TEMPERATURE_CELSIUS,RELATIVE_HUMIDITY_PERCENT
     * 
     * Example:
     * 2025-08-07T10:00:00Z,22.5,58.3
     * 2025-08-07T11:00:00Z,NaN,60.1
     * 
     * @param logLines A list of strings, where each string is a line from a log
     *                 file.
     * @return A ParsingResult object containing both the successful readings and
     *         error messages.
     */
    public static ParsingResult parse(WigosStationIdentifier stationId, Stream<String> logLines) {
        Objects.requireNonNull(stationId, "stationId can't be null");
        Objects.requireNonNull(logLines, "logLines can't be null");

        List<Observation> successfulReadings = new ArrayList<>();
        List<ParserError> errorMessages = new ArrayList<>();
        AtomicInteger lineCount = new AtomicInteger(0);

        logLines.forEach(line -> {
            int currentLineNumber = lineCount.addAndGet(1);

            try {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    errorMessages.add(new ParserError(currentLineNumber,
                            "Line does not contain exactly three parts.",
                            line, stationId));
                    return;
                }

                Instant timestamp = Instant.parse(parts[0].trim());
                double temperature = Double.parseDouble(parts[1].trim());
                double humidity = Double.parseDouble(parts[2].trim());

                successfulReadings.add(new Observation(stationId, timestamp, temperature, humidity));

            } catch (DateTimeParseException | IllegalArgumentException ex) {
                errorMessages.add(new ParserError(currentLineNumber, ex.getMessage(), line, stationId));
            }
        });

        return new ParsingResult(successfulReadings, errorMessages);
    }
}