package org.example;

public class Vehicle implements VehicleInterface, Comparable<Vehicle> {
    public enum FuelType { PETROL, DIESEL, HYBRID }

    private static long nextId = 0;
    private final long id = ++nextId;

    private String brand;
    private String model;
    private double basePrice;
    private int age;
    private double km;
    private FuelType fuelType;

    public Vehicle(String brand, String model, double basePrice,
                   int age, double km, FuelType fuelType) {
        this.brand = brand;
        this.model = model;
        this.basePrice = basePrice;
        this.age = age;
        this.km = km;
        this.fuelType = fuelType;
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getKm() { return km; }
    public void setKm(double km) { this.km = km; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    @Override
    public void applyDiscount(int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Discount must be 0..100");
        }
        basePrice *= (100 - percentage) / 100.0;
    }

    @Override
    public int getFinalPrice() {
        double price = Math.max(200_000, basePrice - age * 150_000);
        if (fuelType == FuelType.HYBRID) {
            price *= 1.15;
        } else if (fuelType == FuelType.DIESEL) {
            price *= 1.05;
        }
        return (int) Math.round(price);
    }

    @Override
    public double averageKmPerYear() {
        return age == 0 ? km : km / age;
    }

    @Override
    public int compareTo(Vehicle other) {
        int byPrice = Integer.compare(getFinalPrice(), other.getFinalPrice());
        return byPrice != 0 ? byPrice : Long.compare(id, other.id);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(brand).append(' ').append(model);
        text.append(" | basePrice=").append(basePrice);
        text.append(" | age=").append(age);
        text.append(" | km=").append(km);
        text.append(" | fuelType=").append(fuelType);
        text.append(" | finalPrice=").append(getFinalPrice());
        text.append(" | averageKmPerYear=").append(averageKmPerYear());
        return text.toString();
    }
}