/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
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

package de.tu_clausthal.in.winf.simulation;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * queue class *
 */
public class CQueue<T> {

    /**
     * list of unprocessed sources *
     */
    private ConcurrentLinkedQueue<T> m_unprocess = new ConcurrentLinkedQueue();
    /**
     * list of processed sources *
     */
    private ConcurrentLinkedQueue<T> m_process = new ConcurrentLinkedQueue();


    /**
     * removes an element of the unproccessed list
     *
     * @return element
     */
    public T pop() {
        return m_unprocess.poll();
    }


    /**
     * adds an element to the proccessed list
     *
     * @param p_item processed element
     */
    public void push(T p_item) {
        m_process.add(p_item);
    }


    /**
     * check if the unprocessed list empty
     *
     * @return empty
     */
    public boolean isEmpty() {
        return m_unprocess.isEmpty() && m_process.isEmpty();
    }


    /**
     * swaps processed list to the unprocessed *
     */
    public synchronized void swap() {
        m_unprocess.addAll(m_process);
        m_process.clear();
    }


    /**
     * returns the number of elements
     *
     * @return number of elements
     */
    public int size() {
        return this.unprocessedsize() + this.processsize();
    }


    /**
     * returns the number of unprocessed elements
     *
     * @return number of elements
     */
    public int unprocessedsize() {
        return m_unprocess.size();
    }


    /**
     * returns the number of processed items
     *
     * @return number of elements
     */
    public int processsize() {
        return m_process.size();
    }


    /**
     * checks if an object exists within the queue
     *
     * @param p_item object
     * @return existence
     */
    public boolean contains(T p_item) {
        return m_unprocess.contains(p_item) || m_process.contains(p_item);
    }


    /**
     * returns a list with unprocessed elements
     *
     * @return object array
     */
    public void unprocessed2array(T[] p_array) {
        m_unprocess.toArray(p_array);
    }


    /**
     * returns an array with processed elements
     *
     * @return object array
     */
    public void process2array(T[] p_array) {
        m_process.toArray(p_array);
    }


    /**
     * adds an element to the unprocessed list
     *
     * @param p_item element
     */
    public void add(T p_item) {
        m_unprocess.add(p_item);
    }


    /**
     * adds a collection to the unprocessed queue
     *
     * @param p_item collection
     */
    public void add(Collection<T> p_item) {
        m_unprocess.addAll(p_item);
    }


    /**
     * removes an item from the lists
     *
     * @param p_item element
     */
    public synchronized void remove(T p_item) {
        m_unprocess.remove(p_item);
        m_process.remove(p_item);
    }


    /**
     * clears all lists *
     */
    public synchronized void clear() {
        m_unprocess.clear();
        m_process.clear();
    }

}