package com.pruebas.sistema.gestion_vehiculo_pieza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartService {

    private final PartRepository partRepository;

    @Autowired
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    /**
     * Disminuye la cantidad de stock de una pieza (simulando una venta o uso).
     */
    public Part removeStock(String partNumber, int quantityToRemove) {
        
        // 1. Busca la pieza
        Part part = partRepository.findByPartNumber(partNumber);
        
        if (part == null) {
            // Si la pieza no existe
            throw new IllegalArgumentException("Pieza con número " + partNumber + " no encontrada.");
        }
        
        // 2. Ejecuta la lógica de dominio (que usa updateStock en Part.java)
        // Nota: Le pasamos un valor negativo para restar.
        part.updateStock(-quantityToRemove); 
        
        // 3. Guarda el cambio en el repositorio y devuelve el resultado
        return partRepository.save(part);
    }
}