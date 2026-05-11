/**
 * Clase de prueba para validar el análisis léxico
 */
public class PruebaAnalizador {
    public static void main(String[] args) {
        AnalizadorLexico analizador = new AnalizadorLexico();
        
        // Test casos
        String[] pruebas = {
            "JUEGO",
            "juego",
            "ESCENA",
            "escena",
            "BUCLE",
            "MOVER",
            "FIN",
            "FINALES",      // No válido (palabra incompleta)
            "JUGADOR",      // No válido (palabra diferente)
            "JUEG",         // No válido (palabra incompleta)
            "  JUEGO  "     // Válido (espacios al inicio/final)
        };
        
        System.out.println("=== PRUEBAS DE ANÁLISIS LÉXICO ===\n");
        
        for (String prueba : pruebas) {
            boolean esReservada = analizador.esReservada(prueba);
            String identificada = analizador.identificarPalabra(prueba);
            
            System.out.printf("Cadena: \"%s\"%n", prueba);
            System.out.printf("  ¿Reservada? %s%n", esReservada);
            System.out.printf("  Identificada como: %s%n", identificada != null ? identificada : "IDENTIFICADOR");
            System.out.println();
        }
        
        System.out.println("\nPalabras reservadas del lenguaje:");
        for (String palabra : analizador.obtenerPalabrasReservadas()) {
            System.out.println("  - " + palabra);
        }
    }
}
