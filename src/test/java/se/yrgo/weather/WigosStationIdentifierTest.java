package se.yrgo.weather;

import static org.assertj.core.api.Assertions.*;

import java.util.random.*;
import java.util.stream.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

class WigosStationIdentifierTest {

    // Just a silly little test to get things started
    @Test
    void testToString() {
        WigosStationIdentifier actual = new WigosStationIdentifier(0, 20000, 0, "02531");
        String expected = "0-20000-0-02531";

        assertThat(actual).hasToString(expected);
    }

    // Another silly test that uses a parameritized test with a method source
    @ParameterizedTest
    @MethodSource("provideStationIds")
    void testCanParseToString(String idString) {
        WigosStationIdentifier identifier = WigosStationIdentifier.parse(idString);
        assertThat(identifier).hasToString(idString);
    }

    private static Stream<Arguments> provideStationIds() {
        // Uses a seed so the "randomness" will always be the same
        RandomGenerator random = RandomGeneratorFactory.getDefault().create(123456);

        // This could of course simply been a list...
        return Stream.generate(() -> {
            int series = 0;
            int issuerOfIdentifier = random.nextInt(0, 65535);
            int issueNumber = random.nextInt(0, 65535);
            int localIdentifier = random.nextInt(0, Integer.MAX_VALUE);
            
            var id = series + "-" + issuerOfIdentifier + "-" + issueNumber + "-" + localIdentifier;

            return Arguments.of(id);
        }).limit(10);
    }
}
