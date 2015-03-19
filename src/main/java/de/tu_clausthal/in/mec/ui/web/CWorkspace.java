/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
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
 **/

package de.tu_clausthal.in.mec.ui.web;


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.ui.CBrowser;

import java.io.File;


/**
 * main workspace
 */
public class CWorkspace extends CBrowser
{
    /**
     * HTTP server to handle websockets *
     */
    protected final CServer m_server = new CServer( "localhost", CConfiguration.getInstance().get().getUibindport(), new CVirtualDirectory( new File( this.getClass().getClassLoader().getResource( "web/root" ).getFile() ), "index.htm" ) );


    /**
     * ctor - adds the browser *
     */
    public CWorkspace()
    {
        super();

        // because on developer structure the Doxygen build-in documentation cannot exists, run it with try-catch to avoid null-pointer-exceptions
        try
        {
            m_server.getVirtualLocation().getLocations().add( new CVirtualFile( new File( this.getClass().getClassLoader().getResource( "web/documentation/user/layout.css" ).getFile() ), "/userdoc/layout.css" ) );
            m_server.getVirtualLocation().getLocations().add( new CVirtualDirectory( new File( this.getClass().getClassLoader().getResource( "web/documentation/user/" + CConfiguration.getInstance().get().getLanguage() ).getFile() ), "index.md", "/userdoc" ) );

            m_server.getVirtualLocation().getLocations().add( new CVirtualDirectory( CConfiguration.getInstance().getLocation( "www" ), "index.htm", "/local" ) );

            m_server.getVirtualLocation().getLocations().add( new CVirtualDirectory( new File( this.getClass().getClassLoader().getResource( "web/documentation/developer" ).getFile() ), "index.htm", "/develdoc" ) );
        }
        catch ( NullPointerException l_exception )
        {
        }

        this.load( "http://localhost:" + CConfiguration.getInstance().get().getUibindport() );
    }

}
