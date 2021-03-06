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

package de.tu_clausthal.in.mec;


/**
 * bootstrap for the Java main call - checking of the correct installed JRE
 *
 * @warning the class must be compiled with a lower target version of the JRE,
 * the Maven build script uses different profiles to do this
 */
public final class CBoot
{

    /**
     * private ctor - avoid instantiation
     */
    private CBoot()
    {
    }


    /**
     * main bootstrap program
     *
     * @param p_args commandline arguments
     */
    public static void main( final String[] p_args )
    {
        checkJVM();
        CMain.main( p_args );
    }


    /**
     * checks th Java runtime version
     */
    private static void checkJVM()
    {
        // check JRE properties (version must be checked numerical)
        if ( !( ( "Oracle Corporation".equalsIgnoreCase( System.getProperty( "java.vendor" ) ) ) && ( Float.parseFloat(
                System.getProperty(
                        "java.specification.version"
                )
        ) >= 1.8f ) ) )
        {
            System.err.println( "JRE from Oracle Corporation (http://www.java.com/) version 1.8 or newer must be installed to performtemplate the program" );
            System.exit( -1 );
        }
    }

}
