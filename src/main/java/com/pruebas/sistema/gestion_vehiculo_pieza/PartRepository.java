package com.pruebas.sistema.gestion_vehiculo_pieza;
import org.springframework.stereotype.Repository;

// Anotación clave para que Spring la reconozca como un componente
@Repository 
public interface PartRepository {
    // Simula la búsqueda por Número de Pieza
    Part findByPartNumber(String partNumber); 
    
    // Simula la operación de guardar (insertar o actualizar)
    Part save(Part part); 
}