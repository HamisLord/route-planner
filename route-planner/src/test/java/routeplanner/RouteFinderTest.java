package routeplanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteFinderTest {

    private Graph graph;
    private RouteFinder finder;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        graph.addRoad("A", "B", 4);
        graph.addRoad("A", "C", 1);
        graph.addRoad("C", "B", 2);
        graph.addRoad("B", "D", 1);
        graph.addRoad("C", "D", 5);
        graph.addRoad("E", "F", 3);
        graph.addCity("G");
        finder = new RouteFinder(graph);
    }


    @Test
    void shortestPathPicksLightestRoute() {
        Route route = finder.shortestPath("A", "D").orElseThrow();

        assertEquals(4, route.distance());
        assertEquals(List.of("A", "C", "B", "D"), route.cities());
    }

    @Test
    void shortestPathIsSymmetric() {
        Route forward = finder.shortestPath("A", "D").orElseThrow();
        Route backward = finder.shortestPath("D", "A").orElseThrow();

        assertEquals(forward.distance(), backward.distance());
    }

    @Test
    void shortestPathToNeighborViaDetour() {
        assertEquals(3, finder.shortestPath("A", "B").orElseThrow().distance());
    }


    @Test
    void pathToSelfIsZero() {
        Route route = finder.shortestPath("A", "A").orElseThrow();

        assertEquals(0, route.distance());
        assertEquals(List.of("A"), route.cities());
    }

    @Test
    void unreachableCityGivesEmptyResult() {
        assertTrue(finder.shortestPath("A", "F").isEmpty());
        assertTrue(finder.shortestPath("A", "G").isEmpty());
        assertTrue(finder.shortestPath("G", "A").isEmpty());
    }

    @Test
    void unknownCityThrows() {
        assertThrows(IllegalArgumentException.class, () -> finder.shortestPath("A", "X"));
        assertThrows(IllegalArgumentException.class, () -> finder.shortestPath("X", "A"));
    }

    @Test
    void emptyGraphThrowsForAnyCity() {
        RouteFinder empty = new RouteFinder(new Graph());

        assertThrows(IllegalArgumentException.class, () -> empty.shortestPath("A", "B"));
    }

    @Test
    void parallelRoadsUseTheShorterOne() {
        Graph g = new Graph();
        g.addRoad("A", "B", 10);
        g.addRoad("A", "B", 3);

        assertEquals(3, new RouteFinder(g).shortestPath("A", "B").orElseThrow().distance());
    }

    @Test
    void zeroLengthRoadsAreHandled() {
        Graph g = new Graph();
        g.addRoad("A", "B", 0);
        g.addRoad("B", "C", 0);

        assertEquals(0, new RouteFinder(g).shortestPath("A", "C").orElseThrow().distance());
    }

    @Test
    void longChainDoesNotOverflowStack() {
        Graph g = new Graph();
        int n = 100_000;
        for (int i = 0; i < n; i++) {
            g.addRoad("c" + i, "c" + (i + 1), 1);
        }

        Route route = new RouteFinder(g).shortestPath("c0", "c" + n).orElseThrow();

        assertEquals(n, route.distance());
        assertEquals(n + 1, route.cities().size());
    }

    @Test
    void distancesDoNotOverflowInt() {
        Graph g = new Graph();
        g.addRoad("A", "B", Integer.MAX_VALUE);
        g.addRoad("B", "C", Integer.MAX_VALUE);

        long expected = 2L * Integer.MAX_VALUE;
        assertEquals(expected, new RouteFinder(g).shortestPath("A", "C").orElseThrow().distance());
    }


    @Test
    void fewestStopsFindsMinimumNumberOfRoads() {
        List<String> path = finder.fewestStops("A", "D").orElseThrow();

        assertEquals(3, path.size());
        assertEquals("A", path.get(0));
        assertEquals("D", path.get(2));
    }

    @Test
    void fewestStopsDiffersFromShortestPath() {
        Graph g = new Graph();
        g.addRoad("A", "D", 100);
        g.addRoad("A", "B", 1);
        g.addRoad("B", "C", 1);
        g.addRoad("C", "D", 1);
        RouteFinder f = new RouteFinder(g);

        assertEquals(List.of("A", "D"), f.fewestStops("A", "D").orElseThrow());
        assertEquals(List.of("A", "B", "C", "D"), f.shortestPath("A", "D").orElseThrow().cities());
        assertEquals(3, f.shortestPath("A", "D").orElseThrow().distance());
    }

    @Test
    void fewestStopsToSelfAndUnreachable() {
        assertEquals(List.of("A"), finder.fewestStops("A", "A").orElseThrow());
        assertEquals(Optional.empty(), finder.fewestStops("A", "E"));
    }

    @Test
    void fewestStopsUnknownCityThrows() {
        assertThrows(IllegalArgumentException.class, () -> finder.fewestStops("A", "X"));
    }


    @Test
    void countsRegions() {
        assertEquals(3, finder.countRegions());
    }

    @Test
    void emptyGraphHasNoRegions() {
        assertEquals(0, new RouteFinder(new Graph()).countRegions());
    }

    @Test
    void singleCityIsOneRegion() {
        Graph g = new Graph();
        g.addCity("A");

        assertEquals(1, new RouteFinder(g).countRegions());
    }

    @Test
    void connectingRegionsReducesTheirNumber() {
        graph.addRoad("D", "E", 7);

        assertEquals(2, finder.countRegions());
        assertTrue(finder.shortestPath("A", "F").isPresent());
    }
}
