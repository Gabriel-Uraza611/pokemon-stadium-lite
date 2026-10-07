package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Move;
import java.util.List;
import model.Move;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MovesContainer extends JPanel {

    private JButton move1Button;
    private JButton move2Button;
    private JButton move3Button;
    private JButton move4Button;

    private final Color playerColor;
    private final Color movesBackgroundColor;
    private final JButton[] moveButtons;
    private List<Move> currentMoves = new ArrayList<>();
    private Consumer<Move> moveListener;
    private boolean active = false;   // los botones solo se activan en el turno del jugador

    public MovesContainer(Color background, Color playerColor) {
        this.movesBackgroundColor = background;
        this.playerColor = playerColor;

        setLayout(new GridLayout(2, 2, 0, 0));
        setBackground(background);
        // Contenedor SIN borde - los botones dibujan todo
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(380, 140));

        move1Button = createMoveButton("ataque 1", BorderPosition.TOP_LEFT);
        move2Button = createMoveButton("ataque 2", BorderPosition.TOP_RIGHT);
        move3Button = createMoveButton("ataque 3", BorderPosition.BOTTOM_LEFT);
        move4Button = createMoveButton("ataque 4", BorderPosition.BOTTOM_RIGHT);
        moveButtons = new JButton[]{move1Button, move2Button, move3Button, move4Button};
        add(move1Button);
        add(move2Button);
        add(move3Button);
        add(move4Button);
        setMoves(null);
        for (int i = 0; i < moveButtons.length; i++) {
            final int index = i;
            moveButtons[i].addActionListener(e -> {
                if (moveListener != null && index < currentMoves.size()) {
                    moveListener.accept(currentMoves.get(index));
                }
            });
        }
        setMoves(null);
    }

    /** Muestra hasta 4 movimientos; los botones sobrantes quedan vacíos y deshabilitados. */
    public void setMoves(List<Move> moves) {
        currentMoves = (moves == null) ? new ArrayList<>() : new ArrayList<>(moves);
        for (int i = 0; i < moveButtons.length; i++) {
            if (i < currentMoves.size()) {
                Move m = currentMoves.get(i);
                moveButtons[i].setText(prettify(m.getName()));
                moveButtons[i].setToolTipText(m.getType() + " · poder " + m.getPower());
            } else {
                moveButtons[i].setText("-");
                moveButtons[i].setToolTipText(null);
            }
        }
        refreshEnabled();
    }

    /** true = es el turno de este jugador y puede elegir un movimiento. */
    public void setActive(boolean active) {
        this.active = active;
        refreshEnabled();
    }

    public void setMoveListener(Consumer<Move> listener) {
        this.moveListener = listener;
    }

    private void refreshEnabled() {
        for (int i = 0; i < moveButtons.length; i++) {
            moveButtons[i].setEnabled(active && i < currentMoves.size());
        }
    }

    // "thunder-punch" -> "Thunder Punch"
    private String prettify(String apiName) {
        StringBuilder sb = new StringBuilder();
        for (String part : apiName.split("-")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    private enum BorderPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private JButton createMoveButton(String text, BorderPosition pos) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(movesBackgroundColor);
        button.setForeground(Color.DARK_GRAY);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Borde preciso según posición: líneas internas 1px, exterior 2px
        button.setBorder(createBorder(pos));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(playerColor);
                button.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(movesBackgroundColor);
                button.setForeground(Color.DARK_GRAY);
            }
        });

        return button;
    }

    private Border createBorder(BorderPosition pos) {
        // MatteBorder(top, left, bottom, right)
        return switch (pos) {
            case TOP_LEFT -> new MatteBorder(2, 2, 1, 1, Color.BLACK); // ext top/left, int bottom/right
            case TOP_RIGHT -> new MatteBorder(2, 1, 1, 2, Color.BLACK); // ext top/right
            case BOTTOM_LEFT -> new MatteBorder(1, 2, 2, 1, Color.BLACK); // ext left/bottom
            case BOTTOM_RIGHT -> new MatteBorder(1, 1, 2, 2, Color.BLACK); // ext right/bottom
            default -> new MatteBorder(1, 1, 1, 1, Color.BLACK);
        };
    }

    public JButton getMove1Button() { return move1Button; }
    public JButton getMove2Button() { return move2Button; }
    public JButton getMove3Button() { return move3Button; }
    public JButton getMove4Button() { return move4Button; }
}