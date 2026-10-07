package battle;

import model.Type;

public final class TypeEffectiveness {

    public static double multiplier(Type attack, Type defense) {
        if (beats(attack, defense)) return 1.3;
        if (beats(defense, attack)) return 0.7;
        return 1.0;
    }

    private static boolean beats(Type a, Type d) {
        return (a == Type.WATER && d == Type.FIRE)
                || (a == Type.FIRE  && d == Type.GRASS)
                || (a == Type.GRASS && d == Type.WATER);
    }

    private TypeEffectiveness() {}
}
