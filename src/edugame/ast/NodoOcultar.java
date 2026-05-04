package edugame.ast;

public class NodoOcultar extends NodoAST {
    private final String entidad;

    public NodoOcultar(String entidad, int linea) {
        super(linea);
        this.entidad = entidad;
    }

    public String getEntidad() { return entidad; }

    @Override
    public String getTipo() { return "OCULTAR"; }
}