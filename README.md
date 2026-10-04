# Pokémon Stadium Lite

Mini-aplicación de escritorio en **Java Swing** que simula un combate por turnos estilo Pokémon Stadium entre dos Pokémon obtenidos en vivo desde [PokeAPI](https://pokeapi.co/).

Proyecto del curso **Desarrollo de Software III** · Tecnología en Sistemas · Universidad del Valle, Sede Tuluá.

## Qué hace

- Carga dos Pokémon por nombre (**Load**) o al azar (**Random**).
- Muestra sprite, nombre, tipos, stats (HP, Attack, Defense, Speed) y barra de HP.
- Ejecuta un combate por turnos (**Fight!**): inicia el más rápido y gana quien deje al rival en 0 HP.
- Registra cada turno en un log desplazable.
- Muestra errores (Pokémon no encontrado, error de red) sin congelar la interfaz.

## Tecnologías

- Java 11+
- Swing
- `java.net.http.HttpClient`
- `org.json`
- Maven

## Ejecución

```bash
git clone <url-del-repositorio>
cd pokemon-stadium-lite
mvn compile exec:java
```

> Requiere Java 11 o superior y conexión a internet.

## Estructura

```
src/main/java/.../pokestadium/
├── model/       # Pokemon
├── api/         # PokeApiClient
├── battle/      # Battle y BattleListener
├── ui/          # Componentes Swing
└── controller/  # Conecta UI, API y combate
```

## Estado

🚧 En desarrollo.

## Autores

- _Tu nombre_
