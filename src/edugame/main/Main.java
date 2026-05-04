package edugame.main;

import edugame.ast.NodoAST;
import edugame.ast.NodoPrograma;
import edugame.error.ErrorEduGame;
import edugame.error.ErrorLexico;
import edugame.error.ErrorSintactico;
import edugame.lexer.Lexer;
import edugame.parser.Parser;
import edugame.token.Token;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main extends JFrame {

    private JTextArea editor;
    private JTextPane highlightPane;
    private StyledDocument doc;
    private JLabel statusLabel;
    private JButton compilarBtn;
    private JButton limpiarBtn;
    private List<Token> tokens;
    private List<String> errores;

    public Main() {
        setTitle("EduGame - Compilador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(45, 45, 48));
        topPanel.setPreferredSize(new Dimension(0, 50));

        JLabel titulo = new JLabel("EduGame Compiler");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titulo);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        highlightPane = new JTextPane();
        highlightPane.setEditable(false);
        highlightPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        doc = highlightPane.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(defaultStyle, "Consolas");
        StyleConstants.setFontSize(defaultStyle, 14);

        Style errorStyle = doc.addStyle("error", defaultStyle);
        StyleConstants.setBackground(errorStyle, new Color(255, 100, 100));
        StyleConstants.setForeground(errorStyle, Color.BLACK);

        Style validoStyle = doc.addStyle("valido", defaultStyle);
        StyleConstants.setBackground(validoStyle, new Color(100, 255, 100));
        StyleConstants.setForeground(validoStyle, Color.BLACK);

        Style normalStyle = doc.addStyle("normal", defaultStyle);
        StyleConstants.setBackground(normalStyle, new Color(60, 60, 64));
        StyleConstants.setForeground(normalStyle, Color.WHITE);

        JScrollPane highlightScroll = new JScrollPane(highlightPane);
        highlightScroll.setPreferredSize(new Dimension(400, 0));
        highlightScroll.setBackground(new Color(60, 60, 64));
        highlightScroll.getViewport().setBackground(new Color(60, 60, 64));

        editor = new JTextArea();
        editor.setFont(new Font("Consolas", Font.PLAIN, 14));
        editor.setBackground(new Color(30, 30, 32));
        editor.setForeground(Color.WHITE);
        editor.setCaretColor(Color.WHITE);
        
        JScrollPane editorScroll = new JScrollPane(editor);
        editorScroll.setPreferredSize(new Dimension(400, 0));

        centerPanel.add(editorScroll);
        centerPanel.add(highlightScroll);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(0, 60));
        bottomPanel.setBackground(new Color(45, 45, 48));

        statusLabel = new JLabel("Listo para compilar...");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(45, 45, 48));

        compilarBtn = new JButton("Compilar");
        compilarBtn.setBackground(new Color(76, 175, 80));
        compilarBtn.setForeground(Color.WHITE);
        compilarBtn.setFocusPainted(false);
        compilarBtn.addActionListener(e -> compilar());

        limpiarBtn = new JButton("Limpiar");
        limpiarBtn.setBackground(new Color(158, 158, 158));
        limpiarBtn.setForeground(Color.WHITE);
        limpiarBtn.setFocusPainted(false);
        limpiarBtn.addActionListener(e -> limpiar());

        btnPanel.add(compilarBtn);
        btnPanel.add(limpiarBtn);

        bottomPanel.add(btnPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        String ejemplo = "JUEGO MiJuego 800x600\n" +
            "  PERSONAJE jugador IMAGEN \"heroe.png\" EN 100 300;\n" +
            "  OBJETO moneda IMAGEN \"moneda.png\" EN 400 200;\n" +
            "  SONIDO recoger ARCHIVO \"coin.wav\";\n" +
            "  FONDO IMAGEN \"fondo.png\";\n" +
            "\n" +
            "  BUCLE\n" +
            "    AL_PRESIONAR TECLA DERECHA\n" +
            "      MOVER jugador DERECHA 5;\n" +
            "    FIN\n" +
            "\n" +
            "    AL_TOCAR jugador moneda\n" +
            "      SI puntos > 10\n" +
            "        MOSTRAR \"Ganaste!\";\n" +
            "      SINO\n" +
            "        REPRODUCIR recoger;\n" +
            "        OCULTAR moneda;\n" +
            "      FIN\n" +
            "    FIN\n" +
            "\n" +
            "  FIN\n" +
            "\n" +
            "FIN";
        editor.setText(ejemplo);

        editor.getInputMap().put(KeyStroke.getKeyStroke("F5"), "compilar (F5)");
        editor.getActionMap().put("compilar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilar();
            }
        });
    }

    private void compilar() {
        String codigo = editor.getText();
        errores = new ArrayList<>();
        tokens = new ArrayList<>();

        try {
            Lexer lexer = new Lexer(codigo);
            tokens = lexer.tokenizar();
            
            highlightPane.setText("");
            
            Parser parser = new Parser(tokens);
            NodoPrograma programa = parser.parsear();
            
            statusLabel.setText("Compilación exitosa!");
            statusLabel.setForeground(new Color(100, 255, 100));
            
            mostrarHighlights(true);
            
        } catch (ErrorLexico e) {
            errores.add("Línea " + e.getLinea() + ": " + e.getMessage());
            statusLabel.setText("Error léxico: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 80, 80));
            mostrarHighlights(false);
        } catch (ErrorSintactico e) {
            errores.add("Línea " + e.getLinea() + ": " + e.getMessage());
            statusLabel.setText("Error sintáctico: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 80, 80));
            mostrarHighlights(false);
        } catch (Exception e) {
            errores.add(e.getMessage());
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(new Color(255, 80, 80));
            mostrarHighlights(false);
        }
    }

    private void mostrarHighlights(boolean todoValido) {
        String codigo = editor.getText();
        String[] lineas = codigo.split("\n");
        
        int[] erroresLinea = new int[lineas.length];
        
        if (!todoValido && tokens != null) {
            for (Token t : tokens) {
                if (t != null && t.getLinea() > 0 && t.getLinea() <= erroresLinea.length) {
                    if (t.getTipo().name().startsWith("DESCONOCIDO") || 
                        t.getTipo().name().contains("ERROR")) {
                        erroresLinea[t.getLinea() - 1] = 1;
                    }
                }
            }
            for (String err : errores) {
                for (int i = 0; i < lineas.length; i++) {
                    if (err.contains("Línea " + (i + 1))) {
                        erroresLinea[i] = 1;
                    }
                }
            }
        }

        try {
            doc.remove(0, doc.getLength());
            
            for (int i = 0; i < lineas.length; i++) {
                String linea = lineas[i];
                if (i > 0) {
                    doc.insertString(doc.getLength(), "\n", null);
                }
                
                if (todoValido || erroresLinea[i] == 1) {
                    if (todoValido) {
                        doc.insertString(doc.getLength(), "✓ " + linea, doc.getStyle("valido"));
                    } else {
                        doc.insertString(doc.getLength(), "✗ " + linea, doc.getStyle("error"));
                    }
                } else {
                    doc.insertString(doc.getLength(), "  " + linea, doc.getStyle("normal"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiar() {
        editor.setText("");
        highlightPane.setText("");
        statusLabel.setText("Listo para compilar...");
        statusLabel.setForeground(Color.WHITE);
        tokens = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main m = new Main();
            m.setVisible(true);
        });
    }
}