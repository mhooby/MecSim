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


import de.tu_clausthal.in.winf.CLogger;
import de.tu_clausthal.in.winf.object.world.ILayer;
import de.tu_clausthal.in.winf.object.world.IMultiLayer;
import de.tu_clausthal.in.winf.simulation.IVoidStepable;

import java.util.concurrent.Callable;


/**
 * class to process a void-stepable item
 */
public class CVoidStepable implements Runnable, Callable<Object>
{

    /**
     * layer object *
     */
    private ILayer m_layer = null;
    /**
     * void-stepable object *
     */
    private IVoidStepable m_object = null;
    /**
     * iteration value *
     */
    private int m_iteration = 0;


    /**
     * ctor
     *
     * @param p_iteration current iteration value
     * @param p_item      void-stepable object
     * @param p_layer     layer of the object or null
     */
    public CVoidStepable( int p_iteration, IVoidStepable p_item, ILayer p_layer )
    {
        if ( p_item == null )
            throw new IllegalArgumentException( "void-stepable argument need not to be null" );

        m_object = p_item;
        m_layer = p_layer;
        m_iteration = p_iteration;
    }


    /**
     * run method to perform the action on
     * runnable and callable interface
     */
    private void perform()
    {
        try
        {
            if ( ( m_layer != null ) && ( m_layer instanceof IMultiLayer ) )
                ( (IMultiLayer) m_layer ).beforeStepObject( m_iteration, m_object );


            m_object.step( m_iteration, m_layer );


            if ( ( m_layer != null ) && ( m_layer instanceof IMultiLayer ) )
                ( (IMultiLayer) m_layer ).afterStepObject( m_iteration, m_object );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( "object [" + m_object.toString() + "] throws: " + l_exception.toString() );
        }
    }


    @Override
    public void run()
    {
        this.perform();
    }

    @Override
    public Object call() throws Exception
    {
        this.perform();
        return null;
    }
}
