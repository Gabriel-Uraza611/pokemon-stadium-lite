package controller;

import api.PokeApiClient;
import model.Pokemon;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PokemonLoadController {

    private final PokeApiClient api;


    public PokemonLoadController(PokeApiClient api) {
        this.api = api;
    }

    public void loadByName(String name, Consumer<Pokemon> onSuccess, Consumer<String> onError) {
        load(() -> api.fetchByName(name), onSuccess, onError);
    }

    public void loadRandom(Consumer<Pokemon> onSuccess, Consumer<String> onError) {
        load(api::fetchRandom, onSuccess, onError);
    }

    // Un solo worker para Load y Random: lo único que cambia es "cómo se obtiene el Pokémon"
    private void load(Supplier<Pokemon> fetcher, Consumer<Pokemon> onSuccess, Consumer<String> onError) {
        new SwingWorker<Pokemon, Void>() {

            @Override
            protected Pokemon doInBackground() {
                // HILO DE FONDO: red + descarga de imagen
                Pokemon p = fetcher.get();
                if (p.getSprite() != null) {
                    try {
                        p.setSpriteImage(ImageIO.read(URI.create(p.getSprite()).toURL()));
                    } catch (IOException ignored) {
                        // sin sprite el Pokémon igual es válido
                    }
                }
                return p;
            }

            @Override
            protected void done() {
                // EDT: aquí ya se puede tocar la UI
                Pokemon p;
                try {
                    p = get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    onError.accept("Carga interrumpida");
                    return;
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    onError.accept(cause.getMessage() != null ? cause.getMessage() : "Error inesperado");
                    return;
                }
                onSuccess.accept(p);
            }
        }.execute();
    }
}