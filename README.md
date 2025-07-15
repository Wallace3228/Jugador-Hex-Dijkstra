# 🎮 Jugador HEX amb Dijkstra i Minimax - Java

Aquest projecte implementa un jugador intel·ligent per al joc **HEX** utilitzant l'algorisme **Dijkstra** combinat amb **Minimax** i **poda Alfa-Beta**. Malauradament, la versió amb IDS (Iterative Deepening Search) no funciona correctament i requereix més desenvolupament.

## ⚙️ Com Executar

1. **Compilació**:
```bash
javac -d . Ai_Dijkstra.java Ai_Dijkstra_IDS.java Dijkstra.java
```

2. **Integració amb el joc HEX**:
```java
// Configuració per a jugador amb profunditat fixa
IPlayer jugador1 = new Ai_Dijkstra("Jugador1", 3); 

// Configuració per a jugador amb IDS (no funcional)
IPlayer jugador2 = new Ai_Dijkstra_IDS("Jugador2"); 
```

## 🧠 Jugadors Implementats

### 1. Ai_Dijkstra (Profunditat Fixa)
- **Algorismes**: Minimax + Poda Alfa-Beta + Dijkstra
- **Profunditat**: Configurable (3-5 recomanada)
- **Funcionament**: 
  - Explora l'arbre de decisions fins a una profunditat fixa
  - Utilitza Dijkstra per avaluar distàncies al tauler
  - Poda Alfa-Beta per optimitzar la cerca

### 2. Ai_Dijkstra_IDS (Iterative Deepening - **NO FUNCIONAL**)
- **Algorismes**: IDS + Minimax + Dijkstra
- **Problemes coneguts**:
  - ❌ No gestiona correctament el timeout
  - ❌ Explora massa nodes sense augmentar profunditat
  - ❌ Rendiment inferior a la versió de profunditat fixa

## 🗺️ Sistema Heurístic - Dijkstra

L'avaluació dels estats es basa en la distància mínima calculada mitjançant l'algorisme de Dijkstra:

```java
// Aplicació de l'algorisme de Dijkstra.
    while (!cua.isEmpty()) {
        Point actual = cua.poll();
        if (visited[actual.x][actual.y]) continue;
        visited[actual.x][actual.y] = true;

        for (Point veí : estat.getNeigh(actual)) {
            if (visited[veí.x][veí.y]) continue;
            int costMoviment;
            if (estat.getPos(veí.x, veí.y) == 0) {
                costMoviment = 1;                       // Casella buida
            } else if ((jugador == PlayerType.PLAYER1 && estat.getPos(veí.x, veí.y) == 1)
              ||       (jugador == PlayerType.PLAYER2 && estat.getPos(veí.x, veí.y) == -1)) {
                costMoviment = 0;                       // Casella ocupada pel jugador
            } else {
                costMoviment = 1000;                    // Casella ocupada per l'oponent
            }
            
            int nouCost = costos[actual.x][actual.y] + costMoviment;
        }
    }
    if (nouCost < costos[veí.x][veí.y]) {
        costos[veí.x][veí.y] = nouCost;
        cua.add(veí);
    }
```

## ⚠️ Limitacions i Problemes Coneguts

1. **Implementació IDS defectuosa**:
   - Només arriba al nivell 1 malgrat temps disponible
   - Gestion incorrecta del timeout
   - Recalcula iteracions sense aprofitar treball previ

2. **Heurística simplificada**:
   - No considera tècniques avançades com "ponts"
   - Assignació de costos poc òptima
   - Manca d'anàlisi de patterns o situacions guanyadores

3. **Problemes de rendiment**:
   - ❌ Càlcul de distàncies amb Dijkstra poc optimitzat
   - ❌ Creixement exponencial de nodes explorats
   - ❌ Falta de taules de transposició per a memòria

## 📌 Conclusions

Aquest projecte representa un intent d'implementar un jugador intel·ligent per a HEX utilitzant algorismes avançats. Malgrat això:

- La implementació d'**IDS no és viable** en el seu estat actual
- Es necessitaria més temps per afegir tècniques com ponts i millorar l'avaluació

## 👥 Autors

- **Alex Matilla Santos**
- **Pau Ortiz Borrás**

Projecte desenvolupat com a part de l'assignatura de **Projecte de programació**.
