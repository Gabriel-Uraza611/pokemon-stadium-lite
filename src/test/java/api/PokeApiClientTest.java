package api;

import model.Move;
import model.Pokemon;
import model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class PokeApiClientTest {

    private static final String POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final String MOVE_URL = "https://pokeapi.co/api/v2/move/";

    private HttpClient mockHttpClient;
    private PokeApiClient apiClient;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        apiClient = new PokeApiClient(mockHttpClient);
    }

    // ------------------------- Helpers -------------------------

    @SuppressWarnings("unchecked")
    private HttpResponse<String> response(int status, String body) {
        HttpResponse<String> r = mock(HttpResponse.class);
        when(r.statusCode()).thenReturn(status);
        when(r.body()).thenReturn(body);
        return r;
    }

    /** Hace que el cliente falso devuelva `resp` cuando se pida exactamente `url`. */
    private void stubUrl(String url, HttpResponse<String> resp) throws Exception {
        doReturn(resp).when(mockHttpClient)
                .send(argThat((HttpRequest req) -> req != null && req.uri().toString().equals(url)), any());
    }

    private String pokemonJson(int moveCount) {
        StringBuilder moves = new StringBuilder();
        for (int i = 1; i <= moveCount; i++) {
            if (i > 1) moves.append(",");
            moves.append("""
                    { "move": { "name": "move-%d", "url": "%s%d/" } }
                    """.formatted(i, MOVE_URL, i));
        }
        return """
                {
                  "id": 25,
                  "name": "pikachu",
                  "sprites": { "front_default": "https://example.com/pikachu.png" },
                  "stats": [
                    { "base_stat": 35, "stat": { "name": "hp" } },
                    { "base_stat": 55, "stat": { "name": "attack" } },
                    { "base_stat": 40, "stat": { "name": "defense" } },
                    { "base_stat": 90, "stat": { "name": "speed" } }
                  ],
                  "types": [ { "type": { "name": "electric" } } ],
                  "moves": [ %s ]
                }
                """.formatted(moves);
    }

    private String moveJson(String name, String power, String type) {
        // power se pasa como String para poder enviar "null"
        return """
                { "name": "%s", "power": %s, "type": { "name": "%s" } }
                """.formatted(name, power, type);
    }

    // ------------------------- fetchByName: caso feliz -------------------------

    @Test
    void fetchByName_ValidPokemon_ReturnsFullyMappedPokemon() throws Exception {
        stubUrl(POKEMON_URL + "pikachu", response(200, pokemonJson(1)));
        stubUrl(MOVE_URL + "1/", response(200, moveJson("move-1", "40", "electric")));

        Pokemon p = apiClient.fetchByName("pikachu");

        assertEquals(25, p.getId());
        assertEquals("pikachu", p.getName());
        assertEquals("https://example.com/pikachu.png", p.getSprite());
        assertEquals(35, p.getMaxHp());
        assertEquals(35, p.getCurrentHp());   // currentHp arranca igual a maxHp
        assertEquals(55, p.getAttack());
        assertEquals(40, p.getDefence());     // "defense" de la API -> defence
        assertEquals(90, p.getSpeed());
        assertEquals(1, p.getTypes().size());
        assertEquals(Type.ELECTRIC, p.getTypes().get(0));
        assertEquals(1, p.getMove().size());
    }

    @Test
    void fetchByName_NameWithSpacesAndUppercase_IsSanitizedInUrl() throws Exception {
        doReturn(response(404, "")).when(mockHttpClient).send(any(), any());

        assertThrows(PokeApiException.class, () -> apiClient.fetchByName("  PIKACHU  "));

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals(POKEMON_URL + "pikachu", captor.getValue().uri().toString());
    }

    @Test
    void fetchByName_MoreThanFourMoves_OnlyFetchesFour() throws Exception {
        stubUrl(POKEMON_URL + "pikachu", response(200, pokemonJson(6)));
        for (int i = 1; i <= 6; i++) {
            stubUrl(MOVE_URL + i + "/", response(200, moveJson("move-" + i, "50", "normal")));
        }

        Pokemon p = apiClient.fetchByName("pikachu");

        assertEquals(4, p.getMove().size());
        verify(mockHttpClient, times(5)).send(any(), any()); // 1 pokemon + 4 movimientos
    }

    // ------------------------- fetchByName: validación de entrada -------------------------

    @Test
    void fetchByName_NullName_ThrowsWithoutCallingApi() throws Exception {
        assertThrows(PokeApiException.class, () -> apiClient.fetchByName(null));
        verifyNoInteractions(mockHttpClient);
    }

    @Test
    void fetchByName_BlankName_ThrowsWithoutCallingApi() throws Exception {
        assertThrows(PokeApiException.class, () -> apiClient.fetchByName("   "));
        verifyNoInteractions(mockHttpClient);
    }

    // ------------------------- fetchByName: errores HTTP / red -------------------------

    @Test
    void fetchByName_Status404_ThrowsNotFound() throws Exception {
        doReturn(response(404, "Not Found")).when(mockHttpClient).send(any(), any());

        PokeApiException ex = assertThrows(PokeApiException.class, () -> apiClient.fetchByName("missingno"));

        assertTrue(ex.getMessage().contains("missingno"));
        // Si tu excepción expone el tipo (ajusta el nombre del getter):
        // assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
    }

    @Test
    void fetchByName_Status500_ThrowsNetworkError() throws Exception {
        doReturn(response(500, "")).when(mockHttpClient).send(any(), any());

        PokeApiException ex = assertThrows(PokeApiException.class, () -> apiClient.fetchByName("pikachu"));

        assertTrue(ex.getMessage().contains("500"));
        // assertEquals(ErrorType.NETWORK, ex.getErrorType());
    }

    @Test
    void fetchByName_IOException_ThrowsNetworkError() throws Exception {
        doThrow(new IOException("sin conexión")).when(mockHttpClient).send(any(), any());

        PokeApiException ex = assertThrows(PokeApiException.class, () -> apiClient.fetchByName("pikachu"));

        assertTrue(ex.getMessage().contains("sin conexión"));
    }

    @Test
    void fetchByName_Interrupted_RestoresInterruptFlagAndThrows() throws Exception {
        doThrow(new InterruptedException()).when(mockHttpClient).send(any(), any());

        assertThrows(PokeApiException.class, () -> apiClient.fetchByName("pikachu"));

        assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted(); // limpiar el flag para no afectar otros tests
    }

    // ------------------------- Movimientos (fetchMoveDetails vía fetchByName) -------------------------

    @Test
    void fetchByName_MoveWithNullPower_MapsPowerToZero() throws Exception {
        stubUrl(POKEMON_URL + "pikachu", response(200, pokemonJson(1)));
        stubUrl(MOVE_URL + "1/", response(200, moveJson("growl", "null", "normal")));

        Pokemon p = apiClient.fetchByName("pikachu");

        Move m = p.getMove().get(0);
        assertEquals("growl", m.getName());   // ajusta los getters a tu clase Move
        assertEquals(0, m.getPower());
        assertEquals(Type.NORMAL, m.getType());
    }

    @Test
    void fetchByName_MoveWithPower_MapsAllFields() throws Exception {
        stubUrl(POKEMON_URL + "pikachu", response(200, pokemonJson(1)));
        stubUrl(MOVE_URL + "1/", response(200, moveJson("thunder-shock", "40", "electric")));

        Move m = apiClient.fetchByName("pikachu").getMove().get(0);

        assertEquals("thunder-shock", m.getName());
        assertEquals(40, m.getPower());
        assertEquals(Type.ELECTRIC, m.getType());
    }

    @Test
    void fetchByName_MoveRequestFails_ThrowsNetworkError() throws Exception {
        stubUrl(POKEMON_URL + "pikachu", response(200, pokemonJson(1)));
        stubUrl(MOVE_URL + "1/", response(503, ""));

        PokeApiException ex = assertThrows(PokeApiException.class, () -> apiClient.fetchByName("pikachu"));

        assertTrue(ex.getMessage().contains("movimiento"));
        assertTrue(ex.getMessage().contains("503"));
    }

    @Test
    void fetchByName_MoveIOException_ThrowsNetworkError() throws Exception {
        stubUrl(POKEMON_URL + "pikachu", response(200, pokemonJson(1)));
        doThrow(new IOException("timeout")).when(mockHttpClient)
                .send(argThat((HttpRequest req) -> req != null && req.uri().toString().equals(MOVE_URL + "1/")), any());

        assertThrows(PokeApiException.class, () -> apiClient.fetchByName("pikachu"));
    }
}