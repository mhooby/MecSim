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

package de.tu_clausthal.in.mec.object.car;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.graph.CEdge;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.CMethodFilter;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.benchmark.IBenchmark;
import de.tu_clausthal.in.mec.ui.CInspector;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import de.tu_clausthal.in.mec.ui.CUI;
import de.tu_clausthal.in.mec.ui.IInspectorDefault;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * class for a default car *
 *
 * @bug refactor ctor (reduce parameter)
 */
public class CDefaultCar extends IInspectorDefault implements ICar
{

    /**
     * reference to the graph
     */
    @CFieldFilter.CAgent( bind = false )
    protected final CCarLayer m_layer = CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" );
    /**
     * maximum speed definition in km/h
     */
    protected final int m_maxspeed;
    /**
     * current speed in km/h (use boxed-type because MAS can modify value)
     */
    protected Integer m_speed;
    /**
     * cell structure of the route
     */
    @CFieldFilter.CAgent( bind = false )
    protected final ArrayList<Pair<EdgeIteratorState, Integer>> m_route;
    /**
     * current position on the route
     */
    @CFieldFilter.CAgent( bind = false )
    protected int m_routeindex;
    /**
     * individual acceleration in m/sec^2
     */
    private final int m_acceleration;
    /**
     * individual deceleration in m/sec^2
     */
    private final int m_deceleration;
    /**
     * boolean flag for end reached
     */
    @CFieldFilter.CAgent( bind = false )
    private boolean m_endreached;
    /**
     * inspector map
     */
    @CFieldFilter.CAgent( bind = false )
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
        putAll( CDefaultCar.super.inspect() );
    }};
    /**
     * linger probability value
     */
    private double m_lingerprobability;

    /**
     * ctor to create the initial values
     *
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @see https://en.wikipedia.org/wiki/Orders_of_magnitude_(acceleration)
     */
    public CDefaultCar( final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final int p_speed, final int p_maxspeed, final int p_acceleration,
            final int p_deceleration, final double p_lingerprobability
    ) throws IllegalArgumentException
    {
        m_route = p_route;
        m_speed = p_speed;
        m_maxspeed = p_maxspeed;
        m_acceleration = p_acceleration;
        m_deceleration = p_deceleration;
        m_lingerprobability = p_lingerprobability;


        if ( ( m_route == null ) || ( m_route.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( CDefaultCar.class, "routeempty" ) );
        if ( m_speed < 1 )
            throw new IllegalArgumentException( CCommon.getResourceString( CDefaultCar.class, "speedtolow" ) );
        if ( m_maxspeed > 350 )
            throw new IllegalArgumentException( CCommon.getResourceString( CDefaultCar.class, "maxspeedtohigh" ) );
        if ( ( m_acceleration < 1 ) || ( m_acceleration > 20 ) )
            throw new IllegalArgumentException( CCommon.getResourceString( CDefaultCar.class, "accelerationincorrect" ) );
        if ( ( m_deceleration < 1 ) || ( m_deceleration > 20 ) )
            throw new IllegalArgumentException( CCommon.getResourceString( CDefaultCar.class, "decelerationincorrect" ) );
        if ( ( m_lingerprobability < 0 ) || ( m_lingerprobability > 1 ) )
            throw new IllegalArgumentException( CCommon.getResourceString( CDefaultCar.class, "lingerprobabilityincorrect" ) );

        m_inspect.put( CCommon.getResourceString( CDefaultCar.class, "maximumspeed" ), m_maxspeed );
        m_inspect.put( CCommon.getResourceString( CDefaultCar.class, "acceleration" ), m_acceleration );
        m_inspect.put( CCommon.getResourceString( CDefaultCar.class, "deceleration" ), m_deceleration );
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final int getAcceleration()
    {
        return m_acceleration;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final int getCurrentSpeed()
    {
        return m_speed;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final void setCurrentSpeed( final int p_speed )
    {
        m_speed = p_speed;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final int getDeceleration()
    {
        return m_deceleration;
    }

    @Override
    public final EdgeIteratorState getEdge()
    {
        return this.getEdge( m_routeindex );
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final GeoPosition getGeoposition()
    {
        if ( m_routeindex >= m_route.size() )
            return null;
        final Pair<EdgeIteratorState, Integer> l_cell = m_route.get( m_routeindex );
        return m_layer.getGraph().getEdge( l_cell.getLeft() ).getGeoPositions( l_cell.getRight() );
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final double getLingerProbability()
    {
        return m_lingerprobability;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final int getMaximumSpeed()
    {
        return m_maxspeed;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final Map<Double, ICar> getPredecessor()
    {
        return this.getPredecessor( 1 );
    }

    /**
     * @bug run with parallel stream and collect
     */
    @Override
    @CMethodFilter.CAgent( bind = false )
    public final Map<Double, ICar> getPredecessor( final int p_count )
    {
        final Map<Double, ICar> l_predecessordistance = new HashMap<>();

        // we get the nearest predecessor within the speed range (performance boost)
        for ( int i = m_routeindex + 1; ( i <= m_routeindex + m_layer.getUnitConvert().getSpeedToCell( m_speed ) ) && ( i < m_route.size() ) &&
                                        ( l_predecessordistance.size() < p_count ); i++
                )
        {
            final ICar l_object = m_layer.getGraph().getEdge( m_route.get( i ).getLeft() ).getObject( m_route.get( i ).getRight() );
            if ( l_object != null )
                l_predecessordistance.put( m_layer.getUnitConvert().getCellToMeter( i - m_routeindex ), l_object );
        }

        return l_predecessordistance;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final boolean hasEndReached()
    {
        return m_endreached;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public Map<String, Object> inspect()
    {
        m_inspect.put( CCommon.getResourceString( CDefaultCar.class, "currentspeed" ), m_speed );
        m_inspect.put( CCommon.getResourceString( CDefaultCar.class, "streetname" ), m_route.get( m_routeindex ).getLeft().getName() );
        m_inspect.put( CCommon.getResourceString( CDefaultCar.class, "currentgeoposition" ), this.getGeoposition() );
        return m_inspect;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final void onClick( final MouseEvent p_event, final JXMapViewer p_viewer )
    {
        final GeoPosition l_position = this.getGeoposition();
        if ( l_position == null )
            return;

        final int l_zoom = this.iconsize( p_viewer );
        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( l_position, p_viewer.getZoom() );
        final Ellipse2D l_circle = new Ellipse2D.Double(
                l_point.getX() - p_viewer.getViewportBounds().getX() - l_zoom,
                l_point.getY() - p_viewer.getViewportBounds().getY() - l_zoom,
                l_zoom * 2, l_zoom * 2
        );

        if ( l_circle.contains( p_event.getX(), p_event.getY() ) )
        {
            CSimulation.getInstance().getStorage().<CInspector>get( "inspector" ).set( this );

            final Stroke l_stroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{1}, 0 );
            final List<Triple<Pair<GeoPosition, GeoPosition>, Color, Stroke>> l_route = new LinkedList<>();
            l_route.addAll( this.getRouteLine( 0, m_routeindex, Color.GREEN, l_stroke ) );
            l_route.addAll( this.getRouteLine( m_routeindex, m_route.size(), Color.CYAN, l_stroke ) );
            CSimulation.getInstance().getStorage().<CUI>get( "ui" ).<CSwingWrapper<COSMViewer>>get( "OSM" ).getComponent().paintFadeLine( l_route );
        }
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public void release()
    {
        super.release();
        final CEdge l_edge = m_layer.getGraph().getEdge( this.getEdge() );
        if ( l_edge != null )
            l_edge.removeObject( this );
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        final GeoPosition l_position = this.getGeoposition();
        if ( l_position == null )
            return;

        final int l_zoom = this.iconsize( p_viewer );
        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( l_position, p_viewer.getZoom() );

        // speed limit color defined with http://wiki.openstreetmap.org/wiki/File:Speed_limit_Germany.png
        p_graphic.setColor( Color.DARK_GRAY );
        if ( m_speed >= 50 )
            p_graphic.setColor( Color.MAGENTA );
        if ( m_speed >= 60 )
            p_graphic.setColor( Color.PINK );
        if ( m_speed >= 80 )
            p_graphic.setColor( Color.BLUE );
        if ( m_speed >= 100 )
            p_graphic.setColor( Color.CYAN );
        if ( m_speed >= 130 )
            p_graphic.setColor( Color.RED );

        p_graphic.fillOval( (int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom );
    }

    @Override
    @IBenchmark
    @CMethodFilter.CAgent( bind = false )
    public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {

        // if the car is at the end
        if ( this.hasEndReached() )
            return;

        // if the car reaches the end
        int l_speed = m_layer.getUnitConvert().getSpeedToCell( this.getCurrentSpeed() );
        if ( m_routeindex + l_speed >= m_route.size() )
        {
            m_endreached = true;
            l_speed = m_route.size() - m_routeindex - 1;
        }

        // if the route index equal to zero, push it car on the first item or wait until it is free
        if ( m_routeindex == 0 )
        {

            if ( !m_layer.getGraph().getEdge( m_route.get( l_speed ).getLeft() ).isEmpty( m_route.get( l_speed ).getRight().intValue() ) )
                return;

            try
            {
                m_layer.getGraph().getEdge( m_route.get( l_speed ).getLeft() ).setObject( this, m_route.get( l_speed ).getRight().intValue() );
                m_routeindex += l_speed;
            }
            catch ( final IllegalAccessException l_exception )
            {
            }

        }
        else
        {

            try
            {
                m_layer.getGraph().getEdge( m_route.get( m_routeindex ).getLeft() ).removeObject( this );
                m_layer.getGraph().getEdge( m_route.get( m_routeindex + l_speed ).getLeft() ).setObject(
                        this, m_route.get( m_routeindex + l_speed ).getRight()
                );
                m_routeindex += l_speed;
            }
            catch ( final IllegalAccessException l_exception )
            {
                m_layer.getGraph().getEdge( m_route.get( m_routeindex ).getLeft() ).removeObject( this );
            }

        }

    }

    /**
     * returns a triple of the current edge id, cell position and geoposition
     *
     * @return tripel (edge information, cell position, geoposition)
     */
    @CMethodFilter.CAgent( bind = false )
    protected final Triple<EdgeIteratorState, Integer, GeoPosition> getCurrentPosition()
    {
        if ( ( m_route == null ) || ( m_routeindex >= m_route.size() ) )
            return new ImmutableTriple<>( null, null, this.getGeoposition() );

        return new ImmutableTriple<>(
                m_route.get( m_routeindex ).getLeft(),
                m_route.get( m_routeindex ).getRight(),
                this.getGeoposition()
        );
    }

    /**
     * returns the number of cars on the current edge
     *
     * @return number of cars
     */
    private Integer getCurrentCarsOnEdge()
    {
        return m_layer.getGraph().getEdge( this.getEdge() ).getNumberOfObjects();
    }

    /**
     * returns the maximum allowed speed at the current edge
     *
     * @return speed value
     */
    private Double getCurrentEdgeMaxSpeed()
    {
        return m_layer.getGraph().getEdgeSpeed( this.getEdge() );
    }

    /**
     * returns the edge from an index
     *
     * @param p_index index
     * @return null or edge
     */
    @CMethodFilter.CAgent( bind = false )
    private final EdgeIteratorState getEdge( final int p_index )
    {
        if ( m_route == null )
            return null;

        return p_index < m_route.size() ? m_route.get( p_index ).getLeft() : null;
    }

    /**
     * creates a list of items for route painting
     *
     * @param p_start start index of the route list
     * @param p_end end index of the route list
     * @param p_color color of the items
     * @param p_stroke stroke of the items
     * @return list with route
     */
    @CMethodFilter.CAgent( bind = false )
    private List<Triple<Pair<GeoPosition, GeoPosition>, Color, Stroke>> getRouteLine( final int p_start, final int p_end, final Color p_color,
            final Stroke p_stroke
    )
    {
        final List<Triple<Pair<GeoPosition, GeoPosition>, Color, Stroke>> l_list = new LinkedList<>();

        GeoPosition l_start = m_layer.getGraph().getEdge( m_route.get( p_start ).getLeft() ).getGeoPositions( m_route.get( p_start ).getRight().intValue() );
        GeoPosition l_end;
        for ( int i = p_start + 1; i < p_end; ++i )
        {
            l_end = m_layer.getGraph().getEdge( m_route.get( i ).getLeft() ).getGeoPositions( m_route.get( i ).getRight().intValue() );
            if ( l_start.equals( l_end ) )
                continue;

            l_list.add( new ImmutableTriple<>( new ImmutablePair<>( l_start, l_end ), p_color, p_stroke ) );
            l_start = l_end;
        }

        return l_list;
    }

    /**
     * reroute to a new target position
     *
     * @param p_position new end position
     * @param p_weight weight for routing
     */
    private void reroute( final GeoPosition p_position, final CGraphHopper.EWeight p_weight )
    {
        if ( m_routeindex >= m_route.size() - 1 )
            return;

        final List<List<EdgeIteratorState>> l_route = m_layer.getGraph().getRoutes( this.getGeoposition(), p_position, p_weight, 1 );
        if ( l_route.size() == 0 )
            return;

        m_route.subList( m_routeindex + 1, m_route.size() );
        m_route.addAll( m_layer.getGraph().getRouteCells( l_route.get( 0 ) ) );
    }

    /**
     * reroute to a new target position
     *
     * @param p_position new end position
     */
    private void reroute( final GeoPosition p_position )
    {
        this.reroute( p_position, CGraphHopper.EWeight.Default );
    }
}
