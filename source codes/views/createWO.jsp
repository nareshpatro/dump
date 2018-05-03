<%-- 
    Document   : createWO
    Created on : Jun 14, 2017, 12:23:28 PM
    Author     : Carmen
    Last Update: Jul, 4 2017, 10:38 AM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page import="java.io.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Create Work Orders</title>

        <script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/createOrders.js?v=${initParam.buildTimeStamp}" />"></script>
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
        
        <%  
        	String woNumber = ((request.getParameter("woNumber") == null) ? "" : request.getParameter("woNumber"));
            String m = ((request.getParameter("m") == null) ? "" : request.getParameter("m"));
            String P = ((request.getParameter("P") == null) ? "" : request.getParameter("P"));
        %>
        
        <script>
        $(document).ready(function(){
			if($('#woType').val() == "20"){
        		$('#reinstatementWrapper').hide();
        		$('#reinstatementWrapper').addClass("removeBorder");
        	}
        <% if("IT Asset".equals(session.getAttribute("respType"))){  %>
        	$('#autoSendEquip option[value="XML or Portal-NO"]').remove();
        	$('#autoSendEquip option[value="XML or Portal-YES"]').remove();
        	$('#autoSendEquip').selectpicker('refresh');
        	$("label[for='maintenanceVendor']").removeClass("required");
        	$('#maintenanceVendor').prop('required', false);
        	$("label[for='autoSendEquipReadOnly']").removeClass("required");
        	$('#autoSendEquip').prop('required', false);
        	$("label[for='mainBodyTypeReadOnly']").removeClass("required");
        	$('#mainBodyType').prop('required', false);
        	
        	var selectedStatus = $('#woStatus').find("option:selected").text();
        	if(selectedStatus.indexOf("Complete") !== -1){		
        		if(!$('#completion').is('[readonly]') ){
        			$('#completion').prop('required', true);
          			$("label[for='completion']").addClass("required");
        		}
				$('#repairDetailsPanel').collapse('show');	
		    }
		    else{
			    $('#completion').prop('required', false);
    			$("label[for='completion']").removeClass("required");
		    }
        	
        	$('#woStatus').on('changed.bs.select', function () {
        		var selected = $(this).find("option:selected").text();
       		  	if(selected.indexOf("Complete") !== -1){
	       		  	if(!$('#completion').is('[readonly]') ){
		      			$('#completion').prop('required', true);
		      			$("label[for='completion']").addClass("required");
	       		  	}
					$('#repairDetailsPanel').collapse('show');
	      			$('#completionDetailsSection').show();
	      			$('#completionDetailsPanel').collapse('show');
	      		}
      			else {
					$('#repairDetailsPanel').collapse('hide');
      				$('#completion').prop('required', false);
      				$("label[for='completion']").removeClass("required");
					$('#completionDetailsPanel').collapse('hide');
      				$('#completionDetailsSection').hide();	  
      			}
        	}); 
        	        	
        <% } else { %>        	
        	$('#woType').on('changed.bs.select', function () {
        	    var selected = $(this).find("option:selected").val();
				var statusSelected = $('#woStatus').find("option:selected").text();
        	    		
        	    if(selected == "20"){
	       	    	if(statusSelected.indexOf("Complete") !== -1){
	       	    		$('#repairDetailsPanel').collapse('show');
	        			completeCM(false);
	        			completePM(true);
	        			$('#woForm').validator('update');
	 			   }
	 			   else{
						$('#repairDetailsPanel').collapse('hide');
	 					completePM(false);
	 					completeCM(false);
	        			$('#woForm').validator('update');
	 			   }
	       		}
	       		else {
	       			if(statusSelected.indexOf("Complete") !== -1){
	       				$('#repairDetailsPanel').collapse('show');
	        			completePM(false);
	        			completeCM(true);
	        			$('#woForm').validator('update');
	 			   }
	 			   else{
	 					completeCM(false);
	 					completePM(false);
	        			$('#woForm').validator('update');
	 			   }
	       		}
        	}); 
        	
        	$('#woStatus').on('changed.bs.select', function () {
       		   	var selected = $(this).find("option:selected").text();
  				$('#reasonsHint').hide();
	      		if(selected.indexOf("Complete") !== -1){
	      			if(!$('#resultAction').is('[readonly]') ){	      			
	      				$('#resultAction').prop('required', true);
	      				$("label[for='resultAction']").addClass("required");
	      			}
	      			$('#repairDetailsPanel').collapse('show');
	      			$('#completionDetailsSection').show();
  			 		$('#completionDetailsPanel').collapse('show');
	      			 	if($('#woType').val() == "20"){
		        			completeCM(false);
		        			completePM(true);
							$('#woForm').validator('update');
	      			   	}
	      				else{
	      				 	completePM(false);
		 					completeCM(true);
							$('#woForm').validator('update');
	      				}
	      		   	}
      			else if (selected == "On Hold"  || selected == "Rejected" || selected == "Cancelled"){   
      				if(selected !== "On Hold"){
      	  				$('#reasonsHint').show();
      				}      				
					$('#completionDetailsPanel').collapse('hide');
      				$('#completionDetailsSection').hide();
	      			if(!$('#resultAction').is('[readonly]') ){	      			
      					$('#resultAction').prop('required', true);
	      				$("label[for='resultAction']").addClass("required");
	      			}
	      			$('#repairDetailsPanel').collapse('show');
      				completePM(false);
 					completeCM(false);
	        		$('#woForm').validator('update');
      			}
      			else {
					$('#completionDetailsPanel').collapse('hide');
      				$('#completionDetailsSection').hide();
      				$('#resultAction').prop('required', false);
      				$("label[for='resultAction']").removeClass("required");
      				$('#repairDetailsPanel').collapse('show');
      				completePM(false);
 					completeCM(false);
	        		$('#woForm').validator('update');
      			}
        	}); 
        	
        <% } %>
		});					   
        function nonItView(typeId, status){
        	<% if(!"IT Asset".equals(session.getAttribute("respType"))){  %>
	        	if(typeId == "10"){
	        		$('#reinstatementWrapper').show();
	        		if(status == "Complete"){
	        			$('#completionDetailsPanel').collapse('show');
	      				$('#completionDetailsSection').show();
	        			completePM(false);
	        			completeCM(true);
	 			   }
	 			   else{
	 					completeCM(false);
	 					completePM(false);
	 			   }
	        	}
	        	else{
	        		$('#reinstatementWrapper').hide();
	        		$('#reinstatementWrapper').addClass("removeBorder");
	        		if(status == "Complete"){
	        			$('#completionDetailsSection').show();
		        		$('#completionDetailsPanel').collapse('show');
	        			completeCM(false);
	        			completePM(true);
	 			   }
	 			   else{
	 					completePM(false);
	 					completeCM(false);
	 					
	 			   }
	        	}	
	        	
	        	if(status == "On Hold" || status == "Rejected" || status == "Cancelled"){
	      			if(!$('#resultAction').is('[readonly]') ){	      			
	            		$('#resultAction').prop('required', true);
	        			$("label[for='resultAction']").addClass("required");
	        			if(status !== "On Hold"){
		      				$('#reasonsHint').show();
		        		}
	      			}
	        		$('#repairDetailsPanel').collapse('show');
	             }
	        	else{
	        		$('#reasonsHint').hide();
	        	}
	        	
        	<% } %>
        }
         
		function checkEmptyRemarks(description){
	      	 <% if("IT Asset".equals(session.getAttribute("respType"))){  %>
	      	 	$('#userRemarks').val(description);
	      	 <% } %>
        }
        
		 function restoreDefaultForm(){
        	$.xhrPool.abortAll();
        	$('#contactPerson').val('<% out.print((request.getSession().getAttribute("ebsFullName") == null) ? "" : request.getSession().getAttribute("ebsFullName")); %>');
        	$('#contactPhone').val('<% out.print((request.getSession().getAttribute("ebsHAContactPhone") == null) ? "" : request.getSession().getAttribute("ebsHAContactPhone")); %>');
        	$('#contactEmail').val('<% out.print((request.getSession().getAttribute("ebsEmail") == null) ? "" : request.getSession().getAttribute("ebsEmail")); %>');
        	$('.alert').hide();
        };
		
        function getParams(){
            var parameters = ["<% out.print(woNumber); %>", "<% out.print(m); %>", "<% out.print(P); %>"];
            return parameters;
        };
        </script>
    </head>

    <body>
        <jsp:include page="sidebarHeader.jsp" />
        
        <div id="content"  style="height: 230%">
           <!-- Loading div for retrieving data waiting time -->
            <div class="container-fluid col-md-12">
	            <div id="loading" style="display:none">
	            	<img src="<spring:url value="/resources/images/spinner.gif" />" alt="Loading" height="100" width="100">
	            	<h5 id="loadingMessage" class="text-primary"></h5>
	            </div>
            </div>
            
            <!-- Div to show successful creation and update -->
            <div class="container-fluid col-md-12">
                <div class="alert alert-danger lovSearchAlert" role="alert" id="postResultsBox" style="display: none;"></div>
            </div>
            
			 <!--  Div to show warnings in creation -->
            <div id="createWOToolbar" class="container-fluid col-md-12 btn-toolbar" style="margin-bottom: 10px;">
                <button type='button' id="backBTN" class="btn btn-default"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Return to Search</button>
                <% if ("Y".equals(session.getAttribute("respRequestView"))) { %>
                	<button type='button' id="goToWRBTN" class="btn btn-default pull-right" style="display:none;"><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span> Go To Work Request</button>
                <% } %>
                <button type='button' id="printBTN" class="btn btn-default pull-right"><span class="glyphicon glyphicon-print" aria-hidden="true"></span> Print</button>
                <% if ("Y".equals(session.getAttribute("respOrderUpdate"))) { %>
                    <a id="editBTN" class="btn btn-default pull-right"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Update</a>
                <% } %>
            </div>
            
             <form:form class="form-horizontal" id="woForm" role="form" method="POST" modelAttribute="workOrder" data-toggle="validator" data-disable="false" data-focus="false">
				<form:hidden path="workOrderId" id="workOrderId" />
				<form:hidden path="mode" id="modeInput"/>
				<form:hidden path="lastUpdateDate" id="lastUpdateDate" />
				<!-- Information for attachments - Attachment mode, new document ids for new attachments, del document ids for deleted attachments -->
				<input type="hidden" name="attachment" id="attachmentMode" />
				<input type="hidden" name="documentId" id="newDocumentIds"/>
				<input type="hidden" name="documentId" id="delDocumentIds"/>
                <div class="container-fluid col-md-12">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 class="panel-title">
                                Work Order Overview
                            </h4>
                        </div>
                        <div class="panel-body">
                            <div class="form-group overviewBorders">
                                <label class='control-label col-md-2 woNumberInfo' for="woNumber">Work Order Number</label>
                                <div class="col-md-4 woNumberInfo">
                                    <input readonly type="text" class="form-control woNumberInfo" id="woNumber" name='woNumber'>
                                </div>
                                
                            <div id="wrNumberWrapper" style="display: none">
									<label class='control-label col-md-2' for="wrNumber">Work Request Number</label>
									<div class="col-md-4">
										<form:input readonly="true" type="text" class="form-control" id="wrNumber" path='wrNumber'/>
									</div>
								</div>
                            </div>
                            
                            <div class="form-group overviewBorders">
                                <label class='control-label col-md-2 required' for="woStatusReadOnly">Work Order Status</label>
                                	<div class=" col-md-4">
	                                	<input type="text" class="form-control selectReadOnly" id="woStatusReadOnly"  style="display: none"> <!--Added 30/08/17-->
										<div class="hideDropdowns" id="woStatusHide">
											<form:select path="woStatus" class="form-control selectpicker"  id="woStatus">
		                                        <form:options items="${woStatusList}" itemValue="name" itemLabel="desc"/>
		                                    </form:select>
										</div>
									</div>

                                <div id="assetNumberGroup" class="form-group removeBorder  overviewBorders">
                                    <label class="control-label col-md-2 required" for="assetNumber">Asset Number</label>
                                    <div class="col-md-4">
                                        <div class="input-group">
                                            <form:input path="assetNumber" class="form-control" id="assetNumber"  maxlength="30" required="required"/>
                                            <span class="input-group-btn">
                                            	<button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetNumber" id="assetNumberFiltered" style="border-top-right-radius: 4px; border-bottom-right-radius: 4px"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                                <button disabled id="assetInfoBTN" type="button" class="btn btn-link btn-sm" data-toggle="modal" data-target="#assetDetailsModal" data-toggle="tooltip" data-placement="top" title="Click to view Asset detail" data-loading-text="Retrieving.."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></button>
                                            </span>
                                        </div>
                                        <span class="help-inline text-danger" style="display: none" id="assetNumberHelp"><spring:message code="HA_WARNING_GENERAL_INVALIDASSETNUMBER" /></span>
                                    </div>
                                </div>
                            </div>
                            
                            <% if("IT Asset".equals(session.getAttribute("respType"))){  %>
	                            <div class="assetInfoWrapper"  style="display:none">
		                            <div class="form-group overviewBorders">
		                            	<label class="control-label col-md-2" for="assetGroup">Asset Group</label>
		                                <div class="col-md-4">
		                                    <input type="text" class="form-control" id="assetGroup" readonly></input>
		                                </div>
		                                
		                                <div id="woOrgWrapper" style="display: none">
			                                <label class="control-label col-md-2" for="woOrg">EAM Org</label>
			                                <div class="col-md-4">
			                                    <input type="text" class="form-control" id="woOrg" readonly></input>
			                                </div> 
			                            </div>
			                            
		                                <div id="assetOrgWrapper">
			                                <label class="control-label col-md-2" for="assetOrg">Asset Organization</label>
			                                <div class="col-md-4">
			                                    <input type="text" class="form-control" id="assetOrg" readonly></input>
			                                </div> 
		                                </div>
		                            </div>
		                            
		                            <div class="form-group overviewBorders">
		                                <label class="control-label col-md-2" for="assetDescription">Asset Description</label>
		                                <div class="col-md-10">
		                                     <p class="control-label" id="assetDescription" style="padding-left: 8px"></p>
		                                </div> 
		                            </div> 
	                            </div>
                            <% } else { %>
                            	<div class="assetInfoWrapper"  style="display:none">
		                            <div class="form-group overviewBorders">
		                            	<div id="woOrgWrapper" style="display: none">
			                            	<label class="control-label col-md-2" for="woOrg">EAM Org</label>
			                                <div class="col-md-4">
			                                    <input type="text" class="form-control" id="woOrg" readonly></input>
			                                </div> 
		                            	</div>
		                            	
			                            <div id="assetOrgWrapper">
			                            	<label class="control-label col-md-2" for="assetOrg">Asset Organization</label>
			                                <div class="col-md-4">
			                                    <input type="text" class="form-control" id="assetOrg" readonly></input>
			                                </div>
			                            </div>
		                                		                                
		                               <label class="control-label col-md-2" for="assetDescription">Asset Description</label>
		                                <div class="col-md-4">
		                                     <p class="control-label" id="assetDescription" style="padding-left: 8px"></p>
		                                </div>
		                            </div>
	                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <div class="container-fluid col-md-12">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 id="orderDetailsTitle" class="panel-title">
                                <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#orderDetailsPanel">Order Details</a>
                            </h4>
                        </div>
                        <div id="orderDetailsPanel" class="panel-collapse in">
                            <div class="panel-body">
                                <div class="form-group">
									<label class="control-label col-md-2 required" for="woTypeReadOnly">Work Order Type</label>
		                                  <div class="col-md-4">
			                                    <input type="text" class="form-control selectReadOnly order" id="woTypeReadOnly"  style="display: none">
			                                    <div class="hideDropdowns orderHide" id="woTypeWrapper">
				                                  	<form:select path="woType" class="form-control selectpicker"  id="woType">
				                                        <form:options items="${woTypeList}" itemValue="name" itemLabel="desc"/>
				                                    </form:select>
				                                </div>
		                                  </div>

	                                 	<div id="breakdownDate" class="form-group removeBorder">
		                                 <label  class="control-label col-md-2 required" for="breakdownDateInput">CM Breakdown Date Time</label>
			                             	<div class=" col-md-4">    
			                                    	<div class='input-group date noFutureDate'>
			                                          <form:input path="breakdownDateInput" class="form-control order" id="breakdownDateInput" required="required"/>
			                                          <span class="input-group-addon orderHide">
			                                              <span class="glyphicon glyphicon-time"></span>
			                                          </span>
			                                        </div>
			                                        <small class="form-text text-muted orderHide"><spring:message code="HA_WARNING_GENERAL_PASTDATESONLY" /></small>
			                                 </div>
			                             </div>
		                            
		                                <div id="scheduleDate" style="display: none" class="form-group removeBorder">
		                                <label  class="control-label col-md-2 required" for="scheduleDateInput">PM Scheduled Date Time</label>
		                                    <div class=" col-md-4">
			                                      <div class='input-group date dateTimePicker' id="validateSchedule">
			                                          <form:input path="scheduleDateInput" class="form-control order" id="scheduleDateInput"/>
			                                          <span class="input-group-addon orderHide">
			                                              <span class="glyphicon glyphicon-time"></span>
			                                          </span>
			                                      </div>
			                                      <!-- <small class="form-text text-muted orderHide" id="scheduleHint"><spring:message code="HA_WARNING_GENERAL_FUTUREDATESONLY" /></small>  -->
		                                    </div>
		                                </div>
	                            </div>
                                
                                <% if("IT Asset".equals(session.getAttribute("respType"))){  %>
								 <div class="form-group" id="scheduleCompletionWrapper" style="display:none">
                                    <label class='control-label col-md-2' for="scheduleStart">Schedule Start Date</label>
                                    <div class="col-md-4">
                                    	<input type="text" class="form-control" id="scheduleStart" readonly>
                                    </div>
                                    
                                    <label class='control-label col-md-2' for="scheduleCompletion">Schedule Completion Date</label>
                                    <div class="col-md-4">
                                    	<input type="text" class="form-control" id="scheduleCompletion" readonly>
                                    </div>
                                </div>
                                <% } %>
                                
                                <div class="form-group">
                                	<label class="control-label col-md-2" for="equipmentSentReadOnly">Equipment Sent To Workshop</label>
                                	 <div class=" col-md-4 selectReadOnly equipmentSentReadOnly" style="display: none">
                                        <input type="text" class="form-control order" id="equipmentSentReadOnly">
                                    </div>
                                    <fieldset class="col-md-4 orderHide" id="equipmentSent">
                                        <label class="radio-inline"><form:radiobutton path="equipmentSent" value="YES" class="radio" />Yes</label>
                                        <label class="radio-inline"><form:radiobutton path="equipmentSent" value="NO" class="radio" />No</label>
                                    </fieldset>
                                    
                                    <label class='control-label col-md-2 required' for="disinfectionStatusReadOnly">Disinfection Status</label>
                                    <div class="col-md-4">
                                    	<input type="text" class="form-control selectReadOnly order" id="disinfectionStatusReadOnly" style="display: none">
	                                    <div class="hideDropdowns orderHide">
		                                    <form:select path="disinfectionStatus" class="form-control selectpicker" id="disinfectionStatus" required="required">
		                                        <form:options items="${disinfectionList}" itemValue="name" itemLabel="desc"/>
		                                    </form:select>
										</div>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                  <label class="control-label col-md-2 required" for="description">Work Order Description</label>
                                  <div class="col-md-10">
                                     <textarea class="form-control vresize order" maxlength="240" id="description" rows="4" name='description' required="required"></textarea>
                                  </div>
                                </div>
                                
                                
                                <div class="form-group">
                                  <label class='control-label col-md-2' for="vendorRemarks">Remark for Vendor</label>
                                  <div class="col-md-10">
                                    <textarea class="form-control vresize order" id="vendorRemarks" maxlength="150" name='remarkForVendor' rows="2"></textarea>
                                  </div>
                                </div>
                                
                                <div class="form-group">
                                    <label class="control-label col-md-2 required" for="contactPerson">HA Contact Person</label>
                                    <div class="col-md-4">
                                        <form:input type="text" class="form-control order hideDropdowns" maxlength="50" id="contactPerson" path='contactPerson' required="required" />
                                    	<p class="control-label selectReadOnly" id="contactPersonReadOnly" style="padding: 3px 0px 3px 8px; min-height: 26px; display: none; word-wrap: break-word;"></p>
                                    </div>
                                </div>
                                      
                                <div class="form-group">
                                    <label class="control-label col-md-2 required" for="contactPhone">HA Contact Phone</label>
                                    <div class="col-md-4">
                                       <form:input type="text" path="contactPhone" class="form-control order" maxlength="50" id="contactPhone" required="required" data-error="Please enter a valid phone number."  pattern="^\(?([0-9]{0,3})\)?([ ]?)([0-9]{4})([ ]?)([0-9]{4})$" />
                                        <div class="help-block with-errors"></div>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label class='control-label col-md-2' for="contactEmail">HA Contact Email</label>
                                    <div class="col-md-4">
                                    	<form:input type="text" path="contactEmail" class="form-control order hideDropdowns" maxlength="240" id="contactEmail" data-error="Please enter a valid email" pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,3}$" />
                                    	<p class="control-label selectReadOnly" id="contactEmailReadOnly" style="padding: 3px 0px 3px 8px; min-height: 26px; display: none; word-wrap: break-word;"></p>
                                    	<div class="help-block with-errors"></div>
                                    </div>
                                </div>

								<div class="form-group" id="outboundWrapper">
                                	  <label class="control-label col-md-2" for="outbound">Outbound Date Time</label>
                                      <div class="col-md-4">
                                          <div class='input-group date dateTimePicker removeButton'>
                                               <input type='text' class="form-control col-md-8" maxlength="150" name="outbound" id="outbound" readonly/>
                                          </div>
                                      </div>
                                </div>		
                                
                            </div>
                        </div>
                    </div>

                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 id="maintenanceDetailsTitle" class="panel-title">
                                <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#maintenanceDetailsPanel">Maintenance Details</a>
                            </h4>
                        </div>
                        <div id="maintenanceDetailsPanel" class="panel-collapse in">
                            <div class="panel-body">
                                <div class="form-group">
                                	<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                    <label class="control-label col-md-4 required" for="maintenanceVendor">Maintenance Body</label>
	                                    <div class="col-md-8">
	                                    	<p style="display: none; padding-left: 8px" class="control-label" id="maintenanceVendorReadOnly"></p>
		                                    <div class="input-group" id="maintenanceVendorWrapper">
		                                        <input type="text" class="form-control" id="maintenanceVendor" required="required">
		                                        <span class="input-group-btn">
		                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="maintenanceVendor" id="maintenanceVendor"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
		                                        </span>
		                                        <input type="hidden" class="form-control" id="hiddenMBody" name="maintenanceVendor">
		                                    </div>
		                                    <small id="maintenanceVendorNum" class="maintenanceHide form-text text-muted"></small>
		                                </div>
		                            </div>

									<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                    <label class="control-label col-md-4 required" for="mainBodyTypeReadOnly">Maintenance Body Type</label>
	                                    <div class="col-md-8">
	                                        <input type="text" class="form-control selectReadOnly maintenance" id="mainBodyTypeReadOnly" style="display: none">
	                                        <div class="hideDropdowns maintenanceHide">
		                                        <form:select class="form-control selectpicker" id="mainBodyType" path='maintenanceBodyType' data-live-search="true" required="required">
				                                	<form:option value="" label="-- Select --" />
			                                    	<form:options items="${maintenanceBodyTypeList}" itemValue="name" itemLabel="desc"/>
				                                </form:select>
		                                    </div>
		                           		</div>
	                               	</div>
                                </div>
							
                                <div class="form-group">
                                    <label class="control-label col-md-2" for="contractNo">Maintenance Contract Number</label>
                                    <div class="col-md-4">
                                        <input readonly maxlength="30" type="text" class="form-control" id="contractNo" name='contractNumber'>
                                    </div>

									<div class="form-group removeBorder">
										<label class="control-label col-md-2 required" for="autoSendEquipReadOnly">Auto Send WO to Supplier</label>
										<div class="col-md-4" id="autoSendWrapper">
											<input type="text" class="form-control selectReadOnly maintenance" id="autoSendEquipReadOnly" style="display: none">
											<% if(!"IT Asset".equals(session.getAttribute("respType"))){  %>
													<div class="hideDropdowns maintenanceHide pull-left" id="autosendDropdown">
														<form:select class="form-control selectpicker" id="autoSendEquip" path='autoSendWO' data-live-search="true" required="required">
															<form:option value="" label="-- Select --" />
															<form:options items="${autoSendWOList}" itemValue="name" itemLabel="desc"/>
														</form:select>
													</div>
											
					                            	<div class="pull-right" id="contactMethod">
					                            	    <button disabled id="contactMethodBTN" type="button" class="btn btn-link btn-sm pull-right"><span class="glyphicon glyphicon-envelope" aria-hidden="true"></span></button>
													</div>
											<% } else { %>
												<div class="hideDropdowns maintenanceHide">
													<form:select class="form-control selectpicker" id="autoSendEquip" path='autoSendWO' data-live-search="true" required="required">
														<form:option value="" label="-- Select --" />
														<form:options items="${autoSendWOList}" itemValue="name" itemLabel="desc"/>
													</form:select>
												</div>
											<% } %>
											
											
											
											
											
										</div>
									</div>
								</div>

                                <div class="form-group">
                                    <label class="control-label col-md-2" for="supplierNo">Supplier Agreement Number</label>
                                    <div class="col-md-4">
                                        <input maxlength="30" readonly type="text" class="form-control" id="supplierNo" name='supplierNumber'>
                                    </div>

									<div class=" form-group removeBorder">
	                                    <label class="control-label col-md-2" for="mainContactPerson">Maintenance Contact Person</label>
	                                    <div class="col-md-4">
	                                         <input type="text" class="form-control maintenance" id="mainContactPerson" maxlength="150" name='maintenanceContactPerson'>
	                                    </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="control-label col-md-2" for="mainPlan">Maintenance Plan</label>
                                    <div class="col-md-4">
                                        <input readonly type="text" class="form-control" maxlength="150" id="mainPlan" name='maintenancePlan'>
                                    </div>
                                    
									<div class=" form-group removeBorder">
	                                    <label class="control-label col-md-2" for="mainContactPhone">Maintenance Contact Phone</label>
	                                    <div class="col-md-4">
	                                        <input type="text" pattern="^[0-9\-\+]{8,15}$" data-error="Please enter a valid phone number." class="form-control maintenance" id="mainContactPhone" maxlength="50" name='maintenanceContactPhone'>
	                                    	<div class="help-block with-errors"></div>
	                                    </div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="control-label col-md-2" for="mainJoinDate">Maintenance Join Date</label>
                                    <div class="col-md-4">
                                    	<input readonly type="text" class="form-control" id="mainJoinDate" name='mainJoinDate'>
                                    </div>
                                    
									<div class="form-group removeBorder">
	                                    <label class='control-label col-md-2' for="mainContactFax">Maintenance Contact Fax</label>
	                                    <div class="col-md-4">
	                                        <input type="text" pattern="^[0-9\-\+]{8,15}$" data-error="Please enter a valid fax number." class="form-control maintenance" maxlength="30" id="mainContactFax" name='maintenanceContactFax'>
											<div class="help-block with-errors"></div>
	                                    </div>
	                            	</div>
                                </div>

                                <div class="form-group">
                                    <label class="control-label col-md-2" for="mainExpiryDate">Maintenance Expiry Date</label>
                                    <div class="col-md-4">
                                    	<input readonly type="text" class="form-control" id="mainExpiryDate" name='mainExpiryDate'>
                                    </div>
                                    
									<div class="form-group removeBorder">
	                                    <label class='control-label col-md-2' for="mainContactEmail">Maintenance Contact Email</label>
	                                    <div class="col-md-4">
	                                        <input type="text" class="form-control maintenance" data-error="Please enter a valid email." pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,3}$" id="mainContactEmail" maxlength="150" name='maintenanceContactEmail'>
	                                    	<div class="help-block with-errors"></div>
	                                    </div>
                                    </div>
                                </div>
			
								<div class="form-group">
                                    <label class='control-label col-md-2' for="mainInterval">Maintenance Interval (Months)</label>
                                    <div class="col-md-4">
                                        <input readonly type="text" class="form-control" maxlength="10" id="mainInterval" name='mainInterval'>
                                    </div>
                                </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h4 id="repairDetailsTitle" class="panel-title">
                                     <a data-toggle="collapse" class="accordion-toggle collapsed" data-parent="#accordion" href="#repairDetailsPanel" id="repairLink">Repair Details</a>
                                </h4>
                            </div>
                            
                            <div id="repairDetailsPanel" class="panel-collapse collapse">
                                <div class="panel-body">
                                    <div class="form-group">
                                    	<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="failureCauseCodeReadOnly">Failure Cause Code</label>
	                                        <div class="col-md-8">
	                                        	<input type="text" class="form-control selectReadOnly" id="failureCauseCodeReadOnly"  style="display: none">
					                            <div class="hideDropdowns">
		                                            <form:select class="form-control selectpicker col-md-4" id="failureCauseCode" path="failureCauseCode" data-live-search="true">
		                                               	<form:option value="" label="-- Select --" />
		                                        		<form:options items="${failureCauseList}" itemValue="name" itemLabel="desc"/>
		                                            </form:select>
		                                        </div>
	                                        </div> 
                                        </div>
                                        
                                    	<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                    	<label class="control-label col-md-4" for="callReceived">Call Received Date Time</label>
	                                        <div class="col-md-8">
	                                            <div class='input-group date dateTimePicker removeButton'>
	                                                <input type='text' class="form-control col-md-8" name="callReceived" id="callReceived"/>
	                                                <span class="input-group-addon">
	                                                    <span class="glyphicon glyphicon-time"></span>
	                                                </span>
	                                            </div>
	                                        </div>
	                                    </div>
                                    </div>
                            
                                    <div class="form-group">
                                    	<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="failureSymptomCodeReadOnly">Failure Symptom Code</label>
	                                        <div class="col-md-8">
	                                        	<input type="text" class="form-control selectReadOnly" id="failureSymptomCodeReadOnly"  style="display: none">
					                            <div class="hideDropdowns">
			                                          <form:select class="form-control selectpicker" id="failureSymptomCode" path='failureSymptomCode' data-live-search="true">
			                                                <form:option value="" label="-- Select --" />
		                                        			<form:options items="${failureSymptomList}" itemValue="name" itemLabel="desc"/>
			                                          </form:select>
			                                   	</div>
	                                        </div>
	                                	</div>
	                                	
                                   		<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="equipmentReceived">Equipment Received Date Time</label>
	                                        <div class="col-md-8">
	                                            <div class='input-group date dateTimePicker removeButton'>
	                                                <input type='text' class="form-control col-md-8" name="equipmentReceived" id="equipmentReceived"/>
	                                                <span class="input-group-addon">
	                                                    <span class="glyphicon glyphicon-time"></span>
	                                                </span>
	                                            </div>
	                                        </div>
	                                    </div>
                                    </div>
                                    
                                    <div class="form-group">
                                    	<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="resolutionCodeReadOnly">Repair Resolution Code</label>
	                                        <div class="col-md-8">
	                                        <input type="text" class="form-control selectReadOnly" id="resolutionCodeReadOnly"  style="display: none">
					                            <div class="hideDropdowns">
			                                          <select class="form-control selectpicker" id="resolutionCode" name='resolutionCode' disabled data-live-search="true">
			                                                <option value=''>-- Select --</option>
			                                          </select>
			                                   	</div>
	                                        </div>
	                                    </div>
	                                    
                                    	<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="attendance">Attendance Date Time</label>
	                                        <div class="col-md-8">
	                                            <div class='input-group date dateTimePicker removeButton'>
	                                                <input type='text' class="form-control col-md-8" name="attendance" id='attendance'/>
	                                                <span class="input-group-addon">
	                                                    <span class="glyphicon glyphicon-time"></span>
	                                                </span>
	                                            </div>
	                                        </div>
	                                    </div>
                                    </div>
                                    
                                    <div class="form-group">
                                    	<div class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="equipmentConditionReadOnly">Equipment Condition</label>
	                                        <div class="col-md-8">
	                                        	<input type="text" class="form-control selectReadOnly" id="equipmentConditionReadOnly"  style="display: none">
					                            <div class="hideDropdowns">
					                            		<form:select class="form-control selectpicker" id="equipmentCondition" path='equipmentCondition' data-live-search="true">
				                                        	<form:option value="" label="-- Select --" />
			                                        		<form:options items="${equipmentConditionList}" itemValue="name" itemLabel="desc"/>
				                                        </form:select>
			                                    </div>
	                                        </div>
                                        </div>
                                        
                                        <div id="reinstatementWrapper" class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="reinstatement">CM Reinstatement Date Time</label>
	                                        <div class="col-md-8">
	                                            <div class='input-group date dateTimePicker removeButton'>
	                                                <input type='text' class="form-control col-md-8" name="reinstatement" id='reinstatement'/>
	                                                <span class="input-group-addon">
	                                                    <span class="glyphicon glyphicon-time"></span>
	                                                </span>
	                                            </div>
	                                        </div>
	                                     </div>
                                    </div>
                                                                        
                                    <div class="form-group">
                                        <label class="control-label col-md-2" for="sparePartCost">Spare Part Cost</label>
                                        <div class="col-md-4 form-group removeBorder">
                                            <input type="text" class="form-control" id="sparePartCost" name='sparePartCost' maxlength="150" pattern="^[0-9]+(\.[0-9][0-9]?)?" data-error="Numerical values to 2 d.p. only">
                                        	<div class="help-block with-errors"></div>
                                        </div>
                                        
                                        <label class="control-label col-md-2" for="laborCost">Labor Cost</label>
                                        <div class="col-md-4 form-group removeBorder">
                                            <input type="text" class="form-control" id="laborCost" name='laborCost' maxlength="150" pattern="^[0-9]+(\.[0-9][0-9]?)?" data-error="Numerical values to 2 d.p. only">
                                        	<div class="help-block with-errors"></div>
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class='control-label col-md-2' for="sparePartDesc">Spare Part Description</label>
                                        <div class="col-md-10 form-group removeBorder">
                                          <textarea class="form-control vresize" id="sparePartDesc" maxlength="150" name='sparePartDesc' rows="2"></textarea>
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="control-label col-md-2" for="technicalName">Technician Name</label>
                                        <div class="col-md-4 form-group removeBorder">
                                            <input type="text" class="form-control" maxlength="150" id="technicalName" name="technicalName">
                                        </div>
                                    </div>
                                                                        
                                    <div class="form-group">
                                        <label class='control-label col-md-2' for="resultAction">Result and Action Taken</label>
                                        <div class="col-md-10">
                                          <textarea class="form-control vresize" maxlength="150" id="resultAction" name="resultAction" rows="2"></textarea>
                                          <small class="form-text text-muted" style="color:red; display: none" id="reasonsHint"><spring:message code="HA_WARNING_WO_REASONFORCANCELORREJECT" /></small>
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class='control-label col-md-2' for="reportReference">Service Report Reference</label>
                                        <div class="col-md-10">
                                          <textarea class="form-control vresize" id="reportReference" maxlength="150" name="reportReference" rows="2"></textarea>
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
		                                <label class='control-label col-md-2' for="userRemarks">Remark for User</label>
		                                <div class="col-md-10">
		                                  <textarea class="form-control vresize" id="userRemarks" maxlength="500" rows="2" name='remarkForUser'></textarea>
		                                </div>
	                                </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="panel panel-primary" id="completionDetailsSection" style="display: none">
                            <div class="panel-heading">
                                <h4 id="completionDetailsTitle" class="panel-title">
                                     <a data-toggle="collapse" class="accordion-toggle collapsed" data-parent="#accordion" href="#completionDetailsPanel" id="completionLink">Completion Details</a>
                                </h4>
                            </div>
                            
                            <div id="completionDetailsPanel" class="panel-collapse collapse">
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="control-label col-md-2" for="vRefNum">Vendor Reference Number</label>
                                        <div class="col-md-4">
                                            <input type="text" class="form-control" maxlength="150" id="vRefNum" name='vRefNum'>
                                        </div>
                                    </div>
                            
                                   	<div class="form-group">
                                   		<div class=" form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="woCompletedBy">Work Order Completed By</label>
	                                        <div class="col-md-8">
	                                            <input type="text" class="form-control" maxlength="150" id="woCompletedBy" name="woCompletedBy">
	                                        </div>
                                        </div>
                                        
                                   		<div id="completionWrapper" class="form-group removeBorder col-md-6" style="padding: 0px">
	                                        <label class="control-label col-md-4" for="completion">PM Completion Date Time</label>
	                                        <div class="col-md-8">
	                                             <div class='input-group date dateTimePicker removeButton'>
	                                                <input type='text' class="form-control col-md-8" name="completion" id='completion'/>
	                                               <span class="input-group-addon">
	                                                   <span class="glyphicon glyphicon-time"></span>
	                                               </span>
	                                           	</div>
	                                        </div>
                                        </div>
                                     </div>
                                    
                                    <div class="form-group">
                                        <label class="control-label col-md-2" for="addMaterialCost">Additional Material Cost</label>
                                        <div class="col-md-4 form-group removeBorder">
                                            <input type="text" class="form-control" id="addMaterialCost" maxlength="150" name='addMaterialCost'  pattern="^[0-9]+(\.[0-9][0-9]?)?" data-error="Numerical values to 2 d.p. only">
                                        	<div class="help-block with-errors"></div>
                                        </div>
                                        
                                        <label class="control-label col-md-2" for="addLaborCost">Additional Labor Cost</label>
                                        <div class="col-md-4 form-group removeBorder">
                                           <input type="text" class="form-control" id="addLaborCost" maxlength="150" name='addLaborCost'  pattern="^[0-9]+(\.[0-9][0-9]?)?" data-error="Numerical values to 2 d.p. only">
                                        	<div class="help-block with-errors"></div>
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class='control-label col-md-2' for="addMaterialDesc">Additional Material Description</label>
                                        <div class="col-md-10 form-group removeBorder">
                                          <textarea class="form-control vresize" id="addMaterialDesc" maxlength="150" name="addMaterialDesc" rows="2"></textarea>	   
                                        </div>
                                    </div>
                                    
                                </div>
                            </div>
                        </div>
                    </form:form>
                    
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 id="attachmentsTitle" class="panel-title">
                                <a data-toggle="collapse" class="accordion-toggle collapsed" data-parent="#accordion" href="#attachmentPanel">Attachments</a>
                            </h4>
                        </div>
                        <div id="attachmentPanel" class="panel-collapse collapse">
                            <div class="panel-body">
                                <div id='frameWrapper' style="display: none;">
                                    <div class='alert alert-success alert-dismissable' id="attachmentAlert">
                                    	<a href='#' class='close' data-hide='alert' aria-label='close'></a>
                                    	<strong><span class='glyphicon glyphicon-ok-sign'></span> Success!</strong> <spring:message code="HA_INFO_GENERAL_ATTACHMENTUPLOADSUCCESS" />
                                    </div>
                                    <iframe id="uploadTrg" name="uploadTrg" height="70" frameborder="0" style="display: none;"></iframe>
                                </div>
                                
                                <div id="addedAttachmentsWrapper" style="display: none; border: none !important; margin-bottom: 10px" class="container-fluid">
                                	<div class="row">
                                		<div class="col-xs-3 attachmentInfo"><label class='control-label'>Title</label></div>
                                		<div class="col-xs-4 attachmentInfo"><label class='control-label'>Description</label></div>
                                		<div class="col-xs-3 attachmentInfo"><label class='control-label'>File Name</label></div>
                                		<div class="col-xs-2 attachmentInfo" style="padding-top: 7px"></div>
                                	</div>
                                </div>


                                <button type='button' id="addAttachmentBTN" class="btn btn-default"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add Attachment(s)</button>
                                
                                <div id="attachmentFields" style="display: none">
                                    <form action="UploadAttachment" method="post" enctype="multipart/form-data" target="uploadTrg" id="attachmentForm"  role="form" name="attachmentForm">                                 
                                         <div class="form-group attachmentBorders">
                                            <label class="control-label col-md-2 required attachmentLabels" for="attachmentTitle">Title</label>
                                            <div class="col-md-10">
                                                <input type="text" class="form-control attachmentInput" id="attachmentTitle" name="title" maxlength="80">
                                            </div>
                                        </div>

                                        <div class="form-group attachmentBorders">
                                          <label class="control-label col-md-2" for="attachmentDescription">Description</label>
                                          <div class="col-md-10">
                                            <textarea class="form-control vresize attachmentInput" id="attachmentDescription" rows="2" name="desc" maxlength="255"></textarea>
                                          </div>
                                        </div>

                                        <div class="form-group attachmentBorders">
                                            <label class='control-label col-md-2 required attachmentLabels' for="attachmentUpload">File</label>
                                            <div class="col-md-10">
                                                <input value='null' type="file" name="file" size="50" id="attachmentUpload" aria-describedby="fileHelp" class="attachmentInput">
                                                <small id="fileHelp" class="form-text text-muted"><spring:message code="HA_INFO_GENERAL_SELECTFILEUPLOAD" /></small>
                                            </div>
                                        </div>
                                    
                                        <div class="col-md-12">
                                            <button class="pull-right btn btn-default" id="cancelAttachmentBTN" style="margin-left:3px" >Cancel</button>
											<input class="pull-right btn btn-primary" type="button" onclick="document.forms[3].submit(); disableUploadBTN(); setTimeout(resetFileInput, 500);" value="Upload" name="uploadBTN" id="uploadBTN" form='attachmentForm'>                                             <!--  <input class="pull-right btn btn-primary" type="submit" value="Upload" name="uploadBTN" id="uploadBTN" form='attachmentForm' onclick="setTimeout(resetFileInput, 500);">    -->     
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="pull-right" style="margin-bottom: 15px;" id="saveBTNs">
                    	<span id="woCancel" class="pull-right btn btn-default" style="margin-left:3px">Cancel</span>       
                        <input class="pull-right btn btn-primary" type="submit" value="Create Work Order" name="woSave" id="woSave" form='woForm'> 
                        <button style="display:none; margin-right: 3px" type='button' id="resendBTN" class="btn btn-primary pull-right">Resend and Save</button>
                    </div>
                </div>
<%--             </form> --%>
        </div>
        
		<!--  Hidden form for view work request -->
		<form id="viewWrForm" action="initCreateWorkRequest"  method="post">
		  	<input type="hidden" name="m" value="v">
		  	<input type="hidden" name="wrNumber"  id="viewWrNumber">
		</form>						
					   
        <!-- Hidden form for print -->
		<form id="printForm" action="GenReport" method="post" target="_blank">
		  <input type="hidden" name="itemNum" id="printWoNumber">
		  <input type="hidden" name="reportType" id="printReportType">
		</form> 
		
		<!-- Hidden form for downloadAttachment -->
		<form id="downloadForm" action="DownloadAttachment" method="post" target="_blank">
		  <input type="hidden" name="docId" id="docId">
		</form> 

        <!--  Hidden form for view work order -->
        <form id="passParamtersForm" action="initCreateWorkOrder" method="post">
          <input type="hidden" name="m" id="modeParameter">
          <input type="hidden" name="woNumber"  id="woNumberParameter">
          <input type="hidden" name="P"  id="previousParameter">
        </form>
		 <!-- Modal for contact method LOV -->
        <div class="modal" id="contactMethodModal" role="dialog">
	        <div class="modal-dialog  modal-md">
	            <div class="modal-content">
	              <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">Contact Method Information</h4>
	              </div>
	                <div class="modal-body">
	                    <div class="container-fluid">
	                        <table class="table-condensed" id="contactMethodTable" data-height="400" data-toggle="table" data-search="true">
	                         <thead>
	                         <tr>
								<th data-field='auto_send' data-visible="true">Auto Send</th>
	                            <th data-field='contact_person' data-visible="true">Contact Person</th>
	                            <th data-field='contact_phone' data-visible="true">Phone</th>
	                            <th data-field='contact_fax' data-visible="true">Fax</th>
	                            <th data-field='contact_email' data-visible="true">Email</th>
	                         </tr>   
	                         </thead>
	                         <tbody>
	                         	<tr>
	                         		<td></td>
	                         		<td></td>
	                         		<td></td>
	                         		<td></td>
	                         		<td></td>
	                         	</tr>
	                         </tbody>
	                        </table>
	                    </div>
	                </div>
	            </div>
	        </div>
    	</div>
    </body>
</html>
