package com.pruebas.sistema.gestion_vehiculo_pieza;
import org.springframework.stereotype.Repository;

//Anotación clave para que Spring la reconozca como un componente
@Repository 
public interface VehicleRepository {
 Vehicle findByVin(String vin);
 Vehicle save(Vehicle vehicle);
}
