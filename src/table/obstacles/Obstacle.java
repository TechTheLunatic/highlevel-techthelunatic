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

package table.obstacles;
import smartMath.Vec2;

/**
 * classe abstraite pour les obstacles sur la table.
 * Les obstacles peuvent avoir différentes formes, et être soit fixes d'un match a l'autre, soit mobiles (un robot adverse est par exemple un obstacle mobile)
 * @author pf, marsu
 *
 */
public abstract class Obstacle
{

	/** Position de l'obstacle sur la table. En fonction de la forme de l'obstacle, il peut s'étendre plus ou moins loin de cette position dans diverses directions */
	protected Vec2 position;
	
	/**
	 * construit un nouvel obstacle a position donnée
	 *
	 * @param position position de l'obstacle a construire
	 */
	public Obstacle (Vec2 position)
	{
		this.position = position.clone();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract Obstacle clone();

	/**
	 * Renvois la position de cet obstacle.
	 *
	 * @return the position
	 */
	public Vec2 getPosition()
	{
		return this.position;
	}
	
	public void setPosition(Vec2 position)
	{
		this.position = position;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "Obstacle en "+position;
	}
	
}
