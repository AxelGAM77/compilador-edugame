package edugame.ast;

public class NodoMostrar extends NodoAST {
    private final String mensaje;
    private final Integer x;
    private final Integer y;

    public NodoMostrar(String mensaje, Integer x, Integer y, int linea) {
        super(linea);
        this.mensaje = mensaje;
        this.x = x;
        this.y = y;
    }

    public String getMensaje() { return mensaje; }
    public Integer getX() { return x; }
    public Integer getY() { return y; }

    @Override
    public String getTipo() { return "MOSTRAR"; }
}