package controller;

import battle.Battle;
import battle.BattleListener;
import model.Move;
import model.Pokemon;
import ui.LogPanel;
import ui.MovesContainer;
import ui.PokemonCard;
import java.util.function.Consumer;
/**
 * Conecta Battle con la UI. Todo ocurre en el EDT (cada ataque lo dispara un clic),
 * así que no hay hilos ni invokeLater aquí.
 */
public class BattleController {

    private final PokemonCard card1, card2;
    private final MovesContainer moves1, moves2;
    private final LogPanel log;

    private Battle battle;
    private Pokemon p1, p2;
    private Consumer<String> onFinished;   // antes: Runnable

    public BattleController(PokemonCard card1, PokemonCard card2,
                            MovesContainer moves1, MovesContainer moves2, LogPanel log) {
        this.card1 = card1;
        this.card2 = card2;
        this.moves1 = moves1;
        this.moves2 = moves2;
        this.log = log;

        // Cualquier botón de movimiento llega aquí; solo el jugador activo los tiene habilitados
        moves1.setMoveListener(this::onMoveChosen);
        moves2.setMoveListener(this::onMoveChosen);
    }

    public void start(Pokemon p1, Pokemon p2, Consumer<String> onFinished){
        this.p1 = p1;
        this.p2 = p2;
        this.onFinished = onFinished;

        battle = new Battle(p1, p2);
        battle.addListener(new BattleListener() {

            @Override
            public void onTurnStart(String pokemon) {
                boolean p1Turn = pokemon.equals(battle.getName1());
                moves1.setActive(p1Turn);
                moves2.setActive(!p1Turn);
                log.addLog("\n▶ Turno de " + pokemon + ": elige un movimiento");
            }

            @Override
            public void onMoveUsed(String attacker, String moveName) {
                log.addLog(attacker + " usa " + moveName.replace('-', ' ') + "!");
            }

            @Override
            public void onTurn(String attacker, String defender, int damage,
                               boolean critical, double modifier) {
                if (damage == 0) {
                    log.addLog("  Pero no tuvo efecto...");
                    return;
                }
                StringBuilder sb = new StringBuilder("  → " + damage + " de daño a " + defender);
                if (critical)     sb.append("  ¡CRÍTICO!");
                if (modifier > 1) sb.append("  (muy eficaz)");
                if (modifier < 1) sb.append("  (poco eficaz)");
                log.addLog(sb.toString());
            }

            @Override
            public void onHpChanged(String pokemon, int hpActual) {
                if (pokemon.equals(battle.getName1())) card1.setHp(hpActual, p1.getMaxHp());
                else                                    card2.setHp(hpActual, p2.getMaxHp());
                log.addLog("  " + pokemon + ": " + hpActual + " HP");
            }

            @Override
            public void onBattleEnded(String winner) {
                moves1.setActive(false);
                moves2.setActive(false);
                log.addLog("\n" + winner + " gana el combate!\n");
                if (BattleController.this.onFinished != null) BattleController.this.onFinished.accept(winner);
            }
        });

        log.addLog("=== " + battle.getName1() + " VS " + battle.getName2() + " ===");
        battle.start();
    }

    private void onMoveChosen(Move move) {
        if (battle != null) battle.useMove(move);
    }
}