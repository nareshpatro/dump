<%-- 
    Document   : searchWO
    Created on : Jun 14, 2017, 11:13:39 AM
    Author     : Carmen
    Last Update: Jun 16, 2017, 3:04 PM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@page import="java.util.List"%>

<!DOCTYPE html>
<html>
    <head>
       <meta http-equiv="X-UA-Compatible" content="IE=edge">
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Search Work Orders</title>
        
        <script src="<spring:url value="/resources/js/moment.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>        
        <script src="<spring:url value="/resources/js/datatables/datatables.min.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/datatables/DataTables-1.10.15/js/dataTables.bootstrap.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/datatables/colResize.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/searchOrders.js?v=${initParam.buildTimeStamp}" />"></script>
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
            	
                //Some parameter checks and functions for when the user enters the page via the dashboard 
                <% if(!request.getParameterMap().isEmpty()){ %> 
                
                    $('#woType').selectpicker('val', ['${param.statusType}']);
                    $('#eamOrg').selectpicker('val', ['${param.eamOrg}']);
                    $('#assetRisk').selectpicker('val', ['${param.riskLevel}']);
                
                if('${param.creationFrom}' !== "" && '${param.creationTo}' !== "" ){
                	$('#dateType').selectpicker('val', ['C']);
                	$('.dateFromLabel').html("Work Order Creation Date From");
                	$('.dateToLabel').html("Work Order Creation Date To");
                	$('.selectDate').hide();
                	$('#dateFrom').val('${param.creationFrom}');
                	$('#dateFrom').attr('disabled', false);
                	$('#dateTo').val('${param.creationTo}');
                	$('#dateTo').attr('disabled', false);
                }
                
                if('${param.scheduleFrom}' !== "" && '${param.scheduleTo}' !== "" ){
                	$('#dateType').selectpicker('val', ['S']);
	            	$('.dateFromLabel').html("PM Scheduled Date From");
                	$('.dateToLabel').html("PM Scheduled Date To");
                	$('.selectDate').hide();
                	$('#dateFrom').val('${param.scheduleFrom}');
                	$('#dateFrom').attr('disabled', false);
                	$('#dateTo').val('${param.scheduleTo}');
                	$('#dateTo').attr('disabled', false);
                }
                    
                if('${param.wrWo}' !== ""){
                	$('#hiddenUser').val('${sessionScope.ebsUserName}');
                	$('#requestedFor').val('${sessionScope.ebsFullName}');
                }
                
                if('${param.maintenanceVendor}' !== ""){
                	$('#maintenanceVendor').val('${param.maintenanceVendor}');
                }
                                
                if('${param.criticalMedEquip}' == "true"){
                    $('#criticalOnly').prop('checked', true);
               	}
	            
	            /***********Code for redirect from OAF*************/ 
	            if(GetURLParameter("fromOAF")==="Yes"){
	            	document.getElementById("assetNumber").value=GetURLParameter("assetNumber");
	            	window.history.replaceState({}, document.title, "/EAM/searchWorkOrder#no-back-button");
	            }
	            /***********Code for redirect from OAF*************/ 
	            
                //Fill in multiple select values
                if('${param.woStatus}' == "breakdown"){
                	$('#dateType').selectpicker('val', ['B']);
	            	$('.dateFromLabel').html("CM Breakdown Date From");
                	$('.dateToLabel').html("CM Breakdown Date To");
                	$('.selectDate').hide();
                	dashboardSearch();
                }
                else if('${param.woStatus}' == "exception"){
	           		//$('#woStatus').selectpicker('val', [1000, 6, 1001, 3, 1]);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'On Hold'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Released'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Unreleased'
					}).prop('selected', true);
					$('#woStatus').selectpicker('refresh');
					dashboardSearch();
	           	}
                else if('${param.woStatus}' == "chart1"){
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'On Hold'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Released'
					}).prop('selected', true);
					$('#woStatus').selectpicker('refresh');
					dashboardSearch();
	           	}
                else if('${param.woStatus}' == "chart2"){
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Unreleased'
					}).prop('selected', true);
					$('#woStatus').selectpicker('refresh');
					dashboardSearch();
	           	}
                else if('${param.woStatus}' == "Outstanding"){
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Released'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'On Hold'
					}).prop('selected', true);
					$('#woStatus').selectpicker('refresh');
					dashboardSearch();
	           	}
	           	else if ('${param.woStatus}' == "PMException"){
	           		//$('#woStatus').selectpicker('val', [1,3,6]);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Unreleased'
					}).prop('selected', true);
					$('#woStatus').selectpicker('refresh');
					dashboardSearch();
	           	}
	           	else if ('${param.woStatus}' == "PMComplete"){
	           		//$('#woStatus').selectpicker('val', [4,7,12,98,1000,1001]);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Complete'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Cancelled'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Closed'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Cancelled By PM'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Complete - Pending Close'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Rejected'
					}).prop('selected', true);
					$('#woStatus').selectpicker('refresh');
					dashboardSearch();
	           	}
	           	else if ('${param.woStatus}' == "PMTotal"){
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Unreleased'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Complete'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Cancelled'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Closed'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Cancelled By PM'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Complete - Pending Close'
					}).prop('selected', true);
	           		jQuery("#woStatus option").filter(function(){
					    return $.trim($(this).text()) ==  'Rejected'
					}).prop('selected', true);
					$('#woStatus').selectpicker('refresh');
					dashboardSearch();
	           	}
	           	else if ('${param.woStatus}' != "Total"){
	           		$.post("./LovLookup", { value: '${param.woStatus}', query: 2}, function(data){
							$('#woStatus').selectpicker('val', [data]);
							dashboardSearch();
	                });
	           	}
	           	else{
	           		if ('${param.woStatus}' == "Total"){
	           			$('#woStatus').selectpicker('selectAll');
	           		}
	           		dashboardSearch();
	           	}
                <% } %> 
                
                $('#woSearchResults').on( 'column-reorder.dt', function ( e, settings, details ) {
                	if(loaded == 3){
                		$.ajax({
                            type: 'POST',
                            data : {
                                "USER_ID" : userId,
                                "RESP_ID" : respId,
                                "APP_ID" : appRespId,
                                "PREF_NAME" : "ColOrder",
                                "PREF_VALUE" : JSON.stringify($('#woSearchResults').DataTable().colReorder.order()),
                                "FUNCTION_CODE" : "WORK ORDER",
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
                
                $('#woSearchResults').on( 'column-visibility.dt', function ( e, settings, column, state ) {
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
                });
                
                $('#woSearchResults').on( 'order.dt', function ( e, settings ) {
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
                        "FUNCTION_CODE" : "WORK ORDER",
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
                                    "FUNCTION_CODE" : "WORK ORDER",
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
                                            "FUNCTION_CODE" : "WORK ORDER",
                                            "PAGE_CODE" : "SEARCH"
                                        },    
                                        url : './delUserPrefExt',
                                        dataType:'text',
                                        success : function(result) {
                                	
                                			loaded = 0;
                                			var hasData = false;
                                			var table = $('#woSearchResults').DataTable();
                                	
                                			if (table.data().any()) {
                                				hasData = true;
                                			}
                                	
                                			table.destroy();
                                			$('#woSearchResults').empty(); 
                                			if(hasData){
                                				initialiseTable(true);
                                			}
                                			else{
                                				initialiseTable(false);
                                			}
                                			$("#woSearchResults").addClass("table table-bordered table-responsive table-striped"); 
                                			$("#woSearchResults").find("thead").addClass("thead-default");
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
                <!-- Alert for no search parameters entered no values found for maintenance body/users -->
                <div class="alert alert-danger lovSearchAlert" role="alert" id="searchAlert" style="display: none;"></div>
                
                <!--  Alert for pm summary click throughs -->
                <div class="alert alert-info" role="alert" id="fromDashboardAlert" style="display: none;"></div>
                
                <form:form id="woSearchForm" action="EAM/processWorkOrder" method="POST" modelAttribute="workOrder">   
                <input type="hidden" name="dashboardValue" id="dashboardValue">
                <!-- Search criteria panel -->
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#searchCriteria" id="criteriaTitle">Search Criteria</a>
                        </h4>
                    </div>
                    
                    <div id="searchCriteria" class="panel-collapse in">
                        <div class="panel-body" style="padding: 8px 0px;">
                            <div class="container-fluid">

                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="woNumber">Work Order Number</label>
                                    <form:input path="woNumber" class="form-control col-md-3" id="woNumber"/>
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
                                    <label  class="control-label" for="woType">Work Order Type</label>
                                    <form:select path="woType" class="form-control selectpicker"  id="woType">
                                        <form:option value="" label="-- Select --" />
                                        <form:options items="${woTypeList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                </div>

                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="woStatus">Work Order Status</label>        
                                    <form:select path="woStatus" class="form-control selectpicker" multiple="true" id="woStatus">
                                        <<form:options items="${woStatusList}" itemValue="name" itemLabel="desc"/>
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
                                    <form:select path="eamOrg" class="form-control selectpicker" data-live-search="true">
                                        <form:option value="" label="-- Select --" />
                                        <form:options items="${eamOrgList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                    <% }  %>
                                    
                                </div>
                            
                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="dateType">Date Type</label>
                                    <form:select path="dateType" class="form-control selectpicker" id="dateType">
										<form:option value="" label="-- Select --" />
                                        <form:options items="${dateTypeList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                </div>
                                
                                <div class="form-group col-md-3">        
                                    <label  class="control-label dateFromLabel" for="dateFrom">
                                        <small class="form-text text-muted selectDate"><spring:message code="HA_WARNING_GENERAL_SELECTDATETYPE" /></small>
                                    </label>
                                    <div class='input-group date dateFrom'>
                                        <input type='text' class="form-control" id="dateFrom" name="dateFrom" disabled />
                                        <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span>
                                    </div>
                                    <small id="fromDateHint" class="form-text text-muted"></small>
                                </div>
                                
                                <div class="form-group col-md-3">
                                    <label  class="control-label dateToLabel" for="dateTo">
                                    	<small class="form-text text-muted selectDate"><spring:message code="HA_WARNING_GENERAL_SELECTDATETYPE" /></small>
                                    </label>
                                    <div class='input-group date dateTo'>
                                        <input type='text' class="form-control" id="dateTo" name="dateTo" disabled />
                                        <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span>
                                    </div>
                                    <small id="toDateHint" class="form-text text-muted"></small>
                                </div>
                            </div>
							
							<% if("IT Asset".equals(session.getAttribute("respType"))) { %>
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
	                                        <input type="text" class="form-control checkValidity" id="assetLocation" name="assetLocation" value=${param.assetLocation}>
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
	                                    <label class="control-label" for="maintenanceVendor">Maintenance Body</label>
	                                    <div class="input-group">
	                                        <input type="text" class="form-control" id="maintenanceVendor">
	                                        <span class="input-group-btn">
	                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="maintenanceVendor" id="maintenanceVendor"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
	                                        </span>
	                                        <input type="hidden" class="form-control" id="hiddenMBody" name="maintenanceVendor" value=${param.mBody}>
	                                    </div>
	                                </div>
	                            </div>
	                            
	                            <div class="container-fluid">
	                                <div class="form-group col-md-3">
	                                    <label  class="control-label" for="serialNumber">Serial Number</label>
	                                    <input type="text" class="form-control col-md-2" id="serialNumber" name="serialNumber">
	                                </div>
	                                
	                                <div class="form-group col-md-3">
	                                    <label  class="control-label" for="createdBy">Work Order Creation By</label>
	                                    <div class="input-group">
	                                        <input type="text" class="form-control col-md-2" id="requestedFor">
	                                        <span class="input-group-btn">
	                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="requestedFor" id="requestedFor"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
	                                        </span>
	                                        <input type="hidden" class="form-control col-md-2" id="hiddenUser" name="createdBy">
	                                    </div>                               
	                                </div>
									
									<div class="col-md-3 pull-right" style="position: relative; height: 50px; margin-bottom: 10px;">
	                                    <input type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 100px;' value="Search" id="woSearchBtn" data-loading-text="Searching.." />
	                                    <button type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 15px;' id="clearWoSearchBTN">Clear All</button>
	                                </div>
	                            </div>
							<% } else { %>
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
                                        <input type="text" class="form-control checkValidity" id="assetLocation" name="assetLocation" value=${param.assetLocation}>
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
                                    <label  class="control-label" for="assetRisk">Risk Level</label>
                                    <form:select path="assetRisk" class="form-control selectpicker" id="assetRisk">
										<form:option value="" label="-- Select --" />
                                        <form:options items="${riskLevelList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                </div>
                            </div>
							
							<div class="container-fluid">
								<div class="form-group col-md-3">
                                        <label class="control-label" for="maintenanceVendor">Maintenance Body</label>
                                    <div class="input-group">
                                        <input type="hidden" class="form-control" id="hiddenMBody"  name="maintenanceVendor" value=${param.mBody}>
                                        <input type="text" class="form-control" id="maintenanceVendor">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="maintenanceVendor" id="maintenanceVendor"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                
                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="maintenanceContract">Maintenance Contract Number</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control checkValidity" id="maintenanceContract">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="maintenanceContract" id="maintenanceContract"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div> 

                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="serialNumber">Serial Number</label>
                                    <input type="text" class="form-control col-md-2" id="serialNumber" name="serialNumber">
                                </div>
                                
                                <div class="form-group col-md-3">
                                    <label  class="control-label" for="createdBy">Work Order Creation By</label>
                                    <div class="input-group">
                                        <input type="hidden" class="form-control col-md-2" id="hiddenUser" name="createdBy">
                                        <input type="text" class="form-control col-md-2" id="requestedFor">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="requestedFor" id="requestedFor"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>                               
                                </div>
                            </div>
                            
                            <div class="container-fluid">    
                                <div class="form-group col-md-3 checkbox" style="position: relative">
                                    <label style="position: absolute; bottom: -35px;  font-weight: bold"><input type="checkbox" id="criticalOnly" name="criticalOnly"  ${param.critialMedEquip == "Critical Medical Equipment Only" ? 'checked' : ''}>Critical Medical Equipment Only</label>
                                </div>

                                <div class="form-group col-md-2 checkbox" style="position: relative">
                                        <label style="position: absolute; bottom: -50px;  font-weight: bold"><input type="checkbox" id="urgentOnly" name="urgentOnly">Urgent Order Only</label>
                                    </div>
                                
                                <div class="col-md-3 pull-right" style="position: relative; height: 50px; margin-bottom: 10px;">
                                    <input type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 100px;' value="Search" id="woSearchBtn" data-loading-text="Searching.." />
                                    <button type="button" class="btn btn-primary" style='position: absolute; bottom: 0px; right: 15px;' id="clearWoSearchBTN">Clear All</button>
                                </div>
                            </div>
							<% } %>
                            
                        </div>
                    </div>
                </div>
            </form:form>
                                
            
            <!-- Search results datatable -->
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">Search Results</h4>
                    </div>
                    <div class="panel-body" style="padding: 15px 20px 15px 20px">
                        <table id="woSearchResults" class="table table-bordered table-responsive table-striped">
                            <thead class="thead-default">
                                <tr>
                                    <th></th>
                                    <th>Work Order Number</th>
                                    <th>EAM Org</th>
                                    <th>Asset Number</th>
                                    <th>Asset Description</th>
                                    <th>Type</th>
                                    <th>Status</th>
                                    <th>CM Breakdown Date Time</th>
                                    <th>PM Scheduled Date Time</th>
                                    <th>Owning Department</th>
                                    <th>Work Order Creation By</th>
                                    
                                    <th>Maintenance Body</th>
                                    <th>Maintenance Contract Number</th>
                                    <th>Location Code</th>
                                    <th>Asset Owner</th>
                                    <th>Asset Group</th>
                                    <th>Work Order Creation Date</th>
                                    <th>Asset Serial Number</th>
                                    <th>Manufacturer</th>
                                    <th>Brand</th>
                                    <th>Model</th>
                                    <th>Work Order Description</th>
                                </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
            
        <!-- Hidden form for export -->
        <form id="exportForm" action="ExportData" method="post" target="_blank">
          <input type="hidden" name="woNumber" id="exportWoNumber">
          <input type="hidden" name="userName" id="exportUserName">
          <input type="hidden" name="parameter" id="exportParameter">
          <input type="hidden" name="exportType" id="exportExportType">
        </form>
        
        <!-- Hidden form for print -->
        <form id="printForm" action="GenReport" method="post" target="_blank">
          <input type="hidden" name="itemNum" id="printWoNumber">
          <input type="hidden" name="reportType" id="printReportType">
        </form>
        
        <!--  Hidden form for view work request -->
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
    </body>
</html>
