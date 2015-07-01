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
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CDiscreteMetric;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CInconsistencyLayer;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CSymmetricDifferenceMetric;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CWeightedDifferenceMetric;
import de.tu_clausthal.in.mec.object.mas.inconsistency.IMetric;
import de.tu_clausthal.in.mec.runtime.CSimulation;

import java.util.HashMap;
import java.util.Map;


/**
 * environment for inconsistency layer and metric
 */
public class CInconsistencyEnvironment
{
    /**
     * layer name
     */
    private final String m_layername;


    /**
     * ctor
     *
     * @param p_layername layer name within the simulation world
     */
    public CInconsistencyEnvironment( final String p_layername )
    {
        if (!(CSimulation.getInstance().getWorld().get( p_layername ) instanceof CInconsistencyLayer))
            throw new IllegalArgumentException( CCommon.getResourceString( this, "layerincorrect", p_layername ) );

        m_layername = p_layername;
    }

    /**
     * UI method - get the current metric
     *
     * @return metric value
     */
    private final Map<String, Object> web_static_getMetric()
    {
        return new HashMap<String, Object>()
        {{
                final IMetric<?,CPath> l_current = CSimulation.getInstance().getWorld().<CInconsistencyLayer>getTyped( m_layername ).getMetric();
                for ( final EMetric l_metric : EMetric.values() )
                    put(
                            l_metric.toString(), new HashMap()
                            {{
                                    put( "active", l_metric.equals( EMetric.isa( l_current ) ) );
                                    put( "id", l_metric.name() );
                                    put( "selector", l_current.getSelector());
                                }}
                    );
            }};
    }

    /**
     * UI method - sets the metric on the current layer
     *
     * @param p_data input data
     * @todo incomplete
     */
    private final void web_static_setMetric( final Map<String, Object> p_data )
    {

    }

    /**
     * UI method - returns URI base
     *
     * @return base name
     */
    private final String web_uribase()
    {
        return m_layername;
    }


    /**
     * enum with metric values
     */
    private enum EMetric
    {
        /**
         * discrete metric
         **/
        Discrete( CCommon.getResourceString( EMetric.class, "discrete" ) ),
        /**
         * symmetric difference metric
         */
        SymmetricDifference( CCommon.getResourceString( EMetric.class, "symmetricdifference" ) ),
        /** weighted difference metric **/
        WeightedDifference( CCommon.getResourceString( EMetric.class, "weighteddifference" ) );
        /**
         * language based name of the metric
         */
        private final String m_text;

        /**
         * ctor
         *
         * @param p_text language based metric name
         */
        private EMetric( final String p_text )
        {
            m_text = p_text;
        }

        /**
         * returns the enum type of a metric object
         *
         * @param p_metric metric
         * @return enum type
         */
        public static EMetric isa( final IMetric<?,CPath> p_metric )
        {
            if ( p_metric instanceof CDiscreteMetric )
                return Discrete;

            if ( p_metric instanceof CSymmetricDifferenceMetric )
                return SymmetricDifference;

            if (p_metric instanceof CWeightedDifferenceMetric)
                return WeightedDifference;

            throw new IllegalStateException( CCommon.getResourceString( EMetric.class, "unknownmetric" ) );
        }

        /**
         * returns a metric instance
         *
         * @return metric
         */
        public IMetric<?,CPath> get( final CPath... p_paths )
        {
            switch ( this )
            {
                case Discrete:
                    return new CDiscreteMetric<>( p_paths );

                case SymmetricDifference:
                    return new CSymmetricDifferenceMetric<>( p_paths );

                case WeightedDifference:
                    return new CWeightedDifferenceMetric<>( p_paths );

                default:
                    throw new IllegalStateException( CCommon.getResourceString( EMetric.class, "unknownmetric" ) );
            }
        }

        @Override
        public String toString()
        {
            return m_text;
        }
    }
}
