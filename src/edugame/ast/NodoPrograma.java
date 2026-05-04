package edugame.ast;

import java.util.List;

public class NodoPrograma extends NodoAST {
    private final String nombre;
    private final int ancho;
    private final int alto;
    private final List<NodoAST> declaraciones;
    private final List<NodoAST> cuerpo;

    public NodoPrograma(String nombre, int ancho, int alto, List<NodoAST> declaraciones, List<NodoAST> cuerpo, int linea) {
        super(linea);
        this.nombre = nombre;
        this.ancho = ancho;
        this.alto = alto;
        this.declaraciones = declaraciones;
        this.cuerpo = cuerpo;
    }

    public String getNombre() { return nombre; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public List<NodoAST> getDeclaraciones() { return declaraciones; }
    public List<NodoAST> getCuerpo() { return cuerpo; }

    @Override
    public String getTipo() { return "PROGRAMA"; }
}