package com.pruebas.sistema.gestion_vehiculo_pieza;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import com.pruebas.sistema.gestion_vehiculo_pieza.Vehicle;
import com.pruebas.sistema.gestion_vehiculo_pieza.Vehicle.VehicleStatus;

public class VehicleTest {

    // --- Fixtures (Métodos para crear objetos de prueba) ---
    private Vehicle createVehicleForSale() {
        return Vehicle.buyForSale(
            "1G1RC71839Y100001", "Toyota", "Corolla", 2022, 15000, 1000, 17000, "4327GTF"
        );
    }
    
    private Vehicle createVehicleForScrap() {
        return Vehicle.buyForScrap(
            "2G1RT51839Y100002", "Ford", "Fiesta", 1999, 50000, 1000
        );
    }
    
    // --- Pruebas de Creación y Falla de Permisos ---
/*
    @Test
    @DisplayName("La creación directa del constructor debe lanzar un error de seguridad")
    void constructorDirectoLanzaError() {
        assertThrows(SecurityException.class, () -> {
            // Intentar llamar al constructor privado directamente
            // NOTA: En Java, esto lanza SecurityException o IllegalAccessException si se intenta con Reflection,
            // pero si intentamos acceder al constructor privado sin Reflection, es un error de compilación.
            // Aquí probamos el mecanismo de "guardToken" simulado por la fábrica.
        });
    }
*/
    @Test
    @DisplayName("La fábrica de venta inicializa el estado a AVAILABLE")
    void fabricaVentaInicializaDisponible() {
        Vehicle v = createVehicleForSale();
        assertTrue(v.isAvailableForSale());
        assertEquals(VehicleStatus.AVAILABLE, v.getStatus());
    }

    @Test
    @DisplayName("La fábrica de desarme inicializa el estado a FOR_DISASSEMBLED")
    void fabricaDesarmeInicializaParaDesarme() {
        Vehicle v = createVehicleForScrap();
        assertTrue(v.canBeDisassembled());
        assertEquals(VehicleStatus.FOR_DISASSEMBLED, v.getStatus());
    }

    @Test
    @DisplayName("La fábrica lanza error si el VIN es inválido")
    void fabricaFallaConVinInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle.buyForSale("VIN_INCORTO", "Marca", "Modelo", 2000, 1000, 100, 1500, "ABC");
        });
    }
    
    // --- Pruebas de Lógica de Negocio y Transiciones ---

    @Test
    @DisplayName("El cálculo de la ganancia es correcto")
    void calcularGanancia() {
        Vehicle v = createVehicleForSale();
        assertEquals(2000.0, v.calculateProfit(), 0.001); // 17000 - 15000
    }

    @Test
    @DisplayName("La transición válida de AVAILABLE a RESERVED funciona")
    void transicionValida() {
        Vehicle v = createVehicleForSale();
        v.updateStatus(VehicleStatus.RESERVED);
        assertEquals(VehicleStatus.RESERVED, v.getStatus());
    }
    
    @Test
    @DisplayName("La transición inválida de AVAILABLE a SOLD lanza error")
    void transicionInvalidaLanzaError() {
        Vehicle v = createVehicleForSale();
        assertThrows(IllegalArgumentException.class, () -> {
            v.updateStatus(VehicleStatus.SOLD);
        });
        assertEquals(VehicleStatus.AVAILABLE, v.getStatus());
    }
    
    // --- Pruebas del Método Estático ---

    @Test
    @DisplayName("validateVin retorna true para un VIN válido")
    void validarVinValido() {
        assertTrue(Vehicle.validateVin("1G1RC71839Y100001"));
    }

    @Test
    @DisplayName("validateVin retorna false si la longitud es incorrecta")
    void validarVinLongitudIncorrecta() {
        assertFalse(Vehicle.validateVin("12345"));
    }
    
    @Test
    @DisplayName("validateVin retorna false si tiene la letra prohibida I")
    void validarVinLetraProhibida() {
        assertFalse(Vehicle.validateVin("1GIRC71839Y100001"));
    }
}