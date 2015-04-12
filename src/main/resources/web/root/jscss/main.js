$(document).ready(function(){

    // global variables
    var form, dialog;

    /* GLOBAL-FUNCTIONS ******************************************************************/

        $('#jqxSplitter').jqxSplitter({ width: '100%', height: '100%', panels: [{ size: '20%', min: 250 }, { size: '80%'}] });

        // apply selected language to text elements
        // TODO: i18n
        /*$("#mecsim_preferences").append(
            $.post(
                "cinternationalization/translate",
                { "name" : "preferences" }
            )
        );*/

        // TODO: add button text according to selected language
        /*$('#mecsim_user_documentation').text(function() {
            $.post(
                ""
            );
        });*/

        /**
         * call to set template links
         * @see http://www.html5rocks.com/en/tutorials/webcomponents/imports/?redirect_from_locale=de
        **/

        $("a.template").click(function( p_event ){
            p_event.preventDefault();
            $("#mecsim_content").load( this.href );
        });

        $("a.template_button").button().click(function( p_event ){
                p_event.preventDefault();
                $("#mecsim_content").load( this.href );
        });

        // reload default page
        $("a.mecsim_home").button().click(function( p_event ){
            p_event.preventDefault();
            location.reload();
        });

        $( "#mecsim_accordion" ).accordion({
            active: false,
            collapsible: true
        });

    /*************************************************************************************/


    /* ACCORDION-FILE-CONTENT ************************************************************/

        $("#root_menu").menu();

    /*************************************************************************************/


    /* ACCORDION-SIMULATION-CONTENT ******************************************************/

        $("#simulation_menu").menu();

        $( "#mecsim_start_button" ).click(function() {
            $.post(
                "csimulation/start"
            ).fail( function( p_data )
                {
                    console.log(p_data);
                    $("#mecsim_start_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_start_error").dialog();
                } );
        });

        $( "#mecsim_stop_button" ).click(function() {
            $.post(
                "csimulation/stop"
            ).fail( function( p_data )
                {
                    console.log(p_data);
                    $("#mecsim_stop_error_text").text(p_data.responseJSON.error);
                    $("#mecsim_stop_error").dialog();
                } );
        });

        $( "#mecsim_reset_button" ).click(function() {
            $.post(
                "csimulation/reset"
            );
        });

        $( "#mecsim_speed_slider" ).slider({
            range: "min",
            value: 1,
            min: 0,
            max: 10,
            slide: function( event, ui ) {
                $( "#speed" ).val( ui.value );
            }
        });
        $( "#speed" ).val( $( "#mecsim_speed_slider" ).slider( "value" ) );

    /*************************************************************************************/


    /* ACCORDION-SOURCE-CONTENT **********************************************************/
    /*************************************************************************************/


    /* ACCORDION-EDITOR-CONTENT **********************************************************/

        $("#mecsim_load_asl").button();
        $("#mecsim_delete_asl").button();
        $("#mecsim_save_asl").button();
        $("#mecsim_agent_files").selectmenu();
        $("#mecsim_new_asl").button();

        // load existing asl files
        load_asl_files();
        function load_asl_files()
        {
            $.getJSON( "cagentenvironment/jason/list", function( p_data ) {
            $("#mecsim_agent_files").empty();
            for(var i in p_data.agents){
                $("#mecsim_agent_files")
                    .append( $("<option></option>")
                    .attr("value",p_data.agents[i])
                    .text(p_data.agents[i]));
            }
                $("#mecsim_agent_files option:first").attr('selected', true);
                $('#mecsim_agent_files').selectmenu('refresh', true);
            });
        }

        // form to create new asl file
        dialog = $("#mecsim_create_asl_form").dialog({
            autoOpen: false,
            buttons: {
                "Create": create_new_asl,
                    Cancel: function() {
                      dialog.dialog( "close" );
                    }
                }
        });

        $("#mecsim_new_asl").click(function() {
            dialog.dialog("open");
        });

        function create_new_asl(){
            if( $("#new_asl").val() )
            {
                $.post(
                    "cagentenvironment/jason/create",
                    { "name" : $("#new_asl").val() }
                ).done(function() {
                    load_asl_files();
                    dialog.dialog("close");
                });
            }
            else
            {
                alert("Please enter a file name");
            }

        }

        // delete asl file
        $("#mecsim_delete_asl").click(function(){
            $.post(
                "cagentenvironment/jason/delete",
                { "name" : $("#mecsim_agent_files").val() }
            ).done(function() {
                load_asl_files();
            });
        });

    /*************************************************************************************/


    /* ACCORDION-STATISTICS-CONTENT ******************************************************/
    /*************************************************************************************/


    /* ACCORDION-GETTING-HELP-CONTENT ****************************************************/

        $("#help_menu").menu();

    /*************************************************************************************/

});