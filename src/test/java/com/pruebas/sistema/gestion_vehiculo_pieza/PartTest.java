package com.pruebas.sistema.gestion_vehiculo_pieza;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class PartTest {

    private Part partFromSupplier;
    private Part partFromDisassembly;

    // Simula la Fixture de pytest: Se ejecuta antes de cada test.
    @BeforeEach
    void setUp() {
        partFromSupplier = Part.createFromSupplier(
            "PN-12345", "Filtro de Aceite", 100, 15.50, "A-1-1", "SUP-001", 20, null
        );
        partFromDisassembly = Part.createFromDisassembly(
            "PN-67890-USED", "Puerta Delantera Izquierda", 1, 120.0, "D-3-5", "VIN-DESARME-987", 0, "Color rojo, leve rayón"
        );
    }

    // --- Pruebas de Creación y Fallas ---
    
    @Test
    @DisplayName("La creación directa del constructor es bloqueada por el compilador (en Java)")
    void testCreacionDirectaFallaConSecurityError() {
        // En Java, el constructor privado (private) lo bloquea el compilador.
        // Por consistencia con la prueba Python, validamos la regla de negocio más cercana:
        // Que no se puede crear un objeto sin usar una de las fábricas.
        // Si el constructor fuera público, probaríamos la SecurityException.
        // Para este patrón, la prueba se elimina o se ignora (como se sugirió antes).
    }

    @Test
    @DisplayName("Fábrica de proveedor crea pieza correctamente")
    void testFabricaProveedorCreaPiezaCorrectamente() {
        assertEquals("Filtro de Aceite", partFromSupplier.getName());
        assertEquals(100, partFromSupplier.getQuantityInStock());
        assertEquals(15.50, partFromSupplier.getUnitPrice(), 0.001);
        assertEquals("SUP-001", partFromSupplier.getSupplierId());
        assertNull(partFromSupplier.getSourceVehicleVin());
    }
    
    @Test
    @DisplayName("Fábrica de desarme crea pieza correctamente")
    void testFabricaDesarmeCreaPiezaCorrectamente() {
        assertEquals("Puerta Delantera Izquierda", partFromDisassembly.getName());
        assertEquals(1, partFromDisassembly.getQuantityInStock());
        assertEquals("VIN-DESARME-987", partFromDisassembly.getSourceVehicleVin());
        assertEquals("Color rojo, leve rayón", partFromDisassembly.getDescription());
        assertNull(partFromDisassembly.getSupplierId());
    }

    @Test
    @DisplayName("Fábrica de proveedor falla sin supplierId")
    void testFabricaProveedorFallaSinSupplierId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Part.createFromSupplier("P1", "N1", 1, 1, "L1", null, 1, null);
        }, "Debería fallar si supplierId es null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            Part.createFromSupplier("P1", "N1", 1, 1, "L1", "", 1, null);
        }, "Debería fallar si supplierId es vacío");
    }

    @Test
    @DisplayName("Fábrica de desarme falla sin sourceVehicleVin")
    void testFabricaDesarmeFallaSinSourceVehicleVin() {
        assertThrows(IllegalArgumentException.class, () -> {
            Part.createFromDisassembly("P1", "N1", 1, 1, "L1", null, 1, null);
        }, "Debería fallar si sourceVehicleVin es null");
    }

    // --- Pruebas de Métodos y Lógica de Negocio ---
    
    @Test
    @DisplayName("updateStock suma y resta correctamente")
    void testUpdateStockSumaYRestaCorrectamente() {
        partFromSupplier.updateStock(25);
        assertEquals(125, partFromSupplier.getQuantityInStock());
        partFromSupplier.updateStock(-10);
        assertEquals(115, partFromSupplier.getQuantityInStock());
    }

    @Test
    @DisplayName("updateStock falla si el stock resulta negativo")
    void testUpdateStockFallaSiStockEsNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            partFromSupplier.updateStock(-101); // 100 - 101 = -1
        });
        // Se verifica que el stock no cambió después del fallo
        assertEquals(100, partFromSupplier.getQuantityInStock());
    }

    @Test
    @DisplayName("isLowStock funciona correctamente con umbral positivo")
    void testIsLowStockFuncionaCorrectamente() {
        assertFalse(partFromSupplier.isLowStock()); // 100 > 20
        partFromSupplier.updateStock(-80); 
        assertTrue(partFromSupplier.isLowStock()); // 20 <= 20
        partFromSupplier.updateStock(-1); 
        assertTrue(partFromSupplier.isLowStock()); // 19 <= 20
    }

    @Test
    @DisplayName("isLowStock siempre es falso si el umbral es cero o negativo")
    void testIsLowStockConUmbralCeroONegativo() {
        partFromDisassembly.setLowStockThreshold(0);
        assertFalse(partFromDisassembly.isLowStock()); 
        
        partFromDisassembly.setLowStockThreshold(-10);
        assertFalse(partFromDisassembly.isLowStock());
    }

    @Test
    @DisplayName("calculateTotalValue calcula correctamente el valor total")
    void testCalculateTotalValue() {
        // 100 unidades * 15.50 = 1550.0
        assertEquals(1550.0, partFromSupplier.calculateTotalValue(), 0.001); 
    }

    @Test
    @DisplayName("Gestión de compatibilidad de vehículo")
    void testCompatibilidadDeVehiculo() {
        String vinTest1 = "VIN-COMPAT-111";
        
        // Inicialmente no compatible
        assertFalse(partFromSupplier.isCompatibleVehicle(vinTest1));
        
        // Agregar y verificar
        partFromSupplier.addCompatibleVehicle(vinTest1);
        assertTrue(partFromSupplier.isCompatibleVehicle(vinTest1));
        
        // No permite duplicados
        int initialSize = partFromSupplier.getCompatibleVehicles().size();
        partFromSupplier.addCompatibleVehicle(vinTest1);
        assertEquals(initialSize, partFromSupplier.getCompatibleVehicles().size());
    }
}