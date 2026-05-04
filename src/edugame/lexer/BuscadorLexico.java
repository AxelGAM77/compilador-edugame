package edugame.lexer;

import edugame.token.TipoToken;

public class BuscadorLexico {

    public static TipoToken buscarPalabraReservada(String lexema) {
        int izq = 0;
        int der = MatrizPalabrasReservadas.LONGITUD - 1;

        while (izq <= der) {
            int mid = izq + (der - izq) / 2;
            int cmp = lexema.compareTo(MatrizPalabrasReservadas.LEXEMAS[mid]);

            if (cmp == 0) {
                return MatrizPalabrasReservadas.TIPOS[mid];
            } else if (cmp < 0) {
                der = mid - 1;
            } else {
                izq = mid + 1;
            }
        }
        return null;
    }
}