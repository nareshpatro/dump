/* --------------------------------------
    File Name createRequests.js
    Author Carmen Ng (PCCW)
    Date September ‎21, ‎2017
    Description
    All functions used uniquely in the create work requests page.

    ---------- Modification History ----------
    20171228 by Frankie Lee
    Add textarea maxlength and attachment Title for UTF8 support
    
	20171215 by Carmen Ng
	Enable save button if there is an error returned
	Stop submit if there is an error
	
	20171213 by Carmen Ng
    Added fill in hidden fields before create		
	
	20171212 by Carmen Ng
	Redesigned how the previous mode message works
	
	20171208 by Carmen Ng
	Fixed issue where outstanding work order was being called on update
	Fixed issue where asset info button was not enabling
	
	20171207 by Carmen Ng
    Moved wrtype dropdown change check from commonfunctions
    If in update mode, get latest asset info instead
	
	20171206 by Carmen Ng
	Added checking for #remark
	Added if button disabled before form submit
	
    20171205 by Carmen Ng
    Cleaned up the code
    Added asset number validate boolean
    
	20171129 by Carmen Ng
	Added cookie for create work order button
	Hide/show create work order button according to status
	
	20171127 by Carmen Ng
	Modified the createEAMWR function
	Added delete attachment support
	Separated the getWorkRequest function
	
	20171122 by Carmen Ng
	Fixed bug where view WR attachment description is null
	Fixed issue where cancel WR did not remove attachment info
	
    20171120 by Carmen Ng
    Fixed bug where confirm modal was triggering the wrong confirmation
    
	20171116 by Carmen Ng
	Fixed ajax error message
	Fixed adding a new attachment in update mode
	Fixed message upon creating work request showing the wrong status and null wo number
	
	20171115 by Carmen Ng
	Removed error messages before restore defaults on cancel
	
	20171110 by Carmen Ng
	Changed cookie name for return
	Added populateAssetInfo
	
	20171102 by Carmen Ng
    Changed the term edit to update
	
    20171026 by Carmen Ng
    Fixed the bug where the upload button was not responding

    20171025 by Carmen Ng
    Added all attachments related functions

    20171020 by Carmen Ng
    Updated the different mode parameters to use POST instead of GET for security

    20171016 by Carmen Ng
    Implemented the printBTN function

    20170921 by Carmen Ng
    Initial version
   -------------------------------------- -*/
	var parameters;
	var WRNumber;
	var WRMode;
	var PreviousMode;

	var checked;
	var assetValidated;
	var userValidated;
	var inactiveAsset;
	
	var user = "";

$(document).ready(function(){
	page = 'sidebarCreateWr';

	checked = false;
	assetValidated = false;
	inactiveAsset = false;
	
	parameters = getParams();
	WRNumber = parameters[0];
	WRMode = parameters[1];
	PreviousMode =  parameters[2];
				
	//User validation functions
	if($('#hiddenUser').val() !== ""){
		userValidated = true;
	}
	
	$('#requestedFor').on({
        focus: function() {
        	user = $('#requestedFor').val();
        },
        blur: function() {
	    	if(user !== $('#requestedFor').val()){
	    		userValidated = false;
	    	}
        }
        
    });
	
	//remark for cancel/reject hint check
	$('#remark').on({
        blur: function() {
	    	if($('#remark').val() !== ""){
				$('#reasonsHint').hide();
	    	}
	    	else{
	    		$('#reasonsHint').show();
	    	}
        }
    });
	
	//wrtype dropdown change 
	$('#requestType').on('changed.bs.select', function () {
        var selected = $(this).find("option:selected").val();
//        if(selected === "Preventive"){
          if(selected === "20"){
            $('#breakdownDateInput').prop("required", false);
            $('#scheduleDateInput').prop("required", true);
            $('#scheduleDateInput').val('');
            $('#breakdownDateInput').val('');
            $( "#breakdownDate" ).hide();
            $( "#scheduleDate" ).show();
        }
        else {
            $('#scheduleDateInput').prop("required", false);
            $('#breakdownDateInput').prop("required", true);
            $('#scheduleDateInput').val('');
            $('#breakdownDateInput').val('');
            $( "#breakdownDate" ).show();
            $( "#scheduleDate" ).hide();
        }
        $('#wrForm').validator('update');
    }); 
	
	
    //Load modes, if work request number is available - get info
    if(WRNumber == ""){
        loadWRMode(WRMode);
    }
    else {
    	loadingScreen(true, "Retreiving Work Request");  
        loadWRMode(WRMode, WRNumber);
        getWrInfo(WRNumber);
    }

    //Button functions
    $("#editBTN").click(function(){
    	changeMode("e", WRNumber, "");
    });

    $("#printBTN").click(function(){
    	$('#postResultsBox').hide();
		$('#printWrNumber').val(WRNumber);
    	$('#printReportType').val("WorkRequest");
    	$('#printForm').submit();
    });

    $("#createWOBTN").click(function(){
    	setCookie("fromWR", WRNumber, 3600000);
    	$('#inWrNumber').val($('#wrNumber').val());
    	$('#inContactPerson').val( $('#contactPerson').val() );
    	$('#inContactPhone').val($('#contactPhone').val());
    	$('#inContactEmail').val($('#contactEmail').val());
    	$('#inBreakdownDateInput').val($('#breakdownDateInput').val());
    	$('#inScheduleDateInput').val($('#scheduleDateInput').val());
    	$('#inDisinfection').val($('#disinfectionReadOnly').val());
    	$('#inEquipmentSent').val($('#equipmentSentReadOnly').val());    	
		$('#createWoForm').submit();
    });
    
    $("#goToWOBTN").click(function(){
    	$('#viewWoNumber').val($(this).attr("name"));
		$('#viewWoForm').submit();
    });

    $('#backBTN').click(function(){
    	if(getCookie('lastSearch') == "WO"){
    		setCookie('returnWO', true, 60000);
    		window.location.href = "searchWorkOrder";
    	}
    	else{
    		setCookie('returnWR', true, 60000);
    		window.location.href = "searchWorkRequest";
    	}
    });
    
    $("#wrCancel").click(function(){
    	var cancelling = true;
    	
    	var cancelConfirm = function(cancelCallback){
    		$('#confirmationMsg').html("All unsaved changes will be cleared. Are you sure you would like to cancel?");
			$("#confirmationModal").modal('show');
		
			$("#confirmYesBTN").on("click", function(){
				cancelCallback(true);
				$("#confirmationModal").modal('hide');
			});
			  
			$("#confirmNoBTN").on("click", function(){
				cancelCallback(false);
				$("#confirmationModal").modal('hide');
			});
		};
		
		cancelConfirm(function(cancelConfirm){
			if(cancelConfirm && cancelling){
				if($('#newDocumentIds').val !== ""){
					$.ajax({
			         	type: 'POST',
			         	data :  {
			         		idType:"doc_id",
			         	    id: $('#newDocumentIds').val()
			            },
			             url : './DeleteAttachment',                        
			             success : function(data) {
			            	if(WRMode == 'e'){
			            		changeMode("v", WRNumber, PreviousMode);			 					
			 		        }
			 		        else {
			 		        	$.xhrPool.abortAll();
			 		        	window.location.href = "initCreateWorkRequest";
			 		        }            
			            	cancelling = false;
			             },
			             error : function(xhr, ajaxOptions, thrownError) {
			            	 if (xhr.statusText =='abort' || thrownError == "") {
			            		 cancelling = false;
			                     return;
			                 }
			                 
			             }

			         });
				}
			}else{
				cancelling = false;
				return false;
			}
		});
    });
    

    //Dropdown changes functions
    $('#wrStatus').on('changed.bs.select', function () {
        var selected = $(this).find("option:selected").val();
        if(selected == "7" || selected == "5"){
            $("#reasonRemark").show();
            $('#remark').prop('required', true);
            if($('#breakdownDate').is(':visible')){
        		$('label[for="breakdownDate"]').removeClass("required");
                $('#breakdownDateInput').prop('required', false);
            }
            $('#wrForm').validator('update');
        }
        else{
            $( "#reasonRemark" ).hide();
            $('#remark').removeAttr('required', false);
            if($('#breakdownDate').is(':visible')){
        		$('label[for="breakdownDate"]').addClass("required");
                $('#breakdownDateInput').prop('required', true);
            }
            $('#wrForm').validator('update');
        }
    });

    //Get asset info on blur
    $('#assetNumber').on({
        blur: function() {
        	if($('#assetNumber').val() == ""){
        		$('#assetInfoBTN').button('reset');
        	    $('#assetNumberHelp').hide();
        	    $('.assetInfoWrapper').hide();
        	}
        	else{
        		if(!$('#assetNumber').is('[readonly]')){
        			getAssetInfo($('#assetNumber').val(), false);
        		}
        	}
        }
    });

    //Makes sure enter does not submit the form in description
    $(window).keydown(function(event){
        if(event.keyCode === 13 && !$('#description').is(":focus")) {
          event.preventDefault();
          return false;
        }
    });
    
    //Upload attachment validations
    $("#attachmentUpload").change(function (){
	    if($('#attachmentTitle').val() !== ""){
	    	$('#uploadBTN').addClass("enabled");
	    	$('#uploadBTN').attr("disabled", false);
	    }
	});
    
    $('#attachmentTitle').on({
        blur: function() {
        	if($('#attachmentUpload').val() !== "" && $('#attachmentTitle').val() !== ""){
    	    	$('#uploadBTN').addClass("enabled");
    	    	$('#uploadBTN').attr("disabled", false);
    	    }
        }
    });

    //Submitting work request form event
    $('#wrForm').validator().on('submit', function (e) {
		if (!e.isDefaultPrevented()) {
			e.preventDefault();
			
			//If attachment area is opened, make sure things have been uploaded/completed
			if($('#attachmentFields').is(':visible')){
				$('#frameWrapper').show();
				$('#attachmentAlert').removeClass("alert-success");
                $('#attachmentAlert').addClass("alert-danger");
				$('#attachmentAlert').html("<strong><span class='glyphicon glyphicon-exclamation-sign'></span> Error!</strong> Please complete or cancel upload attachment.");
            	$('#attachmentAlert').show();
				return;
			}

			if(!assetValidated){
				$('#assetInfoBTN').button('reset');
				setTimeout(function() {
					$('#assetInfoBTN').prop('disabled', true);
				}, 0);
				$('#assetNumberGroup').addClass("has-error");
				$('#assetNumberGroup').addClass("has-danger");
				$('#assetNumberHelp').show();       
				$('.assetInfoWrapper').hide();
				return;
			}									  
			
			if(inactiveAsset){
				if($("#wrStatus").val() !== "5" && $("#wrStatus").val() !== "7"){
					alertMessage("postResultsBox", "HA_ERROR_WR_INACTIVEASSETSTATUS");
					$("#wrStatus").closest(".form-group").addClass("has-error has-danger");
					return;
				}
			}
			
			$('#wrSave').prop("disabled", true);
			loadingScreen(true, "Validating Input Fields");  
			
			//Fill in hidden fields
			if(!userValidated){
        		$.post("./LovLookup", { value: $('#requestedFor').val(), query: 1}, function(data){
            		if(data == "No Match"){
            			loadingScreen(false, "");  
            			$('#wrSave').prop("disabled", false);
            			$( "#requestedForFiltered.lovFieldSearchBtn" ).trigger( "click" );
        				alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
            			return;
            		}
            		else {
            			$('#hiddenUser').val(data);
            			userValidated = true;
            			
            			var workRequest = getWorkRequest();            		
            			//if (workRequest["mode"] == 'CREATE' || workRequest["mode"] == 'UPDATE'){
            			if (workRequest["wrStatusText"] != 'Rejected' && workRequest["wrStatusText"] != 'Cancelled by User' && (workRequest["mode"] == 'CREATE' || workRequest["mode"] == 'UPDATE')){
            				$.ajax({
            					url:'./checkOSWorkRequest',
            					type:'post',
            					contentType: 'application/json',
            					data : JSON.stringify(workRequest),
            					success:function(data){
            						if (data == 'Y'){
            							createEAMWR(WRMode, WRNumber);
            						} 
            						else {
            							if (data.substr(0, 3) == '[N]'){ //do not allow WR creation
            								$('#notificationMsg').html(data.substr(3,data.length-3));
            								$("#notificationModal").modal('show');
            								$('#notifyOkay').on("click", function(){
            									loadingScreen(false, "");  
            									$("#notificationModal").modal('hide');
            									$('#wrSave').prop("disabled", false);
            									}
            								)
            							}else{								
            								var creating = true;
            								//Outstanding WR found, need user confirm					
            								var modalConfirm = function(createCallback){
            									$('#confirmationMsg').html(data);
            									$("#confirmationModal").modal('show');
            								
            									$("#confirmYesBTN").on("click", function(){
        											createCallback(true);
        											$("#confirmationModal").modal('hide');
            									});
            									  
            									$("#confirmNoBTN").on("click", function(){
        											createCallback(false);
        											$("#confirmationModal").modal('hide');
            									});
            								};
            								
            								modalConfirm(function(createConfirm){
            									if (createConfirm && creating){
            										createEAMWR(WRMode, WRNumber);
            									} else{
            										loadingScreen(false, "");  
            										$('#wrSave').prop("disabled", false);
            									}
        										creating = false;
            								});
            							}
            						}
            					}
            				});
            			}
            			else{
            				createEAMWR(WRMode, WRNumber);
            			}
            		}
            	});
            }
			else{
				var workRequest = getWorkRequest();            		
    			if (workRequest["wrStatusText"] != 'Rejected' && workRequest["wrStatusText"] != 'Cancelled by User' && (workRequest["mode"] == 'CREATE' || workRequest["mode"] == 'UPDATE')){
    				$.ajax({
    					url:'./checkOSWorkRequest',
    					type:'post',
    					contentType: 'application/json',
    					data : JSON.stringify(workRequest),
    					success:function(data){
    						if (data == 'Y'){
    							createEAMWR(WRMode, WRNumber);
    						} 
    						else {
    							if (data.substr(0, 3) == '[N]'){ //do not allow WR creation
    								$('#notificationMsg').html(data.substr(3,data.length-3));
    								$("#notificationModal").modal('show');
    								$('#notifyOkay').on("click", function(){
    									loadingScreen(false, "");  
    									$("#notificationModal").modal('hide');
    									$('#wrSave').prop("disabled", false);
    									}
    								)
    							}else{								
    								var creatingConfirmation = true;
    								//Outstanding WR found, need user confirm					
    								var modalConfirm = function(createCallback){
    									$('#confirmationMsg').html(data);
    									$("#confirmationModal").modal('show');
    								
    									$("#confirmYesBTN").on("click", function(){
											createCallback(true);
											$("#confirmationModal").modal('hide');
    									});
    									  
    									$("#confirmNoBTN").on("click", function(){
											createCallback(false);
											$("#confirmationModal").modal('hide');
    									});
    								};
    								
    								modalConfirm(function(createConfirm){
    									if (createConfirm && creatingConfirmation){
    										createEAMWR(WRMode, WRNumber);
    									} else{
    										loadingScreen(false, "");  
    										$('#wrSave').prop("disabled", false);
    									}
    									creatingConfirmation = false;
    								});
    							}
    						}
    					}
    				});
    			}
    			else{
    				createEAMWR(WRMode, WRNumber);
    			}
			}
		}
	});
    
 	// Text area maxlength for UTF8
    $("textarea").on('keydown keyup paste',function(){
        var $that = $(this),
        maxlength = $that.attr('maxlength');

        if($.isNumeric(maxlength) && maxlength > 0){
            var realLen = encodeURIComponent($that.val()).replace(/%[A-F\d]{2}/g, 'U').length;
            var newLines = $that.val().match(/(\r\n|\n|\r)/g);

            if (newLines !== null) {
                realLen += newLines.length;
            }
            var currLen = $that.val().length;
            if (realLen > maxlength) {
                do {
                    $that.val($that.val().substr(0, currLen));
                    currLen --;
                    realLen = encodeURIComponent($that.val()).replace(/%[A-F\d]{2}/g, 'U').length;
                    newLines = $that.val().match(/(\r\n|\n|\r)/g);
                    if (newLines !== null) {
                        realLen += newLines.length;
                    }
                } while (realLen > maxlength);
            }
        };
    });
    
    $('#attachmentTitle').on('keydown keyup paste',function(){
        var $that = $(this),
        maxlength = $that.attr('maxlength');

        if($.isNumeric(maxlength) && maxlength > 0){
            var realLen = encodeURIComponent($that.val()).replace(/%[A-F\d]{2}/g, 'U').length;
            var currLen = $that.val().length;
            if (realLen > maxlength) {
                do {
                    $that.val($that.val().substr(0, currLen));
                    currLen --;
                } while (encodeURIComponent($that.val()).replace(/%[A-F\d]{2}/g, 'U').length > maxlength);
            }
        };
    });
});

function loadWRMode(mode, wrnumber){
    //mode: c = create, v = view, e = edit
    if(mode === "e"){
		checked = true;
		userValidated = true;
        $(document).prop('title', 'EAM - Update Work Request');
        loadCommon("glyphicon-edit", "Edit", "Update Work Request #" + wrnumber);
        //Set hidden fields' values
        $('#workRequestId').attr('value', WRNumber);
        $('#modeInput').attr('value', "UPDATE");
		$('#attachmentMode').attr('value', "NEW");
		//Turns fields on and off, readonly, editable etc.
        $("#wrNumberStatusWrapper").show();
        $('#wrNumber').attr("disabled", true);
        $('#wrNumber').attr('readonly', 'readonly');
        $("#createWRToolbar").hide();
        $("#wrSave").val('Update Work Request');
        $("#requestDetailsTitle").html('Request Details');
        $("#contactDetailsTitle").html('Contact Details');
        $("#attachmentsTitle").html('Attachments');
        $("#assetNumber").attr('readonly', 'readonly');
        $("#workOrder").attr('readonly', 'readonly');
        $("#addAttachmentBTN").show();
        $("#attachmentFields").hide();
        $("#attachedFiles").show();
        $('#descriptionHistoryWrapper').show();
        $('#descriptionLabel').html("Additional Work Request Description");
        $('#descriptionLabel').removeClass("required");
        $('#description').removeAttr("required");
        $('#assetNumberFiltered.lovFieldSearchBtn').hide();
        $('#requestTypeWrapper').hide();
        $('#requestTypeReadOnly').show();
		$('#attachmentBtnList').show();
		$('label[for="assetNumber"]').removeClass("required");
		$('label[for="requestType"]').removeClass("required");
		$('#assetOrgWrapper').hide();
		$('#wrOrgWrapper').show();
    }
    else if(mode === "v"){
        $(document).prop('title', 'EAM - View Work Request');
        loadCommon("glyphicon-list-alt", "View", "View Work Request #" + wrnumber);
        //Turns fields on and off, readonly, editable etc.
        $('.required').removeClass("required");
        $("#wrNumberStatusWrapper").show();
        $("#wrForm input").attr('readonly', 'readonly');
        $("#wrForm textarea").prop("required", false);
        $("#wrForm textarea").attr('readonly', 'readonly');
        $('#equipmentSent').hide();
        $("#wrForm small").hide();
        $("#wrForm .radio").attr("disabled", true);
        $("#saveBTNs").hide();
        $("#addAttachmentBTN").hide();
        $("#attachmentFields").hide();
        $("#attachedFiles").show();
        $('#requestedFor.lovFieldSearchBtn').hide();
        $('#assetNumberFiltered.lovFieldSearchBtn').hide();
        $('.input-group-addon').hide();
        $('.removeButton').removeClass("input-group");
        $('.selectReadOnly').show();
        $('.assetInfoWrapper').show();
        $('#descriptionHistoryWrapper').show();
        $('#descriptionWrapper').hide();
        $('.form-group').addClass("border");
        $('#breakdownDate, #scheduleDate').removeClass("border");
		$('.hideDropdowns').hide();
		$('#attachmentBtnList').hide();	
		$('#requestedForFiltered').hide();	
		$('.removeBorder').removeClass("border");
		$('#assetOrgWrapper').hide();
		$('#wrOrgWrapper').show();
    }
    else {
        loadCommon("glyphicon-edit", "Create", "Create Work Request");
         //Set hidden fields' values
        $('#workRequestId').attr('value', '');
        $('#modeInput').attr('value', "CREATE");
        $('#attachmentMode').attr('value', "NEW");
        //Turns fields on and off, readonly, editable etc.
        $("#wrNumberStatusWrapper").hide();
        $("#woNumberStatusWrapper").hide();
        $("#createWRToolbar").hide();
        $("#workOrderInfo").hide();
        $("#wrSave").html('Create Work Request');
        $("#requestDetailsTitle").html('Request Details');
        $("#contactDetailsTitle").html('Contact Details');
        $("#attachmentsTitle").html('Attachments');
        $("#addAttachmentBTN").show();
        $("#attachmentFields").hide();
        $("#attachedFiles").hide();
        $('#disinfection').selectpicker('val', "NOT NECESSARY");
    }
};

function getWrInfo(WRNumber){
	var wStatus = [];
    var search = {org: '',
            wrNumber: WRNumber,
            assetNumber: '',
            wrType: '',
            wrStatus: wStatus,
            dateType: '',
            dateFrom: '',
            dateTo: '',
            maintenanceVendor: '',
            assetLocation: '',
            assetOwner: '',
            owningDept:'',
            createdBy: '',
            criticalOnly: ''};
        
		$.ajax({
	     	type: 'POST',
	     	contentType: 'application/json',
	     	data : JSON.stringify(search),   
	         url : './processWorkRequestDetail',                        
	         dataType:'json',
	         success : function(data) {
	        	    populateWrInfo(data);
					 loadingScreen(false, "");       
	        	    
	        	    var message = "";
	        	    //Checks where the info came from if from anywhere at all
	        	    if(PreviousMode == ""){
	        	    	$('#postResultsBox').hide();
	        	    	return;
	        	    }
	        	    else if(PreviousMode == "e"){
	        	    	if(data[0].workRequestStatus !== "On Work Order"){
	        	    		message = "Work request #" + data[0].workRequestNumber + " has been updated successfully.";
	        	    	}
	        	    	else if (data[0].workRequestStatus == "On Work Order" ){
	        	    		message = "Work request #" + data[0].workRequestNumber + " has been updated in status: On Work Order. Work Order #" + data[0].woNumber + " has been created.";
	        	    	}
	        	    }
	        	    else if (PreviousMode == "c"){
	        	    	if(data[0].workRequestStatus == "Awaiting Work Order"){
	        	    		message = "Work request #" + data[0].workRequestNumber + " has been created in status: Awaiting Work Order.";
	        	    	}
	        	    	else if (data[0].workRequestStatus == "On Work Order" ){
	        	    		message = "Work request #" + data[0].workRequestNumber + " has been created in status: On Work Order. Work Order #" + data[0].woNumber + " has been created.";
	        	    	}
	        	    }
	        	    alertMessage("postResultsBox", message, "success");
	         },
	         error : function(xhr, ajaxOptions, thrownError) {
	        	 if (xhr.statusText =='abort' || thrownError == "") {
	                 return;
	             }
	             
	         }
	    });
}

function populateWrInfo(data){
	var info = data;
    //get and populate asset information
	var assetNum = info[0].assetNumber;

    assetValidated = true;
    populateAssetInfo(data); 
    
    if(WRMode == 'e'){
    	getAssetInfo(assetNum, true);
    }

    //populate with wr information
    $('#wrNumber').val(info[0].workRequestNumber);
    $('#wrStatusReadOnly').val(info[0].workRequestStatus);
    $('#wrStatusReadOnly').selectpicker('refresh');
    $('#wrStatus').val(info[0].wrStatus);    
    $('#wrStatus').selectpicker('refresh');
    $('#assetNumber').val(assetNum);
    $('#requestTypeReadOnly').val(info[0].workRequestType);
    $('#requestType').val(info[0].wrType);
    $('#requestType').selectpicker('refresh');
    $('#equipmentSentReadOnly').val(info[0].equipmentSent);
    $("input[name=equipmentSent][value=" + info[0].equipmentSent + "]").attr('checked', 'checked');
    $('#disinfectionReadOnly').val(info[0].disinfection);
    if(info[0].disinfection == null || info[0].disinfection == ""){
    	$('#disinfection').selectpicker('val', "NOT NECESSARY");
    }
    else {
    	$('#disinfection').val(info[0].disinfection);
    }
    $('#disinfection').selectpicker('refresh');
    $('#contactPhone').val(data[0].haContactPhone); 
    $('#contactEmail').val(data[0].haContactEmail); 
    $('#contactEmailReadOnly').html(data[0].haContactEmail); 
    $('#contactPerson').val(data[0].haContactPerson); 
    $('#contactPersonReadOnly').html(data[0].haContactPerson); 
    $('#descriptionHistory').val(info[0].descriptionHistory);
    $('#lastUpdateDate').val(data[0].lastUpdateDate);
    $('#wrOrg').val(data[0].workRequestOrg);

    //check if requested for is legit
    if(info[0].requestedFor !== -1){
        $('#requestedFor').val(info[0].requestedForEmployee);
        $('#hiddenUser').val(info[0].requestedFor);
    }
   
    //hide/show according to type
    if(info[0].wrType === "10"){
        $('#scheduleDateInput').prop("required", false);
        $('#breakdownDateInput').prop("required", true);
        if(info[0].hasOwnProperty("breakdownDateInput")){
            $('.noFutureDate').data("DateTimePicker").date(info[0].breakdownDateInput);
        }
        $('#breakdownDate').show();
        $('#scheduleDate').hide();
    }
    else{
        $('#breakdownDateInput').prop("required", false);
        $('#scheduleDateInput').prop("required", true);
        if(info[0].hasOwnProperty("scheduleDateInput")){
	    	$('#scheduleDateInput').val(info[0].scheduleDateInput);
            $('.noPastDate').data("DateTimePicker").date(info[0].scheduleDateInput);
        }
        $('#scheduleDate').show();
        $('#breakdownDate').hide();
    }
	
    if(info[0].wrStatus == "7" || info[0].wrStatus == "5"){
        $('#editBTN').hide();
        $('#remark').val(info[0].remark);
        $('#reasonRemark').show();
        $('#reasonsHint').hide();
    }


    //if work order information applicable, populate
    if(info[0].woNumber){
    	$('#goToWOBTN').attr('name', info[0].woNumber);
        $("#woNumberStatusWrapper").show();
        $('#woNumber').val(info[0].woNumber);
        $('#woStatusReadOnly').val(info[0].woStatus);
        $('#woStatusReadOnly').show();
        $('#woStatus').val(info[0].woStatus);
        $('#woStatus').selectpicker('refresh');
        $("#woNumberStatusWrapper").show();
        $('#editBTN').hide();
        $('#goToWOBTN').show();
    }
    else {
    	if($('#createWOBTN').length){
    		if(info[0].wrStatus !== "3"){
                $('#createWOBTN').hide();
            }
    		else {
                $('#createWOBTN').show();
            }
    	}
    }

    //populate attachment information
    var attachmentDesc;
	for (var i = 0; i < info[0].attachmentInfo.length; i++) {		
		attachmentDesc = "";
		if(info[0].attachmentInfo[i].description !== null){
			attachmentDesc = info[0].attachmentInfo[i].description;
		}
		recordAttachment(info[0].attachmentInfo[i].title, attachmentDesc, info[0].attachmentInfo[i].fileName, info[0].attachmentInfo[i].documentId);
	}
	
	if(WRMode == 'v'){
		$('.deleteAttachment').hide();
	}
	
	$('#wrForm').validator('update');								
}

//function clearWrInfo(){
//    $("#wrForm input").val('');
//    $("#wrForm textarea").val('');
//    $("#wrForm select").val('');
//    $('#requestType').selectpicker('refresh');
//    $('#disinfection').selectpicker('refresh');
//    $('input:radio[name="equipmentSent"]').prop('checked', false);
//    $('#assetInfoBTN').button('reset');
//    $('#assetInfoBTN').attr('disabled', true);
//    $('#postResultsBox').hide();
//    $('.assetInfoWrapper').hide();
//    $('.text-danger').hide();
//    $('.has-error').removeClass("has-error");
//    $('.has-danger').removeClass("has-danger");
//    $('#wrSave').val("Create Work Request");
//    $('#hiddenUser').val("");
//    //$('#wrSave').addClass("disabled");
//	$('.help-block').hide();
//	//commonly used with work order attachments, function found in common functions
//	clearAttachmenInfo();	
//	//found on jsp page as it needs session information									   
//    restoreDefaultForm();
//    $('html,body').animate({ scrollTop: 0 }, 'slow');$('html,body').animate({ scrollTop: 0 }, 'slow');
//}

//retrieves asset information related to asset number
function getAssetInfo(assetNumber, update){
    $('#assetInfoBTN').button('loading');
    $.post("./getAssetInfo", { assetNumber: assetNumber}, function(data){
    	if(data.length !== 0){
    		validateAsset(true);
    		populateAssetInfo(data);    
    	}
    	else if(update){
    		validateAsset(true);
    		alertMessage("postResultsBox", "HA_INFO_INACTIVEASSET");
    		inactiveAsset = true;
    	}
    	else {
			validateAsset(false);
	    }
    });
}

//enters the values for the form and submits form to change modes, pass parameters etc.
function changeMode(mode, wrNumber, previous){
	$('#modeParameter').val(mode);
	$('#wrNumberParameter').val(wrNumber);
	$('#previousParameter').val(previous);
	$('#passParamtersForm').submit();	
}

//Populate asset information for asset number input
function populateAssetInfo(data){
        $('#assetOrgModal').html(data[0].assetOrganisation);
        $('#assetInfoTitle').html("Asset #" + data[0].assetNumber);
        $('#aDescription').html(data[0].assetDescription);
        $('#aItemCode').html(data[0].itemCodeNumber);
        $('#aStatus').html(data[0].assetStatus);
        $('#aLocation').html(data[0].assetlocation);
        $('#aOwner').html(data[0].assetOwner);
        $('#owingDept').html(data[0].owningDept);  
        $('#parentAsset').html(data[0].fatherAssetNumber);
        $('#manufacturer').html(data[0].manufacturer); 
        $('#brand').html(data[0].brand); 
        $('#model').html(data[0].model); 
        $('#dob').html(data[0].dob);
        $('#serialnumber').html(data[0].serialnumber); 
        $('#purchasePrice').html(data[0].purchasePrice); 
        $('#aSupplier').html(data[0].assetSupplier); 
        $('#riskLevel').html(data[0].riskLevel);
        $('#assetOrg').val(data[0].assetOrganisation);
        $('#assetDescription').html(data[0].assetDescription);
        $('#maintenanceBody').html(data[0].maintenanceBody);  
        $('#maintenanceDetailsPopUp').show();
        $('#mBody').html(data[0].maintenanceBody);  
        $('#mBodyType').html(data[0].maintenanceBodyType);  
        $('#autoSend').html(data[0].autoSend);  
        $('#mPlan').html(data[0].maintenancePlan);  
        $('#mContactPhone').html(data[0].maintPhone);  
        $('#mJoinDate').html(data[0].maintenanceJoinDate);  
        $('#mContactFax').html(data[0].maintFax);  
        $('#mExpiryDate').html(data[0].maintenanceExpiryDate); 
        $('#contractNumber').html(data[0].contractNumber); 
        $('#supplierNumber').html(data[0].supplierAgreementNumber); 
        $('#mContactPerson').html(data[0].maintContact); 
        $('#mContactEmail').html(data[0].maintEmail); 
        $('#mInterval').html(data[0].maintenanceInterval); 
        $('#aLocation').prop('title', data[0].assetLocationDesc);
        $('#aOwner').prop('title', data[0].assetOwnerDesc);
        $('#owingDept').prop('title', data[0].assetOwningDepartmentDesc);
        $('[data-toggle="tooltip"]').tooltip();
        $('#assetInfoBTN').prop("disabled", false);
}

function getWorkRequest(){
	var workRequest = {};
	workRequest["workRequestId"] = $("#workRequestId").val();
	workRequest["wrNumber"] = $("#wrNumber").val();
	workRequest["wrStatus"] = $("#wrStatus").val();
	workRequest["wrStatusText"] = $('#wrStatus option:selected').html();
	workRequest["woNumber"] = $("#woNumber").val();
	workRequest["woStatus"] = $("#woStatus").val();
	workRequest["assetNumber"] = $("#assetNumber").val();
	workRequest["requestType"] = $("#requestType").val();
	workRequest["breakdownDateInput"] = $("#breakdownDateInput").val();
	workRequest["scheduleDateInput"] = $("#scheduleDateInput").val();
	workRequest["equipmentSent"] = $('input:radio[name=equipmentSent]:checked').val()
	workRequest["disinfection"] = $("#disinfection").val();
	workRequest["description"] = $("#description").val();
	workRequest["requestedFor"] = $("#hiddenUser").val();
	workRequest["remark"] = $("#remark").val();
	workRequest["contactPerson"] = $("#contactPerson").val();
	workRequest["contactPhone"] = $('#contactPhone').val();
	workRequest["contactEmail"] = $('#contactEmail').val();
	workRequest["mode"] = $('#modeInput').val();
	workRequest["attachmentMode"] = $('#attachmentMode').val();
	workRequest["docId"] = $('#newDocumentIds').val();
	workRequest["lastUpdateDate"] = $("#lastUpdateDate").val();
	return workRequest;
}

function validateAsset(validated){
	if(validated){
		$('#assetInfoBTN').button('reset');
        setTimeout(function() {
            $('#assetInfoBTN').prop('disabled', false);
        }, 0);
        $('#assetNumberGroup').removeClass("has-error");
        $('#assetNumberGroup').removeClass("has-danger");
        $('#assetNumberHelp').hide();       
        if(document.getElementsByClassName("assetInfoWrapper") !== null){
            $('.assetInfoWrapper').show();
        }
        assetValidated = true;
//        if($("#wrSave").hasClass("disabled") && checked){
//		   $("#wrForm").validator('validate');
//		   checked = false;
//		}
	}
	else {
		$('#assetInfoBTN').button('reset');
        setTimeout(function() {
            $('#assetInfoBTN').prop('disabled', true);
        }, 0);
        $('#assetNumberGroup').addClass("has-error");
        $('#assetNumberGroup').addClass("has-danger");
        $('#assetNumberHelp').show();       
        $('.assetInfoWrapper').hide();
        //$("#wrSave").addClass("disabled");
        assetValidated = false;
	}
}

function createEAMWR(WRMode, WRNumber){
	var workRequest;
	if(WRMode == 'e'){
		loadingScreen(true, "Updating Work Request");  
	}
	else{
		loadingScreen(true, "Creating Work Request");  
	}
	
	if($('#delDocumentIds').val() !== ""){
		$.ajax({
         	type: 'POST',
         	data :  {
         		idType:"doc_id",
         	    id: $('#delDocumentIds').val()
            },
            url : './DeleteAttachment',                        
            success : function(data) {
            	if(data.substring(0, 5) !== "ERROR"){
            		//determines whether to add comma to list
        		 	if($('#newDocumentIds').val() == ""){
        		 		$('#newDocumentIds').val(data);
        		 	}
        		 	else {
        		 		$('#newDocumentIds').val($('#newDocumentIds').val() + "," + data);
        		 	}
            		
            		workRequest = getWorkRequest();

        		    $.ajax({
        		        url:'./createWorkRequest',
        		        type:'post',
        		    	contentType: 'application/json',
        		    	data : JSON.stringify(workRequest),   
        		        success:function(data){
        		        	//check if numbers are returned
        		            if(data.substring(0,data.indexOf("&")).match(/^[0-9]+$/)){
        		            	WRNumber =  data.substr(0, data.indexOf("&"));
        		                if(WRMode == 'e'){
        		                	changeMode("v", WRNumber, "e");
        		                }
        		                else{
        			                changeMode("v", WRNumber, "c");
        		                }
        		            }
        		            //error has occured in creating/updating
        		            else {
        		                alertMessage("postResultsBox", data, "error");
        		                loadingScreen(false, "");  
        		                $('#wrSave').prop("disabled", false);
        		            }
        		        }
        		    });
            	}
            	//if returned string is "ERRROR..."
            	else {
            		alertMessage("postResultsBox", "Delete attachment error: " + data, "error");
            	}
            },
            error : function(xhr, ajaxOptions, thrownError) {
            	if (xhr.statusText =='abort' || thrownError == "") {
                    return;
                }
                
            }
         });
	//if there are no attachments to delete
	} else {
		workRequest = getWorkRequest();

	    $.ajax({
	        url:'./createWorkRequest',
	        type:'post',
	    	contentType: 'application/json',
	    	data : JSON.stringify(workRequest),   
	        success:function(data){
				//check if numbers are returned
	            if(data.substring(0,data.indexOf("&")).match(/^[0-9]+$/)){
	            	WRNumber =  data.substr(0, data.indexOf("&"));
    	            if(WRMode == 'e'){
	                	changeMode("v", WRNumber, "e");
	                }
	                else{        		    	           	                
		                changeMode("v", WRNumber, "c");
	                }
	            }
	            //error has occured in creating/updating
	            else {
	                alertMessage("postResultsBox", data, "error");
	                loadingScreen(false, "");  
	                $('#wrSave').prop("disabled", false);
	            }
	        }
	    });
	}		
}