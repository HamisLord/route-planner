package routeplanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GraphTest {

    @Test
    void roadIsTwoWay() {
        Graph g = new Graph();
        g.addRoad("A", "B", 5);

        assertEquals(new Edge("B", 5), g.neighbors("A").get(0));
        assertEquals(new Edge("A", 5), g.neighbors("B").get(0));
    }

    @Test
    void addRoadCreatesMissingCities() {
        Graph g = new Graph();
        g.addRoad("A", "B", 1);

        assertTrue(g.hasCity("A"));
        assertTrue(g.hasCity("B"));
        assertEquals(2, g.size());
    }

    @Test
    void addingSameCityTwiceDoesNothing() {
        Graph g = new Graph();
        g.addCity("A");
        g.addCity("A");

        assertEquals(1, g.size());
    }

    @Test
    void isolatedCityHasNoNeighbors() {
        Graph g = new Graph();
        g.addCity("A");

        assertTrue(g.neighbors("A").isEmpty());
    }

    @Test
    void zeroLengthRoadIsAllowed() {
        Graph g = new Graph();
        g.addRoad("A", "B", 0);

        assertFalse(g.neighbors("A").isEmpty());
    }

    @Test
    void negativeLengthIsRejected() {
        Graph g = new Graph();

        assertThrows(IllegalArgumentException.class, () -> g.addRoad("A", "B", -1));
        assertEquals(0, g.size());
    }

    @Test
    void blankOrNullCityNameIsRejected() {
        Graph g = new Graph();

        assertThrows(IllegalArgumentException.class, () -> g.addCity(" "));
        assertThrows(IllegalArgumentException.class, () -> g.addCity(null));
    }

    @Test
    void neighborsOfUnknownCityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Graph().neighbors("X"));
    }
}
