package ui;

import model.Pokemon;
import model.Type;

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

        clear(); // estado inicial: todo vacío ("-")
    }

    // ---------- ZONA SUPERIOR: nombre, tipo y vida ----------
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setBackground(new Color(226, 232, 228));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 2),
                new EmptyBorder(6, 8, 6, 8)));

        // Fila 1: nombre (izquierda) + tipo (derecha)
        JPanel nameRow = new JPanel(new BorderLayout());
        nameRow.setOpaque(false);

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));

        typeLabel = new JLabel("", SwingConstants.CENTER);
        typeLabel.setOpaque(true);
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
        hpBar.setStringPainted(false);
        hpBar.setOpaque(false);
        hpBar.setPreferredSize(new Dimension(0, 14));
        hpBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // Fila 3: texto HP
        hpLabel = new JLabel();
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

        // Sprite (ocupa todo el espacio sobrante)
        spriteLabel = new JLabel("", SwingConstants.CENTER);

        // Stats a la derecha, abajo
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(226, 232, 228));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(8, 8, 8, 8)));
        statsPanel.setPreferredSize(new Dimension(120, 130));

        JLabel title = new JLabel("STATS:");
        atkLabel = new JLabel();
        defLabel = new JLabel();
        spdLabel = new JLabel();

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

    public void showPokemon(Pokemon p) {
        nameLabel.setText(p.getName());

        Type mainType = p.getTypes().getFirst();
        typeLabel.setText(mainType.name());
        typeLabel.setBackground(TypeColors.of(mainType));

        atkLabel.setText("- ATK: " + p.getAttack());
        defLabel.setText("- DEF: " + p.getDefence());
        spdLabel.setText("- SPD: " + p.getSpeed());

        setHp(p.getCurrentHp(), p.getMaxHp());

        Image img = p.getSpriteImage();
        if (img != null) {
            // el sprite es de 96px; SCALE_FAST mantiene el look pixelado
            spriteLabel.setIcon(new ImageIcon(img.getScaledInstance(192, 192, Image.SCALE_FAST)));
        } else {
            spriteLabel.setIcon(null);
        }
    }

    /** Devuelve la tarjeta a su estado inicial (sin datos). También se usa en el constructor. */
    public void clear() {
        nameLabel.setText("-");
        typeLabel.setText("-");
        typeLabel.setBackground(Color.LIGHT_GRAY);
        atkLabel.setText("- ATK: -");
        defLabel.setText("- DEF: -");
        spdLabel.setText("- SPD: -");
        hpBar.setMaximum(100);
        hpBar.setValue(100);   // la barra arranca llena
        hpLabel.setText("HP: -/-");
        spriteLabel.setIcon(null);
    }
}