package battle;

import model.Move;
import model.Pokemon;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Reglas del combate por turnos. No conoce Swing: solo emite eventos por BattleListener.
 * Flujo: start() -> (useMove() por cada turno) -> onBattleEnded.
 */
public class Battle {

    private final Pokemon p1, p2;
    private final String name1, name2;
    private final Random random;
    private final DamageCalculator calculator;
    private final List<BattleListener> listeners = new CopyOnWriteArrayList<>();

    private Pokemon attacker, defender;
    private boolean started, finished;

    public Battle(Pokemon p1, Pokemon p2) {
        this(p1, p2, new Random());
    }

    // Random inyectable para pruebas deterministas
    public Battle(Pokemon p1, Pokemon p2, Random random) {
        this.p1 = p1;
        this.p2 = p2;
        this.random = random;
        this.calculator = new DamageCalculator(random);

        // Si ambos se llaman igual, se distinguen para no confundir los eventos
        boolean same = p1.getName().equalsIgnoreCase(p2.getName());
        this.name1 = same ? p1.getName() + " (J1)" : p1.getName();
        this.name2 = same ? p2.getName() + " (J2)" : p2.getName();
    }

    public void addListener(BattleListener l)    { listeners.add(l); }
    public void removeListener(BattleListener l) { listeners.remove(l); }

    public String getName1() { return name1; }
    public String getName2() { return name2; }
    public boolean isFinished() { return finished; }

    /** Restaura HP, decide quién empieza (mayor Speed; empate -> azar) y anuncia el primer turno. */
    public void start() {
        p1.heal();
        p2.heal();
        started = true;
        finished = false;

        listeners.forEach(l -> l.onHpChanged(name1, p1.getCurrentHp()));
        listeners.forEach(l -> l.onHpChanged(name2, p2.getCurrentHp()));

        boolean p1First = p1.getSpeed() > p2.getSpeed()
                || (p1.getSpeed() == p2.getSpeed() && random.nextBoolean());
        attacker = p1First ? p1 : p2;
        defender = p1First ? p2 : p1;

        String first = nameOf(attacker);
        listeners.forEach(l -> l.onTurnStart(first));
    }

    /** El Pokémon al que le toca ataca con el movimiento elegido. Luego cambia el turno. */
    public void useMove(Move move) {
        if (!started || finished) return;

        Pokemon atk = attacker, def = defender;
        String atkName = nameOf(atk), defName = nameOf(def);

        DamageResult r = calculator.calculate(atk, def, move);
        def.takeDamage(r.damage());   // el HP nunca baja de 0

        listeners.forEach(l -> l.onMoveUsed(atkName, move.getName()));
        listeners.forEach(l -> l.onTurn(atkName, defName, r.damage(), r.critical(), r.modifier()));
        listeners.forEach(l -> l.onHpChanged(defName, def.getCurrentHp()));

        if (def.isFainted()) {
            finished = true;
            listeners.forEach(l -> l.onBattleEnded(atkName));
            return;
        }

        // cambio de turno
        attacker = def;
        defender = atk;
        listeners.forEach(l -> l.onTurnStart(defName));
    }

    private String nameOf(Pokemon p) {
        return p == p1 ? name1 : name2;
    }
}