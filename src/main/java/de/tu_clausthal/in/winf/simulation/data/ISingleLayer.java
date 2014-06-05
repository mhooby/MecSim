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

package de.tu_clausthal.in.winf.simulation.data;


import de.tu_clausthal.in.winf.simulation.process.IVoidStepable;
import de.tu_clausthal.in.winf.ui.IViewableLayer;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.util.Map;


/**
 * single layer to create a single information structure
 */
public abstract class ISingleLayer implements Painter, IViewableLayer, Ilayer, IVoidStepable {

    protected boolean m_visible = true;
    protected boolean m_active = true;


    public boolean isActive() {
        return m_active;
    }


    public void setActive(boolean p_active) {
        m_active = p_active;
    }


    public boolean isVisible() {
        return m_visible;
    }


    public void setVisible(boolean p_visible) {
        m_visible = p_visible;
    }


    @Override
    public void step(int p_currentstep) {

    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }

    @Override
    public void paint(Graphics2D graphics2D, Object o, int i, int i2) {
    }

}
