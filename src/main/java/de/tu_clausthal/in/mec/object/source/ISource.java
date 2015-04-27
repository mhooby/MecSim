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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.source.factory.IFactory;
import de.tu_clausthal.in.mec.object.source.generator.IGenerator;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.IInspectorDefault;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * abstract class for sources (handles common tasks)
 */
public abstract class ISource<T, P extends IFactory<T>, N extends IGenerator> extends IInspectorDefault implements IReturnSteppable<T>, Painter<COSMViewer>, Serializable
{

    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * position of the source within the map
     */
    protected final GeoPosition m_position;
    /**
     * generator of this source
     */
    private final N m_generator;
    /**
     * factory of this source
     */
    private final P m_factory;
    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap()
    {{
            putAll( ISource.super.inspect() );
        }};


    public ISource( final GeoPosition p_position )
    {
        m_position = p_position;
        m_generator = null;
        m_factory = null;
    }


    public ISource( final GeoPosition p_position, final N p_generator, final P p_factory )
    {
        m_position = p_position;
        m_generator = p_generator;
        m_factory = p_factory;

        if ( this.hasFactoryGenerator() )
        {
            m_inspect.putAll( m_generator.inspect() );
            m_inspect.putAll( m_factory.inspect() );
        }
    }

    public boolean hasFactoryGenerator()
    {
        return ( m_generator != null ) && ( m_factory != null );
    }

    @Override
    public Collection<T> step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        final Set<T> l_return = new HashSet<>();
        if ( this.hasFactoryGenerator() )
            return l_return;

        l_return.addAll( m_factory.generate( null, m_generator.getCount( p_currentstep ) ) );

        return null;
    }

    @Override
    public final Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

    @Override
    public final void onClick( final MouseEvent p_event, final JXMapViewer p_viewer )
    {
        if ( m_position == null )
            return;

        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        final Ellipse2D l_circle = new Ellipse2D.Double(
                l_point.getX() - p_viewer.getViewportBounds().getX(), l_point.getY() - p_viewer.getViewportBounds().getY(), this.iconsize( p_viewer ),
                this.iconsize(
                        p_viewer
                )
        );

        if ( l_circle.contains( p_event.getX(), p_event.getY() ) )
            CSimulation.getInstance().getUIComponents().getInspector().set( this );
    }


    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws exception on loading the data
     * @throws ClassNotFoundException throws exception on deserialization error
     *
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
    p_stream.defaultReadObject();

    m_position = new GeoPosition( p_stream.readDouble(), p_stream.readDouble() );
    this.setImage();
    }

     **
     * write call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws the exception on loading data
     *
    private void writeObject( final ObjectOutputStream p_stream ) throws IOException
    {
    p_stream.defaultWriteObject();

    p_stream.writeDouble( m_position.getLatitude() );
    p_stream.writeDouble( m_position.getLongitude() );
    }
     */

    /**
     * returns the position
     *
     * @return geoposition of the source
     */
    public final GeoPosition getPosition()
    {
        return m_position;
    }

}
