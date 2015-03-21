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


import de.tu_clausthal.in.mec.common.CReflection;
import fi.iki.elonen.NanoHTTPD;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class CVirtualMethod implements IVirtualLocation
{
    private final MethodHandle m_method;
    private final Object m_object;
    private final String m_uri;


    public CVirtualMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uri )
    {
        m_object = p_object;
        m_method = p_method.getHandle();
        m_uri = p_uri;
    }

    @Override
    public boolean match( final String p_uri )
    {
        return m_uri.equals( p_uri );
    }

    @Override
    public Map<String, Object> get( final NanoHTTPD.IHTTPSession p_session )
    {
        return new HashMap()
        {{
                put( "uri", m_uri );
                put( "object", m_object );
            }};
    }

    @Override
    public CMarkdownRenderer getMarkDownRenderer()
    {
        return null;
    }

    @Override
    public final int hashCode()
    {
        return m_uri.hashCode();
    }


    @Override
    public final boolean equals( final Object p_object )
    {
        if ( p_object instanceof CVirtualLocation )
            return this.hashCode() == p_object.hashCode();

        return false;
    }

}
