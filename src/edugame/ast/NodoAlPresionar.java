package edugame.ast;

import java.util.List;

public class NodoAlPresionar extends NodoAST {
    private final String tecla;
    private final List<NodoAST> cuerpo;

    public NodoAlPresionar(String tecla, List<NodoAST> cuerpo, int linea) {
        super(linea);
        this.tecla = tecla;
        this.cuerpo = cuerpo;
    }

    public String getTecla() { return tecla; }
    public List<NodoAST> getCuerpo() { return cuerpo; }

    @Override
    public String getTipo() { return "AL_PRESIONAR"; }
}