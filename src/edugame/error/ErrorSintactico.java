package edugame.error;

public class ErrorSintactico extends ErrorEduGame {
    public ErrorSintactico(String msg, int linea, int columna) {
        super("[SINTACTICO] " + msg, linea, columna);
    }
}