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

/* ============================================================================
 * 				Vec2 class
 * ============================================================================
 * 
 * Bi-dimentionnal vector 2 class. Simple-precision members.
 * Author : Dede
 * Refactoring : Martial
 */

package smartMath;

/**
 * Classe de calcul de vecteurs de dimension 2.
 *
 * @author martial
 * @author pf
 */

public class Vec2
{

	/** Abscisse x*/
	public int x;
	
	/** Ordonnée y*/
	public int y;

	/** Rayon r */
	public double r;

	/** Angle a, entre -pi et pi */
	public double a;
	
	/** Constructeur d'un vecteur nul */
	public Vec2()
	{
		x = 0;
		y = 0;
		r = 0;
		a = 0;
	}
	public boolean isNull()
	{
		return (this.x==0 && this.y==0);
	}

	/**
	 * Construit un vecteur à partir de ses coordonnées cartésiennes
	 * @param requestedX abscisse
	 * @param requestedY ordonnée
	 */
	public Vec2(int requestedX, int requestedY) {
		x = requestedX;
		y = requestedY;
		r = Math.sqrt(x * x + y * y);
		a = this.angle();
	}

	/**
	 * Construit un vecteur à partir de ses coordonnées polaires
	 * @param requestedR rayon
	 * @param requestedA angle
	 */
	public Vec2(double requestedR, double requestedA)
	{
		r = requestedR;
		a = requestedA;
		x = (int)(r*Math.cos(a)+1);
		y = (int)(r*Math.asin(a)+1);
	}
	
	// Il est plus performant de trouver la longueur au carré et de la comparer à des distances au carré que d'en extraire la racine
	/**
	 * @return la longueur au carré du vecteur 
	 */
	public int squaredLength()
	{
		return (int) (r*r);
	}

	/**
	 * @return la longueur du vecteur
	 */
	public float length()
	{
		return (float) r;
	}

	/**
	 * Effectue le produit scalaire avec un second vecteur
	 * @param other le second vecteur du produit scalaire
	 * @return résultat du produit
	 */
	public int dot(Vec2 other)
	{
		return x*other.x + y*other.y;
	}
	
	/**
	 * Construit un nouveau vecteur avec une somme
	 * @param other le vecteur à sommer au premier
	 * @return le nouveau vecteur
	 */
	public Vec2 plusNewVector(Vec2 other)
	{
		return new Vec2(x + other.x, y + other.y);
	}
	
	/**
	 * Construit un nouveau vecteur avec une différence
	 * @param other le vecteur à soustraire au premier
	 * @return le nouveau vecteur
	 */
	public Vec2 minusNewVector(Vec2 other)
	{
		return new Vec2(x - other.x, y - other.y);
	}

	/**
	 * Accroissement du vecteur actuel par un second
	 * @param other le second vecteur
	 */
	public void plus(Vec2 other)
	{
		x += other.x;
		y += other.y;
		r = Math.sqrt(x*x + y*y);
		a = this.angle();
	}
	
	/**
	 * Réduction du vecteur actuel par un second
	 * @param other le second vecteur
	 */
	public void minus(Vec2 other)
	{
		x -= other.x;
		y -= other.y;
		r = Math.sqrt(x*x + y*y);
		a = this.angle();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Vec2 clone()
	{
		return new Vec2(this.x, this.y);
	}
	
	/**
	 * Calcul la distance au carré séparant le vecteur actuel d'un second
	 * @param other le second vecteur
	 * @return distance au carré entre les deux vecteurs
	 */
	public float squaredDistance(Vec2 other)
	{
		return (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y);
	}

	/**
	 * Distance entre le vecteur actuel et un second
	 * @param other le second vecteur
	 * @return la distance entre les deux vecteurs
	 */
	public float distance(Vec2 other)
	{
		return (float) Math.sqrt(squaredDistance(other));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "("+x+","+y+") ("+r+","+a+")";
	}
	
	/**
	 * transforme un point en un cercle de rayon nul
	 * @return un cercle de centre ce Vec2 et de rayon nul.
	 */
	public Circle toCircle()
	{
		return new Circle (this,0);
	}
	
	/**
	 * Compare deux vecteurs
	 * @param other le second vecteur
	 * @return vrai si les coordonnées sont égales
	 */
	public boolean equals(Vec2 other)
	{
		return x == other.x && y == other.y;
	}
	
	/**
	 * Multiplication par un scalaire
	 * @param a le scalaire
	 * @return ancien vecteur dilaté
	 */
	public Vec2 dotFloat(double a)
	{
		return new Vec2((int)(x*a),(int)(y*a));
	}
	
	/**
	 * Remplace les coordonnées du vecteur actuel par celui d'un second
	 * @param other le second vecteur
	 */
	public void set(Vec2 other)
	{
		x = other.x;
		y = other.y;
		r = other.r;
		a = other.a;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (!(obj instanceof Vec2))
			return false;
		Vec2 other = (Vec2) obj;
		if (x != other.x)
			return false;
		else if (y != other.y)
			return false;
		return true;
	}

	/**
	 * Distance Manhattan avec un second vecteur
	 * @param other le second vecteur
	 * @return la distance Manhattan
	 */
	public int manhattan_distance(Vec2 other)
	{
		return Math.abs(x - other.x) + Math.abs(y - other.y); 
	}

	/**
	 * Angle du vecteur par rapport à l'abscisse
	 * @return l'angle en radians
     */
	public double angle()
	{
		int signe=0;
		if(this.squaredLength() == 0)
			return 0;

		if (this.y<0){
			signe=-2;
		}
		return signe*Math.PI + Math.atan2(this.y, this.x);
	}

	/**
	 * Tous les setters et getters parce private :p
	 */

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		r = this.length();
		a = this.angle();
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		r = this.length();
		a = this.angle();
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
		x = (int) (r*Math.cos(a));
		y = (int) (r*Math.sin(a));
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
		x = (int) (r*Math.cos(a));
		y = (int) (r*Math.sin(a));
	}
}

