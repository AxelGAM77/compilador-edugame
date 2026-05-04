package edugame.ast;

public class NodoDeclaracionObjeto extends NodoAST {
    private final String nombre;
    private final String imagen;
    private final int x;
    private final int y;

    public NodoDeclaracionObjeto(String nombre, String imagen, int x, int y, int linea) {
        super(linea);
        this.nombre = nombre;
        this.imagen = imagen;
        this.x = x;
        this.y = y;
    }

    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String getTipo() { return "OBJETO"; }
}