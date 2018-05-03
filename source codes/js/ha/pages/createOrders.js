/* --------------------------------------
    File Name createOrders.js
    Author Carmen Ng (PCCW)
    Date September ‎22, ‎2017
    Description
    All functions used uniquely in the create work orders page.

    ---------- Modification History ----------
    20171228 by Frankie Lee
    Add textarea maxlength and attachment Title for UTF8 support
    
	20171215 by Carmen Ng
	Stopped submit when there's errors
	Added delete attachments to update/create work order
	
	 20171214 by Carmen Ng
    Added functions for contact method lov
	Changed check date validation to also catch which fields
    
	20171213 by Carmen Ng
    Remove update button depending on status		
    			 
	20171212 by Carmen Ng
	Fixed asset not valid on update bug
	
	20171211 by Carmen Ng
	Fixed missing asseet org
	Removed value from resolution code before disabled
	Doesn't update asset on update WO now
	
	20171208 by Carmen Ng
	Fixed issue where asset info button was not enabling
	Fixed issue where view wo wasn't checking wr type for field check
	Added fields that did not match populate asset info names for view mode
	
     20171207 by Carmen Ng
     Latest asset info is now retrieved for update and copy from wr modes
     Cleaned up code and added asset validation
     
     20171206 by Carmen Ng
     Removed resetfileinfo which was causing errors
     Updated populate asset info with correct info
     
     20171205 by Carmen Ng
     Fixed issue where input groups weren't fill width during view
     
     20171204 by Carmen Ng
     Added field validations
     Updated checkStatuses to take status names instead of values
     
     20171129 by Carmen Ng
     Added if create is from work request's create work order button checking
     Added contact method checking
     Added work order status checking
     Added work order status default
     Auto fill maintenance number when hiddenmbody has been changed
     
     20171127 by Carmen Ng
     Added support for delete attachments
     
    20171122 by Fanny Hung
    Add Create Order function
    
     20171116 by Carmen Ng
     Fixed ajax error message
     
     20171115 by Carmen Ng
     Removed error messages on restore to default
     
     20171109 by Carmen Ng
    Added function for update button
    
    20171108 by Carmen Ng
    Added all available fields to populateWOinfo
    Added populateAssetInfo function
     20171102 by Carmen Ng
    Changed the term edit to update
     
    20171020 by Carmen Ng
    Rebuilt all necessary functions/ids/names to accommadate new model

    20170922 by Carmen Ng
    Initial version
   -------------------------------------- -*/

     var parameters;
     var WONumber;
     var WOMode;
     var PreviousMode;

     var checked;
     var assetValidated;
 	 var inactiveAsset;
     
     var WoSearch;
     
     var checkXML;
     var checkAttachmentOnly;
     
     var startingStatus;
     var startingAutoSend;
     var startingSchedule;
     
     var resending;
     
     var mBody = "";

$(document).ready(function(){
     page = 'sidebarCreateWo';
     
     checked = false;
     assetValidated = false;
     WoSearch = true;
     checkXML = false;
     inactiveAsset = false;
     checkAttachmentOnly = false;
     resending = false;
     
     parameters = getParams();
     WONumber = parameters[0];
     WOMode = parameters[1];
     PreviousMode =  parameters[2];    
              
    $('#woForm').validator();

     //add validator and custom asset number validation to form
//     $(':input[required]').on({
//          blur: function() {
//               if(!assetValidated){
//                    if(!$("#woSave").hasClass("disabled")){
//                         $("#woSave").addClass("disabled");
//                         checked = true;
//                    }
//               }
//          }
//     });
     
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
         			getAssetInfo($('#assetNumber').val());
         		}
         	}
         }
     });

     if(WONumber == ""){
          loadWOMode(WOMode);

          //If work request number is available, get work request info
          if ($('#wrNumber').val() !== "" ) { 
               loadingScreen(true, "Retrieving Work Request");
               var wStatus = [];
               var search = {org: '',
                    wrNumber: $('#wrNumber').val(),
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
                    criticalOnly: ''
               };
     
               $.ajax({
                    type: 'POST',
                    contentType: 'application/json',
                    data : JSON.stringify(search),   
                         url : './processWorkRequestDetail',                        
                         dataType:'json',
                         success : function(data) {
                              copyWRInfo(data);
                              $('#backBTN').hide();
                              loadingScreen(false, "");  
                              PreviousMode = "wrSearch";
                       },
                       error : function(xhr, ajaxOptions, thrownError) {
                         if (xhr.statusText =='abort' || thrownError == "") {
                              return;
                         }
                         
                    }
               });
        }
    }
    else {
    	  loadingScreen(true, "Retreiving Work Order");
          loadWOMode(WOMode, WONumber);

          //get work order info
          var wStatus = [];
          var search = {eamOrg: '',
               woNumber: WONumber,
               woStatus: wStatus,
               dateType: '',
               dateFrom: '',
               dateTo: '',
               maintenanceVendor: '',
               assetLocation: '',
               assetOwner: '',
               owningDept:'',
               createdBy: '',
               criticalOnly: '',
               assetRisk: '',
               maintenanceContract: '',
               serialNumber: '',
               urgentOnly: '',
               woMode: WOMode
          };        

          $.ajax({
               type: 'POST',
               contentType: 'application/json',
               data : JSON.stringify(search),   
               url : './processWorkOrderDetail',                        
               dataType:'json',
               success : function(data) {
                    //Checks where the info came from if from anywhere at all
                    if(PreviousMode == ""){
                        $('#postResultsBox').hide();
                    }
                    else if (PreviousMode == "noAttachment"){
                  	     alertMessage("postResultsBox", "HA_INFO_NOATTACHMENTUPDATED");
                    }
                    else if (PreviousMode.indexOf("Error-") != -1){
                    	var message = PreviousMode.substring(6, PreviousMode.length);
                    	alertMessage("postResultsBox", message, "error");
                    }
                    else {
                        $('#postResultsBox').addClass("alert-success");
                        $('#postResultsBox').removeClass("alert-danger");
                        $('#postResultsBox').html(PreviousMode);
                        $('#postResultsBox').show();
                    }
                    
                    if(WOMode == "e" && (data[0].woStatus !== "Cancelled" && data[0].woStatus !== "Cancelled By PM" && data[0].woStatus !== "Rejected" && data[0].woStatus !== "Complete - Pending Close" && data[0].woStatus !== "Closed")){
             		   //If update then check if asset is active first
                    	populateWOInfo(data);
                    	loadingScreen(true, "Checking Asset Info");
                    	$.post("./chkAssetEnabled", { assetNumber: data[0].assetNumber, orgCode: data[0].assetOrg}, function(assetData){
                            if(assetData == "N"){
                            	$('#maintenanceVendorWrapper').hide();
                                $('#maintenanceVendorReadOnly').show();
                                $('label[for="maintenanceVendor"]').removeClass("required");
                                $('#maintenanceVendorNum').hide();
                                $('#mainBodyTypeReadOnly').show();     
                                $('#autoSendEquipReadOnly').show();
                                $('.maintenanceHide').hide();
                                $('.maintenance').prop('readonly', true);
                                $('#maintenanceDetailsPanel .form-group').addClass("border");
                                $('.removeBorder').removeClass("border");
                   	        	$('#woTypeReadOnly').show();
                   	        	$('#disinfectionStatusReadOnly').show();
                   	        	$('.equipmentSentReadOnly').show();
                   	        	$('.orderHide').hide();
                   	        	$('.order').prop('readonly', true);
                   	        	$('#orderDetailsPanel .form-group').addClass("border");
                                $('.removeBorder').removeClass("border");
                   	        	$('#contactMethod').hide();
                   	        	$("#woStatus option:contains('Unreleased')").hide();
                   	        	$("#woStatus option:contains('Released')").hide();
                   	        	$("#woStatus option:contains('On Hold')").hide();
                   	 	        $('#woStatus').selectpicker('refresh');
                   	        	
                   	        	removeRequired();
	                   	        
	                   	     alertMessage("postResultsBox", "HA_INFO_INACTIVEASSET");
	                   	     inactiveAsset = true;
                            }
                            loadingScreen(false, "");
                        });
             	   	}
             	   	else{
             		   populateWOInfo(data);
             		   loadingScreen(false, "");
             	   	}
               },
               error : function(xhr, ajaxOptions, thrownError) {
                    if (xhr.statusText =='abort' || thrownError == "") {
                         return;
                    }
                    
               }
         });
    }

     //Makes sure enter does not submit the form in description
    $(window).keydown(function(event){
          if(event.keyCode === 13 && !$('#description').is(":focus")) {
               event.preventDefault();
               return false;
          }
     });
     
     //If hidden maintenance body is changed, update the maintenance number
     $("#hiddenMBody").on("change", function(){    	 
    	 if(mBody != ""){
    		setMaintenanceInfo(false);
    	 }
    	 
		 if ($('#postResultsBox').html().indexOf("Inactive Maintenance Body")!==-1){
		 	$('#postResultsBox').hide();
		 }
         
		 if($("#hiddenMBody").val() !== ""){
               $('#maintenanceVendorNum').html($(this).val());
               $('#maintenanceVendorNum').show();
               
               $.post("./getMaintenanceInfo", { assetNumber: $('#assetNumber').val(), maintenanceNumber: $('#hiddenMBody').val()}, function(data){
                   if(data.length !== 0){
                	  setMaintenanceInfo(true, data);
                	   
            			 if(data[0].maintenanceBodyType == null){        	  
            	        	  checkContactMethod();
            	          }
            	          else{
            	        	  $('#contactMethodBTN').prop('disabled', true);
            	          }	
                   }
                   else{
	      	        	 checkContactMethod();
                   }
                   mBody = $('#maintenanceVendor').val();
                   $('#maintenanceVendor').trigger("blur");
               });
          }
          else{
               $('#contactMethodBTN').prop('disabled', true);
               $('#maintenanceVendorNum').html("");
               mBody = $('#maintenanceVendor').val();
               $('#maintenanceVendor').trigger("blur");
          }
     });
    
    $('#maintenanceVendor').on({
        focus: function() {
        	mBody = $('#maintenanceVendor').val();
        },
        blur: function() {
	    	if(mBody !== $('#maintenanceVendor').val()){
	    		if($("#maintenanceVendor.lovFieldSearchBtn").data("mouseDown") != true){
	            	$('#hiddenMBody').val("");
	            	$('#hiddenMBody').trigger("change");
	            	if($('#maintenanceVendor').val() !== ""){
	            		$.post("./LovLookup", { value: $(this).val(), query: 9}, function(data){
	            			if(data == "No Match"){
	            				$( "#maintenanceVendor.lovFieldSearchBtn" ).trigger( "click" );
	            				alertMessage("multipleAlert", "HA_ERROR_SELECTPREFERREDCRITERIA");
	                		}
	                		else {
	            				$('#hiddenMBody').val(data);
	            				$('#hiddenMBody').trigger("change");
	                		}
	                	});
	                }
	      	  	}
	    	}
        }
    });
    	
     //Upload attachment validations
     $("#attachmentUpload").change(function (){
          if($('#attachmentTitle').val() !== ""){
               $('#uploadBTN').addClass("enabled");
               $('#uploadBTN').prop("disabled", false);
          }
     });
    
     $('#attachmentTitle').on({
          blur: function() {
               if($('#attachmentUpload').val() !== "" && $('#attachmentTitle').val() !== ""){
                    $('#uploadBTN').addClass("enabled");
                    $('#uploadBTN').prop("disabled", false);
               }
          }
     });
    
    //Dropdown change events
    $('#woType').on('changed.bs.select', function () {
          var selected = $(this).find("option:selected").val();
          if(selected === "20"){
               $('#reinstatementWrapper').hide();
               $('#breakdownDateInput').prop('required', false);
               if(!$('#scheduleDateInput').is('[readonly]') ){	   
            	   $('#scheduleDateInput').prop('required', true);
               }
               $('#breakdownDateInput').val("");
               $( "#breakdownDate" ).hide();
               $( "#scheduleDate" ).show();
               
               if($('#autoSendEquip').find("option:selected").val().indexOf("XML") !== -1){
            	   $('#mainContactEmail').prop('required', false);
                   $("label[for='mainContactEmail']").removeClass("required");
                   $('#mainContactEmail').trigger("blur");
               }
          }
          else {
               $('#reinstatementWrapper').show();       
               $('#scheduleDateInput').prop('required', false);
               if(!$('#breakdownDateInput').is('[readonly]') ){	   
            	   $('#breakdownDateInput').prop('required', true);
               }
               $('#scheduleDateInput').val('');
               $('#breakdownDateInput').val('');
               $( "#breakdownDate" ).show();
               $( "#scheduleDate" ).hide();
               
               if($('#autoSendEquip').find("option:selected").val().indexOf("XML") !== -1){
            	   if(!$('#mainContactEmail').is('[readonly]') ){
            		   $('#mainContactEmail').prop('required', true);
            		   $("label[for='mainContactEmail']").addClass("required");
            	   }
                   $('#mainContactEmail').trigger("blur");
               }
          }
          $('#woForm').validator('update');
     }); 
    
     $('#autoSendEquip').on('changed.bs.select', function () {
          var selected = $(this).find("option:selected").val();
          
          if($("#postResultsBox").html().indexOf("Auto-Send") !== -1){
        	  $("#postResultsBox").hide();
          };
         
          if(selected.indexOf("Email") !== -1){
        	   if(!$('#mainContactEmail').is('[readonly]') ){
        		   	$('#mainContactEmail').prop('required', true);
               		$("label[for='mainContactEmail']").addClass("required");
        	   }
               $('#mainContactFax').prop('required', false);
               $("label[for='mainContactFax']").removeClass("required");
               $('#mainContactFax').trigger("blur");
               $('#mainContactEmail').trigger("blur");
          }
          else if(selected.indexOf("Fax") !== -1){
        	   if(!$('#mainContactFax').is('[readonly]') ){
        		  	$('#mainContactFax').prop('required', true);
               		$("label[for='mainContactFax']").addClass("required");
        	   }
               $('#mainContactEmail').prop('required', false);
               $("label[for='mainContactEmail']").removeClass("required");
               $('#mainContactEmail').trigger("blur");
               $('#mainContactFax').trigger("blur");
          }
          else if(selected.indexOf("XML") !== -1){
        	  if($('#woType').find("option:selected").val() == "10"){
        		  if(!$('#mainContactEmail').is('[readonly]') ){
           	   	  	$('#mainContactEmail').prop('required', true);
           	   	  	$("label[for='mainContactEmail']").addClass("required");
        		  }
                  $('#mainContactEmail').trigger("blur");
              }
        	  else{
        		  $('#mainContactEmail').prop('required', false);
                  $("label[for='mainContactEmail']").removeClass("required");
                  $('#mainContactEmail').trigger("blur");
        	  }
              $('#mainContactFax').prop('required', false);
              $("label[for='mainContactFax']").removeClass("required");
              $('#mainContactFax').trigger("blur");
          }
          else{
        	  $('#mainContactEmail').prop('required', false);
              $("label[for='mainContactEmail']").removeClass("required");
              $('#mainContactEmail').trigger("blur");
	          $('#mainContactFax').prop('required', false);
	          $("label[for='mainContactFax']").removeClass("required");
	          $('#mainContactFax').trigger("blur");
          }

          $('#woForm').validator('update');
     });

     $('#failureCauseCode').on('changed.bs.select', function () {
          var failure = $(this).find("option:selected").val();
          var symptom = $('#failureSymptomCode').val();
          if(failure !== "" && symptom !== ""){
               $.ajax({
                    type: 'POST',
                    data : { cause: failure, symptom: symptom },   
                    url : './ResolutionCode',                        
                    dataType:'json',
                    success : function(results) {
                         var value = "";
                         var html = "<option value=''>-- Select --</option>";
                         for (var i in results) {
                        	 if (results.hasOwnProperty(i)){
                              value = results[i]['desc'];
                              html = html + "<option value='" + value + "'>" + results[i]['name'] + "</option>";
                         	}
                         }
                    
                         $("#resolutionCode").html(html);
                         $("#resolutionCode").prop('disabled', false);
                         $('#resolutionCode').selectpicker('refresh');
                    },
                    error : function(xhr, ajaxOptions, thrownError) {                   
                         if (xhr.statusText =='abort' || thrownError == "") {
                             return;
                         }
                         
                    }
               });
          }
          else{
        	   $("#resolutionCode").val("");
               $("#resolutionCode").prop('disabled', true);
               $('#resolutionCode').selectpicker('refresh');
          }
     }); 
    
     $('#failureSymptomCode').on('changed.bs.select', function () {
          var symptom = $(this).find("option:selected").val();
          var failure = $('#failureCauseCode').val();
          if(failure !== "" && symptom !== ""){
               $.ajax({
                    type: 'POST',
                    data : { cause: failure, symptom: symptom },   
                    url : './ResolutionCode',                        
                    dataType:'json',
                    success : function(results) {                   
                         var html = "<option value=''>-- Select --</option>";
                         for (var i in results) {
                        	 if (results.hasOwnProperty(i)){
                        		 html = html + "<option value='" + results[i]['desc'] + "'>" + results[i]['name'] + "</option>";
                        	 }
                         }
                         $("#resolutionCode").html(html);
                         $("#resolutionCode").prop('disabled', false);
                         $('#resolutionCode').selectpicker('refresh');
                    },
                    error : function(xhr, ajaxOptions, thrownError) {                   
                         if (xhr.statusText =='abort' || thrownError == "") {
                             return;
                         }
                         
                    }
               });
          }
          else{
        	   $("#resolutionCode").val("");
               $("#resolutionCode").prop('disabled', true);
               $('#resolutionCode').selectpicker('refresh');
          }
     }); 

     //Button functions
     $('#backBTN').click(function(){
    	 if(getCookie('lastSearch') == "WR"){
    		 setCookie('returnWR', true, 60000);
             window.location.href = "searchWorkRequest";
    	 }
    	 else{
    		 setCookie('returnWO', true, 60000);
             window.location.href = "searchWorkOrder";
    	 }
     });
    
     $('#goToWRBTN').click(function(){
          $('#viewWrNumber').val($(this).attr("name"));
          $('#viewWrForm').submit();
     });
    
     $("#editBTN").click(function(){
    	 changeMode("e", WONumber, "");
     });
        
	 $("#contactMethodBTN").click(function(){
    	$("#contactMethodModal").modal();
	 });
     
     $('#contactMethodTable').on('click-row.bs.table', function (e, row, field, $element) {
    	 $('#autoSendEquip').val(row["auto_send"]);
         $('#autoSendEquip').selectpicker('refresh');
    	 $('#mainContactPerson').val(row["contact_person"]);
    	 $('#mainContactPhone').val(row["contact_phone"]);
    	 $('#mainContactFax').val(row["contact_fax"]);
    	 $('#mainContactEmail').val(row["contact_email"]);
    	 $('#autoSendEquip').trigger( "change" );
    	 $('#mainContactPerson').trigger( "blur" );
    	 $('#mainContactPhone').trigger( "blur" );
    	 $('#mainContactFax').trigger( "blur" );
    	 $('#mainContactEmail').trigger( "blur" );
    	 $('#contactMethodModal').modal('toggle'); 
     });
     
     $("#printBTN").click(function(){
          $('#postResultsBox').hide();
          $('#printWoNumber').val(WONumber);
          $('#printReportType').val("workorder");
          $('#printForm').submit();
     });
         
	 $("#resendBTN").click(function(){
		 if($('#woStatus').find("option:selected").text() !== "Released"){
			 alertMessage("postResultsBox", "HA_ERROR_RESENDSTATUS");
			 return;
		 }
		 
		 if($('#autoSendEquip').val().indexOf("Fax") == -1 && $('#autoSendEquip').val().indexOf("Email") == -1){
			 alertMessage("postResultsBox", "HA_ERROR_RESENDCONTACTMETHOD");
			 return;
		 }
		 
		 if(startingAutoSend !== $('#autoSendEquip').val()){
			 $.post("./chkResendRecord", { WONumber: WONumber}, function(resendRecord){
                 if(resendRecord == "Y"){
        			 alertMessage("postResultsBox", "HA_ERROR_PENDINGREQUEST");
                 }
                 else{
            		 resending = true;
            		 $('#woForm').submit(); 
                 }
             });
		 }
		 else{
			 resending = true;
    		 $('#woForm').submit(); 
		 }
    });
	 
	 
     $("#woCancel").click(function(){ 
    	 var cancelConfirmation = true;
          var modalConfirm = function(callback){
          $('#confirmationMsg').html("All unsaved changes will be cleared. Are you sure you would like to cancel?");
               $("#confirmationModal").modal('show');
          
               $("#confirmYesBTN").on("click", function(){
                    callback(true);
                    $("#confirmationModal").modal('hide');
               });
                 
               $("#confirmNoBTN").on("click", function(){
                    callback(false);
                    $("#confirmationModal").modal('hide');
               });
          };
          
          modalConfirm(function(confirm){
               if(confirm && cancelConfirmation){
                    if($('#newDocumentIds').val !== ""){
                         $.ajax({
                              type: 'POST',
                              data :  {
                                   idType:"doc_id",
                                   id: $('#newDocumentIds').val()
                              },
                              url : './DeleteAttachment',                        
                              success : function(data) {
                                   if(getCookie("fromWR") !== null){
                                        $('#viewWrNumber').val(getCookie("fromWR"));
                                        delCookie("fromWR");
                                        $('#viewWrForm').submit();
                                   } 
                                   else if(WOMode == 'e'){
                                	   	changeMode("v", WONumber, "");
                                   }
                                   else {
                                	   $.xhrPool.abortAll();
                                	   window.location.href = "initCreateWorkOrder";
                                   }     
                                   cancelConfirmation = false;
                              },
                              error : function(xhr, ajaxOptions, thrownError) {
                                   if (xhr.statusText =='abort' || thrownError == "") {
                                	   cancelConfirmation = false;
                                        return;
                                   }
                                   
                              }
                         });
                    }
               } else {
            	  	cancelConfirmation = false;
                    return false;
               }
          });
     });
    
     $('#woForm').submit(function(e){
    	 if (!e.isDefaultPrevented()) {
               e.preventDefault();
               $('#postResultsBox').hide();
               
               if(checkAttachmentOnly){
            	   if($('#attachmentFields').is(':visible')){
                       $('#frameWrapper').show();
                       $('#attachmentAlert').removeClass("alert-success");
                       $('#attachmentAlert').addClass("alert-danger");
	               	   $('#attachmentAlert').html("<strong><span class='glyphicon glyphicon-exclamation-sign'></span> Error!</strong> Please complete or cancel upload attachment.");
	               	   $('#attachmentAlert').show();
                       return;
                  }
    			  createEAMWO(WOMode, WONumber);
               }
               else{
                   if(checkFutureDate() !== true){
                	   alertMessage("postResultsBox", checkFutureDate(), "error");
                       return;
                  }
                   
                   if(checkDateOrder() !== true){
                	   	alertMessage("postResultsBox", checkDateOrder(), "error");
                        return;
                   }

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
                        $('html,body').animate({ scrollTop: 0 }, 'slow');
                        return;
                   }
                   
                   	if(inactiveAsset){
                   		if($("#woStatus option:selected").text() !== "Cancelled" && $("#woStatus option:selected").text() !== "Rejected" && $("#woStatus option:selected").text() !== "Complete" && $("#woStatus option:selected").text() !== "Complete - Pending Close"){
                   			alertMessage("postResultsBox", "HA_ERROR_WO_INACTIVEASSETSTATUS");
                   			$("#woStatus").closest(".form-group").addClass("has-error has-danger");
                   			return;
                   		}
                   }
                    
                   	//checking for failure code triple input
                   	if(($('#failureCauseCode').val() !== "" || $('#failureSymptomCode').val() !== "" || $('#resolutionCode').val() !== "")  && 
                   		($('#failureCauseCode').val() == "" || $('#failureSymptomCode').val() == "" || $('#resolutionCode').val() == "")){
               			alertMessage("postResultsBox", "HA_ERROR_FAILURETRIPLET");
               			if($('#failureCauseCode').val() == ""){
               				$("#failureCauseCode").closest(".form-group").addClass("has-error has-danger");
               			}
               			if($('#failureSymptomCode').val() == ""){
               				$("#failureSymptomCode").closest(".form-group").addClass("has-error has-danger");
               			}
               			if($('#resolutionCode').val() == ""){
               				$("#resolutionCode").closest(".form-group").addClass("has-error has-danger");
               			}
               			return;
                   	}

                   if($('#autoSendEquip option[value="XML or Portal-NO"]').length && $('#autoSendEquip option[value="XML or Portal-YES"]')){
                	   if(checkXML == true && $('#autoSendEquip').find("option:selected").val().indexOf("XML") !== -1){
                		   alertMessage("postResultsBox", "HA_ERROR_SENTALREADY");
                		   return;
                	   }
                	   
                	   if(!checkXML && $('#autoSendEquip').is(":visible")){
                		   $.post("./gs1VendorCheck", { maintenanceNumber: $('#hiddenMBody').val()}, function(data){
              		          if(data){
          		        		 if($('#autoSendEquip').find("option:selected").val().indexOf("XML") !== -1){
          		        			 promptSchedule();
                                  }
                                  else{
                                	  alertMessage("postResultsBox", "HA_ERROR_GS1VENDOR");
                                      $("#autoSendEquip").closest(".form-group").addClass("has-error has-danger");
                                  }
              		          }
              		          else{
              		        	  if($('#autoSendEquip').find("option:selected").val().indexOf("XML") !== -1){
              		        		  	alertMessage("postResultsBox", "HA_ERROR_NONGS1VENDOR");
                                        $("#autoSendEquip").closest(".form-group").addClass("has-error has-danger");
              		        	  }
         	                      else{
         	                    	promptSchedule();
         	                      }
              		          }
                     	   });
                	   }
                	   else{
                		   promptSchedule();
                	   }
                   }
                   else{
                	   promptSchedule();
                   }
               }
          }
    	 else {
    		 $('#repairDetailsPanel').collapse('show');
    		 $('#completionDetailsPanel').collapse('show');
    		 $('#orderDetailsPanel').collapse('show');
    		 $('#maintenanceDetailsPanel').collapse('show');
    		 setTimeout((function() {
    			 $('html, body').animate({
    				    scrollTop: (($('.has-error').first().offset().top) - 75)
    				 },500);
             }), 300);
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

function loadWOMode(mode, woNumber){
    //mode: c = create work request, v = view, e = edit
    if(mode === "e"){
          checked = true;
          assetValidated = true;
          $(document).prop('title', 'EAM - Update Work Order');
          loadCommon("glyphicon-edit", "Update", "Update Work Order #" + woNumber);
          $("#createWOToolbar").hide();
          $("#woSave").val('Update Work Order');
          $('#assetNumber').prop('readonly', true);
          $('#assetNumberFiltered.lovFieldSearchBtn').hide();
          $("#addAttachmentBTN").show();
          $("#attachmentFields").hide();
          $("#attachedFiles").show();
	      $("#woStatus option:contains('Complete - Pending Close')").hide();
          $("#woStatus option:contains('Cancelled By PM')").remove();
 	      $("#woStatus option:contains('Closed')").remove();
 	      $('#woStatus').selectpicker('refresh');
          //set hidden fields
          $('#workOrderId').attr('value', WONumber);
          $('#modeInput').attr('value', "UPDATE");
          $('#attachmentMode').attr('value', "NEW");
		  $('#assetNumber').off("blur");
          $(':input[required]').off("blur");
          //$('#scheduleHint').hide();
          $('#assetOrgWrapper').hide();
          $('#woOrgWrapper').show();
          
    }
    else if(mode === "v"){
          $(document).prop('title', 'EAM - View Work Order');
          loadCommon("glyphicon-list-alt", "View", "View Work Order #" + woNumber);
          $('.required').removeClass("required");
          $("#woForm input").prop('readonly', true);
          $("#woForm textarea").prop('required', false);
          $("#woForm textarea").prop('readonly', true);
          $('#equipmentSent').hide();
          $("#woForm small").hide();
          $("#woForm .radio").prop('disabled', true);
          $("#saveBTNs").hide();
          $("#addAttachmentBTN").hide();
          $("#attachmentFields").hide();
          $("#attachedFiles").show();
          $('#assetNumberFiltered.lovFieldSearchBtn').hide();
          $('#maintenanceVendor.lovFieldSearchBtn').hide();
          $('.input-group-addon').hide();
          $('.selectReadOnly').show();
          $('.assetInfoWrapper').show();
          $('.form-group').addClass("border");
          $('#breakdownDate, #scheduleDate').removeClass("border");
          $('.removeBorder').removeClass("border");
          $('.hideDropdowns').hide();
          $('#requestedForFiltered').hide();
          $('#disinfectionStatusReadOnly').show();
          $('#mainBodyTypeReadOnly').show();
          $('#autoSendEquipReadOnly').show();
		  $('#assetNumber').off("blur");
          $(':input[required]').off("blur");
          $('#maintenanceVendorWrapper').hide();
          $('#maintenanceVendorReadOnly').show();		
          $('label[for="maintenanceVendor"]').removeClass("required");
          $('#maintenanceVendorNum').hide();
          $('#completionDetailsSection').show();
          $('#contactMethod').hide();
          $('#assetOrgWrapper').hide();
          $('#woOrgWrapper').show();
    }
    else { 
          loadCommon("glyphicon-edit", "Create", "Create Work Order");
          $("#createWOToolbar").hide();
          $("#woSave").html('Create Work Order');
          $("#addAttachmentBTN").show();
          $("#attachmentFields").hide();
          $("#attachedFiles").hide();
          $('#outboundWrapper').hide();
          $("#woStatus option:contains('Complete - Pending Close')").remove();
          $("#woStatus option:contains('Cancelled By PM')").remove();
          $("#woStatus option:contains('Closed')").remove();
          $('#woStatus').selectpicker('refresh');
          $('#woStatus').selectpicker('val', [$("#woStatus option:contains('Unreleased')").val()]);
          $('#disinfectionStatus').selectpicker('val', "NOT NECESSARY");
          if($('#wrNumber').val() !== ""){
               $('#wrNumberWrapper').show();
               $('#assetNumber').prop('readonly', true);
               $('#assetNumberFiltered.lovFieldSearchBtn').hide();
          } 
          //set hidden fields
          $('#workOrderId').attr('value', '');
          $('#modeInput').attr('value', "CREATE");
          $('#attachmentMode').attr('value', "NEW");
    }   
}

function getAssetInfo(assetNumber){
    $('#assetInfoBTN').button('loading');
    $.post("./getAssetInfo", { assetNumber: assetNumber}, function(data){
        if(document.getElementsByClassName("assetInfoWrapper")!== null){
            $('.assetInfoWrapper').show();
        }
        if(data.length !== 0){
          validateAsset(true);
          populateAssetInfo(data);    
        }
        else {
          validateAsset(false);
         }     
    });
}

function createEAMWO(WOMode, WONumber){
	$('#woSave').prop("disabled", true);
	delCookie("fromWR");
	
	var workOrder = getWorkOrder();
	var vendorEnabled = true;
    
	if (workOrder["mode"] == 'CREATE'){
        loadingScreen(true, "Creating Work Order");
	}
	else{
        loadingScreen(true, "Updating Work Order");
	}

	if ((WOMode==""&& workOrder["maintenanceBody"]!="")||(WOMode=="e" && workOrder["maintenanceBody"]!="" && workOrder["woStatusText"]=='Released' )){
		$.post("./chkMainBodyEnabled", {mainBodyNum: workOrder["maintenanceBody"]}, function(mBodyEnabled){
            if(mBodyEnabled == 'N'){
            	alertMessage("postResultsBox", "HA_ERROR_INACTIVEMAINTENANBODY");
            	loadingScreen(false, "");
            	$('#woSave').prop("disabled", false);
            }else{
            	createEAMWOCall(WOMode, WONumber);
            }
        });
	}else{
		createEAMWOCall(WOMode, WONumber);
	}	
}

function createEAMWOCall(WOMode, WONumber){
	var workOrder = getWorkOrder();
	
	if ((WOMode=="e" && workOrder["woType"]=="10") || workOrder["woStatusText"]=="Cancelled" || workOrder["woStatusText"]=="Rejected"){ //No need to check duplicate WO during update CM wO
		createEAMWOP(WOMode, WONumber);
	}else{
	    $.ajax({
			url:'./checkOSWorkOrder',
			type:'post',
			contentType: 'application/json',
			data : JSON.stringify(workOrder),
			success:function(data){
				if (data.substr(0, 3) == '[N]'){ //do not allow WO creation/update
					$('#notificationMsg').html(data.substr(3,data.length-3));
					$("#notificationModal").modal('show');
					$('#notifyOkay').on("click", function(){
						loadingScreen(false, ""); 
						$("#notificationModal").modal('hide');
						$('#woSave').prop("disabled", false);
						}
					)
				}else if (data != 'Y' && data.substr(0, 3) != '[N]'){ //Warning message\
					var createConfirmation = true;
					var modalCreateCfm = function(createCallback){
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
					modalCreateCfm(function(createConfirm){
						if (createConfirm && createConfirmation){
							createEAMWOP(WOMode, WONumber);
							createConfirmation = false;
						} else{
							loadingScreen(false, "");  
							$('#woSave').prop("disabled", false);
							createConfirmation = false;
						}
					});
				}else{
					createEAMWOP(WOMode, WONumber);
				}
			}
	    });
	}
}

function createEAMWOP(WOMode, WONumber){
    var workOrder;
    
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
           		
           		workOrder = getWorkOrder();

       		    $.ajax({
				          url:'./createWorkOrder',
				          type:'post',
				          contentType: 'application/json',
				          data : JSON.stringify(workOrder),   
				          success:function(data){
				               if(data.substring(0,data.indexOf("&")).match(/^[0-9]+$/)){
				                    WONumber = data.slice(data.indexOf("=")+1);
				                    if(resending){
					               		 $.ajax({
					                            url: "./resendWorkOrder",
					                            type: "post",
					                            contentType: 'application/json',
					                            data : JSON.stringify(workOrder),
					                            success:function(data){
					                           		 changeMode("v", WONumber, data);
					                            },
					                            error: function(xhr, ajaxOptions, thrownError) {                   
					                                if (xhr.statusText =='abort' || thrownError == "") {
					                                    return;
					                                }
					                           }
					               		 });
				                    }
				                    else{
				                    	if(WOMode == "e"){
						                    changeMode("v", WONumber, "<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> Work order #" + WONumber + " has been updated.");
					                    }
					                    else{       
					                    	changeMode("v", WONumber, "<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> Work order #" + WONumber + " has been created.");
					                    }
				                    }
				               }
				               else if(data.indexOf("attachment only") !== -1){
			                    	changeMode("v", WONumber, "<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> Attachment(s) for Work order #" + WONumber + " has been updated.");

				               }
				               else if(data.indexOf("No attachment is updated") !== -1){
			                    	changeMode("v", WONumber, "noAttachment");
				               }
				               else {
				                    alertMessage("postResultsBox", data, "error");
				                    loadingScreen(false, "");
				                    $('#woSave').prop("disabled", false);
				               }
				          }
				     });
           	}
           	//if returned string is "ERROR..."
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
		workOrder = getWorkOrder();

	    $.ajax({
         url:'./createWorkOrder',
         type:'post',
         contentType: 'application/json',
         data : JSON.stringify(workOrder),   
         success:function(data){
              if(data.substring(0,data.indexOf("&")).match(/^[0-9]+$/)){
                   WONumber = data.slice(data.indexOf("=")+1);
                   if(resending){
                	   $.ajax({
                           url: "./resendWorkOrder",
                           type: "post",
                           contentType: 'application/json',
                           data : JSON.stringify(workOrder),
                           success:function(data){
                        	   changeMode("v", WONumber, data);
                           },
                           error: function(xhr, ajaxOptions, thrownError) {                   
                               if (xhr.statusText =='abort' || thrownError == "") {
                                   return;
                               }
                          }
              		 });
                   }
                   else{
                	   if(WOMode == "e"){
    		               changeMode("v", WONumber, "<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> Work order #" + WONumber + " has been updated.");
                       }
                       else{                 
                    	   changeMode("v", WONumber, "<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> Work order #" + WONumber + " has been created.");
                       }
                   }
              }
              else if(data.indexOf("attachment only") !== -1){
              	changeMode("v", WONumber, "<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> Attachment(s) for Work order #" + WONumber + " has been updated.");

             }
              else if(data.indexOf("No attachment is updated") !== -1){
            	  changeMode("v", WONumber, "noAttachment");
              }
              else {
                   alertMessage("postResultsBox", data, "error");
                   loadingScreen(false, "");
                   $('#woSave').prop("disabled", false);
              }
         }
    	});
	}		
}

function populateWOInfo(data){
	 populateAssetInfo(data);   						  
     $('#workOrderId').val(data[0].workOrderId);
     $('#woNumber').val(data[0].workOrderNumber);
     $('#woStatusReadOnly').val(data[0].woStatus);
     $('#woStatusReadOnly').selectpicker('refresh');
     $('#woStatus').val(data[0].woStatusId);    
     $('#woStatus').selectpicker('refresh');
     startingStatus = data[0].woStatus;
     $('#assetNumber').val(data[0].assetNumber);
     $('#woTypeReadOnly').val(data[0].woType);
     $('#woType').val(data[0].woTypeId);
     $('#woType').selectpicker('refresh');
     nonItView(data[0].woTypeId, data[0].woStatus);
     $('#mainBodyTypeReadOnly').val(data[0].maintenanceBodyType);     
     $('#mainBodyType').val(data[0].maintenanceBodyType);
     $('#mainBodyType').selectpicker('refresh');
     $('#autoSendEquipReadOnly').val(data[0].autoSendWO);
     $('#autoSendEquip').val(data[0].autoSendWO);
     $('#autoSendEquip').selectpicker('refresh');
     startingAutoSend = data[0].autoSendWO;
     $('#addLaborCost').val(data[0].addLaborCost); 
     $('#addMaterialCost').val(data[0].addMaterialCost); 
     $('#addMaterialDesc').val(data[0].addMaterialDesc); 
     $('#attendance').val(data[0].attendanceDate); 
     $('#callReceived').val(data[0].callRecieved); 
     $('#disinfectionStatusReadOnly').val(data[0].disinfectionStatus);
     if(data[0].disinfectionStatus == null || data[0].disinfectionStatus == ""){
    	 $('#disinfectionStatus').selectpicker('val', "NOT NECESSARY");
     }
     else {
    	 $('#disinfectionStatus').val(data[0].disinfectionStatus);
     }
     $('#disinfectionStatus').selectpicker('refresh');
     $('#equipmentConditionReadOnly').val(data[0].equipmentCondition);
     $('#equipmentCondition').val(data[0].equipmentCondition);
     $('#equipmentCondition').selectpicker('refresh'); 
     $('#equipmentReceived').val(data[0].equipmentRecievedDate); 
     $('#equipmentSentReadOnly').val(data[0].equipmentSent); 
     $("input[name=equipmentSent][value=" + data[0].equipmentSent + "]").attr('checked', 'checked');
     $('input[name="equipmentSent"]').val([data[0].equipmentSent]);
     $('#failureCauseCodeReadOnly').val(data[0].failureCauseCode); 
     $('#failureCauseCode').val(data[0].failureCauseCode);
     $('#failureCauseCode').selectpicker('refresh');
     $('#failureSymptomCodeReadOnly').val(data[0].failureSymptomCode); 
     $('#failureSymptomCode').val(data[0].failureSymptomCode);
     $('#failureSymptomCode').selectpicker('refresh');
     $('#contactEmail').val(data[0].haContactEmail); 
     $('#contactEmailReadOnly').html(data[0].haContactEmail); 
     $('#contactPerson').val(data[0].haContactPerson); 
     $('#contactPersonReadOnly').html(data[0].haContactPerson); 
     $('#contactPhone').val(data[0].haContactPhone); 
     $('#laborCost').val(data[0].laborCost); 
     $('#reinstatement').val(data[0].reinstatementCompletionDate); 
     $('#completion').val(data[0].completionDate); 
     $('#resolutionCodeReadOnly').val(data[0].repairResoultionCode); 
     $('#resolutionCode').val(data[0].repairResoultionCode);
     $('#resolutionCode').selectpicker('refresh');
     $('#resultAction').val(data[0].resultAndAction); 
     $('#reportReference').val(data[0].serviceReport); 
     $('#sparePartCost').val(data[0].sparePartCost); 
     $('#sparePartDesc').val(data[0].sparePartDesc); 
     //$('#supplerAgreementNo').val(data[0].supplerAgreementNo);
     $('#mainPlan').val(data[0].mPlan);
     $('#mainJoinDate').val(data[0].mJoinDate);
     $('#mainExpiryDate').val(data[0].mExpiryDate);
     $('#technicalName').val(data[0].technicalName); 
     $('#userRemarks').val(data[0].userRemark);
     $('#vRefNum').val(data[0].vendorReferenceNo); 
     $('#vendorRemarks').val(data[0].vendorRemark); 
     $('#description').val(data[0].woDescription); 
     $('#woCompletedBy').val(data[0].workOrderCompletedBy);
     $('#mainContactPhone').val(data[0].mContactPhone);  
     $('#mainContactFax').val(data[0].mContactFax);
     $('#mainContactPerson').val(data[0].mContactPerson);
     $('#mainContactEmail').val(data[0].mContactEmail);
     $('#contractNo').val(data[0].maintenanceContract);
     $('#mainInterval').val(data[0].mInterval);
	 $('#riskLevel').html(data[0].assetRisk);
	 $('#serialnumber').html(data[0].assetSerialNumber);
	 $('#aLocation').html(data[0].assetLocation);
	 $('#aLocation').prop('title', data[0].assetLocationDesc);
	 $('#owingDept').html(data[0].owningDepartment);  
	 $('#owingDept').prop('title', data[0].assetOwningDepartmentDesc);
	 $('#owingDept').prop('idvalue', data[0].owningDepartmentId);
	 $('#owingDept').html(data[0].owningDepartment);  
	 $('#assetOrg').val(data[0].assetOrg);
	 $('#assetOrgModal').html(data[0].assetOrg);
	 $('#woOrg').val(data[0].workOrderOrg);
	 $('#outbound').val(data[0].outboundDate);
	 $('#lastUpdateDate').val(data[0].lastUpdateDate);
	 
	 if($('#outbound').val() !== ""){
        $("#woStatus option:contains('Unreleased')").hide();
        $('#woStatus').selectpicker('refresh');
	}
          
	 if(data[0].woStatus == "Cancelled" || data[0].woStatus == "Cancelled By PM" || data[0].woStatus == "Rejected" || data[0].woStatus == "Complete - Pending Close" || data[0].woStatus == "Closed"){
    	 $('.required').removeClass("required");
         $("#woForm input").prop('readonly', true);
         $("#woForm textarea").prop('required', false);
         $("#woForm textarea").prop('readonly', true);
         $('#equipmentSent').hide();
         $("#woForm small").hide();
         $("#woForm .radio").prop('disabled', true);
         $('#assetNumberFiltered.lovFieldSearchBtn').hide();
         $('#maintenanceVendor.lovFieldSearchBtn').hide();
         $('.input-group-addon').hide();
         $('.selectReadOnly').show();
         $('.assetInfoWrapper').show();
         $('.form-group').addClass("border");
         $('#breakdownDate, #scheduleDate').removeClass("border");
         $('.removeBorder').removeClass("border");
         $('.hideDropdowns').hide();
         $('#requestedForFiltered').hide();
         $('#disinfectionStatusReadOnly').show();
         $('#mainBodyTypeReadOnly').show();
         $('#autoSendEquipReadOnly').show();
         $('#maintenanceVendorWrapper').hide();
         $('#maintenanceVendorReadOnly').show();		
         $('#maintenanceVendorNum').hide();
         $('.attachmentLabels').addClass("required");
         $('.attachmentInput').prop('readOnly', false);
         $('.attachmentBorders').removeClass("border");
         $('#contactMethod').hide();
         $('#attachmentPanel').collapse('show');
         $('#editBTN').html("<span class='glyphicon glyphicon-edit' aria-hidden='true'></span> Update Attachment");
         $('#woSave').val("Update Attachment");
         $('#repairDetailsSection').show();
    	 $('#repairDetailsPanel').collapse('show');
    	 $('#completionDetailsSection').show();
    	 $('#completionDetailsPanel').collapse('show');
         checkAttachmentOnly = true;
     }
	           
     if(data[0].wrNumber !== null){
        $('#goToWRBTN').show();
        $('#goToWRBTN').attr('name', data[0].wrNumber);
        $('#wrNumber').val(data[0].wrNumber);
        $('#wrNumberWrapper').show();
     }
     
     if(data[0].woTypeId === "10"){
          $('#scheduleDateInput').prop('required', false);
          $('#breakdownDateInput').prop('required', true);
          if(data[0].hasOwnProperty("breakdownScheduleDate")){
               $('#breakdownDateInput').val(data[0].breakdownScheduleDate);
          }
          $('#breakdownDate').show();
          $('#scheduleDate').hide();
          
          if($('#scheduleCompletionWrapper').length && WOMode == "v"){
   	    	 $('#scheduleCompletionWrapper').show();
   	    	 $('#scheduleCompletion').val(data[0].scheduleEndDate);
   	    	  $('#scheduleStart').val(data[0].scheduleStartDate);
   	      }
     }
     else{
          $('#breakdownDateInput').prop('required', false);
          $('#scheduleDateInput').prop('required', true);
          if(data[0].hasOwnProperty("breakdownScheduleDate")){
               $('#scheduleDateInput').val(data[0].breakdownScheduleDate);
               startingSchedule = data[0].breakdownScheduleDate;
          }
          $('#scheduleDate').show();
          $('#breakdownDate').hide();
     }

     if(data[0].userRemark == ""){
          checkEmptyRemarks(data[0].operationDesc);
     }
     
     var vfailure = data[0].failureCauseCode;
     var vSym = data[0].failureSymptomCode;
     
     if(vfailure && vSym){
    	 $.ajax({
             type: 'POST',
             data : { cause: vfailure, symptom: vSym },   
             url : './ResolutionCode',                        
             dataType:'json',
             success : function(results) {                  
                  var html = "<option value=''>-- Select --</option>";
                  var value = "";
                  for (var i in results) {
                 	 if (results.hasOwnProperty(i)){
	                       value = results[i]['desc'];
	                       if (results[i]['desc']==data[0].repairResoultionCode){
	                            html = html + "<option value='" + value + "' selected>" + results[i]['name'] + "</option>";
	                       }
	                       else {
	                            html = html + "<option value='" + value + "'>" + results[i]['name'] + "</option>";
	                       }
                 	 }
                  }
             
                  $("#resolutionCode").html(html);
                  $("#resolutionCode").prop('disabled', false);
                  $('#resolutionCode').selectpicker('refresh');
             },
             error : function(xhr, ajaxOptions, thrownError) {                  
                  if (xhr.statusText =='abort' || thrownError == "") {
                       return;
                  }
                  
             }
        });
     }
      
	var attachmentDesc;
	 for (var i = 0; i < data[0].attachmentInfo.length; i++) {		
		attachmentDesc = "";
		if(data[0].attachmentInfo[i].description !== null){
			attachmentDesc = data[0].attachmentInfo[i].description;
		}
		recordAttachment(data[0].attachmentInfo[i].title, attachmentDesc, data[0].attachmentInfo[i].fileName, data[0].attachmentInfo[i].documentId);
	 }
	 
	 nonItView(data[0].woTypeId, data[0].woStatus);
	 
	 if(WOMode == 'e'){
         checkStatuses();		
         
         if (data[0].woStatus == "Released" && $('#outbound').val() !== ""){
        	 if(data[0].autoSendWO.indexOf("Fax") !== -1 || data[0].autoSendWO.indexOf("Email") !== -1){
            	 $('#resendBTN').show();
             }
         }
         
         if(data[0].autoSendWO !== null){
	         if(data[0].autoSendWO.indexOf("Email") !== -1){
		      	 if(!$('#mainContactEmail').is('[readonly]') ){
		      		 $('#mainContactEmail').prop('required', true);
		             $("label[for='mainContactEmail']").addClass("required");
		      	 }
	        }
	        else if(data[0].autoSendWO.indexOf("Fax") !== -1){
	      	   if(!$('#mainContactFax').is('[readonly]') ){
	      		  	$('#mainContactFax').prop('required', true);
	             	$("label[for='mainContactFax']").addClass("required");
	      	   }
	        }
	         
	         if(data[0].autoSendWO.indexOf("XML") !== -1){
	        	 if(data[0].woTypeId == "10"){
	          	   	  $('#mainContactEmail').prop('required', true);
	                 $("label[for='mainContactEmail']").addClass("required");
	             }
		       	  else{
		       		  $('#mainContactEmail').prop('required', false);
		                 $("label[for='mainContactEmail']").removeClass("required");
		       	  }
	        	 
	        	 if (data[0].woStatus !== "Unreleased"){
	        		 $('#maintenanceVendorWrapper').hide();
		             $('#maintenanceVendorReadOnly').show();
	                 $('label[for="maintenanceVendor"]').removeClass("required")
		             $('#maintenanceVendorNum').hide();
		             $('#mainBodyTypeReadOnly').show();     
		             $('#autoSendEquipReadOnly').show();
		             $('.maintenanceHide').hide();
		             $('.maintenance').prop('readonly', true);
		             $('#woTypeReadOnly').show();
		             $('#woTypeReadOnly').prop('readonly', true);
		             $('#woTypeWrapper').hide();
		        	 $('#disinfectionStatusReadOnly').show();
		        	 $('.equipmentSentReadOnly').show();
		        	 $('.orderHide').hide();
		        	 $('.order').prop('readonly', true);
		        	 $('#orderDetailsPanel .form-group').addClass("border");
		        	 $('#maintenanceDetailsPanel .form-group').addClass("border");
		             $('.removeBorder').removeClass("border");
		             $('#contactMethod').hide();
	        	 }
	         }
	         else{
	        	 if (data[0].woStatus == "Unreleased"){
	        		 $('#woTypeReadOnly').hide();
	                 $('#woTypeWrapper').show();
	                 $('#maintenanceVendorReadOnly').hide();
	                 $('#maintenanceVendorWrapper').show();
	                 $('#maintenanceVendorNum').show();
	        	 }
	        	 else{
	        		 $('#woTypeReadOnly').show();
	                 $('#woTypeReadOnly').prop('readonly', true);
	                 $('#woTypeWrapper').hide();
	                 $('#maintenanceVendorReadOnly').show();
	                 $('label[for="maintenanceVendor"]').removeClass("required");
	                 $('#maintenanceVendorReadOnly').prop('readonly', true);
	                 $('#maintenanceVendorWrapper').hide();
	                 $('#maintenanceVendorNum').hide();
	        	 }
	        	 
	        	 if($("#outbound").val() !== ""){
	        		 checkXML = true;
	        	 }
	         }
		 }
         
         
         
         if(data[0].woStatus == "Complete"){
    		 $('.required').removeClass("required");
             $("#woForm input").prop('readonly', true);
             $("#woForm textarea").prop('required', false);
             $("#woForm textarea").prop('readonly', true);
             $('#equipmentSent').hide();
             $("#woForm small").hide();
             $("#woForm .radio").prop('disabled', true);
             $('#assetNumberFiltered.lovFieldSearchBtn').hide();
             $('#maintenanceVendor.lovFieldSearchBtn').hide();
             $('.input-group-addon').hide();
             $('.selectReadOnly').show();
             $('.assetInfoWrapper').show();
             $('.form-group').addClass("border");
             $('#breakdownDate, #scheduleDate').removeClass("border");
             $('.removeBorder').removeClass("border");
             $('.hideDropdowns').hide();
             $('#requestedForFiltered').hide();
             $('#disinfectionStatusReadOnly').show();
             $('#mainBodyTypeReadOnly').show();
             $('#autoSendEquipReadOnly').show();
             $('#maintenanceVendorWrapper').hide();
             $('#maintenanceVendorReadOnly').show();		
             $('#maintenanceVendorNum').hide();
             $('.attachmentLabels').addClass("required");
             $('.attachmentInput').prop('readOnly', false);
             $('.attachmentBorders').removeClass("border");
             $('#contactMethod').hide();
             $('#attachmentPanel').collapse('show');
             $('#repairDetailsSection').show();
        	 $('#repairDetailsPanel').collapse('show');
        	 $('#completionDetailsSection').show();
        	 $('#completionDetailsPanel').collapse('show');
        	 
        	 $('#woStatusHide').show();
        	 $('#woStatusReadOnly').hide();
        	 $('.overviewBorders').removeClass("border");
    	 }
         
         removeRequired();
    }   
	 	 
	 if(WOMode == 'v'){
		 $('.deleteAttachment').hide();
		 $('.required').removeClass("required");
		 $('#attachmentPanel').collapse('hide');
	 }
	 
     $('#woForm').validator('update');
}

function copyWRInfo(data){
     //get latest asset information                                                  
     getAssetInfo(data[0].assetNumber);
     
     $('#woType').val(data[0].wrType);
     $('#woType').selectpicker('refresh');
     $('#goToWRBTN').show();
     $('#goToWRBTN').attr('name', data[0].wrNumber);
     $('#wrNumber').val(data[0].workRequestNumber);
     $('#description').val(data[0].description);
     $('#wrNumberWrapper').show();
     $('#assetNumber').val(data[0].assetNumber);
     $('#maintenanceVendor').val(data[0].maintenanceBody);
     $('#mainBodyTypeReadOnly').html(data[0].maintenanceBodyType);  

     if(data[0].wrType === "10"){
          $('#scheduleDateInput').prop('required', false);
          $('#breakdownDateInput').prop('required', true);
          if(data[0].hasOwnProperty("breakdownDateInput")){
               $('#breakdownDateInput').val(data[0].breakdownDateInput);
          }
          $('#breakdownDate').show();
          $('#scheduleDate').hide();
      }
      else{
          $('#breakdownDateInput').prop('required', false);
          $('#scheduleDateInput').prop('required', true);
          if(data[0].hasOwnProperty("scheduleDateInput")){
               $('#scheduleDateInput').val(data[0].scheduleDateInput);
               startingSchedule = data[0].scheduleDateInput;
          }
          $('#scheduleDate').show();
          $('#breakdownDate').hide();
     }
     $('#woForm').validator('update');
}

//validates asset number
function validateAsset(validated){
     if(validated){
          $('#assetInfoBTN').button('reset');
               setTimeout(function() {
               $('#assetInfoBTN').prop('enabled', true);
          }, 0);
          $('#assetNumberGroup').removeClass("has-error");
          $('#assetNumberGroup').removeClass("has-danger");
          $('#assetNumberHelp').hide();       
          if(document.getElementsByClassName("assetInfoWrapper") !== null){
               $('.assetInfoWrapper').show();
          }
          assetValidated = true;
//          if($("#woSave").hasClass("disabled") && checked){
//             $("#woForm").validator('validate');
//             checked = false;
//          }
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
          //$("#woSave").addClass("disabled");
          assetValidated = false;
     }
}

//enters the values for the form and submits form to change modes, pass parameters etc.
function changeMode(mode, woNumber, previous){
     $('#modeParameter').val(mode);
     $('#woNumberParameter').val(woNumber);
     $('#previousParameter').val(previous);
     $('#passParamtersForm').submit();  
}

//Clear work order form information when cancelled
//function clearWoInfo(){
//	$.xhrPool.abortAll();
//    $("#woForm input").val('');
//    $("#woForm textarea").val('');
//    $("#woForm select").val('');
//    $('.selectpicker').selectpicker('refresh');
//    $('input:radio[name="equipmentSent"]').prop('checked', false);
//    $('#assetInfoBTN').button('reset');
//    $('#assetInfoBTN').prop('disabled', true);
//    $('#postResultsBox').hide();
//    $('.assetInfoWrapper').hide();
//    $('.text-danger').hide();
//    $('.has-error').removeClass("has-error");
//    $('.has-danger').removeClass("has-danger");
//    $('.help-block').hide();
//	$('#woSave').val('Create Work Order');
//    $('#woStatus').selectpicker('val', [$("#woStatus option:contains('Unreleased')").val()]); 
//    $('#maintenanceVendorNum').hide();
//    clearAttachmenInfo();	
//    restoreDefaultForm();
//    $('html,body').animate({ scrollTop: 0 }, 'slow');
//}

//Populate asset information for asset number input
function populateAssetInfo(data){
     $('#assetInfoBTN').button('reset');
     $('#assetNumberGroup').removeClass("has-error");
     $('#assetNumberHelp').hide();
     $('#assetInfoTitle').html("Asset #" + data[0].assetNumber);
     $('#aDescription').html(data[0].assetDescription);
     $('#aLocation').html(data[0].assetlocation);
     $('#aOwner').html(data[0].assetOwner);
     $('#owingDept').html(data[0].owningDept);  
     $('#manufacturer').html(data[0].manufacturer); 
     $('#brand').html(data[0].brand); 
     $('#model').html(data[0].model); 
     $('#serialnumber').html(data[0].serialnumber); 
     $('#riskLevel').html(data[0].riskLevel);
     $('#assetDescription').html(data[0].assetDescription);
     $('#maintenanceDetailsPopUp').hide();

     var maintenanceBody = data[0].maintenanceBody;
     if (maintenanceBody !== null && data[0].maintenanceBodyNum !== ""){
          $('#hiddenMBody').val(data[0].maintenanceBodyNum);
          $('#maintenanceVendor').val(maintenanceBody.slice(0,maintenanceBody.length-data[0].maintenanceBodyNum.length-3));
		  $('#maintenanceVendorReadOnly').html(maintenanceBody);
          $('#maintenanceVendorNum').html(data[0].maintenanceBodyNum);
          $('#maintenanceVendorNum').show();
     }
     else{
          $('#maintenanceVendor').val("");
          $('#maintenanceVendorReadOnly').html("");
          $('#maintenanceVendorNum').hide();
     }

     $('#mainBodyType').val(data[0].maintenanceBodyType);  
     $('#mainBodyType').selectpicker('refresh');
     $('#mainPlan').val(data[0].maintenancePlan);  
     $('#mainContactPhone').val(data[0].maintPhone);          
     $('#mainJoinDate').val(data[0].maintenanceJoinDate);             
     $('#mainContactFax').val(data[0].maintFax);
     $('#mainExpiryDate').val(data[0].maintenanceExpiryDate); 
     $('#contractNo').val(data[0].contractNumber);         
     $('#mainContactPerson').val(data[0].maintContact);         
     $('#mainContactEmail').val(data[0].maintEmail); 
     $('#mainInterval').val(data[0].maintenanceInterval); 
     $('#aLocation').prop('title', data[0].assetLocationDesc);
     $('#aOwner').prop('title', data[0].assetOwnerDesc);
     $('#owingDept').prop('title', data[0].assetOwningDepartmentDesc);
     $('#aItemCode').html(data[0].itemCodeNumber);
     $('#aStatus').html(data[0].assetStatus);
     $('#dob').html(data[0].dob);
     $('#purchasePrice').html(data[0].purchasePrice); 
     $('#autoSendEquip').val(data[0].autoSend);
     $('#autoSendEquip').selectpicker('refresh');
     $('#supplierNo').val(data[0].supplierAgreementNumber); 
     $('#parentAsset').html(data[0].fatherAssetNumber);
     $('#assetOrg').val(data[0].assetOrganisation);
     $('#assetOrgModal').html(data[0].assetOrganisation);
     if(WOMode == 'v'){
    	 $('#maintenanceVendorNum').hide();
     }
     
     if(data[0].assetGroupName != null){
    	 $('#aGroup').html(data[0].assetGroupName);
       	$('#assetGroup').val(data[0].assetGroupName);
      	$('#assetGroup').prop('idvalue',data[0].assetGroupId);
     }
     else{
    	 $('#assetGroup').val(data[0].assetGroup);
    	 $('#aGroup').html(data[0].assetGroup); 
     }
     
     $('[data-toggle="tooltip"]').tooltip();
     $('#aSupplier').html(data[0].assetSupplier);                      
     $('#assetInfoBTN').prop("disabled", false);
     $('.assetInfoWrapper').show();
}

//VALIDATIONS
//Changes the available dropdown options according validation criteria
function checkStatuses(){
	if($("#woStatus option:selected").text() == "Complete"){
    	  $("#woStatus option:contains('Complete - Pending Close')").show();
          $("#woStatus option:contains('Unreleased')").hide();
          $("#woStatus option:contains('Cancelled')").hide();
          $("#woStatus option:contains('On Hold')").hide();
          $("#woStatus option:contains('Rejected')").hide();
          $('#woStatus').selectpicker('refresh');
     }
     $('#woForm').validator('update');
}    

//Mandatory fields for complete status functions
function completeCM(add){
     //add = true, add required. add = false, remove required
     if(add){
    	  $('#completionWrapper').hide();
    	  if(!$('#vRefNum').is('[readonly]') ){	      			
    		  $('#vRefNum').prop('required', true);
              $("label[for='vRefNum']").addClass("required");
    	  }
          if(!$('#callReceived').is('[readonly]') ){	
        	  $('#callReceived').prop('required', true);
              $("label[for='callReceived']").addClass("required");
    	  }
          if(!$('#attendance').is('[readonly]') ){	
        	  $('#attendance').prop('required', true);
              $("label[for='attendance']").addClass("required");
    	  }
          if(!$('#reinstatement').is('[readonly]') ){	    
        	  $('#reinstatement').prop('required', true);
              $("label[for='reinstatement']").addClass("required");
    	  }
          if(!$('#failureCauseCodeReadOnly').is(":visible") ){	  
        	  $('#failureCauseCode').prop('required', true);
              $("label[for='failureCauseCodeReadOnly']").addClass("required");
    	  }
          if(!$('#failureSymptomCodeReadOnly').is(":visible") ){	    
        	  $('#failureSymptomCode').prop('required', true);
              $("label[for='failureSymptomCodeReadOnly']").addClass("required");
    	  }
          if(!$('#resolutionCodeReadOnly').is(":visible") ){	      		
        	  $('#resolutionCode').prop('required', true);
              $("label[for='resolutionCodeReadOnly']").addClass("required");
    	  }
          if(!$('#reportReference').is('[readonly]') ){	
        	  $('#reportReference').prop('required', true);
              $("label[for='reportReference']").addClass("required");
    	  }
          if(!$('#woCompletedBy').is('[readonly]') ){	     
        	  $('#woCompletedBy').prop('required', true);
              $("label[for='woCompletedBy']").addClass("required");
    	  }
          if(!$('#equipmentConditionReadOnly').is(":visible")  ){	     
        	  $('#equipmentCondition').prop('required', true);
              $("label[for='equipmentConditionReadOnly']").addClass("required");
    	  }
     }
     else {
    	  $('#completionWrapper').show();
          $('#vRefNum').prop('required', false);
          $("label[for='vRefNum']").removeClass("required");
          $('#callReceived').prop('required', false);
          $("label[for='callReceived']").removeClass("required");
          $('#attendance').prop('required', false);
          $("label[for='attendance']").removeClass("required");
          $('#reinstatement').prop('required', false);
          $("label[for='reinstatement']").removeClass("required");
          $('#failureCauseCode').prop('required', false);
          $("label[for='failureCauseCodeReadOnly']").removeClass("required");
          $('#failureSymptomCode').prop('required', false);
          $("label[for='failureSymptomCodeReadOnly']").removeClass("required");
          $('#resolutionCode').prop('required', false);
          $("label[for='resolutionCodeReadOnly']").removeClass("required");
          $('#reportReference').prop('required', false);
          $("label[for='reportReference']").removeClass("required");
          $('#woCompletedBy').prop('required', false);
          $("label[for='woCompletedBy']").removeClass("required");
          $('#equipmentCondition').prop('required', false);
          $("label[for='equipmentConditionReadOnly']").removeClass("required");
     }
     $('#woForm').validator('update');
}

//mandatory validation for preventive completion status
function completePM(add){
     //add = true, add required. add = false, remove required
     if(add){
    	  $('#completionWrapper').show();
    	  if(!$('#attendance').is('[readonly]') ){	    
    		  $('#attendance').prop('required', true);
          	  $("label[for='attendance']").addClass("required");
    	  }
    	  if(!$('#completion').is('[readonly]') ){	    
    		  $('#completion').prop('required', true);
          	  $("label[for='completion']").addClass("required");
    	  }
    	  if(!$('#reportReference').is('[readonly]') ){
    		  $('#reportReference').prop('required', true);
          	  $("label[for='reportReference']").addClass("required");
    	  }
    	  if(!$('#woCompletedBy').is('[readonly]') ){
    		  $('#woCompletedBy').prop('required', true);
          	  $("label[for='woCompletedBy']").addClass("required");
    	  }
     }
     else {
    	  $('#completionWrapper').hide();
          $('#attendance').prop('required', false);
          $("label[for='attendance']").removeClass("required");
          $('#completion').prop('required', false);
          $("label[for='completion']").removeClass("required");
          $('#reportReference').prop('required', false);
          $("label[for='reportReference']").removeClass("required");
          $('#woCompletedBy').prop('required', false);
          $("label[for='woCompletedBy']").removeClass("required");
     }
     $('#woForm').validator('update');
}

function checkFutureDate(){
	 var dateArray = [];
     var fieldArray = [];
     var idArray = [];
     
     if($('#callReceived').val() !== ""){
          dateArray.push(moment($('#callReceived').val(), "DD/MM/YYYY HH:mm"));
          fieldArray.push("Call Received Date Time");
          idArray.push("callReceived");
     }
     
     if($('#equipmentReceived').val() !== ""){
         dateArray.push(moment($('#equipmentReceived').val(), "DD/MM/YYYY HH:mm"));
         fieldArray.push("Equipment Received Date Time");
         idArray.push("equipmentReceived");
    }
     
     if($('#attendance').val() !== ""){
         dateArray.push(moment($('#attendance').val(), "DD/MM/YYYY HH:mm"));
         fieldArray.push("Attendance Date Time");
         idArray.push("attendance");
    }
     
     if($('#reinstatement').val() !== ""){
         dateArray.push(moment($('#reinstatement').val(), "DD/MM/YYYY HH:mm"));
         fieldArray.push("Reinstatement Date Time");
         idArray.push("reinstatement");
    }
     
     if($('#completion').val() !== ""){
         dateArray.push(moment($('#completion').val(), "DD/MM/YYYY HH:mm"));
         fieldArray.push("Completion Date Time");
         idArray.push("completion");
    }
     
     for(i = 0; i < dateArray.length; i++) {
         if(dateArray[i] > moment()) {
         	$("#"+idArray[i]).closest(".form-group").addClass("has-error has-danger");
             return fieldArray[i] + " should not be future date.";
         }
     }
     return true;
}

//Date validation for Breakdown Date Time<= Call received Date Time <= Equipment Received Date Time <= Attendance Date Time <= Reinstatement Date Time/Completion Date Time
function checkDateOrder(){
     var dateArray = [];
     var fieldArray = [];
     var idArray = [];
     
     if($('#breakdownDateInput').val() !== "" && $('#breakdownDate').is(":visible")){
          dateArray.push(moment($('#breakdownDateInput').val(), "DD/MM/YYYY HH:mm"));
          fieldArray.push("Breakdown Date");
          idArray.push("breakdownDateInput");
     }
     
     if($('#callReceived').val() !== ""){
          dateArray.push(moment($('#callReceived').val(), "DD/MM/YYYY HH:mm"));
          fieldArray.push("Call received Date Time");
          idArray.push("callReceived");
     }
     
     if($('#equipmentReceived').val() !== ""){
          dateArray.push(moment($('#equipmentReceived').val(), "DD/MM/YYYY HH:mm"));
          fieldArray.push("Equipment Received Date Time");
          idArray.push("equipmentReceived");
     }
     
     if($('#attendance').val() !== ""){
          dateArray.push(moment($('#attendance').val(), "DD/MM/YYYY HH:mm"));
          fieldArray.push("Attendance Date Time");
          idArray.push("attendance");
     }
     
     if($('#completion').is(":visible") && $('#reinstatement').is(":visible") && $('#reinstatement').val() !== "" && $('#completion').val() !== ""){
          if(moment($('#reinstatement').val(), "DD/MM/YYYY HH:mm") < moment($('#completion').val(), "DD/MM/YYYY HH:mm")){
        	  dateArray.push(moment($('#reinstatement').val(), "DD/MM/YYYY HH:mm"));
        	  fieldArray.push("Reinstatement Date Time");
        	  idArray.push("reinstatement");
          }
          else{
        	  dateArray.push(moment($('#completion').val(), "DD/MM/YYYY HH:mm"));
        	  fieldArray.push("Completion Date Time");
        	  idArray.push("completion");
          }
     }
     else{
          if($('#reinstatement').val() !== "" && $('#reinstatement').is(":visible")){
               dateArray.push(moment($('#reinstatement').val(), "DD/MM/YYYY HH:mm"));
               fieldArray.push("Reinstatement Date Time");
               idArray.push("reinstatement");
          }
          else if($('#completion').val() !== "" && $('#completion').is(":visible")){
               dateArray.push(moment($('#completion').val(), "DD/MM/YYYY HH:mm"));
               fieldArray.push("Completion Date Time");
               idArray.push("completion");
          }
     }    
     
     for(i = 1; i < dateArray.length; i++) {
        if(dateArray[i-1] > dateArray[i]) {
        	$("#"+idArray[i-1]).closest(".form-group").addClass("has-error has-danger");
        	$("#"+idArray[i]).closest(".form-group").addClass("has-error has-danger");
            return fieldArray[i-1] + " should not be later than " + fieldArray[i] + ".";
        }
    }
    return true;
}

function getWorkOrder(){
     var workOrder = {}
     workOrder["workOrderId"] = $("#workOrderId").val();
     workOrder["woNumber"] = $("#woNumber").val();
     workOrder["woStatus"] = $("#woStatus").val();
     workOrder["woStatusText"] = $('#woStatus option:selected').html();
     workOrder["assetNumber"] = $("#assetNumber").val();
     if($("#assetGroup")[0] != undefined)
	     if($("#assetGroup")[0].idvalue != undefined)
	     	workOrder["assetGroupId"] = $("#assetGroup")[0].idvalue.toString();
     if($("#owingDept")[0] != undefined)
	     if($("#owingDept")[0].idvalue != undefined)
	    	 workOrder["owningDepartmentId"] = $("#owingDept")[0].idvalue.toString();
     workOrder["woType"] = $("#woType").val();
     if($('#breakdownDateInput').val() !== ""){
          workOrder["breakdownScheduleDate"] = $("#breakdownDateInput").val();
     }
     if($('#scheduleDateInput').val() !== ""){
          workOrder["breakdownScheduleDate"] = $("#scheduleDateInput").val();
     }
     workOrder["outbound"] = $("#outbound").val();
     workOrder["equipmentSent"] = $('input:radio[name=equipmentSent]:checked').val()
     workOrder["disinfectionStatus"] = $("#disinfectionStatus").val();
     workOrder["woDescription"] = $("#description").val();
     workOrder["vendorRemark"] = $("#vendorRemarks").val();
     workOrder["userRemark"] = $("#userRemarks").val();
     workOrder["contactPerson"] = $("#contactPerson").val();
     workOrder["contactPhone"] = $('#contactPhone').val();
     workOrder["contactEmail"] = $('#contactEmail').val();
     workOrder["requestedFor"] = $("#hiddenUser").val();
     workOrder["mode"] = $('#modeInput').val();
     workOrder["attachmentMode"] = $('#attachmentMode').val();
     workOrder["docId"] = $('#newDocumentIds').val();         
     workOrder["maintenanceBody"] = $('#hiddenMBody').val();                 
     workOrder["maintenanceBodyType"] = $('#mainBodyType').val();                    
     workOrder["autoSendWO"] = $('#autoSendEquip').val();
     workOrder["mContactPerson"] = $('#mainContactPerson').val();
     workOrder["mContactPhone"] = $('#mainContactPhone').val();
     workOrder["mContactFax"] = $('#mainContactFax').val();
     workOrder["mContactEmail"] = $('#mainContactEmail').val();
     workOrder["vendorReferenceNo"] = $('#vRefNum').val();
     workOrder["failureCauseCode"] = $('#failureCauseCode').val();
     workOrder["callRecieved"] = $('#callReceived').val();
     workOrder["failureSymptomCode"] = $('#failureSymptomCode').val();
     workOrder["equipmentRecievedDate"] = $('#equipmentReceived').val();
     workOrder["repairResoultionCode"] = $('#resolutionCode').val();
     workOrder["attendanceDate"] = $('#attendance').val();
     workOrder["equipmentCondition"] = $('#equipmentCondition').val();
     if($('#completion').val() !== ""){
          workOrder["completionDate"] = $("#completion").val();
     }
     if($('#reinstatement').val() !== ""){
          workOrder["reinstatementCompletionDate"] = $("#reinstatement").val();
     }
//     workOrder["reinstatementCompletionDate"] = $('#completion').val();
     workOrder["sparePartCost"] = $('#sparePartCost').val();
     workOrder["laborCost"] = $('#laborCost').val();
     workOrder["sparePartDesc"] = $('#sparePartDesc').val();
     workOrder["addMaterialCost"] = $('#addMaterialCost').val();
     workOrder["addLaborCost"] = $('#addLaborCost').val();
     workOrder["addMaterialDesc"] = $('#addMaterialDesc').val();
     workOrder["technicalName"] = $('#technicalName').val();
     workOrder["workOrderCompletedBy"] = $('#woCompletedBy').val();
     workOrder["resultAndAction"] = $('#resultAction').val();
     workOrder["serviceReport"] = $('#reportReference').val();
     workOrder["wrNumber"] = $('#wrNumber').val();
     workOrder["lastUpdateDate"] = $("#lastUpdateDate").val();
     
     workOrder["mPlan"] = document.getElementById("mainPlan").value;
     workOrder["mExpiryDate"] = document.getElementById("mainExpiryDate").value;
     workOrder["mJoinDate"] = document.getElementById("mainJoinDate").value;
     workOrder["supplierAgreementNumber"] = document.getElementById("supplierNo").value;
     workOrder["maintenanceContract"] = document.getElementById("contractNo").value;
     
     return workOrder;
}

function setMaintenanceInfo(populate, data){
	if(populate){
		$('#mainBodyType').val(data[0].maintenanceBodyType);
        $('#mainBodyType').selectpicker('refresh');
        $('#contractNo').val(data[0].contractNumber);
        $('#autoSendEquipReadOnly').val(data[0].autoSend);
        $('#autoSendEquip').val(data[0].autoSend);
        $('#autoSendEquip').selectpicker('refresh');
        $('#autoSendEquip').change();
        $('#supplierNo').val(data[0].supplierAgreementNumber);
        $('#mainContactPerson').val(data[0].maintContact);
        $('#mainPlan').val(data[0].maintenancePlan);
        $('#mainContactPhone').val(data[0].maintPhone);
        $('#mainJoinDate').val(data[0].maintenanceJoinDate);
        $('#mainContactFax').val(data[0].maintFax);
        $('#mainExpiryDate').val(data[0].maintenanceExpiryDate);
        $('#mainContactEmail').val(data[0].maintEmail);
        $('#mainInterval').val(data[0].maintenanceInterval);
        $('#mainContactPhone').trigger('blur');
        $('#mainContactFax').trigger('blur');
        $('#mainContactEmail').trigger('blur');
	}
	else{
		$('#mainBodyType').val("");
        $('#mainBodyType').selectpicker('refresh');
        $('#contractNo').val("");
        $('#autoSendEquipReadOnly').val("");
        $('#autoSendEquip').val("");
        $('#autoSendEquip').selectpicker('refresh');
        $('#supplierNo').val("");
        $('#mainContactPerson').val("");
        $('#mainPlan').val("");
        $('#mainContactPhone').val("");
        $('#mainJoinDate').val("");
        $('#mainContactFax').val("");
        $('#mainExpiryDate').val("");
        $('#mainContactEmail').val("");
        $('#mainInterval').val("");
        $('#mainContactEmail').prop('required', false);
        $("label[for='mainContactEmail']").removeClass("required");
        $('#mainContactFax').prop('required', false);
        $("label[for='mainContactFax']").removeClass("required");
        $('#mainContactPhone').trigger('blur');
        $('#mainContactFax').trigger('blur');
        $('#mainContactEmail').trigger('blur');
	}
}

function checkContactMethod(){
	$.ajax({
    	type: 'POST',
    	data : {
    		maintenanceNumber: $('#hiddenMBody').val()
    	},
    	dataType: 'json',
        url : './ContactMethod',                        
        success : function(data) {
        	if(data.length > 0){
            	$('#contactMethodTable').bootstrapTable('load', data);   
            	$('#contactMethodBTN').prop('disabled', false);
        	}
        	else{
        		$('#contactMethodBTN').prop('disabled', true);
        	}
        }
    });
}

//Used to remove required fields to read only and hidden fields
function removeRequired(){
	$('input[type=text][readonly]').prop('required', false);
    $('textarea[readonly]').prop('required', false);
    
    $('input:hidden').each(function() { 
 	   	$(this).prop('required', false);
    });
    
    $('textarea:hidden').each(function() { 
  	   	$(this).prop('required', false);
    });
    
    $('input[type=text][readonly]').each(function() { 
	   $label = $('label[for="'+ $(this).attr('id') +'"]');
	   if ($label.length > 0 ) {
		   $label.removeClass("required");
	   }
    });
    
    $('textarea[readonly]').each(function() { 
 	   $label = $('label[for="'+ $(this).attr('id') +'"]');
 	   if ($label.length > 0 ) {
 		   $label.removeClass("required");
 	   }
    });
    
    $(".hideDropdowns:hidden > .bootstrap-select > select").each(function() { 
   	 $(this).prop('required', false);
    });
}

function promptSchedule(){
	if($('#scheduleDate').length && !$('#scheduleDateInput').is('[readonly]') && (moment($('#scheduleDateInput').val(), "DD/MM/YYYY HH:mm") < moment())){
		var prompting = true;
		var status = $('#woStatus').find("option:selected").text();
		
		if(WOMode == "e" && (status !== "Unreleased" && status !== "Released" && status !== "On Hold")){
			if(status == "Complete"){
				if(startingSchedule == $('#scheduleDateInput').val()){
					createEAMWO(WOMode, WONumber);
					return;
				}
			}
			else{
				createEAMWO(WOMode, WONumber);
				return;
			}
		}
    	
    	var scheduleConfirm = function(scheduleCallback){
    		if(WOMode == "e"){
    			$('#confirmationMsg').html("The PM Work Order will be updated with a past schedule date. Are you sure you want to proceed?");
    		}
    		else{
    			$('#confirmationMsg').html("The PM Work Order will be created with a past schedule date. Are you sure you want to proceed?");
    		}
    		
			$("#confirmationModal").modal('show');
		
			$("#confirmYesBTN").on("click", function(){
				if(prompting){
					scheduleCallback(true);
					$("#confirmationModal").modal('hide');
				}
			});
			  
			$("#confirmNoBTN").on("click", function(){
				if(prompting){
					scheduleCallback(false);
					$("#confirmationModal").modal('hide');
				}
			});
		};
		
		scheduleConfirm(function(scheduleConfirm){
			if(scheduleConfirm){
				createEAMWO(WOMode, WONumber);
			}
			prompting = false;
		});
	}
	else{
		createEAMWO(WOMode, WONumber);
	}
}
