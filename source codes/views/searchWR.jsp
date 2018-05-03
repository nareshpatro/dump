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
        <title>Search Work Requests</title>

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
            
            
            $('#wrSearchResults').on( 'column-reorder.dt', function ( e, settings, details ) {
            	if(loaded == 3){
            		$.ajax({
                        type: 'POST',
                        data : {
                            "USER_ID" : userId,
                            "RESP_ID" : respId,
                            "APP_ID" : appRespId,
                            "PREF_NAME" : "ColOrder",
                            "PREF_VALUE" : JSON.stringify($('#wrSearchResults').DataTable().colReorder.order()),
                            "FUNCTION_CODE" : "WORK REQUEST",
                            "PAGE_CODE" : "SEARCH"
                        },    
                        url : './saveUserPrefExt',
                        dataType:'text',
                        success : function(result) {
                        	// When column reoder, force to save table sorting again
                        	saveTableOrder();
                        },
                        error : function(xhr, ajaxOptions, thrownError) {
                            if (xhr.status == 404) {
                            }
                        }
                    });
            	}
            });   
            
            $('#wrSearchResults').on( 'column-visibility.dt', function ( e, settings, column, state ) {
            	if(loaded == 3){
            		if(state){
            			var index = ColVis.indexOf(column);
                		if (index > -1) {
                			ColVis.splice(index, 1);
                		}
                	}
                	else {
                		ColVis.push(column);
                	}
                	
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
                        },
                        error : function(xhr, ajaxOptions, thrownError) {
                            if (xhr.status == 404) {
                            }
                        }
                    });
            	}
            });
            
            $('#wrSearchResults').on( 'order.dt', function ( e, settings ) {
            	if(loaded == 3){
            		saveTableOrder();
            	}
            });  
        });
		
		function restoreDefaultSettings(){
        	$.ajax({
                type: 'POST',
                data :{
                    "USER_ID" : userId,
                    "RESP_ID" : respId,
                    "APP_ID" : appRespId,
                    "PREF_NAME" : "COLORDER",
                    "FUNCTION_CODE" : "WORK REQUEST",
                    "PAGE_CODE" : "SEARCH"
                },   
                url : './delUserPrefExt',
                dataType:'text',
                success : function(result) {
                        $.ajax({
                            type: 'POST',
                            data : {
                                "USER_ID" : userId,
                                "RESP_ID" : respId,
                                "APP_ID" : appRespId,
                                "PREF_NAME" : "COLVIS",
                                "FUNCTION_CODE" : "WORK REQUEST",
                                "PAGE_CODE" : "SEARCH"
                            },    
                            url : './delUserPrefExt',
                            dataType:'text',
                            success : function(result) {
                                $.ajax({
                                    type: 'POST',
                                    data : {
                                        "USER_ID" : userId,
                                        "RESP_ID" : respId,
                                        "APP_ID" : appRespId,
                                        "PREF_NAME" : "TABLEORDER",
                                        "FUNCTION_CODE" : "WORK REQUEST",
                                        "PAGE_CODE" : "SEARCH"
                                    },    
                                    url : './delUserPrefExt',
                                    dataType:'text',
                                    success : function(result) {
                            			loaded = 0;
                            			var hasData = false;
                            			var table = $('#wrSearchResults').DataTable();
                            	
                            			if (table.data().any()) {
                            				hasData = true;
                            			}
                            	
                            			table.destroy();
                            			$('#wrSearchResults').empty(); 
                            			if(hasData){
                            				initialiseTable(true);
                            			}
                            			else{
                            				initialiseTable(false);
                            			}
                            			$("#wrSearchResults").addClass("table table-bordered table-responsive table-striped"); 
                            			$("#wrSearchResults").find("thead").addClass("thead-default");
                            			loadSettings(userId, respId, appRespId);
                                    },
                                    error : function(xhr, ajaxOptions, thrownError) {
                                    	if (xhr.statusText =='abort' || thrownError == "") {
                                            return;
                                        }
                                    }
                                });
                            },
                            error : function(xhr, ajaxOptions, thrownError) {
                            	if (xhr.statusText =='abort' || thrownError == "") {
                                    return;
                                }
                            }
                        });
                },
                error : function(xhr, ajaxOptions, thrownError) {
                    if (xhr.statusText =='abort' || thrownError == "") {
                        return;
                    }
                }
            });
        }
        </script>
        
     
    </head>
    <body>
        <jsp:include page="sidebarHeader.jsp" />
        <div id="content">
            <div class="container-fluid">
                <!-- Alert for no search parameters entered or no values found for maintenance body/users -->
                <div class="alert alert-danger lovSearchAlert" role="alert" id="searchAlert" style="display: none;"></div>
                <form:form id="wrSearchForm" action="processWorkRequest" method="POST" modelAttribute="workRequest">                    
                <!-- Search criteria panel -->
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#searchCriteria" id="criteriaTitle">Search Criteria</a>
                        </h4>
                    </div>
                    
                    <div id="searchCriteria" class="panel-collapse collapse in">
                        <div class="panel-body" style="padding: 8px 0px;">
                            <div class="container-fluid">

                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="wrNumber">Work Request Number</label>
                                    <form:input path="wrNumber" class="form-control col-md-3" id="wrNumber"/>
                                </div>
                                
                                <div class="form-group col-md-3">
                                    <label class="control-label" for="assetNumber">Asset Number</label>
                                    <div class="input-group">
	                                    <form:input path="assetNumber" class="form-control checkValidity" id="assetNumber"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetNumber" id="assetNumber"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                
                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="wrType">Work Request Type</label>
									<form:select path="wrType" class="form-control selectpicker"  >
										<form:option value="" label="-- Select --" />
                                        <form:options items="${workRequestTypeList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                </div>

                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="wrStatus">Work Request Status</label>        
									<form:select path="wrStatus" class="form-control selectpicker" multiple="true" id="wrStatus">
                                        <form:options items="${workRequestStatusList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                </div>
                            </div>

                            <div class="container-fluid">
                                 <div class="form-group col-md-3">
                                    <label  class="control-label" for="eamOrg">EAM Org</label>
                                    <% if(((List)request.getAttribute("eamOrgList")).size() == 1){ %>
                                    <form:select path="eamOrg" class="form-control selectpicker">
                                        <form:options items="${eamOrgList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                    <% } else { %>
                                    <form:select path="eamOrg" class="form-control selectpicker"  data-live-search="true">
                                        <form:option value="" label="-- Select --" />
                                        <form:options items="${eamOrgList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                    <% }  %>
                                    
                                </div>
                            
                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="dateType">Date Type</label>
									<form:select path="dateType" class="form-control selectpicker" >
										<form:option value="" label="-- Select --" />
                                        <form:options items="${dateTypeList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                </div>
                                
                                <div class="form-group col-md-3">        
                                    <label  class="control-label dateFromLabel" for="dateFrom">
                                        <% if(request.getParameterNames().hasMoreElements()){
                                            if (request.getParameter("creationFrom").isEmpty() && request.getParameter("creationTo").isEmpty()) { %>
                                            <small class="form-text text-muted selectDate"><spring:message code="HA_WARNING_GENERAL_SELECTDATETYPE" /></small>
                                        <% } else { %>
                                            Work Request Creation Date From
                                        <% }} else { %>
                                            <small class="form-text text-muted selectDate"><spring:message code="HA_WARNING_GENERAL_SELECTDATETYPE" /></small>
                                        <% } %>
                                    </label>
                                    <div class='input-group date dateFrom'>
                                        <input type='text' class="form-control" id="dateFrom" name="dateFrom" ${param.creationFrom == null ? 'disabled' : ''} value='${param.creationFrom}' />
                                        <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span>
                                    </div>
                                    <small id="fromDateHint" class="form-text text-muted"></small>
                                </div>
                                
                                <div class="form-group col-md-3">
                                    <label  class="control-label dateToLabel" for="dateTo">
                                        <% if(request.getParameterNames().hasMoreElements()){
                                            if (request.getParameter("creationFrom").isEmpty() && request.getParameter("creationTo").isEmpty()) { %>
                                            <small class="form-text text-muted selectDate"><spring:message code="HA_WARNING_GENERAL_SELECTDATETYPE" /></small>
                                        <% } else { %>
                                           Work Request Creation Date To
                                        <% }} else { %>
                                            <small class="form-text text-muted selectDate"><spring:message code="HA_WARNING_GENERAL_SELECTDATETYPE" /></small>
                                        <% } %>
                                    </label>
                                    <div class='input-group date dateTo'>
                                        <input type='text' class="form-control" id="dateTo" ${param.creationTo == null ? 'disabled' : ''} name="dateTo" value='${param.creationTo}'/>
                                        <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span>
                                    </div>
                                    <small id="toDateHint" class="form-text text-muted"></small>
                                </div>
                            </div>
                            
                            <div class="container-fluid">
                            	<div class="form-group col-md-3">
                                        <label class="control-label" for="owningDept">Owning Department</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control checkValidity" id="owningDept" name="owningDept" value=${param.owningDept}>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="owningDept" id="owningDept"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>                                
                                
                                <div class="form-group col-md-3">
                                        <label class="control-label" for="assetLocation">Location Code</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control checkValidity " id="assetLocation" name="assetLocation"  value=${param.assetLocation}>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetLocation" id="assetLocation"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                
                                <div class="form-group col-md-3">
                                        <label class="control-label" for="assetOwner">Asset Owner</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control checkValidity" id="assetOwner" name="assetOwner">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetOwner" id="assetOwner"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>   
                                
                                <div class="form-group col-md-3">
	                                <label  class="control-label" for="riskLevel">Risk Level</label>
	                                <form:select path="riskLevel" class="form-control selectpicker" id="assetRisk">
										<form:option value="" label="-- Select --" />
                                        <form:options items="${riskLevelList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
	                            </div>                              
                            </div>
                            
                            <div class="container-fluid">   
                            	<div class="form-group col-md-3">
                                        <label class="control-label" for="maintenanceVendor">Maintenance Body</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="maintenanceVendor">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="maintenanceVendor" id="maintenanceVendor"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                        <input type="hidden" class="form-control" id="hiddenMBody" name="maintenanceVendor" value=${param.mBody}>
                                    </div>
                                </div>                            	 
                                
                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="createdBy">Work Request Creation By</label>
                                	<div class="input-group">
                                    	<input type="text" class="form-control col-md-2" id="requestedFor">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="requestedFor" id="requestedFor"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                	    <input type="hidden" class="form-control col-md-2" id="hiddenUser" name="createdBy">
                                    </div>                               
                                </div>
                                    
                                <div class="form-group col-md-3 checkbox" style="position: relative">
                                    <label style="position: absolute; bottom: -35px;  font-weight: bold"><input type="checkbox" id="criticalOnly" name="criticalOnly"  ${param.critialMedEquip == "Critical Medical Equipment Only" ? 'checked' : ''}>Critical Medical Equipment Only</label>
                                </div> 
                                
                                <div class="col-md-3 pull-right" style="position: relative; height: 50px; margin-bottom: 10px;">
                                    <input type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 100px;' value="Search" id="wrSearchBtn" data-loading-text="Searching.." />
                                    <button type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 15px;' id="clearWrSearchBTN">Clear All</button>
                                </div>
                                
                            </div>
                            
                        </div>
                    </div>
                    
                </div>
                </form:form>
                <!-- Search results datatable -->
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">Search Results</h4>
                    </div>
                    <div class="panel-body" style="padding: 15px 20px 15px 20px" id="tableWrapper">
                        <table id="wrSearchResults" class="table table-bordered table-responsive table-striped">
                            <thead class="thead-default">
                                <tr>
                                    <th></th>
                                    <th>Work Request Number</th>
                                    <th>EAM Org</th>
                                    <th>Asset Number</th>
                                    <th>Asset Description</th>
                                    <th>Work Request Type</th>
                                    <th>Work Request Status</th>
                                    <th>Work Order Number</th>
                                    <th>Work Order Status</th>
                                    <th>CM Breakdown Date Time</th>
                                    <th>PM Scheduled Date Time</th>
                                    <th>Work Request Creation By</th>
                                    
                                    <th>Maintenance Body</th>
                                    <th>Location Code</th>
                                    <th>Asset Owner</th>
                                    <th>Owning Department</th>
                                    <th>Work Request Creation Date Time</th>
                                    <th>Asset Serial Number</th>
                                    <th>Manufacturer</th>
                                    <th>Brand</th>
                                    <th>Model</th>
                                    <th>Work Request Description</th>
                                </tr>
                            </thead>
                        </table>
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
