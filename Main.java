import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Main extends JFrame {
    private JTextArea areaCodigoEntrada;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JButton btnAnalizar;
    private JLabel etiquetaEstado;
    private JTextArea areaLog;

    public Main() {
        setTitle("Analizador Léxico - EduGame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        JSplitPane panelPrincipal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        panelPrincipal.setDividerLocation(230);
        panelPrincipal.setResizeWeight(0.3);

        panelPrincipal.setTopComponent(crearPanelSuperior());
        panelPrincipal.setBottomComponent(crearPanelInferior());

        this.add(panelPrincipal, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());

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
            "\t\tNumero x = 10;\n" +
            "\t\tTexto msg = \"hola\";\n" +
            "\tFIN\n" +
            "\tACTUALIZAR\n" +
            "\t\tSI\n" +
            "\t\t\tPULSA $A\n" +
            "\t\t\t\tpersonaje MOVER 5 IZQUIERDA;\n" +
            "\t\t\tFIN\n" +
            "\t\tFIN\n" +
            "\tFIN\n" +
            "FIN"
        );

        JScrollPane scrollCodigo = new JScrollPane(areaCodigoEntrada);
        panelCodigo.add(scrollCodigo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Control"));

        btnAnalizar = new JButton("Analizar léxicamente");
        btnAnalizar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAnalizar.setPreferredSize(new Dimension(190, 35));
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
        tablaTokens.setFont(new Font("Courier New", Font.PLAIN, 11));
        tablaTokens.setRowHeight(20);
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(50);
        tablaTokens.getColumnModel().getColumn(4).setPreferredWidth(40);
        tablaTokens.getColumnModel().getColumn(5).setPreferredWidth(40);

        JScrollPane scrollTokens = new JScrollPane(tablaTokens);
        panelTokens.add(scrollTokens, BorderLayout.CENTER);

        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Log"));
        areaLog = new JTextArea();
        areaLog.setFont(new Font("Courier New", Font.PLAIN, 11));
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(30, 30, 30));
        areaLog.setForeground(new Color(0, 200, 0));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        panelLog.add(scrollLog, BorderLayout.CENTER);

        JSplitPane splitCentral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTokens, panelLog);
        splitCentral.setDividerLocation(620);
        splitCentral.setResizeWeight(0.65);

        panel.add(splitCentral, BorderLayout.CENTER);
        return panel;
    }

    private void analizar(ActionEvent e) {
        String codigo = areaCodigoEntrada.getText();
        areaLog.setText("");
        modeloTabla.setRowCount(0);
        TablaErrores.limpiar();

        if (codigo.trim().isEmpty()) {
            log("No hay código para analizar", new Color(200, 150, 0));
            etiquetaEstado.setText("Sin código");
            etiquetaEstado.setForeground(new Color(150, 100, 0));
            return;
        }

        log("═══ ANÁLISIS LÉXICO ═══", new Color(0, 150, 255));
        try {
            Tokenizador tokenizador = new Tokenizador(codigo);
            List<Token> tokens = tokenizador.tokenizar();

            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                String lexemaId = obtenerLexemaId(token);
                Object[] fila = {i, token.getTipo().toString(), token.getLexema(), lexemaId, token.getFila(), token.getColumna()};
                modeloTabla.addRow(fila);
            }

            log("Tokens generados: " + tokens.size(), new Color(0, 200, 0));
            log("  Tipos: " + contarTipos(tokens), new Color(0, 180, 0));

            mostrarErroresTabla();

            if (TablaErrores.tieneErrores()) {
                etiquetaEstado.setText("✗ Análisis léxico con errores");
                etiquetaEstado.setForeground(new Color(200, 0, 0));
            } else {
                etiquetaEstado.setText("✓ Análisis léxico completado: " + tokens.size() + " tokens");
                etiquetaEstado.setForeground(new Color(0, 100, 0));
            }

        } catch (Exception ex) {
            log("✗ Error léxico: " + ex.getMessage(), new Color(255, 60, 60));
            mostrarErroresTabla();
            etiquetaEstado.setText("✗ Error léxico");
            etiquetaEstado.setForeground(new Color(200, 0, 0));
        }
    }

    private void mostrarErroresTabla() {
        List<ErrorInfo> errores = TablaErrores.obtenerErrores();
        if (!errores.isEmpty()) {
            log("", Color.WHITE);
            log("═══ DIAGNÓSTICOS ═══", new Color(255, 180, 0));
            for (ErrorInfo err : errores) {
                Color c = err.getTipo() == ErrorInfo.TipoError.ADVERTENCIA
                    ? new Color(255, 200, 0)
                    : new Color(255, 80, 80);
                log("[" + err.getTipo() + "] Línea " + err.getFila() + ", Col " + err.getColumna() + ": " + err.getMensaje(), c);
                if (err.tieneSugerencia()) {
                    log("  → " + err.getSugerencia(), new Color(255, 180, 0));
                }
            }
            log("", Color.WHITE);
            int advertencias = TablaErrores.cantidadAdvertencias();
            int errs = TablaErrores.cantidadErrores() - advertencias;
            log("Total: " + errs + " error(es), " + advertencias + " advertencia(s)", new Color(200, 200, 200));
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
        StringBuilder s = new StringBuilder();
        if (reservadas > 0) s.append(reservadas).append(" reservadas, ");
        if (identificadores > 0) s.append(identificadores).append(" identificadores, ");
        if (numeros > 0) s.append(numeros).append(" números, ");
        if (textos > 0) s.append(textos).append(" textos, ");
        if (teclas > 0) s.append(teclas).append(" teclas, ");
        if (operadores > 0) s.append(operadores).append(" operadores, ");
        if (delimitadores > 0) s.append(delimitadores).append(" delimitadores, ");
        String result = s.toString();
        return result.endsWith(", ") ? result.substring(0, result.length() - 2) : result;
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
