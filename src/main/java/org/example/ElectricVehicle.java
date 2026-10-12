package org.example;

public class ElectricVehicle extends Vehicle implements ElectricInterface {
    private int batteryCapacity;
    private boolean hasHeatPump;

    public ElectricVehicle(String brand, String model, double basePrice,
                           int age, double km, int batteryCapacity,
                           boolean hasHeatPump) {
        super(brand, model, basePrice, age, km, FuelType.HYBRID);
        this.batteryCapacity = batteryCapacity;
        this.hasHeatPump = hasHeatPump;
    }

    public int getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(int batteryCapacity) { this.batteryCapacity = batteryCapacity; }

    public boolean hasHeatPump() { return hasHeatPump; }
    public void setHasHeatPump(boolean hasHeatPump) { this.hasHeatPump = hasHeatPump; }

    @Override
    public int getFinalPrice() {
        double multiplier = 1.0;
        if (batteryCapacity >= 60) multiplier += 0.10;
        if (hasHeatPump) multiplier += 0.05;
        return (int) Math.round(super.getFinalPrice() * multiplier);
    }

    @Override
    public String toString() {
        return super.toString() + " | batteryCapacity=" + batteryCapacity
                + " | hasHeatPump=" + hasHeatPump;
    }

    @Override
    public boolean hasSameRangeCategory(Vehicle other) {
        if (!(other instanceof ElectricVehicle)) return false;
        ElectricVehicle electric = (ElectricVehicle) other;
        return Math.abs(batteryCapacity - electric.batteryCapacity) <= 5;
    }

    @Override
    public int batteryCostShare() {
        return batteryCapacity * 25_000;
    }
}