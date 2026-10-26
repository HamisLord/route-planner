package routeplanner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class RouteFinder {

    private record QueueItem(String city, long distance) {
    }

    private final Graph graph;

    public RouteFinder(Graph graph) {
        this.graph = graph;
    }


    public Optional<Route> shortestPath(String from, String to) {
        graph.requireCity(from);
        graph.requireCity(to);

        Map<String, Long> dist = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<QueueItem> queue = new PriorityQueue<>(Comparator.comparingLong(QueueItem::distance));

        dist.put(from, 0L);
        queue.add(new QueueItem(from, 0L));

        while (!queue.isEmpty()) {
            QueueItem current = queue.poll();
            if (current.distance() > dist.get(current.city())) {
                continue;
            }
            if (current.city().equals(to)) {
                return Optional.of(new Route(current.distance(), restorePath(previous, from, to)));
            }
            for (Edge edge : graph.neighbors(current.city())) {
                long candidate = current.distance() + edge.weight();
                Long known = dist.get(edge.to());
                if (known == null || candidate < known) {
                    dist.put(edge.to(), candidate);
                    previous.put(edge.to(), current.city());
                    queue.add(new QueueItem(edge.to(), candidate));
                }
            }
        }
        return Optional.empty();
    }


    public Optional<List<String>> fewestStops(String from, String to) {
        graph.requireCity(from);
        graph.requireCity(to);

        Set<String> visited = new HashSet<>();
        Map<String, String> previous = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();

        visited.add(from);
        queue.add(from);

        while (!queue.isEmpty()) {
            String city = queue.poll();
            if (city.equals(to)) {
                return Optional.of(restorePath(previous, from, to));
            }
            for (Edge edge : graph.neighbors(city)) {
                if (visited.add(edge.to())) {
                    previous.put(edge.to(), city);
                    queue.add(edge.to());
                }
            }
        }
        return Optional.empty();
    }


    public int countRegions() {
        Set<String> visited = new HashSet<>();
        int regions = 0;
        for (String start : graph.cities()) {
            if (visited.contains(start)) {
                continue;
            }
            regions++;
            Queue<String> queue = new ArrayDeque<>();
            visited.add(start);
            queue.add(start);
            while (!queue.isEmpty()) {
                for (Edge edge : graph.neighbors(queue.poll())) {
                    if (visited.add(edge.to())) {
                        queue.add(edge.to());
                    }
                }
            }
        }
        return regions;
    }

    private List<String> restorePath(Map<String, String> previous, String from, String to) {
        List<String> path = new ArrayList<>();
        for (String city = to; city != null; city = previous.get(city)) {
            path.add(city);
        }
        Collections.reverse(path);
        return path;
    }
}
