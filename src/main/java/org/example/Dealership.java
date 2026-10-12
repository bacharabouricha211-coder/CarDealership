package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

public class Dealership {
    private final TreeSet<Vehicle> vehicles = new TreeSet<>();

    public static void main(String[] args) {
        Dealership dealership = new Dealership();
        dealership.loadVehicles();
        dealership.runReport();
    }

    private void loadVehicles() {
        try (BufferedReader reader = Files.newBufferedReader(Path.of("vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) vehicles.add(parseVehicle(line));
            }
            if (vehicles.isEmpty()) throw new IOException("The input file is empty");
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Could not load vehicles.txt; using sample vehicles: " + error.getMessage());
            vehicles.clear();
            addFallbackVehicles();
        }
    }

    private Vehicle parseVehicle(String line) {
        String[] parts = line.split("#");
        if (parts.length != 6 && parts.length != 8) {
            throw new IllegalArgumentException("Invalid row: " + line);
        }

        String[] name = parts[1].split(" ", 2);
        String brand = name[0];
        String model = name.length == 2 ? name[1] : "";
        double basePrice = Double.parseDouble(parts[2]);
        int age = Integer.parseInt(parts[3]);
        double km = Double.parseDouble(parts[4]);
        Vehicle.FuelType fuel = Vehicle.FuelType.valueOf(parts[5]);

        if (parts[0].equals("VEHICLE") && parts.length == 6) {
            return new Vehicle(brand, model, basePrice, age, km, fuel);
        }

        if (parts[0].equals("ELECTRIC") && parts.length == 8
                && fuel == Vehicle.FuelType.HYBRID) {
            int capacity = Integer.parseInt(parts[6]);
            if (!parts[7].equalsIgnoreCase("yes") && !parts[7].equalsIgnoreCase("no")) {
                throw new IllegalArgumentException("Heat pump must be yes or no: " + line);
            }
            boolean heatPump = parts[7].equalsIgnoreCase("yes");
            return new ElectricVehicle(brand, model, basePrice, age, km,
                    capacity, heatPump);
        }

        throw new IllegalArgumentException("Unknown vehicle type: " + line);
    }

    private void addFallbackVehicles() {
        vehicles.add(new Vehicle("Toyota", "Corolla", 5_500_000, 4,
                75_000, Vehicle.FuelType.HYBRID));
        vehicles.add(new ElectricVehicle("Nissan", "Leaf", 4_800_000, 5,
                65_000, 40, false));
    }

    private void runReport() {
        StringBuilder report = new StringBuilder();
        appendFleetAverage(report);
        appendCheapest(report);
        appendTopElectric(report);
        appendTotalValue(report);
        appendFilteredHybrids(report);
        appendYoungCount(report);
        appendDiscountSimulation(report);

        String text = report.toString();
        System.out.print(text);

        try {
            Files.writeString(Path.of("outputDealership.txt"), text);
        } catch (IOException error) {
            System.err.println("Could not write outputDealership.txt: " + error.getMessage());
        }
    }

    private void appendFleetAverage(StringBuilder report) {
        double totalKm = 0;
        for (Vehicle vehicle : vehicles) totalKm += vehicle.getKm();
        report.append("1. Fleet Average Mileage: ")
                .append(totalKm / vehicles.size()).append(" km\n");
    }

    private void appendCheapest(StringBuilder report) {
        report.append("2. Cheapest Vehicle: ")
                .append(vehicles.first()).append('\n');
    }

    private void appendTopElectric(StringBuilder report) {
        ElectricVehicle top = null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle instanceof ElectricVehicle) {
                ElectricVehicle electric = (ElectricVehicle) vehicle;
                if (top == null || electric.getFinalPrice() > top.getFinalPrice()) {
                    top = electric;
                }
            }
        }
        report.append("3. Top Electric Vehicle: ");
        if (top == null) {
            report.append("none\n");
        } else {
            report.append(top.getBrand()).append(' ').append(top.getModel())
                    .append(" | batteryCapacity=").append(top.getBatteryCapacity())
                    .append(" | averageKmPerYear=").append(top.averageKmPerYear())
                    .append('\n');
        }
    }

    private void appendTotalValue(StringBuilder report) {
        long total = 0;
        for (Vehicle vehicle : vehicles) total += vehicle.getFinalPrice();
        report.append("4. Total Fleet Inventory Value: ").append(total).append('\n');
    }

    private void appendFilteredHybrids(StringBuilder report) {
        long total = 0;
        for (Vehicle vehicle : vehicles) total += vehicle.getFinalPrice();
        double averagePrice = (double) total / vehicles.size();
        report.append("5. Filtered Hybrid Inventory:\n");
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getFuelType() == Vehicle.FuelType.HYBRID
                    && vehicle.getFinalPrice() <= averagePrice) {
                report.append(" ").append(vehicle).append('\n');
            }
        }
    }

    private void appendYoungCount(StringBuilder report) {
        int count = 0;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getAge() < 5) count++;
        }
        report.append("6. Young Inventory Count: ").append(count).append('\n');
    }

    private void appendDiscountSimulation(StringBuilder report) {
        Vehicle selected = vehicles.first();
        vehicles.remove(selected);
        selected.applyDiscount(10);
        vehicles.add(selected);
        report.append("7. Discount Simulation: ")
                .append(selected.getBrand()).append(' ').append(selected.getModel())
                .append(" | updatedFinalPrice=").append(selected.getFinalPrice())
                .append('\n');
    }
}