package edugame.token;

import java.util.EnumSet;
import java.util.Set;

public class ClasificadorToken {

    public static final Set<TipoToken> ABREN_BLOQUE = EnumSet.of(
        TipoToken.JUEGO,
        TipoToken.ESCENA,
        TipoToken.INICIAR,
        TipoToken.BUCLE,
        TipoToken.DIBUJAR,
        TipoToken.SI,
        TipoToken.SINO,
        TipoToken.REPETIR,
        TipoToken.MIENTRAS,
        TipoToken.AL_PRESIONAR,
        TipoToken.AL_SOLTAR,
        TipoToken.AL_CLICK,
        TipoToken.AL_TOCAR
    );

    public static final Set<TipoToken> CIERRAN_BLOQUE = EnumSet.of(
        TipoToken.FIN
    );

    public static final Set<TipoToken> INICIAN_INSTRUCCION = EnumSet.of(
        TipoToken.PERSONAJE,
        TipoToken.OBJETO,
        TipoToken.FONDO,
        TipoToken.SONIDO,
        TipoToken.MOVER,
        TipoToken.MOSTRAR,
        TipoToken.OCULTAR,
        TipoToken.REPRODUCIR,
        TipoToken.DETENER,
        TipoToken.GUARDAR
    );

    public static boolean abreBloque(TipoToken t) { return ABREN_BLOQUE.contains(t); }
    public static boolean cierraBloque(TipoToken t) { return CIERRAN_BLOQUE.contains(t); }
    public static boolean iniciainstruccion(TipoToken t) { return INICIAN_INSTRUCCION.contains(t); }
    public static boolean esDelimitador(TipoToken t) { return abreBloque(t) || cierraBloque(t); }
}