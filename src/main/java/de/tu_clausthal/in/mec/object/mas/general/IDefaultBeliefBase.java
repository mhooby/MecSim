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

package de.tu_clausthal.in.mec.object.mas.general;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


/**
 * generic default beliefbase for software agents, where
 * each beliefbase can contain further inherited getBeliefbases
 */
public abstract class IDefaultBeliefBase<T> implements IBeliefBase<T>
{
    /**
     * structure for beliefbase elements (i.e. literals and inherited getBeliefbases)
     */
    protected final Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> m_elements = new HashMap<>();

    /**
     * default ctor
     */
    public IDefaultBeliefBase()
    {
        this( null, null );
    }

    /**
     * ctor - just the top-level literals are specified
     *
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase( final Set<ILiteral<T>> p_literals )
    {
        this( null, p_literals );
    }

    /**
     * ctor - top-level literals and inherited getBeliefbases are specified
     *
     * @param p_inheritedBeliefbases inherited getBeliefbases with paths
     * @param p_literals top level literals
     */
    public IDefaultBeliefBase( final Map<String, IBeliefBase<T>> p_inheritedBeliefbases, final Set<ILiteral<T>> p_literals )
    {
        // generate map-entries for getBeliefbases
        if ( p_inheritedBeliefbases != null )
            for ( final Map.Entry<String, IBeliefBase<T>> l_beliefbase : p_inheritedBeliefbases.entrySet() )
                this.add( new CPath( l_beliefbase.getKey() ), l_beliefbase.getValue() );

        // generate map-entries for literals
        if ( p_literals != null )
            for ( final ILiteral<T> l_literal : p_literals )
                this.add( l_literal );
    }

    @Override
    public boolean add( final CPath p_path, final IBeliefBaseElement p_element )
    {
        if ( p_element instanceof ILiteral )
            return this.add( p_path, (ILiteral) p_element );

        if ( p_element instanceof IBeliefBase )
            return this.add( p_path, (IBeliefBase) p_element );

        return false;
    }

    /**
     * fills up a stack with Iterator-objects on beliefbase elements
     *
     * @param p_current current beliefbase with top-level-literals to iterate over
     * @param p_stack current stack
     */
    private static <N> void collapseIterator( final IBeliefBase<N> p_current, final CPath p_currentPath, final Stack<Pair<CPath, Iterator<ILiteral<N>>>> p_stack )
    {
        // push iterator object on top-level beliefbase elements
        p_stack.push( new ImmutablePair<CPath, Iterator<ILiteral<N>>>( p_currentPath, p_current.getLiterals( CPath.EMPTY ).iterator() ) );

        // recursive call for all inherited beliefbases
        final Map<String, IBeliefBase<N>> l_inheritedBeliefbases = p_current.getBeliefbases(CPath.EMPTY);
        for ( final Map.Entry<String, IBeliefBase<N>> l_beliefbase : l_inheritedBeliefbases.entrySet() )
            collapseIterator( l_beliefbase.getValue(), p_currentPath.append( l_beliefbase.getKey() ), p_stack );
    }

    @Override
    public Set<ILiteral<T>> getLiterals( final CPath... p_path )
    {
        if ( p_path == null || p_path.length == 0 )
            return new HashSet<ILiteral<T>>(){{
                for ( final ILiteral<T> l_literal : this )
                    add( l_literal );
            }};

        // step through path array and add top-level literals
        final Set<ILiteral<T>> l_literals = new HashSet<ILiteral<T>>();
        for ( int i = 0; i < p_path.length; ++i )
        {
            final IBeliefBase<T> l_beliefBase = this.get( p_path[ i ] );
            if ( l_beliefBase == null )
                continue;

            for ( final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap : l_beliefBase.getElements( CPath.EMPTY ).values() )
            {
                // get top-level literals if existing
                final Set<IBeliefBaseElement> l_elements = l_innerMap.get( ILiteral.class );
                if ( l_elements == null )
                    continue;

                // add them to returning literal set
                for ( final IBeliefBaseElement l_literal : l_elements )
                    l_literals.add( (ILiteral) l_literal );
            }
        }

        return l_literals;
    }

    /**
     * adds a literal to beliefbase with specified path. If path is non-existing
     * it will be constructed.
     *
     * @param p_path path to beliefbase
     * @param p_literal literal to add
     * @return true, if addition was successful
     */
    private boolean add( final CPath p_path, final ILiteral<T> p_literal )
    {
        // further path can be specified in literals functor
        final CPath l_deepPath = new CPath( p_literal.getFunctor().toString() );

        // get inherited beliefbase
        final IBeliefBase<T> l_beliefbase =
                this.getOrDefault(
                        p_path.append( l_deepPath.getSubPath( 0, l_deepPath.size() - 1 ) ), new IDefaultBeliefBase<T>()
                        {
                        }
                );

        // get beliefbase elements and inner-map with specified key
        final String l_key = l_deepPath.getSuffix();
        final Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> l_elements = l_beliefbase.getElements( CPath.EMPTY );
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = l_elements.get( l_key );

        final ILiteral<T> l_literal = new CDefaultLiteral<T>(
                l_key, p_literal.getLiteral(),
                (CTermList) p_literal.getValues(), (CTermSet) p_literal.getAnnotation()
        )
        {
        };

        // if there is no inner-map with same key, generate new map-entry and put literal into it
        if ( l_innerMap == null )
            return l_elements.put(
                    l_key, new HashMap()
                    {{
                            put(
                                    ILiteral.class, new HashSet()
                                    {{
                                            add( l_literal );
                                        }}
                            );
                        }}
            ) == null;

        // get literals with same key from inner-map
        final Set<IBeliefBaseElement> l_innerLiterals = l_innerMap.get( ILiteral.class );

        // if there are no literals with same key, generate new set and put literal into it
        if ( l_innerLiterals == null )
            return l_innerMap.put(
                    ILiteral.class, new HashSet()
                    {{
                            add( l_literal );
                        }}
            ) == null;

        // if there are literals with same key, just add current literal to this set
        return l_innerLiterals.add( l_literal );

    }

    /**
     * adds a literal to top-level elements
     *
     * @param p_literal literal to add with path in functor
     * @return
     */
    private boolean add( final ILiteral<T> p_literal )
    {
        return this.add( CPath.EMPTY, p_literal );
    }

    /**
     * Adds a beliefbase to specified path. The name of the new beliefbase
     * has to be the last element in variable path.
     *
     * @param p_path path with name as last element
     * @param p_beliefbase beliefbase to add
     * @return true if addition was successful
     */
    private boolean add( final CPath p_path, final IBeliefBase<T> p_beliefbase )
    {
        // a name (i.e. last element in path) must be specified to add a new beliefbase
        if ( p_path.isEmpty() )
            throw new IllegalArgumentException( CCommon.getResourceString( IDefaultBeliefBase.class, "emptypath" ) );

        // get beliefbase or construct new ones
        final IBeliefBase<T> l_inherited = this.getOrDefault(
                p_path.getSubPath(0, p_path.size() - 1),
                new IDefaultBeliefBase<T>()
                {
                }
        );

        // get inner map of beliefbase elements
        final Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> l_elements = l_inherited.getElements( CPath.EMPTY );
        if ( !l_elements.keySet().contains( p_path.getSuffix() ) )
            l_elements.put( p_path.getSuffix(), new HashMap<Class<?>, Set<IBeliefBaseElement>>() );
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = l_elements.get( p_path.getSuffix() );

        // push beliefbase into inner map
        l_innerMap.put(
                IBeliefBase.class, new HashSet<IBeliefBaseElement>()
                {{
                        add( p_beliefbase );
                    }}
        );

        return true;
    }

    @Override
    public boolean addAll( final CPath p_path, final Collection<ILiteral<T>> p_literals )
    {
        // get inherited beliefbase, construct a new one if path is unknown
        IBeliefBase<T> l_innerBeliefBase = this.getOrDefault(
                p_path, new IDefaultBeliefBase<T>()
                {
                }
        );

        // add every literal to beliefbase
        boolean l_success = true;
        for ( final ILiteral<T> l_literal : p_literals )
            l_success = l_success && l_innerBeliefBase.add( CPath.EMPTY, l_literal );

        return l_success;
    }

    @Override
    public Map<String, IBeliefBase<T>> getBeliefbases( final CPath... p_path )
    {
        if ( p_path == null )
            return Collections.EMPTY_MAP;

        // fill the resulting map with top-level beliefbases
        final Map<String, IBeliefBase<T>> l_result = new HashMap<>();
        for ( final CPath l_path : p_path )
        {
            // get inherited beliefbase
            final IBeliefBase<T> l_beliefbase = this.get( l_path );
            if ( l_beliefbase == null )
                continue;

            // iteration over beliefbase-elements
            for ( final Map.Entry<String, Map<Class<?>, Set<IBeliefBaseElement>>> l_element : l_beliefbase.getElements( CPath.EMPTY ).entrySet() )
                if ( l_element.getValue().containsKey( IBeliefBase.class ) )
                    l_result.put( l_element.getKey(), (IBeliefBase<T>) l_element.getValue().get(IBeliefBase.class).iterator().next() );
        }

        return l_result;
    }

    public void clear()
    {
        m_elements.clear();
    }

    @Override
    public void clear( final CPath... p_path )
    {
        // if nothing is specified, everything will be cleared
        if ( p_path == null || p_path.length == 0 )
        {
            m_elements.clear();
            return;
        }

        // clear beliefbases with specified paths
        for ( final CPath l_path : p_path )
        {
            final IBeliefBase l_beliefBase = this.get( l_path );
            if ( l_beliefBase != null )
                l_beliefBase.clear();
        }
    }

    @Override
    public IBeliefBase<T> get( final CPath p_path )
    {
        if ( p_path.isEmpty() )
            return this;

        // get map of beliefbase-elements by first path-element
        final Map<Class<?>, Set<IBeliefBaseElement>> l_beliefBaseElements = m_elements.get( p_path.get( 0 ) );
        if ( l_beliefBaseElements == null )
            return null;

        // get beliefbase with name matching the specified first path-element
        final Set<IBeliefBaseElement> l_beliefbase = l_beliefBaseElements.get( IBeliefBase.class );
        if ( ( l_beliefbase == null ) || ( l_beliefbase.isEmpty() ) )
            return null;

        // recursive call in inherited beliefbase with shortened path
        return ( (IBeliefBase) l_beliefbase.iterator().next() ).get( p_path.getSubPath( 1 ) );
    }

    @Override
    public Set<IBeliefBaseElement> get( final CPath p_path, final String p_key, final Class p_class )
    {
        // go down the hierarchy and get last beliefbase
        final IBeliefBase<T> l_beliefbase = this.get(p_path);
        if ( l_beliefbase == null || !l_beliefbase.getElements( CPath.EMPTY ).containsKey( p_key ) )
            return null;

        // get beliefbase-elements of given name and class
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = l_beliefbase.getElements( CPath.EMPTY ).get( p_key );
        if ( !l_innerMap.containsKey( p_class ) )
            return null;

        return l_innerMap.get( p_class );
    }

    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements()
    {
        return m_elements;
    }

    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements( String p_path )
    {
        return this.getElements( new CPath( p_path ) );
    }

    @Override
    public Map<String, Map<Class<?>, Set<IBeliefBaseElement>>> getElements( CPath p_path )
    {
        if ( p_path.isEmpty() )
            return m_elements;

        final IBeliefBase l_beliefbase = this.get( p_path );
        return l_beliefbase == null ? Collections.EMPTY_MAP : l_beliefbase.getElements( CPath.EMPTY );
    }

    /**
     * returns top-level beliefbase elements
     *
     * @param p_name elements key (functor for literals, name for beliefbase)
     * @param p_class elements class
     * @return set of top-level beliefbase elements
     */
    public Set<IBeliefBaseElement> getElements( final String p_name, final Class<?> p_class )
    {
        return this.getElements( CPath.EMPTY, p_name, p_class );
    }

    /**
     * gets specified beliefbase if available, or default beliefbase if not available
     *
     * @param p_path path with name of the beliefbase as its last element
     * @param p_beliefbase default beliefbase, will be returned if the specified path cannot be found
     * @return specified beliefbase if path exists, otherwise the default beliefbase
     */
    public IBeliefBase<T> getOrDefault( final CPath p_path, final IBeliefBase<T> p_beliefbase )
    {
        if ( p_path.isEmpty() )
            return this;

        IDefaultBeliefBase<T> l_currentBeliefbase = this;

        for ( final CPath l_pathElement : p_path )
        {
            // get next inherited beliefbase
            final IDefaultBeliefBase<T> l_nextBeliefbase = (IDefaultBeliefBase) l_currentBeliefbase.getBeliefbase( l_pathElement.getSuffix() );

            // if no such beliefbase exists, create a new one
            if ( l_nextBeliefbase == null )
                l_currentBeliefbase.addTopElement(
                        l_pathElement.getSuffix(), IBeliefBase.class, new HashSet<IBeliefBaseElement>()
                        {{
                                add(
                                        l_pathElement.equals( p_path ) ?
                                                p_beliefbase :
                                                new IDefaultBeliefBase<T>()
                                                {
                                                }
                                );
                            }}
                );

            // go down the hierarchy
            l_currentBeliefbase = (IDefaultBeliefBase) l_currentBeliefbase.getBeliefbase( l_pathElement.getSuffix() );
        }

        return l_currentBeliefbase;
    }

    public boolean remove( final CPath p_path, final IBeliefBase p_beliefbase )
    {
        // if nothing is specified, nothing can be removed
        if ( p_path.isEmpty() )
            return false;

        // go down the hierarchy and get the last beliefbase
        final IBeliefBase<T> l_beliefbase = this.get( p_path.getSubPath( 0, p_path.size() - 1 ) );
        if ( l_beliefbase == null )
            return false;

        return l_beliefbase.getElements( CPath.EMPTY, p_path.getSuffix(), IBeliefBase.class ).remove( p_beliefbase );
    }

    public boolean remove( final CPath p_path, final ILiteral<T> p_literal )
    {
        final IBeliefBase<T> l_beliefbase = this.get( p_path );
        if ( l_beliefbase == null )
            return false;

        return l_beliefbase.getElements( CPath.EMPTY, p_literal.getFunctor().toString(), ILiteral.class ).remove( p_literal );
    }

    @Override
    public void update()
    {
        this.getBeliefbases().values().forEach( de.tu_clausthal.in.mec.object.mas.general.IBeliefBase::update );
    }

    /**
     * adds a set of beliefbase elements on top-level
     *
     * @param p_name name of the elements as key
     * @param p_class class of the elements
     * @param p_element the elements themselves
     */
    private void addTopElement( final String p_name, final Class<?> p_class, final Set<IBeliefBaseElement> p_element )
    {
        // get inner map by name, or create a new one
        final Map<Class<?>, Set<IBeliefBaseElement>> l_innerMap = m_elements.getOrDefault(
                p_name, new HashMap<Class<?>, Set<IBeliefBaseElement>>()
        );
        m_elements.put( p_name, l_innerMap );

        // get beliefbase elements
        final Set<IBeliefBaseElement> l_data = l_innerMap.getOrDefault( p_class, new HashSet<IBeliefBaseElement>() );
        if ( IBeliefBase.class.equals( p_class ) )
            l_data.clear();

        l_data.addAll( p_element );
        l_innerMap.put( p_class, l_data );
    }

    /**
     * recursive method to fill up a stack with Iterator-objects
     *
     * @return iterator stack
     */
    private Stack<Pair<CPath, Iterator<ILiteral<T>>>> collapseIterator()
    {
        final Stack<Pair<CPath, Iterator<ILiteral<T>>>> l_stack = new Stack<>();
        collapseIterator( this, CPath.EMPTY, l_stack );
        return l_stack;
    }

    @Override
    public boolean remove( final CPath p_path, final IBeliefBaseElement p_element )
    {
        if ( p_element instanceof ILiteral )
            this.remove( p_path, (ILiteral) p_element );

        if ( p_element instanceof IBeliefBase )
            this.remove( p_path, (IBeliefBase) p_element );

        return false;
    }

    /**
     * returns beliefbase with specified key (i.e. name of the beliefbase)
     *
     * @param p_name name of beliefbase
     * @return beliefbase, or null if no such beliefbase was found
     */
    private IBeliefBase<T> getBeliefbase( final String p_name )
    {
        final Set<IBeliefBaseElement> l_bb = this.getElements( CPath.EMPTY, p_name, IBeliefBase.class );
        return ( l_bb == null || l_bb.isEmpty() ) ? null : (IBeliefBase) l_bb.iterator().next();
    }

    /**
     * returns set of beliefbase elements with specified keys
     *
     * @param p_name name of beliefbase elements
     * @param p_class class of beliefbase elements
     * @return set of beliefbase elements, or null if nothing was found
     */
    @Override
    public Set<IBeliefBaseElement> getElements( final CPath p_path, final String p_name, final Class<?> p_class )
    {
        final IBeliefBase<T> l_beliefbase = this.get( p_path );
        if ( l_beliefbase == null )
            return null;

        return (Set<IBeliefBaseElement>) l_beliefbase.getElements( CPath.EMPTY )
                           .getOrDefault( p_name, Collections.EMPTY_MAP )
                           .getOrDefault( p_class, Collections.EMPTY_SET );
    }

    @Override
    public int hashCode()
    {
        return m_elements.values().hashCode();
    }

    @Override
    public boolean equals( final Object p_object )
    {
        return this.hashCode() == p_object.hashCode();
    }

    @Override
    public String toString()
    {
        return m_elements.toString();
    }

    @Override
    public Iterator<ILiteral<T>> iterator()
    {
        return new Iterator<ILiteral<T>>()
        {
            /**
             * iterator stack
             */
            private final Stack<Pair<CPath,Iterator<ILiteral<T>>>> m_stack = collapseIterator();

            @Override
            public boolean hasNext()
            {
                // if the stack is empty, we have nothing to iterate over
                if ( m_stack.isEmpty() )
                    return false;

                // return true, if the top element of the stack has a next element
                if ( m_stack.peek().getRight().hasNext() )
                    return true;

                // the top element has no next element, so it can be removed
                m_stack.pop();

                // recursive call
                return this.hasNext();
            }

            /**
             * returns next element of the top iterator
             *
             * @return successor element
             */
            @Override
            public ILiteral<T> next()
            {
                return new CDefaultLiteral<T>( m_stack.peek().getLeft(), m_stack.peek().getRight().next() );
            }
        };
    }


    /**
     * removes specified beliefbase elements from a beliefbase
     *
     * @param p_path path to beliefbase
     * @param p_key elements name as key
     * @param p_class class of elements
     * @return true, if removal was successful
     */
    public boolean remove( final CPath p_path, final String p_key, final Class p_class )
    {
        final Set<IBeliefBaseElement> p_elements = this.getElements( p_path, p_key, p_class );

        if ( p_elements.isEmpty() )
            return false;

        p_elements.clear();
        return true;
    }

    public boolean remove( final String p_path, final ILiteral<T> p_literal )
    {
        return this.remove( new CPath( p_path ), p_literal );
    }
}
