package edugame.ast;

public class NodoDeclaracionSonido extends NodoAST {
    private final String nombre;
    private final String archivo;

    public NodoDeclaracionSonido(String nombre, String archivo, int linea) {
        super(linea);
        this.nombre = nombre;
        this.archivo = archivo;
    }

    public String getNombre() { return nombre; }
    public String getArchivo() { return archivo; }

    @Override
    public String getTipo() { return "SONIDO"; }
}