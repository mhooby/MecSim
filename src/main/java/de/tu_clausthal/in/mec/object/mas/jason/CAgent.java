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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.mas.IVoidAgent;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import de.tu_clausthal.in.mec.object.mas.general.implementation.CBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.implementation.CBeliefBaseStorage;
import de.tu_clausthal.in.mec.object.mas.general.implementation.CBeliefMaskStorage;
import de.tu_clausthal.in.mec.object.mas.jason.action.CBeliefRemove;
import de.tu_clausthal.in.mec.object.mas.jason.action.CInternalEmpty;
import de.tu_clausthal.in.mec.object.mas.jason.action.CLiteral2Number;
import de.tu_clausthal.in.mec.object.mas.jason.action.CMethodBind;
import de.tu_clausthal.in.mec.object.mas.jason.action.IAction;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CBindingStorage;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CMessageStorage;
import de.tu_clausthal.in.mec.runtime.message.CParticipant;
import de.tu_clausthal.in.mec.runtime.message.IMessage;
import jason.JasonException;
import jason.RevisionFailedException;
import jason.architecture.AgArch;
import jason.architecture.MindInspectorWeb;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Agent;
import jason.asSemantics.InternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.PlanLibrary;
import jason.bb.DefaultBeliefBase;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * class of a Jason agent architecture
 *
 * @tparam T typ of binding objects
 * @todo error in cycle step, synchronize agent and generic beliefbase correctly
 */
public class CAgent implements IVoidAgent<Literal>
{
    /**
     * name of the binding beliefbase and its mask
     */
    private static final String c_beliefbase_bind = "bind";
    /**
     * name of the message beliefbase and its mask
     */
    private static final String c_beliefbase_message = "message";
    /**
     * name of the root beliefbase and its mask
     */
    private static final String c_beliefbase_root = "root";
    /**
     * bind name of the initial object
     */
    private static final String c_bindname = "self";
    /**
     * static list of internal overwrite actions
     */
    private static final Map<String, InternalAction> c_overwriteaction = new HashMap<String, InternalAction>()
    {{
            // overwrite default internal actions
            final CInternalEmpty l_empty13 = new CInternalEmpty( 1, 3 );
            put( "jason.stdlib.clone", new CInternalEmpty() );
            put( "jason.stdlib.wait", l_empty13 );
            put( "jason.stdlib.create_agent", l_empty13 );
            put( "jason.stdlib.kill_agent", new CInternalEmpty( 1, 1 ) );
            put( "jason.stdlib.stopMAS", new CInternalEmpty( 0, 0 ) );

            // add own function
            put( "mecsim.literal2number", new CLiteral2Number() );
            put( "mecsim.removeBelief", new CBeliefRemove() );
        }};
    /**
     * path seperator
     */
    private final static String c_seperator = "::";
    /**
     * set with actions of this implementation
     */
    private final Map<String, IAction> m_action = new HashMap<>();
    /**
     * agent object
     */
    private final Agent m_agent;
    /**
     * Jason interal agent architecture to run the reasoning cycle
     */
    private final CJasonArchitecture m_architecture;
    /**
     * root beliefbase
     */
    private final IBeliefBaseMask<Literal> m_beliefbaserootmask;
    /**
     * agents beliefbases
     */
    private final CBeliefBaseStorage<Literal> m_beliefbases = new CBeliefBaseStorage();
    /**
     * cycle number of the agent - it need not to be equal to the simulation step (the cycle is the lifetime of the
     * agent)
     */
    private int m_cycle;
    /**
     * mapping from functor to path
     */
    private final Map<String, CPath> m_mapping = new HashMap<>();
    /**
     * method bind
     */
    private final CMethodBind m_methodBind;
    /**
     * name of the agent
     */
    private final CPath m_namepath;
    /**
     * participant object
     */
    private final CParticipant m_participant;

    /**
     * ctor
     *
     * @param p_asl agent ASL file
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final String p_asl ) throws JasonException
    {
        this( null, p_asl, null );
    }

    /**
     * ctor
     *
     * @param p_namepath name of the agent (full path)
     * @param p_asl agent ASL file
     * @param p_bind object that should be bind with the agent
     * @throws JasonException throws an Jason exception
     * @note a default behaviour is defined: the name of the agent is the Java object information (class name and object hash)
     * and all properties and methods will be bind to the agent with the source "self"
     */
    public CAgent( final CPath p_namepath, final String p_asl, final T p_bind ) throws JasonException
    {
        m_namepath = ( p_namepath == null ) || ( p_namepath.isEmpty() ) ? new CPath( this.getClass().getSimpleName() + "@" + this.hashCode() ) : p_namepath;

        // Jason code design error: the agent name is stored within the AgArch, but it can read if an AgArch has got an AgArch
        // successor (AgArchs are a linked list), so we insert a cyclic reference to the AgArch itself
        m_architecture = new CJasonArchitecture();
        m_architecture.insertAgArch( m_architecture );

        // build an own agent to handle manual internal actions
        m_agent = new CJasonAgent( IEnvironment.getAgentFile( p_asl ), m_architecture );

        // initialize message system
        m_participant = new CParticipant( this );

        // create action bind & beliefbases with tree structure
        m_methodBind = p_bind == null ? null : new CMethodBind( c_bindname, p_bind );

        m_beliefbases.add( c_beliefbase_root, new CBeliefBase<>( new CBeliefMaskStorage<>() ) );
        m_beliefbases.add( c_beliefbase_message, new CBeliefBase<>( new CMessageStorage( m_agent.getTS(), c_seperator ) ) );
        m_beliefbases.add( c_beliefbase_bind, new CBeliefBase<>( new CBindingStorage() ) );

        m_beliefbaserootmask = m_beliefbases.get( c_beliefbase_root ).createMask( c_beliefbase_root );
        m_beliefbases.get( c_beliefbase_root ).add( m_beliefbases.get( c_beliefbase_bind ).createMask( c_beliefbase_bind ) );
        m_beliefbases.get( c_beliefbase_root ).add( m_beliefbases.get( c_beliefbase_message ).createMask( c_beliefbase_message ) );

        if ( p_bind != null )
        {
            // register possible actions
            m_action.put( "set", new de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind( c_bindname, p_bind ) );
            m_action.put( "invoke", m_methodBind );

            m_beliefbases.get( c_beliefbase_bind ).<CBindingStorage>getStorage().push( c_bindname, this );
        }
    }


    /**
     * ctor
     *
     * @param p_namepath name of the agent (full path)
     * @param p_asl agent ASL file
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final CPath p_namepath, final String p_asl ) throws JasonException
    {
        this( p_namepath, p_asl, null );
    }


    /**
     * ctor
     *
     * @param p_asl agent ASL file
     * @param p_bind object that should be bind with the agent
     * @throws JasonException throws an Jason exception
     */
    public CAgent( final String p_asl, final T p_bind ) throws JasonException
    {
        this( null, p_asl, p_bind );
    }

    /**
     * returns the set of actions
     *
     * @return action set
     */
    public final Map<String, IAction> getActions()
    {
        return m_action;
    }

    @Override
    public final int getCycle()
    {
        return m_cycle;
    }

    @Override
    public final String getName()
    {
        return m_namepath.getPath( c_seperator );
    }

    @Override
    public final String getSource()
    {
        return new File( m_agent.getASLSrc() ).getName();
    }

    @Override
    public void registerAction( final String p_name, final Object p_object )
    {
        if ( m_methodBind == null )
            return;

        m_methodBind.push( p_name, p_object );
    }

    @Override
    public void registerMask( final CPath p_path, final IBeliefBaseMask<Literal> p_mask )
    {
        m_beliefbaserootmask.add( p_mask );
    }


    @Override
    public final void release()
    {
        m_agent.stopAg();
        m_participant.release();
        MindInspectorWeb.get().removeAg( m_agent );
    }

    @Override
    public void unregisterAction( final String p_name )
    {
        if ( m_methodBind == null )
            return;

        m_methodBind.remove( p_name );
    }

    @Override
    public void unregisterMask( final CPath p_path )
    {

    }

    @Override
    public final CPath getReceiverPath()
    {
        return m_namepath;
    }

    /**
     * pass messages to message containing beliefbase
     *
     * @param p_messages set of messages
     */
    @Override
    public final void receiveMessage( final Set<IMessage> p_messages )
    {
        m_beliefbases.get( c_beliefbase_message ).<CMessageStorage>getStorage().receiveMessage( p_messages );
    }

    @Override
    public void paint( final Graphics2D p_graphic, final Object p_object, final int p_width, final int p_height )
    {

    }

    @Override
    public final void step( final int p_currentstep, final ILayer p_layer )
    {
        m_architecture.cycle( p_currentstep );
    }

    /**
     * class to create an own agent architecture to define the reasoning cycle one agent uses one agent architecture
     *
     * @note Jason needs on the Agent.create call an instance of AgArch and not AgArchTier, so we need an own class to
     * create an own cycle call
     * @warning An AgArch is a linked-list of AgArchs, the agent name can read if an AgArch has got a successor only
     * (Jason code design error)
     * @see http://jason.sourceforge.net/api/jason/architecture/AgArchInfraTier.html
     */
    protected class CJasonArchitecture extends AgArch
    {

        @Override
        public final void act( final ActionExec p_action, final List<ActionExec> p_feedback )
        {
            final IAction l_action = m_action.get( p_action.getActionTerm().getFunctor() );
            if ( l_action != null )
                try
                {
                    l_action.act( m_agent, p_action.getActionTerm() );
                    p_action.setResult( true );
                }
                catch ( final Exception l_exception )
                {
                    p_action.setFailureReason( ASSyntax.createAtom( "exception" ), l_exception.getMessage() );
                    p_action.setResult( false );
                }

            p_feedback.add( p_action );
        }

        @Override
        public final boolean canSleep()
        {
            return false;
        }

        @Override
        public final String getAgName()
        {
            return m_namepath.getPath( c_seperator );
        }

        @Override
        public final void sendMsg( final Message p_message ) throws Exception
        {
            p_message.setSender( getReceiverPath().toString() );
            m_participant.sendMessage( new CPath( p_message.getReceiver().split( c_seperator ) ), new CMessage( p_message ) );
        }

        @Override
        public final void broadcast( final Message p_message ) throws Exception
        {
            final CPath l_path = new CPath( m_namepath );
            if ( m_namepath.size() > 0 )
                l_path.removeSuffix();

            p_message.setSender( m_namepath.toString() );
            p_message.setReceiver( l_path.toString() );
            m_participant.sendMessage( l_path, new CMessage( p_message ) );
        }

        /**
         * manual call of the reasoning cycle
         *
         * @param p_currentstep current step
         */
        public final void cycle( final int p_currentstep )
        {
            // update beliefbases
            m_beliefbases.get( "root" ).update();

            // synchronize agent beliefbase
            m_agent.getBB().clear();
            for ( final ILiteral<Literal> l_literal : m_beliefbases.get( "root" ) )
                try
                {
                    m_agent.addBel( l_literal.getLiteral() );
                }
                catch ( final RevisionFailedException l_exception )
                {
                    CLogger.error( l_exception );
                }

            // run agent reasoning cycle for deducing new beliefs
            // the reasoning cycle must be called within the transition system
            this.setCycleNumber( m_cycle++ );
            this.getTS().reasoningCycle();
        }
    }

    /**
     * class of an own Jason agent to handle Jason stdlib internal action includes
     *
     * @note we do the initialization process manually, because some internal actions are removed from the default
     * behaviour
     * @see http://jason.sourceforge.net/api/jason/asSemantics/TransitionSystem.html
     */
    protected class CJasonAgent extends Agent
    {

        /**
         * ctor - for building a "blank / empty" agent
         *
         * @param p_asl ASL file
         * @param p_architecture architecture
         */
        public CJasonAgent( final File p_asl, final AgArch p_architecture ) throws JasonException
        {
            this.setTS( new TransitionSystem( this, null, null, p_architecture ) );
            this.setBB( new DefaultBeliefBase() );
            this.setPL( new PlanLibrary() );
            this.initDefaultFunctions();


            try
            {
                CReflection.getClassField( this.getClass(), "initialGoals" ).getSetter().invoke( this, new ArrayList<>() );
                CReflection.getClassField( this.getClass(), "initialBels" ).getSetter().invoke( this, new ArrayList<>() );

                // create internal actions map - reset the map and overwrite not useable actions with placeholder
                CReflection.getClassField( this.getClass(), "internalActions" ).getSetter().invoke( this, c_overwriteaction );
            }
            catch ( final Throwable l_throwable )
            {
                CLogger.error( l_throwable );
            }

            this.load( p_asl.toString() );
            MindInspectorWeb.get().registerAg( this );
        }

    }

}
