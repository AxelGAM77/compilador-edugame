package edugame.ast;

import java.util.List;

public class NodoSi extends NodoAST {
    private final String condicion;
    private final List<NodoAST> cuerpoSi;
    private final List<NodoAST> cuerpoSino;

    public NodoSi(String condicion, List<NodoAST> cuerpoSi, List<NodoAST> cuerpoSino, int linea) {
        super(linea);
        this.condicion = condicion;
        this.cuerpoSi = cuerpoSi;
        this.cuerpoSino = cuerpoSino;
    }

    public String getCondicion() { return condicion; }
    public List<NodoAST> getCuerpoSi() { return cuerpoSi; }
    public List<NodoAST> getCuerpoSino() { return cuerpoSino; }

    @Override
    public String getTipo() { return "SI"; }
}