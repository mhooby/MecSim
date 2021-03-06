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

package de.tu_clausthal.in.mec.object.mas.generic;


import de.tu_clausthal.in.mec.common.CPath;


/**
 * literal interface
 *
 * @note closed world assumption, no negation marker needed
 */
public interface ILiteral<T> extends ITerm
{

    /**
     * clones the literal
     *
     * @param p_prefix add a path to the functor
     * @return copy of the literal
     */
    ILiteral<T> clone( final CPath p_prefix );

    /**
     * returns the optional annotations
     *
     * @return annotation term
     */
    ITermCollection getAnnotation();

    /**
     * returns the functor / dataset of the literal
     *
     * @return function data
     */
    IAtom<String> getFunctor();

    /**
     * getter for language specific literal
     *
     * @return literal
     */
    T getLiteral();

    /**
     * returns the optional value term
     *
     * @return value term
     */
    ITermCollection getValues();

    /**
     * getter of the literal for the negation
     */
    boolean isNegated();
}
