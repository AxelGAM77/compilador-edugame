package edugame.lexer;

import edugame.token.TipoToken;

public class MatrizPalabrasReservadas {

    public static final String[] LEXEMAS = {
        "ABAJO",        "AL_CLICK",     "AL_PRESIONAR", "AL_SOLTAR",
        "AL_TOCAR",     "ARCHIVO",      "ARRIBA",       "BUCLE",
        "DERECHA",      "DETENER",     "DIBUJAR",      "EN",
        "ESCENA",       "FALSO",       "FIN",          "FONDO",
        "GUARDAR",      "IMAGEN",      "INICIAR",      "IZQUIERDA",
        "JUEGO",        "MIENTRAS",    "MOSTRAR",      "MOVER",
        "NO",           "NUMERO",      "O",           "OBJETO",
        "OCULTAR",      "PERSONAJE",   "REPETIR",     "REPRODUCIR",
        "SI",           "SINO",       "SONIDO",      "TECLA",
        "TEXTO",        "VERDADERO",  "VECES",       "Y"
    };

    public static final TipoToken[] TIPOS = {
        TipoToken.ABAJO,        TipoToken.AL_CLICK,     TipoToken.AL_PRESIONAR, TipoToken.AL_SOLTAR,
        TipoToken.AL_TOCAR,     TipoToken.ARCHIVO,      TipoToken.ARRIBA,       TipoToken.BUCLE,
        TipoToken.DERECHA,      TipoToken.DETENER,      TipoToken.DIBUJAR,      TipoToken.EN,
        TipoToken.ESCENA,       TipoToken.FALSO,       TipoToken.FIN,          TipoToken.FONDO,
        TipoToken.GUARDAR,      TipoToken.IMAGEN,      TipoToken.INICIAR,      TipoToken.IZQUIERDA,
        TipoToken.JUEGO,        TipoToken.MIENTRAS,     TipoToken.MOSTRAR,      TipoToken.MOVER,
        TipoToken.NO,           TipoToken.NUMERO_KW,   TipoToken.O,            TipoToken.OBJETO,
        TipoToken.OCULTAR,      TipoToken.PERSONAJE,   TipoToken.REPETIR,     TipoToken.REPRODUCIR,
        TipoToken.SI,           TipoToken.SINO,       TipoToken.SONIDO,      TipoToken.TECLA,
        TipoToken.TEXTO_KW,     TipoToken.VERDADERO,   TipoToken.VECES,      TipoToken.Y
    };

    public static final int LONGITUD = LEXEMAS.length;
}