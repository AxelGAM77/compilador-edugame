/**
 * AutomataFinito: Representación genérica de un autómata finito determinista (AFD)
 * para reconocer una palabra específica.
 * 
 * Responsabilidades:
 * - Almacenar la matriz de transiciones
 * - Mapear caracteres a columnas
 * - Validar cadenas contra el autómata
 */
public class AutomataFinito {
    private String palabra;           // La palabra que reconoce (ej: "JUEGO")
    private int[][] matrizTransiciones;  // Matriz[estado][columna] = siguiente_estado
    private char[] caracteresValidos;    // Caracteres esperados en orden
    private int estadoAceptacion;        // Estado final válido
    private int estadoError = -1;        // Código de error
    
    /**
     * Constructor: Define un autómata para una palabra específica
     * 
     * @param palabra palabra a reconocer
     * @param matrizTransiciones matriz de transiciones del AFD
     * @param caracteresValidos caracteres que pueden aparecer (en orden de columnas)
     * @param estadoAceptacion estado final válido
     */
    public AutomataFinito(String palabra, int[][] matrizTransiciones, 
                          char[] caracteresValidos, int estadoAceptacion) {
        this.palabra = palabra;
        this.matrizTransiciones = matrizTransiciones;
        this.caracteresValidos = caracteresValidos;
        this.estadoAceptacion = estadoAceptacion;
    }
    
    /**
     * Obtiene la columna (índice) de un carácter en la matriz
     * 
     * @param letra carácter a buscar
     * @return índice en caracteresValidos, o -1 si no existe
     */
    private int obtenerColumna(char letra) {
        for (int i = 0; i < caracteresValidos.length; i++) {
            if (caracteresValidos[i] == letra) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Valida si una cadena coincide exactamente con la palabra del autómata
     * 
     * @param cadena texto a validar
     * @return true si la cadena es exactamente la palabra; false en otro caso
     */
    public boolean esValida(String cadena) {
        String subcadena = cadena.trim();
        
        // Validación rápida: la longitud debe coincidir
        if (subcadena.length() != palabra.length()) {
            return false;
        }
        
        int estado = 0;
        
        for (int i = 0; i < subcadena.length(); i++) {
            char letra = Character.toLowerCase(subcadena.charAt(i));
            int columna = obtenerColumna(letra);
            
            // Carácter no reconocido o ya llegó a aceptación antes de tiempo
            if (columna == -1 || estado >= estadoAceptacion) {
                return false;
            }
            
            // Transición
            estado = matrizTransiciones[estado][columna];
            
            // Detectar error (transición a estado inválido)
            if (estado == estadoError) {
                return false;
            }
        }
        
        // Validar que terminó exactamente en el estado de aceptación
        return estado == estadoAceptacion;
    }
    
    /**
     * Obtiene la palabra que este autómata reconoce
     */
    public String getPalabra() {
        return palabra;
    }
    
    /**
     * Obtiene la matriz de transiciones (para debugging)
     */
    public int[][] getMatrizTransiciones() {
        return matrizTransiciones;
    }
}
