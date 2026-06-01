import java.util.ArrayList;
import java.util.List;

public class ParserSintactico {

    private static class ReglaTransicion {
        int estadoActual;
        String tipoToken;
        String lexemaEsperado;
        int nuevoEstado;

        ReglaTransicion(int estadoActual, String tipoToken, String lexemaEsperado, int nuevoEstado) {
            this.estadoActual = estadoActual;
            this.tipoToken = tipoToken;
            this.lexemaEsperado = lexemaEsperado;
            this.nuevoEstado = nuevoEstado;
        }

        ReglaTransicion(int estadoActual, String tipoToken, int nuevoEstado) {
            this(estadoActual, tipoToken, null, nuevoEstado);
        }
    }

    private List<ReglaTransicion> tablaDeclaracion;
    private List<ReglaTransicion> tablaAsignacion;
    private List<ReglaTransicion> tablaLlamadaFuncion;
    private List<ReglaTransicion> tablaFuncion;
    private List<ReglaTransicion> tablaBloqueSI;
    private List<ReglaTransicion> tablaBloqueDIBUJAR;
    private List<ReglaTransicion> tablaPulsa;
    private List<ReglaTransicion> tablaExpresion;

    private List<Token> tokens;
    private int posicion;

    public ParserSintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.posicion = 0;
        inicializarAutomas();
    }

    private void inicializarAutomas() {
        // Declaración: Tipo Ident [= Expr] ;
        tablaDeclaracion = new ArrayList<>();
        tablaDeclaracion.add(new ReglaTransicion(0, "RESERVADA", 1));
        tablaDeclaracion.add(new ReglaTransicion(0, "IDENTIFICADOR", 1));
        tablaDeclaracion.add(new ReglaTransicion(1, "IDENTIFICADOR", 2));
        tablaDeclaracion.add(new ReglaTransicion(2, "OPERADOR", "=", 3));
        tablaDeclaracion.add(new ReglaTransicion(2, "DELIMITADOR", ";", 5));
        tablaDeclaracion.add(new ReglaTransicion(3, "*", 4));
        tablaDeclaracion.add(new ReglaTransicion(4, "DELIMITADOR", ";", 5));

        // Asignación: Id = Expr ;
        tablaAsignacion = new ArrayList<>();
        tablaAsignacion.add(new ReglaTransicion(0, "IDENTIFICADOR", 1));
        tablaAsignacion.add(new ReglaTransicion(1, "OPERADOR", "=", 2));
        tablaAsignacion.add(new ReglaTransicion(2, "*", 3));
        tablaAsignacion.add(new ReglaTransicion(3, "DELIMITADOR", ";", 4));

        // LlamadaFunción: Id ( [args] ) ;
        tablaLlamadaFuncion = new ArrayList<>();
        tablaLlamadaFuncion.add(new ReglaTransicion(0, "IDENTIFICADOR", 1));
        tablaLlamadaFuncion.add(new ReglaTransicion(1, "DELIMITADOR", "(", 2));
        tablaLlamadaFuncion.add(new ReglaTransicion(2, "DELIMITADOR", ")", 4));
        tablaLlamadaFuncion.add(new ReglaTransicion(2, "*", 3));
        tablaLlamadaFuncion.add(new ReglaTransicion(3, "DELIMITADOR", ")", 4));
        tablaLlamadaFuncion.add(new ReglaTransicion(4, "DELIMITADOR", ";", 5));

        // Función: FUNCION [TipoRet] Nombre ( [Params] ) Cuerpo FIN
        tablaFuncion = new ArrayList<>();
        tablaFuncion.add(new ReglaTransicion(0, "RESERVADA", "FUNCION", 1));
        tablaFuncion.add(new ReglaTransicion(1, "IDENTIFICADOR", 2));
        tablaFuncion.add(new ReglaTransicion(1, "RESERVADA", 2));
        tablaFuncion.add(new ReglaTransicion(2, "IDENTIFICADOR", 3));
        tablaFuncion.add(new ReglaTransicion(2, "DELIMITADOR", "(", 4));
        tablaFuncion.add(new ReglaTransicion(3, "DELIMITADOR", "(", 4));
        tablaFuncion.add(new ReglaTransicion(4, "DELIMITADOR", ")", 6));
        tablaFuncion.add(new ReglaTransicion(4, "IDENTIFICADOR", 5));
        tablaFuncion.add(new ReglaTransicion(5, "DELIMITADOR", ")", 6));
        tablaFuncion.add(new ReglaTransicion(5, "DELIMITADOR", ",", 5));
        tablaFuncion.add(new ReglaTransicion(6, "*", 7));
        tablaFuncion.add(new ReglaTransicion(7, "RESERVADA", "FIN", 8));

        // BloqueSI: SI ( Expr ) PULSA... FIN  o  SI PULSA... FIN
        tablaBloqueSI = new ArrayList<>();
        tablaBloqueSI.add(new ReglaTransicion(0, "RESERVADA", "SI", 1));
        tablaBloqueSI.add(new ReglaTransicion(1, "DELIMITADOR", "(", 2));
        tablaBloqueSI.add(new ReglaTransicion(1, "RESERVADA", "PULSA", 5));
        tablaBloqueSI.add(new ReglaTransicion(2, "*", 3));
        tablaBloqueSI.add(new ReglaTransicion(3, "DELIMITADOR", ")", 4));
        tablaBloqueSI.add(new ReglaTransicion(4, "RESERVADA", "PULSA", 5));
        tablaBloqueSI.add(new ReglaTransicion(5, "RESERVADA", "FIN", 6));

        // BloqueDIBUJAR: DIBUJAR [instrucciones] FIN
        tablaBloqueDIBUJAR = new ArrayList<>();
        tablaBloqueDIBUJAR.add(new ReglaTransicion(0, "RESERVADA", "DIBUJAR", 1));
        tablaBloqueDIBUJAR.add(new ReglaTransicion(1, "RESERVADA", "FIN", 3));
        tablaBloqueDIBUJAR.add(new ReglaTransicion(1, "*", 2));
        tablaBloqueDIBUJAR.add(new ReglaTransicion(2, "RESERVADA", "FIN", 3));

        // Pulsa: PULSA $Tecla Accion ;
        tablaPulsa = new ArrayList<>();
        tablaPulsa.add(new ReglaTransicion(0, "RESERVADA", "PULSA", 1));
        tablaPulsa.add(new ReglaTransicion(1, "TECLA", 2));
        tablaPulsa.add(new ReglaTransicion(2, "RESERVADA", "MOVER", 3));
        tablaPulsa.add(new ReglaTransicion(2, "RESERVADA", "INTERACTUAR", 3));
        tablaPulsa.add(new ReglaTransicion(2, "IDENTIFICADOR", 3));
        tablaPulsa.add(new ReglaTransicion(3, "DELIMITADOR", ";", 4));

        // Expresión: valor [op expr]
        tablaExpresion = new ArrayList<>();
        tablaExpresion.add(new ReglaTransicion(0, "NUMERO", 1));
        tablaExpresion.add(new ReglaTransicion(0, "TEXTO", 1));
        tablaExpresion.add(new ReglaTransicion(0, "IDENTIFICADOR", 1));
        tablaExpresion.add(new ReglaTransicion(0, "TECLA", 1));
        tablaExpresion.add(new ReglaTransicion(0, "RESERVADA", "Nuevo", 1));
        tablaExpresion.add(new ReglaTransicion(0, "DELIMITADOR", "(", 1));
        tablaExpresion.add(new ReglaTransicion(1, "OPERADOR", 2));
        tablaExpresion.add(new ReglaTransicion(2, "*", 1));
        tablaExpresion.add(new ReglaTransicion(1, "*", 3));
    }

    private boolean verificarTransicion(ReglaTransicion regla) {
        Token tok = actual();
        if (tok == null) return false;
        if (!regla.tipoToken.equals("*") && !tok.getTipo().toString().equals(regla.tipoToken)) {
            return false;
        }
        if (regla.lexemaEsperado != null && !tok.getLexema().equals(regla.lexemaEsperado)) {
            return false;
        }
        return true;
    }

    private int buscarTransicion(List<ReglaTransicion> tabla, int estadoActual) {
        for (ReglaTransicion regla : tabla) {
            if (regla.estadoActual == estadoActual && verificarTransicion(regla)) {
                return regla.nuevoEstado;
            }
        }
        return -1;
    }

    private Token actual() {
        if (posicion < tokens.size()) return tokens.get(posicion);
        return null;
    }

    private Token consumir() {
        if (posicion < tokens.size()) return tokens.get(posicion++);
        return null;
    }

    private boolean coincidir(String tipo, String lexema) {
        Token tok = actual();
        if (tok == null) return false;
        boolean tipoOk = tok.getTipo().toString().equals(tipo);
        if (lexema == null) return tipoOk;
        return tipoOk && tok.getLexema().equals(lexema);
    }

    private boolean esTipoDato(String lexema) {
        return TablaSimbolos.esTipoDato(lexema);
    }

    private boolean esInicioStatement() {
        Token tok = actual();
        if (tok == null) return false;
        if (tok.getTipo() == TipoToken.IDENTIFICADOR) return true;
        if (tok.getTipo() == TipoToken.RESERVADA) {
            String lex = tok.getLexema();
            return esTipoDato(lex) || lex.equals("SI") || lex.equals("DIBUJAR") ||
                   lex.equals("PULSA") || lex.equals("REGRESAR");
        }
        return false;
    }

    private boolean esExpresionInicio() {
        Token tok = actual();
        if (tok == null) return false;
        if (tok.getTipo() == TipoToken.NUMERO || tok.getTipo() == TipoToken.TEXTO ||
            tok.getTipo() == TipoToken.IDENTIFICADOR || tok.getTipo() == TipoToken.TECLA) {
            return true;
        }
        if (tok.getTipo() == TipoToken.DELIMITADOR && tok.getLexema().equals("(")) return true;
        if (tok.getTipo() == TipoToken.RESERVADA && tok.getLexema().equals("Nuevo")) return true;
        return false;
    }

    // ==================== PROGRAMA ====================

    public NodoAST parsear() {
        NodoAST programa = new NodoAST("Programa");

        if (!coincidir("RESERVADA", "JUEGO")) {
            throw new RuntimeException("Se esperaba 'JUEGO' al inicio, fila: " +
                (actual() != null ? actual().getFila() : "fin"));
        }
        consumir();

        if (!coincidir("IDENTIFICADOR", null)) {
            throw new RuntimeException("Se esperaba identificador después de 'JUEGO', fila: " +
                (actual() != null ? actual().getFila() : "fin"));
        }

        programa.agregarHijo(parsearJuego());

        while (posicion < tokens.size() && coincidir("RESERVADA", "JUEGO")) {
            consumir();
            if (!coincidir("IDENTIFICADOR", null)) {
                throw new RuntimeException("Se esperaba identificador después de 'JUEGO'");
            }
            programa.agregarHijo(parsearJuego());
        }

        return programa;
    }

    // ==================== BLOQUE JUEGO ====================

    private NodoAST parsearJuego() {
        NodoAST juego = new NodoAST("Juego");
        Token nombre = consumir();
        juego.agregarHijo(new NodoAST("Identificador", nombre.getLexema()));

        // INICIAR
        if (!coincidir("RESERVADA", "INICIAR")) {
            throw new RuntimeException("Se esperaba 'INICIAR' en JUEGO, fila: " + nombre.getFila());
        }
        consumir();
        NodoAST iniciar = new NodoAST("INICIAR");
        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN") && !coincidir("RESERVADA", "ACTUALIZAR")) {
            if (esInicioStatement()) {
                iniciar.agregarHijo(parsearStatement());
            } else {
                break;
            }
        }
        if (!coincidir("RESERVADA", "FIN")) {
            throw new RuntimeException("Se esperaba 'FIN' después de INICIAR, fila: " + actual().getFila());
        }
        consumir();
        juego.agregarHijo(iniciar);

        // ACTUALIZAR
        if (!coincidir("RESERVADA", "ACTUALIZAR")) {
            throw new RuntimeException("Se esperaba 'ACTUALIZAR' en JUEGO, fila: " + actual().getFila());
        }
        consumir();
        NodoAST actualizar = new NodoAST("ACTUALIZAR");
        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN")) {
            if (coincidir("RESERVADA", "SI")) {
                actualizar.agregarHijo(parsearBloqueSI());
            } else if (coincidir("RESERVADA", "DIBUJAR")) {
                actualizar.agregarHijo(parsearBloqueDIBUJAR());
            } else if (esInicioStatement()) {
                actualizar.agregarHijo(parsearStatement());
            } else {
                break;
            }
        }
        if (!coincidir("RESERVADA", "FIN")) {
            throw new RuntimeException("Se esperaba 'FIN' después de ACTUALIZAR, fila: " + actual().getFila());
        }
        consumir();
        juego.agregarHijo(actualizar);

        // FUNCIONES
        while (posicion < tokens.size() && coincidir("RESERVADA", "FUNCION")) {
            juego.agregarHijo(parsearFuncion());
        }

        if (!coincidir("RESERVADA", "FIN")) {
            throw new RuntimeException("Se esperaba 'FIN' para cerrar JUEGO, fila: " + actual().getFila());
        }
        consumir();

        return juego;
    }

    // ==================== FUNCIÓN ====================

    private NodoAST parsearFuncion() {
        NodoAST funcion = new NodoAST("Funcion");
        consumir(); // FUNCION

        if (!coincidir("IDENTIFICADOR", null) && !esTipoDato(actual().getLexema())) {
            throw new RuntimeException("Se esperaba nombre de función o tipo de retorno, fila: " + actual().getFila());
        }

        // Tipo retorno opcional
        boolean tieneTipoRetorno = false;
        if (posicion + 1 < tokens.size() &&
            (tokens.get(posicion + 1).getTipo() == TipoToken.IDENTIFICADOR ||
             esTipoDato(tokens.get(posicion + 1).getLexema()))) {
            Token tipoRet = consumir();
            funcion.agregarHijo(new NodoAST("TipoRetorno", tipoRet.getLexema()));
            tieneTipoRetorno = true;
        }

        Token nombreFunc = consumir();
        funcion.agregarHijo(new NodoAST("NombreFuncion", nombreFunc.getLexema()));

        if (!coincidir("DELIMITADOR", "(")) {
            throw new RuntimeException("Se esperaba '(' después del nombre de función, fila: " + actual().getFila());
        }
        consumir();

        NodoAST params = new NodoAST("Parametros");
        while (!coincidir("DELIMITADOR", ")")) {
            if (params.getHijos().size() > 0) {
                if (!coincidir("DELIMITADOR", ",")) {
                    throw new RuntimeException("Se esperaba ',' entre parámetros, fila: " + actual().getFila());
                }
                consumir();
            }
            if (actual() != null && (actual().getTipo() == TipoToken.IDENTIFICADOR || esTipoDato(actual().getLexema()))) {
                params.agregarHijo(new NodoAST("Parametro", consumir().getLexema()));
            } else {
                break;
            }
        }
        if (!coincidir("DELIMITADOR", ")")) {
            throw new RuntimeException("Se esperaba ')' en parámetros, fila: " + actual().getFila());
        }
        consumir();
        funcion.agregarHijo(params);

        // Cuerpo
        NodoAST cuerpo = new NodoAST("CuerpoFuncion");
        boolean tieneRegresar = false;
        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN")) {
            if (coincidir("RESERVADA", "REGRESAR")) {
                cuerpo.agregarHijo(parsearReturn());
                tieneRegresar = true;
            } else if (esInicioStatement()) {
                cuerpo.agregarHijo(parsearStatement());
            } else {
                throw new RuntimeException("Token inesperado en función: " + actual().getLexema() +
                    ", fila: " + actual().getFila());
            }
        }

        // Si tiene tipo de retorno, REGRESAR es obligatorio
        if (tieneTipoRetorno && !tieneRegresar) {
            throw new RuntimeException("La función '" + nombreFunc.getLexema() +
                "' tiene tipo de retorno '" + funcion.getHijos().get(0).getValor() +
                "' pero no tiene REGRESAR, fila: " + nombreFunc.getFila());
        }

        if (!coincidir("RESERVADA", "FIN")) {
            throw new RuntimeException("Se esperaba 'FIN' para cerrar función, fila: " + actual().getFila());
        }
        consumir();
        funcion.agregarHijo(cuerpo);

        return funcion;
    }

    // ==================== RETURN ====================

    private NodoAST parsearReturn() {
        NodoAST ret = new NodoAST("REGRESAR");
        consumir(); // REGRESAR
        if (esExpresionInicio()) {
            ret.agregarHijo(parsearExpresion());
        }
        if (coincidir("DELIMITADOR", ";")) consumir();
        return ret;
    }

    // ==================== BLOQUE SI ====================

    private NodoAST parsearBloqueSI() {
        NodoAST si = new NodoAST("SI");
        consumir(); // SI

        if (coincidir("DELIMITADOR", "(")) {
            consumir();
            si.agregarHijo(parsearExpresion());
            if (!coincidir("DELIMITADOR", ")")) {
                throw new RuntimeException("Se esperaba ')' en SI");
            }
            consumir();
        }

        while (posicion < tokens.size() && coincidir("RESERVADA", "PULSA")) {
            si.agregarHijo(parsearPulsa());
        }

        if (!coincidir("RESERVADA", "FIN")) {
            throw new RuntimeException("Se esperaba 'FIN' para cerrar SI, fila: " + actual().getFila());
        }
        consumir();
        return si;
    }

    // ==================== BLOQUE DIBUJAR ====================

    private NodoAST parsearBloqueDIBUJAR() {
        NodoAST dibujar = new NodoAST("DIBUJAR");
        consumir(); // DIBUJAR

        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN")) {
            if (esInicioStatement()) {
                dibujar.agregarHijo(parsearStatement());
            } else {
                throw new RuntimeException("Token inesperado en DIBUJAR: " + actual().getLexema() +
                    ", fila: " + actual().getFila());
            }
        }
        if (!coincidir("RESERVADA", "FIN")) {
            throw new RuntimeException("Se esperaba 'FIN' para cerrar DIBUJAR, fila: " + actual().getFila());
        }
        consumir();
        return dibujar;
    }

    // ==================== PULSA ====================

    private NodoAST parsearPulsa() {
        NodoAST pulsa = new NodoAST("PULSA");
        consumir(); // PULSA

        if (!coincidir("TECLA", null)) {
            throw new RuntimeException("Se esperaba tecla ($X) después de PULSA, fila: " + actual().getFila());
        }
        Token tecla = consumir();
        pulsa.agregarHijo(new NodoAST("Tecla", tecla.getLexema()));

        if (coincidir("RESERVADA", "MOVER")) {
            consumir();
            NodoAST accion = new NodoAST("AccionMover");
            if (coincidir("NUMERO", null)) {
                accion.agregarHijo(new NodoAST("Cantidad", consumir().getLexema()));
            }
            if (actual() != null && (coincidir("RESERVADA", "IZQUIERDA") || coincidir("RESERVADA", "DERECHA") ||
                coincidir("RESERVADA", "ARRIBA") || coincidir("RESERVADA", "ABAJO"))) {
                accion.agregarHijo(new NodoAST("Direccion", consumir().getLexema()));
            }
            pulsa.agregarHijo(accion);
        } else if (coincidir("RESERVADA", "INTERACTUAR")) {
            consumir();
            NodoAST accion = new NodoAST("AccionInteractuar");
            if (coincidir("IDENTIFICADOR", null)) {
                accion.agregarHijo(new NodoAST("Objeto1", consumir().getLexema()));
            }
            if (coincidir("IDENTIFICADOR", null)) {
                accion.agregarHijo(new NodoAST("Objeto2", consumir().getLexema()));
            }
            pulsa.agregarHijo(accion);
        } else if (coincidir("IDENTIFICADOR", null)) {
            if (posicion + 1 < tokens.size() && tokens.get(posicion + 1).getLexema().equals("(")) {
                pulsa.agregarHijo(parsearLlamadaFuncion());
            } else {
                Token id = consumir();
                NodoAST accion = new NodoAST("AccionGeneral");
                accion.agregarHijo(new NodoAST("Objeto", id.getLexema()));
                if (coincidir("RESERVADA", "MOVER")) {
                    consumir();
                    if (coincidir("NUMERO", null)) {
                        accion.agregarHijo(new NodoAST("Cantidad", consumir().getLexema()));
                    }
                    if (actual() != null && (coincidir("RESERVADA", "IZQUIERDA") || coincidir("RESERVADA", "DERECHA") ||
                        coincidir("RESERVADA", "ARRIBA") || coincidir("RESERVADA", "ABAJO"))) {
                        accion.agregarHijo(new NodoAST("Direccion", consumir().getLexema()));
                    }
                }
                pulsa.agregarHijo(accion);
            }
        }

        if (coincidir("DELIMITADOR", ";")) consumir();
        return pulsa;
    }

    // ==================== STATEMENT ====================

    private NodoAST parsearStatement() {
        Token tok = actual();
        if (tok == null) throw new RuntimeException("Token inesperado: fin de entrada");

        if (coincidir("RESERVADA", "SI")) return parsearBloqueSI();
        if (coincidir("RESERVADA", "DIBUJAR")) return parsearBloqueDIBUJAR();
        if (coincidir("RESERVADA", "PULSA")) return parsearPulsa();
        if (coincidir("RESERVADA", "REGRESAR")) return parsearReturn();

        if (tok.getTipo() == TipoToken.RESERVADA && esTipoDato(tok.getLexema())) {
            return parsearDeclaracion();
        }

        if (tok.getTipo() == TipoToken.IDENTIFICADOR) {
            if (posicion + 1 < tokens.size() && tokens.get(posicion + 1).getLexema().equals("=")) {
                return parsearAsignacion();
            } else if (posicion + 1 < tokens.size() && tokens.get(posicion + 1).getLexema().equals("(")) {
                return parsearLlamadaFuncion();
            } else {
                throw new RuntimeException("Sentencia no válida: " + tok.getLexema() + ", fila: " + tok.getFila());
            }
        }

        throw new RuntimeException("Token inesperado: " + tok.getLexema() + ", fila: " + tok.getFila());
    }

    // ==================== DECLARACIÓN ====================

    private NodoAST parsearDeclaracion() {
        NodoAST decl = new NodoAST("Declaracion");
        Token tipo = consumir();
        decl.agregarHijo(new NodoAST("Tipo", tipo.getLexema()));

        if (!coincidir("IDENTIFICADOR", null)) {
            throw new RuntimeException("Se esperaba identificador en declaración, fila: " + tipo.getFila());
        }
        Token id = consumir();
        decl.agregarHijo(new NodoAST("Identificador", id.getLexema()));

        if (coincidir("OPERADOR", "=")) {
            consumir();
            decl.agregarHijo(parsearExpresion());
        }

        if (coincidir("DELIMITADOR", ";")) consumir();
        return decl;
    }

    // ==================== ASIGNACIÓN ====================

    private NodoAST parsearAsignacion() {
        NodoAST asig = new NodoAST("Asignacion");
        Token id = consumir();
        asig.agregarHijo(new NodoAST("Identificador", id.getLexema()));

        if (!coincidir("OPERADOR", "=")) {
            throw new RuntimeException("Se esperaba '=' en asignación, fila: " + id.getFila());
        }
        consumir();

        asig.agregarHijo(parsearExpresion());
        if (coincidir("DELIMITADOR", ";")) consumir();
        return asig;
    }

    // ==================== LLAMADA FUNCIÓN ====================

    private NodoAST parsearLlamadaFuncion() {
        NodoAST llamada = new NodoAST("LlamadaFuncion");
        Token id = consumir();
        llamada.agregarHijo(new NodoAST("NombreFuncion", id.getLexema()));

        if (!coincidir("DELIMITADOR", "(")) {
            throw new RuntimeException("Se esperaba '(' en llamada, fila: " + id.getFila());
        }
        consumir();

        NodoAST args = new NodoAST("Argumentos");
        if (!coincidir("DELIMITADOR", ")")) {
            args.agregarHijo(parsearExpresion());
            while (coincidir("DELIMITADOR", ",")) {
                consumir();
                args.agregarHijo(parsearExpresion());
            }
        }
        if (!coincidir("DELIMITADOR", ")")) {
            throw new RuntimeException("Se esperaba ')' en llamada, fila: " + actual().getFila());
        }
        consumir();
        llamada.agregarHijo(args);

        if (coincidir("DELIMITADOR", ";")) consumir();
        return llamada;
    }

    // ==================== EXPRESIÓN ====================

    private NodoAST parsearExpresion() {
        NodoAST expr = new NodoAST("Expresion");
        Token tok = actual();

        if (tok == null) {
            throw new RuntimeException("Se esperaba expresión, fin de entrada");
        }

        if (tok.getTipo() == TipoToken.NUMERO) {
            consumir();
            expr.agregarHijo(new NodoAST("Numero", tok.getLexema()));
        } else if (tok.getTipo() == TipoToken.TEXTO) {
            consumir();
            expr.agregarHijo(new NodoAST("Texto", tok.getLexema()));
        } else if (tok.getTipo() == TipoToken.TECLA) {
            consumir();
            expr.agregarHijo(new NodoAST("Tecla", tok.getLexema()));
        } else if (tok.getTipo() == TipoToken.RESERVADA && tok.getLexema().equals("Nuevo")) {
            consumir(); // Nuevo
            if (coincidir("IDENTIFICADOR", null) || (coincidir("RESERVADA", null) && esTipoDato(actual().getLexema()))) {
                Token tipoConstructor = consumir();
                NodoAST constructor = new NodoAST("Constructor", tipoConstructor.getLexema());
                if (coincidir("DELIMITADOR", "(")) {
                    consumir();
                    NodoAST args = new NodoAST("Argumentos");
                    if (!coincidir("DELIMITADOR", ")")) {
                        args.agregarHijo(parsearExpresion());
                        while (coincidir("DELIMITADOR", ",")) {
                            consumir();
                            args.agregarHijo(parsearExpresion());
                        }
                    }
                    if (!coincidir("DELIMITADOR", ")")) {
                        throw new RuntimeException("Se esperaba ')' en constructor, fila: " + actual().getFila());
                    }
                    consumir();
                    constructor.agregarHijo(args);
                }
                expr.agregarHijo(constructor);
            }
        } else if (tok.getTipo() == TipoToken.IDENTIFICADOR) {
            if (posicion + 1 < tokens.size() && tokens.get(posicion + 1).getLexema().equals("(")) {
                expr.agregarHijo(parsearLlamadaFuncion());
            } else {
                consumir();
                expr.agregarHijo(new NodoAST("Identificador", tok.getLexema()));
            }
        } else if (tok.getTipo() == TipoToken.DELIMITADOR && tok.getLexema().equals("(")) {
            consumir();
            expr.agregarHijo(parsearExpresion());
            if (coincidir("DELIMITADOR", ")")) consumir();
        } else {
            throw new RuntimeException("Expresión inesperada: " + tok.getLexema() + ", fila: " + tok.getFila());
        }

        if (actual() != null && actual().getTipo() == TipoToken.OPERADOR &&
            !actual().getLexema().equals("=")) {
            Token op = consumir();
            NodoAST binario = new NodoAST("Operacion", op.getLexema());
            binario.agregarHijo(expr);
            binario.agregarHijo(parsearExpresion());
            return binario;
        }

        return expr;
    }

    // ==================== ÁRBOL DE TEXTO ====================

    public static String generarTextoAST(NodoAST nodo, String prefijo, boolean esUltimo) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefijo).append(esUltimo ? "└── " : "├── ").append(nodo.getTipo());
        if (nodo.getValor() != null) {
            sb.append(": ").append(nodo.getValor());
        }
        sb.append("\n");

        List<NodoAST> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimo = (i == hijos.size() - 1);
            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
            sb.append(generarTextoAST(hijos.get(i), nuevoPrefijo, ultimo));
        }

        return sb.toString();
    }
}
