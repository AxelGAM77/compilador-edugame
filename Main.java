import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Main extends JFrame {
    private JTextArea areaCodigoEntrada;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JButton btnAnalizar;
    private JLabel etiquetaEstado;
    private JTree arbolSintactico;
    private DefaultMutableTreeNode nodoRaizArbol;
    private JTextArea areaLog;

    public Main() {
        setTitle("Compilador EduGame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        JSplitPane panelPrincipal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        panelPrincipal.setDividerLocation(250);
        panelPrincipal.setResizeWeight(0.3);

        // Arriba: código fuente + controles
        panelPrincipal.setTopComponent(crearPanelSuperior());

        // Abajo: tokens + árbol + log
        panelPrincipal.setBottomComponent(crearPanelInferior());

        this.add(panelPrincipal, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());

        // Área de código
        JPanel panelCodigo = new JPanel(new BorderLayout());
        panelCodigo.setBorder(BorderFactory.createTitledBorder("Código Fuente"));

        areaCodigoEntrada = new JTextArea();
        areaCodigoEntrada.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaCodigoEntrada.setLineWrap(true);
        areaCodigoEntrada.setWrapStyleWord(true);
        areaCodigoEntrada.setText(
            "JUEGO miJuego\n" +
            "\tINICIAR\n" +
            "\t\tPersonaje personaje = Nuevo Personaje();\n" +
            "\t\tImagen imagenP = Nuevo Imagen(\"ruta\");\n" +
            "\t\tSprite personajeSprite = Nuevo Sprite(nombre, imagenP);\n" +
            "\t\tObjeto puerta = Nuevo Objeto();\n" +
            "\t\tNumero x = 10;\n" +
            "\t\tTexto gameOver = \"over\";\n" +
            "\tFIN\n" +
            "\tACTUALIZAR\n" +
            "\t\tSI\n" +
            "\t\t\tPULSA $A personaje MOVER 5 IZQUIERDA;\n" +
            "\t\t\tPULSA $B nombre();\n" +
            "\t\t\tPULSA $C INTERACTUAR personaje puerta;\n" +
            "\t\tFIN\n" +
            "\t\tDIBUJAR\n" +
            "\t\tFIN\n" +
            "\tFIN\n" +
            "\tFUNCION Numero suma(Numero a, Numero b)\n" +
            "\t\tREGRESAR a + b;\n" +
            "\tFIN\n" +
            "FIN"
        );

        JScrollPane scrollCodigo = new JScrollPane(areaCodigoEntrada);
        panelCodigo.add(scrollCodigo, BorderLayout.CENTER);

        // Botón y estado
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Control"));

        btnAnalizar = new JButton("Analizar");
        btnAnalizar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAnalizar.setPreferredSize(new Dimension(150, 35));
        btnAnalizar.addActionListener(this::analizar);

        etiquetaEstado = new JLabel("Listo");
        etiquetaEstado.setFont(new Font("Arial", Font.PLAIN, 11));

        panelBotones.add(btnAnalizar);
        panelBotones.add(etiquetaEstado);

        panel.add(panelCodigo, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel izquierdo: Tokens
        JPanel panelTokens = new JPanel(new BorderLayout());
        panelTokens.setBorder(BorderFactory.createTitledBorder("Tokens"));

        String[] columnas = {"Id", "Tipo", "Lexema", "LexId", "Fila", "Col"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTokens = new JTable(modeloTabla);
        tablaTokens.setFont(new Font("Courier New", Font.PLAIN, 10));
        tablaTokens.setRowHeight(20);
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(35);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(40);
        tablaTokens.getColumnModel().getColumn(4).setPreferredWidth(35);
        tablaTokens.getColumnModel().getColumn(5).setPreferredWidth(35);

        JScrollPane scrollTokens = new JScrollPane(tablaTokens);
        panelTokens.add(scrollTokens, BorderLayout.CENTER);

        // Panel derecho: Árbol + Log
        JPanel panelDerecho = new JPanel(new BorderLayout());

        // Árbol
        JPanel panelArbol = new JPanel(new BorderLayout());
        panelArbol.setBorder(BorderFactory.createTitledBorder("Árbol Sintáctico"));
        nodoRaizArbol = new DefaultMutableTreeNode("Programa");
        arbolSintactico = new JTree(nodoRaizArbol);
        arbolSintactico.setFont(new Font("Courier New", Font.PLAIN, 10));
        JScrollPane scrollArbol = new JScrollPane(arbolSintactico);
        panelArbol.add(scrollArbol, BorderLayout.CENTER);

        // Log
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Log"));
        areaLog = new JTextArea();
        areaLog.setFont(new Font("Courier New", Font.PLAIN, 10));
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(30, 30, 30));
        areaLog.setForeground(new Color(0, 200, 0));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setPreferredSize(new Dimension(0, 120));
        panelLog.add(scrollLog, BorderLayout.CENTER);

        // Dividir árbol y log verticalmente
        JSplitPane splitDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelArbol, panelLog);
        splitDerecho.setDividerLocation(300);
        splitDerecho.setResizeWeight(0.7);

        panelDerecho.add(splitDerecho, BorderLayout.CENTER);

        // Split tokens | (árbol + log)
        JSplitPane splitCentral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTokens, panelDerecho);
        splitCentral.setDividerLocation(400);
        splitCentral.setResizeWeight(0.35);

        panel.add(splitCentral, BorderLayout.CENTER);

        return panel;
    }

    private void analizar(ActionEvent e) {
        String codigo = areaCodigoEntrada.getText();
        areaLog.setText("");

        if (codigo.trim().isEmpty()) {
            log("⚠ No hay código para analizar", new Color(200, 150, 0));
            return;
        }

        // FASE 1: Análisis Léxico
        log("═══ FASE 1: ANÁLISIS LÉXICO ═══", new Color(0, 150, 255));
        List<Token> tokens;
        try {
            Tokenizador tokenizador = new Tokenizador(codigo);
            tokens = tokenizador.tokenizar();
            modeloTabla.setRowCount(0);

            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                String lexemaId = obtenerLexemaId(token);
                Object[] fila = {i, token.getTipo().toString(), token.getLexema(), lexemaId, token.getFila(), token.getColumna()};
                modeloTabla.addRow(fila);
            }

            log("✓ Tokens generados: " + tokens.size(), new Color(0, 200, 0));
            log("  Tipos: " + contarTipos(tokens), new Color(0, 180, 0));

        } catch (Exception ex) {
            log("✗ Error léxico: " + ex.getMessage(), new Color(255, 60, 60));
            etiquetaEstado.setText("✗ Error léxico");
            etiquetaEstado.setForeground(new Color(200, 0, 0));
            return;
        }

        // FASE 2: Análisis Sintáctico
        log("", Color.WHITE);
        log("═══ FASE 2: ANÁLISIS SINTÁCTICO ═══", new Color(0, 150, 255));
        try {
            ParserSintactico parser = new ParserSintactico(tokens);
            NodoAST ast = parser.parsear();

            // Mostrar árbol en Swing
            nodoRaizArbol.removeAllChildren();
            construirArbolSwing(nodoRaizArbol, ast);
            arbolSintactico.expandRow(0);
            for (int i = 0; i < arbolSintactico.getRowCount(); i++) {
                arbolSintactico.expandRow(i);
            }

            // Log del árbol
            String textoArbol = ParserSintactico.generarTextoAST(ast, "", true);
            for (String linea : textoArbol.split("\n")) {
                if (!linea.trim().isEmpty()) {
                    log(linea, new Color(0, 200, 150));
                }
            }

            log("", Color.WHITE);
            log("✓ Análisis sintáctico completado correctamente", new Color(0, 200, 0));
            log("  Nodos AST: " + contarNodos(ast), new Color(0, 180, 0));

            etiquetaEstado.setText("✓ Análisis completado: " + tokens.size() + " tokens, AST generado");
            etiquetaEstado.setForeground(new Color(0, 100, 0));

        } catch (Exception ex) {
            log("✗ Error sintáctico: " + ex.getMessage(), new Color(255, 60, 60));
            etiquetaEstado.setText("✗ Error sintáctico");
            etiquetaEstado.setForeground(new Color(200, 0, 0));
        }
    }

    private void log(String mensaje, Color color) {
        areaLog.setForeground(color);
        areaLog.append(mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private String contarTipos(List<Token> tokens) {
        int reservadas = 0, identificadores = 0, numeros = 0, textos = 0, operadores = 0, delimitadores = 0, teclas = 0;
        for (Token t : tokens) {
            switch (t.getTipo()) {
                case RESERVADA: reservadas++; break;
                case IDENTIFICADOR: identificadores++; break;
                case NUMERO: numeros++; break;
                case TEXTO: textos++; break;
                case OPERADOR: operadores++; break;
                case DELIMITADOR: delimitadores++; break;
                case TECLA: teclas++; break;
            }
        }
        String s = "";
        if (reservadas > 0) s += reservadas + " reservadas, ";
        if (identificadores > 0) s += identificadores + " identificadores, ";
        if (numeros > 0) s += numeros + " números, ";
        if (textos > 0) s += textos + " textos, ";
        if (teclas > 0) s += teclas + " teclas, ";
        if (operadores > 0) s += operadores + " operadores, ";
        if (delimitadores > 0) s += delimitadores + " delimitadores";
        return s.replaceAll(", $", "");
    }

    private int contarNodos(NodoAST nodo) {
        int count = 1;
        for (NodoAST hijo : nodo.getHijos()) {
            count += contarNodos(hijo);
        }
        return count;
    }

    private void construirArbolSwing(DefaultMutableTreeNode nodoPadre, NodoAST nodoAST) {
        String texto = nodoAST.getTipo();
        if (nodoAST.getValor() != null) {
            texto += ": " + nodoAST.getValor();
        }
        DefaultMutableTreeNode nodoSwing = new DefaultMutableTreeNode(texto);
        nodoPadre.add(nodoSwing);

        for (NodoAST hijo : nodoAST.getHijos()) {
            construirArbolSwing(nodoSwing, hijo);
        }
    }

    private String obtenerLexemaId(Token token) {
        switch (token.getTipo()) {
            case RESERVADA:
                int idRes = TablaSimbolos.obtenerLexemaId(token.getLexema());
                return idRes >= 0 ? String.valueOf(idRes) : "N/A";
            case OPERADOR:
                int idOp = TablaSimbolos.obtenerOperadorId(token.getLexema());
                return idOp >= 0 ? String.valueOf(idOp) : "N/A";
            case DELIMITADOR:
                int idDelim = TablaSimbolos.obtenerDelimitadorId(token.getLexema());
                return idDelim >= 0 ? String.valueOf(idDelim) : "N/A";
            case TECLA:
                int idTecla = TablaSimbolos.obtenerTeclaId(token.getLexema());
                return idTecla >= 0 ? String.valueOf(idTecla) : "N/A";
            default:
                return "-";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
