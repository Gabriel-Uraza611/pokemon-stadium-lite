package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PokemonTest {

    @Test
    void testTakeDamageReducesHpCorrectly() {
        Pokemon pokemon = new Pokemon();
        pokemon.setMaxHp(100);
        pokemon.setCurrentHp(100);

        pokemon.takeDamage(30);

        assertEquals(70, pokemon.getCurrentHp());
        assertFalse(pokemon.isFainted());
    }
    @Test
    void testTakeDamageNeverDropsBelowZero() {

        Pokemon pokemon = new Pokemon();
        pokemon.setMaxHp(100);
        pokemon.setCurrentHp(50);

        pokemon.takeDamage(120);

        assertEquals(0, pokemon.getCurrentHp());
        assertTrue(pokemon.isFainted());
    }
}
