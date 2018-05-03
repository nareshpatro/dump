<%-- 
    Document   : createWR
    Created on : Jun 14, 2017, 12:06:04 PM
    Author     : Carmen
    Last Update: Jul 4, 2017, 10:19 AM
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
        <title>Create Work Requests</title>
        
        <script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/createRequests.js?v=${initParam.buildTimeStamp}" />"></script>
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
    </head>
    
    <%  
    	String wrNumber = ((request.getParameter("wrNumber") == null) ? "" : request.getParameter("wrNumber"));
    	String m = ((request.getParameter("m") == null) ? "" : request.getParameter("m"));
    	String P = ((request.getParameter("P") == null) ? "" : request.getParameter("P"));
    %>
     
    
    <script>
    function restoreDefaultForm(){
    	$.xhrPool.abortAll();
    	$('#hiddenUser').val('<% out.print((request.getSession().getAttribute("ebsFullName") == null) ? "" : request.getSession().getAttribute("ebsFullName")); %>');
    	$('#requestedFor').val('<% out.print((request.getSession().getAttribute("ebsFullName") == null) ? "" : request.getSession().getAttribute("ebsFullName")); %>');
    	$('#contactPerson').val('<% out.print((request.getSession().getAttribute("ebsFullName") == null) ? "" : request.getSession().getAttribute("ebsFullName")); %>');
    	$('#contactPhone').val('<% out.print((request.getSession().getAttribute("ebsHAContactPhone") == null) ? "" : request.getSession().getAttribute("ebsHAContactPhone")); %>');
    	$('#contactEmail').val('<% out.print((request.getSession().getAttribute("ebsEmail") == null) ? "" : request.getSession().getAttribute("ebsEmail")); %>');
    	$('.alert').hide();
    }
    
    function getParams(){
    	var parameters = ["<% out.print(wrNumber); %>", "<% out.print(m); %>", "<% out.print(P); %>"];
    	return parameters;
    };
    </script>
    
    <body>
        <jsp:include page="sidebarHeader.jsp" />

        <div id="content">
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
            
            
            <div id="createWRToolbar" class="container-fluid col-md-12 btn-toolbar" style="margin-bottom: 10px;">
                <button type='button' id="backBTN" class="btn btn-default"><span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Return to Search</button>
				<% if ("Y".equals(session.getAttribute("respOrderCreate"))) { %>
                	<button type='button' id="createWOBTN" class="btn btn-default pull-right" style="display:none;"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Create Work Order</button>
                <% } %>
                <% if ("Y".equals(session.getAttribute("respOrderView"))) { %>
                	<button type='button' id="goToWOBTN" class="btn btn-default pull-right" style="display:none;"><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span> Go To Work Order</button>
                <% } %>
                <button type='button' id="printBTN" class="btn btn-default pull-right"><span class="glyphicon glyphicon-print" aria-hidden="true"></span> Print</button>
                <% if ("Y".equals(session.getAttribute("respRequestUpdate"))) { %>
                	<a id="editBTN" class="btn btn-default pull-right"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Update</a>
            	<% } %>
            </div>
            
            <form:form class="form-horizontal" id="wrForm" role="form" action="processWorkRequest" method="POST" modelAttribute="workRequest" data-toggle="validator" data-disable="false" name="wrForm">                
				<form:hidden path="workRequestId" id="workRequestId" />
				<form:hidden path="mode" id="modeInput"/>
				<!-- Information for attachments - Attachment mode, new document ids for new attachments, del document ids for deleted attachments -->
				<input type="hidden" name="attachment" id="attachmentMode" />
				<input type="hidden" name="documentId" id="newDocumentIds"/>
				<input type="hidden" name="documentId" id="delDocumentIds"/>
				<form:hidden path="lastUpdateDate" id="lastUpdateDate" />
				
                <div class="container-fluid col-md-12">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 id="requestDetailsTitle" class="panel-title">
                                <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#requestDetailsPanel">Request Details</a>
                            </h4>
                        </div>
                        
                        <div id="requestDetailsPanel" class="panel-collapse in">
                            <div class="panel-body">
                                <div class="form-group" id="wrNumberStatusWrapper">
                                    <label class="control-label col-md-2" for="wrNumber">Work Request Number</label>
                                    <div class="col-md-4">
                                        <form:input path="wrNumber" class="form-control" id="wrNumber" />
                                    </div>
                                    
                                    <div class="form-group removeBorder">
	                                    <label class="control-label col-md-2 required" for="wrStatus">Work Request Status</label>
	                                    <div class="col-md-4">
	                                    <input type="text" class="form-control selectReadOnly" id="wrStatusReadOnly"  style="display: none"> <!--Added 30/08/17-->
										<div class="hideDropdowns">
											<form:select path="wrStatus" class="form-control selectpicker" id="wrStatus">
		                                        <form:options items="${workRequestStatusLimitedList}" itemValue="name" itemLabel="desc"/>
		                                    </form:select>
										</div>
									</div>

                                    </div>
                                </div>
                                    
                                <div class="form-group" id="woNumberStatusWrapper" style="display:none;">
                                    <label class="control-label col-md-2 required" for="woNumber">Work Order Number</label>
                                    <div class="col-md-4">
                                        <form:input path="woNumber" class="form-control" id="woNumber" />
                                    </div>
                                    
                                    <label class="control-label col-md-2 required" for="woStatus">Work Order Status</label>
                                    <div class="col-md-4">
                                    	<input type="text" class="form-control" id="woStatusReadOnly"  style="display: none">	
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <div id="assetNumberGroup">
                                        <label class="control-label col-md-2 required" for="assetNumber">Asset Number</label>
                                        <div class=" col-md-4">
                                        <div class="input-group">
                                        	<form:input path="assetNumber" class="form-control" id="assetNumber" required="required" maxlength="30"/>
                                            <span class="input-group-btn">
                                            	<button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetNumber" id="assetNumberFiltered" style="border-top-right-radius: 4px; border-bottom-right-radius: 4px"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                                <button disabled id="assetInfoBTN" type="button" class="btn btn-link btn-sm" data-toggle="modal" data-target="#assetDetailsModal" data-toggle="tooltip" data-placement="top" title="Click to view Asset detail" data-loading-text="Retrieving.."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></button>
                                            </span>
                                        </div>
                                            <span class="help-inline text-danger" style="display: none" id="assetNumberHelp"><spring:message code="HA_WARNING_GENERAL_INVALIDASSETNUMBER" /></span>
                                        </div>
                                    </div>
                                    
                                    <div class="assetInfoWrapper" style="display:none">
                                    	<div id="wrOrgWrapper" style="display: none">
			                                <label class="control-label col-md-2" for="wrOrg">EAM Org</label>
			                                <div class="col-md-4">
			                                    <input type="text" class="form-control" id="wrOrg" readonly></input>
			                                </div> 
			                            </div>
			                            
		                                <div id="assetOrgWrapper">
			                                <label class="control-label col-md-2" for="assetOrg">Asset Organization</label>
			                                <div class="col-md-4">
			                                    <input type="text" class="form-control" id="assetOrg" readonly></input>
			                                </div> 
		                                </div>
                                    </div>
                                </div>
                                
                                <div class="form-group assetInfoWrapper" style="display:none">
                                    <label class="control-label col-md-2" for="maintenanceBody">Maintenance Body</label>
                                    <div class="col-md-4">
                                        <p class="control-label" id="maintenanceBody" style="padding-left: 8px"></p>
                                    </div> 

                                    <label class="control-label col-md-2" for="assetDescription">Asset Description</label>
                                    <div class="col-md-4">
                                        <p class="control-label" id="assetDescription" style="padding-left: 8px"></p>
                                    </div> 
                                </div>
                                
                                <div class="form-group">
	                                  <label class="control-label col-md-2 required" for="requestType">Work Request Type</label>
		                                  <div class="col-md-4">
		                                  		<!-- Added 20/10/17 to disable edit wr type start -->
			                                    <input type="text" class="form-control selectReadOnly" id="requestTypeReadOnly"  style="display: none" readonly>
			                                    <div class="hideDropdowns"  id="requestTypeWrapper">
			                                    <!-- Added 20/10/17 to disable edit wr type end -->
				                                  	<form:select path="requestType" class="form-control selectpicker" id="requestType">
				                                        <form:options items="${workRequestTypeList}" itemValue="name" itemLabel="desc"/>
				                                    </form:select>
				                                </div>
                                  		 </div>
										  
		                                 <div id="breakdownDate" class="form-group">
		                                 <label  class="control-label col-md-2 required" for="breakdownDate">CM Breakdown Date Time</label>
			                             	<div class=" col-md-4">    
			                                    	<div class='input-group date noFutureDate'>
			                                          <form:input path="breakdownDateInput" class="form-control" id="breakdownDateInput" required="required"/>
			                                          <span class="input-group-addon">
			                                              <span class="glyphicon glyphicon-time"></span>
			                                          </span>
			                                        </div>
			                                        <small class="form-text text-muted"><spring:message code="HA_WARNING_GENERAL_PASTDATESONLY" /></small>
			                                 </div>
			                             </div>
									
		                                <div id="scheduleDate" style="display: none" class="form-group">
		                                <label  class="control-label col-md-2 required" for="scheduleDate">PM Scheduled Date Time</label>
		                                    <div class=" col-md-4">
			                                      <div class='input-group date noPastDate'>
			                                          <form:input path="scheduleDateInput" class="form-control" id="scheduleDateInput" />
			                                          <span class="input-group-addon">
			                                              <span class="glyphicon glyphicon-time"></span>
			                                          </span>
			                                      </div>
			                                      <small class="form-text text-muted"><spring:message code="HA_WARNING_GENERAL_FUTUREDATESONLY" /></small>
		                                    </div>
		                                </div>
	                            </div>
                                
                                <div class="form-group">
                                    <label class="control-label col-md-2" for="equipmentSent">Equipment Sent To Workshop</label>
                                    <div class=" col-md-4 selectReadOnly" style="display: none">
                                        <input type="text" class="form-control" id="equipmentSentReadOnly">
                                    </div>
                                    <fieldset class="col-md-4" id="equipmentSent">
                                    <label class="radio-inline"><form:radiobutton path="equipmentSent" value="YES" class="radio" />Yes</label>
                                    <label class="radio-inline"><form:radiobutton path="equipmentSent" value="NO" class="radio" />No</label>
                                    </fieldset>
                                    
                                    <label class='control-label col-md-2 required' for="disinfection">Disinfection Status</label>
                                    <div class="col-md-4">
                                    <input type="text" class="form-control selectReadOnly" id="disinfectionReadOnly" style="display: none">
                                    <div class="hideDropdowns">
	                                    <form:select path="disinfection" class="form-control selectpicker" id="disinfection" required="required">
	                                        <form:options items="${disinfectionList}" itemValue="name" itemLabel="desc"/>
	                                    </form:select>
									</div>
                                    </div>
                                </div>
                                
                                <div class="form-group" id="descriptionHistoryWrapper" style="display:none">
                                  <label class="control-label col-md-2" for="descriptionHistory">Work Request Description History</label>
                                  <div class="col-md-10">
                                      <textarea class="form-control vresize" id="descriptionHistory" rows="5" name="descriptionHistory" readonly></textarea>
                                  </div>
                                </div> 
                                
                                <div class="form-group" id="descriptionWrapper">
                                  <label class="control-label col-md-2 required" for="description" id="descriptionLabel">Work Request Description</label>
                                  <div class="col-md-10">
                                  	<form:textarea path="description" class="form-control vresize" id="description" rows="5" required="required" maxlength="2000"/>
                                   
                                  </div>
                                </div>  
                                      
                                <div class="form-group">
                                    <label class="control-label col-md-2 required" for="requestedFor">Requested For</label>
                                    <div class=" col-md-4">
                                    <div class="input-group">
                                        <input class="form-control" id="requestedFor" value="${sessionScope.ebsFullName}" required="required"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="requestedFor" id="requestedForFiltered"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                        <form:input path="requestedFor" type="hidden" class="form-control col-md-2" id="hiddenUser" value="${sessionScope.ebsUserName}"/>
                                    </div>
                                    </div>
                                </div>
                                      
                                <div class="form-group" id="reasonRemark" style="display: none">
                                  <label class="control-label col-md-2 required" for="remark">Work Request Cancel/Reject Remarks</label>
                                  <div class="col-md-8">
                                    <form:textarea path="remark" class="form-control vresize" id="remark" rows="3" maxlength="1000"/>
                                    <small class="form-text text-muted" style="color:red" id="reasonsHint"><spring:message code="HA_WARNING_WR_REASONFORCANCELORREJECT" /></small>
                                  </div>
                                </div>  
                            </div>
                        </div>
                    </div>
                
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 id="contactDetailsTitle" class="panel-title">
                                <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#contactDetailsPanel">Contact Details</a>
                            </h4>
                        </div>
                        
                        <div id="contactDetailsPanel" class="panel-collapse in">
                            <div class="panel-body">
                                <div class="form-group">
                                    <label class="control-label col-md-2 required" for="contactPerson">HA Contact Person</label>
                                    <div class="col-md-4">
                                        <form:input path="contactPerson" class="form-control hideDropdowns" id="contactPerson" maxlength="50" required="required" value="${sessionScope.ebsFullName}"/>
                                    	<p class="control-label selectReadOnly" id="contactPersonReadOnly" style="padding: 3px 0px 3px 8px; min-height: 26px; display: none; word-wrap: break-word;"></p>
                                    </div>
                                </div>
                                
                                <div class="form-group">    
                                    <label class="control-label col-md-2 required" for="contactPhone">HA Contact Phone</label>
                                    <div class="col-md-4">                                    
                                        <form:input type="text" path="contactPhone" class="form-control" maxlength="50" id="contactPhone" required="required" data-error="Please enter a valid phone number."  pattern="^\(?([0-9]{0,3})\)?([ ]?)([0-9]{4})([ ]?)([0-9]{4})$" value="${sessionScope.ebsHAContactPhone}"/>
                                    	<div class="help-block with-errors"></div>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label class='control-label col-md-2' for="contactEmail">HA Contact Email</label>
                                    <div class="col-md-4">
                                        <form:input type="text" path="contactEmail" class="form-control hideDropdowns" maxlength="240" id="contactEmail" data-error="Please enter a valid email" pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,3}$" value="${sessionScope.ebsEmail}"/>
                                    	<p class="control-label selectReadOnly" id="contactEmailReadOnly" style="padding: 3px 0px 3px 8px; min-height: 26px; display: none; word-wrap: break-word;"></p>
                                    	<div class="help-block with-errors"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    </form:form>
                    
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 id="attachmentsTitle" class="panel-title">
                                <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#attachmentPanel">Attachments</a>
                            </h4>
                        </div>
                        
                        <div id="attachmentPanel" class="panel-collapse in">
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
                                        <div class="form-group">
                                            <label class="control-label col-md-2 required" for="attachmentTitle">Title</label>
                                            <div class="col-md-10">
                                                <input type="text" class="form-control" id="attachmentTitle" name="title" maxlength="80">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                          <label class="control-label col-md-2" for="attachmentDescription">Description</label>
                                          <div class="col-md-10">
                                            <textarea class="form-control vresize" id="attachmentDescription" rows="2" name="desc" maxlength="255"></textarea>
                                          </div>
                                        </div>

                                        <div class="form-group">
                                            <label class='control-label col-md-2 required' for="attachmentUpload">File</label>
                                            <div class="col-md-10">
                                                <input value='null' type="file" name="file" size="50" id="attachmentUpload" aria-describedby="fileHelp">
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
                    	<span id="wrCancel" class="pull-right btn btn-default" style="margin-left:3px">Cancel</span>       
                        <input class="pull-right btn btn-primary" type="submit" value="Create Work Request" name="wrSave" id="wrSave" form='wrForm'>   
                    </div>
<%--                 </form> --%>

            </div>
        
        <!--  Hidden form for create work order -->
		<form id="createWoForm" action="initCreateWorkOrder"  method="post">
		  <input type="hidden" name="wrNumber"  id="inWrNumber">
		  <input type="hidden" name="contactPerson"  id="inContactPerson">
		  <input type="hidden" name="contactPhone"  id="inContactPhone">
		  <input type="hidden" name="contactEmail"  id="inContactEmail">
		  <input type="hidden" name="breakdownDateInput"  id="inBreakdownDateInput">
		  <input type="hidden" name="scheduleDateInput"  id="inScheduleDateInput">
		  <input type="hidden" name="disinfection"  id="inDisinfection">		  
		  <input type="hidden" name="equipmentSent"  id="inEquipmentSent">		  
		</form>	
            
         <!--  Hidden form for view work order -->
		<form id="viewWoForm" action="initCreateWorkOrder"  method="post">
		  	<input type="hidden" name="m" value="v">
		  	<input type="hidden" name="woNumber"  id="viewWoNumber">
		</form>	
    
        <!-- Hidden form for downloadAttachment -->
		<form id="downloadForm" action="DownloadAttachment" method="post" target="_blank">
		  <input type="hidden" name="docId" id="docId">
		</form> 
    
        <!-- Hidden form for print -->
		<form id="printForm" action="GenReport" method="post" target="_blank">
		  <input type="hidden" name="itemNum" id="printWrNumber">
		  <input type="hidden" name="reportType" id="printReportType">
		</form>   
            
		<!--  Hidden form for view work request -->
		<form id="passParamtersForm" action="initCreateWorkRequest" method="post">
		  <input type="hidden" name="m" id="modeParameter">
		  <input type="hidden" name="wrNumber" id="wrNumberParameter">
		  <input type="hidden" name="P"  id="previousParameter">
		</form>
    </body>
</html>
