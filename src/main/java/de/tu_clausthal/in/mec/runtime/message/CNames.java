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

package de.tu_clausthal.in.mec.runtime.message;


import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.mas.IAgent;


/**
 * create a global message name structure
 */
public final class CNames
{

    /**
     * creates a full name
     *
     * @param p_object object
     * @param p_name name
     * @return fqn path
     */
    public static CPath getName( final Object p_object, final String p_name )
    {
        final CPath l_path = getGroup( p_object );
        l_path.pushback( p_name );
        return l_path;
    }

    /**
     * creates a full name
     *
     * @param p_object object
     * @return fqn path
     */
    public static CPath getName( final Object p_object )
    {
        return getName( p_object, p_object.toString() );
    }

    /**
     * creates the group path
     *
     * @param p_object object
     * @return group path
     */
    protected static CPath getGroup( final Object p_object )
    {

        if ( p_object instanceof IAgent )
            return new CPath( "agent" );

        if ( p_object instanceof ICar )
            return new CPath( "car" );

        return new CPath( "unkown" );
    }

}
