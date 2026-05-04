package edugame.ast;

import java.util.List;

public class NodoGuardar extends NodoAST {
    private final String variable;
    private final String expresion;

    public NodoGuardar(String variable, String expresion, int linea) {
        super(linea);
        this.variable = variable;
        this.expresion = expresion;
    }

    public String getVariable() { return variable; }
    public String getExpresion() { return expresion; }

    @Override
    public String getTipo() { return "GUARDAR"; }
}