package edugame.ast;

public class NodoReproducir extends NodoAST {
    private final String sonido;

    public NodoReproducir(String sonido, int linea) {
        super(linea);
        this.sonido = sonido;
    }

    public String getSonido() { return sonido; }

    @Override
    public String getTipo() { return "REPRODUCIR"; }
}