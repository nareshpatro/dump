/* --------------------------------------
    File Name index.js
    Author Carmen Ng (PCCW)
    Date September ‎22, ‎2017
    Description
    All functions used uniquely in the dashboard page.

     ---------- Modification History ----------
	20171127 by Carmen Ng
	Added ignore empty error message for ajax abort
	 
	20171121 by Carmen Ng
	Dashboard date range from limit to validation and auto change
	
	20171116 by Carmen Ng
	Fixed ajax call error message
	 
	20171115 by Carmen Ng
	Fixed date time picker min and max not clearing when one date was blank
	 
    20171027 by Carmen Ng
    Working on and clickvaluelink functions to link back to search work order

    20171026 by Carmen Ng
    Added IT asset/Non IT asset checking for labels, buttons, modules

    20170922 by Carmen Ng
    Initial version
   -------------------------------------- -*/

var savedModulesArray = [];
var modulesArray = [];
var modulesLoaded = false;
var loaded = 0;

var savedSettingsCache;

var cmChart1;
var cmChart2;
var pmChart1;
var pmChart2;
var woTrend;

var trendMonths = [11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0];

$(document).ready(function(){
    loadCommon("glyphicon-dashboard", "Dashboard", "");
    //custom subtitle style
    $('#pageSubtitle').html("<span style='color:red'>Tips: You can review and setup your dashboard settings under 'Edit Preferences'</span>");
    
    page = 'sidebarDashboard';
            
    if($('#eamOrg option').length == 2){
		$('.CMDateHint').html("Max date range is 1 year.");
		$('.PMDateHint').html("Max date range is 1 year.");
		$('#creationFrom').val(moment().subtract(CMDateDefault, 'M').format("DD/MM/YYYY"));
	    $('#creationTo').val(moment().format("DD/MM/YYYY"));           
	    $('#scheduleFrom').val(moment().subtract(PMDateDefault, 'M').format("DD/MM/YYYY"));
	    $('#scheduleTo').val(moment().add(PMDateDefault, 'M').format("DD/MM/YYYY"));
	}
	else{
		$('.CMDateHint').html("Max date range is " + corpDateRangeMax + " months.");
		$('.PMDateHint').html("Max date range is " + corpDateRangeMax + " months.");
		$('#creationFrom').val(moment().subtract(corpCMDateDefault, 'M').format("DD/MM/YYYY"));
	    $('#creationTo').val(moment().format("DD/MM/YYYY"));           
	    $('#scheduleFrom').val(moment().subtract(corpPMDateDefault, 'M').format("DD/MM/YYYY"));
	    $('#scheduleTo').val(moment().add(corpPMDateDefault, 'M').format("DD/MM/YYYY"));
	}
    
    //Initialisation
    $('.toggles').bootstrapToggle();
        
    $("#wrStat").on("click", function(){
    	clickValueLink(1, 3, $('#wrStat').html());
    });
    
    $("#cmWoStat").on("click", function(){
    	clickValueLink(3, "5", $('#cmWoStat').html());
    });
        
    //Button settings
    $("#closeAlert").on("click", function(){
        $("#userPreferenceAlert").hide();
    });
    
    $('#dashboardFilters').keypress(function(e) {
        if(e.which == 13) {
        	$("#userPreferenceAlert").hide();
        	$('#dashboardAlert').hide();
        	
            applyFilters();
        }
    });
    
    $("#applyDashboardFilter").click(function(){
    	$("#userPreferenceAlert").hide();
		$('#dashboardAlert').hide();
    	
    	applyFilters();
    });
    
    $("#viewSettingsBTN").click(function(){
        modulesArray = [];
        modulesArray = modulesArray.concat(savedModulesArray);
        processLoadedFilterSettings(savedSettingsCache, false);
    });

        //Module switches record on and off settings START
            $('#cm1Switch').change(function() {
                recordToArray(1);
            });

            $('#cm2Switch').change(function() {
                recordToArray(2);
            });

            $('#cm3Switch').change(function() {
                recordToArray(3);
            });

            $('#pm1Switch').change(function() {
                recordToArray(4);
            });

            $('#pm2Switch').change(function() {
                recordToArray(5);
            });

            $('#pm3Switch').change(function() {
                recordToArray(6);
            }); 
            
            $('#summarySwitch').change(function() {
                recordToArray(7);
            }); 
        //Module switches record on and off settings END
    
    $('#cancelSettingsBTN').click(function(){
    	$('#preferenceAlert').hide();
        turnOnOffModules();
        processLoadedFilterSettings(savedSettingsCache, false);
    });
    
     //Date picker settings START
    $('.dateFromExtra').datetimepicker({
        format: 'DD/MM/YYYY',
        minDate: '1900-01-01'
    });

    $('.dateToExtra').datetimepicker({
        format: 'DD/MM/YYYY',
        useCurrent: false,
        minDate: '1900-01-01'
    });
																																 
    //Date picker settings END
 
    $( "#maintenanceVendor" ).blur(function() {
    	if($("#maintenanceVendor.lovFieldSearchBtn").data("mouseDown") != true){
        	$('#hiddenMBody').val("");
        	if($('#maintenanceVendor').val() !== ""){
        		$.post("./LovLookup", { value: $(this).val().trim(), query: 0}, function(data){
        			if(data == "No Match"){
        				$( "#maintenanceVendor.lovFieldSearchBtn" ).trigger( "click" );
        				alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
            			$('#multipleAlert').show();
            		}
            		else {
        				$('#hiddenMBody').val(data);
            		}
            	});
            }
  	  	}
    });
});
//Load settings from database and save in session for easy access
function loadSettings(userId, respId, appRespId){
    $.ajax({
        type: 'POST',
        data :{
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "Dashboard Filters",
            "FUNCTION_CODE" : "DASHBOARD",
            "PAGE_CODE" : "PREFERENCE"
        },   
        url : './getUserPrefExt',
        dataType:'text',
        success : function(result) {
            if(result == "NO DATA"){
            	if($('#eamOrg option').length == 2){
                    var defaultSettings = '[{"name":"wrWoSaved","value":"' + userId + '"},{"name":"eamOrgSaved","value":"' + $("#eamOrg option:eq(1)").val() + '"},{"name":"owningDeptSaved","value":""},{"name":"assetLocationSaved","value":""}]';
            	}
            	else{
                    var defaultSettings = '[{"name":"wrWoSaved","value":"' + userId + '"},{"name":"eamOrgSaved","value":""},{"name":"owningDeptSaved","value":""},{"name":"assetLocationSaved","value":""}]';
            	}
            	
                processLoadedFilterSettings(defaultSettings, true);
                
                $.ajax({
                    type: 'POST',
                    data : {
                        "USER_ID" : userId,
                        "RESP_ID" : respId,
                        "APP_ID" : appRespId,
                        "PREF_NAME" : "Dashboard Filters",
                        "PREF_VALUE" : defaultSettings,
                        "FUNCTION_CODE" : "DASHBOARD",
                        "PAGE_CODE" : "PREFERENCE"
                    },    
                    url : './saveUserPrefExt',
                    dataType:'text',
                    success : function(result) {
                    	loaded = loaded + 1;
                    	if(loaded == 2){
                    		applySavedSettings();
                    	}
                    },
                    error : function(xhr, ajaxOptions, thrownError) {
                    	if (xhr.statusText =='abort' || thrownError == "") {

                            return;
                        }
                    }
                });
                
            } else {
                processLoadedFilterSettings(result, true);
                loaded = loaded + 1;
            	if(loaded == 2){
            		applySavedSettings();
            	}
            }
        },
        error : function(xhr, ajaxOptions, thrownError) {
            if (xhr.statusText =='abort' || thrownError == "") {

                return;
            }
        }
    });
    
    $.ajax({
        type: 'POST',
        data : {
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "Dashboard Modules",
            "FUNCTION_CODE" : "DASHBOARD",
            "PAGE_CODE" : "PREFERENCE"
        },   
        url : './getUserPrefExt',
        dataType:'text',
        success : function(result) {
            
            if(result == "NO DATA"){
                savedModulesArray = [1, 2, 3, 4, 5, 6];
                modulesArray = [1, 2, 3, 4, 5, 6];
                
                $.ajax({
                    type: 'POST',
                    data : {
                        "USER_ID" : userId,
                        "RESP_ID" : respId,
                        "APP_ID" : appRespId,
                        "PREF_NAME" : "Dashboard Modules",
                        "PREF_VALUE" : JSON.stringify(modulesArray),
                        "FUNCTION_CODE" : "DASHBOARD",
                        "PAGE_CODE" : "PREFERENCE"
                    },   
                    url : './saveUserPrefExt',
                    dataType:'text',
                    success : function(result) {
                    	loaded = loaded + 1;
                    	if(loaded == 2){
                    		applySavedSettings();
                    	}
                    },
                    error : function(xhr, ajaxOptions, thrownError) {
                    	if (xhr.statusText =='abort' || thrownError == "") {

                            return;
                        }
                    }
                });
            }
            else {
                var parsed = JSON.parse(result);
                savedModulesArray = [];
                modulesArray = [];

                for(var i in parsed){
                	if (parsed.hasOwnProperty(i)){
	                    savedModulesArray.push(parsed[i]);
	                    modulesArray.push(parsed[i]);
                	}
                }
                loaded = loaded + 1;
            	if(loaded == 2){
            		applySavedSettings();
            	}
            }
            
            modulesLoaded = true;
            
        },
        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {

                return;
            }
        }
    });
}

//Save settings to database and session
function saveSettings(userId, respId, appRespId){
    $.ajax({
        type: 'POST',
        data : {
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "Dashboard Filters",
            "PREF_VALUE" : JSON.stringify($('#saveDashboardFiltersForm').serializeArray()),
            "FUNCTION_CODE" : "DASHBOARD",
            "PAGE_CODE" : "PREFERENCE"
        },    
        url : './saveUserPrefExt',
        dataType:'text',
        success : function(result) {
        	savedSettingsCache = JSON.stringify($('#saveDashboardFiltersForm').serializeArray());
        },
        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {

                return;
            }
        }
    });
    
 
    $.ajax({
        type: 'POST',
        data : {
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "Dashboard Modules",
            "PREF_VALUE" : JSON.stringify(modulesArray),
            "FUNCTION_CODE" : "DASHBOARD",
            "PAGE_CODE" : "PREFERENCE"
        },   
        url : './saveUserPrefExt',
        dataType:'text',
        success : function(result) {
            savedModulesArray = [];
            savedModulesArray = savedModulesArray.concat(modulesArray);
            
            applySavedSettings();
            $('#viewSettings').modal('hide'); 
            $('#userPreferenceAlert').show();
        },
        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {

                return;
            }
        }
    });
}

function processLoadedFilterSettings(result, restore){
	savedSettingsCache = result;	
    var results = JSON.parse(result);
    $('#wrWoSaved').prop('checked', false);
    if(restore){
        $('#wrWo').prop('checked', false);
    }
    var name = "";
    jQuery.each(results, function( i, result ) {
        name = result.name;
       if(name == "eamOrgSaved"){
            $('#' + name).selectpicker('val', [result.value]);
            if(restore){
                $('#' + name.slice(0,-5)).selectpicker('val', [result.value]);
            }
       }
       else if(name == "wrWoSaved"){
           $('#' + name).prop('checked', true);
           if(restore){
        	   $('#' + name.slice(0, -5)).prop('checked', true);
           }
       }
       else{
            $('#' + name).val(result.value);
            if(restore){
                $('#' + name.slice(0,-5)).val(result.value);
            }
       }
    });
}

//Save and apply settings from preferences menu
function applySavedSettings(){
    turnOnOffModules();
    $('#eamOrg').selectpicker('val', $('#eamOrgSaved').val());
    $('#wrWo').prop('checked', $('#wrWoSaved').is(':checked'));
    $('#assetLocation').val($('#assetLocationSaved').val());
    $('#owningDept').val($('#owningDeptSaved').val());
    applyFilters();
}

//Used to record array of which modules are on and which are off.
function recordToArray(module){
    if(modulesLoaded == true){
        if(modulesArray.indexOf(module) == -1){
            modulesArray.push(module);
        }
        else{
            modulesArray.splice(modulesArray.indexOf(module), 1);
        }
    }
}

function checkHeadings(){
    if($('#cm1').css('display') == 'none' && $('#cm2').css('display') == 'none' && $('#cm3').css('display') == 'none'){
       $( "#cmHeading" ).hide();
    }
    else if(!$('#cm1').length &&  $('#cm2').css('display') == 'none' && $('#cm3').css('display') == 'none'){
    	$( "#cmHeading" ).hide();
    }
    else{
    	$( "#cmHeading" ).show();
    }
    
    if($('#pm1').css('display') == 'none' && $('#pm2').css('display') == 'none' && $('#pm3').css('display') == 'none'){
        $( "#pmHeading" ).hide();
    }
    else{
    	$( "#pmHeading" ).show();
    }
}

//Takes the settings applied and turns modules and headings on and off appropriately
function turnOnOffModules(){
	modulesLoaded = false;
    for (i = 1; i < 4; i++) { 
        if(savedModulesArray.indexOf(i) !== -1){
            $('#cm' + i).show();
            $('#cm' + i + 'Switch').bootstrapToggle('on');
        }
        else {
            $('#cm' + i ).hide();
            $('#cm' + i + 'Switch').bootstrapToggle('off');
        }
    }
    
    for (i = 0; i < 4; i++) { 
        j = i + 3;
        if(savedModulesArray.indexOf(j) !== -1){
            $('#pm' + i).show();
            $('#pm' + i + 'Switch').bootstrapToggle('on');
        }
        else {
            $('#pm' + i ).hide();
            $('#pm' + i + 'Switch').bootstrapToggle('off');
        }
    }
    
    checkHeadings();
    modulesLoaded = true;
}

//Links within the tables submit settings, passes the appropriate values to search work request
function clickValueLink(module, value, records){
	setCookie('overMax', records, 60000);
	
   if (module === 1){
       $('#hiddenWrStatus').val(value);
       $('#hiddenStatusType').val("10");
       $('#dashboardFiltersForm').attr('action', 'searchWorkRequest');
       //Clear Unused values
       $('#hiddenScheduleFrom').val("");
       $('#hiddenScheduleTo ').val("");
       $('#dashboardFiltersForm').submit();
   }
   else if (module === 2){
       $('#hiddenWoStatus').val(value);
       $('#hiddenStatusType').val("10");
       //Clear PM values
       $('#hiddenScheduleFrom').val("");
       $('#hiddenScheduleTo ').val("");
       $('#dashboardFiltersForm').submit();
   }
   else if (module === 3){
	   var dateRange = value.charAt(0); 
	   if(value.length == 1){
		   var exception = "exception";
	   }
	   else{
		   var exception = value.substring(1, value.length);
	   }
	   
	   
	   $('#hiddenWoStatus').val(exception);
       $('#hiddenStatusType').val("10");
       var v_today = new Date();
       var v_from_cdate = $.datepicker.parseDate("dd/mm/yy", $('#hiddenCreationFrom').val());
       var v_to_cdate = $.datepicker.parseDate("dd/mm/yy", $('#hiddenCreationTo').val());
       var v_from;
       var v_to;       
       
       if(dateRange == "4"){ //>=22
    	   if(exception == "chart2"){
    		   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-8);
    	   }
    	   else{
    		   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-22);
    	   }
    	   
    	   
    	   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', v_from_cdate) );
    	   
    	   if (v_to_cdate<v_to){
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to_cdate) );
    	   }else{
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to) );
    	   }                       
       }
       else if(dateRange == "3"){ //15 to 21
    	   if(exception == "chart2"){
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-7);
        	   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-4);
    	   }
    	   else{
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-21);
        	   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-15);
    	   }
    	   
    	   
    	   if (v_from_cdate<=v_from){
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', v_from) );
    	   }else{
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', v_from_cdate) );
    	   }
    	   if (v_to_cdate<v_to){
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to_cdate) );
    	   }else{
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to) );
    	   }             
       }
       else if(dateRange == "2"){ //8 to 14
    	   if(exception == "chart2"){
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-3);
        	   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-3);
    	   }
    	   else{
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-14);
        	   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-8);
    	   }
    	   
    	   
    	   if (v_from_cdate<=v_from){
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', v_from) );
    	   }else{
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', v_from_cdate) );
    	   }
    	   if (v_to_cdate<v_to){
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to_cdate) );
    	   }else{
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to) );
    	   }        
       }
       else if(dateRange == "1"){ //4 to 7 days
    	   if(exception == "chart2"){
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-2);
        	   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-2);
    	   }
    	   else{
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-7);
        	   v_to = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-4);
    	   }
    	   
    	   
    	   if (v_from_cdate<=v_from){
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', v_from) );
    	   }else{
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', v_from_cdate) );
    	   }
    	   if (v_to_cdate<v_to){
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to_cdate) );
    	   }else{
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to) );
    	   }
       }
       else if(dateRange == "0"){//0 to 3 days
    	   if(exception == "chart2"){
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-1);
        	   v_to = v_today;
    	   }
    	   else{
    		   v_from = new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate()-3);
        	   v_to = v_today;
    	   }
    	   
    	   
    	   if (v_from_cdate<=v_from){
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', new Date(v_from.getFullYear(),v_from.getMonth(), v_from.getDate())) );
    	   }else{
    		   $('#hiddenCreationFrom').val($.datepicker.formatDate('dd/mm/yy', new Date(v_from_cdate.getFullYear(),v_from_cdate.getMonth(), v_from_cdate.getDate())) );
    	   }
    	   if (v_to_cdate<v_to){
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to_cdate) );
    	   }else{
    		   $('#hiddenCreationTo').val($.datepicker.formatDate('dd/mm/yy', v_to) );
    	   }
       } 
       //Clear PM values
       $('#hiddenScheduleFrom').val("");
       $('#hiddenScheduleTo').val("");
       $('#dashboardFiltersForm').submit();
   }
   else if (module === 4){
	   var v_today = new Date();
       $('#hiddenStatusType').attr('value', "20");
       if(value == "Overdue*"){
    	   $('#hiddenPmSummary').val(value);
    	   $('#hiddenWoStatus').val("PMException"); 	
    	   
    	   var v_scheduleTo = moment($('#scheduleTo').val(), 'DD/MM/YYYY');
    	   var v_now = moment().add(-1, 'days');

    	   if (v_scheduleTo.diff(v_now) >= 0){
    		   $('#hiddenScheduleTo').val(v_now.format('DD/MM/YYYY'));
    	   } else {
    		   $('#hiddenScheduleTo').val($('#scheduleTo').attr('value'));
    	   }
       }
       else if(value == "Completed"){
    	   $('#hiddenWoStatus').val("PMComplete");
       }
       else if(value == "Upcoming*"){
    	   $('#hiddenPmSummary').val(value);
    	   $('#hiddenWoStatus').val("PMException");
		   $('#hiddenScheduleFrom').val($.datepicker.formatDate('dd/mm/yy', new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate())) );
       }
       else {
    	   //$('#hiddenWoStatus').attr('value', "");
    	   $('#hiddenWoStatus').val(value);
       }
       //Clear CM values
       $('#hiddenCreationFrom').val("");
       $('#hiddenCreationTo').val("");
       $('#dashboardFiltersForm').submit();
   }                 
   else if (module === 5){
       $('#hiddenWoStatus').val(value);
       $('#hiddenStatusType').val("20");
       //Clear CM values
       $('#hiddenCreationFrom').val("");
       $('#hiddenCreationTo').val("");
       $('#dashboardFiltersForm').submit();
   }
   else if (module === 6){
       $('#hiddenStatusType').val("20");
       
       var dateRange = value.charAt(0); 
	   if(value.length == 1){
		   var exception = "Outstanding";
	   }
	   else{
		   var exception = value.substring(1, value.length);
	   }
	   
	   $('#hiddenWoStatus').val(exception);
       
       var scheduleInputFrom = moment($('#scheduleFrom').val(), 'DD/MM/YYYY');
	   var scheduleInputTo = moment($('#scheduleTo').val(), 'DD/MM/YYYY');
       
	   if (dateRange == "0"){
		   $('#hiddenScheduleFrom').val(scheduleInputFrom.format("DD/MM/YYYY"));
		   if(scheduleInputTo < moment().subtract(15, 'days')){
    		   $('#hiddenScheduleTo').val(scheduleInputTo.format("DD/MM/YYYY"));
    	   }
    	   else{
               $('#hiddenScheduleTo').val(moment().subtract(15, 'days').format("DD/MM/YYYY"));   
    	   }   
       }
       else if(dateRange == "1"){
    	   if(scheduleInputFrom > moment().subtract(14, 'days')){
    		   $('#hiddenScheduleFrom').val(scheduleInputFrom.format("DD/MM/YYYY"));
    	   }
    	   else{
        	   $('#hiddenScheduleFrom').val(moment().subtract(14, 'days').format("DD/MM/YYYY"));
    	   }
    	   
    	   if(scheduleInputTo < moment().subtract(8, 'days')){
    		   $('#hiddenScheduleTo').val(scheduleInputTo.format("DD/MM/YYYY"));
    	   }
    	   else{
    		   $('#hiddenScheduleTo').val(moment().subtract(8, 'days').format("DD/MM/YYYY"));
    	   }                  
       }
       else if(dateRange == "2"){
    	   if(scheduleInputFrom > moment().subtract(7, 'days')){
    		   $('#hiddenScheduleFrom').val(scheduleInputFrom.format("DD/MM/YYYY"));
    	   }
    	   else{
    		   $('#hiddenScheduleFrom').val(moment().subtract(7, 'days').format("DD/MM/YYYY"));
    	   }
    	   
    	   if(scheduleInputTo < moment()){
    		   $('#hiddenScheduleTo').val(scheduleInputTo.format("DD/MM/YYYY"));
    	   }
    	   else{
    		   $('#hiddenScheduleTo').val(moment().format("DD/MM/YYYY"));
    	   }    
       }
       else if(dateRange == "3"){
    	   if(scheduleInputFrom > moment()){
    		   $('#hiddenScheduleFrom').val(scheduleInputFrom.format("DD/MM/YYYY"));
    	   }
    	   else{
    		   $('#hiddenScheduleFrom').val(moment().format("DD/MM/YYYY"));
    	   }
    	   
    	   if(scheduleInputTo < moment()){
    		   $('#hiddenScheduleTo').val(scheduleInputTo.format("DD/MM/YYYY"));
    	   }
    	   else{
    		   $('#hiddenScheduleTo').val(moment().add(14, 'days').format("DD/MM/YYYY"));
    	   }  
       }
       else if(dateRange == "4"){
    	   if(scheduleInputFrom > moment().add(14, 'days')){
    		   $('#hiddenScheduleFrom').val(scheduleInputFrom.format("DD/MM/YYYY"));
    	   }
    	   else{
    		   $('#hiddenScheduleFrom').val(moment().add(14, 'days').format("DD/MM/YYYY")); 
    	   }    	  
           $('#hiddenScheduleTo').val(scheduleInputTo.format("DD/MM/YYYY"));
       }
       else {           
    	   $('#hiddenScheduleFrom').val(scheduleInputFrom.format("DD/MM/YYYY"));
    	   $('#hiddenScheduleTo').val(scheduleInputTo.format("DD/MM/YYYY"));
       }
       
        //Clear CM values
       $('#hiddenCreationFrom').val("");
       $('#hiddenCreationTo').val("");
       $('#dashboardFiltersForm').submit();
   }
   else if (module === 8){
	   var type = value.substring(0, 2);
	   var index = value.substring(2, value.length);
	  
	   if(type == "10"){
		   $('#hiddenWoStatus').val("breakdown");
	   }
	   else {
		   $('#hiddenWoStatus').val("");
	   }
       
       $('#hiddenStatusType').val(type);
       $('#hiddenScheduleFrom').val(moment().subtract(trendMonths[index], 'M').startOf('month').format("DD/MM/YYYY"));
       $('#hiddenScheduleTo ').val(moment().subtract(trendMonths[index], 'M').endOf('month').format("DD/MM/YYYY"));
       
       $('#hiddenCriticalMedEquip').val("");
       $('#hiddenMBodyPost').val("");
       $('#hiddenMaintenanceVendor').val("");
       $('#hiddenAssetRisk').val("");
       
       //Clear PM values
       $('#hiddenCreationFrom').val("");
       $('#hiddenCreationTo').val("");
       $('#dashboardFiltersForm').submit();
   }
}

// clickTop10ValueLink
function clickTop10ValueLink(value1, value2){
	var valueArray = $('#top10SearchCriteria').text().split(":");
	var value = valueArray[0];
	var module = valueArray[2];
	var v_today = new Date();
	
	if(module == "5") {
		$('#hiddenWoStatus').attr('value', value);
	} else {
	    if(value == "Overdue"){
	    	$('#hiddenPmSummary').attr('value', value);
	    	$('#hiddenWoStatus').attr('value', "PMException"); 	
	 	   
	    	var v_scheduleTo = moment($('#scheduleTo').val(), 'DD/MM/YYYY');
	    	var v_now = moment().add(-1, 'days');

	    	if (v_scheduleTo.diff(v_now) >= 0){
	    		$('#hiddenScheduleTo').attr('value', v_now.format('DD/MM/YYYY'));
	    	} else {
	    		$('#hiddenScheduleTo').attr('value', $('#scheduleTo').attr('value'));
	    	}
	    }
	    else if(value == "Completed"){
	    	$('#hiddenWoStatus').attr('value', "PMComplete");
	    }
	    else if(value == "Upcoming"){
	    	$('#hiddenPmSummary').attr('value', value);
	    	$('#hiddenWoStatus').attr('value', "PMException");
	    	$('#hiddenScheduleFrom').attr('value', $.datepicker.formatDate('dd/mm/yy', new Date(v_today.getFullYear(),v_today.getMonth(), v_today.getDate())) );
	    }
	    else {
	    	$('#hiddenWoStatus').attr('value', "");
	    }
	}
	
    $('#hiddenStatusType').attr('value', "20");
    $('#hiddenMaintenanceVendor').attr('value', value1);
    $('#hiddenMBodyPost').attr('value', value2);
    $('#dashboardFiltersForm').attr('action', 'searchWorkOrder');
    //Clear CM values
    $('#hiddenCreationFrom').attr('value', "");
    $('#hiddenCreationTo').attr('value', "");
    $('#dashboardFiltersForm').submit();
}

//Apply the filters to the hidden form
function applyFilters(){
	if($('#eamOrg option').length == 2){
		if((moment($('#creationTo').val(), "DD/MM/YYYY") > moment($('#creationFrom').val(), "DD/MM/YYYY").add(1, 'y')) || (moment($('#scheduleTo').val(), "DD/MM/YYYY") > moment($('#scheduleFrom').val(), "DD/MM/YYYY").add(1, 'y'))){
			alertMessage("dashboardAlert", "HA_ERROR_DATEOUTOFRANGE");
			return;
		}
	}
	else{
		if((moment($('#creationTo').val(), "DD/MM/YYYY") > moment($('#creationFrom').val(), "DD/MM/YYYY").add(corpDateRangeMax, 'M')) || (moment($('#scheduleTo').val(), "DD/MM/YYYY") > moment($('#scheduleFrom').val(), "DD/MM/YYYY").add(corpDateRangeMax, 'M'))){
			alertMessage("dashboardAlert", "HA_ERROR_DATEOUTOFRANGE");
			return;
		}
	}
	
    //Close search table panel
    $('#filterTitle').addClass("collapsed");
    $('#dashboardFilters').removeClass("in");
    //Disable button to avoid multiple submits
    $('#applyDashboardFilter').button('loading');
    $('#applyDashboardFilter').attr("disabled", true);
    //Common Filters
    $('#hiddenCriticalMedEquip').attr('value', $('#criticalMedEquip').is(':checked'));
    $('#hiddenEamOrg').attr('value', $('#eamOrg').val());
    $('#hiddenOwningDept').attr('value', $('#owningDept').val());
    $('#hiddenAssetLocation').attr('value', $('#assetLocation').val());
    $('#hiddenMaintenanceVendor').attr('value', $('#maintenanceVendor').val());
    $('#hiddenMBodyPost').attr('value', $('#hiddenMBody').val());
    //CM Filters
    var onlyMine = $('#wrWo').val();
    if(!$('#wrWo').is(':checked')){
    	onlyMine = "";
    }
    $('#hiddenWrWo').attr('value', onlyMine);
        
    if(!$('#wrWo').is(':checked')){
    	$('#hiddenWrWo').attr('value', "");
    }
    
    $('#hiddenCreationFrom').attr('value', $('#creationFrom').val());
    $('#hiddenCreationTo').attr('value', $('#creationTo').val());
    //PM Filters
    $('#hiddenAssetRisk').attr('value', $('#assetRisk').val());
    $('#hiddenScheduleFrom').attr('value', $('#scheduleFrom').val());
    $('#hiddenScheduleTo ').attr('value', $('#scheduleTo').val());
    
    applyToModules();
}

//Apply the paramters to each of the modules
function applyToModules(){
    var search = {};
    search["eamOrg"] = $('#hiddenEamOrg').val();
    search["statusType"] = $('#statusType').val();
    search["dashboardFor"] = $('#hiddenWrWo').val();
    search["owningDept"] =  $('#hiddenOwningDept').val().trim();
    search["assetLocation"] = $('#hiddenAssetLocation').val().trim();
    search["criticalMedEquip"] =  $('#hiddenCriticalMedEquip').val();
    search["creationFrom"] = $('#hiddenCreationFrom').val();
    search["creationTo"] = $('#hiddenCreationTo').val();
    search["mBody"] = $('#hiddenMBodyPost').val().trim();
    search["riskLevel"] = $('#hiddenAssetRisk').val();
    search["scheduleFrom"] = $('#hiddenScheduleFrom').val();
    search["scheduleTo"] = $('#hiddenScheduleTo').val();
    search["maintenanceVendor"] = $('#hiddenMBody').val();
//    search["criticalOnly"] = $('#criticalOnly').prop("checked"); 
    
    $('#wrStatWrapper').hide();
	$('#wrStatLoading').show();
	$('#cmWoStatWrapper').hide();
  	$('#cmWoStatLoading').show();
  	$('#cmChart1').hide();
  	$('#cmChart1Loading').show();
  	$('#cmChart2').hide();
  	$('#cmChart2Loading').show();
  	$('#pmChart1').hide();
  	$('#pmChart1Loading').show();
  	$('#pmChart2').hide();
  	$('#pmChart2Loading').show();
  	$('#woTrend').hide();
  	$('#woTrendLoading').show();  	
    
    $('#wrSummary').hide();
	$('#wrSummaryLoading').show();
	$('#woSummary').hide();
	$('#woSummaryLoading').show();
	$('#outstandingWo').hide();
	$('#outstandingWoLoading').show();
	$('#pmSummary').hide();
	$('#pmSummaryLoading').show();
	$('#statusSummary').hide();
	$('#statusSummaryLoading').show();
	$('#upcomingWO').hide();
	$('#upcomingWOLoading').show();
	$('#wrSummary').bootstrapTable('destroy');
	$('#wrSummary').bootstrapTable();
	$('#woSummary').bootstrapTable('destroy');
	$('#woSummary').bootstrapTable();
	$('#outstandingWo').bootstrapTable('destroy');
	$('#outstandingWo').bootstrapTable();
	$('#pmSummary').bootstrapTable('destroy');
	$('#pmSummary').bootstrapTable();
	$('#statusSummary').bootstrapTable('destroy');
	$('#statusSummary').bootstrapTable();
	$('#upcomingWO').bootstrapTable('destroy');
	$('#upcomingWO').bootstrapTable();
	
	callWOTrend(search);
	
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=1',                        
        dataType:'json',
        success : function(data1) {
	      	  $('#wrStat').html(data1[0].wrstatuscount);
	      	  $('#wrStatWrapper').show();
	      	  $('#wrStatLoading').hide();
        	  $('#wrSummary').bootstrapTable('load', data1);
        	  $('#wrSummary').show();
        	  $('#wrSummaryLoading').hide();
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });
//    alert('Call WO');
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=2',                        
        dataType:'json',
        success : function(data1) {
//        	alert('WO data1 ' + data1);
        	  $('#woSummary').bootstrapTable('load', data1);
        	  $('#woSummary').show();
        	  $('#woSummaryLoading').hide();
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });
    
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=3',                        
        dataType:'json',
        success : function(data) {
        	$('#cmWoStat').html(data[5].outstandingcount);
        	$('#cmWoStatWrapper').show();
	      	$('#cmWoStatLoading').hide();
        	$('#outstandingWo').bootstrapTable('load', data);
        	$('#outstandingWo').show();
        	$('#outstandingWoLoading').hide();
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });
    
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=3&chart=1',                        
        dataType:'json',
        success : function(data) {
        	drawCmChart1(data);
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });
    
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=3&chart=2',                        
        dataType:'json',
        success : function(data) {
        	drawCmChart2(data);
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });
    
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=4',                        
        dataType:'json',
        success : function(data) {
        	$('#pmSummary').bootstrapTable('load', data);
        	$('#pmSummary').show();
        	$('#pmSummaryLoading').hide();
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });
    
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=5',                        
        dataType:'json',
        success : function(data1) {
        	  $('#statusSummary').bootstrapTable('load', data1);
        	  $('#statusSummary').show();
        	  $('#statusSummaryLoading').hide();
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });
    
    
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=6',                        
        dataType:'json',
        success : function(data) {
        	  if($('#pmChart1').length){
        		drawPmChart1(data);
        	  }
        	  $('#upcomingWO').bootstrapTable('load', data);
        	  $('#upcomingWO').show();
        	  $('#upcomingWOLoading').hide();
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    }); 
    
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=6&chart=true',                        
        dataType:'json',
        success : function(data) {
	    	  if($('#pmChart2').length){
	    		drawPmChart2(data);
	    	  }
        },
        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
        }
    });     
        
    //When everything has been loaded, activate the buttons again.
    $(document).ajaxStop(function () {
      $('#applyDashboardFilter').button('reset');
      $('#applyDashboardFilter').attr("disabled", false);
    });
}

//Get the dashboard parameters to apply to tables
function dashboardParams() {
    return {
        dashboardFor: $('#hiddenWrWo').attr('value'),
        eamOrg: $('#hiddenEamOrg').attr('value'),
        owningDept: $('#hiddenOwningDept').attr('value'),
        assetLocation: $('#hiddenAssetLocation').attr('value'),
        criticalMedEquip: $('#hiddenCriticalMedEquip').attr('value'),
        creationFrom: $('#hiddenCreationFrom').attr('value'),
        creationTo: $('#hiddenCreationTo').attr('value'),
        mBody: $('#hiddenMBody').attr('value'),
        assetRisk: $('#hiddenAssetRisk').attr('value'),
        scheduleFrom: $('#hiddenScheduleFrom').attr('value'),
        scheduleTo: $('#hiddenScheduleTo').attr('value')
    };
}

///Styling for top 10 button cell
function top10Style(value, row, index, field){
    return {
        css: {"border-left": "none", "padding": "0px"}
    };
}

//Open the top 10 menu
function openTop10(title){
	var top10 = title.split(":");
    $('#top10').modal('show');
    $('#top4ModalTitle').text(top10[0] + " Work Orders");
    //$('#top10Titles').text(top10[0] + " PMs");
    $('#top10Titles').text("Top 10 Maintenance Body");
    $('#top10SearchCriteria').text(title);
    
    var search = {}
    search["eamOrg"] = $('#hiddenEamOrg').attr('value');
    search["statusType"] = $('#statusType').attr('value');
    
    search["dashboardFor"] = $('#hiddenWrWo').attr('value');
    search["owningDept"] =  $('#hiddenOwningDept').attr('value');
    search["assetLocation"] = $('#hiddenAssetLocation').attr('value');
    search["criticalMedEquip"] =  $('#hiddenCriticalMedEquip').attr('value');
    search["creationFrom"] = $('#hiddenCreationFrom').attr('value');
    search["creationTo"] = $('#hiddenCreationTo').attr('value');
    search["mBody"] = $('#hiddenMBodyPost').attr('value');
    search["riskLevel"] = $('#hiddenAssetRisk').val();
    search["scheduleFrom"] = $('#hiddenScheduleFrom').attr('value');
    search["scheduleTo"] = $('#hiddenScheduleTo').attr('value');
    search["maintenanceVendor"] = $('#hiddenMBody').val();
    
    if(top10[2] == "4") {
    	search["pmSummaryDesc"] = title;
    } else {
    	search["pmWostatus"] = title;
    }

    $('#top10Data').hide();
    $('#top10Data').bootstrapTable('destroy');
    $('#top10Data').bootstrapTable();
    $('#top10DataLoading').show();
    $.ajax({
        type: 'POST',   
        contentType: 'application/json',
        data : JSON.stringify(search), 
        url : './processDashBoard?module=7',                        
        dataType:'json',
        success : function(data1) {
        	  $('#top10Data').bootstrapTable('load', data1);
        	  $('#top10Data').show();
        	  $('#top10DataLoading').hide();
        },

        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
            $('#top10DataLoading').hide();
        }
    });
 }

function drawCmChart1(data){
	$('#cmChart1').show();
  	$('#cmChart1Loading').hide();
	
	var cmChart1Canvas = $('#cmChart1').get(0).getContext('2d');
	
	var cmChart1Options     = {
		layout: {
            padding: {
                left: 0,
                right: 47,
                top: 0,
                bottom: 0
            }
        },
		legend: {
			position: 'right',
            labels: {
            	fontSize: 11,
            	boxWidth: 11,
            	generateLabels: function(chart) {
                    var data = chart.data;
                    if (data.labels.length && data.datasets.length) {
                        return data.labels.map(function(label, i) {
                            var meta = chart.getDatasetMeta(0);
                            var ds = data.datasets[0];
                            var arc = meta.data[i];
                            var custom = arc && arc.custom || {};
                            var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
                            var arcOpts = chart.options.elements.arc;
                            var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
                            var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
                            var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

							// We get the value of the current label
							var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

                            return {
                                // Instead of `text: label,`
                                // We add the value to the string
                                text: label + " (" + value + ")",
                                fillStyle: fill,
                                strokeStyle: stroke,
                                lineWidth: bw,
                                hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
                                index: i
                            };
                        });
                    } else {
                        return [];
                    }
                },
            	filter: function(legendItem, data) {
   	             	return legendItem.index != 5;
            	}
            }
        },
        tooltips: {
        	enabled: true,
            callbacks: {
               afterBody: function(t, d) {
                  return 'Work Orders'; 
               }
            }
         }
	};
	
	var cmChart1Data = {
		    datasets: [{
		        data: [
		        	data[4].outstandingcount, 
		        	data[3].outstandingcount, 
		        	data[2].outstandingcount, 
		        	data[1].outstandingcount, 
		        	data[0].outstandingcount
		        ],
		        backgroundColor: [
		            '#dd4b39',
		            '#ff851b',
		            '#3c8dbc',
		            '#00c0ef',
		            '#00a65a'
		        ]
		    }],
		    labels: [
		    	'>21 Days',
		    	'14-21 Days',
		    	'7-14 Days',
		    	'3-7 Days',
		        '<=3 Days'
		    ]
		};
	
	if(data[4].outstandingcount == "0" && data[3].outstandingcount == "0" && data[2].outstandingcount == "0" && data[1].outstandingcount == "0" && data[0].outstandingcount == "0"){
		cmChart1Data.datasets[0].data.push("1");
		cmChart1Data.datasets[0].backgroundColor.push("#d2d6de");
		cmChart1Data.labels.push("No WOs");
		cmChart1Options.tooltips.enabled = false;
		$( "#cmChart1").unbind( "click" );
	}
	else{		
		$('#cmChart1').on("click", function(evt){
		      var activePoints = cmChart1.getElementsAtEvent(evt);
		      if (activePoints[0]) {
		    	  	var chartData = activePoints[0]['_chart'].config.data;
		    	  	var idx = activePoints[0]['_index'];
		    	  	var label = chartData.labels[idx];
		        	var value = chartData.datasets[0].data[idx];
			        
		        	var dateRange;
		        	switch (label) {
			            case '<=3 Days':
			            	dateRange = "0";
			                break;
			            case '3-7 Days':
			            	dateRange = "1";
			                break;
			            case '7-14 Days':
			            	dateRange = "2";
			                break;
			            case '14-21 Days':
			            	dateRange = "3";
			                break;
			            case '>21 Days':
			            	dateRange = "4";
			                break;
		        	}
		        	
		        	clickValueLink(3, dateRange+"chart1", value);
		      }
		});
	}
	
	cmChart1 = new Chart(cmChart1Canvas , {
        type: "doughnut",
        data: cmChart1Data, 
        options: cmChart1Options
    });
	
}

function drawCmChart2(data){
	$('#cmChart2').show();
  	$('#cmChart2Loading').hide();
	
	var cmChart2Canvas = $('#cmChart2').get(0).getContext('2d');
	
	var cmChart2Options     = {
		layout: {
            padding: {
                left: 0,
                right: 47,
                top: 0,
                bottom: 0
            }
        },
		legend: {
			position: 'right',
            labels: {
            	fontSize: 11,
            	boxWidth: 11,
            	generateLabels: function(chart) {
                    var data = chart.data;
                    if (data.labels.length && data.datasets.length) {
                        return data.labels.map(function(label, i) {
                            var meta = chart.getDatasetMeta(0);
                            var ds = data.datasets[0];
                            var arc = meta.data[i];
                            var custom = arc && arc.custom || {};
                            var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
                            var arcOpts = chart.options.elements.arc;
                            var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
                            var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
                            var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

							// We get the value of the current label
							var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

                            return {
                                // Instead of `text: label,`
                                // We add the value to the string
                                text: label + " (" + value + ")",
                                fillStyle: fill,
                                strokeStyle: stroke,
                                lineWidth: bw,
                                hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
                                index: i
                            };
                        });
                    } else {
                        return [];
                    }
                },
            	filter: function(legendItem, data) {
   	             	return legendItem.index != 5;
            	}
            }
        },
        tooltips: {
        	enabled: true,
            callbacks: {
               afterBody: function(t, d) {
                  return 'Work Orders'; 
               }
            }
         }
	};
	
	var cmChart2Data = {
		    datasets: [{
		        data: [
		        	data[4].outstandingcount, 
		        	data[3].outstandingcount, 
		        	data[2].outstandingcount, 
		        	data[1].outstandingcount, 
		        	data[0].outstandingcount
		        ],
		        backgroundColor: [
		            '#dd4b39',
		            '#ff851b',
		            '#3c8dbc',
		            '#00c0ef',
		            '#00a65a'
		        ]
		    }],
		    labels: [
		    	'>7 Days',
		    	'3-7 Days',
		    	'2-3 Days',
		    	'1-2 Days',
		        '<= 1 Day'
		    ]
		};
	
	if(data[4].outstandingcount == "0" && data[3].outstandingcount == "0" && data[2].outstandingcount == "0" && data[1].outstandingcount == "0" && data[0].outstandingcount == "0"){
		cmChart2Data.datasets[0].data.push("1");
		cmChart2Data.datasets[0].backgroundColor.push("#d2d6de");
		cmChart2Data.labels.push("No WOs");
		cmChart2Options.tooltips.enabled = false;
		$( "#cmChart2").unbind( "click" );
	}
	else{		
		$('#cmChart2').on("click", function(evt){
		      var activePoints = cmChart2.getElementsAtEvent(evt);
		      if (activePoints[0]) {
		    	  	var chartData = activePoints[0]['_chart'].config.data;
		    	  	var idx = activePoints[0]['_index'];
		    	  	var label = chartData.labels[idx];
		        	var value = chartData.datasets[0].data[idx];
			       
		        	var dateRange;
		        	switch (label) {
			            case '<= 1 Day':
			            	dateRange = "0";
			                break;
			            case '1-2 Days':
			            	dateRange = "1";
			                break;
			            case '2-3 Days':
			            	dateRange = "2";
			                break;
			            case '3-7 Days':
			            	dateRange = "3";
			                break;
			            case '>7 Days':
			            	dateRange = "4";
			                break;
		        	}
		        	
		        	clickValueLink(3, dateRange+"chart2", value);
		      }
		});
	}
	
	cmChart2 = new Chart(cmChart2Canvas , {
        type: "doughnut",
        data: cmChart2Data, 
        options: cmChart2Options
    });
}

function drawPmChart1(data){
	$('#pmChart1').show();
  	$('#pmChart1Loading').hide();
	
	var pmChart1Canvas = $('#pmChart1').get(0).getContext('2d');
	
	var pmChart1Options     = {
		legend: {
			position: 'right',
            labels: {
            	fontSize: 11,
            	boxWidth: 11,
            	generateLabels: function(chart) {
                    var data = chart.data;
                    if (data.labels.length && data.datasets.length) {
                        return data.labels.map(function(label, i) {
                            var meta = chart.getDatasetMeta(0);
                            var ds = data.datasets[0];
                            var arc = meta.data[i];
                            var custom = arc && arc.custom || {};
                            var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
                            var arcOpts = chart.options.elements.arc;
                            var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
                            var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
                            var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

							// We get the value of the current label
							var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

                            return {
                                // Instead of `text: label,`
                                // We add the value to the string
                                text: label + " (" + value + ")",
                                fillStyle: fill,
                                strokeStyle: stroke,
                                lineWidth: bw,
                                hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
                                index: i
                            };
                        });
                    } else {
                        return [];
                    }
                },
            	filter: function(legendItem, data) {
   	             	return legendItem.index != 5;
            	}
            }
        },
        tooltips: {
        	enabled: true,
            callbacks: {
               afterBody: function(t, d) {
                  return 'Work Orders'; 
               }
            }
         }
	};
	
	var pmChart1Data = {
		    datasets: [{
		        data: [
		        	data[0].upcomingWoCount, 
		        	data[1].upcomingWoCount, 
		        	data[2].upcomingWoCount, 
		        	data[3].upcomingWoCount, 
		        	data[4].upcomingWoCount
		        ],
		        backgroundColor: [
		            '#dd4b39',
		            '#ff851b',
		            '#3c8dbc',
		            '#00c0ef',
		            '#00a65a'
		        ]
		    }],
		    labels: [
		    	'Overdue > 14 Days',
		    	'Overdue 7-14 Days',
		    	'Overdue <= 7 Days',
		    	'Upcoming <= 14 Days',
		        'Upcoming > 14 Days'
		    ]
		};
	
	if(data[4].upcomingWoCount == "0" && data[3].upcomingWoCount == "0" && data[2].upcomingWoCount == "0" && data[1].upcomingWoCount == "0" && data[0].upcomingWoCount == "0"){
		pmChart1Data.datasets[0].data.push("1");
		pmChart1Data.datasets[0].backgroundColor.push("#d2d6de");
		pmChart1Data.labels.push("No WOs");
		pmChart1Options.tooltips.enabled = false;
		$( "#pmChart1").unbind( "click" );
	}
	else{		
		$('#pmChart1').on("click", function(evt){
		      var activePoints = pmChart1.getElementsAtEvent(evt);
		      if (activePoints[0]) {
		    	  	var chartData = activePoints[0]['_chart'].config.data;
		    	  	var idx = activePoints[0]['_index'];
		    	  	var label = chartData.labels[idx];
		        	var value = chartData.datasets[0].data[idx];
			        
		        	var dateRange;
		        	switch (label) {
			            case 'Overdue > 14 Days':
			            	dateRange = "0";
			                break;
			            case 'Overdue 7-14 Days':
			            	dateRange = "1";
			                break;
			            case 'Overdue <= 7 Days':
			            	dateRange = "2";
			                break;
			            case 'Upcoming <= 14 Days':
			            	dateRange = "3";
			                break;
			            case 'Upcoming > 14 Days':
			            	dateRange = "4";
			                break;
		        	}
		        	
		        	clickValueLink(6, dateRange, value);
		      }
		});
	}
	
	pmChart1 = new Chart(pmChart1Canvas , {
        type: "doughnut",
        data: pmChart1Data, 
        options: pmChart1Options
    });
	
}

function drawPmChart2(data){
	$('#pmChart2').show();
  	$('#pmChart2Loading').hide();
	
	var pmChart2Canvas = $('#pmChart2').get(0).getContext('2d');
	
	var pmChart2Options     = {
		legend: {
			position: 'right',
            labels: {
            	fontSize: 11,
            	boxWidth: 11,
            	generateLabels: function(chart) {
                    var data = chart.data;
                    if (data.labels.length && data.datasets.length) {
                        return data.labels.map(function(label, i) {
                            var meta = chart.getDatasetMeta(0);
                            var ds = data.datasets[0];
                            var arc = meta.data[i];
                            var custom = arc && arc.custom || {};
                            var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
                            var arcOpts = chart.options.elements.arc;
                            var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
                            var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
                            var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

							// We get the value of the current label
							var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

                            return {
                                // Instead of `text: label,`
                                // We add the value to the string
                                text: label + " (" + value + ")",
                                fillStyle: fill,
                                strokeStyle: stroke,
                                lineWidth: bw,
                                hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
                                index: i
                            };
                        });
                    } else {
                        return [];
                    }
                },
            	filter: function(legendItem, data) {
   	             	return legendItem.index != 5;
            	}
            }
        },
        tooltips: {
        	enabled: true,
            callbacks: {
               afterBody: function(t, d) {
                  return 'Work Orders'; 
               }
            }
         }
	};
	
	var pmChart2Data = {
			datasets: [{
		        data: [
		        	data[0].upcomingWoCount, 
		        	data[1].upcomingWoCount, 
		        	data[2].upcomingWoCount, 
		        	data[3].upcomingWoCount, 
		        	data[4].upcomingWoCount
		        ],
		        backgroundColor: [
		            '#dd4b39',
		            '#ff851b',
		            '#3c8dbc',
		            '#00c0ef',
		            '#00a65a'
		        ]
		    }],
		    labels: [
		    	'Overdue > 14 Days',
		    	'Overdue 7-14 Days',
		    	'Overdue <= 7 Days',
		    	'Upcoming <= 14 Days',
		        'Upcoming > 14 Days'
		    ]
		};
	
	if(data[4].upcomingWoCount == "0" && data[3].upcomingWoCount == "0" && data[2].upcomingWoCount == "0" && data[1].upcomingWoCount == "0" && data[0].upcomingWoCount == "0"){
		pmChart2Data.datasets[0].data.push("1");
		pmChart2Data.datasets[0].backgroundColor.push("#d2d6de");
		pmChart2Data.labels.push("No WOs");
		pmChart2Options.tooltips.enabled = false;
		$( "#pmChart2").unbind( "click" );
	}
	else{		
		$('#pmChart2').on("click", function(evt){
		      var activePoints = pmChart2.getElementsAtEvent(evt);
		      		      
		      if (activePoints[0]) {
		    	  	var chartData = activePoints[0]['_chart'].config.data;
		    	  	var idx = activePoints[0]['_index'];
		    	  	var label = chartData.labels[idx];
		        	var value = chartData.datasets[0].data[idx];
		        	
		        	var dateRange;
		        	switch (label) {
			            case 'Overdue > 14 Days':
			            	dateRange = "0";
			                break;
			            case 'Overdue 7-14 Days':
			            	dateRange = "1";
			                break;
			            case 'Overdue <= 7 Days':
			            	dateRange = "2";
			                break;
			            case 'Upcoming <= 14 Days':
			            	dateRange = "3";
			                break;
			            case 'Upcoming > 14 Days':
			            	dateRange = "4";
			                break;
		        	}
		        	
		        	clickValueLink(6, dateRange+"chart2", value);
		      }
		});
	}
	
	pmChart2 = new Chart(pmChart2Canvas , {
        type: "doughnut",
        data: pmChart2Data, 
        options: pmChart2Options
    });
}

function drawWoTrend(cm, pm){
	$('#woTrend').show();
  	$('#woTrendLoading').hide();
	
  	var count = 0;
	var trendLabels = [];
	var woTrendCanvas = $('#woTrend').get(0).getContext('2d');

	var legendDataset = [];
	
	var tempArray = {};
	var cmData = [];
	var pmData = [];
	
	for (var month in trendMonths) {
		trendLabels.push(moment().subtract(trendMonths[month], 'M').format('MMM').toUpperCase());		
	}
		
	for (var months in trendLabels) {
		if(count < cm.length){
			if(trendLabels[months] == cm[count].trendMonth){
				tempArray = {x:cm[count].trendMonth, y:cm[count].trendCount};
				cmData.push(tempArray);
				count++;
			}
			else{
				tempArray = {x:trendLabels[months], y:0};
				cmData.push(tempArray);
			}
		}
		else{
			tempArray = {x:trendLabels[months], y:0};
			cmData.push(tempArray);
		}
	}
		
	count = 0;
	if(pm){
		for (var months in trendLabels) {
			if(count < pm.length){
				if(trendLabels[months] == pm[count].trendMonth){
					tempArray = {x:pm[count].trendMonth, y:pm[count].trendCount};
					pmData.push(tempArray);
					count++;
				}
				else{
					tempArray = {x:trendLabels[months], y:0};
					pmData.push(tempArray);
				}
			}
			else{
				tempArray = {x:trendLabels[months], y:0};
				pmData.push(tempArray);
			}
		}
		
		legendDataset = [
		    {
		      label: 'CM by Breakdown Date',
		      backgroundColor: '#3c8dbc',
		      borderColor: '#3c8dbc',
		      data: cmData,
		      fill: false
		    },
		    {
		      label: 'PM by Schedule Date',
		      backgroundsColor: '#AFB2B8',
		      borderColor: '#AFB2B8',
		      data: pmData,
		      fill: false
		    }
		];
	}
	else{
		legendDataset = [
		    {
		      label: 'CM  by Breakdown Date',
		      backgroundColor: '#3c8dbc',
		      borderColor: '#3c8dbc',
		      data: cmData,
		      fill: false
		    }
		];
	}
	
	
	
	var woTrendData = {
	  labels  : trendLabels,
	  datasets: legendDataset
	};
	
	var woTrendOptions = {
		responsive: true,
		maintainAspectRatio: false,
		legend: {
			position: 'right'
        },
        tooltips: {
        	mode: 'x-axis'
         },
         scales: {
 		    xAxes: [{
                 gridLines: {
                     display:false
                 }
             }],
 		    yAxes: [{
                 gridLines: {
                     display:false
                 },
                 ticks: {
                     beginAtZero: true
                 }
             }]
 		}
	};
	
	woTrend = new Chart(woTrendCanvas , {
        type: "line",
        data: woTrendData, 
        options: woTrendOptions
    });
	
	$('#woTrend').on("click", function(evt){
	      var activePoints = woTrend.getElementAtEvent(evt);
	      if (activePoints[0]) {
	    	  	var chartData = activePoints[0]['_chart'].config.data;
	    	  	var idx = activePoints[0]['_index'];
	    	  	var dataidx = activePoints[0]['_datasetIndex'];
	        	var value = chartData.datasets[dataidx].data[idx];
	        	
	        	if(dataidx == 1){
	        		clickValueLink(8, "20"+idx, value.y);
	        	}
	        	else{
	        		clickValueLink(8, "10"+idx, value.y);
	        	}
	      }
	});
}