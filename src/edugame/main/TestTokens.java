package edugame.main;

import edugame.lexer.Lexer;
import edugame.token.Token;
import java.util.List;

public class TestTokens {
    public static void main(String[] args) {
        String codigo = "JUEGO MiJuego 800x600\n" +
            "BUCLE\n" +
            "AL_PRESIONAR TECLA DERECHA\n" +
            "MOVER jugador DERECHA 5;\n" +
            "FIN\n" +
            "FIN";
        
        System.out.println("=== TOKENS ===");
        Lexer lexer = new Lexer(codigo);
        List<Token> tokens = lexer.tokenizar();
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
}