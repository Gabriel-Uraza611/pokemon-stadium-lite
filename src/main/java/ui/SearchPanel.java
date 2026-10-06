package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchPanel extends JPanel {
    private JTextField nameField;
    private JButton loadButton;
    private JButton randomButton;

    public SearchPanel(Color background) {
        setLayout(new BorderLayout(0, 0));
        setBackground(background);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(12, 16, 12, 16)));
        setPreferredSize(new Dimension(380, 140));

        // Panel contenedor principal que centra verticalmente
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Panel con el contenido (name row + botones)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Name row
        JPanel nameRow = new JPanel(new BorderLayout(8, 0));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel nameLabel = new JLabel("name");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        nameRow.add(nameLabel, BorderLayout.WEST);
        nameRow.add(nameField, BorderLayout.CENTER);
        contentPanel.add(nameRow);

        // Espacio entre name row y botones
        contentPanel.add(Box.createVerticalStrut(16));

        // Botones
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonRow.setOpaque(false);

        loadButton = createButton("Load");
        randomButton = createButton("Random");
        buttonRow.add(loadButton);
        buttonRow.add(randomButton);
        contentPanel.add(buttonRow);

        centerWrapper.add(contentPanel, gbc);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); // BOLD, un poco más grande
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(10, 20, 10, 20))); // Padding un poco mayor
        return button;
    }

    public String getNameText() {
        return nameField.getText().trim();
    }

    public void clearName() {
        nameField.setText("");
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JButton getLoadButton() {
        return loadButton;
    }

    public JButton getRandomButton() {
        return randomButton;
    }
}