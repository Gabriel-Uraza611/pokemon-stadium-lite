package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LogPanel extends JScrollPane {

    private JTextArea logArea;

    public LogPanel() {
        // Área de texto para el log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(Color.BLACK);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding interno

        // Configurar el JScrollPane
        setViewportView(logArea);
        setBackground(Color.WHITE);
        getViewport().setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(0, 0, 0, 0)));
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setPreferredSize(new Dimension(340, 0)); // Ancho fijo, alto flexible
    }

    /**
     * Añade una línea al log con timestamp opcional.
     */
    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            // Auto-scroll al final
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }


    public void clear() {
        SwingUtilities.invokeLater(() -> logArea.setText(""));
    }

    public JTextArea getLogArea() {
        return logArea;
    }
}