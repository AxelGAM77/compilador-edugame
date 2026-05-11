import java.util.HashMap;
import java.util.Map;

/**
 * AnalizadorLexico: Analizador léxico generalizado que usa autómatas finitos
 * para reconocer palabras clave del lenguaje EduGame
 * 
 * Responsabilidades:
 * - Mantener un registro de autómatas para cada palabra clave
 * - Clasificar cadenas como palabras reservadas o identificadores
 * - Proporcionar métodos de búsqueda rápida de tokens
 */
public class AnalizadorLexico {
    private Map<String, AutomataFinito> automatas;
    
    /**
     * Constructor: Inicializa los autómatas para todas las palabras clave
     */
    public AnalizadorLexico() {
        this.automatas = new HashMap<>();
        
        // Registrar todas las palabras clave del lenguaje EduGame
        automatas.put("JUEGO", ConstructorAutomatas.crearAutomataJUEGO());
        automatas.put("ESCENA", ConstructorAutomatas.crearAutomataESCENA());
        automatas.put("BUCLE", ConstructorAutomatas.crearAutomatabUCLE());
        automatas.put("MOVER", ConstructorAutomatas.crearAutomataMOVER());
        automatas.put("FIN", ConstructorAutomatas.crearAutomataFIN());
        
        // Agregar más palabras aquí conforme expande el lenguaje
    }
    
    /**
     * Verifica si una cadena es una palabra clave reservada
     * 
     * @param cadena texto a verificar
     * @return true si coincide con alguna palabra clave; false en otro caso
     */
    public boolean esReservada(String cadena) {
        String normalizada = cadena.trim().toUpperCase();
        
        // Buscar en todos los autómatas
        for (AutomataFinito automata : automatas.values()) {
            if (automata.esValida(normalizada)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Identifica qué palabra clave es una cadena
     * 
     * @param cadena texto a identificar
     * @return nombre de la palabra clave, o null si no coincide con ninguna
     */
    public String identificarPalabra(String cadena) {
        String normalizada = cadena.trim().toUpperCase();
        
        for (Map.Entry<String, AutomataFinito> entrada : automatas.entrySet()) {
            if (entrada.getValue().esValida(normalizada)) {
                return entrada.getKey();
            }
        }
        return null;
    }
    
    /**
     * Obtiene un autómata específico por nombre de palabra
     * Útil para debuging o validaciones específicas
     * 
     * @param palabra nombre de la palabra clave
     * @return AutomataFinito o null si no existe
     */
    public AutomataFinito obtenerAutomata(String palabra) {
        return automatas.get(palabra);
    }
    
    /**
     * Obtiene todas las palabras clave reconocidas
     */
    public java.util.Set<String> obtenerPalabrasReservadas() {
        return automatas.keySet();
    }
}
