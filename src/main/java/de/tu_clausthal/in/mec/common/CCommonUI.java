/**
 * @cond
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

package de.tu_clausthal.in.mec.common;


import javafx.scene.Node;
import javafx.scene.control.Tab;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;


/**
 * class for any UI helper calls
 */
public class CCommonUI
{

    /**
     * create a tab for the UI
     *
     * @param p_name tab name
     * @param p_node node content
     */
    public static Tab createTab( final String p_name, final Node p_node )
    {
        final Tab l_tab = new Tab();
        l_tab.setText( p_name );
        l_tab.setContent( p_node );
        l_tab.setClosable( false );
        return l_tab;
    }


    /**
     * creates a filesave dialog, which stores the current path
     *
     * @return File or null
     * @bug UI frame
     */
    public static File openFileSaveDialog( File p_defaultfilepath, String[][] p_fileextensions )
    {
        /*
        JFileChooser l_filedialog = initFileDialog( p_defaultfilepath, p_fileextensions );
        if ( l_filedialog.showSaveDialog( CSimulation.getInstance().getUIServer() ) != JFileChooser.APPROVE_OPTION )
            return null;
        p_defaultfilepath = l_filedialog.getCurrentDirectory();

        return l_filedialog.getSelectedFile();
        */
        return null;
    }


    /**
     * create input text dialog
     *
     * @param p_title       title
     * @param p_description description text
     * @return string with content or null
     * @bug UI frame
     */
    public static String openTextInputDialog( String p_title, String... p_description )
    {
        /*
        return JOptionPane.showInputDialog( CSimulation.getInstance().getUIServer(), StringUtils.join( p_description, "\n" ),
                p_title, JOptionPane.PLAIN_MESSAGE );
        */
        return null;
    }


    /**
     * select input dialog
     *
     * @param p_values      selected values
     * @param p_title       title
     * @param p_description description text
     * @return selected value
     * @bug UI frame
     */
    public static String openGroupSelectDialog( String[] p_values, String p_title, String... p_description )
    {
        /*
        return (String) JOptionPane.showInputDialog( CSimulation.getInstance().getUIServer(), StringUtils.join( p_description, "\n" ),
                p_title, JOptionPane.PLAIN_MESSAGE, null, p_values, p_values[0] );
        */
        return null;
    }

    /**
     * creates a fileload dialog, which stores the current path
     *
     * @return File or null
     * @bug UI frame
     */
    public static File openFileLoadDialog( File p_defaultfilepath, String[][] p_fileextensions )
    {
        /*
        JFileChooser l_filedialog = initFileDialog( p_defaultfilepath, p_fileextensions );
        if ( l_filedialog.showOpenDialog( CSimulation.getInstance().getUIServer() ) != JFileChooser.APPROVE_OPTION )
            return null;
        p_defaultfilepath = l_filedialog.getCurrentDirectory();

        return l_filedialog.getSelectedFile();
        */
        return null;
    }

    /**
     * creates file dialog with extension list
     *
     * @param p_defaultfilepath changes / gets the default file path
     * @param p_fileextensions  arra with extension and description
     * @return filechooser
     */
    protected static JFileChooser initFileDialog( File p_defaultfilepath, String[][] p_fileextensions )
    {
        JFileChooser l_filedialog = new JFileChooser();
        l_filedialog.setCurrentDirectory( p_defaultfilepath );
        if ( p_fileextensions != null )
        {
            l_filedialog.setAcceptAllFileFilterUsed( false );
            for ( String[] l_item : p_fileextensions )
                l_filedialog.addChoosableFileFilter( l_item.length == 1 ? new UIFileFilter( l_item[0] ) : new UIFileFilter( l_item[0], l_item[1] ) );
        }
        return l_filedialog;
    }


    /**
     * file filter class to create a filter list
     */
    protected static class UIFileFilter extends FileFilter
    {
        /**
         * type description
         */
        private String m_description = "";
        /**
         * extension
         */
        private String m_extension = "";

        /**
         * ctor
         *
         * @param p_extension extension
         */
        public UIFileFilter( String p_extension )
        {
            m_extension = p_extension;
        }

        /**
         * ctor
         *
         * @param p_extension   extension
         * @param p_description description
         */
        public UIFileFilter( String p_extension, String p_description )
        {
            m_extension = p_extension;
            m_description = p_description;

        }

        @Override
        public boolean accept( File p_file )
        {
            if ( p_file.isDirectory() )
                return true;
            return ( p_file.getName().toLowerCase().endsWith( m_extension ) );
        }

        @Override
        public String getDescription()
        {
            return m_description;
        }
    }

}
