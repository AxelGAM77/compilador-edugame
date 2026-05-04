package edugame.ast;

import java.util.List;

public class NodoAlClick extends NodoAST {
    private final List<NodoAST> cuerpo;

    public NodoAlClick(List<NodoAST> cuerpo, int linea) {
        super(linea);
        this.cuerpo = cuerpo;
    }

    public List<NodoAST> getCuerpo() { return cuerpo; }

    @Override
    public String getTipo() { return "AL_CLICK"; }
}