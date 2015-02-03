/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.mas.jason.action;


import jason.asSyntax.Structure;


/**
 * action of an agent
 */
public abstract class IAction
{

    /**
     * returns the name of the action
     *
     * @return name of the action
     */
    public String getName()
    {
        return null;
    }

    /**
     * runs the action *
     *
     * @param p_args arguments of the action
     */
    public void act( Structure p_args )
    {
    }

    @Override
    public int hashCode()
    {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals( Object obj )
    {
        return this.hashCode() == obj.hashCode();
    }

}