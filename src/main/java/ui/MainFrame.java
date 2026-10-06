package ui;

import api.PokeApiClient;
import controller.AudioController;
import controller.PokemonLoadController;
import model.Pokemon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.util.function.Consumer;

public class MainFrame extends JFrame {

    private JPanel leftBattlePanel;
    private JPanel rightLogPanel;
    private LogPanel logPanel;
    private JPanel playerOnePanel;
    private JPanel playerTwoPanel;
    private PokemonCard pokemonBoxP1;
    private PokemonCard pokemonBoxP2;
    private SearchPanel searchPanelP1;
    private SearchPanel searchPanelP2;
    private MovesContainer movesContainerP1;
    private MovesContainer movesContainerP2;
    private JButton fightButton;
    private final PokemonLoadController loadController = new PokemonLoadController(new PokeApiClient());
    private Pokemon pokemonP1, pokemonP2;

    private final AudioController audioController = new AudioController();

    private void wirePlayer(SearchPanel search, PokemonCard card, MovesContainer moves,
                            Consumer<Pokemon> store) {

        Consumer<Pokemon> onSuccess = p -> {
            card.showPokemon(p);
            moves.setMoves(p.getMoves());
            search.setText(p.getName());           // útil cuando viene de Random
            store.accept(p);
            fightButton.setEnabled(pokemonP1 != null && pokemonP2 != null);
        };

        Consumer<String> onError = msg -> logPanel.addLog(msg);

        search.addLoadListener(e ->
                loadController.loadByName(search.getText(), onSuccess, onError));

        search.addRandomListener(e ->
                loadController.loadRandom(onSuccess, onError));
    }

    public MainFrame() {
        setTitle("Pokemon Stadium Lite");
        setSize(1080, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        leftBattlePanel = new JPanel();
        leftBattlePanel.setLayout(new GridLayout(2, 1, 0, 0));

        // ----------------------------------------------------
        // JUGADOR 1 (Arriba - Azul pastel)
        // ----------------------------------------------------
        playerOnePanel = new JPanel();
        playerOnePanel.setBackground(new Color(42, 117, 187));
        playerOnePanel.setLayout(new BorderLayout(5, 5));
        playerOnePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(5, 5, 5, 5)));

        pokemonBoxP1 = new PokemonCard(new Color(207, 220, 255));

        JPanel rightPanelP1 = new JPanel();
        rightPanelP1.setLayout(new GridLayout(2, 1, 0, 5));
        rightPanelP1.setOpaque(false);

        movesContainerP1 = new MovesContainer(new Color(207, 220, 255), new Color(42, 117, 187));
        searchPanelP1 = new SearchPanel(new Color(207, 220, 255));

        rightPanelP1.add(movesContainerP1);
        rightPanelP1.add(searchPanelP1);

        playerOnePanel.add(pokemonBoxP1, BorderLayout.WEST);
        playerOnePanel.add(rightPanelP1, BorderLayout.CENTER);

        // ----------------------------------------------------
        // JUGADOR 2 (Abajo - Rosa pastel)
        // ----------------------------------------------------
        playerTwoPanel = new JPanel();
        playerTwoPanel.setBackground(new Color(201, 25, 48));
        playerTwoPanel.setLayout(new BorderLayout(5, 5));
        playerTwoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(5, 5, 5, 5)));

        pokemonBoxP2 = new PokemonCard(new Color(255, 212, 212));

        JPanel rightPanelP2 = new JPanel();
        rightPanelP2.setLayout(new GridLayout(2, 1, 0, 5));
        rightPanelP2.setOpaque(false);

        movesContainerP2 = new MovesContainer(new Color(255, 212, 212), new Color(201, 25, 48));
        searchPanelP2 = new SearchPanel(new Color(255, 212, 212));

        rightPanelP2.add(movesContainerP2);
        rightPanelP2.add(searchPanelP2);

        playerTwoPanel.add(rightPanelP2, BorderLayout.CENTER);
        playerTwoPanel.add(pokemonBoxP2, BorderLayout.EAST);

        leftBattlePanel.add(playerOnePanel);
        leftBattlePanel.add(playerTwoPanel);

        add(leftBattlePanel, BorderLayout.CENTER);

        // ----------------------------------------------------
        // PANEL DERECHO (LOG) + BOTÓN FIGHT! + LOGO
        // ----------------------------------------------------
        rightLogPanel = new JPanel();
        rightLogPanel.setLayout(new BorderLayout());
        rightLogPanel.setBackground(new Color(226, 232, 228));
        rightLogPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(2, 10, 10, 10))); // Top padding reducido a 2px
        rightLogPanel.setPreferredSize(new Dimension(360, 0));

        // Logo arriba - escalado a ~170px de ancho
        JLabel logoLabel = createLogoLabel();
        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoWrapper.setOpaque(false);
        logoWrapper.add(logoLabel);
        rightLogPanel.add(logoWrapper, BorderLayout.NORTH);

        // LogPanel con scroll - ocupa el centro
        logPanel = new LogPanel();
        rightLogPanel.add(logPanel, BorderLayout.CENTER);

        // Botón FIGHT! - centrado abajo
        fightButton = createFightButton();
        JPanel fightButtonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        fightButtonWrapper.setOpaque(false);
        fightButtonWrapper.add(fightButton);
        rightLogPanel.add(fightButtonWrapper, BorderLayout.SOUTH);

        add(rightLogPanel, BorderLayout.EAST);

        // Iniciar música de selección
        audioController.playLoop("audio/selection.wav");

        // Listener del botón FIGHT!
        fightButton.addActionListener(e -> onFightPressed());
        fightButton.setEnabled(false);
        wirePlayer(searchPanelP1, pokemonBoxP1, movesContainerP1, p -> pokemonP1 = p);
        wirePlayer(searchPanelP2, pokemonBoxP2, movesContainerP2, p -> pokemonP2 = p);


        setVisible(true);
    }

    private JButton createFightButton() {
        JButton button = new JButton("FIGHT!");
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(201, 25, 48)); // Rojo jugador 2
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(14, 40, 14, 40)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void onFightPressed() {
        audioController.switchTo("audio/battle.wav");
        // TODO: Aquí irá la lógica de iniciar combate
        System.out.println("¡COMBATE INICIADO!");
    }

    private JLabel createLogoLabel() {
        // Cargar imagen desde resources
        ImageIcon originalIcon = new ImageIcon(
            getClass().getClassLoader().getResource("img/logo.png")
        );

        if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            // Fallback si no carga
            JLabel fallback = new JLabel("POKÉMON STADIUM LITE");
            fallback.setFont(new Font("SansSerif", Font.BOLD, 18));
            fallback.setForeground(new Color(42, 117, 187));
            return fallback;
        }

        // Escalar a ~220px de ancho (bien grande)
        int targetWidth = 220;
        int targetHeight = (int) (targetWidth * (originalIcon.getIconHeight() / (double) originalIcon.getIconWidth()));
        Image scaledImage = originalIcon.getImage().getScaledInstance(
            targetWidth, targetHeight, Image.SCALE_SMOOTH
        );
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return logoLabel;
    }

    @Override
    public void dispose() {
        audioController.stop();
        super.dispose();
    }

    public LogPanel getLogPanel() {
        return logPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}