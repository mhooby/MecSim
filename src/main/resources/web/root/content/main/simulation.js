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


"use strict";


/**
 * ctor to create the simulation menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Simulation( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
Simulation.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Simulation.prototype.getContent = function()
{
    return '<p> <button class = "ui-menu-button" id = "' + this.generateSubID("start") + '" ></button> ' +
           '<button class = "ui-menu-button" id = "' + this.generateSubID("stop")  + '" ></button> ' +
           '<button class = "ui-menu-button" id = "' + this.generateSubID("reset") + '" ></button> </p>' +

           '<p> <button class = "ui-menu-button" id = "' + this.generateSubID("load") + '" >' +
           '</button> <button class = "ui-menu-button" id = "' + this.generateSubID("save") + '" ></button>' +
           '<input type = "file" id = "' + this.generateSubID("loadfile") + '" /> <input type = "file" id = "' + this.generateSubID("savefile") + '" /></p>' +

           '<p><div id="' + this.generateSubID("threadsleeptime") + '"></div></p>' +

           Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Simulation.prototype.getGlobalContent = function()
{
    return Layout.dialog({
        id        : this.generateSubID("dialog"),
        contentid : this.generateSubID("text"),
        title     : this.generateSubID("dialogtitle")
    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
Simulation.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    MecSim.language({ url : "/clanguageenvironment/simulation", target : this });


    // --- create start / stop / reset buttons & bind actions to the button ------------------------------------------------------------------------------------
    ["start", "stop", "reset"].forEach( function(pc_item) {

        jQuery(self.generateSubID(pc_item, "#")).button().click( function() {

            MecSim.ajax("/csimulation/"+pc_item).fail( function( po_data ) {

                jQuery( self.generateSubID("text", "#")   ).text(po_data.responseJSON.error);
                jQuery(self.generateSubID("dialog", "#")).dialog({
                    modal    : true
                });

            });

        });

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- reset runs clear on the log layer -------------------------------------------------------------------------------------------------------------------
    jQuery(this.generateSubID("reset", "#")).click( function() {
        jQuery( MecSim.ui().log("#") ).empty();
    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- load / save buttons & bind actions ------------------------------------------------------------------------------------------------------------------
    ["load", "save"].forEach( function(pc_item) {

        jQuery( self.generateSubID(pc_item + "file", "#") ).css("opacity", "0");
        jQuery( self.generateSubID(pc_item, "#") ).button().click( function( po_event ) {

            po_event.preventDefault();
            jQuery( self.generateSubID(pc_item + "file", "#") ).trigger( "click" );

        });
    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------


    // --- thread-sleep-time slider ----------------------------------------------------------------------------------------------------------------------------
    MecSim.ajax({
        url  : "/csimulation/getthreadsleep"
    }).done(function(po) {

        jQuery( self.generateSubID("threadsleeptime", "#") ).slider({
            value  : po.time,
            min    : 1,
            max    : 1500,
            change : function( po_event, po_ui )
            {
                MecSim.ajax({
                    url     : "/csimulation/setthreadsleep",
                    data    : { time : po_ui.value  },
                });
            }
        });

    });
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
}


/**
 * @Overwrite
**/
Simulation.prototype.setName = function(pc)
{
    this.mc_name = pc;
    jQuery( this.getID("#") ).append(this.mc_name);
}
