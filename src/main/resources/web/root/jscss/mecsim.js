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


"use strict";

/**
 * base modul to represent base algorithms
 * and structure to encapsulate structures
 **/
var MecSim = (function (px_modul) {


    // --- websocket structure ---------------------------------------------------------------------------------------------------------------------------------
    /**
     * websocket structure to encapsulate access to the internal websocket structure  with function binding
     *
     * @param pc_wspath path of the websocket (can set relative to the window location)
     * @param po_options object with onmessage, onerror, onopen and onclose functions to bind functions direct
     **/
    px_modul.websocket = function( pc_wspath, po_options )
    {
        var lo_options   = po_options || {};
        var lo_socket;

        if ((pc_wspath.startsWith("ws://")) || (pc_wspath.startsWith("wss://")))
            lo_socket = new WebSocket(pc_wspath);
        else
            lo_socket = new WebSocket( ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname + (((location.port != 80) && (location.port != 443)) ? ":" + location.port : "") + (pc_wspath.startsWith("/") ? pc_wspath : location.pathname + pc_wspath ) );

        if (lo_socket !== undefined)
        {
            lo_socket.onopen    = lo_options.onopen    || null;
            lo_socket.onclose   = lo_options.onclose   || null;
            lo_socket.onmessage = lo_options.onmessage || null;
            lo_socket.onerror   = lo_options.onerror   || null;
        }
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- logger structure ------------------------------------------------------------------------------------------------------------------------------------
    /**
     * logger structure to encapsulate functionality of Java logger class
     **/
    px_modul.logger = function() {return{

        /**
         * function to read the logger configuration
         * @param px_success function which is called on read success
         **/
        get   : function( px_success ) { $.ajax({ url : "/clogger/configuration", type: "POST",  success : px_success }); },

        /**
         * error logging
         * @param px_message message
         **/
        error : function( px_message ) { console.error(px_message); $.ajax({ url : "/clogger/error",         type: "POST",  data : px_message  }); },

        /**
         * debug logging
         * @param px_message message
         **/
        debug : function( px_message ) { console.warn(px_message); $.ajax({ url : "/clogger/debug",         type: "POST",  data : px_message  }); },

        /**
         * warning logging
         * @param px_message message
         **/
        warn  : function( px_message ) { console.warn(px_message); $.ajax({ url : "/clogger/warn",          type: "POST",  data : px_message  }); },

        /**
         * information logging
         * @param px_message message
         **/
        info  : function( px_message ) { console.info(px_message); $.ajax({ url : "/clogger/info",          type: "POST",  data : px_message  }); },

        /**
         * output logging
         * @param px_message message
         **/
        out   : function( px_message ) { console.log(px_message); $.ajax({ url : "/clogger/out",           type: "POST",  data : px_message  }); }

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // ---- configuration structure ----------------------------------------------------------------------------------------------------------------------------
    /**
     * configuration access to encapsulate access
    **/

    px_modul.configuration = function() { return {

        /** get the configuration
         * @param px_success function which is called on successful reading
         **/
        get : function( px_success ) { $.ajax({ url : "/cconfiguration/get", type: "POST",  success : px_success }); },

        /**
         * sets the configuration
         * @param po_data full configuration object
         **/
        set : function( po_data ) { $.ajax({ url : "/cconfiguration/set", type: "POST",  data : po_data }); },

    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------



    // --- language access -------------------------------------------------------------------------------------------------------------------------------------
    /**
     * language access for setting text elements
     * @note the keys of the returned json object are the labels of the DOM elements
     * @param pc_group the group label, that matches the URL part
    **/
    px_modul.language = function( pc_group, pc_callback )
    {
        $.ajax({
            url : "/clanguageenvironment/"+pc_group,
            type: "GET",
            success : function( po_data )
            {
                $.each(po_data, function(pc_key, pc_text){
                    $(pc_key).text(pc_text);
                });

                if(pc_callback !== undefined || pc_callback !==null)
                    pc_callback();
            }
        });
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- UI references ---------------------------------------------------------------------------------------------------------------------------------------
    /**
     * references to static UI components
     **/
    px_modul.ui = function() {return {

        /** reference to the screen area **/
        screen      : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_screen"; },
        /** reference to the menu **/
        menu        : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_menu"; },
        /** reference to the accordion **/
        accordion   : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_accordion"; },
        /** reference to the menu section of the screen **/
        screenmenu  : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_screen_right"; },
        /** reference to the content area **/
        content     : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_content"; },
        /** reference to log area **/
        log         : function(pc_prefix) { return (pc_prefix ? pc_prefix : "") + "mecsim_global_log"; },
    };}
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- initializing of the UI content ----------------------------------------------------------------------------------------------------------------------
    px_modul.uiinitialize = function( pa )
    {
        // create basic layout within the body element (three frame structure)
        jQuery( '<div id = "' + px_modul.ui().screen()     + '" >' +
                '<div id = "' + px_modul.ui().menu()       + '" >' +
                '<div id = "' + px_modul.ui().accordion()  + '" />' +
                '</div>' +
                '<div>' +
                '<div id = "' + px_modul.ui().screenmenu() + '" >' +
                '<div id = "' + px_modul.ui().content()    + '" ></div >' +
                '<div id = "' + px_modul.ui().log()        + '" ></div >' +
                '</div >' +
                '</div >' +
                '</div>').appendTo("body");


        // add main layout elements to the HTML body
        if (Array.isArray(pa))
            pa.forEach( function(px_item) {
                if (px_item instanceof Pane)
                {
                    // global elements
                    if (px_item.getGlobalContent())
                        jQuery( px_item.getGlobalContent() ).appendTo("body");
                    if (px_item.getGlobalCSS())
                        jQuery("head").append( '<style type = "text/css">' + px_item.getGlobalCSS() + '</style>' );

                    // accordion elements
                    if (px_item.getName())
                    {
                        jQuery( '<h3 id = "' + px_item.getID() + '">' + px_item.getName() + '</h3>' ).appendTo( px_modul.ui().accordion("#") );
                        if (px_item.getContent())
                            jQuery( px_item.getContentWithContainer() ).appendTo( px_modul.ui().accordion("#") );
                    }

                    px_item.afterDOMAdded();
                }
            });


        // initialize the content pane with the three layer structures - must be called at the end, because of correct layout structure
        jQuery( px_modul.ui().screen("#") ).jqxSplitter({ width: "100%", height: "100%", panels: [{ size: "20%", min: 250 }, { size: "80%"}] });
        jQuery( px_modul.ui().screenmenu("#") ).jqxSplitter({ width: "100%", height: "100%", orientation: "horizontal", panels: [{ size: "85%", collapsible: false }] });
        jQuery( px_modul.ui().accordion("#") ).accordion({ active: false, collapsible: true, heightStyle: "content" });
    }
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    return px_modul;

}(MecSim || {}));
