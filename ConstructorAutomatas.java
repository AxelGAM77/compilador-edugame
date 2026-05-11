/**
 * ConstructorAutomatas: Factory para crear autómatas finitos para palabras clave
 * 
 * Responsabilidades:
 * - Definir matrices de transiciones para cada palabra clave del lenguaje
 * - Centralizar la lógica de construcción de autómatas
 * - Permitir agregar nuevas palabras fácilmente
 */
public class ConstructorAutomatas {
    
    /**
     * Crea el autómata para la palabra "JUEGO"
     * 
     * Estados: 0=inicio, 1=J, 2=JU, 3=JUE, 4=JUEG, 5=JUEGO(aceptación), -1=error
     * Columnas: J=0, U=1, E=2, G=3, O=4
     */
    public static AutomataFinito crearAutomataJUEGO() {
        int[][] matriz = {
            {1, -1, -1, -1, -1},    // Estado 0: espera 'J'
            {-1, 2, -1, -1, -1},    // Estado 1: espera 'U'
            {-1, -1, 3, -1, -1},    // Estado 2: espera 'E'
            {-1, -1, -1, 4, -1},    // Estado 3: espera 'G'
            {-1, -1, -1, -1, 5}     // Estado 4: espera 'O'
        };
        char[] caracteres = {'J', 'U', 'E', 'G', 'O'};
        return new AutomataFinito("JUEGO", matriz, caracteres, 5);
    }
    
    /**
     * Crea el autómata para la palabra "ESCENA"
     * 
     * Estados: 0=inicio, 1=E, 2=ES, 3=ESC, 4=ESCE, 5=ESCEN, 6=ESCENA(aceptación), -1=error
     * Columnas: E=0, S=1, C=2, N=3, A=4
     */
    public static AutomataFinito crearAutomataESCENA() {
        int[][] matriz = {
            {1, -1, -1, -1, -1},    // Estado 0: espera 'E'
            {-1, 2, -1, -1, -1},    // Estado 1: espera 'S'
            {-1, -1, 3, -1, -1},    // Estado 2: espera 'C'
            {4, -1, -1, -1, -1},    // Estado 3: espera 'E'
            {-1, -1, -1, 5, -1},    // Estado 4: espera 'N'
            {-1, -1, -1, -1, 6}     // Estado 5: espera 'A'
        };
        char[] caracteres = {'E', 'S', 'C', 'N', 'A'};
        return new AutomataFinito("ESCENA", matriz, caracteres, 6);
    }
    
    /**
     * Crea el autómata para la palabra "BUCLE"
     * 
     * Estados: 0=inicio, 1=B, 2=BU, 3=BUC, 4=BUCL, 5=BUCLE(aceptación), -1=error
     * Columnas: B=0, U=1, C=2, L=3, E=4
     */
    public static AutomataFinito crearAutomatabUCLE() {
        int[][] matriz = {
            {1, -1, -1, -1, -1},    // Estado 0: espera 'B'
            {-1, 2, -1, -1, -1},    // Estado 1: espera 'U'
            {-1, -1, 3, -1, -1},    // Estado 2: espera 'C'
            {-1, -1, -1, 4, -1},    // Estado 3: espera 'L'
            {-1, -1, -1, -1, 5}     // Estado 4: espera 'E'
        };
        char[] caracteres = {'B', 'U', 'C', 'L', 'E'};
        return new AutomataFinito("BUCLE", matriz, caracteres, 5);
    }
    
    /**
     * Crea el autómata para la palabra "MOVER"
     * 
     * Estados: 0=inicio, 1=M, 2=MO, 3=MOV, 4=MOVE, 5=MOVER(aceptación), -1=error
     * Columnas: M=0, O=1, V=2, E=3, R=4
     */
    public static AutomataFinito crearAutomataMOVER() {
        int[][] matriz = {
            {1, -1, -1, -1, -1},    // Estado 0: espera 'M'
            {-1, 2, -1, -1, -1},    // Estado 1: espera 'O'
            {-1, -1, 3, -1, -1},    // Estado 2: espera 'V'
            {-1, -1, -1, 4, -1},    // Estado 3: espera 'E'
            {-1, -1, -1, -1, 5}     // Estado 4: espera 'R'
        };
        char[] caracteres = {'M', 'O', 'V', 'E', 'R'};
        return new AutomataFinito("MOVER", matriz, caracteres, 5);
    }
    
    /**
     * Crea el autómata para la palabra "FIN"
     * 
     * Estados: 0=inicio, 1=F, 2=FI, 3=FIN(aceptación), -1=error
     * Columnas: F=0, I=1, N=2
     */
    public static AutomataFinito crearAutomataFIN() {
        int[][] matriz = {
            {1, -1, -1},    // Estado 0: espera 'F'
            {-1, 2, -1},    // Estado 1: espera 'I'
            {-1, -1, 3}     // Estado 2: espera 'N'
        };
        char[] caracteres = {'F', 'I', 'N'};
        return new AutomataFinito("FIN", matriz, caracteres, 3);
    }
    
    /**
     * Método genérico para crear un autómata desde una palabra (opcionalmente)
     * Útil para palabras dinámicas o pruebas
     */
    public static AutomataFinito crearAutomataGenerico(String palabra) {
        // Convertir palabra a caracteres únicos
        char[] caracteres = palabra.toCharArray();
        
        // Crear matriz: cada estado permite solo el carácter siguiente
        int[][] matriz = new int[palabra.length()][palabra.length()];
        
        // Inicializar todo con -1 (error)
        for (int i = 0; i < palabra.length(); i++) {
            for (int j = 0; j < palabra.length(); j++) {
                matriz[i][j] = -1;
            }
        }
        
        // Llenar diagonal: en estado i, el carácter en posición i lleva al estado i+1
        for (int i = 0; i < palabra.length(); i++) {
            matriz[i][i] = i + 1;
        }
        
        return new AutomataFinito(palabra, matriz, caracteres, palabra.length());
    }
}
