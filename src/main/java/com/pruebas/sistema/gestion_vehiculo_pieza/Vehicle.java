package com.pruebas.sistema.gestion_vehiculo_pieza;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Vehicle {
	// Clase interna (Enum)
    public enum VehicleStatus {
        AVAILABLE, IN_REPAIR, FOR_DISASSEMBLED, DISASSEMBLED, SOLD, RESERVED
    }

    // Atributos privados
    private final String vin;
    private String make;
    private String model;
    private int year;
    private double purchasePrice;
    private double salePrice;
    private int mileage;
    private String licensePlate;
    private VehicleStatus status;
    private List<String> photos;
    private String ownerId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constante para el token de guardia
    private static final String FACTORY_AUTHORIZED = "FACTORY_AUTHORIZED";

    /**
     * Constructor Privado - Solo accesible mediante métodos de fábrica.
     */
    private Vehicle(String vin, String make, String model, int year, double purchasePrice,
                    int mileage, VehicleStatus initialStatus, double salePrice, String licensePlate,
                    String guardToken) {
        
        // Simulación del permiso de Python
        if (!FACTORY_AUTHORIZED.equals(guardToken)) {
            throw new SecurityException("No se puede crear un 'Vehicle' directamente. " +
                                        "Use un método de fábrica como: Vehicle.buyForSale(), buyForScrap(), buyForRepair()");
        }

        // Inicialización de atributos
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.mileage = mileage;
        this.licensePlate = licensePlate;
        this.status = initialStatus;
        
        // Atributos por defecto/iniciales
        this.photos = new ArrayList<>();
        this.ownerId = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- Métodos Estáticos (Fábricas) para Sobrecarga de Constructor ---

    /**
     * Fábrica para un vehículo comprado para venta inmediata (AVAILABLE).
     */
    public static Vehicle buyForSale(String vin, String make, String model, int year, double purchasePrice, int mileage, double salePrice, String licensePlate) {
        if (!validateVin(vin)) {
            throw new IllegalArgumentException("El VIN " + vin + " no es válido.");
        }
        return new Vehicle(vin, make, model, year, purchasePrice, mileage, VehicleStatus.AVAILABLE, salePrice, licensePlate, FACTORY_AUTHORIZED);
    }
    
    /**
     * Fábrica para un vehículo comprado para desarme (FOR_DISASSEMBLED).
     */
    public static Vehicle buyForScrap(String vin, String make, String model, int year, double purchasePrice, int mileage) {
        if (!validateVin(vin)) {
            throw new IllegalArgumentException("El VIN " + vin + " no es válido.");
        }
        // salePrice y licensePlate son opcionales en Python, aquí los ponemos a valores por defecto
        return new Vehicle(vin, make, model, year, purchasePrice, mileage, VehicleStatus.FOR_DISASSEMBLED, 0.0, null, FACTORY_AUTHORIZED);
    }
    
    /**
     * Fábrica para un vehículo comprado para reparación (IN_REPAIR).
     */
    public static Vehicle buyForRepair(String vin, String make, String model, int year, double purchasePrice, int mileage) {
        if (!validateVin(vin)) {
            throw new IllegalArgumentException("El VIN " + vin + " no es válido.");
        }
        return new Vehicle(vin, make, model, year, purchasePrice, mileage, VehicleStatus.IN_REPAIR, 0.0, null, FACTORY_AUTHORIZED);
    }

    // --- Lógica de Negocio ---

    /**
     * Metodo estatico para validar un VIN (Vehicle Identification Number).
     */
    public static boolean validateVin(String vin) {
        if (vin == null) return false;

        String vinUpper = vin.toUpperCase();

        // 1. Regla de Longitud (17 caracteres)
        if (vinUpper.length() != 17) return false;

        // 2. Regla de Caracteres Prohibidos (I, Q, O) y Alfanuméricos
        String invalidCharsPattern = "[IQO]";
        if (Pattern.compile(invalidCharsPattern).matcher(vinUpper).find()) return false;
        
        // 3. Regla de Caracteres Alfanuméricos (isalnum)
        // [A-Z0-9] excluyendo I, Q, O
        String alphanumericPattern = "^[A-HJ-NPR-Z0-9]*$"; // El regex debe ser más preciso si se excluyen las letras
        // Si ya excluimos I, Q, O, solo comprobamos que todo sea letra o dígito
        return vinUpper.matches("^[A-Z0-9]*$");
    }

    /**
     * Agrega una URL de foto a la lista.
     */
    public void addPhoto(String urlPhoto) {
        this.photos.add(urlPhoto);
    }

    /**
     * Calcula la ganancia potencial.
     */
    public double calculateProfit() {
        return this.salePrice - this.purchasePrice;
    }

    /**
     * Metodo para cambiar el estado del vehículo, aplicando validación.
     */
    public void updateStatus(VehicleStatus newStatus) {
        if (isTransitionValid(newStatus)) {
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException(
                String.format("Transición ilegal de %s a %s", this.status, newStatus)
            );
        }
    }

    /**
     * Método privado que sirve para validar la transición de estado.
     */
    private boolean isTransitionValid(VehicleStatus newStatus) {
        VehicleStatus currentStatus = this.status;

        // 1. Un estado VENDIDO o DESGUAZADO es final
        if (currentStatus == VehicleStatus.SOLD || currentStatus == VehicleStatus.DISASSEMBLED) {
            return false;
        }

        // 2. Transiciones permitidas
        switch (currentStatus) {
            case AVAILABLE:
                return newStatus == VehicleStatus.RESERVED || newStatus == VehicleStatus.IN_REPAIR;
            case RESERVED:
                return newStatus == VehicleStatus.SOLD || newStatus == VehicleStatus.AVAILABLE;
            case IN_REPAIR:
                return newStatus == VehicleStatus.AVAILABLE;
            case FOR_DISASSEMBLED:
                return newStatus == VehicleStatus.DISASSEMBLED;
            default:
                return false;
        }
    }

    // --- Getters y Setters ---

    // Getters
    public String getVin() { return vin; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getSalePrice() { return salePrice; }
    public int getMileage() { return mileage; }
    public String getLicensePlate() { return licensePlate; }
    public VehicleStatus getStatus() { return status; }
    public List<String> getPhotos() { return Collections.unmodifiableList(photos); }
    public String getOwnerId() { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isAvailableForSale() { return this.status == VehicleStatus.AVAILABLE; }
    public boolean canBeDisassembled() { return this.status == VehicleStatus.FOR_DISASSEMBLED; }
    public boolean needsRepair() { return this.status == VehicleStatus.IN_REPAIR; }

    // Setters
    public void setMake(String make) { this.make = make; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

}
