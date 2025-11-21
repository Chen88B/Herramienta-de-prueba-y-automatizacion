package com.pruebas.sistema.gestion_vehiculo_pieza;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Part {

    // --- Atributos privados (Encapsulación) ---
    private final String partNumber;
    private String name;
    private String description;
    private int quantityInStock;
    private int lowStockThreshold;
    private double unitPrice;
    private String location;
    private final String sourceVehicleVin;
    private final String supplierId;
    private List<String> compatibleVehicles;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constante para el token de guardia
    private static final String FACTORY_AUTHORIZED = "FACTORY_AUTHORIZED";

    /**
     * Constructor Privado - Sólo accesible mediante métodos de fábrica.
     */
    private Part(String partNumber, String name, int quantityInStock, double unitPrice, 
                 String location, int lowStockThreshold, String description, 
                 String sourceVehicleVin, String supplierId, List<String> compatibleVehicles, 
                 String guardToken) {
        
        // Simulación del permiso de Python
        if (!FACTORY_AUTHORIZED.equals(guardToken)) {
            throw new SecurityException(
                "No se puede crear 'Part' directamente. " +
                "Use un método de fábrica como: Part.createFromSupplier() o Part.createFromDisassembly()"
            );
        }

        // Inicialización de atributos
        this.partNumber = partNumber;
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.unitPrice = unitPrice;
        this.location = location;
        
        // Opcionales con valores por defecto
        this.lowStockThreshold = lowStockThreshold;
        this.description = description;
        this.sourceVehicleVin = sourceVehicleVin; // Será null o un VIN
        this.supplierId = supplierId;           // Será null o un ID
        
        this.compatibleVehicles = (compatibleVehicles != null) ? compatibleVehicles : new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // --- Métodos de Fábrica Estáticos ---

    /**
     * Fábrica para una pieza comprada a un proveedor.
     */
    public static Part createFromSupplier(String partNumber, String name, int quantityInStock, double unitPrice, 
                                        String location, String supplierId, int lowStockThreshold, String description) {
        if (supplierId == null || supplierId.isEmpty()) {
            throw new IllegalArgumentException("create_from_supplier DEBE tener un 'supplier_id'.");
        }
        
        // El sourceVehicleVin es nulo y pasamos el token
        return new Part(partNumber, name, quantityInStock, unitPrice, location, lowStockThreshold, 
                        description, null, supplierId, null, FACTORY_AUTHORIZED);
    }

    /**
     * Fábrica para una pieza obtenida de un vehículo desguazado.
     */
    public static Part createFromDisassembly(String partNumber, String name, int quantityInStock, double unitPrice, 
                                            String location, String sourceVehicleVin, int lowStockThreshold, String description) {
        if (sourceVehicleVin == null || sourceVehicleVin.isEmpty()) {
            throw new IllegalArgumentException("create_from_disassembly DEBE tener un 'source_vehicle_vin'.");
        }

        // El supplierId es nulo y pasamos el token
        return new Part(partNumber, name, quantityInStock, unitPrice, location, lowStockThreshold, 
                        description, sourceVehicleVin, null, null, FACTORY_AUTHORIZED);
    }
    
    // --- Lógica de Negocio ---

    /**
     * Metodo para agregar o restar la cantidad de pieza en stock.
     */
    public void updateStock(int cantidad) {
        if (this.quantityInStock + cantidad < 0) {
            throw new IllegalArgumentException(
                String.format("No se puede restar %d. Solo hay %d en stock.", Math.abs(cantidad), this.quantityInStock)
            );
        }
        this.quantityInStock += cantidad;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Metodo para comprobar si la cantidad de pieza está por debajo de la alerta.
     */
    public boolean isLowStock() {
        if (this.lowStockThreshold <= 0) {
            return false;
        }
        return this.quantityInStock <= this.lowStockThreshold;
    }

    /**
     * Metodo para calcula el valor total de la pieza en stock.
     */
    public double calculateTotalValue() {
        return this.quantityInStock * this.unitPrice;
    }

    /**
     * Metodo para agregar un nuevo vehículo compatible (por VIN).
     */
    public void addCompatibleVehicle(String vehicleVin) {
        if (!this.compatibleVehicles.contains(vehicleVin)) {
            this.compatibleVehicles.add(vehicleVin);
        }
    }

    /**
     * Metodo para comprobar si un vehículo es compatible con la pieza.
     */
    public boolean isCompatibleVehicle(String vehicleVin) {
        return this.compatibleVehicles.contains(vehicleVin);
    }

    // --- Getters  ---

    public String getPartNumber() { return partNumber; }
    public String getName() { return name; }
    public int getQuantityInStock() { return quantityInStock; }
    public double getUnitPrice() { return unitPrice; }
    public String getLocation() { return location; }
    public String getSupplierId() { return supplierId; }
    // Devolvemos una lista inmodificable para proteger el estado interno
    public List<String> getCompatibleVehicles() { return Collections.unmodifiableList(compatibleVehicles); } 
    public int getLowStockThreshold() { return lowStockThreshold; }
    public String getDescription() { return description; }
    public String getSourceVehicleVin() { return sourceVehicleVin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // --- Setters (Simulan @setter de Python) ---

    public void setName(String name) { this.name = name; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
}