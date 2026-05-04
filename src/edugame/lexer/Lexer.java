package edugame.lexer;

import edugame.error.ErrorLexico;
import edugame.token.TipoToken;
import edugame.token.Token;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String fuente;
    private int pos;
    private int linea;
    private int columna;
    private final int longitud;

    public Lexer(String fuente) {
        this.fuente = fuente;
        this.longitud = fuente.length();
        this.pos = 0;
        this.linea = 1;
        this.columna = 1;
    }

    public List<Token> tokenizar() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = siguienteToken();
            if (t != null) tokens.add(t);
        } while (t == null || t.getTipo() != TipoToken.FIN_ARCHIVO);
        return tokens;
    }

    private Token siguienteToken() {
        while (pos < longitud) {
            char c = fuente.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\r') {
                consume();
                continue;
            }
            if (c == '\n') {
                nuevaLinea();
                continue;
            }
            break;
        }

        if (pos >= longitud) {
            return new Token(TipoToken.FIN_ARCHIVO, "EOF", linea, columna);
        }

        int lineaInicio = linea;
        int columnaInicio = columna;
        char c = fuente.charAt(pos);

        if (c == '/' && siguiente() == '/') {
            return comentario();
        }

        if (c == '"') {
            return cadena();
        }

        if (c == ';') {
            consume();
            return new Token(TipoToken.PUNTO_COMA, ";", lineaInicio, columnaInicio);
        }

        if (esDigito(c)) {
            return numero();
        }

        if (esLetra(c) || c == '_') {
            return palabra();
        }

        if (c == '>') {
            consume();
            if (siguiente() == '=') {
                consume();
                return new Token(TipoToken.OP_MAYOR_IGUAL, ">=", lineaInicio, columnaInicio);
            }
            return new Token(TipoToken.OP_MAYOR, ">", lineaInicio, columnaInicio);
        }

        if (c == '<') {
            consume();
            if (siguiente() == '=') {
                consume();
                return new Token(TipoToken.OP_MENOR_IGUAL, "<=", lineaInicio, columnaInicio);
            }
            return new Token(TipoToken.OP_MENOR, "<", lineaInicio, columnaInicio);
        }

        Token simple = operadorSimple(c, lineaInicio, columnaInicio);
        if (simple != null) return simple;

        Token simbolo = simboloSimple(c, lineaInicio, columnaInicio);
        if (simbolo != null) return simbolo;

        consume();
        throw new ErrorLexico("Carácter no reconocido: '" + c + "'", lineaInicio, columnaInicio);
    }

    private Token comentario() {
        consume();
        consume();
        while (pos < longitud && fuente.charAt(pos) != '\n') {
            consume();
        }
        return null;
    }

    private Token cadena() {
        int lineaInicio = linea;
        int columnaInicio = columna;
        consume();
        StringBuilder sb = new StringBuilder();
        while (pos < longitud) {
            char c = fuente.charAt(pos);
            if (c == '"') {
                consume();
                return new Token(TipoToken.CADENA, sb.toString(), lineaInicio, columnaInicio);
            }
            if (c == '\n') {
                throw new ErrorLexico("Cadena de texto no cerrada", lineaInicio, columnaInicio);
            }
            sb.append(c);
            consume();
        }
        throw new ErrorLexico("Cadena de texto no cerrada", lineaInicio, columnaInicio);
    }

    private Token numero() {
        int lineaInicio = linea;
        int columnaInicio = columna;
        StringBuilder sb = new StringBuilder();
        while (pos < longitud && esDigito(fuente.charAt(pos))) {
            sb.append(fuente.charAt(pos));
            consume();
        }
        
        // Manejar formato NxN para dimensiones (ej: 800x600)
        if (pos < longitud && (fuente.charAt(pos) == 'x' || fuente.charAt(pos) == 'X')) {
            char xChar = fuente.charAt(pos);
            consume(); // consumir la 'x'
            sb.append(xChar);
            // No hay espacio entre número y 'x' - es dimensiones
            while (pos < longitud && esDigito(fuente.charAt(pos))) {
                sb.append(fuente.charAt(pos));
                consume();
            }
            return new Token(TipoToken.DIMENSION, sb.toString(), lineaInicio, columnaInicio);
        }
        
        if (pos < longitud && fuente.charAt(pos) == '.' && pos + 1 < longitud && esDigito(fuente.charAt(pos + 1))) {
            sb.append('.');
            consume();
            while (pos < longitud && esDigito(fuente.charAt(pos))) {
                sb.append(fuente.charAt(pos));
                consume();
            }
            return new Token(TipoToken.NUMERO_DECIMAL, sb.toString(), lineaInicio, columnaInicio);
        }
        return new Token(TipoToken.NUMERO_ENTERO, sb.toString(), lineaInicio, columnaInicio);
    }

    private Token palabra() {
        int lineaInicio = linea;
        int columnaInicio = columna;
        StringBuilder sb = new StringBuilder();
        while (pos < longitud) {
            char c = fuente.charAt(pos);
            if (esLetra(c) || esDigito(c) || c == '_') {
                sb.append(c);
                consume();
            } else {
                break;
            }
        }
        String lexema = sb.toString().toUpperCase();
        TipoToken tipo = BuscadorLexico.buscarPalabraReservada(lexema);
        if (tipo != null) {
            return new Token(tipo, lexema, lineaInicio, columnaInicio);
        }
        return new Token(TipoToken.IDENTIFICADOR, sb.toString(), lineaInicio, columnaInicio);
    }

    private Token operadorSimple(char c, int lin, int col) {
        consume();
        switch (c) {
            case '+': return new Token(TipoToken.OP_SUMA, "+", lin, col);
            case '-': return new Token(TipoToken.OP_RESTA, "-", lin, col);
            case '*': return new Token(TipoToken.OP_MULT, "*", lin, col);
            case '/': return new Token(TipoToken.OP_DIV, "/", lin, col);
            case '=': return new Token(TipoToken.OP_IGUAL, "=", lin, col);
        }
        return null;
    }

    private Token simboloSimple(char c, int lin, int col) {
        consume();
        switch (c) {
            case '(': return new Token(TipoToken.PARENTESIS_ABR, "(", lin, col);
            case ')': return new Token(TipoToken.PARENTESIS_CIE, ")", lin, col);
            case ',': return new Token(TipoToken.COMA, ",", lin, col);
            case ':': return new Token(TipoToken.DOS_PUNTOS, ":", lin, col);
        }
        return null;
    }

    private char actual() {
        return pos < longitud ? fuente.charAt(pos) : '\0';
    }

    private char siguiente() {
        return (pos + 1) < longitud ? fuente.charAt(pos + 1) : '\0';
    }

    private char consume() {
        char c = fuente.charAt(pos);
        pos++;
        columna++;
        return c;
    }

    private void nuevaLinea() {
        if (pos < longitud && fuente.charAt(pos) == '\n') {
            pos++;
            linea++;
            columna = 1;
        }
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
               c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' || c == 'ü' ||
               c == 'ñ' || c == 'Á' || c == 'É' || c == 'Í' || c == 'Ó' || c == 'Ú' || c == 'Ü' || c == 'Ñ';
    }
}