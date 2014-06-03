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

package de.tu_clausthal.in.winf.objects.norms;

import de.tu_clausthal.in.winf.mas.norm.IRange;
import de.tu_clausthal.in.winf.mas.norm.IRangeCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * disjoint range
 */
public class CDisjointRangeCollection<T> implements IRangeCollection<T> {
    private Set<IRange<T>> m_ranges = new HashSet();


    @Override
    public boolean isWithin(T p_object) {
        if (m_ranges.isEmpty())
            return false;

        for (IRange<T> l_item : m_ranges)
            if (l_item.isWithin(p_object))
                return true;

        return false;
    }

    @Override
    public void release() {
        for (IRange<T> l_item : m_ranges)
            l_item.release();
    }

    @Override
    public int size() {
        return m_ranges.size();
    }

    @Override
    public boolean isEmpty() {
        return m_ranges.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return m_ranges.contains(o);
    }

    @Override
    public Iterator<IRange<T>> iterator() {
        return m_ranges.iterator();
    }

    @Override
    public Object[] toArray() {
        return m_ranges.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return m_ranges.toArray(a);
    }

    @Override
    public boolean add(IRange<T> iRange) {
        return m_ranges.add(iRange);
    }

    @Override
    public boolean remove(Object o) {
        return m_ranges.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return m_ranges.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends IRange<T>> c) {
        return m_ranges.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return m_ranges.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return m_ranges.retainAll(c);
    }

    @Override
    public void clear() {
        m_ranges.clear();
    }
}
