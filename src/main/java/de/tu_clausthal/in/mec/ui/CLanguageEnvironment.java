/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.common.CCommon;

import java.util.HashMap;
import java.util.Map;


/**
 * class which is responsible for multi language support in the web ui
 */
public class CLanguageEnvironment
{

    /**
     * waypoint labels
     */
    private static final Map<String, String> m_waypointLabels = new HashMap<String, String>()
    {{
            put( "wizardwidget", CCommon.getResourceString( CLanguageEnvironment.class, "wizardwidget" ) );
            put( "factorysettings", CCommon.getResourceString( CLanguageEnvironment.class, "factorysettings" ) );
            put( "generatorsettings", CCommon.getResourceString( CLanguageEnvironment.class, "generatorsettings" ) );
            put( "carsettings", CCommon.getResourceString( CLanguageEnvironment.class, "carsettings" ) );
            put( "customizing", CCommon.getResourceString( CLanguageEnvironment.class, "customizing" ) );
            put( "selectyourfactory", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourfactory" ) );
            put( "selectyouragentprogram", CCommon.getResourceString( CLanguageEnvironment.class, "selectyouragentprogram" ) );
            put( "selectyourgenerator", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourgenerator" ) );
            put( "selectyourcarcount", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourcarcount" ) );
            put( "selectyourmean", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourmean" ) );
            put( "selectyourdeviation", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourdeviation" ) );
            put( "selectyourlowerbound", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourlowerbound" ) );
            put( "selectyourupperbound", CCommon.getResourceString( CLanguageEnvironment.class, "selectyourupperbound" ) );
            put( "selectcarspeedprob", CCommon.getResourceString( CLanguageEnvironment.class, "selectcarspeedprob" ) );
            put( "selectmaxcarspeedprob", CCommon.getResourceString( CLanguageEnvironment.class, "selectmaxcarspeedprob" ) );
            put( "selectaccprob", CCommon.getResourceString( CLanguageEnvironment.class, "selectaccprob" ) );
            put( "selectdecprob", CCommon.getResourceString( CLanguageEnvironment.class, "selectdecprob" ) );
            put( "selectlingerprob", CCommon.getResourceString( CLanguageEnvironment.class, "selectlingerprob" ) );
            put( "selecttoolname", CCommon.getResourceString( CLanguageEnvironment.class, "selecttoolname" ) );
            put( "selecttoolnamevalue", CCommon.getResourceString( CLanguageEnvironment.class, "selecttoolnamevalue" ) );
            put( "selecttoolcolor", CCommon.getResourceString( CLanguageEnvironment.class, "selecttoolcolor" ) );
            put( "previous", CCommon.getResourceString( CLanguageEnvironment.class, "previous" ) );
            put( "next", CCommon.getResourceString( CLanguageEnvironment.class, "next" ) );
            put( "finish", CCommon.getResourceString( CLanguageEnvironment.class, "finish" ) );
        }};

    /**
     * method to read waypoint specific labels and resource strings
     *
     * @return
     */
    private final Map<String, String> web_static_getwaypointlabels()
    {
        return m_waypointLabels;
    }

}
