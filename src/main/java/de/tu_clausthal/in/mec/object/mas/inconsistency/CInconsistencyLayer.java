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

package de.tu_clausthal.in.mec.object.mas.inconsistency;

import cern.colt.function.DoubleFunction;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Mult;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.ISingleEvaluateLayer;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.runtime.benchmark.IBenchmark;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * layer with inconsistence data
 *
 * @see https://dst.lbl.gov/ACSSoftware/colt/
 */
public final class CInconsistencyLayer<T extends IAgent> extends ISingleEvaluateLayer
{
    /**
     * algebra object
     */
    private static final Algebra c_algebra = new Algebra();
    /**
     * algorithm to calculate stationary probability
     **/
    private final EAlgorithm m_algorithm;
    /**
     * map with object and inconsistency value
     **/
    private final Map<T, Double> m_data = new ConcurrentHashMap<>();
    /**
     * epsilon value to create an aperiodic markow-chain
     **/
    private final double m_epsilon;
    /**
     * number of iterations of the stochastic algorithm
     **/
    private final int m_iteration;
    /**
     * metric object to create the value of two objects
     **/
    private IMetric<T, CPath> m_metric;
    /**
     * name of the layer
     */
    private final String m_name;
    /**
     * update of the metric values
     */
    private final int m_updatestep;

    /**
     * ctor - use numeric algorithm
     *
     * @param p_name language-based name of the layer
     * @param p_metric object metric
     */
    public CInconsistencyLayer( final String p_name, final IMetric<T, CPath> p_metric )
    {
        m_name = p_name;
        m_metric = p_metric;
        m_algorithm = EAlgorithm.Numeric;
        m_iteration = 0;
        m_epsilon = 0.001;
        m_updatestep = 1;
    }

    /**
     * ctor - use stochastic algorithm
     *
     * @param p_name language-based name of the layer
     * @param p_metric object metric
     * @param p_iteration iterations
     * @param p_epsilon epsilon value
     */
    public CInconsistencyLayer( final String p_name, final IMetric<T, CPath> p_metric, final int p_iteration,
            final double p_epsilon
    )
    {
        m_name = p_name;
        m_metric = p_metric;
        m_algorithm = EAlgorithm.Iteration;
        m_iteration = p_iteration;
        m_epsilon = p_epsilon;
        m_updatestep = 1;
    }

    public Double getInconsistencyValue( final T p_object )
    {
        return m_data.get( p_object );
    }

    /**
     * adds a new object to the
     * inconsistency structure
     *
     * @param p_object new object
     */
    public boolean add( final T p_object )
    {
        if ( ( p_object == null ) || ( m_data.containsKey( p_object ) ) )
            return false;

        m_data.put( p_object, new Double( 0 ) );
        return true;
    }

    @Override
    public int getCalculationIndex()
    {
        return 500;
    }

    /**
     * @bug run it parallel stream
     */
    @Override
    @IBenchmark
    public final void step( final int p_currentstep, final ILayer p_layer )
    {
        if ( ( m_data.size() < 2 ) || ( p_currentstep % m_updatestep != 0 ) )
            return;

        // get key list of map for addressing elements in the correct order
        final ArrayList<T> l_keys = new ArrayList<T>( m_data.keySet() );

        // calculate markov chain transition matrix
        final DoubleMatrix2D l_matrix = new DenseDoubleMatrix2D( m_data.size(), m_data.size() );
        for ( int i = 0; i < l_keys.size(); ++i )
        {
            final T l_item = l_keys.get( i );
            for ( int j = i + 1; j < l_keys.size(); ++j )
            {
                final double l_value = this.getMetricValue( l_item, l_keys.get( j ) );
                l_matrix.setQuick( i, j, l_value );
                l_matrix.setQuick( j, i, l_value );
            }

            // row-wise normalization for getting probabilities
            final double l_norm = c_algebra.norm1( l_matrix.viewRow( i ) );
            if ( l_norm != 0 )
                l_matrix.viewRow( i ).assign( Mult.div( l_norm ) );

            // set epsilon slope for preventing periodic markov chains
            l_matrix.setQuick( i, i, m_epsilon );
        }

        final DoubleMatrix1D l_eigenvector;
        if ( l_matrix.zSum() <= m_data.size() * m_epsilon )
            l_eigenvector = new DenseDoubleMatrix1D( m_data.size() );
        else
            l_eigenvector = this.getStationaryDistribution( l_matrix );

        // set inconsistency value for each entry
        for ( int i = 0; i < l_keys.size(); ++i )
            m_data.put( l_keys.get( i ), l_eigenvector.get( i ) );
    }

    /**
     * gets the current metric
     *
     * @return get metric
     */
    public final IMetric<T, CPath> getMetric()
    {
        return m_metric;
    }

    /**
     * sets the metric
     *
     * @param p_metric metric
     */
    public final void setMetric( final IMetric<T, CPath> p_metric )
    {
        m_metric = p_metric;
    }

    /**
     * removes an object from the
     * inconsistency structure
     *
     * @param p_object removing object
     */
    public boolean remove( final T p_object )
    {
        m_data.remove( p_object );
        return true;
    }

    @Override
    public final String toString()
    {
        return m_name;
    }

    /**
     * get the largest eigen vector with QR decomposition
     *
     * @param p_matrix matrix
     * @return largest eigenvector (not normalized)
     */
    private static DoubleMatrix1D getLargestEigenvector( final DoubleMatrix2D p_matrix )
    {
        final EigenvalueDecomposition l_eigen = new EigenvalueDecomposition( p_matrix );

        // gets the position of the largest eigenvalue
        final DoubleMatrix1D l_eigenvalues = l_eigen.getRealEigenvalues();

        int l_position = 0;
        for ( int i = 0; i < l_eigenvalues.size(); ++i )
            if ( l_eigenvalues.get( i ) > l_eigenvalues.get( l_position ) )
                l_position = i;

        // gets the largest eigenvector
        return l_eigen.getV().viewColumn( l_position );
    }

    /**
     * get the largest eigen vector based on the perron-frobenius theorem
     *
     * @param p_matrix matrix
     * @param p_iteration number of iterations
     * @return largest eigenvector (not normalized)
     *
     * @see http://en.wikipedia.org/wiki/Perron%E2%80%93Frobenius_theorem
     */
    private static DoubleMatrix1D getLargestEigenvector( final DoubleMatrix2D p_matrix, final int p_iteration )
    {
        DoubleMatrix1D l_probability = DoubleFactory1D.dense.random( p_matrix.rows() );
        for ( int i = 0; i < p_iteration; ++i )
        {
            l_probability = c_algebra.mult( p_matrix, l_probability );
            l_probability.assign(
                    Mult.div(
                            c_algebra.norm2( l_probability )
                    )
            );
        }

        return l_probability;
    }

    /**
     * returns metric value
     *
     * @param p_first
     * @param p_second
     * @return
     */
    private double getMetricValue( final T p_first, final T p_second )
    {
        if ( p_first.equals( p_second ) )
            return 0;

        return m_metric.calculate( p_first, p_second );
    }

    /**
     * calculates the stationary distribution
     *
     * @param p_matrix transition matrix
     * @return stationary distribution
     */
    @IBenchmark
    private DoubleMatrix1D getStationaryDistribution( final DoubleMatrix2D p_matrix )
    {
        final DoubleMatrix1D l_eigenvector;

        switch ( m_algorithm )
        {
            case Iteration:
                l_eigenvector = getLargestEigenvector( p_matrix, m_iteration );
                break;

            case Numeric:
                l_eigenvector = getLargestEigenvector( p_matrix );
                break;

            default:
                throw new IllegalStateException( CCommon.getResourceString( CInconsistencyLayer.class, "algorithm" ) );
        }

        // normalize eigenvector and create positiv oriantation
        l_eigenvector.assign( Mult.div( c_algebra.norm1( l_eigenvector ) ) );
        l_eigenvector.assign(
                new DoubleFunction()
                {
                    @Override
                    public double apply( final double p_value )
                    {
                        return Math.abs( p_value );
                    }
                }
        );

        return l_eigenvector;
    }

    /**
     * numeric algorithm structure
     */
    private static enum EAlgorithm
    {
        /**
         * use numeric algorithm (QR decomposition)
         **/
        Numeric,
        /**
         * use stochastic algorithm (fixpoint iteration)
         **/
        Iteration
    }

}
