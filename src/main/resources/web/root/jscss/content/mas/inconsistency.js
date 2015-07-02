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

//@todo fix HTML5 data attributes


/**
 * ctor to create the inconsistency item
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function MASInconsistency( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
MASInconsistency.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
MASInconsistency.prototype.getGlobalContent = function()
{
    return Layout.dialog({

        id        : this.generateSubID("dialog"),
        title     : "",
        content   : Layout.select({

            id    : this.generateSubID("metric"),
            label : ""

        }) +

        Layout.area({

            id    : this.generateSubID("paths"),
            label : ""

        })

    }) +
    Pane.prototype.getGlobalContent.call(this);
}


/**
 * @Overwrite
**/
MASInconsistency.prototype.getContent = function()
{
    return '<button id = "' + this.getID() + '" ></button >' + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
MASInconsistency.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    MecSim.language({

        url : "/cinconsistencyenvironment/" + this.mc_id + "/label",
        target : this,
        finish : function()
        {

            jQuery( self.generateSubID("dialog", "#") ).dialog({
                modal    : true,
                autoOpen : false,
                overlay  : { background: "black" },
                buttons  : {

                    "OK" : function() {}

                }
            });

        }

    });

    // bind button action
    jQuery(this.getID("#")).button().click( function() {

        MecSim.ajax({

            url     : "/cinconsistencyenvironment/" + self.mc_id + "/getmetric",
            success : function( po_data )
            {

                jQuery( self.generateSubID("metric", "#") ).empty();
                jQuery( self.generateSubID("paths", "#") ).empty();

                jQuery.each( po_data, function( pc_key, po_value ) {

                    jQuery( Layout.option({
                        label : pc_key,
                        id    : po_value.id
                    },
                    po_value.active ? po_value.id : null) ).appendTo( self.generateSubID("metric", "#")  );

                    if (po_value.active)
                        jQuery( po_value.selector.join("\n") ).appendTo( self.generateSubID("path", "#") );

                    jQuery( self.generateSubID("dialog", "#") ).dialog( "open" );

                });


            }

        });

    });
}