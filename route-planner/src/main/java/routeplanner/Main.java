package routeplanner;

import java.util.List;
import java.util.Optional;


public class Main {

    public static void main(String[] args) {
        Graph graph = buildSampleMap();
        RouteFinder finder = new RouteFinder(graph);

        System.out.println("Городов: " + graph.size() + ", регионов: " + finder.countRegions());
        System.out.println();

        if (args.length == 2) {
            printRoutes(finder, args[0], args[1]);
            return;
        }
        printRoutes(finder, "Москва", "Воронеж");
        printRoutes(finder, "Москва", "Курск");
        printRoutes(finder, "Москва", "Хабаровск");
    }

    private static void printRoutes(RouteFinder finder, String from, String to) {
        System.out.println("=== " + from + " -> " + to + " ===");
        try {
            Optional<Route> shortest = finder.shortestPath(from, to);
            if (shortest.isEmpty()) {
                System.out.println("Маршрута не существует");
            } else {
                System.out.println("Кратчайший: " + String.join(" -> ", shortest.get().cities())
                        + " (" + shortest.get().distance() + " км)");
                List<String> fewest = finder.fewestStops(from, to).orElseThrow();
                System.out.println("Меньше всего дорог: " + String.join(" -> ", fewest)
                        + " (" + (fewest.size() - 1) + " дороги)");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        System.out.println();
    }

    private static Graph buildSampleMap() {
        Graph g = new Graph();
        g.addRoad("Москва", "Тула", 180);
        g.addRoad("Москва", "Рязань", 190);
        g.addRoad("Москва", "Калуга", 160);
        g.addRoad("Калуга", "Орёл", 190);
        g.addRoad("Тула", "Орёл", 170);
        g.addRoad("Тула", "Липецк", 300);
        g.addRoad("Рязань", "Липецк", 280);
        g.addRoad("Орёл", "Курск", 150);
        g.addRoad("Липецк", "Воронеж", 110);
        g.addRoad("Курск", "Воронеж", 220);

        g.addRoad("Хабаровск", "Владивосток", 750);
        return g;
    }
}
