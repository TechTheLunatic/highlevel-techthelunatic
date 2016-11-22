package pathfinder;

import container.Service;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleManager;
import table.obstacles.ObstacleRectangular;
import utils.Config;
import utils.Log;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by shininisan on 17/11/16.
 */
 public class Pathfinding implements Service {
    private Graphe graphe;
    private Table table;
    private Config config;
    private Log log;

    public Pathfinding(Log log, Config config, Table table) {
        this.log = log;
        this.config = config;
        this.table = table;


    }

    public void setGraphe(Graphe graphe) {
        this.graphe = graphe;
    }

    public Graphe getGraphe() {
        return this.graphe;
    }

    /**
     * @param departV  noeud de depart de l'A*
     * @param arriveeV noeud d'arrivée
     * @return Liste des noeuds
     */
    public ArrayList<Vec2> Astarfoulah(Vec2 departV, Vec2 arriveeV, Graphe g) {// on met les noeuds dans une priority queue


        Noeud depart = new Noeud(g, departV);
        g.getlNoeuds().add(depart);
        Noeud arrivee = new Noeud(g, arriveeV);
        g.getlNoeuds().add(arrivee);
        ObstacleManager a = this.table.getObstacleManager();
        for (Noeud noeud1 : g.getlNoeuds()) {
            depart.attachelien(noeud1);
            noeud1.attachelien(depart);
            arrivee.attachelien(noeud1);
            noeud1.attachelien(arrivee);
        }

        for (ObstacleCircular z : a.getFixedObstacles()) {
            for (int i = 0; i < depart.lArretes.size(); i++) {
                if (depart.lArretes.get(i).isBloquant(z)) {
                    i--;
                }
            }
            for (int i = 0; i < arrivee.lArretes.size(); i++) {
                if (arrivee.lArretes.get(i).isBloquant(z)) {
                    i--;
                }
            }
        }
        for (ObstacleRectangular z : a.getRectangles()) {
            for (int i = 0; i < depart.lArretes.size(); i++) {
                if (depart.lArretes.get(i).isBloquant(z)) {
                    i--;
                }


            }
            for (int i = 0; i < arrivee.lArretes.size(); i++) {
                if (arrivee.lArretes.get(i).isBloquant(z)) {
                    i--;
                }


            }

        }
        return Astarfoulah(depart, arrivee, g);
    }


    public ArrayList<Vec2> Astarfoulah(Noeud depart, Noeud arrivee, Graphe g) {// on met les noeuds dans une priority queue


        ArrayList<Vec2> chemin = new ArrayList();
        ArrayList<Noeud> closedlist = new ArrayList();


        PriorityQueue<Noeud> pq = new PriorityQueue(g.getNoeudsurtable(), new ComparaNoeud());
        pq.add(depart);
        depart.distheuristique(arrivee);
        Noeud noeudCourant = new Noeud();


        while (noeudCourant != arrivee && !pq.isEmpty()) {
            if (noeudCourant.getIndice() == -1) {
                noeudCourant.sommedepart = 0;
            }
            log.debug("pq size" + pq.size());
            noeudCourant = pq.poll();
            if (arrivee == noeudCourant) {
                while (noeudCourant != null && chemin.size() < 100) {
                    noeudCourant = noeudCourant.noeudPrecedent;
                    chemin.add(noeudCourant.position);
                    return chemin;
                }
            }

            for (Arrete aux : noeudCourant.lArretes) {
                {
                    double b = noeudCourant.sommedepart + aux.cout;
                    if (!closedlist.contains(aux.arrivee) || aux.arrivee.sommedepart < b  ) // la dernière partie fait existe dans openlist. si y a pas mieux dans Closed list non plus
                    {
                        aux.arrivee.sommedepart = b;
                        pq.add(aux.arrivee);
                        aux.arrivee.noeudPrecedent = noeudCourant;
                        aux.arrivee.visite = true;
                    }


                }
                closedlist.add(noeudCourant);


            }

        }

return null;
    }



    /**
     *
     * @param position la position du robot
     * @param detecte l'obstacle détecté
     * @param chemin la liste des Noeuds que le robot était censé suivre
     * @param indice l'indice de la liste précédente
     * @return la liste des noeuds intermédiaires pour éviter l'obstacle
     */
    public ArrayList<Vec2> actGraphe(Vec2 position, ObstacleCircular detecte, ArrayList<Noeud> chemin, int indice)
    {
        //on veut une copie de ini et de fin
        Noeud ini=new Noeud(chemin.get(indice));
        Noeud fin=new Noeud(chemin.get(indice+1));

        ini.position=position;
        // on supprime les liens inutiles et actualise les valeurs on rajoute les noeuds de l'obstacles, on relie

        ini.lArretes.add(new Arrete(ini,fin));
        // on crée un nouveau graphe, en recopiant les arêtes du noeud précédent, actualisant les valeurs
        Graphe sousg=new Graphe(this.log,this.config,this.table);

        sousg.initGraphe(position,detecte,ini,fin);
        return this.Astarfoulah(ini,fin,sousg);


    }

    @Override
    public void updateConfig()
    {
        //TODO charger la config
    }
}