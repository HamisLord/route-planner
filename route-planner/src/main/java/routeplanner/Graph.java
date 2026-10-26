package routeplanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {

    private final Map<String, List<Edge>> adjacency = new HashMap<>();

    public void addCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Название города не может быть пустым");
        }
        adjacency.computeIfAbsent(city, k -> new ArrayList<>());
    }

    public void addRoad(String a, String b, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Длина дороги не может быть отрицательной: " + length);
        }
        addCity(a);
        addCity(b);
        adjacency.get(a).add(new Edge(b, length));
        adjacency.get(b).add(new Edge(a, length));
    }

    public boolean hasCity(String city) {
        return adjacency.containsKey(city);
    }

    public int size() {
        return adjacency.size();
    }

    public Set<String> cities() {
        return adjacency.keySet();
    }

    public List<Edge> neighbors(String city) {
        requireCity(city);
        return adjacency.get(city);
    }

    void requireCity(String city) {
        if (!adjacency.containsKey(city)) {
            throw new IllegalArgumentException("Неизвестный город: " + city);
        }
    }
}
