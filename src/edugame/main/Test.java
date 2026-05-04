package edugame.main;

import edugame.lexer.Lexer;
import edugame.token.Token;
import edugame.parser.Parser;
import edugame.ast.NodoPrograma;
import edugame.error.ErrorLexico;
import edugame.error.ErrorSintactico;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        // SIN el FIN - debería dar error
        String codigo = "JUEGO MiJuego 800x600\n" +
            "AL_PRESIONAR TECLA IZQUIERDA\n" +
            "  MOVER jugador IZQUIERDA 5;";
        
        System.out.println("=== CODIGO (sin FIN) ===");
        System.out.println(codigo);
        System.out.println();
        
        try {
            System.out.println("=== LEXER ===");
            Lexer lexer = new Lexer(codigo);
            List<Token> tokens = lexer.tokenizar();
            for (Token t : tokens) {
                System.out.println(t);
            }
            System.out.println();
            
            System.out.println("=== PARSER ===");
            Parser parser = new Parser(tokens);
            NodoPrograma prog = parser.parsear();
            System.out.println("OK: " + prog.getNombre());
            
        } catch (ErrorLexico e) {
            System.out.println("ERROR LEXICO: " + e);
        } catch (ErrorSintactico e) {
            System.out.println("ERROR SINTACTICO: " + e);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}