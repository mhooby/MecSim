/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>              #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.simulation.thread;


import de.tu_clausthal.in.winf.CConfiguration;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;
import de.tu_clausthal.in.winf.simulation.CSimulation;
import de.tu_clausthal.in.winf.simulation.IReturnStepable;
import de.tu_clausthal.in.winf.simulation.IStepable;
import de.tu_clausthal.in.winf.simulation.IVoidStepable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * main simulation thread
 */
public class CMainLoop implements Runnable
{
    private ExecutorService m_pool = Executors.newWorkStealingPool();

    private Collection<Callable<Object>> m_tasks = new LinkedList();

    private int m_simulationcount = 0;


    /**
     * returns a runnable object of the stepable input
     *
     * @param p_iteration iteration
     * @param p_object    stepable object
     * @param p_layer     layer
     * @return runnable object
     */
    private static Callable createTask( int p_iteration, IStepable p_object, ILayer p_layer )
    {
        if ( p_object instanceof IVoidStepable )
            return new CVoidStepable( p_iteration, (IVoidStepable) p_object, p_layer );

        if ( p_object instanceof IReturnStepable )
            return new CReturnStepable( p_iteration, (IReturnStepable) p_object, p_layer );

        throw new IllegalArgumentException( "stepable object need not be null" );
    }


    @Override
    public void run()
    {
        while ( !Thread.currentThread().isInterrupted() )
        {

            try
            {

                m_tasks.clear();
                for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
                    m_tasks.add( createTask( m_simulationcount, l_layer, null ) );
                m_pool.invokeAll( m_tasks );


                m_tasks.clear();
                for ( ILayer l_layer : CSimulation.getInstance().getWorld().values() )
                    if ( l_layer instanceof IMultiLayer )
                    {
                        for ( Object l_object : ( (IMultiLayer) l_layer ) )
                            m_tasks.add( createTask( m_simulationcount, (IStepable) l_object, l_layer ) );
                        m_pool.invokeAll( m_tasks );
                    }

                m_simulationcount++;
                Thread.sleep( CConfiguration.getInstance().get().ThreadSleepTime );
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                return;
            }
        }

    }


    public void stop()
    {
        Thread.currentThread().interrupt();
    }

}
