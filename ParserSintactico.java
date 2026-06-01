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
    private List<ReglaTransicion> tablaBloquePULSA;
    private List<ReglaTransicion> tablaExpresion;

    private List<Token> tokens;
    private int posicion;

    public ParserSintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.posicion = 0;
        inicializarAutomas();
    }

    private void inicializarAutomas() {
        tablaDeclaracion = new ArrayList<>();
        tablaDeclaracion.add(new ReglaTransicion(0, "RESERVADA", 1));
        tablaDeclaracion.add(new ReglaTransicion(0, "IDENTIFICADOR", 1));
        tablaDeclaracion.add(new ReglaTransicion(1, "IDENTIFICADOR", 2));
        tablaDeclaracion.add(new ReglaTransicion(2, "OPERADOR", "=", 3));
        tablaDeclaracion.add(new ReglaTransicion(2, "DELIMITADOR", ";", 5));
        tablaDeclaracion.add(new ReglaTransicion(3, "*", 4));
        tablaDeclaracion.add(new ReglaTransicion(4, "DELIMITADOR", ";", 5));

        tablaAsignacion = new ArrayList<>();
        tablaAsignacion.add(new ReglaTransicion(0, "IDENTIFICADOR", 1));
        tablaAsignacion.add(new ReglaTransicion(1, "OPERADOR", "=", 2));
        tablaAsignacion.add(new ReglaTransicion(2, "*", 3));
        tablaAsignacion.add(new ReglaTransicion(3, "DELIMITADOR", ";", 4));

        tablaLlamadaFuncion = new ArrayList<>();
        tablaLlamadaFuncion.add(new ReglaTransicion(0, "IDENTIFICADOR", 1));
        tablaLlamadaFuncion.add(new ReglaTransicion(1, "DELIMITADOR", "(", 2));
        tablaLlamadaFuncion.add(new ReglaTransicion(2, "DELIMITADOR", ")", 4));
        tablaLlamadaFuncion.add(new ReglaTransicion(2, "*", 3));
        tablaLlamadaFuncion.add(new ReglaTransicion(3, "DELIMITADOR", ")", 4));
        tablaLlamadaFuncion.add(new ReglaTransicion(4, "DELIMITADOR", ";", 5));

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

        tablaBloqueSI = new ArrayList<>();
        tablaBloqueSI.add(new ReglaTransicion(0, "RESERVADA", "SI", 1));
        tablaBloqueSI.add(new ReglaTransicion(1, "DELIMITADOR", "(", 2));
        tablaBloqueSI.add(new ReglaTransicion(1, "RESERVADA", "PULSA", 5));
        tablaBloqueSI.add(new ReglaTransicion(2, "*", 3));
        tablaBloqueSI.add(new ReglaTransicion(3, "DELIMITADOR", ")", 4));
        tablaBloqueSI.add(new ReglaTransicion(4, "RESERVADA", "PULSA", 5));
        tablaBloqueSI.add(new ReglaTransicion(5, "RESERVADA", "FIN", 6));

        tablaBloqueDIBUJAR = new ArrayList<>();
        tablaBloqueDIBUJAR.add(new ReglaTransicion(0, "RESERVADA", "DIBUJAR", 1));
        tablaBloqueDIBUJAR.add(new ReglaTransicion(1, "RESERVADA", "FIN", 3));
        tablaBloqueDIBUJAR.add(new ReglaTransicion(1, "*", 2));
        tablaBloqueDIBUJAR.add(new ReglaTransicion(2, "RESERVADA", "FIN", 3));

        tablaBloquePULSA = new ArrayList<>();
        tablaBloquePULSA.add(new ReglaTransicion(0, "RESERVADA", "PULSA", 1));
        tablaBloquePULSA.add(new ReglaTransicion(1, "TECLA", 2));
        tablaBloquePULSA.add(new ReglaTransicion(2, "*", 3));
        tablaBloquePULSA.add(new ReglaTransicion(3, "RESERVADA", "FIN", 4));

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
        if (!regla.tipoToken.equals("*") && !tok.getTipo().toString().equals(regla.tipoToken)) return false;
        if (regla.lexemaEsperado != null && !tok.getLexema().equals(regla.lexemaEsperado)) return false;
        return true;
    }

    private int buscarTransicion(List<ReglaTransicion> tabla, int estadoActual) {
        for (ReglaTransicion regla : tabla) {
            if (regla.estadoActual == estadoActual && verificarTransicion(regla)) return regla.nuevoEstado;
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

    private void errorSintactico(String mensaje) {
        Token tok = actual();
        int fila = tok != null ? tok.getFila() : 0;
        int col = tok != null ? tok.getColumna() : 0;

        String sugerencia = null;
        if (tok != null && tok.getTipo() == TipoToken.IDENTIFICADOR) {
            sugerencia = TablaErrores.buscarSugerencia(tok.getLexema());
            if (sugerencia != null) sugerencia = "¿Quisiste decir '" + sugerencia + "'?";
        }

        TablaErrores.agregarError(ErrorInfo.TipoError.SINTACTICO, mensaje, sugerencia, fila, col);
        throw new RuntimeException(mensaje + (sugerencia != null ? " (" + sugerencia + ")" : "") + ", fila: " + fila);
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
            tok.getTipo() == TipoToken.IDENTIFICADOR || tok.getTipo() == TipoToken.TECLA) return true;
        if (tok.getTipo() == TipoToken.DELIMITADOR && tok.getLexema().equals("(")) return true;
        if (tok.getTipo() == TipoToken.RESERVADA && tok.getLexema().equals("Nuevo")) return true;
        return false;
    }

    // ==================== PROGRAMA ====================

    public NodoAST parsear() {
        NodoAST programa = new NodoAST("Programa");

        if (!coincidir("RESERVADA", "JUEGO")) errorSintactico("Se esperaba 'JUEGO' al inicio del programa");
        consumir();

        if (!coincidir("IDENTIFICADOR", null)) errorSintactico("Se esperaba identificador después de 'JUEGO'");

        programa.agregarHijo(parsearJuego());

        while (posicion < tokens.size() && coincidir("RESERVADA", "JUEGO")) {
            consumir();
            if (!coincidir("IDENTIFICADOR", null)) errorSintactico("Se esperaba identificador después de 'JUEGO'");
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
        if (!coincidir("RESERVADA", "INICIAR")) errorSintactico("Se esperaba 'INICIAR' en JUEGO");
        consumir();
        NodoAST iniciar = new NodoAST("INICIAR");
        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN") && !coincidir("RESERVADA", "ACTUALIZAR")) {
            if (esInicioStatement()) {
                iniciar.agregarHijo(parsearStatement());
            } else {
                break;
            }
        }
        if (!coincidir("RESERVADA", "FIN")) errorSintactico("Se esperaba 'FIN' después de INICIAR");
        consumir();
        juego.agregarHijo(iniciar);

        // ACTUALIZAR
        if (!coincidir("RESERVADA", "ACTUALIZAR")) errorSintactico("Se esperaba 'ACTUALIZAR' en JUEGO");
        consumir();
        NodoAST actualizar = new NodoAST("ACTUALIZAR");
        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN")) {
            if (coincidir("RESERVADA", "SI")) {
                actualizar.agregarHijo(parsearBloqueSI());
            } else if (coincidir("RESERVADA", "DIBUJAR")) {
                actualizar.agregarHijo(parsearBloqueDIBUJAR());
            } else if (coincidir("RESERVADA", "PULSA")) {
                actualizar.agregarHijo(parsearBloquePULSA());
            } else if (esInicioStatement()) {
                actualizar.agregarHijo(parsearStatement());
            } else {
                break;
            }
        }
        if (!coincidir("RESERVADA", "FIN")) errorSintactico("Se esperaba 'FIN' después de ACTUALIZAR");
        consumir();
        juego.agregarHijo(actualizar);

        // FUNCIONES
        while (posicion < tokens.size() && coincidir("RESERVADA", "FUNCION")) {
            juego.agregarHijo(parsearFuncion());
        }

        if (!coincidir("RESERVADA", "FIN")) errorSintactico("Se esperaba 'FIN' para cerrar JUEGO");
        consumir();

        return juego;
    }

    // ==================== FUNCIÓN ====================

    private NodoAST parsearFuncion() {
        NodoAST funcion = new NodoAST("Funcion");
        consumir(); // FUNCION

        boolean tieneTipoRetorno = false;
        String tipoRetorno = null;

        if (actual() != null && esTipoDato(actual().getLexema())) {
            tipoRetorno = actual().getLexema();
            consumir();
            tieneTipoRetorno = true;
            funcion.agregarHijo(new NodoAST("TipoRetorno", tipoRetorno));
        } else if (actual() != null && actual().getLexema().equals("VACIA")) {
            consumir();
            funcion.agregarHijo(new NodoAST("TipoRetorno", "VACIA"));
        }

        if (!coincidir("IDENTIFICADOR", null)) errorSintactico("Se esperaba nombre de función");
        Token nombreFunc = consumir();
        funcion.agregarHijo(new NodoAST("NombreFuncion", nombreFunc.getLexema()));

        if (!coincidir("DELIMITADOR", "(")) errorSintactico("Se esperaba '(' después del nombre de función");
        consumir();

        NodoAST params = new NodoAST("Parametros");
        while (!coincidir("DELIMITADOR", ")")) {
            if (params.getHijos().size() > 0) {
                if (!coincidir("DELIMITADOR", ",")) errorSintactico("Se esperaba ',' entre parámetros");
                consumir();
            }
            if (actual() != null && (actual().getTipo() == TipoToken.IDENTIFICADOR || esTipoDato(actual().getLexema()))) {
                NodoAST param = new NodoAST("Parametro");
                Token tipoOId = consumir();
                if (actual() != null && actual().getTipo() == TipoToken.IDENTIFICADOR) {
                    param.agregarHijo(new NodoAST("Tipo", tipoOId.getLexema()));
                    param.agregarHijo(new NodoAST("Nombre", consumir().getLexema()));
                } else {
                    param.agregarHijo(new NodoAST("Nombre", tipoOId.getLexema()));
                }
                params.agregarHijo(param);
            } else {
                break;
            }
        }
        if (!coincidir("DELIMITADOR", ")")) errorSintactico("Se esperaba ')' en parámetros");
        consumir();
        funcion.agregarHijo(params);

        NodoAST cuerpo = new NodoAST("CuerpoFuncion");
        boolean tieneRegresar = false;
        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN")) {
            if (coincidir("RESERVADA", "REGRESAR")) {
                cuerpo.agregarHijo(parsearReturn());
                tieneRegresar = true;
            } else if (esInicioStatement()) {
                cuerpo.agregarHijo(parsearStatement());
            } else {
                errorSintactico("Token inesperado en función: " + actual().getLexema());
            }
        }

        if (tieneTipoRetorno && !tieneRegresar) {
            errorSintactico("La función '" + nombreFunc.getLexema() +
                "' tiene tipo de retorno '" + tipoRetorno +
                "' pero no tiene REGRESAR");
        }

        if (!coincidir("RESERVADA", "FIN")) errorSintactico("Se esperaba 'FIN' para cerrar función");
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
            if (!coincidir("DELIMITADOR", ")")) errorSintactico("Se esperaba ')' en SI");
            consumir();
        }

        while (posicion < tokens.size() && coincidir("RESERVADA", "PULSA")) {
            si.agregarHijo(parsearBloquePULSA());
        }

        if (!coincidir("RESERVADA", "FIN")) errorSintactico("Se esperaba 'FIN' para cerrar SI");
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
                errorSintactico("Token inesperado en DIBUJAR: " + actual().getLexema());
            }
        }
        if (!coincidir("RESERVADA", "FIN")) errorSintactico("Se esperaba 'FIN' para cerrar DIBUJAR");
        consumir();
        return dibujar;
    }

    // ==================== BLOQUE PULSA ====================
    // PULSA $Tecla
    //     acciones...
    // FIN

    private NodoAST parsearBloquePULSA() {
        NodoAST pulsa = new NodoAST("PULSA");
        consumir(); // PULSA

        if (!coincidir("TECLA", null)) errorSintactico("Se esperaba tecla ($X) después de PULSA");
        Token tecla = consumir();
        pulsa.agregarHijo(new NodoAST("Tecla", tecla.getLexema()));

        while (posicion < tokens.size() && !coincidir("RESERVADA", "FIN")) {
            pulsa.agregarHijo(parsearAccionPulsa());
        }

        if (!coincidir("RESERVADA", "FIN")) errorSintactico("Se esperaba 'FIN' para cerrar PULSA");
        consumir();
        return pulsa;
    }

    // ==================== ACCIÓN DENTRO DE PULSA ====================

    private NodoAST parsearAccionPulsa() {
        Token tok = actual();
        if (tok == null) errorSintactico("Token inesperado: fin de entrada");

        // obj MOVER cantidad DIR
        if (tok.getTipo() == TipoToken.IDENTIFICADOR && posicion + 1 < tokens.size() &&
            tokens.get(posicion + 1).getLexema().equals("MOVER")) {
            return parsearAccionMover();
        }

        // INTERACTUAR obj1 obj2
        if (coincidir("RESERVADA", "INTERACTUAR")) {
            return parsearAccionInteractuar();
        }

        // obj();  — llamada a función
        if (tok.getTipo() == TipoToken.IDENTIFICADOR && posicion + 1 < tokens.size() &&
            tokens.get(posicion + 1).getLexema().equals("(")) {
            return parsearLlamadaFuncion();
        }

        // id = expr;  — asignación
        if (tok.getTipo() == TipoToken.IDENTIFICADOR && posicion + 1 < tokens.size() &&
            tokens.get(posicion + 1).getLexema().equals("=")) {
            return parsearAsignacion();
        }

        errorSintactico("Acción no válida en PULSA: " + tok.getLexema());
        return null;
    }

    // ==================== ACCIÓN MOVER ====================

    private NodoAST parsearAccionMover() {
        NodoAST accion = new NodoAST("AccionMover");
        Token obj = consumir(); // identificador
        accion.agregarHijo(new NodoAST("Objeto", obj.getLexema()));

        consumir(); // MOVER

        if (coincidir("NUMERO", null)) {
            accion.agregarHijo(new NodoAST("Cantidad", consumir().getLexema()));
        }

        if (actual() != null && (coincidir("RESERVADA", "IZQUIERDA") || coincidir("RESERVADA", "DERECHA") ||
            coincidir("RESERVADA", "ARRIBA") || coincidir("RESERVADA", "ABAJO"))) {
            accion.agregarHijo(new NodoAST("Direccion", consumir().getLexema()));
        }

        if (coincidir("DELIMITADOR", ";")) consumir();
        return accion;
    }

    // ==================== ACCIÓN INTERACTUAR ====================

    private NodoAST parsearAccionInteractuar() {
        NodoAST accion = new NodoAST("INTERACTUAR");
        consumir(); // INTERACTUAR

        if (coincidir("IDENTIFICADOR", null)) {
            accion.agregarHijo(new NodoAST("identificador", consumir().getLexema()));
        }
        if (coincidir("IDENTIFICADOR", null)) {
            accion.agregarHijo(new NodoAST("identificador", consumir().getLexema()));
        }
        if (coincidir("IDENTIFICADOR", null)) {
            accion.agregarHijo(parsearLlamadaFuncion());
        }

        if (coincidir("DELIMITADOR", ";")) consumir();
        return accion;
    }

    // ==================== STATEMENT ====================

    private NodoAST parsearStatement() {
        Token tok = actual();
        if (tok == null) errorSintactico("Token inesperado: fin de entrada");

        if (coincidir("RESERVADA", "SI")) return parsearBloqueSI();
        if (coincidir("RESERVADA", "DIBUJAR")) return parsearBloqueDIBUJAR();
        if (coincidir("RESERVADA", "PULSA")) return parsearBloquePULSA();
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
                errorSintactico("Sentencia no válida: " + tok.getLexema());
            }
        }

        errorSintactico("Token inesperado: " + tok.getLexema());
        return null;
    }

    // ==================== DECLARACIÓN ====================

    private NodoAST parsearDeclaracion() {
        NodoAST decl = new NodoAST("Declaracion");
        Token tipo = consumir();
        decl.agregarHijo(new NodoAST("Tipo", tipo.getLexema()));

        if (!coincidir("IDENTIFICADOR", null)) errorSintactico("Se esperaba identificador en declaración");
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

        if (!coincidir("OPERADOR", "=")) errorSintactico("Se esperaba '=' en asignación");
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

        if (!coincidir("DELIMITADOR", "(")) errorSintactico("Se esperaba '(' en llamada a función");
        consumir();

        NodoAST args = new NodoAST("Argumentos");
        if (!coincidir("DELIMITADOR", ")")) {
            args.agregarHijo(parsearExpresion());
            while (coincidir("DELIMITADOR", ",")) {
                consumir();
                args.agregarHijo(parsearExpresion());
            }
        }
        if (!coincidir("DELIMITADOR", ")")) errorSintactico("Se esperaba ')' en llamada a función");
        consumir();
        llamada.agregarHijo(args);

        if (coincidir("DELIMITADOR", ";")) consumir();
        return llamada;
    }

    // ==================== EXPRESIÓN ====================

    private NodoAST parsearExpresion() {
        NodoAST expr = new NodoAST("Expresion");
        Token tok = actual();

        if (tok == null) errorSintactico("Se esperaba expresión, fin de entrada");

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
            consumir();
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
                    if (!coincidir("DELIMITADOR", ")")) errorSintactico("Se esperaba ')' en constructor");
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
            errorSintactico("Expresión inesperada: " + tok.getLexema());
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
        if (nodo.getValor() != null) sb.append(": ").append(nodo.getValor());
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
