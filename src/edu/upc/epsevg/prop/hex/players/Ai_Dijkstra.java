package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.SearchType;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.Algorithms.Dijkstra;
import java.awt.Point;
import java.util.*;

/**
 * Jugador HEX avançat basat en Minimax i Dijkstra.
 */
public class Ai_Dijkstra implements IPlayer, IAuto {

    private String name;
    private int profunditatMaxima;
    private PlayerType _el_meu_player;

    /**
     * Constructor del jugador.
     * @param name Nom del jugador.
     * @param profunditatMaxima Profunditat màxima de cerca en Minimax.
     */
    public Ai_Dijkstra(String name, int profunditatMaxima) {
        this.name = name;
        this.profunditatMaxima = profunditatMaxima;
    }

    @Override
    public void timeout() {
        // Gestió del temps d'execució
    }

    /**
     * Genera el moviment òptim basat en l'algorisme Minimax.
     * @param s Estat actual del joc.
     * @return El moviment calculat.
     */
    @Override
    public PlayerMove move(HexGameStatus s) {
        _el_meu_player = s.getCurrentPlayer();
        Point millorMoviment = null;
        int millorValor = -1000;
        int nodesExplorats = 0; // Comptador de nodes explorats

        List<Point> moviments = obtenirMoviments(s);

        if (moviments.isEmpty()) {
            // Si no hi ha moviments possibles, retornar un moviment nul
            return new PlayerMove(null, 0L, profunditatMaxima, SearchType.MINIMAX);
        }

        for (Point moviment : moviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(moviment);

            int[] resultat = minimax(nouEstat, profunditatMaxima - 1, -1000, 1000, false, 0);
            int valor = resultat[0];
            nodesExplorats += resultat[1]; // Acumulem els nodes explorats

            if (valor > millorValor) {
                millorValor = valor;
                millorMoviment = moviment;
            }
        }

        // Assegurar que millorMoviment no és nul abans de retornar
        if (millorMoviment == null) {
            millorMoviment = moviments.get(0); // Escollir el primer moviment vàlid com a alternativa
        }

        return new PlayerMove(millorMoviment, (long) nodesExplorats, profunditatMaxima, SearchType.MINIMAX);
    }

    /**
     * Implementació de l'algorisme Minimax amb poda alfa-beta.
     * @param estat Estat actual del joc.
     * @param profunditat Profunditat restant en l'arbre.
     * @param alpha Valor alfa per a la poda.
     * @param beta Valor beta per a la poda.
     * @param esMaximitzant Indica si és el torn del jugador maximitzant.
     * @return El valor calculat per a l'estat actual.
     */
    private int[] minimax(HexGameStatus estat, int profunditat, int alpha, int beta, boolean esMaximitzant, int nodesExplorats) {
        nodesExplorats++; // Incrementem el comptador de nodes explorats

        if (profunditat == 0 || estat.isGameOver()) {
            return new int[]{avaluar(estat, profunditat), nodesExplorats};
        }

        List<Point> moviments = obtenirMoviments(estat);

        if (esMaximitzant) {
            int valorMax = -1000;
            for (Point moviment : moviments) {
                HexGameStatus nouEstat = new HexGameStatus(estat);
                nouEstat.placeStone(moviment);

                int[] resultat = minimax(nouEstat, profunditat - 1, alpha, beta, false, nodesExplorats);
                valorMax = Math.max(valorMax, resultat[0]);
                nodesExplorats = resultat[1];
                alpha = Math.max(alpha, valorMax);
                if (beta <= alpha) break;
            }
            return new int[]{valorMax, nodesExplorats};
        } else {
            int valorMin = 1000;
            for (Point moviment : moviments) {
                HexGameStatus nouEstat = new HexGameStatus(estat);
                nouEstat.placeStone(moviment);

                int[] resultat = minimax(nouEstat, profunditat - 1, alpha, beta, true, nodesExplorats);
                valorMin = Math.min(valorMin, resultat[0]);
                nodesExplorats = resultat[1];
                beta = Math.min(beta, valorMin);
                if (beta <= alpha) break;
            }
            return new int[]{valorMin, nodesExplorats};
        }
    }

    /**
     * Avalua l'estat actual del tauler utilitzant l'heurística basada en Dijkstra.
     * @param estat Estat actual del joc.
     * @param profunditat Profunditat restant per ajustar el valor d'estats finals.
     * @return Valor heurístic de l'estat.
     */
    private int avaluar(HexGameStatus estat, int profunditat) {
        if (estat.isGameOver()) {
            if (estat.GetWinner() == _el_meu_player) {
                return 1000 + profunditat; // Afegeix la profunditat per premiar victòries més ràpides
            } else {
                return -1000 - profunditat; // Penalitza derrotes més ràpides
            }
        }
        int distJugador = Dijkstra.calcularDistancia(estat, _el_meu_player);
        int distOponent = Dijkstra.calcularDistancia(estat, PlayerType.opposite(_el_meu_player));
        return distOponent - distJugador;
    }

    /**
     * Obté una llista de moviments possibles en l'estat actual.
     * @param estat Estat actual del joc.
     * @return Llista de punts corresponents als moviments vàlids.
     */
    private List<Point> obtenirMoviments(HexGameStatus estat) {
        List<Point> moviments = new ArrayList<>();
        for (MoveNode node : estat.getMoves()) {
            moviments.add(node.getPoint());
        }
        return moviments;
    }

    @Override
    public String getName() {
        return "Dijkstra(" + name + ")";
    }
}
