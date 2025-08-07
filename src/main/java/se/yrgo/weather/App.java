package se.yrgo.weather;

import java.io.*;
import java.nio.file.*;
import java.util.function.*;
import java.util.stream.*;

import se.yrgo.weather.WeatherLogParser.*;

/**
 * An app that prints some statistics from a weather log file.
 * 
 */
public class App {
    public static void main(String[] args) throws IOException {
        Path stationData = Path.of("0-20000-0-02513.csv");

        String station = stationData.getFileName().toString().replace(".csv", "");
        WigosStationIdentifier identifier = WigosStationIdentifier.parse(station);

        try (Stream<String> lines = Files.lines(stationData)) {
            var result = WeatherLogParser.parse(identifier, lines);

            long valid = count(result, Observation::isValid);
            long partial = count(result, Observation::isPartial);
            long invalid = count(result, Observation::isInvalid);
            long errors = result.errors().size();

            double maxTemp = findMax(result, Observation::temperature);
            double maxHum = findMax(result, Observation::humidity);

            System.out.printf("""
                Valid:   %d
                Partial: %d
                Invalid: %d
                Errors:  %d

                Max Temp:     %.2f
                Max Humidity: %.2f
            """, valid, partial, invalid, errors, maxTemp, maxHum);
        }
        catch (IOException ex) {
            System.err.println("An error occurred: " + ex.getMessage());
        }
    }

    private static double findMax(ParsingResult result, ToDoubleFunction<Observation> sup) {
        return result.observations().stream().reduce(Double.NEGATIVE_INFINITY, 
            (acc, val) -> Double.isNaN(sup.applyAsDouble(val)) ? acc : Math.max(acc, sup.applyAsDouble(val)), 
            (acc, val) -> Double.isNaN(val) ? acc : Math.max(acc, val));
    }

    private static long count(ParsingResult result, Predicate<Observation> pred) {
        return result.observations().stream().filter(pred).count();
    }
}