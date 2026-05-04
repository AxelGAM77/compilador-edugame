package edugame.parser;

import edugame.ast.*;
import edugame.error.ErrorSintactico;
import edugame.token.ClasificadorToken;
import edugame.token.TipoToken;
import edugame.token.Token;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int cursor;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.cursor = 0;
    }

    public NodoPrograma parsear() {
        return parsearPrograma();
    }

    private Token actual() { return tokens.get(cursor); }
    private TipoToken tipoActual() { return actual().getTipo(); }

    private boolean verificar(TipoToken tipo) { return tipoActual() == tipo; }

    private Token consumir(TipoToken esperado) {
        Token t = actual();
        if (t.getTipo() == esperado) {
            cursor++;
            return t;
        }
        throw new ErrorSintactico(
            "Se esperaba '" + esperado + "' pero se encontró '" + t.getLexema() + "'",
            t.getLinea(), t.getColumna()
        );
    }

    private void terminarInstruccion() {
        consumir(TipoToken.PUNTO_COMA);
    }

private NodoPrograma parsearPrograma() {
        Token juego = consumir(TipoToken.JUEGO);
        Token nombre = consumir(TipoToken.IDENTIFICADOR);
        
        // El formato es 800x600 - el lexer ya lo tokeniza como DIMENSION
        Token dimTok = consumir(TipoToken.DIMENSION);
        String dim = dimTok.getLexema();
        String[] partes = dim.split("x");
        int ancho = Integer.parseInt(partes[0]);
        int alto = Integer.parseInt(partes[1]);

        List<NodoAST> declaraciones = new ArrayList<>();
        List<NodoAST> cuerpo = new ArrayList<>();

        while (!verificar(TipoToken.FIN) && !verificar(TipoToken.FIN_ARCHIVO)) {
            NodoAST nodo = parsearSentencia();
            if (nodo != null) {
                if (nodo instanceof NodoDeclaracionPersonaje ||
                    nodo instanceof NodoDeclaracionObjeto ||
                    nodo instanceof NodoDeclaracionSonido ||
                    nodo instanceof NodoDeclaracionFondo) {
                    declaraciones.add(nodo);
                } else {
                    cuerpo.add(nodo);
                }
            }
        }

        if (verificar(TipoToken.FIN)) {
            cursor++;
        }

        return new NodoPrograma(nombre.getLexema(), ancho, alto, declaraciones, cuerpo, juego.getLinea());
    }

    private NodoAST parsearSentencia() {
        TipoToken tipo = tipoActual();

        if (tipo == TipoToken.PERSONAJE) return parsearPersonaje();
        if (tipo == TipoToken.OBJETO) return parsearObjeto();
        if (tipo == TipoToken.SONIDO) return parsearSonido();
        if (tipo == TipoToken.FONDO) return parsearFondo();
        if (tipo == TipoToken.MOVER) return parsearMover();
        if (tipo == TipoToken.MOSTRAR) return parsearMostrar();
        if (tipo == TipoToken.OCULTAR) return parsearOcultar();
        if (tipo == TipoToken.REPRODUCIR) return parsearReproducir();
        if (tipo == TipoToken.DETENER) return parsearDetener();
        if (tipo == TipoToken.GUARDAR) return parsearGuardar();
        if (tipo == TipoToken.SI) return parsearSi();
        if (tipo == TipoToken.BUCLE) return parsearBucle();
        if (tipo == TipoToken.AL_PRESIONAR) return parsearAlPresionar();
        if (tipo == TipoToken.AL_SOLTAR) return parsearAlSoltar();
        if (tipo == TipoToken.AL_CLICK) return parsearAlClick();
        if (tipo == TipoToken.AL_TOCAR) return parsearAlTocar();

        if (verificar(TipoToken.FIN) || verificar(TipoToken.FIN_ARCHIVO)) {
            return null;
        }

        Token t = actual();
        throw new ErrorSintactico(
            "Se esperaba una instrucción o bloque pero se encontró '" + t.getLexema() + "'",
            t.getLinea(), t.getColumna()
        );
    }

    private NodoDeclaracionPersonaje parsearPersonaje() {
        Token kw = consumir(TipoToken.PERSONAJE);
        Token nombre = consumir(TipoToken.IDENTIFICADOR);
        consumir(TipoToken.IMAGEN);
        Token imagen = consumir(TipoToken.CADENA);
        consumir(TipoToken.EN);
        Token x = consumir(TipoToken.NUMERO_ENTERO);
        Token y = consumir(TipoToken.NUMERO_ENTERO);
        terminarInstruccion();
        return new NodoDeclaracionPersonaje(
            nombre.getLexema(),
            imagen.getLexema(),
            Integer.parseInt(x.getLexema()),
            Integer.parseInt(y.getLexema()),
            kw.getLinea()
        );
    }

    private NodoDeclaracionObjeto parsearObjeto() {
        Token kw = consumir(TipoToken.OBJETO);
        Token nombre = consumir(TipoToken.IDENTIFICADOR);
        consumir(TipoToken.IMAGEN);
        Token imagen = consumir(TipoToken.CADENA);
        consumir(TipoToken.EN);
        Token x = consumir(TipoToken.NUMERO_ENTERO);
        Token y = consumir(TipoToken.NUMERO_ENTERO);
        terminarInstruccion();
        return new NodoDeclaracionObjeto(
            nombre.getLexema(),
            imagen.getLexema(),
            Integer.parseInt(x.getLexema()),
            Integer.parseInt(y.getLexema()),
            kw.getLinea()
        );
    }

    private NodoDeclaracionSonido parsearSonido() {
        Token kw = consumir(TipoToken.SONIDO);
        Token nombre = consumir(TipoToken.IDENTIFICADOR);
        consumir(TipoToken.ARCHIVO);
        Token archivo = consumir(TipoToken.CADENA);
        terminarInstruccion();
        return new NodoDeclaracionSonido(nombre.getLexema(), archivo.getLexema(), kw.getLinea());
    }

    private NodoDeclaracionFondo parsearFondo() {
        Token kw = consumir(TipoToken.FONDO);
        consumir(TipoToken.IMAGEN);
        Token imagen = consumir(TipoToken.CADENA);
        terminarInstruccion();
        return new NodoDeclaracionFondo(imagen.getLexema(), kw.getLinea());
    }

    private NodoMover parsearMover() {
        Token kw = consumir(TipoToken.MOVER);
        Token entidad = consumir(TipoToken.IDENTIFICADOR);
        Token direccion = consumirDireccion();
        Token cantidad = consumir(TipoToken.NUMERO_ENTERO);
        terminarInstruccion();
        return new NodoMover(
            entidad.getLexema(),
            direccion.getLexema(),
            Integer.parseInt(cantidad.getLexema()),
            kw.getLinea()
        );
    }

    private NodoMostrar parsearMostrar() {
        Token kw = consumir(TipoToken.MOSTRAR);
        Token msg = consumir(TipoToken.CADENA);
        Integer x = null;
        Integer y = null;
        if (verificar(TipoToken.EN)) {
            consumir(TipoToken.EN);
            Token xTok = consumir(TipoToken.NUMERO_ENTERO);
            Token yTok = consumir(TipoToken.NUMERO_ENTERO);
            x = Integer.parseInt(xTok.getLexema());
            y = Integer.parseInt(yTok.getLexema());
        }
        terminarInstruccion();
        return new NodoMostrar(msg.getLexema(), x, y, kw.getLinea());
    }

    private NodoOcultar parsearOcultar() {
        Token kw = consumir(TipoToken.OCULTAR);
        Token entidad = consumir(TipoToken.IDENTIFICADOR);
        terminarInstruccion();
        return new NodoOcultar(entidad.getLexema(), kw.getLinea());
    }

    private NodoReproducir parsearReproducir() {
        Token kw = consumir(TipoToken.REPRODUCIR);
        Token sonido = consumir(TipoToken.IDENTIFICADOR);
        terminarInstruccion();
        return new NodoReproducir(sonido.getLexema(), kw.getLinea());
    }

    private NodoDetener parsearDetener() {
        Token kw = consumir(TipoToken.DETENER);
        Token sonido = consumir(TipoToken.IDENTIFICADOR);
        terminarInstruccion();
        return new NodoDetener(sonido.getLexema(), kw.getLinea());
    }

    private NodoGuardar parsearGuardar() {
        Token kw = consumir(TipoToken.GUARDAR);
        Token variable = consumir(TipoToken.IDENTIFICADOR);
        consumir(TipoToken.OP_IGUAL);
        Token valor = consumir(TipoToken.NUMERO_ENTERO);
        terminarInstruccion();
        return new NodoGuardar(variable.getLexema(), valor.getLexema(), kw.getLinea());
    }

    private Token consumirDireccion() {
        TipoToken tipo = tipoActual();
        if (tipo == TipoToken.DERECHA || tipo == TipoToken.IZQUIERDA ||
            tipo == TipoToken.ARRIBA || tipo == TipoToken.ABAJO) {
            return consumir(tipo);
        }
        Token t = actual();
        throw new ErrorSintactico(
            "Se esperaba una dirección (DERECHA, IZQUIERDA, ARRIBA, ABAJO)",
            t.getLinea(), t.getColumna()
        );
    }

    private NodoSi parsearSi() {
        Token si = consumir(TipoToken.SI);
        
        // Parsear condición: identificador op valor
        Token izq = consumir(TipoToken.IDENTIFICADOR);
        TipoToken op = tipoActual();
        if (op == TipoToken.OP_MAYOR || op == TipoToken.OP_MENOR || op == TipoToken.OP_IGUAL ||
            op == TipoToken.OP_MAYOR_IGUAL || op == TipoToken.OP_MENOR_IGUAL) {
            cursor++;
        }
        
        Token der;
        if (verificar(TipoToken.NUMERO_ENTERO)) {
            der = consumir(TipoToken.NUMERO_ENTERO);
        } else if (verificar(TipoToken.IDENTIFICADOR)) {
            der = consumir(TipoToken.IDENTIFICADOR);
        } else {
            throw new ErrorSintactico(
                "Se esperaba un valor después del operador",
                actual().getLinea(), actual().getColumna()
            );
}
        
        List<NodoAST> cuerpoSi = new ArrayList<>();
        while (!verificar(TipoToken.FIN) && !verificar(TipoToken.SINO) && !verificar(TipoToken.FIN_ARCHIVO)) {
            NodoAST n = parsearSentencia();
            if (n != null) cuerpoSi.add(n);
        }

        List<NodoAST> cuerpoSino = null;
        if (verificar(TipoToken.SINO)) {
            cursor++;
            cuerpoSino = new ArrayList<>();
            while (!verificar(TipoToken.FIN) && !verificar(TipoToken.FIN_ARCHIVO)) {
                NodoAST n = parsearSentencia();
                if (n != null) cuerpoSino.add(n);
            }
        }

        consumir(TipoToken.FIN);
        return new NodoSi(izq.getLexema(), cuerpoSi, cuerpoSino, si.getLinea());
    }

    private NodoBucle parsearBucle() {
        Token bucle = consumir(TipoToken.BUCLE);
        List<NodoAST> cuerpo = new ArrayList<>();
        while (!verificar(TipoToken.FIN) && !verificar(TipoToken.FIN_ARCHIVO)) {
            NodoAST n = parsearSentencia();
            if (n != null) cuerpo.add(n);
        }
        consumir(TipoToken.FIN);
        return new NodoBucle(cuerpo, bucle.getLinea());
    }

    private NodoAST parsearAlPresionar() {
        Token kw = consumir(TipoToken.AL_PRESIONAR);
        consumir(TipoToken.TECLA);
        Token tecla = consumirDireccion();
        List<NodoAST> cuerpo = new ArrayList<>();
        while (!verificar(TipoToken.FIN) && !verificar(TipoToken.FIN_ARCHIVO)) {
            NodoAST n = parsearSentencia();
            if (n != null) cuerpo.add(n);
        }
        consumir(TipoToken.FIN);
        return new NodoAlPresionar(tecla.getLexema(), cuerpo, kw.getLinea());
    }

    private NodoAST parsearAlSoltar() {
        Token kw = consumir(TipoToken.AL_SOLTAR);
        consumir(TipoToken.TECLA);
        Token tecla = consumirDireccion();
        List<NodoAST> cuerpo = new ArrayList<>();
        while (!verificar(TipoToken.FIN) && !verificar(TipoToken.FIN_ARCHIVO)) {
            NodoAST n = parsearSentencia();
            if (n != null) cuerpo.add(n);
        }
        consumir(TipoToken.FIN);
        return new NodoAlSoltar(tecla.getLexema(), cuerpo, kw.getLinea());
    }

    private NodoAST parsearAlClick() {
        Token kw = consumir(TipoToken.AL_CLICK);
        List<NodoAST> cuerpo = new ArrayList<>();
        while (!verificar(TipoToken.FIN) && !verificar(TipoToken.FIN_ARCHIVO)) {
            NodoAST n = parsearSentencia();
            if (n != null) cuerpo.add(n);
        }
        consumir(TipoToken.FIN);
        return new NodoAlClick(cuerpo, kw.getLinea());
    }

    private NodoAST parsearAlTocar() {
        Token kw = consumir(TipoToken.AL_TOCAR);
        Token entidad1 = consumir(TipoToken.IDENTIFICADOR);
        Token entidad2 = consumir(TipoToken.IDENTIFICADOR);
        List<NodoAST> cuerpo = new ArrayList<>();
        while (!verificar(TipoToken.FIN) && !verificar(TipoToken.FIN_ARCHIVO)) {
            NodoAST n = parsearSentencia();
            if (n != null) cuerpo.add(n);
        }
        consumir(TipoToken.FIN);
        return new NodoAlTocar(entidad1.getLexema(), entidad2.getLexema(), cuerpo, kw.getLinea());
    }
}