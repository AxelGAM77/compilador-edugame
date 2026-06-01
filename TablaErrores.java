import java.util.ArrayList;
import java.util.List;

public class TablaErrores {

    private static List<ErrorInfo> errores = new ArrayList<>();

    // ==================== REGISTRAR ERRORES ====================

    public static void agregarError(ErrorInfo.TipoError tipo, String mensaje, int fila, int columna) {
        errores.add(new ErrorInfo(tipo, mensaje, fila, columna));
    }

    public static void agregarError(ErrorInfo.TipoError tipo, String mensaje, String sugerencia, int fila, int columna) {
        errores.add(new ErrorInfo(tipo, mensaje, sugerencia, fila, columna));
    }

    // ==================== SUGERENCIAS ====================

    public static String buscarSugerencia(String palabra) {
        String mejor = null;
        int mejorDistancia = Integer.MAX_VALUE;

        for (String lexema : TablaSimbolos.LEXEMAS) {
            int dist = levenshtein(palabra.toLowerCase(), lexema.toLowerCase());
            if (dist < mejorDistancia && dist <= 3) {
                mejorDistancia = dist;
                mejor = lexema;
            }
        }

        return mejor;
    }

    public static String buscarSugerenciaTecla(String tecla) {
        String mejor = null;
        int mejorDistancia = Integer.MAX_VALUE;

        for (String t : TablaSimbolos.TECLAS) {
            int dist = levenshtein(tecla.toUpperCase(), t.toUpperCase());
            if (dist < mejorDistancia && dist <= 2) {
                mejorDistancia = dist;
                mejor = t;
            }
        }

        return mejor;
    }

    // ==================== DISTANCIA DE LEVENSHTEIN ====================

    private static int levenshtein(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int costo = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j] + 1,       // eliminar
                    dp[i][j - 1] + 1),       // insertar
                    dp[i - 1][j - 1] + costo // reemplazar
                );
            }
        }

        return dp[len1][len2];
    }

    // ==================== CONSULTAS ====================

    public static List<ErrorInfo> obtenerErrores() {
        return new ArrayList<>(errores);
    }

    public static List<ErrorInfo> obtenerErroresPorTipo(ErrorInfo.TipoError tipo) {
        List<ErrorInfo> filtrados = new ArrayList<>();
        for (ErrorInfo e : errores) {
            if (e.getTipo() == tipo) filtrados.add(e);
        }
        return filtrados;
    }

    public static int cantidadErrores() { return errores.size(); }

    public static int cantidadAdvertencias() {
        int count = 0;
        for (ErrorInfo e : errores) {
            if (e.getTipo() == ErrorInfo.TipoError.ADVERTENCIA) count++;
        }
        return count;
    }

    public static boolean tieneErrores() {
        for (ErrorInfo e : errores) {
            if (e.getTipo() != ErrorInfo.TipoError.ADVERTENCIA) return true;
        }
        return false;
    }

    public static void limpiar() {
        errores.clear();
    }
}
