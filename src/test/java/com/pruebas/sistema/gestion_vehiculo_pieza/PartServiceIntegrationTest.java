package com.pruebas.sistema.gestion_vehiculo_pieza;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest 
public class PartServiceIntegrationTest {

    // Inyecta el servicio que vamos a probar
    @Autowired 
    private PartService partService; 

    // Mock necesario para la prueba de Part
    @MockBean 
    private PartRepository partRepository;
    
    // Mock necesario para que Spring pueda construir VehicleService
    @MockBean 
    private VehicleRepository vehicleRepository; 

    @Test
    void removeStock_DebeDisminuirStockYGuardarEnRepositorio() {
        // ARRANGE
        String partNumber = "P12345";
        int stockInicial = 10;
        int cantidadARestar = 3;
        
        // 1. Crear el objeto Part usando el método de fábrica que proporcionaste
        Part part = Part.createFromSupplier(partNumber, "Filtro", stockInicial, 25.50, "A1", "SUP-01", 5, "Filtro de aceite");
        
        // 2. Mockito: Simular findByPartNumber
        when(partRepository.findByPartNumber(partNumber)).thenReturn(part);

        // 3. Mockito: Simular save() para evitar NullPointerException
        when(partRepository.save(any(Part.class))).thenReturn(part); 

        // ACT
        Part resultPart = partService.removeStock(partNumber, cantidadARestar);

        // ASSERT
        
        // Verificar que la lógica de dominio (updateStock) se ejecutó correctamente
        int expectedStock = stockInicial - cantidadARestar;
        assertEquals(expectedStock, resultPart.getQuantityInStock(), "El stock debe disminuir correctamente.");
        
        // Verificar que la integración (Repository) se ejecutó correctamente
        verify(partRepository, times(1)).save(resultPart); 
    }
    
    @Test
    void removeStock_DebeLanzarExcepcionSiNoExiste() {
        // ARRANGE
        String partNumber = "P99999";
        
        // Mockito: Simular findByPartNumber para que devuelva null
        when(partRepository.findByPartNumber(partNumber)).thenReturn(null);

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            partService.removeStock(partNumber, 1);
        }, "Debe lanzar excepción si la pieza no se encuentra.");
        
        // Verificar que NUNCA se intentó guardar en la base de datos
        verify(partRepository, never()).save(any(Part.class));
    }
}