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

package de.tu_clausthal.in.mec.runtime.thread;

import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.IEvaluateLayer;
import de.tu_clausthal.in.mec.object.IFeedForwardLayer;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.ISingleLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppable;
import de.tu_clausthal.in.mec.runtime.ISteppable;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * main simulation thread
 *
 * @bug breaks down on some longer runs (debugging)
 */
public class CMainLoop implements Runnable
{
    /**
     * thread-pool for handling all objects
     */
    private ExecutorService m_pool = Executors.newWorkStealingPool();

    /**
     * simulation counter
     */
    private int m_simulationcount;

    /**
     * boolean to pause/resume the thread
     */
    private boolean m_pause = true;

    /**
     * number of threads for running *
     */
    private int m_shutdownstep = Integer.MAX_VALUE;


    /**
     * returns a runnable object of the steppable input
     *
     * @param p_iteration iteration
     * @param p_object    steppable object
     * @param p_layer     layer
     * @return runnable object
     */
    private static Callable<Object> createTask( final int p_iteration, final ISteppable p_object, final ILayer p_layer )
    {
        if ( p_object instanceof IVoidSteppable )
            return new CVoidSteppable( p_iteration, (IVoidSteppable) p_object, p_layer );

        if ( p_object instanceof IReturnSteppable )
            return new CReturnSteppable( p_iteration, (IReturnSteppable) p_object, p_layer );

        throw new IllegalArgumentException( CCommon.getResourceString( CMainLoop.class, "notsteppable" ) );
    }


    /**
     * invokes all defined data
     *
     * @param p_layer      layer
     * @param p_tasksource invoking tasks
     * @throws InterruptedException is throws on task error
     */
    protected final void invokeTasks( final ILayer p_layer, final Collection<ISteppable> p_tasksource ) throws InterruptedException
    {
        final Collection<Callable<Object>> l_tasklist = new LinkedList<>();
        for ( ISteppable l_object : p_tasksource )
            l_tasklist.add( createTask( m_simulationcount, l_object, p_layer ) );
        m_pool.invokeAll( l_tasklist );
    }


    /**
     * @todo check speed of thread-sleep-time with caching
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public final void run()
    {
        CLogger.info( CCommon.getResourceString( this, "start" ) );

        // order of all layer - the order will be read only once
        // so the thread need not be startup on program initializing
        final List<ILayer> l_layerorder = CSimulation.getInstance().getWorld().getOrderedLayer();
        CLogger.info( l_layerorder );

        while ( !Thread.currentThread().isInterrupted() )
        {

            try
            {
                // if thread is paused
                if ( m_pause )
                {
                    Thread.yield();
                    continue;
                }

                // shutdown
                if ( m_simulationcount >= m_shutdownstep )
                    break;


                // run all layer
                final Collection<Callable<Object>> l_tasks = new LinkedList<>();
                l_tasks.add( new CVoidSteppable( m_simulationcount, CSimulation.getInstance().getMessageSystem(), null ) );
                for ( ILayer l_layer : l_layerorder )
                    if ( l_layer.isActive() )
                        l_tasks.add( createTask( m_simulationcount, l_layer, null ) );
                m_pool.invokeAll( l_tasks );


                // run all layer objects - only multi-, evaluate- & network layer can store other objects
                for ( ILayer l_layer : l_layerorder )
                {
                    if ( ( !l_layer.isActive() ) || ( l_layer instanceof ISingleLayer ) )
                        continue;

                    if ( l_layer instanceof IMultiLayer )
                    {
                        this.invokeTasks( l_layer, (IMultiLayer) l_layer );
                        continue;
                    }

                    if ( l_layer instanceof IEvaluateLayer )
                    {
                        this.invokeTasks( l_layer, (IEvaluateLayer) l_layer );
                        continue;
                    }

                    if ( l_layer instanceof IFeedForwardLayer )
                    {
                        ( (IFeedForwardLayer) l_layer ).beforeStepAllObject( m_shutdownstep );
                        while ( !( (IFeedForwardLayer) l_layer ).isEmpty() )
                            this.invokeTasks( l_layer, (IFeedForwardLayer) l_layer );
                        ( (IFeedForwardLayer) l_layer ).afterStepAllObject( m_shutdownstep );
                        continue;
                    }
                }


                m_simulationcount++;
                Thread.sleep( CConfiguration.getInstance().get().<Integer>getTraverse( "simulation/threadsleeptime" ) );
            }
            catch ( final InterruptedException l_exception )
            {
                Thread.currentThread().interrupt();
                return;
            }
        }

        m_pause = true;
        CLogger.info( CCommon.getResourceString( this, "stop" ) );
    }


    /**
     * thread is shut down
     */
    public final void stop()
    {
        Thread.currentThread().interrupt();
    }

    /**
     * checks if the thread is paused
     *
     * @return boolean for pause
     */
    public final boolean isPaused()
    {
        return m_pause;
    }

    /**
     * sets pause state
     */
    public final void pause()
    {
        m_pause = true;
    }

    /**
     * resume state
     */
    public final void resume()
    {
        m_pause = false;
    }

    /**
     * resumes thread and shut down thread after
     *
     * @param p_steps number of steps which are run
     */
    public final void resume( final int p_steps )
    {
        if ( p_steps < 1 )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "stepnumber" ) );

        m_shutdownstep = m_simulationcount + p_steps;
        m_pause = false;
    }

    /**
     * resets the thread
     */
    public final void reset()
    {
        if ( !m_pause )
            throw new IllegalStateException( CCommon.getResourceString( this, "pause" ) );

        CLogger.info( CCommon.getResourceString( this, "reset" ) );

        try
        {
            m_simulationcount = 0;
            final Collection<Callable<Object>> l_tasks = new LinkedList<>();
            for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
                l_tasks.add( new CLayerReset( l_layer ) );
            m_pool.invokeAll( l_tasks );
        }
        catch ( final InterruptedException l_exception )
        {
        }

    }

}