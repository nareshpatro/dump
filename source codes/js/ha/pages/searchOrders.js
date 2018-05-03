/* --------------------------------------
    File Name createOrders.js
    Author Carmen Ng (PCCW)
    Date August ‎31, ‎2017
    Description
    All functions used uniquely in the search work orders page.

    ---------- Modification History ----------
	20171213 by Carmen Ng
	Added toggleCheckValidity
	20171211 by Carmen Ng
	Adjusted table layout
	Fixed print button not enabling
	
	20171130 by Carmen Ng
	Disabled print and export button when there were no results
	Added restore default button
	
	20171128 by Carmen Ng
	Added validation for hidden fields before search
	
	20171127 by Carmen Ng
	Added from date larger than to date validations
	Turn off lov search searching until wo search is over
	
	20171127 by Carmen Ng
	Fixed and added some validations regarding search fields
	
	20171122 by Carmen Ng
	Clear all now removes alerts
	Fixed issue where formats would be removed after column reorder
	
	20171117 by Carmen Ng
    Fixed responsive UI for table elements
    Added checking for wo number and serial number not starting with %
	
	20171116 by Carmen Ng
	Fixed ajax call error message
	
	20171115 by Carmen Ng
	Wildcard only search not allowed, must have value
	
	20171114 by Carmen Ng
	Added support for overdue/upcoming exceptions
	
	20171110 by Carmen Ng
	Added cookie for search work order return
	
    20171020 by Carmen Ng
    Rebuilt all necessary functions/ids/names to accommadate new model

    20170831 by Carmen Ng
    Initial version
   -------------------------------------- -*/

	var mBody = "";
	var user = "";
	var toggleOff;
	
	var exportParameters;
	
	var exportObject;
	var tableObject;
	var exportLength;
	var tableLength;

$(document).ready(function(){
    loadCommon("glyphicon-search", "Search", "Search Work Orders");
    page = 'sidebarSearchWo';
    toggleOff = false;
    toggleCheckValidity(true);
    exportParameters = {};
    
    exportObject = {};
	tableObject = {};
	
	if($('#eamOrg option').length == 1){
		$('#fromDateHint').html("Max date range is 1 year.");
		$('#toDateHint').html("Max date range is 1 year.");
	}
	else{
		$('#fromDateHint').html("Max date range is " + corpDateRangeMax + " months.");
		$('#toDateHint').html("Max date range is " + corpDateRangeMax + " months.");
	}
		
    //Press enter to submit search form, runs same click search button checks
    $('#searchCriteria').keypress(function(e) {
        if(e.which == 13) {
        	$('#searchAlert').hide();
        	
        	toggleCheckValidity(false);
            submitSearch();
        }
    });
    
    //Datatables intitiation and settings
    initialiseTable(false);

    //Search work requests from search criteria button function
    $('#woSearchBtn').click(function () {
    	$.xhrPool.abortAll();
    	
    	$('#searchAlert').hide();
    	
        submitSearch();
    });

    //Clear work requests search criteria button function
    $('#clearWoSearchBTN').click(function () {
        $('#woNumber').val("");
        $('#assetNumber').val("");
        $('#woType').val("");
        $("#woType").val('').trigger('change');
        $("#woStatus").val('').trigger('change');
        $("#woStatus").selectpicker('deselectAll');
        $("#woStatus").val([]);
        $("#dateType").val('').trigger('change');
        $('#dateTo').val("");
        $('#dateFrom').val("");
        $('.dateFromLabel').html('<small class="form-text text-muted selectDate">Please select a date type.</small>');
        $('.dateToLabel').html('<small class="form-text text-muted selectDate">Please select a date type.</small>');
        $('#dateFrom').attr("disabled", true);
        $('#dateTo').attr("disabled", true);
        $('#assetRisk').val("");
        $("#assetRisk").val('').trigger('change');
        $('#maintenanceContract').val("");
        $('#maintenanceVendor').val("");
        $('#serialNumber').val("");
        $('#assetLocation').val("");
        $('#assetOwner').val("");
        $('#owningDept').val("");
        $('#requestedFor').val("");
		$('#hiddenUser').val("");
        $('#criticalOnly').prop('checked', false);
        $('#urgentOnly').prop('checked', false);
        if($('#eamOrg > option').length > 1){
        	$('#eamOrg').val("");
            $("#eamOrg").val('').trigger('change');
        }
	});
    
    //If user has entered from the "return to search page" button
    if(getCookie('returnWO') == "true" && getCookie('searchWOValues') !== ""){
        var previousValues = JSON.parse(getCookie('searchWOValues'));
        $.each(previousValues, function(name, value){
            switch(name){
                case 'woType':
                case 'woStatus':
                case 'eamOrg':
                case 'maintenanceContract':
                case 'assetRisk':
                case 'dateType':
                    $('#' + name).val(value);
                    $('#' + name).selectpicker('refresh');
                    $('#dateTo').attr("disabled", false);
                    $('#dateFrom').attr("disabled", false);
                    if(value == "C"){
                    	$('.dateFromLabel').html("Work Order Creation Date From");
                    	$('.dateToLabel').html("Work Order Creation Date To");
                    }
                    else if(value == "B"){
                    	$('.dateFromLabel').html("CM Breakdown Date From");
                    	$('.dateToLabel').html("CM Breakdown Date To");
                    }
                    else if(value == "S"){
                    	$('.dateFromLabel').html("PM Scheduled Date From");
                    	$('.dateToLabel').html("PM Scheduled Date To");
                    }
                    break;
                case 'urgentOnly':
                case 'criticalOnly':
                    $('#' + name).prop('checked', value);
                    break;
                case 'createdBy':
                    $('#hiddenUser').val(value);
                    break;
                case 'createdByInput':
                    $('#requestedFor').val(value);
                    break;
                default:
                    $('#' + name).val(value);
            }
        });   
        toggleCheckValidity(false);
        submitSearch();
        setCookie('returnWO', false, 60000);
    } 
    
    $('#dateType').on('changed.bs.select', function (e, newValue) {
        $('#dateFrom').removeAttr('disabled');
        $('#dateTo').removeAttr('disabled');
       
        if(newValue === 1){
            $('.dateFromLabel').html('CM Breakdown Date From');
            $('.dateToLabel').html('CM  Breakdown Date To');
        }
        else if (newValue === 2){
            $('.dateFromLabel').html('Work Order Creation Date From');
            $('.dateToLabel').html('Work Order Creation Date To');
        }
        else if (newValue === 3){
            $('.dateFromLabel').html('PM Scheduled Date From');
            $('.dateToLabel').html('PM Scheduled Date To');
        }
        else {
            $('.dateFromLabel').html('<small class="form-text text-muted selectDate">Please select a date type.</small>');
            $('.dateToLabel').html('<small class="form-text text-muted selectDate">Please select a date type.</small>');
            $('#dateFrom').attr('disabled', 'disabled');
            $('#dateTo').attr('disabled', 'disabled');
			$('#dateFrom').val("");
            $('#dateTo').val("");		   
        }
    });
});

function viewWorkOrder(el){
    $('#viewWoNumber').val($(el).attr("name"));
    $('#viewWoForm').submit();
}

function submitSearch(){
	if($("#criticalOnly").length == 0 && $("#urgentOnly").length == 0 && checkEmptyfields()  && $('#assetRisk').length == 0 && $('#maintenanceContract').length == 0){
	   	alertMessage("searchAlert", "HA_ERROR_LEASTONECRITERIA");
        return;
   }else if(checkEmptyfields() && !$('#criticalOnly').prop('checked') && !$('#urgentOnly').prop('checked') && $('#assetRisk').val() == "" && replaceAll($('#maintenanceContract').val(), "%", "") == ""){	   
	   	alertMessage("searchAlert", "HA_ERROR_LEASTONECRITERIA");
       return;
   }
	
	//Check if work order number or serial number begins with %
	if($('#woNumber').val().charAt(0) == "%" || $('#serialNumber').val().charAt(0) == "%"){
		alertMessage("searchAlert", "HA_ERROR_WO_SEARCHWILDCARDERROR");
    	return;
	}
	
	if($('#woNumber').val() == "" && $('#assetNumber').val() == ""){
		if ($('#dateFrom').val() ==  "" && $('#dateTo').val() == ""){
			alertMessage("searchAlert", "HA_ERROR_ENTERDATE");
			return;
	    } 
	}
	
	//Check if date to is not entered when date from is
	if  ($('#dateFrom').val() !==  "" && $('#dateTo').val() == ""){
		alertMessage("searchAlert", "HA_ERROR_DATETOMISSING");
		return;
    } 
	//Check if date from is not entered when date to is
	else if ($('#dateFrom').val() ==  "" && $('#dateTo').val() != ""){
		alertMessage("searchAlert", "HA_ERROR_DATEFROMMISSING");
		return;
    }
	//Check if from date is after to date
	else if(moment($('#dateFrom').val(), "DD/MM/YYYY") > moment($('#dateTo').val(), "DD/MM/YYYY")) {
		alertMessage("searchAlert", "HA_ERROR_DATEFROMLARGER");
		return;
	}
	else{
		if($('#eamOrg option').length == 1){
			if(moment($('#dateTo').val(), "DD/MM/YYYY") > moment($('#dateFrom').val(), "DD/MM/YYYY").add(1, 'y')){
				alertMessage("searchAlert", "HA_ERROR_DATEOUTOFRANGE");
				return;
			}
		}
		else{
			if(moment($('#dateTo').val(), "DD/MM/YYYY") > moment($('#dateFrom').val(), "DD/MM/YYYY").add(corpDateRangeMax, 'M')){
				alertMessage("searchAlert", "HA_ERROR_DATEOUTOFRANGE");
				return;
			}
		}
	}
	   
    $('#criteriaTitle').addClass("collapsed");
    $('#searchCriteria').addClass("collapse");
    $('#searchCriteria').removeClass("in");
    $('#searchAlert').hide();
	$('#woSearchBtn').button('loading');
   $('#woSearchBtn').attr("disabled", true);
   var table = $('#woSearchResults').DataTable();
   table.processing( true );
   
   //Validate hidden fields first, with count to ensure both has been checked
   if($('#maintenanceVendor').val() !== "" && $('#hiddenMBody').val() == ""){
		$.post("./LovLookup", { value: $('#maintenanceVendor').val().trim(), query: 0}, function(data){
			if(data == "No Match"){
				$( "#maintenanceVendor.lovFieldSearchBtn" ).trigger( "click" );
				alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
	   			$('#criteriaTitle').removeClass("collapsed");
	   			$('#searchCriteria').addClass("in");
	   			$('#searchCriteria').css("height", "auto");
			    $('#searchAlert').hide();
				$('#woSearchBtn').button('reset');
			    $('#woSearchBtn').attr("disabled", false);
			    var table = $('#woSearchResults').DataTable();
			    table.processing( false );
   			return;
   		}
   		else {
				$('#hiddenMBody').val(data);
				
				if($('#requestedFor').val() !== "" && $('#hiddenUser').val() == ""){
					$.post("./LovLookup", { value: $('#requestedFor').val().trim(), query: 1}, function(data){
			    		if(data == "No Match"){
			    			$( "#requestedFor.lovFieldSearchBtn" ).trigger( "click" );
			    			alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
			    			$('#criteriaTitle').removeClass("collapsed");
			    			$('#searchCriteria').addClass("in");
			    			$('#searchCriteria').css("height", "auto");
						    $('#searchAlert').hide();
							$('#woSearchBtn').button('reset');
						    $('#woSearchBtn').attr("disabled", false);
						    var table = $('#woSearchResults').DataTable();
						    table.processing( false );
			    			return;
			    		}
			    		else {
			    			$('#hiddenUser').val(data);
			    			searchWorkOrders();
			    		}
			    	});
			    }
				else{
					searchWorkOrders();
				}
   		}
			
	});
   } else{
	   	if($('#requestedFor').val() !== "" && $('#hiddenUser').val() == ""){
				$.post("./LovLookup", { value: $('#requestedFor').val().trim(), query: 1}, function(data){
		    		if(data == "No Match"){
		    			$( "#requestedFor.lovFieldSearchBtn" ).trigger( "click" );
		    			alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
		    			$('#criteriaTitle').removeClass("collapsed");
		    			$('#searchCriteria').addClass("in");
		    			$('#searchCriteria').css("height", "auto");
					    $('#searchAlert').hide();
						$('#woSearchBtn').button('reset');
					    $('#woSearchBtn').attr("disabled", false);
					    var table = $('#woSearchResults').DataTable();
					    table.processing( false );
		    			return;
		    		}
		    		else {
		    			$('#hiddenUser').val(data);
		    			searchWorkOrders();
		    		}
		    	});
		    }
			else{
				searchWorkOrders();
			}
	   }
}		

function searchWorkOrders(){
    var search = {};
    search["woNumber"] = $("#woNumber").val();
    search["assetNumber"] = $("#assetNumber").val().trim();
    search["woType"] = $("#woType").val();
    search["woStatus"] = $("#woStatus").val();
    search["eamOrg"] = $("#eamOrg").val();
    search["dateType"] = $("#dateType").val();
    search["dateFrom"] = $("#dateFrom").val();
    search["dateTo"] = $("#dateTo").val();
    search["assetRisk"] = $("#assetRisk").val();
    if($("#maintenanceContract").length){
        search["maintenanceContract"] = $("#maintenanceContract").val().trim();
    }
    else{
        search["maintenanceContract"] = "";
    }
    search["maintenanceVendor"] = $("#maintenanceVendor").val().trim();
    search["serialNumber"] = $("#serialNumber").val().trim();
    search["hiddenMBody"] = $("#hiddenMBody").val();
    search["assetLocation"] = $("#assetLocation").val().trim();
    search["assetOwner"] = $("#assetOwner").val().trim();
    search["owningDept"] = $("#owningDept").val().trim();
    search["createdBy"] = $("#hiddenUser").val();
    search["criticalOnly"] = $('#criticalOnly').prop("checked");
    search["urgentOnly"] = $("#urgentOnly").prop("checked");
    search["dashboardValue"] = $('#dashboardValue').val();
    
    exportParameters["woNumber"] = $("#woNumber").val();
    exportParameters["assetNumber"] = $("#assetNumber").val();
    exportParameters["woType"] = $('[data-id="woType"]').attr("title");
    exportParameters["woStatus"] = $('[data-id="woStatus"]').attr("title");
    exportParameters["eamOrg"] = $('[data-id="eamOrg"]').attr("title");
    exportParameters["dateType"] = $('[data-id="dateType"]').attr("title");
    exportParameters["dateFrom"] = $("#dateFrom").val();
    exportParameters["dateTo"] = $("#dateTo").val();
    exportParameters["assetRisk"] = $("#assetRisk").val();
    exportParameters["maintenanceContract"] = $("#maintenanceContract").val();
    exportParameters["maintenanceVendor"] = $("#maintenanceVendor").val();
    exportParameters["serialNumber"] = $("#serialNumber").val();
    exportParameters["assetLocation"] = $("#assetLocation").val();
    exportParameters["assetOwner"] = $("#assetOwner").val();
    exportParameters["owningDept"] = $("#owningDept").val();
    exportParameters["createdBy"] = $("#createdBy").val();
    exportParameters["criticalOnly"] = $('#criticalOnly').prop("checked"); 
    exportParameters["urgentOnly"] = $('#urgentOnly').prop("checked"); 
    
    //Set cookies for if the user returns to search page from view work request,  expires in one hour
    setCookie('searchWOValues', JSON.stringify(search), 3600000);
    setCookie('lastSearch', "WO", 3600000);
    
    var table = $('#woSearchResults').DataTable();
    $('#woSearchBtn').attr("disabled", true);
	    $.ajax({
	        type: 'POST',
	        contentType: 'application/json',
	        data : JSON.stringify(search),   
	        url : './processWorkOrder',                        
	        dataType:'json',
	        success : function(data1) {	       	        	
	        	//if(Object.keys(data1).length  == 1 && data1[0].woDescription !== null){
	        	if(Object.keys(data1).length  == 1 && data1[0].operationDescription !== null){
	        		//alertMessage("searchAlert", "Total " + data1[0].woDescription + " records returned and hit the limit to display and export. Please refine your searching criteria.", "error");
	        		alertMessage("searchAlert", "Total " + data1[0].operationDescription + " records returned and hit the limit to display and export. Please refine your searching criteria.", "error");
	        		$('#woSearchBtn').attr("disabled", false);
		        	$('#woSearchBtn').button('reset');
		        	table.clear().draw();
		        	table.button( 4 ).disable();
		        	table.processing( false );
		        	toggleCheckValidity(true);		
		        	return;
	        	}	        	
	        	
	        	exportObject = data1.concat();
	        	tableObject = data1.concat();
	        	
	        	exportLength = Object.keys(exportObject).length;
	        	tableLength = Object.keys(tableObject).length;
	        	
	        	if(tableLength > tableMax){
	        		tableObject.splice(tableMax, tableObject.length - tableMax);
	        		alertMessage("searchAlert", "Total " + tableLength + " records returned. Only max. " + tableMax + " records will be displayed in the result table. Please export the full result set via Excel export.", "note");
	        	}
	        	
	        	table.clear().rows.add(tableObject).draw();
	        	$('#woSearchBtn').attr("disabled", false);
	        	$('#woSearchBtn').button('reset');
	        	table.processing( false );
	        	var rowCount = table.rows().count();
	        	table.button( 4 ).enable(rowCount > 0);
	        	toggleCheckValidity(true);		
	        },
	
	        error : function(xhr, ajaxOptions, thrownError) {
	        	if (xhr.statusText =='abort' || thrownError == "") {
                    return;
                }
                
	        }
	    })
}

function loadSettings(userId, respId, appRespId){
    $.ajax({
        type: 'POST',
        data :{
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "ColOrder",
            "FUNCTION_CODE" : "WORK ORDER",
            "PAGE_CODE" : "SEARCH"
        },   
        url : './getUserPrefExt',
        dataType:'text',
        success : function(result) {
            if(result == "NO DATA"){
                var ColOrder = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21];
                
                $.ajax({
                    type: 'POST',
                    data : {
                        "USER_ID" : userId,
                        "RESP_ID" : respId,
                        "APP_ID" : appRespId,
                        "PREF_NAME" : "ColOrder",
                        "PREF_VALUE" : JSON.stringify(ColOrder),
                        "FUNCTION_CODE" : "WORK ORDER",
                        "PAGE_CODE" : "SEARCH"
                    },    
                    url : './saveUserPrefExt',
                    dataType:'text',
                    success : function(result) {
                    	loaded = loaded + 1;
                    	loadColVis();
                    },
                    error : function(xhr, ajaxOptions, thrownError) {
                    	if (xhr.statusText =='abort' || thrownError == "") {
                            return;
                        }
                        
                    }
                });
                
            } else {
            	$('#woSearchResults').DataTable().colReorder.order(JSON.parse(result));
            	loaded = loaded + 1;
            	loadColVis();
            }
        },
        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
            
        }
    });  
}

function loadColVis(){
	$.ajax({
        type: 'POST',
        data :{
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "COLVIS",
            "FUNCTION_CODE" : "WORK ORDER",
            "PAGE_CODE" : "SEARCH"
        },   
        url : './getUserPrefExt',
        dataType:'text',
        success : function(result) {
            if(result == "NO DATA"){
            	ColVis = [2,11,12,13,14,15,16,17,18,19,20,21];
                
                $.ajax({
                    type: 'POST',
                    data : {
                        "USER_ID" : userId,
                        "RESP_ID" : respId,
                        "APP_ID" : appRespId,
                        "PREF_NAME" : "ColVis",
                        "PREF_VALUE" : JSON.stringify(ColVis),
                        "FUNCTION_CODE" : "WORK ORDER",
                        "PAGE_CODE" : "SEARCH"
                    },    
                    url : './saveUserPrefExt',
                    dataType:'text',
                    success : function(result) {
                    	for (i = 0; i < ColVis.length; ++i) {
                    		$('#woSearchResults').DataTable().column( ColVis[i] ).visible( false );
                    	}
                    	loaded = loaded + 1;
                    	loadTableOrder();
                    },
                    error : function(xhr, ajaxOptions, thrownError) {
                    	if (xhr.statusText =='abort' || thrownError == "") {
                            return;
                        }
                        
                    }
                });
                
            } else {
            	ColVis = JSON.parse(result);
            	var col = -1;
            	for (i = 0; i < ColVis.length; ++i) {
            		col = ColVis[i];
            		$('#woSearchResults').DataTable().column( col ).visible( false );
            	}
            	loaded = loaded + 1;
            	loadTableOrder();
            }
        },
        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
            
        }
    }); 
}

function loadTableOrder(){
	$.ajax({
        type: 'POST',
        data :{
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "TableOrder",
            "FUNCTION_CODE" : "WORK ORDER",
            "PAGE_CODE" : "SEARCH"
        },   
        url : './getUserPrefExt',
        dataType:'text',
        success : function(result) {
            if(result == "NO DATA"){
            	$('#woSearchResults').DataTable().order([[ 1, 'desc' ]]).draw();
            	loaded = loaded + 1;
            } else {
            	$('#woSearchResults').DataTable().order(JSON.parse(result)).draw();
            	loaded = loaded + 1;
            }
        },
        error : function(xhr, ajaxOptions, thrownError) {
        	if (xhr.statusText =='abort' || thrownError == "") {
                return;
            }
            
        }
    }); 
}

function saveTableOrder() {
	$.ajax({
        type: 'POST',
        data : {
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "TableOrder",
            "PREF_VALUE" : JSON.stringify($('#woSearchResults').DataTable().order()),
            "FUNCTION_CODE" : "WORK ORDER",
            "PAGE_CODE" : "SEARCH"
        },    
        url : './saveUserPrefExt',
        dataType:'text',
        success : function(result) {
        },
        error : function(xhr, ajaxOptions, thrownError) {
            if (xhr.status == 404) {
            }
        }
    });	
}

function checkEmptyfields(){
	if(replaceAll($('#woNumber').val(), "%", "") == "" && 
			replaceAll($('#assetNumber').val(), "%", "") == "" && 
			$('#woType').val() == "" && 
			$('#woStatus').val().toString() == "" && 
			$('#dateTo').val() == "" && 
			$('#dateFrom').val() == "" && 
			replaceAll($('#maintenanceVendor').val(), "%", "") == "" && 
			replaceAll($('#serialNumber').val(), "%", "") == "" && 
			replaceAll($('#assetLocation').val(), "%", "") == "" && 
			replaceAll($('#assetOwner').val(), "%", "") == "" && 
			replaceAll($('#owningDept').val(), "%", "") == "" && 
			replaceAll($('#requestedFor').val(), "%", "") == ""){
		return true;
	}
	else {
		return false;
	}
}	
	
 //Turn on/off checking for LOV validity
function toggleCheckValidity(on){
	if(on){
		$(':focus').blur();
		$(".checkValidity").blur(function() {
	    	var id = this.id;
	    	var query;
	    	var button = $(this).closest('div').find('span').find('button');
	    	
	    	switch (id) {
		        case "assetNumber":
		       	 	query = "5";
		            break;
		        case "maintenanceContract":
		        	query = "8";
		            break;
		        case "assetLocation":
		        	query = "1";
		            break;
		        case "assetOwner":
		        	query = "2";
		            break;
		        case "owningDept":
		       	 	query = "3";
		            break;
		        case "assetLocationSaved":
		        	query = "1";
		            break;
		        case "owningDeptSaved":
		        	query = "3";
		            break;
		        default:
		        	throw new IllegalArgumentException("Invalid query for: " + key);
	    	}
	    	    	
	    	if(button.data("mouseDown") != true){
	    		if(toggleOff){
	    			toggleOff = false;
	    			return;
	    		}
	    		
	        	if($(this).val() !== ""){
	        		$.post("./LovValidity", { value: $(this).val().trim(), query: query}, function(data){
	        			if(data == "Match"){
	        				return;
	            		}
	            		else {
	            			button.trigger( "click" );
	            			//$('#' + id + '.lovFieldSearchBtn').trigger( "click" );
	            			alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
	            		}
	            	});
	            }
	  	  	}
	    });
	    
		//Maintenance body selection - add value to another id
		$('#maintenanceVendor').on({
	        focus: function() {
	        	mBody = $('#maintenanceVendor').val();
	        },
	        blur: function() {
		    	if(mBody !== $('#maintenanceVendor').val()){
		    		if($("#maintenanceVendor.lovFieldSearchBtn").data("mouseDown") != true){
		            	$('#hiddenMBody').val("");
		            	if($('#maintenanceVendor').val() !== ""){
		            		$.post("./LovLookup", { value: $(this).val().trim(), query: 9}, function(data){
		            			if(data == "No Match"){
		            				$( "#maintenanceVendor.lovFieldSearchBtn" ).trigger( "click" );
		            				alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
		                		}
		                		else {
		            				$('#hiddenMBody').val(data);
		                		}
		                	});
		                }
		      	  	}
		    	}
	        }
	    });
		
	    //Users selection - add value to another id
		$( "#requestedFor" ).on({
	    	focus: function(){
	    		user = $('#requestedFor').val();
	    	},
	    	blur: function(){
	    		if($("#requestedFor.lovFieldSearchBtn").data("mouseDown") != true){
		        	$('#hiddenUser').val("");
		        	if($('#requestedFor').val() !== ""){
		        		$.post("./LovLookup", { value: $(this).val().trim(), query: 1}, function(data){
		            		if(data == "No Match"){
		            			$( "#requestedFor.lovFieldSearchBtn" ).trigger( "click" );
	            				alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
		            		}
		            		else {
		            			$('#hiddenUser').val(data);
		            		}
		            	});
		            }
		  	  	}
	    	}
	    });
	}
	else{
		$(".checkValidity").off("blur");
		$("#maintenanceVendor").off("blur");
		$("#requestedFor").off("blur");
		toggleOff = true;
	}
}

function initialiseTable(restore){
	$.fn.dataTable.moment( 'DD/MM/YYYY HH:mm' );
	
	$('#woSearchResults').dataTable( {
        processing: true,
        deferRender: true,
        dom: "<'row'<'col-sm-12 buttonCol'Z>>" +
        "<'row'<'col-sm-12 tableSpacing text-right'fl>>" +
        "<'row'<'col-sm-12'rt>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        autoWidth: false,
        "oLanguage": {
            "sSearch": "_INPUT_",
            "sSearchPlaceholder" : "Filter Results",
            "sProcessing": "<img src='./resources/images/spinner.gif' alt='Loading' height='75' width='75'>"
        },
        colReorder: {
            realtime: false,
            fixedColumnsLeft: 1
        },
        columnDefs: [ {
            orderable: false,
            className: 'select-checkbox',
            targets:   0
        } ],
        buttons: [
            {
                extend: 'selectAll',
                text: '<span class="glyphicon glyphicon-check" aria-hidden="true"></span> Select All'
            },
            {
                extend: 'selectNone',
                text: '<span class="glyphicon glyphicon-unchecked" aria-hidden="true"></span> Deselect All'
            },
            {
                extend: 'colvis',
                text: '<span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span> Hide/Show Columns',
                columns: ':not(.noVis)'
            },
			{
				text: '<span class="glyphicon glyphicon-wrench" aria-hidden="true"></span> Restore Default Settings',
				action: function ( e, dt, node, config ) {
					var restoring = true;
					
					var cancelConfirm = function(cancelCallback){
						$('#confirmationMsg').html("The current columns setting will be restored to the default settings. Are you sure you want to proceed?");
						$("#confirmationModal").modal('show');
					
						$("#confirmYesBTN").on("click", function(){
							if(restoring){
								cancelCallback(true);
								$("#confirmationModal").modal('hide');
							}
						});
						  
						$("#confirmNoBTN").on("click", function(){
							if(restoring){
								cancelCallback(false);
								$("#confirmationModal").modal('hide');
							}
						});
					};
					
					cancelConfirm(function(cancelConfirm){
						if(cancelConfirm){
							restoreDefaultSettings();
						}else{
							return false;
						}
						restoring = false;
					});
				}
			},
            {
                text: '<span class="glyphicon glyphicon-export" aria-hidden="true"></span> Export',
                enabled: false,
                action: function ( e, dt, node, config ) {
                    //Variable declaration
                    var parametersString = "";
                    
                    var selectedWONumbers = [];
                    var selectedCount = dt.rows( { selected: true } ).count();
                	var rowCount = dt.rows().count();
                    
                    //Get the work request numbers. If selected, get selected. If not, get all.
                    if(selectedCount > 0 && selectedCount !== rowCount){
                    	if(selectedCount > 100){
            				alertMessage("searchAlert", "HA_ERROR_WO_EXPORTSELECTALL");
                    		return;
                    	}
                    	
                    	if($('#searchAlert').html().indexOf("Please select all") !== -1){
                    		$('#searchAlert').hide();
                    	}
                    	
                        var rows = dt.rows({ selected: true }).data();
                        
                        for (i = 0; i < rows.length; i++) { 
                            selectedWONumbers[i] = rows[i]['workOrderNumber'];
                        }
                        
                        parametersString = "Work Order Numbers = " + selectedWONumbers.toString();                        
                    }
                    else{
                    	if($('#searchAlert').html().indexOf("Please select all") !== -1){
                    		$('#searchAlert').hide();
                    	}
//                        var rows = dt.rows().data();
//                        
//                        for (i = 0; i < rows.length; i++) { 
//                            selectedWONumbers[i] = rows[i]['workOrderNumber'];
//                        }
                    	
                    	for (i = 0; i < exportObject.length; i++) {
                    		selectedWONumbers[i] = exportObject[i]['workOrderNumber'];
        	            }
                        
                        //Get the inputed search criterias and build parameters string
                        for (var key in exportParameters) {
                            if (exportParameters.hasOwnProperty(key)) {
                                if(exportParameters[key] && exportParameters[key] !== "-- Select --"){
                                    var criteria = "";
                                    switch (key) {
                                         case "assetLocation":
                                             criteria = "Location Code = ";
                                             break;
                                         case "assetNumber":
                                             criteria = "Asset Number = ";
                                             break;
                                         case "assetOwner":
                                             criteria = "Asset Owner = ";
                                             break;
                                         case "createdBy":
                                             criteria = "Work Order Creation By = ";
                                             break;
                                         case "criticalOnly":
                                             criteria = "Critical Only = ";
                                             break;
                                         case "dateFrom":
                                             criteria = "Date From = ";
                                             break;
                                         case "dateTo":
                                             criteria = "Date To = ";
                                             break;
                                         case "dateType":
                                             criteria = "Date Type = ";
                                             break;
                                         case "eamOrg":
                                             criteria = "EAM Org = ";
                                             break;
                                         case "maintenanceVendor":
                                             criteria = "Maintenance Body = ";
                                             break;
                                         case "owningDept":
                                             criteria = "Owning Department = ";
                                             break;
                                         case "woNumber":
                                             criteria = "Work Order Number = ";
                                             break;
                                         case "woStatus":
                                             criteria = "Work Order Status = ";
                                             break;
                                         case "woType":
                                             criteria = "Work Order Type = ";
                                             break;
                                         case "assetRisk":
                                             criteria = "Asset Risk = ";
                                             break;
                                         case "maintenanceContract":
                                             criteria = "Maintenance Contract Number = ";
                                             break;
                                         case "serialNumber":
                                             criteria = "Serial Number = ";
                                             break;
                                         case "urgentOnly":
                                             criteria = "Urgent Only = ";
                                             break;
                                         default:
                                             throw new IllegalArgumentException("Invalid criteria: " + key);
                                     }
                                    parametersString = parametersString + criteria + exportParameters[key] + ", ";
                                }
                            }
                        }
                        parametersString = parametersString.slice(0, -2);
                    }         
                    
                    console.log(selectedWONumbers.toString());
                    console.log($('#loggedInUser').text());
                    console.log(parametersString);
                    console.log("WorkOrder");
                    
                	$('#exportWoNumber').val(selectedWONumbers.toString());
                    $('#exportUserName').val($('#loggedInUser').text());
                    $('#exportParameter').val(parametersString);
                    $('#exportExportType').val("WorkOrder");
                    $('#exportForm').submit();                    
                    
                }
        },
        {
            text: '<span class="glyphicon glyphicon-print" aria-hidden="true"></span> Print',
            enabled: false,
            action: function ( e, dt, node, config ) {
                $('#searchAlert').hide();
                
                var selectedWONumbers = [];
                var count = dt.rows( { selected: true } ).count();
                
                //Get the work request numbers. If selected, get selected. If not, get all.
                if(count > 0){
                    var rows = dt.rows({ selected: true }).data();
                    
                    if(rows.count() > 10){
        				alertMessage("searchAlert", "HA_ERROR_WO_PRINTMAX");
                    }
                    else{
                        for (i = 0; i < rows.length; i++) { 
                            selectedWONumbers[i] = rows[i]['workOrderNumber'];
                        }
                        $('#printWoNumber').val(selectedWONumbers.toString());
                        $('#printReportType').val("workorder");

                        $('#printForm').submit();
                    }
                }
            }
        }
    ],
        colResize: {
            "tableWidthFixed": false
        },
        select: {
            style:    'multi',
            selector: 'td:first-child'
        },
        order: [[ 1, 'desc' ]],
        columns: [
            { "defaultContent": "", "className":"noVis select-checkbox"},
            { "title": "Work Order Number", data: "workOrderNumber", "defaultContent": "", "className":"noVis",
            "mRender": function ( sData, type, row, meta ) {
	            	if(sData == null){
	                    return sData;
	                } 
	            	else {
	                    return "<a onclick='viewWorkOrder(this)' name='"+sData+"' style='cursor: pointer'>"+sData+"</a>";
	                }
            }
            },
            { "title": "EAM Org", data: "workOrderOrg", "defaultContent": ""},
            { "title": "Asset Number", data: "assetNumber", "defaultContent": ""},
            { "title": "Asset Description", data: "assetDescription", "defaultContent": ""},
            { "title": "Work Order Type", data: "woType", "defaultContent": ""},
            { "title": "Work Order Status", data: "woStatus", "defaultContent": "", "width":"130px"},
            { "title": "CM Breakdown Date Time", data: "breakdownDate", "defaultContent": "", "width":"130px"},
            { "title": "PM Scheduled Date Time", data: "scheduleDate", "defaultContent": "", "width":"130px"},
            { "title": "Owning Department", data: "owningDepartment", "defaultContent": ""},
            { "title": "Work Order Creation By", data: "createdBy", "defaultContent": ""},
            { "title": "Maintenance Body", data: "maintenanceBody", "defaultContent": ""},
            { "title": "Maintenance Contract Number", data: "maintenanceContract", "defaultContent": ""},
            { "title": "Location Code", data: "assetLocation", "defaultContent": ""},
            { "title": "Asset Owner", data: "assetOwner", "defaultContent": ""},
            { "title": "Asset Group", data: "assetGroup", "defaultContent": ""},
            { "title": "Work Order Creation Date Time", data: "creationDate", "defaultContent": "",
	        	"mRender": function ( sData, type, row, meta ) {
	            	if(sData == null){
	                    return sData;
	                } 
	            	else {
	                    return moment(sData).format('DD/MM/YYYY HH:mm');
	                }
        	}
            },
            { "title": "Serial Number", data: "assetSerialNumber", "defaultContent": ""},
            { "title": "Manufacturer", data: "manufacturer", "defaultContent": ""},
            { "title": "Brand", data: "brand", "defaultContent": ""},
            { "title": "Model", data: "model", "defaultContent": ""},
            { "title": "Work Order Description", data: "woDescription", "defaultContent": ""}
        ]
    } );
    jQuery('.dataTable').wrap('<div class="dataTables_scroll" />');
    $('#woSearchResults').DataTable().buttons().container().appendTo( $('.buttonCol') );
    $('#woSearchResults').DataTable().on( 'select deselect', function () {
        var selectedRows =  $('#woSearchResults').DataTable().rows( { selected: true } ).count();
        $('#woSearchResults').DataTable().button( 5 ).enable( selectedRows > 0 );
    });
    
    if(restore){
    	$('#criteriaTitle').addClass("collapsed");
		$('#searchCriteria').addClass("collapse");
		$('#searchCriteria').removeClass("in");
		$('#searchAlert').hide();
		$('#woSearchBtn').button('loading');
		$('#woSearchBtn').attr("disabled", true);
		$('#woSearchResults').DataTable().processing( true );
   		searchWorkOrders();
    }
}

function dashboardSearch(){
	//Check if sent from dashboard with max records
	if(getCookie("overMax") !== null &&  Number(getCookie("overMax")) > exportMax){
		var max = getCookie("overMax");
		alertMessage("searchAlert", "Total " + max + " records returned and hit the limit to display and export. Please refine your searching criteria.", "error");
		delCookie("overMax");
	}
	else{
		$('#searchCriteria').collapse("hide");
		$('#searchAlert').hide();
		$('#woSearchBtn').button('loading');
		$('#woSearchBtn').attr("disabled", true);
		$('#woSearchResults').DataTable().processing( true );
   		searchWorkOrders();
	}	        	
}