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

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import fi.iki.elonen.NanoHTTPD;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * definition of virtual directory locations
 */
public final class CVirtualDirectory implements IVirtualLocation
{
    /**
     * URI reg expression for filter
     */
    private static final String c_allowchar = "[^a-zA-Z0-9_/]+";
    /**
     * seperator
     */
    private static final String c_seperator = "/";
    /**
     * virtual-location-directory *
     */
    private final URL m_directory;
    /**
     * index file *
     */
    private final String m_index;
    /**
     * markdown renderer
     */
    private final CMarkdownRenderer m_markdown;
    /**
     * URI pattern *
     */
    private final String m_uri;

    /**
     * ctor
     *
     * @param p_directory base directory of the physical directory
     * @param p_index index file
     */
    public CVirtualDirectory( final URL p_directory, final String p_index )
    {
        if ( ( p_index == null ) || ( p_index.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "indexempty" ) );
        if ( p_directory == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "directorynotexists", p_directory ) );

        m_uri = c_seperator;
        m_directory = p_directory;
        m_index = p_index.startsWith( c_seperator ) ? p_index : c_seperator + p_index;
        m_markdown = null;
    }

    /**
     * ctor
     *
     * @param p_directory base directory of the physical directory
     * @param p_index index file
     * @param p_uri regular expression of the URI
     */
    public CVirtualDirectory( final URL p_directory, final String p_index, final String p_uri )
    {
        if ( ( p_index == null ) || ( p_index.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "indexempty" ) );
        if ( p_directory == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "directorynotexists", p_directory ) );
        if ( ( p_uri == null ) || ( p_uri.isEmpty() ) || ( !p_uri.endsWith( c_seperator ) ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "trailingslashempty", p_uri ) );

        m_directory = p_directory;
        m_index = p_index.startsWith( c_seperator ) ? p_index : c_seperator + p_index;
        m_uri = p_uri.startsWith( c_seperator ) ? p_uri.replaceAll( c_allowchar, "" ) : c_seperator + p_uri.replaceAll( c_allowchar, "" );
        m_markdown = null;

        CLogger.info( p_uri );
    }

    /**
     * ctor
     *
     * @param p_directory base directory of the physical directory
     * @param p_index index file
     * @param p_uri regular expression of the URI
     * @param p_markdown markdown renderer
     */
    public CVirtualDirectory( final URL p_directory, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        if ( ( p_index == null ) || ( p_index.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "indexempty" ) );
        if ( p_directory == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "directorynotexists", p_uri ) );
        if ( ( p_uri == null ) || ( p_uri.isEmpty() ) || ( !p_uri.endsWith( c_seperator ) ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "trailingslashempty", p_uri ) );

        m_directory = p_directory;
        m_index = p_index.startsWith( c_seperator ) ? p_index : c_seperator + p_index;
        m_uri = p_uri.startsWith( c_seperator ) ? p_uri.replaceAll( c_allowchar, "" ) : c_seperator + p_uri.replaceAll( c_allowchar, "" );
        m_markdown = p_markdown;
    }

    @Override
    public final URL get( final NanoHTTPD.IHTTPSession p_session ) throws MalformedURLException, URISyntaxException
    {
        // URL concatination must be performtemplate with string manually, because otherwise last URL element can be removed
        if ( p_session.getUri().equals( m_uri ) )
            return CCommon.concatURL( m_directory, m_index );

        // special call on root-location (/) to avoid path error
        return CCommon.concatURL( m_directory, p_session.getUri().replaceFirst( m_uri, c_seperator ) );
    }

    @Override
    public final CMarkdownRenderer getMarkDownRenderer()
    {
        return m_markdown;
    }

    @Override
    public final boolean match( final String p_uri )
    {
        return p_uri.startsWith( m_uri );
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
