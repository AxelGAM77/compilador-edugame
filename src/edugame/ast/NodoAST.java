package edugame.ast;

import java.util.ArrayList;
import java.util.List;

public abstract class NodoAST {
    private final int linea;

    public NodoAST(int linea) {
        this.linea = linea;
    }

    public int getLinea() { return linea; }

    public abstract String getTipo();
}