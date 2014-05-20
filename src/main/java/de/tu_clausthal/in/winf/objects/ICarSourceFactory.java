/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.objects;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

import java.awt.*;
import java.util.Collection;


/**
 * factory interface of car - defines a source
 */
public interface ICarSourceFactory extends Waypoint {

    /**
     * sets the value how many cars are created in one step
     *
     * @param p_number integer number greate than zero
     */
    public void setNumberOfCars(int p_number);


    /**
     * creates a list of new car objects
     *
     * @return collection of cars
     */
    public Collection<ICar> generate();


    /**
     * returns the color of the source
     * for rendering on the map
     *
     * @return Color
     */
    public Color getColor();


    /**
     * returns the geoposition of the source
     *
     * @return geoposition
     */
    public GeoPosition getPosition();

}