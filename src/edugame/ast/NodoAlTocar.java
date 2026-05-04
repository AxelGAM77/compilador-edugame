package edugame.ast;

import java.util.List;

public class NodoAlTocar extends NodoAST {
    private final String entidad1;
    private final String entidad2;
    private final List<NodoAST> cuerpo;

    public NodoAlTocar(String entidad1, String entidad2, List<NodoAST> cuerpo, int linea) {
        super(linea);
        this.entidad1 = entidad1;
        this.entidad2 = entidad2;
        this.cuerpo = cuerpo;
    }

    public String getEntidad1() { return entidad1; }
    public String getEntidad2() { return entidad2; }
    public List<NodoAST> getCuerpo() { return cuerpo; }

    @Override
    public String getTipo() { return "AL_TOCAR"; }
}