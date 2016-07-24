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

package smartMath;

/**
 * 
 * @author paul
 *Classe des cercles, utile pour les points d'entree des scripts et peut-etre pour le pathDingDing
 */
public class Circle {

	public Vec2 position;
	public double radius;
	
	/**
	 * construit un cercle
	 * @param position le centre en mm  pas de virgule
	 * @param radius le rayon en mm, avec virgule
	 */
	public Circle(Vec2 position, double radius)
	{
		this.position=position;
		this.radius=radius;
	}

	/**
	 * construit un cercle
	 * dans ce constructeur on suppose le rayon nul
	 *
	 * @param center le centre en mm, pas de virgule
	 */
	public Circle(Vec2 center)
	{
		this.position=center;
		this.radius=0;
	}
	/**
	 * construit un cercle de rayon nul
	 * @param centerX la coordonee en abscisse du centre
	 * @param centerY la coordonee en ordonne du centre
	 */
	public Circle (int centerX, int centerY)
	{
		this.position=new Vec2(centerX,centerY);
		this.radius=0;
	}

	/**
	 * cree un nouveau cerlce
	 * @param centerX la position en abscisse du centre
	 * @param centerY la position en ordonnee du centre
	 * @param ray le rayon
	 */
	public Circle(int centerX, int centerY, int ray) 
	{
		this.position=new Vec2(centerX,centerY);
		this.radius=ray;
	}

	/**
	 * 
	 * @return le centre du cercle (position en mm)
	 */
	public Vec2 toVec2()
	{
		return this.position;
	}

	/**
	 * test si le Vec2 est dans le disque
	 * @param point un vec2 a tester
	 * @return vrai si le point est a l'interieur du cercle ou dessus
	 */
	public boolean containDisk(Vec2 point)
	{
		return (point.distance(this.position)<=this.radius);
	}
	
	/**
	 * test si le Vec2 appartient au cercle
	 * @param point un Vec2 a tester
	 * @return vrai si le point est sur le cercle
	 */
	public boolean containCircle(Vec2 point)
	{
		double dx=point.x-this.position.x;
		double dy=point.y-this.position.y;
		return (dx*dx+dy*dy)==(radius*radius);
	}
}
