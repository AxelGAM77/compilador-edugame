package edugame.error;

public class ErrorEduGame extends RuntimeException {
    protected final int linea;
    protected final int columna;

    public ErrorEduGame(String msg, int linea, int columna) {
        super(msg);
        this.linea = linea;
        this.columna = columna;
    }

    public int getLinea() { return linea; }
    public int getColumna() { return columna; }

    @Override
    public String toString() {
        return String.format("Error L%d:C%d → %s", linea, columna, getMessage());
    }
}