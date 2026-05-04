package edugame.ast;

public class NodoDeclaracionFondo extends NodoAST {
    private final String imagen;

    public NodoDeclaracionFondo(String imagen, int linea) {
        super(linea);
        this.imagen = imagen;
    }

    public String getImagen() { return imagen; }

    @Override
    public String getTipo() { return "FONDO"; }
}