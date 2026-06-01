import java.util.ArrayList;
import java.util.List;

public class Tokenizador {
    private String entrada;
    private int posicion;
    private int fila;
    private int columna;
    private List<Token> tokens;

    public Tokenizador(String entrada) {
        this.entrada = entrada;
        this.posicion = 0;
        this.fila = 1;
        this.columna = 1;
        this.tokens = new ArrayList<>();
    }

    public List<Token> tokenizar() {
        while (posicion < entrada.length()) {
            char actual = entrada.charAt(posicion);

            // Ignorar espacios en blanco
            if (Character.isWhitespace(actual)) {
                if (actual == '\n') {
                    fila++;
                    columna = 1;
                } else {
                    columna++;
                }
                posicion++;
                continue;
            }

            // Detectar números
            if (Character.isDigit(actual)) {
                extraerNumero();
                continue;
            }

            // Detectar identificadores y palabras reservadas
            if (Character.isLetter(actual) || actual == '_') {
                extraerPalabra();
                continue;
            }

            // Detectar strings (entre comillas)
            if (actual == '"' || actual == '\'') {
                extraerString(actual);
                continue;
            }

            // Detectar teclas ($ seguido de nombre de tecla)
            if (actual == '$') {
                extraerTecla();
                continue;
            }

            // Detectar operadores y delimitadores
            extraerOperadorDelimitador();
        }

        return tokens;
    }

    private void extraerNumero() {
        int colInicio = columna;
        StringBuilder numero = new StringBuilder();

        while (posicion < entrada.length() && Character.isDigit(entrada.charAt(posicion))) {
            numero.append(entrada.charAt(posicion));
            posicion++;
            columna++;
        }

        tokens.add(new Token(TipoToken.NUMERO, numero.toString(), fila, colInicio));
    }

    private void extraerPalabra() {
        int colInicio = columna;
        StringBuilder palabra = new StringBuilder();

        while (posicion < entrada.length() &&
                (Character.isLetterOrDigit(entrada.charAt(posicion)) || entrada.charAt(posicion) == '_')) {
            palabra.append(entrada.charAt(posicion));
            posicion++;
            columna++;
        }

        String lexema = palabra.toString();
        if (esReservada(lexema)) {
            tokens.add(new Token(TipoToken.RESERVADA, lexema, fila, colInicio));
        } else {
            // Buscar sugerencia si parece una reservada mal escrita
            String sugerencia = TablaErrores.buscarSugerencia(lexema);
            if (sugerencia != null) {
                TablaErrores.agregarError(ErrorInfo.TipoError.ADVERTENCIA,
                    "'" + lexema + "' no es una palabra reservada",
                    "¿Quisiste decir '" + sugerencia + "'?", fila, colInicio);
            }
            tokens.add(new Token(TipoToken.IDENTIFICADOR, lexema, fila, colInicio));
        }
    }

    private void extraerString(char delimitador) {
        int colInicio = columna;
        posicion++; // Saltar comilla inicial
        columna++;
        StringBuilder contenido = new StringBuilder();

        while (posicion < entrada.length() && entrada.charAt(posicion) != delimitador) {
            contenido.append(entrada.charAt(posicion));
            posicion++;
            columna++;
        }

        if (posicion < entrada.length()) {
            posicion++; // Saltar comilla final
            columna++;
        }

        tokens.add(new Token(TipoToken.TEXTO, contenido.toString(), fila, colInicio));
    }

    private void extraerTecla() {
        int colInicio = columna;
        posicion++; // Saltar el $
        columna++;

        StringBuilder tecla = new StringBuilder();
        while (posicion < entrada.length() &&
                (Character.isLetterOrDigit(entrada.charAt(posicion)))) {
            tecla.append(entrada.charAt(posicion));
            posicion++;
            columna++;
        }

        String nombreTecla = tecla.toString();
        if (TablaSimbolos.obtenerTeclaId(nombreTecla) >= 0) {
            tokens.add(new Token(TipoToken.TECLA, nombreTecla, fila, colInicio));
        } else {
            String sugerencia = TablaErrores.buscarSugerenciaTecla(nombreTecla);
            String msg = "Tecla no reconocida: $" + nombreTecla;
            String sug = sugerencia != null ? "¿Quisiste decir $" + sugerencia + "?" : null;
            TablaErrores.agregarError(ErrorInfo.TipoError.LEXICO, msg, sug, fila, colInicio);
            throw new RuntimeException(msg + (sug != null ? " (" + sug + ")" : "") +
                    " en fila " + fila + ", columna " + colInicio);
        }
    }

    private void extraerOperadorDelimitador() {
        int colInicio = columna;
        char actual = entrada.charAt(posicion);
        String lexema = String.valueOf(actual);

        // Detectar operadores de dos caracteres si aplica
        if (posicion + 1 < entrada.length()) {
            String dosChars = actual + entrada.substring(posicion + 1, posicion + 2);
            if (esOperador(dosChars)) {
                lexema = dosChars;
                posicion++;
                columna++;
            }
        }

        TipoToken tipo = esDelimitador(lexema) ? TipoToken.DELIMITADOR : TipoToken.OPERADOR;
        tokens.add(new Token(tipo, lexema, fila, colInicio));
        posicion++;
        columna++;
    }

    private boolean esReservada(String palabra) {
        for (String lexema : TablaSimbolos.LEXEMAS) {
            if (lexema.equals(palabra)) {
                return true;
            }
        }
        return false;
    }

    private boolean esOperador(String lexema) {
        for (String op : TablaSimbolos.OPERADORES) {
            if (op.equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    private boolean esDelimitador(String lexema) {
        for (String delim : TablaSimbolos.DELIMITADORES) {
            if (delim.equals(lexema)) {
                return true;
            }
        }
        return false;
    }
}
