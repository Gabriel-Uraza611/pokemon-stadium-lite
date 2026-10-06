package ui;

import model.Type;
import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

public final class TypeColors {
    private static final Map<Type, Color> COLORS = new EnumMap<>(Type.class);

    static {
        put(Type.NORMAL, "#A8A878");   put(Type.FIRE, "#F08030");
        put(Type.WATER, "#6890F0");    put(Type.ELECTRIC, "#F8D030");
        put(Type.GRASS, "#78C850");    put(Type.ICE, "#98D8D8");
        put(Type.FIGHTING, "#C03028"); put(Type.POISON, "#A040A0");
        put(Type.GROUND, "#E0C068");   put(Type.FLYING, "#A890F0");
        put(Type.PSYCHIC, "#F85888");  put(Type.BUG, "#A8B820");
        put(Type.ROCK, "#B8A038");     put(Type.GHOST, "#705898");
        put(Type.DRAGON, "#7038F8");   put(Type.DARK, "#705848");
        put(Type.STEEL, "#B8B8D0");    put(Type.FAIRY, "#EE99AC");
    }

    private static void put(Type t, String hex) { COLORS.put(t, Color.decode(hex)); }

    public static Color of(Type t) { return COLORS.getOrDefault(t, Color.GRAY); }

    private TypeColors() {}
}