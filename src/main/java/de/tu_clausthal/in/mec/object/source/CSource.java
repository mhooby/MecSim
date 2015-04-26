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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.source.factory.IFactory;
import de.tu_clausthal.in.mec.object.source.generator.IGenerator;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Collection;


/**
 * class with default source implementation for cars
 */
public class CSource extends ISource<ICar>
{
    /**
     * ctor - default: without generator and factory
     *
     * @param p_position position of the source
     */
    public CSource( final GeoPosition p_position )
    {
        this.setPosition( p_position );
        this.setImage();
    }

    /**
     * ctor - with specific generator and factory
     *
     * @param p_position position of the source
     * @param p_generator generator for this source
     * @param p_factory factory for this source
     */
    public CSource( final GeoPosition p_position, final IGenerator p_generator, final IFactory<ICar> p_factory )
    {
        this( p_position );
        this.setGenerator( p_generator );
        this.setFactory( p_factory );
    }

    @Override
    public final Collection<ICar> step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        return this.getFactory().generate( this.getGenerator().getCount( p_currentstep ) );
    }
}
