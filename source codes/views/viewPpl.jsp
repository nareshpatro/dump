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
        <title>view People~~**##*****</title>

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
                
                
                
                
                
                 <div class="panel panel-primary">
                     <div class="panel-heading">
                         <h4 class="panel-title">
                              <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#alertAndNotifications" id="repairLink">Alert & Notifications</a>
                         </h4>
                     </div>
                     
                     <div class="container">
	                     <div id="alertAndNotifications" class="panel-collapse collapse in">
	                     
	                     	<div style="background-color:#0000FF" class="col-md-10">Alerts about Employee Input</div>
	                     
					        <div class="bg-odd col-md-8">New Hires who are not yet verified</div>
					        <div class="bg-odd col-md-1">&nbsp;</div>
					        <div class="bg-odd col-md-1">4</div>
					        
					        <div class="bg-even col-md-8">New Hires with incomplete Bank Details</div>
					        <div class="bg-even col-md-1">&nbsp;</div>
					        <div class="bg-even col-md-1">1</div>
					        
					        <div class="bg-odd col-md-8">Employees in Transition or Hospital Authority Organization</div>
					        <div class="bg-odd col-md-1">&nbsp;</div>
					        <div class="bg-odd col-md-1">1</div>
					        
					        <div class="bg-even col-md-8">Employees with missing medical enrolment input</div>
					        <div class="bg-even col-md-1">&nbsp;</div>
					        <div class="bg-even col-md-1">0</div>

	                     </div>
	                 </div>
	                 
                 </div>
                
                
                
                
                
                
                
                <form:form id="wrSearchForm" action="processWorkRequest" method="POST" modelAttribute="workRequest">                    
                <!-- Search criteria panel -->
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#searchCriteria" id="criteriaTitle">Search People</a>
                        </h4>
                    </div>
                    
                    <div id="searchCriteria" class="panel-collapse collapse in">
                        <div class="panel-body" style="padding: 8px 0px;">
                        
                        
                        	<div class="container-fluid">
                        		<div class="form-group col-md-3">
                        			<label class="control-label" for="assetNumber">Effective Date:</label>
                        		</div>
                        	</div>
                        
                        
                            <div class="container-fluid">

                                <div class="form-group col-md-3">
                                    <label class="control-label" for="assetNumber">HKID</label>
                                    <div class="input-group">
	                                    <form:input path="assetNumber" class="form-control checkValidity" id="assetNumber"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetNumber" id="assetNumber"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>

                                <div class="form-group col-md-3">
                                    <label class="control-label" for="assetNumber">Employee Number</label>
                                    <div class="input-group">
	                                    <form:input path="assetNumber" class="form-control checkValidity" id="assetNumber"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetNumber" id="assetNumber"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>

                            </div>
                            
                            
                            
                            <div class="container-fluid">

                                <div class="form-group col-md-3">
                                    <label class="control-label" for="assetNumber">HKID Name</label>
                                    <div class="input-group">
	                                    <form:input path="assetNumber" class="form-control checkValidity" id="assetNumber"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetNumber" id="assetNumber"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>

                                <div class="form-group col-md-3">
                                    <label class="control-label" for="assetNumber">Chinese Name</label>
                                    <div class="input-group">
	                                    <form:input path="assetNumber" class="form-control checkValidity" id="assetNumber"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetNumber" id="assetNumber"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                
                                
                            </div>
                            
                            
                        	<div class="container-fluid">
                                <div class="col-md-3 pull-right" style="position: relative; height: 50px; margin-bottom: 10px;">
                                	<button type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 880px;' id="createPeopleRecord">Create People Record</button>
                                    <input type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 600px;' value="Search" id="wrSearchBtn" data-loading-text="Searching.." />
                                    <button type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 515px;' id="clearWrSearchBTN">Clear All</button>
                                </div>
                        	</div>
                            
                            
                        </div>
                    </div>
                    
                </div>
                </form:form>
                <!-- Search results datatable -->
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
