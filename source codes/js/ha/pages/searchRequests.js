/* --------------------------------------
    File Name searchRequests.js
    Author Carmen Ng (PCCW)
    Date September ‎26, ‎2017
    Description
    All functions used uniquely in the create work requests page.
    
    ---------- Modification History ----------
    20171215 by Carmen Ng
    Moved initialiseTable into a function
    
	20171213 by Carmen Ng
	Added toggleCheckValidity
	20171211 by Carmen Ng
	Adjusted table layout
	Fixed print button not enabling
	
	20171130 by Carmen Ng
	Disabled print and export button when there were no results in table
	Added restore default function
	
	20171128 by Carmen Ng
	Added validation for hidden fields before search
	
	20171127 by Carmen Ng
	Added from date larger than to date validations
	Turn off lov search searching until wr search is over
	
	20171122 by Carmen Ng
	Fixed issue where formats would be removed after column reorder
	
	20171117 by Carmen Ng
    Fixed responsive UI for table elements
    Added checking for wr number not starting with %
    
	20171116 by Carmen Ng
	Fixed ajax call error message
	
	20171115 by Carmen Ng
	Wildcard only search not allowed, must have value
	
    20171110 by Carmen Ng
    Added viewWorkOrder function for click work order number function
    
    20171108 by Carmen Ng
    Added populateAssetInfo function
    
    20171024 by Carmen Ng
    Added asset risk criteria

    20171012 by Carmen Ng
    Added print reports function

    20171006 by Carmen Ng
    Added export excel function

    20171003 by Carmen Ng
    Made UI changes to the datatable regarding layout etc.

    20170926 by Carmen Ng
    Initial version
   -------------------------------------- -*/

	var mBody = "";
	var user = "";
	var toggleOff = true;
	
	var exportParameters;
	
	var exportObject;
	var tableObject;
	
$(document).ready(function(){
    loadCommon("glyphicon-search", "Search", "Search Work Requests");
    page = 'sidebarSearchWr';
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
    $('#wrSearchBtn').click(function () {
    	$.xhrPool.abortAll();
    	$('#searchAlert').hide();
    	
    	submitSearch();
    });

    //Clear work requests search criteria button function
    $('#clearWrSearchBTN').click(function () {
        $('#wrNumber').val("");
        $('#assetNumber').val("");
        $('#wrType').val("");
        $("#wrType").val('').trigger('change');
        $("#wrStatus").val('').trigger('change');
        $("#wrStatus").selectpicker('deselectAll');
        $("#wrStatus").val([]);
        $("#dateType").val('').trigger('change');
        $('#dateTo').val("");
        $('#dateFrom').val("");
        $('.dateFromLabel').html('<small class="form-text text-muted selectDate">Please select a date type.</small>');
        $('.dateToLabel').html('<small class="form-text text-muted selectDate">Please select a date type.</small>');
        $('#dateFrom').attr("disabled", true);
        $('#dateTo').attr("disabled", true);
        $('#maintenanceVendor').val("");
        $('#hiddenMBody').val("");
        $('#assetLocation').val("");
        $('#assetOwner').val("");
        $('#owningDept').val("");
        $('#requestedFor').val("");
        $('#hiddenUser').val("");
        $('#criticalOnly').prop('checked', false);
        $('#assetRisk').val("");
        $("#assetRisk").val('').trigger('change');
        $('#assetRisk').selectpicker('deselectAll');
        if($('#eamOrg > option').length > 1){
        	$('#eamOrg').val("");
            $("#eamOrg").val('').trigger('change');
        }
    });
    
    //If user has entered from the "return to search page" button
    if(getCookie('returnWR') == "true" && getCookie('searchWRValues') !== ""){
        var previousValues = JSON.parse(getCookie('searchWRValues'));
    	$.each(previousValues, function(name, value){
    		switch(name){
	            case 'wrType':
	            case 'wrStatus':
	            case 'eamOrg':
	            case 'dateType':
	            	$('#' + name).val(value);
	            	$('#' + name).selectpicker('refresh');
	                $('#dateTo').attr("disabled", false);
                    $('#dateFrom').attr("disabled", false);
                    if(value == "C"){
                    	$('.dateFromLabel').html("Creation Date From");
                    	$('.dateToLabel').html("Creation Date To");
                    }
                    else if(value == "B"){
                    	$('.dateFromLabel').html("Breakdown Date From");
                    	$('.dateToLabel').html("Breakdown Date To");
                    }
                    else if(value == "S"){
                    	$('.dateFromLabel').html("Schedule Date From");
                    	$('.dateToLabel').html("Schedule Date To");
                    }
	                break;
	            case 'riskLevel':
	            	$('#assetRisk').val(value);
	            	$('#assetRisk').selectpicker('refresh');
	                break;    
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
    	$('#searchCriteria').collapse('hide');
    	$('#wrSearchBtn').button('loading');
        $('#wrSearchBtn').attr("disabled", true);
        $('#wrSearchResults').DataTable().processing( true );
    	searchWorkRequests();
    	setCookie('returnWR', false, 60000);
    } 
    
    $('#dateType').on('changed.bs.select', function (e, newValue) {
        $('#dateFrom').removeAttr('disabled');
        $('#dateTo').removeAttr('disabled');
       
        if(newValue === 1){
            $('.dateFromLabel').html('CM Breakdown Date From');
            $('.dateToLabel').html('CM  Breakdown Date To');
        }
        else if (newValue === 2){
            $('.dateFromLabel').html('Work Request Creation Date From');
            $('.dateToLabel').html('Work Request Creation Date To');
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

function viewWorkRequest(el){
	$('#viewWrNumber').val($(el).attr("name"));
	$('#viewWrForm').submit();
}

function viewWorkOrder(el){
	$('#viewWoNumber').val($(el).attr("name"));
	$('#viewWoForm').submit();
}

function searchWorkRequests(){
    var search = {}
    search["wrNumber"] = $("#wrNumber").val();
    search["assetNumber"] = $("#assetNumber").val().trim();
    search["wrType"] = $("#wrType").val();
    search["wrStatus"] = $("#wrStatus").val();
    search["eamOrg"] = $("#eamOrg").val();
    search["dateType"] = $("#dateType").val();
    search["dateFrom"] = $("#dateFrom").val();
    search["dateTo"] = $("#dateTo").val();
    search["maintenanceVendor"] = $("#maintenanceVendor").val().trim();
    search["hiddenMBody"] = $("#hiddenMBody").val();
    search["assetLocation"] = $("#assetLocation").val().trim();
    search["assetOwner"] = $("#assetOwner").val().trim();
    search["owningDept"] = $("#owningDept").val().trim();
    search["createdBy"] = $("#hiddenUser").val();
    search["criticalOnly"] = $('#criticalOnly').prop("checked");
    search["createdByInput"] = $("#requestedFor").val().trim();
    search["riskLevel"] = $("#assetRisk").val();
    
	exportParameters["wrNumber"] = $("#wrNumber").val();
	exportParameters["assetNumber"] = $("#assetNumber").val();
	exportParameters["wrType"] = $('[data-id="wrType"]').attr("title");
	exportParameters["wrStatus"] = $('[data-id="wrStatus"]').attr("title");
	exportParameters["eamOrg"] = $('[data-id="eamOrg"]').attr("title");
	exportParameters["dateType"] = $('[data-id="dateType"]').attr("title");
    exportParameters["dateFrom"] = $("#dateFrom").val();
    exportParameters["dateTo"] = $("#dateTo").val();
    exportParameters["maintenanceVendor"] = $("#maintenanceVendor").val();
    exportParameters["assetLocation"] = $("#assetLocation").val();
    exportParameters["assetOwner"] = $("#assetOwner").val();
    exportParameters["owningDept"] = $("#owningDept").val();
    exportParameters["createdBy"] = $("#createdBy").val();
    exportParameters["criticalOnly"] = $('#criticalOnly').prop("checked"); 
    exportParameters["riskLevel"] = $('[data-id="assetRisk"]').attr("title");
        
    //Set cookies for if the user returns to search page from view work request,  expires in one hour
    setCookie('searchWRValues', JSON.stringify(search), 3600000);
    setCookie('lastSearch', "WR", 3600000);
    
    var table = $('#wrSearchResults').DataTable();
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            data : JSON.stringify(search),   
            url : './processWorkRequest',                        
            dataType:'json',
            success : function(data1) {            	
	        	//if(Object.keys(data1).length  == 1 && data1[0].description !== null){
            	if(Object.keys(data1).length  == 1 && data1[0].maintFax !== null){
	        		//alertMessage("searchAlert", "Total " + data1[0].description + " records returned and hit the limit to display and export. Please refine your searching criteria.", "error");
            		alertMessage("searchAlert", "Total " + data1[0].maintFax + " records returned and hit the limit to display and export. Please refine your searching criteria.", "error");
	        		$('#wrSearchBtn').attr("disabled", false);
		        	$('#wrSearchBtn').button('reset');
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
	        	$('#wrSearchBtn').attr("disabled", false);
	        	$('#wrSearchBtn').button('reset');
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


function submitSearch(){
	//Check if there are any values entered
	if(replaceAll($('#wrNumber').val(), "%", "") == "" 
		&& replaceAll($('#assetNumber').val(), "%", "") == "" 
		&& $('#wrType').val() == "" 
		&& $('#wrStatus').val() == "" 
		&& $('#dateTo').val() == ""
		&& $('#dateFrom').val() == "" 
		&& replaceAll($('#maintenanceVendor').val(), "%", "") == "" 
		&& replaceAll($('#assetLocation').val(), "%", "") == "" 
		&& replaceAll($('#assetOwner').val(), "%", "") == "" 
		&& replaceAll($('#owningDept').val(), "%", "") == "" 
		&& $('#assetRisk').val() == "" 
		&& replaceAll($('#requestedFor').val(), "%", "") == "" 
		&& !$('#criticalOnly').prop("checked")){
			alertMessage("searchAlert", "HA_ERROR_LEASTONECRITERIA");
			return;
	}
	
	//Check if work request number starts with %
	if($('#wrNumber').val().charAt(0) == "%"){
		alertMessage("searchAlert", "HA_ERROR_WR_SEARCHWILDCARDERROR");
    	return;
	}
		
	if($('#wrNumber').val() == "" && $('#assetNumber').val() == ""){
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

    $('#searchCriteria').collapse('hide');
    $('#searchAlert').hide();
	$('#wrSearchBtn').button('loading');
    $('#wrSearchBtn').attr("disabled", true);
    var table = $('#wrSearchResults').DataTable();
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
				$('#wrSearchBtn').button('reset');
			    $('#wrSearchBtn').attr("disabled", false);
			    var table = $('#wrSearchResults').DataTable();
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
							$('#wrSearchBtn').button('reset');
						    $('#wrSearchBtn').attr("disabled", false);
						    var table = $('#wrSearchResults').DataTable();
						    table.processing( false );
			    			return;
			    		}
			    		else {
			    			$('#hiddenUser').val(data);
			    			searchWorkRequests();
			    		}
			    	});
			    }
				else{
					searchWorkRequests();
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
					$('#wrSearchBtn').button('reset');
				    $('#wrSearchBtn').attr("disabled", false);
				    var table = $('#wrSearchResults').DataTable();
				    table.processing( false );
	    			return;
	    		}
	    		else {
	    			$('#hiddenUser').val(data);
	    			searchWorkRequests();
	    		}
	    	});
	    }
		else{
			searchWorkRequests();
		}
    }
}		

function loadSettings(userId, respId, appRespId){
    $.ajax({
        type: 'POST',
        data :{
            "USER_ID" : userId,
            "RESP_ID" : respId,
            "APP_ID" : appRespId,
            "PREF_NAME" : "ColOrder",
            "FUNCTION_CODE" : "WORK REQUEST",
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
                        "FUNCTION_CODE" : "WORK REQUEST",
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
            	$('#wrSearchResults').DataTable().colReorder.order(JSON.parse(result));
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
            "FUNCTION_CODE" : "WORK REQUEST",
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
                        "FUNCTION_CODE" : "WORK REQUEST",
                        "PAGE_CODE" : "SEARCH"
                    },    
                    url : './saveUserPrefExt',
                    dataType:'text',
                    success : function(result) {
                    	for (i = 0; i < ColVis.length; ++i) {
                    		$('#wrSearchResults').DataTable().column( ColVis[i] ).visible( false );
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
            		$('#wrSearchResults').DataTable().column( col ).visible( false );
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
            "FUNCTION_CODE" : "WORK REQUEST",
            "PAGE_CODE" : "SEARCH"
        },   
        url : './getUserPrefExt',
        dataType:'text',
        success : function(result) {
            if(result == "NO DATA"){
            	$('#wrSearchResults').DataTable().order([[ 1, 'desc' ]]).draw();
            	loaded = loaded + 1;
            } else {
            	$('#wrSearchResults').DataTable().order(JSON.parse(result)).draw();
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
            "PREF_VALUE" : JSON.stringify($('#wrSearchResults').DataTable().order()),
            "FUNCTION_CODE" : "WORK REQUEST",
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

//Turn on/off checking for LOV validity
function toggleCheckValidity(on){
	if(on){
		$(':focus').blur();
		$(".checkValidity").on({
			blur: function(){
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
	
	$('#wrSearchResults').dataTable( {
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
            "sProcessing": "<img src='/EAM/resources/images/spinner.gif' alt='Loading' height='75' width='75'>"
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
                    
                	var selectedWRNumbers = [];
                	var selectedCount = dt.rows( { selected: true } ).count();
                	var rowCount = dt.rows().count();
                	
                	//Get the work request numbers. If selected, get selected. If not, get all.
                	if(selectedCount > 0 && selectedCount !== rowCount){
                		if(selectedCount > 100){
                			alertMessage("searchAlert", "HA_ERROR_WR_EXPORTSELECTALL");
                    		return;
                    	}
                		if($('#searchAlert').html().indexOf("Please select all") !== -1){
                    		$('#searchAlert').hide();
                    	}
                		var rows = dt.rows({ selected: true }).data();
                		
                		for (i = 0; i < rows.length; i++) { 
                    		selectedWRNumbers[i] = rows[i]['workRequestNumber'];
                    	}
                		
                		parametersString = "Work Request Numbers = " + selectedWRNumbers.toString();                		
                	}
                	else{
                		if($('#searchAlert').html().indexOf("Please select all") !== -1){
                    		$('#searchAlert').hide();
                    	}
//                		var rows = dt.rows().data();
//                		
//                		for (i = 0; i < rows.length; i++) { 
//                    		selectedWRNumbers[i] = rows[i]['workRequestNumber'];
//                    	}
                		
                		for (i = 0; i < exportObject.length; i++) {
                			selectedWRNumbers[i] = exportObject[i]['workRequestNumber'];
        	            }
                		
                		//Get the inputed search criterias and build parameters string
                    	for (var key in exportParameters) {
                    		if (exportParameters.hasOwnProperty(key)) {
                    			if(exportParameters[key] && exportParameters[key] !== "-- Select --"){
                    				var criteria = "";
            	    			    switch (key) {
            	    			         case "assetLocation":
            	    			        	 criteria = "Asset Location = ";
            	    			             break;
            	    			         case "assetNumber":
            	    			        	 criteria = "Asset Number = ";
            	    			             break;
            	    			         case "assetOwner":
            	    			        	 criteria = "Asset Owner = ";
            	    			             break;
            	    			         case "createdBy":
            	    			        	 criteria = "Created By = ";
            	    			             break;
            	    			         case "riskLevel":
            	    			        	 criteria = "Risk Level = ";
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
            	    			         case "wrNumber":
            	    			        	 criteria = "Work Request Number = ";
            	    			             break;
            	    			         case "wrStatus":
            	    			        	 criteria = "Work Request Status = ";
            	    			             break;
            	    			         case "wrType":
            	    			        	 criteria = "Work Request Type = ";
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
                	
                	$('#exportWrNumber').val(selectedWRNumbers.toString());
                	$('#exportUserName').val($('#loggedInUser').text());
                	$('#exportParameter').val(parametersString);
                	$('#exportExportType').val("WorkRequest");
                	$('#exportForm').submit();

                }
        },
        {
            text: '<span class="glyphicon glyphicon-print" aria-hidden="true"></span> Print',
            enabled: false,
            action: function ( e, dt, node, config ) {
            	$('#searchAlert').hide();
            	
            	var selectedWRNumbers = [];
            	var count = dt.rows( { selected: true } ).count();
            	
            	//Get the work request numbers. If selected, get selected. If not, get all.
            	if(count > 0){
            		var rows = dt.rows({ selected: true }).data();
            		
            		if(rows.count() > 10){
            			alertMessage("searchAlert", "HA_ERROR_WR_PRINTMAX");
            		}
            		else{
            			for (i = 0; i < rows.length; i++) { 
                    		selectedWRNumbers[i] = rows[i]['workRequestNumber'];
                    	}
            			
            			$('#printWrNumber').val(selectedWRNumbers.toString());
                    	$('#printReportType').val("WorkRequest");

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
            { "title": "Work Request Number", data: "workRequestNumber", "defaultContent": "", "className":"noVis",
             "mRender": function ( sData, type, row, meta ) {
	            	if(sData == null){
	                    return sData;
	                } 
	            	else {
	                    return "<a onclick='viewWorkRequest(this)' name='"+sData+"' style='cursor: pointer'>"+sData+"</a>";
	                }
             }
            },
            { "title": "EAM Org", data: "eamOrg", "defaultContent": ""},
            { "title": "Asset Number", data: "assetNumber", "defaultContent": ""},
            { "title": "Asset Description", data: "assetDescription", "defaultContent": ""},
            { "title": "Work Request Type", data: "workRequestType", "defaultContent": ""},
            { "title": "Work Request Status", data: "workRequestStatus", "defaultContent": "", "width":"130px"},
            { "title": "Work Order Number", data: "woNumber", "defaultContent": "", "className":"noVis",
            	"mRender": function ( sData, type, row, meta ) {
	            	if(sData == null){
	                    return sData;
	                } 
	            	else {
	                    return '<a onclick="viewWorkOrder(this)" name="'+sData+'" style="cursor: pointer">'+sData+'</a>';
	                }
            }
            },
            { "title": "Work Order Status", data: "woStatus", "defaultContent": ""},
            { "title": "CM Breakdown Date Time", data: "cmBreakdownDate", "defaultContent": "", "width":"134px"},
            { "title": "PM Scheduled Date Time", data: "pmScheduleDate", "defaultContent": "", "width":"134px"},
            { "title": "Work Request Creation By", data: "createdby", "defaultContent": ""},
            { "title": "Maintenance Body", data: "maintenancevendor", "defaultContent": ""},
            { "title": "Location Code", data: "assetlocation", "defaultContent": ""},
            { "title": "Asset Owner", data: "assetowner", "defaultContent": ""},
            { "title": "Owning Department", data: "owningdept", "defaultContent": ""},
            { "title": "Work Request Creation Date Time", data: "createddate", "defaultContent": "", 
            	"mRender": function ( sData, type, row, meta ) {
	            	if(sData == null){
	                    return sData;
	                } 
	            	else {
	                    return moment(sData).format('DD/MM/YYYY HH:mm');
	                }
	            }
            },
            { "title": "Serial Number", data: "serialnumber", "defaultContent": ""},
            { "title": "Manufacturer", data: "manufacturer", "defaultContent": ""},
            { "title": "Brand", data: "brand", "defaultContent": ""},
            { "title": "Model", data: "model", "defaultContent": ""},
            { "title": "Work Request Description", data: "description", "defaultContent": ""}
        ]
    } );
    jQuery('.dataTable').wrap('<div class="dataTables_scroll" />');
    $('#wrSearchResults').DataTable().buttons().container().appendTo( $('.buttonCol') );
    $('#wrSearchResults').DataTable().on( 'select deselect', function () {
        var selectedRows =  $('#wrSearchResults').DataTable().rows( { selected: true } ).count();
        $('#wrSearchResults').DataTable().button( 5 ).enable( selectedRows > 0 );
    });
    
    if(restore){
    	$('#searchCriteria').collapse('hide');
        $('#searchAlert').hide();
    	$('#wrSearchBtn').button('loading');
        $('#wrSearchBtn').attr("disabled", true);
        $('#wrSearchResults').DataTable().processing( true );
		searchWorkRequests();
    }
}