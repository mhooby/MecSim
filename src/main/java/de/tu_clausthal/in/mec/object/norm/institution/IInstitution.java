/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.norm.institution;

import de.tu_clausthal.in.mec.object.norm.INorm;
import de.tu_clausthal.in.mec.object.norm.INormMessage;
import de.tu_clausthal.in.mec.object.norm.range.IRangeCollection;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;

import java.io.Serializable;
import java.util.Collection;


/**
 * interface of an institution, an institution is a collection of norms, stores collection of other institutions and
 * additional aspects
 */
public interface IInstitution<T> extends Collection<INorm<T>>, Serializable, Painter<COSMViewer>
{


    /**
     * returns a name of the institution *
     */
    public String getName();


    /**
     * checks an object
     *
     * @param p_object object
     */
    public void check( T p_object );


    /**
     * returns the range of the workspace of the institution
     *
     * @return range collection
     */
    public IRangeCollection<T> getRange();


    /**
     * update event norm itself (on update) or form another institution (on changing)
     *
     * @param p_norm norm object
     */
    public void update( INorm<T> p_norm );


    /**
     * update event which is generated by an institution
     *
     * @param p_message norm message
     */
    public void receive( INormMessage<T> p_message );


    /**
     * returns all institution which are superior of this institution
     *
     * @return collection
     */
    public IInstitutionCollection getSuperior();


    /**
     * returns all institutions which are inferior of this institution
     */
    public IInstitutionCollection getInferior();


    /**
     * release call *
     */
    public void release();

}
