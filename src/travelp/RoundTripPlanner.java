package travelp;

import java.util.Arrays;
import java.util.List;

public class RoundTripPlanner {
    private int startCityIndex;
    private int endCityIndex;

    private WeightedGraph<String> flightNetwork;

    private String[] cities;
    private int[][] connections;

    private List<String> forwardRoute;
    private List<String> returnRoute;

    // Add fare fields
    private double forwardTripFare;
    private double returnTripFare;

    public RoundTripPlanner(String[] cities, int[][] connections, int startCityIndex, int endCityIndex) {
        this.cities = cities;
        this.connections = connections;
        this.startCityIndex = startCityIndex;
        this.endCityIndex = endCityIndex;
        generateRoundTrip();
        calculateFares();  // Calculate fares after generating routes
    }

    public void generateRoundTrip() {
        flightNetwork = new WeightedGraph<>(cities, connections);

        // Forward trip
        WeightedGraph.ShortestPathTree shortestPath1 = flightNetwork.getShortestPath(startCityIndex);
        forwardRoute = shortestPath1.getPath(endCityIndex);

        // Block used edges for return trip
        int currentCityInReturnPath = endCityIndex;
        int parentCityInReturnPath;
        while (currentCityInReturnPath != startCityIndex) {
            parentCityInReturnPath = shortestPath1.getParent(currentCityInReturnPath);
            for (Edge ed : flightNetwork.neighbors.get(currentCityInReturnPath)) {
                if (ed.v == parentCityInReturnPath) {
                    ((WeightedEdge) ed).weight = Integer.MAX_VALUE;
                }
            }
            for (Edge ed : flightNetwork.neighbors.get(parentCityInReturnPath)) {
                ((WeightedEdge) ed).weight = Integer.MAX_VALUE;
            }
            currentCityInReturnPath = parentCityInReturnPath;
        }

        // Return trip
        WeightedGraph.ShortestPathTree shortestPath2 = flightNetwork.getShortestPath(endCityIndex);
        returnRoute = shortestPath2.getPath(startCityIndex);
    }

    // New method to calculate fares and save to fields
    private void calculateFares() {
        double forwardDistanceKm = calculateDistanceInKm(forwardRoute);
        forwardTripFare = forwardDistanceKm * 4;

        double returnDistanceKm = calculateDistanceInKm(returnRoute);
        returnTripFare = returnDistanceKm * 4;
    }

    public String getRoundTripDetails() {
        StringBuilder result = new StringBuilder();
        result.append("📍 Round Trip Details:\n\n");
        // ---------- FORWARD TRIP ----------
        StringBuilder forwardRouteBuilder = new StringBuilder(forwardRoute.get(0));
       
        for (int i = 1; i < forwardRoute.size(); i++) {
            forwardRouteBuilder.append(" -> ").append(forwardRoute.get(i));
        }
        result.append("Forward trip from ").append(cities[startCityIndex])
              .append(" to ").append(cities[endCityIndex]).append(": ")
              .append(forwardRouteBuilder).append("\n");

        double forwardDistanceKm = calculateDistanceInKm(forwardRoute);
        result.append(String.format("Forward route distance: %.2f km\n", forwardDistanceKm));
        result.append(String.format("Forward trip fare: ₹%.2f\n", forwardTripFare));

        // ---------- RETURN TRIP ----------
        StringBuilder returnRouteBuilder = new StringBuilder(returnRoute.get(0));
        for (int i = 1; i < returnRoute.size(); i++) {
            returnRouteBuilder.append(" -> ").append(returnRoute.get(i));
        }
        result.append("Return trip from ").append(cities[endCityIndex])
              .append(" to ").append(cities[startCityIndex]).append(": ")
              .append(returnRouteBuilder).append("\n");

        double returnDistanceKm = calculateDistanceInKm(returnRoute);
        result.append(String.format("Return route distance: %.2f km\n", returnDistanceKm));
        result.append(String.format("Return trip fare: ₹%.2f\n", returnTripFare));

        // ---------- TOTAL ----------
        result.append(String.format("Total roundtrip distance: %.2f km\n", forwardDistanceKm + returnDistanceKm));
        result.append(String.format("Total roundtrip fare: ₹%.2f\n", forwardTripFare + returnTripFare));

        return result.toString();
    }

    // ✅ One-way trip details
    public String getForwardTripDetails() {
        StringBuilder result = new StringBuilder();

        // ---------- FORWARD TRIP ----------
        StringBuilder forwardRouteBuilder = new StringBuilder(forwardRoute.get(0));
        for (int i = 1; i < forwardRoute.size(); i++) {
            forwardRouteBuilder.append(" -> ").append(forwardRoute.get(i));
        }

        result.append("One Way Trip from ").append(cities[startCityIndex])
              .append(" to ").append(cities[endCityIndex]).append(": ")
              .append(forwardRouteBuilder).append("\n");

        double forwardDistanceKm = calculateDistanceInKm(forwardRoute);
        result.append(String.format("Route distance: %.2f km\n", forwardDistanceKm));
        result.append(String.format("Estimated fare: ₹%.2f\n", forwardTripFare));

        return result.toString();
    }

    private double calculateDistanceInKm(List<String> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String fromCity = route.get(i);
            String toCity = route.get(i + 1);
            totalDistance += getDistanceBetweenCities(fromCity, toCity);
        }
        return totalDistance;
    }

    private int getDistanceBetweenCities(String fromCity, String toCity) {
        int fromCityIndex = Arrays.asList(cities).indexOf(fromCity);
        int toCityIndex = Arrays.asList(cities).indexOf(toCity);
        for (int[] connection : connections) {
            if (connection[0] == fromCityIndex && connection[1] == toCityIndex) {
                return connection[2];
            }
        }
        return 0;
    }

    public List<String> getForwardRoute() {
        return forwardRoute;
    }

    public List<String> getReturnRoute() {
        return returnRoute;
    }

    // Add these getter methods to expose fares:
    public double getForwardTripFare() {
        return forwardTripFare;
    }

    public double getReturnTripFare() {
        return returnTripFare;
    }

    public double getTotalRoundTripFare() {
        return forwardTripFare + returnTripFare;
    }
}
