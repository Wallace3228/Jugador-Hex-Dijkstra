package edu.upc.epsevg.prop.hex.Algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.*;

/**
 * Classe utilitària per calcular distàncies mínimes en un tauler HEX utilitzant l'algorisme de Dijkstra.
 */
public class Dijkstra {

    /**
     * Calcula la distància mínima des del costat inicial fins al costat oposat per a un jugador.
     * @param estat Estat actual del joc.
     * @param jugador Jugador per al qual es calcula la distància.
     * @return Distància mínima calculada.
     */
    public static int calcularDistancia(HexGameStatus estat, PlayerType jugador) {
        int mida = estat.getSize();
        int[][] costos = new int[mida][mida];
        boolean[][] visited = new boolean[mida][mida];

        // Inicialitzem tots els costos amb un valor alt per indicar que són inaccessibles inicialment.
        for (int[] fila : costos) {
            Arrays.fill(fila, 1000);
        }

        // Cua de prioritat per processar els nodes amb menor cost primer.
        PriorityQueue<Point> cua = new PriorityQueue<>(Comparator.comparingInt(p -> costos[p.x][p.y]));

        // Configuració inicial segons el jugador actual.
        if (jugador == PlayerType.PLAYER1) {
            for (int i = 0; i < mida; i++) {
                switch (estat.getPos(0, i)) {
                    case 0: // Casella buida
                        costos[0][i] = 1;
                        break;
                    case 1: // Casella ocupada pel jugador
                        costos[0][i] = 0;
                        break;
                    default: // Casella ocupada per l'oponent
                        costos[0][i] = 1000;
                        break;
                }
                cua.add(new Point(0, i));
            }
        } else {
            for (int i = 0; i < mida; i++) {
                switch (estat.getPos(i, 0)) {
                    case 0: // Casella buida
                        costos[i][0] = 1;
                        break;
                    case -1: // Casella ocupada pel jugador
                        costos[i][0] = 0;
                        break;
                    default: // Casella ocupada per l'oponent
                        costos[i][0] = 1000;
                        break;
                }
                cua.add(new Point(i, 0));
            }
        }

        // Aplicació de l'algorisme de Dijkstra.
        while (!cua.isEmpty()) {
            Point actual = cua.poll();
            if (visited[actual.x][actual.y]) continue;
            visited[actual.x][actual.y] = true;

            for (Point veí : estat.getNeigh(actual)) {
                if (visited[veí.x][veí.y]) continue;
                int costMoviment;
                if (estat.getPos(veí.x, veí.y) == 0) {
                    costMoviment = 1;       // Casella buida
                } else if ((jugador == PlayerType.PLAYER1 && estat.getPos(veí.x, veí.y) == 1) ||
                           (jugador == PlayerType.PLAYER2 && estat.getPos(veí.x, veí.y) == -1)) {
                    costMoviment = 0;       // Casella ocupada pel jugador
                } else {
                    costMoviment = 1000;    // Casella ocupada per l'oponent
                }

                int nouCost = costos[actual.x][actual.y] + costMoviment;

                if (nouCost < costos[veí.x][veí.y]) {
                    costos[veí.x][veí.y] = nouCost;
                    cua.add(veí);
                }
            }
        }

        // Càlcul de la distància mínima segons el costat oposat del jugador.
        int minimaDistancia = 1000;
        if (jugador == PlayerType.PLAYER1) {
            for (int i = 0; i < mida; i++) {
                minimaDistancia = Math.min(minimaDistancia, costos[mida - 1][i]);
            }
        } else {
            for (int i = 0; i < mida; i++) {
                minimaDistancia = Math.min(minimaDistancia, costos[i][mida - 1]);
            }
        }

        return minimaDistancia;
    }
}