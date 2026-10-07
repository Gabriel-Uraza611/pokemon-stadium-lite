package battle;

import model.Move;
import model.Pokemon;

import java.util.Random;

public class DamageCalculator {

    private static final double CRIT_CHANCE = 0.10;
    private static final double CRIT_MULTIPLIER = 1.5;

    private final Random random;

    public DamageCalculator(Random random) {
        this.random = random;
    }

    /**
     * base   = (power * ATK / DEF) / 5 + 2
     * daño   = base * variación(0.85-1.0) * crítico(10% -> x1.5) * efectividad(x1.3 / x0.7 / x1.0)
     * La efectividad compara el tipo del movimiento con el primer tipo del defensor.
     */
    public DamageResult calculate(Pokemon attacker, Pokemon defender, Move move) {
        // Movimiento sin poder (de estado): no hace daño
        if (move.getPower() <= 0) {
            return new DamageResult(0, false, 1.0);
        }

        double base = (move.getPower() * (double) attacker.getAttack()
                / Math.max(1, defender.getDefence())) / 5.0 + 2;

        double variance = 0.85 + random.nextDouble() * 0.15;
        boolean critical = random.nextDouble() < CRIT_CHANCE;
        double effectiveness = TypeEffectiveness.multiplier(move.getType(), defender.getTypes().get(0));

        double total = base * variance * effectiveness * (critical ? CRIT_MULTIPLIER : 1.0);
        int damage = Math.max(1, (int) Math.round(total));

        return new DamageResult(damage, critical, effectiveness);
    }
}