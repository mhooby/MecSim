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

package de.tu_clausthal.in.mec.object.mas.jason.action;


import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.IMethodBind;
import de.tu_clausthal.in.mec.object.mas.jason.CCommon;
import jason.NoValueException;
import jason.asSemantics.Agent;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * action to invoke any method on an object
 *
 * @warning methods does not use any primitive datatypes - primitive datatypes must be used with its boxed-type
 */
public final class CMethodBind extends IMethodBind<Agent, Structure>
{
    /**
     * name of the source literal on returning argument
     */
    private static final String c_returnannotation = "source";


    /**
     * ctor - default
     */
    public CMethodBind()
    {
    }

    /**
     * ctor bind an object
     *
     * @param p_name name of the binding object
     * @param p_object object
     */
    public CMethodBind( final String p_name, final Object p_object )
    {
        this.push( p_name, p_object );
    }


    @Override
    public final void act( final Agent p_agent, final Structure p_args )
    {
        // check number of argument first
        final List<Term> l_args = p_args.getTerms();
        if ( l_args.size() < 2 )
            throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "argument" ) );

        // first & second argument must changed to a string (cast calls are not needed, we use the object string call)
        final String l_objectname = de.tu_clausthal.in.mec.object.mas.jason.CCommon.clearString( l_args.get( 0 ).toString() );
        final CReflection.CMethodCache<Object> l_object = m_bind.get( l_objectname );
        if ( l_object == null )
            throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "object", l_objectname ) );

        try
        {
            // build argument data & types and invoke the method
            final List<Class<?>> l_argumenttypes = new LinkedList<>();
            final List<Object> l_argumentdata = new LinkedList<>();
            final Pair<Class<?>, String> l_returntype = this.decodeMethodSignature( l_args, l_argumenttypes, l_argumentdata );
            final Object l_return = this.invokeMethod(
                    l_object, de.tu_clausthal.in.mec.object.mas.jason.CCommon.clearString( l_args.get( 1 ).toString() ), l_returntype.getKey(), l_argumenttypes,
                    l_argumentdata
            );

            // push return data into the agent (empty belief is allowed) - source of the belief is set explicit
            final Literal l_belief = de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral( l_returntype.getValue(), l_return );
            l_belief.addAnnot( de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral( c_returnannotation, l_objectname ) );

            if ( ( l_return == null ) || ( !void.class.equals( l_return.getClass() ) ) )
                p_agent.addBel( l_belief );
        }
        catch ( final Exception l_exception )
        {
            throw new IllegalArgumentException( l_exception.getMessage() );
        }
        catch ( final Throwable l_throwable )
        {
            throw new IllegalArgumentException( l_throwable.getMessage() );
        }
    }


    /**
     * converts a list of terms int an array of class objects
     *
     * @param p_list term list
     * @return class array
     */
    private Class<?>[] convertTermListToClassArray( final ListTerm p_list ) throws ClassNotFoundException, IllegalAccessException
    {
        final Class<?>[] l_classes = new Class<?>[p_list.size()];
        for ( int i = 0; i < l_classes.length; i++ )
            l_classes[i] = this.convertTermToClass( p_list.get( i ) );
        return l_classes;
    }

    /**
     * converts a term into an array of class objects
     *
     * @param p_term term
     * @return class object array
     */
    private Class<?>[] convertTermListToClassArray( final Term p_term ) throws ClassNotFoundException, IllegalAccessException
    {
        final Class<?>[] l_classes = new Class<?>[1];
        l_classes[0] = this.convertTermToClass( p_term );
        return l_classes;
    }

    /**
     * converts a term value into a class object
     *
     * @param p_term Jason term
     * @return class object
     */
    private Class<?> convertTermToClass( final Term p_term ) throws IllegalAccessException
    {
        String l_classname = de.tu_clausthal.in.mec.object.mas.jason.CCommon.clearString( p_term.toString() );
        if ( "void".equalsIgnoreCase( l_classname ) )
            return void.class;

        Class<?> l_class = null;
        try
        {

            switch ( l_classname )
            {
                // check primitive datatypes
                case "bool":
                case "boolean":
                case "byte":
                case "char":
                case "short":
                case "int":
                case "long":
                case "float":
                case "double":
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "primitive", l_classname ) );

                    // object types
                default:
                    l_class = CReflection.getClassIndex().get( l_classname );
                    if ( l_class == null )
                        l_class = Class.forName( l_classname );
            }

        }
        catch ( final ClassNotFoundException l_exception )
        {
            throw new IllegalAccessException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "classnotfound", l_classname ) );
        }

        return l_class;
    }

    /**
     * decodes the Jason parameter list to create the Java signature call in the structure
     * "commandname(bind, method, [return type / default void | [return type, return belief name]], [method type arguments / default void], ...arguments)"
     *
     * @param p_parameter Jason parameter
     * @param p_argumenttypes append list with method argument types
     * @param p_argumentdata append list with method argument data
     * @return returns a pair with class and belief name of the return paramater
     *
     * @throws ClassNotFoundException on class error
     * @throws NoValueException on value error
     */
    private org.apache.commons.lang3.tuple.Pair<Class<?>, String> decodeMethodSignature( final List<Term> p_parameter, final List<Class<?>> p_argumenttypes,
            final List<Object> p_argumentdata
    ) throws ClassNotFoundException, NoValueException, IllegalAccessException
    {
        // class and string are immutable types, so we need to create a return argument
        Class<?> l_returntype = void.class;
        String l_returnname = CCommon.clearString( p_parameter.get( 1 ).toString() );

        // if paramter count > 2, then return type exists (single value or list value with 2 elements)
        if ( p_parameter.size() > 2 )
            if ( !p_parameter.get( 2 ).isList() )
                l_returntype = this.convertTermToClass( p_parameter.get( 2 ) );
            else
            {
                final ListTerm l_list = ( (ListTerm) p_parameter.get( 2 ) );
                if ( l_list.size() != 2 )
                    throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "returnargument" ) );

                l_returntype = this.convertTermToClass( l_list.get( 0 ) );
                l_returnname = CCommon.clearString( l_list.get( 1 ).toString() );
            }

        // if parameter count > 3, method arguments and argument types are exists
        if ( p_parameter.size() > 3 )
        {
            if ( p_parameter.get( 3 ).isList() )
                p_argumenttypes.addAll( Arrays.asList( this.convertTermListToClassArray( (ListTerm) p_parameter.get( 3 ) ) ) );
            else
                p_argumenttypes.add( this.convertTermToClass( p_parameter.get( 3 ) ) );

            if ( p_parameter.size() - 4 != p_argumenttypes.size() )
                throw new IllegalArgumentException( de.tu_clausthal.in.mec.common.CCommon.getResourceString( this, "argumentnumber" ) );

            for ( int i = 4; i < p_parameter.size(); ++i )
                p_argumentdata.add( CCommon.getJavaValue( p_parameter.get( i ), p_argumenttypes.get( i - 4 ) ) );
        }

        // return immutable types
        return new ImmutablePair<>( l_returntype, l_returnname );
    }

}
