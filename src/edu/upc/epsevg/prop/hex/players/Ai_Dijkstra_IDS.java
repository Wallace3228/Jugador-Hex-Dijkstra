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
 * Implementació del jugador basat en Iterative Deepening Search (IDS)
 * utilitzant Dijkstra com a heurística.
 */
public class Ai_Dijkstra_IDS implements IPlayer, IAuto {

    private String name;
    private PlayerType _el_meu_player;
    private boolean timeout;

    /**
     * Constructor del jugador IDS.
     * @param name Nom del jugador.
     */
    public Ai_Dijkstra_IDS(String name) {
        this.name = name;
        this.timeout = false;
    }

    @Override
    public void timeout() {
        this.timeout = true;
    }

    @Override
    public PlayerMove move(HexGameStatus s) {
        _el_meu_player = s.getCurrentPlayer();
        Point millorMoviment = null;
        int millorValor = -1000;
        int nodesExplorats = 0;

        List<Point> moviments = obtenirMoviments(s);

        if (moviments.isEmpty()) {
            // Si no hi ha moviments possibles, retornar un moviment nul
            return new PlayerMove(null, 0L, 1, SearchType.MINIMAX);
        }

        // IDS: Comencem amb profunditat 1 i augmentem en cada iteració.
        for (int profunditatActual = 1; !timeout; profunditatActual++) {
            Point millorMovimentActual = null;
            int millorValorActual = -1000;

            for (Point moviment : moviments) {
                if (timeout) break;

                HexGameStatus nouEstat = new HexGameStatus(s);
                nouEstat.placeStone(moviment);

                int[] resultat = minimax(nouEstat, profunditatActual - 1, -1000, 1000, false, 0);
                int valor = resultat[0];
                nodesExplorats += resultat[1];

                if (valor > millorValorActual) {
                    millorValorActual = valor;
                    millorMovimentActual = moviment;
                }
            }

            if (!timeout) {
                millorValor = millorValorActual;
                millorMoviment = millorMovimentActual;
            }
        }

        if (millorMoviment == null) {
            millorMoviment = moviments.get(0);
        }

        return new PlayerMove(millorMoviment, (long) nodesExplorats, 1, SearchType.MINIMAX);
    }

    private int[] minimax(HexGameStatus estat, int profunditat, int alpha, int beta, boolean esMaximitzant, int nodesExplorats) {
        nodesExplorats++;

        if (profunditat == 0 || estat.isGameOver() || timeout) {
            return new int[]{avaluar(estat, profunditat), nodesExplorats};
        }

        List<Point> moviments = obtenirMoviments(estat);

        if (esMaximitzant) {
            int valorMax = -1000;
            for (Point moviment : moviments) {
                if (timeout) break;

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
                if (timeout) break;

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

    private int avaluar(HexGameStatus estat, int profunditat) {
        if (estat.isGameOver()) {
            if (estat.GetWinner() == _el_meu_player) {
                return 1000 + profunditat;
            } else {
                return -1000 - profunditat;
            }
        }
        int distJugador = Dijkstra.calcularDistancia(estat, _el_meu_player);
        int distOponent = Dijkstra.calcularDistancia(estat, PlayerType.opposite(_el_meu_player));
        return distOponent - distJugador;
    }

    private List<Point> obtenirMoviments(HexGameStatus estat) {
        List<Point> moviments = new ArrayList<>();
        for (MoveNode node : estat.getMoves()) {
            moviments.add(node.getPoint());
        }
        return moviments;
    }

    @Override
    public String getName() {
        return "Dijkstra-IDS(" + name + ")";
    }
}
