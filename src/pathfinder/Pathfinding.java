/*
 * Copyright (c) 2016, INTech.
 *
 * This file is part of INTech's HighLevel.
 *
 *  INTech's HighLevel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  INTech's HighLevel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it.  If not, see <http://www.gnu.org/licenses/>.
 */

package pathfinder;

import container.Service;
import exceptions.Locomotion.PointInObstacleException;
import smartMath.Circle;
import smartMath.Geometry;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.*;
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

    private Pathfinding(Log log, Config config, Table table) {
        this.log = log;
        this.config = config;
        this.table = table;
        this.graphe = new Graphe(log, config, table);

    }

    public void setGraphe(Graphe graphe) {
        this.graphe = graphe;
    }

    public Graphe getGraphe() {
        return this.graphe;
    }

    /**
     * L'algorithme vérifie les points d'arrivé et de départ, et lance Astarfoullah avec des noeuds hors des obstacles
     * et dans la table
     * @param departV          la position de début du pathfinding
     * @param arriveeV         la position d'arrivée
     * @param robotOrientation l'orientation du robot sur la position de départ
     * @return une arrayliste des positions intermédiaires
     */
    public ArrayList<Vec2> Astarfoulah(Vec2 departV, Vec2 arriveeV, double robotOrientation) throws PointInObstacleException {
        // On récupère les obstacles et on s'assure que l'orientation du robot soit entre -pi et pi
        ObstacleManager a = this.table.getObstacleManager();
        robotOrientation = robotOrientation%Math.PI;
        // Une longueur qui sert dans tout les cas ou envoie le robot dans un obstacle : on l'envoie au plus près du danger !
        // C'est pourquoi on a besoin de le considérer rectangulaire
        int robotMarge = a.mRobotRadius - a.getmRobotWidth()/2;

        // Si le point de départ est hors de la table
        if (Math.abs(departV.getX()) > 1500 - a.mRobotRadius ||
                Math.abs(departV.getY() - 1000) > 1000 - a.mRobotRadius) {
            log.debug("Retourne sur la table connard => Point de départ " + departV + " Orientation : " + robotOrientation);

            // Cas "simple", où le robot est perpendiculaire au côté de la table sur lequel il est bloqué
            // Il recule/avance juste pour rentrer dans la table (ya pas d'obstacle derriere, faut pas déconner)

            if (departV.getY() < a.mRobotRadius && (Math.abs(robotOrientation) > Math.PI/4 && Math.abs(robotOrientation) < 3 * Math.PI/4)) {
                ArrayList<Vec2> newPath = Astarfoulah(new Vec2(departV.getX(), a.mRobotRadius + 1), arriveeV, robotOrientation);
                newPath.add(0, departV);
                return newPath;
            } else if (departV.getY() > 2000 - a.mRobotRadius && Math.abs(robotOrientation) > Math.PI / 4 && Math.abs(robotOrientation) < 3 * Math.PI / 4) {
                ArrayList<Vec2> newPath = Astarfoulah(new Vec2(departV.getX(), 2000 - a.mRobotRadius), arriveeV, robotOrientation);
                newPath.add(0, departV);
                return newPath;
            } else if (Math.abs(departV.getX()) > 1500 - a.mRobotRadius && (Math.abs(robotOrientation) < Math.PI / 4 || Math.abs(robotOrientation) > 3 * Math.PI / 4)) {
                ArrayList<Vec2> newPath = Astarfoulah(new Vec2((1500 - a.mRobotRadius) * departV.getX() / Math.abs(departV.getX()), departV.getY()), arriveeV, robotOrientation);
                newPath.add(0, departV);
                return newPath;
            }

            // 2 Cas de figure : ou le robot est tangent aux bords x=-1500 (ou 1500), ou il est tangent aux bords y=0 (ou 2000)
            // Dans les deux cas le principe est le même : on regarde de combien on peut tourner, au maximum, sans toucher le bord de la table (marge)
            // On calcule les 2 vecteurs de longueur minimum pour rerentrer dans la table (vecPropoFW/BW)
            // Si l'un est dans un obstacle, on vérifie l'autre; si les deux sont dans des obstacles, on prend les deux successivement,
            // et on rappelle Astarfoulah avec le nouveau vecteur de départ.
            // Evidemment, si l'angle marge est trop petit, ca peut ne pas fonctionner : mais sans trajectoire courbe, ce cas est improbable...

            else if ((departV.getY() < a.mRobotRadius && (Math.abs(robotOrientation) < Math.PI/4 || Math.abs(robotOrientation) > 3*Math.PI/4)) ||
                    (2000 - departV.getY() < a.mRobotRadius && (Math.abs(robotOrientation) < Math.PI/4 || Math.abs(robotOrientation) > 3*Math.PI/4 )))
            {
                int sens = Math.abs(departV.getY()-1000)/(departV.getY()-1000);
                double marge = Math.acos((double)(a.getmRobotLenght()) / (2 * a.mRobotRadius)) - Math.acos((double)(departV.getY())/ a.mRobotRadius);
                double radius = Math.min((a.mRobotRadius - departV.getY()) / Math.sin(marge), 140);
                Vec2 vecPropoFW = new Vec2(radius, Math.PI + sens*marge);
                Vec2 vecPropoBW = new Vec2(radius, -sens*marge);

                if (whichObstacle(vecPropoFW.plusNewVector(departV)) instanceof ObstacleCircular)
                {
                    if (whichObstacle(vecPropoBW.plusNewVector(departV)) instanceof ObstacleCircular)
                    {
                        ArrayList<Vec2> newPath = Astarfoulah(vecPropoFW.plusNewVector(vecPropoBW).plusNewVector(departV), arriveeV, robotOrientation);
                        newPath.add(0, departV);
                        newPath.add(1, vecPropoFW.plusNewVector(departV));
                        return newPath;
                    }
                    ArrayList<Vec2> newPath = Astarfoulah(vecPropoBW.plusNewVector(departV), arriveeV, robotOrientation);
                    newPath.add(0, departV);
                    return newPath;
                }
                ArrayList<Vec2> newPath = Astarfoulah(vecPropoFW.plusNewVector(departV), arriveeV, robotOrientation);
                newPath.add(0, departV);
                return newPath;
            }

            else if (Math.abs(departV.getX())>1500-a.mRobotRadius && Math.abs(robotOrientation) < 3*Math.PI/4 && Math.abs(robotOrientation) > Math.PI/4)
            {
                int sens = Math.abs(departV.getX())/departV.getX();
                double marge = Math.acos((double)(a.getmRobotLenght()) / 2*a.mRobotRadius) - Math.acos((double)(departV.getY()) / 2*a.mRobotRadius);
                double radius = Math.min((a.mRobotRadius - departV.getY()) / Math.sin(marge), 140);

                Vec2 vecPropoFW = new Vec2(radius, sens*marge + Math.PI/2);
                Vec2 vecPropoBW = new Vec2(radius, -sens*marge - Math.PI/2);
                if (whichObstacle(vecPropoFW.plusNewVector(departV)) != null)
                {
                    if (whichObstacle(vecPropoBW.plusNewVector(departV)) != null)
                    {
                        ArrayList<Vec2> newPath = Astarfoulah(vecPropoFW.plusNewVector(vecPropoBW).plusNewVector(departV), arriveeV, robotOrientation);
                        newPath.add(0, departV);
                        newPath.add(1, vecPropoFW.plusNewVector(departV));
                        return newPath;
                    }
                    ArrayList<Vec2> newPath = Astarfoulah(vecPropoBW.plusNewVector(departV), arriveeV, robotOrientation);
                    newPath.add(0, departV);
                    return newPath;
                }
                ArrayList<Vec2> newPath = Astarfoulah(vecPropoFW.plusNewVector(departV), arriveeV, robotOrientation);
                newPath.add(0, departV);
                return newPath;
            }
        }

        //TODO Gérer les cas ou le point d'arrivé est pile poil au centre de l'obstacle
        else {
            // Si le point de départ est dans un obstacle (et dans la table)
            Obstacle obstacle = whichObstacle(departV);
            if (obstacle != null) {
                log.debug("Point de départ dans un obstacle :" + departV);
                // Cas des obstacles circulaires
                if (obstacle instanceof ObstacleCircular) {
                    Vec2 vecRef = obstacle.getPosition().minusNewVector(departV);
                    double radius = ((ObstacleCircular) obstacle).getRadius();

                    // De même, on différencie les cas ou le robot est tangent, et celui ou il est perpendiculaire
                    // Perpendiculaire
                    if (Math.abs(robotOrientation - vecRef.getA()) < Math.PI / 4 || Math.abs(robotOrientation - vecRef.getA()) > 3 * Math.PI / 4) {
                        vecRef.setR(radius + 1);
                        vecRef.setA(vecRef.getA() + Math.PI);

                        ArrayList<Vec2> newPath = Astarfoulah(obstacle.getPosition().plusNewVector(vecRef), arriveeV, robotOrientation);
                        newPath.add(0, departV);
                        return newPath;
                    }

                    //Tangent
                    else {
                        vecRef = vecRef.dotFloat(-1);
                        Vec2 vecPropoFW = new Vec2(radius + 1, vecRef.getA() + Math.acos(vecRef.getR() / radius));
                        Vec2 vecPropoBW = new Vec2(radius + 1, vecRef.getA() - Math.acos(vecRef.getR() / radius));

                        if (whichObstacle(obstacle.getPosition().plusNewVector(vecPropoFW)) != null) {
                            ArrayList<Vec2> newPath = Astarfoulah(obstacle.getPosition().plusNewVector(vecPropoBW), arriveeV, robotOrientation);
                            newPath.add(0, departV);
                            return newPath;
                        } else {
                            ArrayList<Vec2> newPath = Astarfoulah(obstacle.getPosition().plusNewVector(vecPropoFW), arriveeV, robotOrientation);
                            newPath.add(0, departV);
                            return newPath;
                        }
                    }
                }

                // TODO Optimisier et debuger le cas des obstacles rectangulaires
                // Cas des obstacles rectangulaires (similaire au cas de la table)
                if (obstacle instanceof ObstacleRectangular) {
                    Vec2 newDepartV = ((ObstacleRectangular) obstacle).pointProche(departV);
                    log.debug("Nouveau départ :" + newDepartV);
                    double angleTest = newDepartV.minusNewVector(departV).getA();
                    double angleref = Math.abs(Math.abs(angleTest) - Math.abs(robotOrientation));

                    // Perpendiculaire
                    if (angleref < Math.PI / 4 || angleref > 3 * Math.PI / 4) {
                        ArrayList<Vec2> newPath = Astarfoulah(newDepartV, arriveeV, robotOrientation);
                        newPath.add(0, departV);
                        return newPath;
                    }

                    //Tangent
                    else {
                        double radiusRef = departV.minusNewVector(newDepartV).length();
                        double marge = Math.acos((double) a.getmRobotLenght() / (2 * a.mRobotRadius)) - Math.acos((a.mRobotRadius - radiusRef) / a.mRobotRadius);
                        double radius = Math.min(radiusRef / Math.sin(marge) + 2, 100);

                        Vec2 vecPropoFW = new Vec2(radius, 0);
                        Vec2 vecPropoBW = new Vec2(radius, 0);

                        if (Math.abs(angleTest) == Math.PI) {
                            vecPropoFW.setA(Math.PI / 2 + marge);
                            vecPropoBW.setA(-Math.PI / 2 - marge);
                        } else if (angleTest == 0) {
                            vecPropoFW.setA(Math.PI / 2 - marge);
                            vecPropoBW.setA(-Math.PI / 2 + marge);
                        } else if (angleTest == -Math.PI / 2) {
                            vecPropoFW.setA(-Math.PI + marge);
                            vecPropoBW.setA(-marge);
                        } else {
                            vecPropoFW.setA(Math.PI - marge);
                            vecPropoBW.setA(marge);
                        }

                        ArrayList<Vec2> newPath = Astarfoulah(departV.plusNewVector(vecPropoFW), arriveeV, robotOrientation);
                        newPath.add(0, departV);
                        return newPath;
                    }
                }
            }
        }

        // Si le point d'arrivé est hors de la table
        // Si tel est son souhait, on l'amene hors de la table... Mais en passant par un point faisant en sorte qu'il arrive perpendiculairement
        // au bord de la table : on appelle Astarfoulah sur le point dans la table le plus proche du point d'arrivée hors-table

        if (Math.abs(arriveeV.getX()) > 1500 - a.mRobotRadius ||
                arriveeV.getY() < a.mRobotRadius ||
                arriveeV.getY() > 2000 - a.mRobotRadius) {
            log.debug("Je ne quitterai pas cette table sans une bonne raison ! => Point d'arrivée " + arriveeV + "\n" + "Bon, d'accord...");
            if (Math.abs(arriveeV.getX())>1500-a.mRobotRadius)
            {
                int sens = Math.abs(arriveeV.getX())/arriveeV.getX();
                Vec2 newArriveeV = new Vec2(sens*(1500-a.mRobotRadius), arriveeV.getY());

                ArrayList<Vec2> newPath = Astarfoulah(departV, newArriveeV, robotOrientation);
                arriveeV.setX(sens*(1500-a.getmRobotWidth()/2));

                if (whichObstacle(newArriveeV) == null){
                    newPath.add(newPath.size(), arriveeV);
                }
                return newPath;
            }
            if (Math.abs(arriveeV.getY()-1000)>1000 - a.mRobotRadius)
            {
                int sens = Math.abs(arriveeV.getY()-1000)/(arriveeV.getY()-1000);
                Vec2 newArriveeV = new Vec2(arriveeV.getX(), 1000+sens*(1000-a.mRobotRadius));

                ArrayList<Vec2> newPath = Astarfoulah(departV, newArriveeV, robotOrientation);
                arriveeV.setY(1000 + sens*(1000-a.getmRobotWidth()/2));

                if (whichObstacle(newArriveeV) == null) {
                    newPath.add(newPath.size(), arriveeV);
                }
                return newPath;
            }
        }

        // Si le point d'arrivé est dans un obstacle (et dans la table)
        Obstacle obstacle = whichObstacle(arriveeV);
        if (obstacle != null){
            log.debug("Point d'arrivé dans un obstacle :"+ arriveeV);
            // Cas des obstacles circulaires
            if (obstacle instanceof ObstacleCircular)
            {
                Vec2 toReturn = Geometry.pointExterieur(arriveeV, ((ObstacleCircular) obstacle).getCircle());
                log.debug("Vecteur à retourné :" + toReturn);
                ArrayList<Vec2> newPath = Astarfoulah(departV, toReturn, robotOrientation);
                return newPath;
            }
            else if(obstacle instanceof ObstacleRectangular){
                return Astarfoulah(departV, ((ObstacleRectangular) obstacle).noeudProche(arriveeV).position, robotOrientation);
            }
        }

        // Si tout va bien, on lance de Pathfinding des noeuds !
        Graphe graph = this.graphe;
        Noeud depart = new Noeud(graph, departV);
        Noeud arrivee = new Noeud(graph, arriveeV);
        return Astarfoulah(depart, arrivee, graph);
    }

    /**
     * A star
     *
     * @param depart  Noeud de départ
     * @param arrivee Noeud d'arrivée
     * @param g       graphe
     * @return Liste de vec2 des points de passage
     */
    public ArrayList<Vec2> Astarfoulah(Noeud depart, Noeud arrivee, Graphe g) throws PointInObstacleException {

        ObstacleManager a = this.table.getObstacleManager();
        //On vérifie que ça n'intersecte ni les obstacles circulaires ni ca
        for (int i = 0; i < g.getlNoeuds().size(); i++)
        {
            int j = 0;
            boolean creerdep = true;
            boolean creerarr = true;
            int nombobst = a.getmCircularObstacle().size();
            int nombobstRec = a.getRectangles().size();

            j = 0;
            while ((creerdep || creerarr) && j < nombobst) {

                if (a.getmCircularObstacle().get(j).isInObstacle(g.getlNoeuds().get(i).position)) {
                    creerdep = false;
                    creerarr = false;
                }

                creerdep = creerdep && !(Geometry.intersects(new Segment(depart.position, g.getlNoeuds().get(i).position), new Circle(a.getmCircularObstacle().get(j).getPosition(), a.getmCircularObstacle().get(j).getRadius())));
                creerarr = creerarr && !(Geometry.intersects(new Segment(arrivee.position, g.getlNoeuds().get(i).position), new Circle(a.getmCircularObstacle().get(j).getPosition(), a.getmCircularObstacle().get(j).getRadius())));
                j++;
            }
            while ((creerdep || creerarr) && j < nombobstRec) {

                creerdep = creerdep && !Geometry.intersects(new Segment(depart.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(1).position));
                creerdep = creerdep && !Geometry.intersects(new Segment(depart.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(3).position));
                creerdep = creerdep && !Geometry.intersects(new Segment(depart.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(2).position));
                creerdep = creerdep && !Geometry.intersects(new Segment(depart.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(2).position, a.getRectangles().get(j).getlNoeud().get(3).position));
                creerdep = creerdep && !Geometry.intersects(new Segment(depart.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(2).position));
                creerdep = creerdep && !Geometry.intersects(new Segment(depart.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(3).position));

                creerarr = creerarr && !Geometry.intersects(new Segment(arrivee.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(1).position));
                creerarr = creerarr && !Geometry.intersects(new Segment(arrivee.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(3).position));
                creerarr = creerarr && !Geometry.intersects(new Segment(arrivee.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(2).position));
                creerarr = creerarr && !Geometry.intersects(new Segment(arrivee.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(2).position, a.getRectangles().get(j).getlNoeud().get(3).position));
                creerarr = creerarr && !Geometry.intersects(new Segment(arrivee.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(2).position));
                creerarr = creerarr && !Geometry.intersects(new Segment(arrivee.position, g.getlNoeuds().get(i).position), new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(3).position));

                j++;
            }

            if (creerdep) {
                depart.attachelien(g.getlNoeuds().get(i));
                g.getlNoeuds().get(i).attachelien(depart);
            }
            if (creerarr) {
                arrivee.attachelien(g.getlNoeuds().get(i));
                g.getlNoeuds().get(i).attachelien(arrivee);

            }
        }

        ArrayList<Vec2> chemin = new ArrayList<>();
        ArrayList<Noeud> closedlist = new ArrayList<>();

        //on stocke dedans les noeuds qu'on rencontre, comparaNoeud est l'heuristique
        PriorityQueue<Noeud> pq = new PriorityQueue(g.getlNoeuds().size(), new ComparaNoeud());

        depart.noeudPrecedent = null;
        closedlist.add(depart);
        depart.distheuristique(arrivee);
        depart.sommedepart = 0;
        Noeud noeudCourant = depart;


        do {
            if (noeudCourant == null) {
                log.debug("noeudCourant est null");
                return null;
            }
            for (int i = 0; i < noeudCourant.lArretes.size(); i++) // on parcourt les arrêtes de noeudCourant
            {
                noeudCourant.lArretes.get(i).arrivee.distheuristique(arrivee);//on actualise la distance à l'arrivée
                double b = noeudCourant.sommedepart + noeudCourant.lArretes.get(i).cout;
                if (!closedlist.contains(noeudCourant.lArretes.get(i).arrivee) && noeudCourant.lArretes.get(i).arrivee.sommedepart > b) // On vérifie qu'il n'est pas dans la closedList ou que on a déjà trouvé mieux
                {
                    //on modifie la sommedepuis le départ et le noeud précédent, puis on ajoute a la priorityQueue
                    noeudCourant.lArretes.get(i).arrivee.sommedepart = b;

                    noeudCourant.lArretes.get(i).arrivee.noeudPrecedent = noeudCourant;
                    pq.add(noeudCourant.lArretes.get(i).arrivee);
                }
            }

            if (pq.isEmpty()) break;

            noeudCourant = pq.poll();

            if (arrivee == noeudCourant) { // on reconstruit le chemin (limité à 100 au cas où il y a des boucles)
                while (noeudCourant != null && chemin.size() < 100) {
                    chemin.add(0, noeudCourant.position);
                    noeudCourant = noeudCourant.noeudPrecedent;
                }
                for (Noeud k : g.getlNoeuds()) //on réinitialise les noeuds pour le suivant
                {

                    k.sommedepart = 100000000;
                    k.noeudPrecedent = null;

                }

                //on supprime les liens dans le sens retour liés au départ
                for (int j = 0; j < depart.lArretes.size(); j++) {
                    int p = depart.lArretes.get(j).arrivee.lArretes.size();
                    for (int i = 0; i < p; i++) {

                        if (depart.lArretes.get(j).arrivee != depart && depart.lArretes.get(j).arrivee.lArretes.get(i).arrivee == depart) { // Il peut arriver que le noeud créer un lien vers lui même, on vérifie donc que ce n'est pas le cas
                            depart.lArretes.get(j).arrivee.lArretes.remove(depart.lArretes.get(j).arrivee.lArretes.get(i));
                            i--;
                            p--;

                        }
                    }
                }

                //on supprime les liens dans le sens retour liés à l'arrivée
                for (int j = 0; j < arrivee.lArretes.size(); j++) {
                    int p = arrivee.lArretes.get(j).arrivee.lArretes.size();
                    for (int i = 0; i < p; i++) {

                        if (arrivee.lArretes.get(j).arrivee != arrivee && arrivee.lArretes.get(j).arrivee.lArretes.get(i).arrivee == arrivee) {
                            arrivee.lArretes.get(j).arrivee.lArretes.remove(arrivee.lArretes.get(j).arrivee.lArretes.get(i));
                            i--;
                            p--;
                        }
                    }
                }

                //On détache le noeud de départ et d'arrivée
                g.getlNoeuds().remove(depart);
                g.getlNoeuds().remove(arrivee);
                return chemin;
            }


            closedlist.add(noeudCourant);


        } while (!pq.isEmpty());


        log.debug("No path found");

        //on supprime les liens dans le sens retour liés au départ
        for (int j = 0; j < depart.lArretes.size(); j++) {
            int p = depart.lArretes.get(j).arrivee.lArretes.size();
            for (int i = 0; i < p; i++) {

                if (depart.lArretes.get(j).arrivee != depart && depart.lArretes.get(j).arrivee.lArretes.get(i).arrivee == depart) { // Il peut arriver que le noeud créer un lien vers lui même, on vérifie donc que ce n'est pas le cas
                    depart.lArretes.get(j).arrivee.lArretes.remove(depart.lArretes.get(j).arrivee.lArretes.get(i));
                    i--;
                    p--;

                }
            }
        }

        //on supprime les liens dans le sens retour liés à l'arrivée
        for (int j = 0; j < arrivee.lArretes.size(); j++) {
            int p = arrivee.lArretes.get(j).arrivee.lArretes.size();
            for (int i = 0; i < p; i++) {

                if (arrivee.lArretes.get(j).arrivee != arrivee && arrivee.lArretes.get(j).arrivee.lArretes.get(i).arrivee == arrivee) {
                    arrivee.lArretes.get(j).arrivee.lArretes.remove(arrivee.lArretes.get(j).arrivee.lArretes.get(i));
                    i--;
                    p--;

                }
            }
        }

        //On détache le noeud de départ et d'arrivée
        g.getlNoeuds().remove(depart);
        g.getlNoeuds().remove(arrivee);
        return new ArrayList<Vec2>();
    }

    // TODO : savoir à quoi sert cette méthode
    public ArrayList<Vec2> ennemiDetecte(Vec2 posRobot, Vec2 cible, double robotOrientation) {
        ObstacleManager a = this.table.getObstacleManager();

        //creerNoeudoptimal();
        ObstacleProximity ennemi = table.getObstacleManager().getMobileObstacles().get(0);

        double angle = Math.acos(ennemi.getRadius() / posRobot.distance(ennemi.getPosition()));
        Vec2 jeanMichel = new Vec2((int) (ennemi.getRadius() * Math.cos(angle)), (int) (ennemi.getRadius() * Math.asin(angle)));
        Vec2 m = ennemi.getPosition().plusNewVector(jeanMichel);

        double angleArrivee = Math.acos(ennemi.getRadius() / cible.distance(ennemi.getPosition()));
        Vec2 jeanMichelArrivee = new Vec2((int) (ennemi.getRadius() * Math.cos(angle)), (int) (ennemi.getRadius() * Math.asin(angle)));
        Vec2 m2 = ennemi.getPosition().plusNewVector(jeanMichelArrivee);

        Vec2 k = Geometry.intersection(new Segment(posRobot, m), new Segment(cible, m2));

        boolean creedepart = true;
        boolean creearrivee = true;
        int j = 0;
        int nbObstCirc = a.getmCircularObstacle().size();
        int nbObstRect = a.getRectangles().size();
        Segment segdep = new Segment(posRobot, k);
        Segment segarr = new Segment(k, cible);
        while (creearrivee && creedepart && j < nbObstCirc) {
            creearrivee = !(a.getmCircularObstacle().get(j).isInObstacle(cible));
            creedepart = !(a.getmCircularObstacle().get(j).isInObstacle(k));

            creedepart = creedepart && !(Geometry.intersects(segdep, new Circle(a.getmCircularObstacle().get(j).getPosition(), a.getmCircularObstacle().get(j).getRadius())));
            creearrivee = creearrivee && !(Geometry.intersects(segarr, new Circle(a.getmCircularObstacle().get(j).getPosition(), a.getmCircularObstacle().get(j).getRadius())));

            j++;
        }
        j = 0;
        while (creearrivee && creedepart && j < nbObstRect) {
            creearrivee = !(a.getRectangles().get(j).isInObstacle(cible));
            creedepart = !(a.getRectangles().get(j).isInObstacle(k));

            creedepart = creedepart && !Geometry.intersects(segdep, new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(1).position));
            creedepart = creedepart && !Geometry.intersects(segdep, new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(3).position));
            creedepart = creedepart && !Geometry.intersects(segdep, new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(2).position));
            creedepart = creedepart && !Geometry.intersects(segdep, new Segment(a.getRectangles().get(j).getlNoeud().get(2).position, a.getRectangles().get(j).getlNoeud().get(3).position));
            creedepart = creedepart && !Geometry.intersects(segdep, new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(2).position));
            creedepart = creedepart && !Geometry.intersects(segdep, new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(3).position));

            creearrivee = creearrivee && !Geometry.intersects(segarr, new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(1).position));
            creearrivee = creearrivee && !Geometry.intersects(segarr, new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(3).position));
            creearrivee = creearrivee && !Geometry.intersects(segarr, new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(2).position));
            creearrivee = creearrivee && !Geometry.intersects(segarr, new Segment(a.getRectangles().get(j).getlNoeud().get(2).position, a.getRectangles().get(j).getlNoeud().get(3).position));
            creearrivee = creearrivee && !Geometry.intersects(segarr, new Segment(a.getRectangles().get(j).getlNoeud().get(1).position, a.getRectangles().get(j).getlNoeud().get(2).position));
            creearrivee = creearrivee && !Geometry.intersects(segarr, new Segment(a.getRectangles().get(j).getlNoeud().get(0).position, a.getRectangles().get(j).getlNoeud().get(3).position));

            j++;
        }
        if (creearrivee && creedepart) {
            // vecteur OM

            // si aucun des deux pointsest pas dans un obstacle et que aucun des deux traits intersecte avec un obstacle
            // on le renvoie
            // Sinon on tente de décaler l'obstacle
            //sinon on refait le graphe
            ArrayList<Vec2> renvoi = new ArrayList<Vec2>();
            renvoi.add(k);
            return renvoi;
        } else {
            this.graphe.initGraphe();
            try {
                return Astarfoulah(posRobot, cible, robotOrientation);
            } catch (PointInObstacleException e) {

            }

        }
        return new ArrayList<Vec2>();
    }

    @Override
    public void updateConfig() {

    }

    /**
     * Methode qui determine si le vecteur est dans un obstacle
     *
     * @param propo la position (celle du robot enfaite...)
     * @autor Rem
     */
    private Obstacle whichObstacle(Vec2 propo) {
        for (Obstacle o : table.getObstacleManager().getmCircularObstacle()){
            if (o.isInObstacle(propo)){
                return o;
            }
        }
        for (Obstacle o : table.getObstacleManager().getmRectangles()){
            if (o.isInObstacle(propo)){
                return o;
            }
        }
        return null;
    }
}