import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Main extends JFrame {
    private JTextArea areaCodigoEntrada;
    private JTable tablaTokens;
    private DefaultTableModel modeloTabla;
    private JButton btnTokenizar;
    private JLabel etiquetaEstado;

    public Main() {
        setTitle("Tokenizador - Analizador Léxico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Panel superior: Entrada de código
        JPanel panelSuperior = crearPanelEntrada();
        
        // Panel inferior: Tabla de tokens
        JPanel panelInferior = crearPanelTokens();
        
        // Panel con botón
        JPanel panelBotones = crearPanelBotones();

        // Agregar paneles al frame
        this.add(panelSuperior, BorderLayout.NORTH);
        this.add(panelInferior, BorderLayout.CENTER);
        this.add(panelBotones, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel crearPanelEntrada() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Código Fuente"));
        panel.setPreferredSize(new Dimension(0, 200));

        areaCodigoEntrada = new JTextArea();
        areaCodigoEntrada.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaCodigoEntrada.setLineWrap(true);
        areaCodigoEntrada.setWrapStyleWord(true);
        areaCodigoEntrada.setText("JUEGO iniciar\nPERSONAJE x 100\nMOVER DERECHA\nFIN");

        JScrollPane scrollEntrada = new JScrollPane(areaCodigoEntrada);
        panel.add(scrollEntrada, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelTokens() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tokens Identificados"));

        // Crear tabla con columnas: TokenId, Tipo, Lexema, LexemaId, Fila, Columna
        String[] columnas = {"TokenId", "Tipo", "Lexema", "LexemaId", "Fila", "Columna"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };

        tablaTokens = new JTable(modeloTabla);
        tablaTokens.setFont(new Font("Courier New", Font.PLAIN, 11));
        tablaTokens.setRowHeight(25);
        
        // Ancho de columnas
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(60);   // TokenId
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(100);  // Tipo
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(150);  // Lexema
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(80);   // LexemaId
        tablaTokens.getColumnModel().getColumn(4).setPreferredWidth(60);   // Fila
        tablaTokens.getColumnModel().getColumn(5).setPreferredWidth(80);   // Columna

        JScrollPane scrollTabla = new JScrollPane(tablaTokens);
        panel.add(scrollTabla, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Control"));

        btnTokenizar = new JButton("Tokenizar");
        btnTokenizar.setFont(new Font("Arial", Font.BOLD, 12));
        btnTokenizar.setPreferredSize(new Dimension(120, 40));
        btnTokenizar.addActionListener(this::tokenizarCodigio);

        etiquetaEstado = new JLabel("Listo");
        etiquetaEstado.setFont(new Font("Arial", Font.PLAIN, 11));

        panel.add(btnTokenizar);
        panel.add(etiquetaEstado);

        return panel;
    }

    private void tokenizarCodigio(ActionEvent e) {
        String codigo = areaCodigoEntrada.getText();
        
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese código", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);

            // Tokenizar
            Tokenizador tokenizador = new Tokenizador(codigo);
            List<Token> tokens = tokenizador.tokenizar();

            // Llenar tabla
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                String lexemaId = obtenerLexemaId(token);
                
                Object[] fila = {
                    i,                           // TokenId
                    token.getTipo().toString(),  // Tipo
                    token.getLexema(),          // Lexema
                    lexemaId,                   // LexemaId
                    token.getFila(),            // Fila
                    token.getColumna()          // Columna
                };
                
                modeloTabla.addRow(fila);
            }

            etiquetaEstado.setText("✓ " + tokens.size() + " tokens identificados correctamente");
            etiquetaEstado.setForeground(new Color(0, 100, 0));

        } catch (Exception ex) {
            etiquetaEstado.setText("✗ Error: " + ex.getMessage());
            etiquetaEstado.setForeground(new Color(200, 0, 0));
            JOptionPane.showMessageDialog(this, "Error al tokenizar:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            
            case NUMERO:
            case TEXTO:
            case IDENTIFICADOR:
                return "-"; // No tienen ID predefinido
            
            default:
                return "N/A";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
