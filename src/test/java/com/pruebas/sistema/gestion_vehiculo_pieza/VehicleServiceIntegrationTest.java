package com.pruebas.sistema.gestion_vehiculo_pieza;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Anotación clave: Carga el contexto de Spring Boot para la prueba.
@SpringBootTest 
public class VehicleServiceIntegrationTest {

    // Inyecta la instancia real de VehicleService.
    @Autowired 
    private VehicleService vehicleService; 

    // Simula la dependencia de VehicleRepository (Base de Datos)
    @MockBean 
    private VehicleRepository vehicleRepository;
    
    // Simula la dependencia de PartRepository. 
    // Necesario para que Spring pueda construir el PartService al cargar el contexto.
    @MockBean 
    private PartRepository partRepository;

    @Test
    void reserveVehicle_DebeActualizarEstadoYGuardarEnRepositorio() {
        // ARRANGE (Preparar)
        String vin = "1G1RC71839Y100001";
        String ownerId = "CUST-007";
        
        // 1. Crear el objeto Vehicle con estado inicial AVAILABLE
        Vehicle vehicle = Vehicle.buyForSale(vin, "Toyota", "Corolla", 2022, 15000, 1000, 17000, "4327GTF");
        
        // 2. Mockito: Simular que el Repositorio encuentra el vehículo
        when(vehicleRepository.findByVin(vin)).thenReturn(vehicle);

        // Simular que save() devuelve el objeto 'vehicle'
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle); 

        // ACT (Ejecutar el método de integración)
        Vehicle resultVehicle = vehicleService.reserveVehicle(vin, ownerId);

        // ASSERT (Verificar)
        
        // 3. Verificar que la lógica de dominio (Vehicle) se ejecutó correctamente
        assertEquals(Vehicle.VehicleStatus.RESERVED, resultVehicle.getStatus(), "El estado del vehículo debe ser RESERVED."); 
        
        // 4. Verificar que la integración (Repository) se ejecutó correctamente
        // Aseguramos que el método save() del repositorio fue llamado exactamente una vez.
        verify(vehicleRepository, times(1)).save(resultVehicle); 
    }
    
    @Test
    void reserveVehicle_DebeLanzarExcepcionSiNoEstaDisponible() {
        // ARRANGE (Preparar)
        String vin = "2G1RT51839Y100002";
        // Crear un objeto Vehicle en estado FOR_DISASSEMBLED
        Vehicle vehicle = Vehicle.buyForScrap(vin, "Ford", "Fiesta", 1999, 50000, 1000);
        when(vehicleRepository.findByVin(vin)).thenReturn(vehicle);

        // ACT & ASSERT (Verificar la excepción)
        assertThrows(IllegalArgumentException.class, () -> {
            vehicleService.reserveVehicle(vin, "CUST-008");
        }, "Debe lanzar excepción si el vehículo no está AVAILABLE.");
        
        // Verificar que NUNCA se intentó guardar en la base de datos si la lógica falló.
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}