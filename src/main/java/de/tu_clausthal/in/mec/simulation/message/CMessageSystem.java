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
 **/

package de.tu_clausthal.in.mec.simulation.message;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.common.CTreeNode;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.simulation.IVoidSteppable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * message handler class
 */
public class CMessageSystem implements IVoidSteppable
{
    /**
     * event listener *
     */
    protected final Set<IActionListener> m_listener = new HashSet<>();
    /**
     * tree structure of all objects (root-node is equal to this object)
     */
    protected final CTreeNode<Pair<Set<IParticipant>, Set<IMessage>>> m_root = new CTreeNode( this.toString() );


    /**
     * adds a listener
     *
     * @param p_listener listener
     */
    public void addActionListener( final IActionListener p_listener )
    {
        m_listener.add( p_listener );
    }


    /**
     * removes a listener
     *
     * @param p_listener listener
     */
    public void removeListener( final IActionListener p_listener )
    {
        m_listener.remove( p_listener );
    }


    /**
     * register a new participant
     *
     * @param p_path     path of the receiver
     * @param p_receiver participant
     */
    public final synchronized void register( final CPath p_path, final IParticipant p_receiver )
    {
        if ( ( p_path == null ) || ( p_path.isEmpty() ) || ( p_receiver == null ) || ( p_receiver == null ) )
        {
            CLogger.error( CCommon.getResourceString( this, "register", p_receiver, p_path ) );
            return;
        }

        final CTreeNode<Pair<Set<IParticipant>, Set<IMessage>>> l_node = m_root.getNode( p_path );
        if ( l_node.isDataNull() ) l_node.setData( new ImmutablePair<>( new HashSet(), new HashSet() ) );
        l_node.getData().getLeft().add( p_receiver );

        CLogger.info( CCommon.getResourceString( this, "registered", p_receiver, p_path ) );

        for ( IActionListener l_item : m_listener )
            l_item.onRegister( p_path, p_receiver );
    }


    /**
     * unregister a participant
     *
     * @param p_path     path of the receiver
     * @param p_receiver participant
     */
    public final synchronized void unregister( final CPath p_path, final IParticipant p_receiver )
    {
        if ( ( p_path == null ) || ( p_path.isEmpty() ) || ( p_receiver == null ) || ( !m_root.pathExist( p_path ) ) )
        {
            CLogger.error( CCommon.getResourceString( this, "unregister", p_receiver, p_path ) );
            return;
        }

        final CTreeNode<Pair<Set<IParticipant>, Set<IMessage>>> l_node = m_root.getNode( p_path );
        l_node.getData().getLeft().remove( p_receiver );

        CLogger.info( CCommon.getResourceString( this, "unregistered", p_receiver, p_path ) );

        for ( IActionListener l_item : m_listener )
            l_item.onUnregister( p_path, p_receiver );
    }


    /**
     * pushs a message to the queue
     *
     * @param p_path    receiver
     * @param p_message message
     */
    public final synchronized void pushMessage( final CPath p_path, final IMessage<?> p_message )
    {
        if ( ( p_path == null ) || ( p_message == null ) || ( p_path.isEmpty() ) ) return;

        if ( !m_root.pathExist( p_path ) )
        {
            CLogger.error( CCommon.getResourceString( this, "messagefail", p_message.getData(), p_path ) );
            return;
        }

        if ( ( p_message.getSource() == null ) || ( p_message.getSource().isEmpty() ) || ( p_message.getSource().getPath().equals( p_path ) ) )
        {
            CLogger.error( CCommon.getResourceString( this, "push", p_message, p_path ) );
            return;
        }

        // check time to live value
        if ( p_message.ttl() < 0 )
        {
            CLogger.info( CCommon.getResourceString( this, "ttl", p_message ) );
            return;
        }

        for ( Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.getNode( p_path ).getTreeData() )
        {
            // if item equal null skip
            if ( l_item == null ) continue;

            l_item.getRight().add( p_message );
        }

        for ( IActionListener l_item : m_listener )
            l_item.onPushMessage( p_path, p_message );
    }


    @Override
    public final void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        for ( Pair<Set<IParticipant>, Set<IMessage>> l_item : m_root.getTreeData( false ) )
        {
            // data element within the tree can be used null values, so this items will be skipped
            // the item is also skipped, if there does not exists messages
            if ( ( l_item == null ) || ( l_item.getRight() == null ) || ( l_item.getRight().isEmpty() ) ) continue;

            for ( IParticipant l_receiver : l_item.getLeft() )
                l_receiver.receiveMessage( l_item.getRight() );

            // clear all messages, that are received
            l_item.getRight().clear();
        }
    }


    @Override
    public final Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public final void release()
    {

    }


    /**
     * observer interface *
     */
    public interface IActionListener
    {

        /**
         * is called on register
         *
         * @param p_path     path of the object
         * @param p_receiver receiver
         */
        public void onRegister( final CPath p_path, final IParticipant p_receiver );

        /**
         * is called on unregister
         *
         * @param p_path     path of the object
         * @param p_receiver receiver
         */
        public void onUnregister( final CPath p_path, final IParticipant p_receiver );

        /**
         * is called on a message push
         */
        public void onPushMessage( final CPath p_pathreceiver, final IMessage<?> p_message );

    }
}
