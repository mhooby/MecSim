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

package de.tu_clausthal.in.mec.ui.web;


import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.runtime.CSimulation;


/**
 * main workspace of the web-menu structure (browser & server structure)
 */
public final class CWorkspace extends CBrowser
{
    /**
     * path of the binding host
     */
    private static final String c_httphost = "ui/server/host";
    /**
     * path of the binding port
     */
    private static final String c_httpport = "ui/server/port";
    /**
     * HTTP server to handle websockets *
     */
    private final CServer m_server = new CServer(
            CConfiguration.getInstance().get().<String>get( c_httphost ), CConfiguration.getInstance().get().<Integer>get( c_httpport ), new CVirtualDirectory(
            CCommon.getResourceURL( "web/root" ), "index.htm"
    ), CConfiguration.getInstance().get().<Integer>get(
            "ui/server/websocketheartbeat"
    )
    );

    /**
     * ctor - adds the browser *
     */
    public CWorkspace()
    {
        super( EMenu.BackForward );

        this.load(
                "http://" + CConfiguration.getInstance().get().<String>get( c_httphost ) + ":" + CConfiguration.getInstance().get().<Integer>get( c_httpport )
        );

        CSimulation.getInstance().getStorage().add( "server", m_server );
    }

}
