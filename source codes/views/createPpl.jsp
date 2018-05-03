<%-- 
    Document   : searchWR
    Created on : Jun 14, 2017, 11:04:00 AM
    Author     : Carmen
    Last update: Jun 21, 2017, 12:01 PM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@page import="hk.org.ha.eam.util.Lov,hk.org.ha.eam.model.WorkRequest"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>create People~~**##*****</title>

        <script src="<spring:url value="/resources/js/moment.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>        
        <script src="<spring:url value="/resources/js/datatables/datatables.min.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/datatables/DataTables-1.10.15/js/dataTables.bootstrap.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/datatables/colResize.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/searchRequests.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/datatables/tableProcessing.js?v=${initParam.buildTimeStamp}" />"></script>
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/js/datatables/Select-1.2.2/css/select.bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link href="<spring:url value="/resources/js/datatables/DataTables-1.10.15/css/dataTables.bootstrap.css?v=${initParam.buildTimeStamp}" />" rel="stylesheet" type="text/css"/>
        
        <script> 
        var userId = '${sessionScope.ebsUserId}';
        var respId = '${sessionScope.ebsRespId}';
        var appRespId = '${sessionScope.ebsRespAppId}';
        var loaded;
        var ColVis = [];
        
        var tableMax = '${sessionScope.ebsQueryLimit}';
        var exportMax = '${sessionScope.ebsExportLimit}';
        var corpDateRangeMax = '${sessionScope.ebsCorpDateRangeLimit}';
        
        $(document).ready(function(){
        	loaded = 0;
        	loadSettings(userId, respId, appRespId);
        	
        	//Parameter checks and functions for when the user enters the page via the dashboard
        	<% if(!request.getParameterMap().isEmpty()){ %>      
				if('${param.wrStatus}' == "total"){
        			$('#wrStatus').selectpicker('selectAll');
        		}
        		else{
        			$('#wrStatus').selectpicker('val', ['${param.wrStatus}']);
        		}
				
	    		$('#wrType').selectpicker('val', ['${param.statusType}']);
	        	$('#eamOrg').selectpicker('val', ['${param.eamOrg}']);
	        	$('#assetRisk').selectpicker('val', ['${param.riskLevel}']);
		        
		        if('${param.maintenanceVendor}' !== ""){
	            	$('#maintenanceVendor').val('${param.maintenanceVendor}');
	            }
		        
		        if('${param.criticalMedEquip}' == "true"){
	                $('#criticalOnly').prop('checked', true);
	           	}
		        
		        if('${param.wrWo}' !== ""){
	            	$('#hiddenUser').val('${sessionScope.ebsUserName}');
	            	$('#requestedFor').val('${sessionScope.ebsFullName}');
	            }
		        
		        <%if (request.getParameter("creationFrom")!=null && request.getParameter("creationTo")!=null) { %>
		        	$('#dateType').selectpicker('val', ['C']);
	            <% } %>
	            
	            
	            
	            /***********Code for redirect from OAF*************/ 
	            if(GetURLParameter("fromOAF")==="Yes"){
	            	document.getElementById("assetNumber").value=GetURLParameter("assetNumber");
	            	$('#dateType').selectpicker('val', []);
	            	document.getElementById("dateFrom").value=null;
	            	document.getElementById("dateTo").value=null;
	            	window.history.replaceState({}, document.title, "/EAM/searchWorkRequest#no-back-button");
		            $('#searchCriteria').collapse("show");
	            }
	            /***********Code for redirect from OAF*************/ 
	         
	          	//Check if sent from dashboard with max records
	        	if(getCookie("overMax") !== null && Number(getCookie("overMax")) > exportMax){
	        		var max = getCookie("overMax");
	        		alertMessage("searchAlert", "error", "Total " + max + " records returned and hit the limit to display and export. Please refine your searching criteria.");
	        		delCookie("overMax");
	        	}
	        	else{
	        		$('#searchCriteria').collapse("hide");
		            $('#searchAlert').hide();
		        	$('#wrSearchBtn').button('loading');
		            $('#wrSearchBtn').attr("disabled", true);
	        		$('#wrSearchResults').DataTable().processing( true );
					searchWorkRequests();
					delCookie("overMax");
	        	}
            <% } %>  
        }
        </script>
        
     
    </head>
    <body>
        <jsp:include page="sidebarHeader.jsp" />
        <div id="content">
            <div class="container-fluid">
                <!-- Alert for no search parameters entered or no values found for maintenance body/users -->
                <div class="alert alert-danger lovSearchAlert" role="alert" id="searchAlert" style="display: none;"></div>
                
                <div class="panel panel-primary"> 
                <div class="panel-body" style="padding: 8px 0px;">   
                	<!-- effective date -->                 
                      <div class="container-fluid">
                      	<div class="row">
	                      <div class="form-group col-md-4">
	                        <label class="control-label col-md-6" for="effDate">Effective Date:</label>
	                             <div class='input-group date dateFrom dateFromLimited'>
				                    <input type='text' class="form-control col-md-6" name="effDate" id="creationFrom"/>
				                    <span class="input-group-addon">
				                      <span class="glyphicon glyphicon-calendar"></span>
				                    </span>
				                 </div>
				                   <small class="form-text text-muted CMDateHint"></small>
	                      </div>
	                      </div>
	                      <!-- second row -->
	                      <div class="row" style="border-width:thin;border-style:solid;border-color:red;padding-top:5px;">
	                      <div class="form-group col-md-4 form-inline">
	                      	<label class="control-label col-md-4" for="title">Title:</label>	                      	
							<select id="title" name="title" class="form-control selectpicker col-md-6">
   								<option value="NONE">--- Select ---</option> 
							</select>
	                      </div>
	                      	<div class="form-group col-md-4 form-inline">
	                      	<label class="control-label col-md-3" for="title">Gender:</label>	                      	
							<label class="radio-inline">
							<input type="radio" name="gender" style="margin-top:3px;">M
							</label>
							<label class="radio-inline">
							<input type="radio" name="gender" style="margin-top:3px;">F
							</label>
	                      </div>
	                      <div class="form-group col-md-4 form-inline">
	                      	<label class="control-label col-md-8" for="empNum">Employee Number:</label>
	                      	<input type='text' class="form-control" name="empNum" id="empNum" size="6"/>
	                      </div>
	                      </div>
	                      
	                      <!-- third row -->
						  <div class="row" style="margin-top:7px;">
						  <div class="form-group col-md-6 form-inline">
						  <label class="control-label col-md-4" for="hkidName">HKID Name:</label>
						  <input type='text' class="form-control col-md-8" name="hkidName" id="hkidName"/>
						  </div>
						  <div class="form-group col-md-6 form-inline">
						  <label class="control-label col-md-4" for="hkidName">Chinese Name:</label>
						  <input type='text' class="form-control" name="chnName" id="chnName"/>
						  </div>
						  </div>
						  
						  <!-- fourth row -->
						  <div class="row">
						  <div class="form-group col-md-6 form-inline">
						  <label class="control-label col-md-4" for="lastName">Last Name:</label>
						  <input type='text' class="form-control" name="lastName" id="lastName"/>
						  </div>
						  <div class="form-group col-md-6 form-inline">
						  <label class="control-label col-md-4" for="firstName">First Name:</label>
						  <input type='text' class="form-control" name="firstName" id="firstName"/>
						  </div>
						  </div>

	                      </div>
	                      
                      </div>

                 </div>
                 </div> 
                 <!-- create people tabs -->
                 <div class="container-fluid col-md-12">
                 <div>
	                 <ul class="nav nav-tabs">
            			<li class="active" id="personalTab"><a href="#personalContent" data-toggle="tab">Personal Details</a></li>
            			<li id="contInfoTab"><a href="#contInfoContent" data-toggle="tab">Contact Info</a></li>
            			<li id="othrPersnInfoTab"><a href="#othrPersnInfoContent" data-toggle="tab">Other Personal Info</a></li>
            			<li id="contactsTab"><a href="#contactsContent" data-toggle="tab">Contacts</a></li>
            			<li id="assignmentsTab"><a href="#assignmentsContent" data-toggle="tab">Assignments</a></li>
            			<li id="salaryInfoTab"><a href="#salaryInfoContent" data-toggle="tab">Salary Info</a></li>
            			<li id="payMthdTab"><a href="#payMthdContent" data-toggle="tab">Pay Method & MPF Information</a></li>
            			<li id="othrAssignmtInfoTab"><a href="#othrAssignmtInfoContent" data-toggle="tab">Other Assignment Info</a></li>
	            	 </ul>
	            	
	            	<!-- Start of Personal tab -->
	            	<div class="tab-content" >
            			<div class="tab-pane active" id="personalContent">
            				<div class="row">
            					<div class="col-md-4">
            						<h1>Personal Details</h1>
            					</div>
            				</div>
            			</div>
            			
            			<!-- Start of Contact Info tab -->
            			<div class="tab-pane" id="contInfoContent">
							<div class="row">
								<div class="col-md-12">
									<jsp:include page="contactInfo.jsp"></jsp:include>
								</div>
            				</div>
            			</div>
            			
            			<!-- Start of Other Personal Info tab -->
            			<div class="tab-pane" id="othrPersnInfoContent">
            				<div class="row">
            					<div class="col-md-4">
            						<h1>Other Personal Info</h1>
            					</div>
            				</div>
            			</div>
            			
            			<!-- Start of Contacts tab -->
            			<div class="tab-pane" id="contactsContent">
            				<div class="row">
            					<div class="col-md-4">
            						<h1>Contacts</h1>
            					</div>
            				</div>
            			</div>
            			
            			<!-- Start of Assignment tab -->
            			<div class="tab-pane" id="assignmentsContent">
            				<div class="row">
            					<div class="col-md-12"> <!--
            						<jsp:include page="assignmt.jsp"></jsp:include>
									-->
            					</div>
            				</div>
            			</div>
            			
            			<!-- Start of Salary Info tab -->
            			<div class="tab-pane" id="salaryInfoContent">
            				<div class="row">
            					<div class="col-md-4">
            						<h1>Salary Info</h1>
            					</div>
            				</div>
            			</div>
            			
            			<!-- Start of Pay Method & MPF Information tab -->
            			<div class="tab-pane" id="payMthdContent">
            				<div class="row">
            					<div class="col-md-4">
            						<h1>Pay Method & MPF Information</h1>
            					</div>
            				</div>
            			</div>
            			
            			<!-- Start of Other Assignment Info tab -->
            			<div class="tab-pane" id="othrAssignmtInfoContent">
            				<div class="row">
            					<div class="col-md-4">
            						<h1>Other Assignment Info</h1>
            					</div>
            				</div>
            			</div>
            			
            		</div>
	            	
	            	
            	</div>     
                </div>
                  
        </div>


		<!-- Hidden form for export -->
		<form id="exportForm" action="ExportData" method="post" target="_blank">
		  <input type="hidden" name="wrNumber" id="exportWrNumber">
		  <input type="hidden" name="userName" id="exportUserName">
		  <input type="hidden" name="parameter" id="exportParameter">
		  <input type="hidden" name="exportType" id="exportExportType">
		</form>

		<!-- Hidden form for print -->
		<form id="printForm" action="GenReport" method="post" target="_blank">
		  <input type="hidden" name="itemNum" id="printWrNumber">
		  <input type="hidden" name="reportType" id="printReportType">
		</form>
		
		<!--  Hidden form for view work request -->
		<form id="viewWrForm" action="initCreateWorkRequest"  method="post">
		  <input type="hidden" name="m" value="v">
		  <input type="hidden" name="wrNumber"  id="viewWrNumber">
		</form>

		<!--  Hidden form for view work order -->
		<form id="viewWoForm" action="initCreateWorkOrder"  method="post">
		  	<input type="hidden" name="m" value="v">
		  	<input type="hidden" name="woNumber"  id="viewWoNumber">
		</form>																	
        <!-- Description read more info -->
        <div class="modal" id="showCell" role="dialog">
            <div class="modal-dialog  modal-sm">
                <div class="modal-content">
                  <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                  </div>
                  <div class="modal-body" id="showCellInfo">
                  </div>
                  <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                  </div>
                </div>
            </div>
        </div>
        
        
        
        <script>       
        function dateFormat(value, row, index) {
        		if(value == null){
        			return "-";
        		}else{
        	   		return moment(value).format('DD/MM/YYYY HH:mm');
        		}
        	}
        </script>
    </body>
</html>
