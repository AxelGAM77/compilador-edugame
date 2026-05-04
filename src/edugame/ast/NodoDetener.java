package edugame.ast;

public class NodoDetener extends NodoAST {
    private final String sonido;

    public NodoDetener(String sonido, int linea) {
        super(linea);
        this.sonido = sonido;
    }

    public String getSonido() { return sonido; }

    @Override
    public String getTipo() { return "DETENER"; }
}