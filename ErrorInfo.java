public class ErrorInfo {

    public enum TipoError {
        LEXICO, SINTACTICO, SEMANTICO, ADVERTENCIA
    }

    private TipoError tipo;
    private String mensaje;
    private String sugerencia;
    private int fila;
    private int columna;

    public ErrorInfo(TipoError tipo, String mensaje, int fila, int columna) {
        this(tipo, mensaje, null, fila, columna);
    }

    public ErrorInfo(TipoError tipo, String mensaje, String sugerencia, int fila, int columna) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.sugerencia = sugerencia;
        this.fila = fila;
        this.columna = columna;
    }

    public TipoError getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public String getSugerencia() { return sugerencia; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }

    public boolean tieneSugerencia() { return sugerencia != null && !sugerencia.isEmpty(); }
}
