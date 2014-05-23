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

package de.tu_clausthal.in.winf.ui;

import javax.swing.*;


/**
 * class for create the menubar *
 */
public class CMenuBar extends JMenuBar {

    /**
     * ctor with menu items *
     */
    public CMenuBar() {
        super();

        CMenuListener l_listener = new CMenuListener();

        String[] l_file = {"Load Sources", "Save Sources", null, "Screenshot"};
        this.add(CMenuFactory.createMenu("File", l_file, l_listener));

        String[] l_actions = {"Start", "Stop", null, "Reset"};
        this.add(CMenuFactory.createMenu("Action", l_actions, l_listener));

        String[] l_weights = {"Default", "Speed", "Traffic Jam", "Speed & Traffic Jam"};
        this.add(CMenuFactory.createRadioMenu("Graph Weights", l_weights, l_listener));

        String[] l_drivemodel = {"Nagel-Schreckenberg"};
        this.add(CMenuFactory.createRadioMenu("Driving Model", l_drivemodel, l_listener));
    }


}
