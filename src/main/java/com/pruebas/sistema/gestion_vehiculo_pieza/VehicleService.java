package com.pruebas.sistema.gestion_vehiculo_pieza;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Lógica de negocio: Reserva un vehículo, actualizando su estado y dueño.
     */
    public Vehicle reserveVehicle(String vin, String ownerId) {
        Vehicle vehicle = vehicleRepository.findByVin(vin);
        
        if (vehicle == null) {
            throw new RuntimeException("Vehículo no encontrado");
        }
        
        // La validación y cambio de estado están en la clase Vehicle (lógica de dominio)
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.updateStatus(Vehicle.VehicleStatus.RESERVED);
            vehicle.setOwnerId(ownerId);
            return vehicleRepository.save(vehicle); // Persistencia
        } else {
             throw new IllegalArgumentException("El vehículo no está disponible para reservar.");
        }
    }
}