public class TablaSimbolos {

    // ==================== PALABRAS RESERVADAS ====================
    // Abren bloques: JUEGO, INICIAR, DIBUJAR, ACTUALIZAR, PULSA, SI
    // Cierra bloques: FIN (unico)
    // Tipos de datos: Personaje, Numero, Texto, Imagen, Sprite, Objeto, Caracter, Boolean
    // Acciones: MOVER, INTERACTUAR
    // Direcciones: DERECHA, IZQUIERDA, ARRIBA, ABAJO
    // Otros: Nuevo, FUNCION, REGRESAR

    public static String[] LEXEMAS = {
        // Abren bloques
        "JUEGO", "INICIAR", "ACTUALIZAR", "DIBUJAR", "PULSA", "SI", "FUNCION",
        // Cierra bloques
        "FIN",
        // Tipos de datos
        "Personaje", "Numero", "Texto", "Imagen", "Sprite", "Objeto", "Caracter", "Boolean",
        // Acciones
        "MOVER", "INTERACTUAR",
        // Direcciones
        "DERECHA", "IZQUIERDA", "ARRIBA", "ABAJO",
        // Otros
        "Nuevo", "REGRESAR"
    };

    public static String[] OPERADORES = {
        "+", "-", "*", "/", "%", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "="
    };

    public static String[] DELIMITADORES = {
        "(", ")", "{", "}", "[", "]", ",", ";", ":"
    };

    public static String[] TECLAS = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "ESPACIO", "ENTER", "TAB", "ESCAPE", "BORRAR",
        "SHIFT", "CTRL", "ALT",
        "ARRIBA", "ABAJO", "IZQUIERDA", "DERECHA",
        "F1", "F2", "F3", "F4", "F5", "F6",
        "F7", "F8", "F9", "F10", "F11", "F12"
    };

    // ==================== LOOKUP POR ID ====================

    public static int obtenerLexemaId(String lexema) {
        for (int i = 0; i < LEXEMAS.length; i++) {
            if (LEXEMAS[i].equals(lexema)) {
                return i;
            }
        }
        return -1;
    }

    public static int obtenerOperadorId(String operador) {
        for (int i = 0; i < OPERADORES.length; i++) {
            if (OPERADORES[i].equals(operador)) {
                return i;
            }
        }
        return -1;
    }

    public static int obtenerDelimitadorId(String delimitador) {
        for (int i = 0; i < DELIMITADORES.length; i++) {
            if (DELIMITADORES[i].equals(delimitador)) {
                return i;
            }
        }
        return -1;
    }

    public static int obtenerTeclaId(String tecla) {
        for (int i = 0; i < TECLAS.length; i++) {
            if (TECLAS[i].equals(tecla)) {
                return i;
            }
        }
        return -1;
    }

    // ==================== VERIFICACIONES ====================

    public static boolean esTipoDato(String lexema) {
        return lexema.equals("Personaje") || lexema.equals("Numero") || lexema.equals("Texto") ||
               lexema.equals("Imagen") || lexema.equals("Objeto") || lexema.equals("Sprite") ||
               lexema.equals("Caracter") || lexema.equals("Boolean");
    }

    public static boolean esAperturaBloque(String lexema) {
        return lexema.equals("JUEGO") || lexema.equals("INICIAR") || lexema.equals("ACTUALIZAR") ||
               lexema.equals("DIBUJAR") || lexema.equals("PULSA") || lexema.equals("SI");
    }
}
