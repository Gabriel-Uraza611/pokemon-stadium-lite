package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedProgressBar extends JProgressBar {

    private static final Color GREEN_COLOR = new Color(99, 255, 143);
    private static final Color YELLOW_COLOR = new Color(255, 234, 84);
    private static final Color RED_COLOR = new Color(255, 41, 41);
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final int BORDER_THICKNESS = 1;
    private static final int ARC = 8; // radio de esquinas redondeadas

    public RoundedProgressBar(int min, int max) {
        super(min, max);
        setUI(new RoundedProgressBarUI());
        setStringPainted(false);
        setOpaque(false);
        setBackground(new Color(220, 220, 220));
        setPreferredSize(new Dimension(0, 14));
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    public void setHp(int hp, int maxHp) {
        setMaximum(maxHp);
        setValue(hp);
        updateColor();
    }

    private void updateColor() {
        double ratio = getMaximum() > 0 ? (double) getValue() / getMaximum() : 0;
        if (ratio > 0.5) {
            setForeground(GREEN_COLOR);
        } else if (ratio > 0.2) {
            setForeground(YELLOW_COLOR);
        } else {
            setForeground(RED_COLOR);
        }
        repaint();
    }

    private static class RoundedProgressBarUI extends BasicProgressBarUI {

        @Override
        protected void paintDeterminate(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            JProgressBar bar = (JProgressBar) c;
            int width = bar.getWidth();
            int height = bar.getHeight();
            int x = 0;
            int y = 0;

            // Fondo
            RoundRectangle2D bgRect = new RoundRectangle2D.Float(x, y, width, height, ARC, ARC);
            g2.setColor(bar.getBackground());
            g2.fill(bgRect);

            // Progreso
            int progressWidth = (int) (width * bar.getPercentComplete());
            if (progressWidth > 0) {
                RoundRectangle2D progressRect = new RoundRectangle2D.Float(x, y, progressWidth, height, ARC, ARC);
                g2.setColor(bar.getForeground());
                g2.fill(progressRect);
            }

            // Borde negro
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(BORDER_THICKNESS));
            g2.draw(bgRect);

            g2.dispose();
        }

        @Override
        protected void paintIndeterminate(Graphics g, JComponent c) {
            paintDeterminate(g, c);
        }
    }
}