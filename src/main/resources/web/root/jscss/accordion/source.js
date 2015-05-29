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

/** todo collection for source-ui ck
TODO responsive wizard content height
TODO same selectmenu (consistent)
TODO use next previous finish labels
TODO check last jquery slectors
TODO save toolbox in config/web
TODO Further GUI Elements (WaypointType, Radius, CarSettings Patameters, Histrogramm)
TODO bounding in minimized mode maybe snap to tab
TODO plot distribution
TODO Car Settings
TODO show error dialog if tool can not created (check for correctness on java side)
TODO distingusish between init and refresh(asl, ...)
TODO source list table (JTable or JQuery Plugin)
TODO source weighting table (JTable or JQuery Plugin)
TODO radial refactor (bugs and generic)
**/

var SourcePanel = ( function (px_module) {

    px_module.settings = {
        labels  :   {},
        dom     :   {
            label       : {},
            panel       : $("#mecsim_source_panel"),
            toolbox     : $("#mecsim_source_toolbox"),
            createTool  : $("#mecsim_source_createTool")
        },
        obj     :   {}
    };

    //method to initialize source-ui
    px_module.init = function() {

        //get labels and build the toolbox
        SourcePanel.getLabels();
        SourcePanel.buildToolbox();

        //load template if panel clicked
        SourcePanel.settings.dom.panel.on("click", function(data){
            MecSim.ui().content().empty();
            MecSim.ui().content().load("template/source.htm", function(){

                //get dom elements and set labels
                SourcePanel.getDOMElements();
                MecSim.language("getstaticwaypointlabels", function(){

                    //create widget
                    SourcePanel.settings.obj.widget = Widget.createWidget(
                        SourcePanel.settings.dom.widget,
                        {
                            name     : SourcePanel.settings.labels.wizardwidget,
                            width    : 850
                        }
                    );

                    //create wizard
                    SourcePanel.settings.obj.wizard = SourcePanel.settings.dom.wizard.steps({
                        headerTag: "h3",
                        bodyTag: "section",
                        transitionEffect: "slideLeft",
                        stepsOrientation: "vertical",
                        autoFocus: true,
                        onInit: SourcePanel.buildContent,
                        onStepChanging: SourcePanel.validateWizardStep,
                        onFinished: SourcePanel.finishWizard
                    });

                    //additional initial value
                    SourcePanel.settings.dom.selectRadius.val(0.75);
                    SourcePanel.settings.dom.generatorInputCarcount.val(1);
                    SourcePanel.settings.dom.toolName.attr("value", SourcePanel.settings.labels.selecttoolnamevalue);
                });
            });
        });

        //listen to create tool button
        SourcePanel.settings.dom.createTool.button().on("click", function(data){
            SourcePanel.settings.obj.widget.close();
        });
    };

    //method to get DOM Elements
    px_module.getDOMElements = function(){

        //dom elements (no labels)
        SourcePanel.settings.dom.widget                          = $("#mecsim_source_widget");
        SourcePanel.settings.dom.wizard                          = $("#mecsim_source_wizard");
        SourcePanel.settings.dom.colorpicker                     = $("#mecsim_source_colorpicker");
        SourcePanel.settings.dom.selectFactory                   = $("#mecsim_source_selectFactory");
        SourcePanel.settings.dom.selectWaypointType              = $("#mecsim_source_selectWaypointType");
        SourcePanel.settings.dom.selectRadius                    = $("#mecsim_source_waypointRadius");
        SourcePanel.settings.dom.selectAgentProgram              = $("#mecsim_source_selectAgentProgram");
        SourcePanel.settings.dom.selectGenerator                 = $("#mecsim_source_selectGenerator");
        SourcePanel.settings.dom.generatorInputCarcount          = $("#mecsim_source_generatorInputCarcount");
        SourcePanel.settings.dom.generatorInput2                 = $("#mecsim_source_generatorInput2");
        SourcePanel.settings.dom.generatorInput3                 = $("#mecsim_source_generatorInput3");
        SourcePanel.settings.dom.carSettings                     = $("#mecsim_source_carSettings");
        SourcePanel.settings.dom.toolName                        = $("#mecsim_source_toolName");

        //dom elements (dynamic labels)
        SourcePanel.settings.dom.label.generatorinput2label      = $("#mecsim_source_generatorInput2_label");
        SourcePanel.settings.dom.label.generatorinput3label      = $("#mecsim_source_generatorInput3_label");
        SourcePanel.settings.dom.label.speedprobinput1label      = $("#mecsim_source_speedProbInput1_label");
        SourcePanel.settings.dom.label.speedprobinput2label      = $("#mecsim_source_speedProbInput2_label");
        SourcePanel.settings.dom.label.maxSpeedprobinput1label   = $("#mecsim_source_maxSpeedProbInput1_label");
        SourcePanel.settings.dom.label.maxSpeedprobinput2label   = $("#mecsim_source_maxSpeedProbInput2_label");
        SourcePanel.settings.dom.label.accprobinput1label        = $("#mecsim_source_accProbInput1_label");
        SourcePanel.settings.dom.label.accprobinput2label        = $("#mecsim_source_accProbInput2_label");
        SourcePanel.settings.dom.label.decprobinput1label        = $("#mecsim_source_decProbInput1_label");
        SourcePanel.settings.dom.label.decprobinput2label        = $("#mecsim_source_decProbInput2_label");
        SourcePanel.settings.dom.label.lingerprobinput1label     = $("#mecsim_source_lingerProbInput1_label");
        SourcePanel.settings.dom.label.lingerprobinput2label     = $("#mecsim_source_lingerProbInput2_label");
     };

    //method to get source-ui related labels which are dynamic
    px_module.getLabels = function(){
        $.ajax({
            url     : "/clanguageenvironment/getdynamicwaypointlabels",
            success : function( px_data ){

                for(var key in px_data){
                    SourcePanel.settings.labels[key] = px_data[key];
                }
            }
        });
    };

    //method to create toolbox
    px_module.buildToolbox = function(){
        $.ajax({
            url     : "/cwaypointenvironment/listtools",
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {

                    $("</p>").appendTo(SourcePanel.settings.dom.toolbox);

                    var button = $("<button></button>")
                        .text(pc_key)
                        .attr("class", "mecsim_global_accordeonbutton")
                        .attr("value",pc_key)
                        .button()
                        .on("click", {"toolname": pc_key}, SourcePanel.listenToolButton)
                        .appendTo(SourcePanel.settings.dom.toolbox);

                    $("<span></span>")
                        .css("background-color", "rgb("+ px_value.redValue +","+ px_value.greenValue +","+ px_value.blueValue +")")
                        .attr("class", "mecsim_source_toolIcon")
                        .prependTo(button);

                });
            }
        });
    };

    //method to listen to toolbutton
    px_module.listenToolButton = function(event){
        $.ajax({
            url     : "/cwaypointenvironment/settool",
            data    : {"toolname": event.data.toolname}
        });
    };

    //method to build up the wizard content
    px_module.buildContent = function() {

        SourcePanel.getDOMElements();

        //create selectmenu with possible agent programs
        $.ajax({
            url     : "/cwaypointenvironment/listwaypointtypes",
            success : function( px_data ){
                px_data.waypointtypes.forEach(function(data){
                    SourcePanel.settings.dom.selectWaypointType
                        .append( $("<option></option>")
                        .attr("value",data)
                        .text(data));
                });
            }
        });

        //create selectmenu with factory types
        $.ajax({
            url     : "/cwaypointenvironment/listfactories",
            success : function( px_data ){
                px_data.factories.forEach(function(data){
                    $.each( data, function( pc_key, px_value ) {
                        SourcePanel.settings.dom.selectFactory
                            .append( $("<option></option>")
                            .attr("value",pc_key)
                            .attr("requireAgent", px_value)
                            .text(pc_key));
                    });
                });
                SourcePanel.updateFactorySettings();
            }
        });

        //create selectmenu with possible agent programs
        $.ajax({
            url     : "/cagentenvironment/jason/list",
            success : function( px_data ){
                px_data.agents.forEach(function(data){
                    SourcePanel.settings.dom.selectAgentProgram
                        .append( $("<option></option>")
                        .attr("value",data)
                        .text(data));
                });
            }
        });

        //create selectmenu with distribution select
        $.ajax({
            url     : "/cwaypointenvironment/listdistribution",
            success : function( px_data ){
                px_data.generators.forEach(function(data){
                    $("<option></option>")
                        .attr("value",data)
                        .text(data)
                        .appendTo(".mecsim_source_distributionSelect");
                    });

                SourcePanel.updateGeneratorSettings();
            }
        });

        //create carsettings accordion
        SourcePanel.settings.dom.carSettings.accordion({collapsible: true, heightStyle: "content", active: false});

        //create colorpicker
        SourcePanel.settings.obj.colorpicker = $("#mecsim_source_colorpicker").spectrum({
            showPaletteOnly: true,
            togglePaletteOnly: true,
            togglePaletteMoreText: 'more',
            togglePaletteLessText: 'less',
            color: '#008C4F',
            palette: [
                ["#000","#444","#666","#999","#ccc","#eee","#f3f3f3","#fff"],
                ["#f00","#f90","#ff0","#0f0","#0ff","#00f","#90f","#f0f"],
                ["#f4cccc","#fce5cd","#fff2cc","#d9ead3","#d0e0e3","#cfe2f3","#d9d2e9","#ead1dc"]
            ]
        });

        //listen to different ui elements
        SourcePanel.settings.dom.selectWaypointType.on("change", SourcePanel.updateWaypointSettings);
        SourcePanel.settings.dom.selectFactory.on("change", SourcePanel.updateFactorySettings);
        SourcePanel.settings.dom.selectGenerator.on("change", SourcePanel.updateGeneratorSettings);
    };

    //method to validate if the current wizard step is valide
    px_module.validateWizardStep = function(event , currentIndex, newIndex) {

        //validate first step
        if(currentIndex===0){
            if($('#mecsim_source_selectFactory option:selected').attr('requireAgent')==="true" && SourcePanel.settings.dom.selectAgentProgram.val()===null)
                return false;
        }

        //validate second step
        if(currentIndex===1){
            var generatorInput1 = Number(SourcePanel.settings.dom.generatorInputCarcount.val());
            var generatorInput2 = Number(SourcePanel.settings.dom.generatorInput2.val());
            var generatorInput3 = Number(SourcePanel.settings.dom.generatorInput3.val());

            if( isNaN(generatorInput1) || isNaN(generatorInput2) || isNaN(generatorInput3) || generatorInput1 <= 0)
                return false;

            if(SourcePanel.settings.dom.selectGenerator.val() === "uniform distribution" || SourcePanel.settings.dom.selectGenerator.val() === "Gleichverteilung"){
                if( (generatorInput2 >= generatorInput3) || (generatorInput2 < 0) )
                    return false;
            }else{
                if( generatorInput2 < generatorInput3){
                    return false;
                }
            }
        }

        return true;
    };

    //method to finish the last wizard step
    px_module.finishWizard = function(){
        $.ajax({
            url     : "/cwaypointenvironment/createtool",
            data    : {
                        "factory"           : SourcePanel.settings.dom.selectFactory.val(),
                        "agentprogram"      : SourcePanel.settings.dom.selectAgentProgram.val(),
                        "generator"         : SourcePanel.settings.dom.selectGenerator.val(),
                        "generatorInput1"   : SourcePanel.settings.dom.generatorInputCarcount.val(),
                        "generatorInput2"   : SourcePanel.settings.dom.generatorInput2.val(),
                        "generatorInput3"   : SourcePanel.settings.dom.generatorInput3.val(),
                        "name"              : SourcePanel.settings.dom.toolName.val(),
                        "r"                 : SourcePanel.settings.obj.colorpicker.spectrum("get")._r,
                        "g"                 : SourcePanel.settings.obj.colorpicker.spectrum("get")._g,
                        "b"                 : SourcePanel.settings.obj.colorpicker.spectrum("get")._b
                    },
            success : function( data ){
                $.each( data, function( pc_key, px_value ) {

                    $("</p>").appendTo(SourcePanel.settings.dom.toolbox);

                    var button = $("<button></button>")
                        .text(pc_key)
                        .attr("class", "mecsim_global_accordeonbutton")
                        .attr("value",pc_key)
                        .button()
                        .on("click", {"toolname": pc_key}, SourcePanel.listenToolButton)
                        .appendTo(SourcePanel.settings.dom.toolbox);

                    $("<span></span>")
                        .css("background-color", "rgb("+ px_value.redValue +","+ px_value.greenValue +","+ px_value.blueValue +")")
                        .attr("class", "mecsim_source_toolIcon")
                        .prependTo(button);

                });
            }
        }).fail(function(){
            console.log("tool creation failed!");
        });
    };

    //method to update waypoint settings
    px_module.updateWaypointSettings = function(){
        if(SourcePanel.settings.dom.selectWaypointType.val() === "Auto Wegpunkt (Zufall)" || SourcePanel.settings.dom.selectWaypointType.val() === "random car waypoint"){
            SourcePanel.settings.dom.selectRadius.attr('disabled', false);
        }else{
            SourcePanel.settings.dom.selectRadius.attr('disabled', true);
        }
    };

    //method to update factory settings
    px_module.updateFactorySettings = function(){
        if($("#mecsim_source_selectFactory option:selected").attr("requireAgent") === "true"){
            SourcePanel.settings.dom.selectAgentProgram.attr('disabled', false);
        }else{
            SourcePanel.settings.dom.selectAgentProgram.attr('disabled', true);
        }
    };

    //method to update generator settings
    px_module.updateGeneratorSettings = function(){

        SourcePanel.updateLabels(
            SourcePanel.settings.dom.selectGenerator.val(),
            [
                {
                    expected: ["uniform distribution", "Gleichverteilung"],
                    config : [
                        { element : SourcePanel.settings.dom.label.generatorinput2label, text   : SourcePanel.settings.labels.selectyourlowerbound },
                        { element : SourcePanel.settings.dom.label.generatorinput3label, text   : SourcePanel.settings.labels.selectyourupperbound },
                        { element : SourcePanel.settings.dom.generatorInput2, value  : 1 },
                        { element : SourcePanel.settings.dom.generatorInput3, value  : 3 }
                    ]
                },
                {
                    expected: ["default"],
                    config : [
                        { element : SourcePanel.settings.dom.label.generatorinput2label, text  : SourcePanel.settings.labels.selectyourmean },
                        { element : SourcePanel.settings.dom.label.generatorinput3label, text  : SourcePanel.settings.labels.selectyourdeviation },
                        { element : SourcePanel.settings.dom.generatorInput2, value  : 5 },
                        { element : SourcePanel.settings.dom.generatorInput3, value  : 1 }
                    ]
                }
            ]
        );
    };

    //generic method to upate labels
    px_module.updateLabels = function(checkElement, options){
        checkElement = SourcePanel.settings.dom.selectGenerator.val();

        var foundflag = false;

        options.forEach(function(p_option){
            if(foundflag)
                return;

            p_option.expected.forEach(function(p_expected){
                if(checkElement === p_expected || p_expected === "default"){
                    p_option.config.forEach(function(entry){
                        if(entry.text !== undefined && entry.text !== null)
                            entry.element.text(entry.text);
                        if(entry.value !== undefined && entry.value !== null)
                            entry.element.val(entry.value);
                    });
                    foundflag = true;
                }
            });
        });
    };

    return px_module;

}(SourcePanel || {}));
