/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
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
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package de.tu_clausthal.in.mec.object;

import de.tu_clausthal.in.mec.runtime.ISteppable;


/**
 * interface of the layer
 */
public interface ILayer extends ISteppable
{

    /**
     * returns an index value to defining an order for calculation
     *
     * @return index value of ordering (need not be unique)
     */
    int getCalculationIndex();

    /**
     * checks activity of the layer
     *
     * @return boolean of activity
     */
    boolean isActive();

    /**
     * activates / deactivates the layer
     *
     * @param p_active activity
     */
    void setActive( final boolean p_active );

    /**
     * is called on simulation start
     */
    void onSimulationStart();

    /**
     * is called on simulation stop
     */
    void onSimulationStop();

    /**
     * is called on simulation reset
     */
    void onSimulationReset();

}
