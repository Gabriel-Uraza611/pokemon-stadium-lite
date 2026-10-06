package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.BasicStroke;
import java.awt.RenderingHints;

public class PokemonCard extends JPanel {
    private JLabel nameLabel;
    private JLabel typeLabel;
    private JProgressBar hpBar;
    private JLabel hpLabel;
    private JLabel spriteLabel;
    private JLabel atkLabel, defLabel, spdLabel;

    public PokemonCard(Color background) {
        setLayout(new BorderLayout());
        setBackground(background);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(8, 8, 8, 8)));
        setPreferredSize(new Dimension(350, 345));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(background), BorderLayout.CENTER);
    }

    // ---------- ZONA SUPERIOR: nombre, tipo y vida ----------
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setBackground(new Color(226, 232, 228)); // Color del panel de log
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 2),
                new EmptyBorder(6, 8, 6, 8)));

        // Fila 1: nombre (izquierda) + tipo (derecha)
        JPanel nameRow = new JPanel(new BorderLayout());
        nameRow.setOpaque(false);

        nameLabel = new JLabel("charmander");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));

        typeLabel = new JLabel("WATER", SwingConstants.CENTER);
        typeLabel.setOpaque(true);
        typeLabel.setBackground(new Color(60, 120, 220));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        typeLabel.setBorder(new EmptyBorder(2, 8, 2, 8));

        nameRow.add(nameLabel, BorderLayout.WEST);
        nameRow.add(typeLabel, BorderLayout.EAST);

        // Fila 2: barra de vida (simple, verde, borde negro 1px)
        hpBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // Fondo
                g2.setColor(new Color(220, 220, 220));
                g2.fillRect(0, 0, w, h);
                // Progreso verde
                int pw = (int) (w * getPercentComplete());
                if (pw > 0) {
                    g2.setColor(new Color(99, 255, 143));
                    g2.fillRect(0, 0, pw, h);
                }
                // Borde negro 1px
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1));
                g2.drawRect(0, 0, w - 1, h - 1);
                g2.dispose();
            }
        };
        hpBar.setValue(100);
        hpBar.setStringPainted(false);
        hpBar.setOpaque(false);
        hpBar.setPreferredSize(new Dimension(0, 14));
        hpBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // Fila 3: texto HP
        hpLabel = new JLabel("HP: 100/100");
        hpLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel barAndText = new JPanel(new BorderLayout(0, 2));
        barAndText.setOpaque(false);
        barAndText.add(hpBar, BorderLayout.NORTH);
        barAndText.add(hpLabel, BorderLayout.CENTER);

        top.add(nameRow, BorderLayout.NORTH);
        top.add(barAndText, BorderLayout.CENTER);
        return top;
    }

    // ---------- ZONA CENTRAL: sprite + stats ----------
    private JPanel buildCenterPanel(Color background) {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(background);
        center.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Sprite a la izquierda (ocupa todo el espacio sobrante)
        spriteLabel = new JLabel("", SwingConstants.CENTER);
        // spriteLabel.setIcon(new ImageIcon("resources/squirtle.png"));

        // Stats a la derecha, abajo (como en la guía)
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(226, 232, 228)); // Color del panel de log
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(8, 8, 8, 8)));
        statsPanel.setPreferredSize(new Dimension(120, 130));

        JLabel title = new JLabel("STATS:");
        atkLabel = new JLabel("- ATK: XXX");
        defLabel = new JLabel("- DEF: XXX");
        spdLabel = new JLabel("- SPD: XXX");

        for (JLabel l : new JLabel[]{title, atkLabel, defLabel, spdLabel}) {
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPanel.add(l);
            statsPanel.add(Box.createVerticalStrut(4));
        }

        // Wrapper para alinear los stats abajo a la derecha
        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setOpaque(false);
        statsWrapper.add(statsPanel, BorderLayout.SOUTH);

        center.add(spriteLabel, BorderLayout.CENTER);
        center.add(statsWrapper, BorderLayout.EAST);
return center;
    }

    // ---------- MÉTODOS PARA ACTUALIZAR DESDE EL JUEGO ----------
    public void setHp(int hp, int maxHp) {
        hpBar.setMaximum(maxHp);
        hpBar.setValue(hp);
        hpLabel.setText("HP: " + hp + "/" + maxHp);
        hpBar.repaint();
    }
}