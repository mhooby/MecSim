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

package de.tu_clausthal.in.mec.object;


import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.simulation.IReturnSteppable;
import de.tu_clausthal.in.mec.simulation.IReturnSteppableTarget;
import de.tu_clausthal.in.mec.simulation.ISteppable;
import de.tu_clausthal.in.mec.simulation.IVoidSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * layer with connected elements - elements are return-stepable and the element queue will be processed until all
 * elements are finished, it corresponds to a feed-forward structure
 *
 * @note there exists no cycle check, so detection of infinity loops must be manually
 */
public abstract class IFeedForwardLayer<T extends IFeedForwardLayer.IFinish & IReturnSteppable<?> & IReturnSteppableTarget<T> & Painter> implements Painter<COSMViewer>, Collection<T>, IViewableLayer, IVoidSteppable, ILayer
{

    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * list unprocessing data items
     */
    protected final Queue<T> m_processingdata = new ConcurrentLinkedDeque<>();
    /**
     * list of finished data
     */
    protected final Queue<T> m_finisheddata = new ConcurrentLinkedDeque<>();
    /**
     * flag for visibility
     */
    protected boolean m_visible = true;
    /**
     * flag for activity
     */
    protected boolean m_active = true;

    @Override
    public final boolean isActive()
    {
        return m_active;
    }

    @Override
    public final void setActive( final boolean p_active )
    {
        m_active = p_active;
    }

    @Override
    public int getCalculationIndex()
    {
        return 0;
    }

    @Override
    public final boolean isVisible()
    {
        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();

        return m_visible;
    }

    @Override
    public final void setVisible( final boolean p_visible )
    {
        m_visible = p_visible;

        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();
    }

    @Override
    public void step( final int p_currentstep, final ILayer p_layer )
    {
    }

    /**
     * method which is called before the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object      object
     */
    public void beforeStepObject( final int p_currentstep, final T p_object )
    {
    }

    /**
     * method which is called after the object step method is called
     *
     * @param p_currentstep current step
     * @param p_object      object
     */
    public void afterStepObject( final int p_currentstep, final T p_object )
    {
        // push data to the queue
        if ( p_object.isFinish() )
            m_finisheddata.add( p_object );
        else
            m_processingdata.add( p_object );
    }

    /**
     * method is run, before all objects run
     *
     * @param p_currentstep step number
     */
    public void beforeStepAllObject( final int p_currentstep )
    {
        m_processingdata.addAll( m_finisheddata );
        m_finisheddata.clear();
    }

    /**
     * method is run, after all objects are finished
     *
     * @param p_currentstep step number
     */
    public void afterStepAllObject( final int p_currentstep )
    {
    }


    @Override
    public final int size()
    {
        return m_processingdata.size();
    }

    @Override
    public final boolean isEmpty()
    {
        return m_processingdata.isEmpty();
    }

    @Override
    public final boolean contains( final Object p_object )
    {
        return m_processingdata.contains( p_object );
    }

    @Override
    public final Iterator<T> iterator()
    {
        return m_processingdata.iterator();
    }

    @Override
    public final Object[] toArray()
    {
        return m_processingdata.toArray();
    }

    @Override
    public final <S> S[] toArray( final S[] p_value )
    {
        return m_processingdata.toArray( p_value );
    }

    @Override
    public final boolean add( final T p_value )
    {
        final boolean l_return = m_processingdata.add( p_value );

        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();

        return l_return;
    }

    @Override
    public final boolean remove( final Object p_object )
    {
        final boolean l_result = m_processingdata.remove( p_object );

        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();

        return l_result;
    }

    @Override
    public final boolean containsAll( final Collection<?> p_collection )
    {
        for ( Object l_item : p_collection )
            if ( !m_processingdata.contains( l_item ) )
                return false;

        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();

        return true;
    }

    @Override
    public final boolean addAll( final Collection<? extends T> p_collection )
    {
        final boolean l_return = m_processingdata.addAll( p_collection );

        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();

        return l_return;
    }

    @Override
    public final boolean removeAll( final Collection<?> p_collection )
    {
        for ( Object l_item : p_collection )
        {
            if ( m_processingdata.remove( l_item ) )
                continue;

            return false;
        }

        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();

        return true;
    }

    @Override
    public final boolean retainAll( final Collection<?> p_collection )
    {
        return m_processingdata.retainAll( p_collection );
    }

    @Override
    public final void clear()
    {
        m_processingdata.clear();

        if ( CSimulation.getInstance().hasUI() )
            COSMViewer.getSimulationOSM().repaint();
    }

    @Override
    public Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public void release()
    {
        for ( ISteppable l_item : m_processingdata )
            l_item.release();
    }

    @Override
    public void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        if ( !m_visible )
            return;

        final Rectangle l_viewportBounds = p_viewer.getViewportBounds();
        p_graphic.translate( -l_viewportBounds.x, -l_viewportBounds.y );
        for ( T l_item : this )
            l_item.paint( p_graphic, p_viewer, p_width, p_height );
    }


    /**
     * interface for finishing *
     */
    public static interface IFinish
    {

        /**
         * method to detect finish
         *
         * @return finish flag
         */
        public boolean isFinish();

    }

}