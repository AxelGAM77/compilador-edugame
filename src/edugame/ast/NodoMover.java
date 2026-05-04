package edugame.ast;

public class NodoMover extends NodoAST {
    private final String entidad;
    private final String direccion;
    private final int cantidad;

    public NodoMover(String entidad, String direccion, int cantidad, int linea) {
        super(linea);
        this.entidad = entidad;
        this.direccion = direccion;
        this.cantidad = cantidad;
    }

    public String getEntidad() { return entidad; }
    public String getDireccion() { return direccion; }
    public int getCantidad() { return cantidad; }

    @Override
    public String getTipo() { return "MOVER"; }
}