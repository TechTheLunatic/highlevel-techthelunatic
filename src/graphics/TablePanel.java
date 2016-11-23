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

//TODO : refactor

package graphics;

import pathfinder.Arrete;
import pathfinder.Noeud;
import robot.Robot;
import smartMath.Segment;
import smartMath.Vec2;
import table.Table;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleProximity;
import table.obstacles.ObstacleRectangular;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * panneau sur lequel est dessine la table
 * @author Etienne
 *
 */
public class TablePanel extends JPanel
{	
	/** numéro pour la serialisation	 */
	private static final long serialVersionUID = -3033815690221481964L;
	
	private ArrayList<Vec2> mPath;
	private ArrayList<Vec2> mGraph;
	private ArrayList<Vec2> mArr;
	private Table mTable;
	private Robot mRobot;
	private boolean isRobotPresent = true;

	public TablePanel(Table table, Robot robot)
	{
		mPath = new ArrayList<Vec2>();
		mGraph = new ArrayList<>();
		mArr = new ArrayList<>();
		mTable = table;
		mRobot = robot;
	}
	
	public TablePanel(Table table)
	{
		mPath = new ArrayList<Vec2>();
		mGraph = new ArrayList<>();
		mArr = new ArrayList<>();
		mTable = table;
		isRobotPresent = false;
	}
	
	public void paintComponent(Graphics g)
	{
		// Les bords de la table
		g.setColor(Color.black);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    
	    // Lignes des obstacles 
	    g.setColor(Color.darkGray);
	    ArrayList<Segment> lines = mTable.getObstacleManager().getLines();
	    for(int i = 0; i < lines.size(); i++)
	    {
	    	g.drawLine((lines.get(i).getA().x + 1500) * this.getWidth() / 3000,
                    (-lines.get(i).getA().y) * this.getHeight() / 2000 + this.getHeight(),
                    (lines.get(i).getB().x + 1500) * this.getWidth() / 3000,
                    (-lines.get(i).getB().y) * this.getHeight() / 2000 + this.getHeight());
	    }
	    
	    
	    // Obstacles rectangulaires
	    g.setColor(Color.white);
	    ArrayList<ObstacleRectangular> rects = mTable.getObstacleManager().getRectangles();
	    for(int i = 0; i < rects.size(); i++)
	    {
	    	g.fillRect((rects.get(i).getPosition().x - (rects.get(i).getSizeX() / 2) + 1500) * this.getWidth() / 3000, 
	    			  -(rects.get(i).getPosition().y + rects.get(i).getSizeY()/2) * this.getHeight() / 2000 + this.getHeight(),
	    			  rects.get(i).getSizeX() * this.getWidth() / 3000, 
	    			  rects.get(i).getSizeY() * this.getHeight() / 2000);
	    }	    
	    
	    // Les obstacles fixes : plots, gobelets
	    g.setColor(Color.white);
	    ArrayList<ObstacleCircular> fixedObstacles = mTable.getObstacleManager().getFixedObstacles();
	    for(int i = 0; i < fixedObstacles.size(); i++)
	    {
			g.drawOval((fixedObstacles.get(i).getPosition().x - (fixedObstacles.get(i).getRadius() /*+ mTable.getObstacleManager().getRobotRadius()*/) + 1500) * this.getWidth() / 3000,
					-(fixedObstacles.get(i).getPosition().y + fixedObstacles.get(i).getRadius() /*+ mTable.getObstacleManager().getRobotRadius()*/) * this.getHeight() / 2000 + this.getHeight(),
					(2 * (fixedObstacles.get(i).getRadius())) * this.getWidth() / 3000,
					(2 * (fixedObstacles.get(i).getRadius())) * this.getHeight() / 2000);
	    }
	    
	    //les robots ennemis
	    g.setColor(Color.red);
	    ArrayList<ObstacleProximity> ennemyRobots = mTable.getObstacleManager().getMobileObstacles();
	    for(int i = 0; i < ennemyRobots.size(); i++)
		    g.drawOval((ennemyRobots.get(i).getPosition().x - ennemyRobots.get(i).getRadius() + 1500) * this.getWidth() / 3000,
		    		-(ennemyRobots.get(i).getPosition().y + ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight(),
		    		(2 * ennemyRobots.get(i).getRadius()) * this.getWidth() / 3000,
					(2 * ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000);
	    
	    //les robots ennemis non confirmés
	    g.setColor(new Color(0, 100, 100));
	    ennemyRobots = mTable.getObstacleManager().getUntestedArrayList();
	    for(int i = 0; i < ennemyRobots.size(); i++)
		    g.drawOval((ennemyRobots.get(i).getPosition().x - ennemyRobots.get(i).getRadius() + 1500) * this.getWidth() / 3000,
		    		-(ennemyRobots.get(i).getPosition().y + ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight(),
		    		(2 * ennemyRobots.get(i).getRadius()) * this.getWidth() / 3000,
					(2 * ennemyRobots.get(i).getRadius()) * this.getHeight() / 2000);
	    
		// Notre robot
	    if(isRobotPresent)
	    {
		    g.setColor(Color.green);
		    Vec2 position = mRobot.getPosition();
		    double orientation = mRobot.getOrientation();
		    g.drawOval( (position.x - 100 + 1500) * this.getWidth() / 3000,
		    		   -(position.y + 100) * this.getHeight() / 2000 + this.getHeight(), 
		    		    (2 * 100) * this.getWidth() / 3000,
		    		    (2 * 100) * this.getHeight() / 2000);
		    g.drawLine((position.x + 1500) * this.getWidth() / 3000, 
		    			-position.y * this.getHeight() / 2000 + this.getHeight(),
		    			(int)((position.x + 200*Math.cos(orientation) + 1500) * this.getWidth() / 3000),
		    			(int)(-(position.y + 200*Math.sin(orientation)) * this.getHeight() / 2000 + this.getHeight()));
	    }
	    
	    // un chemin
	    g.setColor(Color.blue);
	    for(int i = 0; i+1 < mPath.size(); i++)
	    {
	    	g.drawLine( (mPath.get(i).x + 1500) * this.getWidth() / 3000, 
	    			    -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight(),
	    			    (mPath.get(i+1).x + 1500) * this.getWidth() / 3000,
	    			    -mPath.get(i+1).y * this.getHeight() / 2000 + this.getHeight() );
	    }
	    
	    // les points du chemin
	    g.setColor(Color.cyan);
	    for(int i = 0; i < mPath.size(); i++)
	    {
	    	g.fillOval( (mPath.get(i).x + 1500) * this.getWidth() / 3000 - 3,
	    			    -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight() - 3,
	    			     6,
	    			     6);
	    }

		for(int i = 0; i < mGraph.size(); i++)
		{
			g.fillOval( (mGraph.get(i).x + 1500) * this.getWidth() / 3000 - 3,
					-mGraph.get(i).y * this.getHeight() / 2000 + this.getHeight() - 3,
					6,
					6);
		}

		g.setColor(Color.MAGENTA);
		for(int i = 1; i+1 < mArr.size(); i++)
		{
			g.drawLine( (mArr.get(0).x + 1500) * this.getWidth() / 3000,
					-mArr.get(0).y * this.getHeight() / 2000 + this.getHeight(),
					(mArr.get(i).x + 1500) * this.getWidth() / 3000,
					-mArr.get(i).y * this.getHeight() / 2000 + this.getHeight() );
		}
	    
	    // les coordonnées des points du chemin
	    g.setColor(Color.magenta);
	    for(int i = 0; i < mPath.size(); i++)
	    {
	    	g.drawString(mPath.get(i).x + ", " + mPath.get(i).y, 
	    			    (mPath.get(i).x + 1500) * this.getWidth() / 3000, 
	    			    -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight());
	    }
	    
	    g.setColor(Color.yellow);
	    g.drawOval( (mTable.getObstacleManager().getDiscPosition().x- mTable.getObstacleManager().getDiscRadius() + 1500) * this.getWidth() / 3000,
	    		   -(mTable.getObstacleManager().getDiscPosition().y + mTable.getObstacleManager().getDiscRadius()) * this.getHeight() / 2000 + this.getHeight(), 
	    		    (2 * mTable.getObstacleManager().getDiscRadius()) * this.getWidth() / 3000,
	    		    (2 * mTable.getObstacleManager().getDiscRadius()) * this.getHeight() / 2000);
	}
	
	//permet d'afficher un chemin
	public void drawArrayList(ArrayList<Vec2> path)
	{
		mPath = path;
		repaint();
	}

	//permet d'afficher le graphe
	public void drawGraphe(ArrayList<Vec2> graph)
	{
		mGraph = graph;
		repaint();
	}

	//permet d'afficher les arretes d'un noeud
	public void drawArretes(Noeud node)
	{
		mArr.clear();
		mArr.add(node.position);

		for(Arrete a : node.lArretes)
		{
			mArr.add(a.arrivee.position);
		}
	}
	
	public Table getTable()
	{
		return mTable;
	}
}
