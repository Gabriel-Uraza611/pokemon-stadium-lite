package battle;

public interface BattleListener {
    void onTurn(String attacker, String defender, int damage, boolean critical, double modifier);
    void onHpChanged(String pokemon, int hpActual);
    void onBattleEnded(String winner);

    /** A quién le toca atacar ahora. */
    default void onTurnStart(String pokemon) {}

    /** Qué movimiento se usó (para el log). */
    default void onMoveUsed(String attacker, String moveName) {}
}