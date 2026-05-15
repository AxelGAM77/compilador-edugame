public class TablaSimbolos {
    public static String[] LEXEMAS = {
        "JUEGO",        "INICIAR",      "DIBUJAR",      "ACTUALIZAR",       "PERSONAJE",        "MOVER",
        "FIN",          "NUMERO",       "TEXTO",        "IMAGEN",           "INTERACTUAR",      "DERECHA",
        "IZQUIERDA",    "ARRIBA",       "ABAJO"
    }; //id es igual al indice en el arreglo

    public static String[] OPERADORES = {
        "+", "-", "*", "/", "%", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "="
    };

    public static String[] DELIMITADORES = {
        "(", ")", "{", "}", "[", "]", ",", ";", ":"
    };

    public static int obtenerLexemaId(String lexema) {
        for (int i = 0; i < LEXEMAS.length; i++) {
            if (LEXEMAS[i].equalsIgnoreCase(lexema)) {
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
}
