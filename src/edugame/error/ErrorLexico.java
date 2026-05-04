package edugame.error;

public class ErrorLexico extends ErrorEduGame {
    public ErrorLexico(String msg, int linea, int columna) {
        super("[LEXICO] " + msg, linea, columna);
    }
}