package com.pruebas.sistema.gestion_vehiculo_pieza;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test unitario para la clase Calculator.
 * Usa solo JUnit, sin cargar el contexto de Spring Boot.
 */
public class CalculatorTest {

    @Test
    void testAddTwoNumbers() {
        // ARRANGE (Preparar)
        Calculator calculator = new Calculator();
        int numberA = 5;
        int numberB = 3;
        int expectedSum = 8; // 5 + 3 = 8

        // ACT (Actuar)
        int actualSum = calculator.add(numberA, numberB);

        // ASSERT (Afirmar/Verificar)
        // Comprueba si el resultado real es igual al resultado esperado
        assertEquals(expectedSum, actualSum, "La suma de 5 y 3 debe ser 8");
    }

    @Test
    void testAddNegativeNumber() {
        // ARRANGE
        Calculator calculator = new Calculator();
        int a = 10;
        int b = -4;
        int expected = 6;

        // ACT
        int actual = calculator.add(a, b);

        // ASSERT
        assertEquals(expected, actual, "La suma con un número negativo debe ser correcta");
    }
    
    @Test
    void testAdd0Number() {
        // ARRANGE
        Calculator calculator = new Calculator();
        int a = 10;
        int b = 0;
        int expected = 10;

        // ACT
        int actual = calculator.add(a, b);

        // ASSERT
        assertEquals(expected, actual, "La suma con un número cero debe ser correcta");
    }
}