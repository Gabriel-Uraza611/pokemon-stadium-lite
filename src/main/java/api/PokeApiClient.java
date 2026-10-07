package api;

import model.Move;
import model.Pokemon;
import model.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokeApiClient {

    //variable global de clase
    private final HttpClient client;
    private static final int MAX_POKEMON_ID = 151; // primera generación
    private final Random random = new Random();

    //inyeccion de dependencias para constructor con parametros -> para pruebas unitarias
    public PokeApiClient(HttpClient client) {
        this.client = client;
    }

    public PokeApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    private Move fetchMoveDetails(String moveUrl) {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(moveUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PokeApiException(ErrorType.NETWORK, "Error al obtener detalles del movimiento: " + response.statusCode());
            }

            JSONObject moveJson = new JSONObject(response.body());

            String name = moveJson.getString("name");
            // Manejo defensivo: los movimientos de estado devuelven null en "power"
            int power = moveJson.isNull("power") ? 0 : moveJson.getInt("power");

            String typeName = moveJson.getJSONObject("type").getString("name");
            Type type = Type.valueOf(typeName.toUpperCase());

            return new Move(name, type, power);

        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PokeApiException(ErrorType.NETWORK, "Operación interrumpida");
        }catch (IOException e) {
            throw new PokeApiException(ErrorType.NETWORK, "Error de red: " + e.getMessage());
        }
    }

    public Pokemon fetchRandom() {
        int id = random.nextInt(MAX_POKEMON_ID) + 1;
        return fetchByName(String.valueOf(id));
    }

    public Pokemon fetchByName(String name){
        if (name == null || name.trim().isEmpty()) {
            throw new PokeApiException(ErrorType.INVALID_RESPONSE, "El nombre no puede estar vacío");
        }

        //formato del nombre
        String sanitizedName = name.toLowerCase().trim();
        //construccion de URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon/"+sanitizedName))
                .GET()
                .build();
        /* En esta parte es donde se hace la peticion a la URL creada y se trae el TOODO el JSON GIGANTE para guardarlo en
        * jsonBody como string*/
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new PokeApiException(ErrorType.NOT_FOUND, "Pokémon no encontrado: " + sanitizedName);
            }

            if (response.statusCode() != 200) {
                throw new PokeApiException(ErrorType.NETWORK, "Error en la respuesta del servidor: " + response.statusCode());
            }

            JSONObject jsonBody = new JSONObject(response.body());

            // Creacion de las variables para crear al pokemoncito (se usa la inicial p por "Pokemon"
            int pId = jsonBody.getInt("id");
            String pName = jsonBody.getString("name");
            JSONObject SpriteObj = jsonBody.getJSONObject("sprites");
            String pSprite = SpriteObj.getString("front_default");

            /*NOTA: se limitara la busqueda de pokemons hasta la primera generacion, con el fin de garantizar que todos los
            pokemon tengan 6 stats, ademas por fidelidad al juego original de la N64
            */

            //adquicision de stats
            JSONArray statsArray = jsonBody.getJSONArray("stats");
            int pMaxHp = 0, pAttack = 0, pDefence = 0, pSpeed = 0;

            for (int i = 0; i < statsArray.length(); i++) {
                JSONObject statObj = statsArray.getJSONObject(i);
                String statName = statObj.getJSONObject("stat").getString("name");
                int value = statObj.getInt("base_stat");

                switch (statName) {
                    case "hp" -> pMaxHp = value;
                    case "attack" -> pAttack = value;
                    case "defense" -> pDefence = value;
                    case "speed" -> pSpeed = value;
                }
            }

            int pCurrentHp = pMaxHp;

            //extraccion de tipos
            List<Type> pTypes = new ArrayList<>();
            jsonBody.getJSONArray("types").forEach(type -> {
                //acceso primero al objeto type, luego al arreglo del tipo especifico y finalmente al nombre de dicho tipo
                JSONObject typesObj = (JSONObject) type;
                JSONObject specificType = typesObj.getJSONObject("type");
                String typeName = specificType.getString("name");
                //se añade al arreglo de una vez con base al enum poniendo el nombre extraido en mayusculas
                pTypes.add(Type.valueOf(typeName.toUpperCase()));
            });

            //extraccion de movimientos
            List<Move> pMoves = new ArrayList<>();
            JSONArray movesArray = jsonBody.getJSONArray("moves");

// Recorre hasta juntar 4 movimientos que hagan daño (tope de 20 consultas)
            for (int i = 0; i < movesArray.length() && pMoves.size() < 4 && i < 20; i++) {
                String moveUrl = movesArray.getJSONObject(i).getJSONObject("move").getString("url");
                Move m = fetchMoveDetails(moveUrl);
                if (m.getPower() > 0) pMoves.add(m);
            }

// Respaldo para que nunca quede sin ataques
            if (pMoves.isEmpty()) pMoves.add(new Move("tackle", Type.NORMAL, 40));

            return new Pokemon(
                    pId, pName, pTypes,
                    pMaxHp, pCurrentHp,
                    pAttack, pDefence, pSpeed,
                    pMoves, pSprite
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PokeApiException(ErrorType.NETWORK, "Operación interrumpida");
        }catch (IOException e) {
            throw new PokeApiException(ErrorType.NETWORK, "Error de red: " + e.getMessage());
        }

    }
}
