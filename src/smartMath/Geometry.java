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
 * classe de calculs de géométrie
 * @author Etienne
 */
public class Geometry
{
	/**
	 * calcule le "vrai" modulo (entre 0 et module) contairement a % qui calcule entre -module et module
	 * @param number le nombre dont on veut calculer le modulo
	 * @param module le module pour le modulo
	 * @return number [module]
	 */
	public static double modulo(double number, double module)
	{
		double modulo = number%module;
		if (modulo<0)
			modulo += module;
		return modulo;
	}
	
	/**
	 * calcule la différence entre deux angles dans un cercle (prends en compte le fait que le cercle soit circulaire)
	 * @param angle1 le premier angle entre 0 et sizeOfCircle
	 * @param angle2 le deuxieme angle entre 0 et sizeOfCircle (on peut échanger angle1 et angle2 sans changer le retour)
	 * @param sizeOfCircle la taille du cercle (2Pi en Radiant 2000Pi en milliRadian ou 360 en Degré par exemple)
	 * @return angle1 - angle2 dans un espace circulaire, forcement < a sizeOfCircle
	 */
	public static double minusAngle(double angle1, double angle2, double sizeOfCircle)
	{
		double angleMin = Math.min(angle1, angle2);
		double angleMax = Math.max(angle1, angle2);
		return Math.min(angleMax-angleMin, sizeOfCircle-angleMax+angleMin);
	}

	/**
	 * dit si l'angleEnd est plus loin que angleBegin sur le cercle trigo (prends en compte le fait que le cercle soit circulaire)
	 * @param angleBegin l'angle de position initiale (doit être en Radian pas milliRadian)
	 * @param angleEnd l'angle dont on veut savoir si il est plus loin sur le cercle trigo (doit être en Radian et pas en milliRadian)
	 * @return true si angleEnd est plus loin que angleBegin sur le cercle trigo
	 */
	public static boolean isFurtherInTrigoCircle(double angleBegin, double angleEnd) 
	{
		// si on ne passe pas par 2PI alors sera le plus petit (en abs)
		double possibility1 = modulo(angleEnd, 2*Math.PI)-modulo(angleBegin, 2*Math.PI);
		// si on passe par 2PI alors sera le plus petit (en abs)
		double possibility2 = modulo(angleEnd, 2*Math.PI)+2*Math.PI-modulo(angleBegin, 2*Math.PI);
		
		if (Math.abs(possibility1)<Math.abs(possibility2))
			//on ne passe pas par 2PI, on regarde si on tourne dans le sens trigo
			return possibility1>0;
		else
			//on passe par 2PI, on regarde si on tourne dans le sens trigo
			return possibility2>0;
	}
	
	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @return vrai si il y a intersection entre les deux segments, faux sinon (les extremités ne sont pas comptées comme intersection)
	 */
	public static boolean intersects(Segment segment1, Segment segment2)
	{
		// les points formant les segments 1 et 2 sont A1, B1, A2, B2
		// pour qu'il y ait intersection, il faut :
		// - les segments ne soient pas parallèles : (A1B1)^(A2B2) != 0
		// - le point d'intersection est entre A2 et B2 : (A1B1)^(A1B2) * (A1B1)^(A1A2) < 0
		// - le point d'intersection est entre A1 et B1 : (A2B2)^(A2B1) * (A2B2)^(A2A1) < 0
		// ^ = produit vectoriel
		return ((double)segment1.getB().x - (double)segment1.getA().x) * ((double)segment2.getB().y - (double)segment2.getA().y) - ((double)segment1.getB().y - (double)segment1.getA().y) * ((double)segment2.getB().x - (double)segment2.getA().x) != 0
				&& (((double)segment1.getB().x - (double)segment1.getA().x) * ((double)segment2.getB().y - (double)segment1.getA().y) - ((double)segment1.getB().y - (double)segment1.getA().y) * ((double)segment2.getB().x - (double)segment1.getA().x)) * (((double)segment1.getB().x - (double)segment1.getA().x) * ((double)segment2.getA().y - (double)segment1.getA().y) - ((double)segment1.getB().y - (double)segment1.getA().y) * ((double)segment2.getA().x - (double)segment1.getA().x)) < 0
				&& (((double)segment2.getB().x - (double)segment2.getA().x) * ((double)segment1.getB().y - (double)segment2.getA().y) - ((double)segment2.getB().y - (double)segment2.getA().y) * ((double)segment1.getB().x - (double)segment2.getA().x)) * (((double)segment2.getB().x - (double)segment2.getA().x) * ((double)segment1.getA().y - (double)segment2.getA().y) - ((double)segment2.getB().y - (double)segment2.getA().y) * ((double)segment1.getA().x - (double)segment2.getA().x)) < 0
				;
	}
	
	/**
	 * 
	 * @param segment
	 * @param circle
	 * @return vrai si il y a intersection entre le segment et le cercle, faux sinon
	 */
	public static boolean intersects(Segment segment, Circle circle)
	{
		// TODO : expliquer l'algo (TOO MANY CASTS EXCEPTION)
		double area = ((double)circle.center.x - (double)segment.getA().x)*((double)segment.getB().y - (double)segment.getA().y) - ((double)circle.center.y - (double)segment.getA().y)*((double)segment.getB().x - (double)segment.getA().x);
		double distA = ((double)segment.getA().x - (double)circle.center.x)*((double)segment.getA().x - (double)circle.center.x) + ((double)segment.getA().y - (double)circle.center.y)*((double)segment.getA().y - (double)circle.center.y);
		double distB = ((double)segment.getB().x - (double)circle.center.x)*((double)segment.getB().x - (double)circle.center.x) + ((double)segment.getB().y - (double)circle.center.y)*((double)segment.getB().y - (double)circle.center.y);
		if(distA >= (double)circle.radius*(double)circle.radius && distB < (double)circle.radius*(double)circle.radius || distA < (double)circle.radius*(double)circle.radius && distB >= (double)circle.radius*(double)circle.radius)
			return true;
		return distA >= (double)circle.radius*(double)circle.radius
			&& distB >= (double)circle.radius*(double)circle.radius
			&& area * area / (((double)segment.getB().x - (double)segment.getA().x)*((double)segment.getB().x - (double)segment.getA().x)+((double)segment.getB().y - (double)segment.getA().y)*((double)segment.getB().y - (double)segment.getA().y)) <= (double)circle.radius * (double)circle.radius
			&& ((double)segment.getB().x - (double)segment.getA().x)*((double)circle.center.x - (double)segment.getA().x) + ((double)segment.getB().y - (double)segment.getA().y)*((double)circle.center.y - (double)segment.getA().y) >= 0
			&& ((double)segment.getA().x - (double)segment.getB().x)*((double)circle.center.x - (double)segment.getB().x) + ((double)segment.getA().y - (double)segment.getB().y)*((double)circle.center.y - (double)segment.getB().y) >= 0;
	}
	
	
	
	/**
	 * 
	 * @param segment1
	 * @param segment2
	 * @return le point d'intersection des droites portées par les segments.
	 */
	public static Vec2 intersection(Segment segment1, Segment segment2)
	{
		// resolution du systeme associe aux deux segments
		double inter, k;
		
		if((segment2.getB().y - segment2.getA().y) != 0)
		{
			inter = (double)(segment2.getB().x - segment2.getA().x) / (double)(segment2.getB().y - segment2.getA().y);
			k = (segment1.getA().x - segment2.getA().x + inter * (double)(segment2.getA().y - segment1.getA().y)) / (double)(segment1.getB().x - segment1.getA().x - inter * (segment1.getB().y - segment1.getA().y));
		}
		else
			k = -(double)(segment2.getA().y - segment1.getA().y) / (double)(segment1.getB().y - segment1.getA().y);
		
		return new Vec2((int)(segment1.getA().x - k * (segment1.getB().x - segment1.getA().x)), (int)(segment1.getA().y - k * (segment1.getB().y - segment1.getA().y)));
	}

	/**
	 * Vérifie si la valeur donnée est entre les bornes données (limites incluses), utilisé pour simplifier les if
	 * @param val la valeur à tester
	 * @param a borne inf
	 * @param b borne sup
     */
	public static boolean isBetween(double val, double a, double b)
	{
		if(a>b) //Si le singe a mie de pain inf à la place de sup
		{
			double temp=b;
			b=a;
			a=temp;
		}

		return val >= a && val <= b;
	}
}
