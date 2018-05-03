<%-- 
    Document   : index
    Created on : Jun 14, 2017, 10:38:19 AM
    Author     : Carmen
    Last update: Jun 21, 2017, 12:26 PM
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Dashboard#~*KIDSON 5</title>

        <script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/chart.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/ha/pages/searchPeople.js?v=${initParam.buildTimeStamp}" />"></script>
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
        <link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
        
        <script>
            
            var savedModulesArray = [];
            var modulesArray = [];
            var userId = '${sessionScope.ebsUserId}';
            var respId = '${sessionScope.ebsRespId}';
            var appRespId = '${sessionScope.ebsRespAppId}';
            
            var CMDateDefault = '${sessionScope.ebsCMDateRange}';
            var PMDateDefault = '${sessionScope.ebsPMDateRange}';

			var corpDateRangeMax = '${sessionScope.ebsCorpDateRangeLimit}';
            var corpPMDateDefault = '${sessionScope.ebsCorpPMDateRange}';
            var corpCMDateDefault = '${sessionScope.ebsCorpCMDateRange}';	
            
			var summaryOff = '${sessionScope.ebsSummaryDefaultOff}';
            
            $(document).ready(function(){            	
            
                loadSettings(userId, respId, appRespId);

                //Save settings click function put here to pass session attributes
                $("#saveSettingsBTN").click(function(){     
                	if(cmChart1 && cmChart2 && pmChart1 && pmChart2 && woTrend){
                		cmChart1.destroy();
                    	cmChart2.destroy();
                    	pmChart1.destroy();
                    	pmChart2.destroy();
                    	woTrend.destroy();
                	}
                    saveSettings(userId, respId, appRespId);
                });
                
                $('#createPeopleRecord').click(function() {
              	  //alert( "creqtePeopleRecord called." );
              	  window.location='./initCreatePeople';
              	});
                
                $('#pplSearchBtn').click(function(){
                	//alert('PeopleSearch');
                	//$('#peopleSearchForm').submit();
                	submitSearch();
                });
            });
            
        </script>

    </head>
    <body>
        <jsp:include page="sidebarHeader.jsp" />    
        
        <div style="margin-right: 30px">
            <button id='viewSettingsBTN' type="button" class="btn btn-default btn-sm pull-right" data-toggle="modal" data-target="#viewSettings" style="margin-top: 10px"><span class="glyphicon glyphicon-wrench"></span> Edit Preferences</button>
        </div>
        
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
         
             <form:form id="peopleSearchForm" action="processPeople" method="POST" modelAttribute="people">                    
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
                        			<label class="control-label" for="effDate">Effective Date:</label>
		                             <div class='input-group date dateFrom dateFromLimited'>
					                    <input type='text' class="form-control col-md-6" name="effDate" id="effDate"/>
					                    <span class="input-group-addon">
					                      <span class="glyphicon glyphicon-calendar"></span>
					                    </span>
					                 </div>
					                   <small class="form-text text-muted CMDateHint"></small>
                        		</div>
                        	</div>
                        
                             <div class="container-fluid">
                                <div class="form-group col-md-3">
                                    <label class="control-label" for="hkid">HKID</label>
                                    <div class="input-group">
	                                    <form:input path="hkid" class="form-control checkValidity" id="hkid"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="hkidBtn" id="hkidBtn"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>

                                <div class="form-group col-md-3">
                                    <label class="control-label" for="empNumber">Employee Number</label>
                                    <div class="input-group">
	                                    <form:input path="empNumber" class="form-control checkValidity" id="empNumber"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="empNumberBtn" id="empNumberBtn"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                            </div>
                            
                            
                            
                            <div class="container-fluid">

                                <div class="form-group col-md-3">
                                    <label class="control-label" for="hkidName">HKID Name</label>
                                    <div class="input-group">
	                                    <form:input path="hkidName" class="form-control checkValidity" id="hkidName"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="hkidNameBtn" id="hkidName"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>

                                <div class="form-group col-md-3">
                                    <label class="control-label" for="chnName">Chinese Name</label>
                                    <div class="input-group">
	                                    <form:input path="chnName" class="form-control checkValidity" id="chnName"/>
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="chnNameBtn" id="chnNameBtn"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                
                                
                            </div>
                  
                        	<div class="container-fluid">
                                <div class="col-md-10 form-inline" style="position: relative; height: 50px; margin-bottom: 10px;">
                                	<button type="button" class="btn btn-primary" style="margin-right:100px;" id="createPeopleRecord" data-loading-text="Loading People..">Create People Record</button>
                                    <input type="button" class="btn btn-primary"  value="Search" id="pplSearchBtn" data-loading-text="Searching.." />
                                    <button type="button" class="btn btn-primary" id="clearPplSearchBTN">Clear All</button>
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
                     <table id="pplSearchResults" class="table table-bordered table-responsive table-striped">
                         <thead class="thead-default">
                             <tr>
                                 <th></th>
                                 <th>Employee Number</th>
								 <th>Last Name</th>
								 <th>First Name</th>
                             </tr>
                         </thead>
                     </table>
                 </div>
             </div>         
         
            </div>
		</div>	
        

        

		<!-- View Settings Modal -->
        <div class="modal" id="viewSettings" role="dialog" data-backdrop="static" data-keyboard="false">
         	<div class="modal-dialog  modal-lg">
                <div class="modal-content">
	                  <div class="modal-header">
	                    <h4 class="modal-title">Dashboard Settings</h4>
	                  </div>
                	<div class="modal-body">
                        <div class="container-fluid">
                			<div class="alert" style="display: none" id="preferenceAlert"></div>
                        
                            <form:form id="saveDashboardFiltersForm" action="processUserPreference" method="POST" modelAttribute="dashBoard">   
               					<% if(!"IT Asset".equals(session.getAttribute("respType")) && "Y".equals(session.getAttribute("respRequestCreate")) && "Y".equals(session.getAttribute("respRequestView"))) { %>
	                                <div class="form-group col-md-12 checkbox">
	                                    <label style="font-weight: bold"><input type="checkbox" id="wrWoSaved" name="wrWoSaved" checked>My Work Requests/Orders Only</label>
	                                </div>
                                <% } else { %>
                                	<div class="form-group col-md-12 checkbox">
	                                    <label style="font-weight: bold"><input type="checkbox" id="wrWoSaved" name="wrWoSaved" checked>My Work Orders Only</label>
	                                </div>
                                <% } %>

                                <div class="form-group col-md-4">
                                    <label  class="control-label" for="eamOrgSaved">EAM Org</label>
                                    <form:select path="eamOrgSaved" class="form-control selectpicker" id="eamOrgSaved" data-live-search="true">
										<form:option value="" label="-- Select --" />
                                        <form:options items="${eamOrgList}" itemValue="name" itemLabel="desc"/>
                                    </form:select>
                                </div>
                                <div class="form-group col-md-4">
                                        <label class="control-label" for="owningDeptSaved">Owning Department</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control checkValidity" id="owningDeptSaved" name="owningDeptSaved">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="owningDeptSaved" id="owningDept"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                <div class="form-group col-md-4">
                                        <label class="control-label" for="assetLocationSaved">Location Code</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control checkValidity" id="assetLocationSaved" name="assetLocationSaved">
                                        <span class="input-group-btn">
                                            <button type="button" class="btn btn-primary lovFieldSearchBtn" name="assetLocationSaved" id="assetLocation"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                            </form:form>
                                                                      
                            <div class="col-md-6">
                            	<h5 class="text-default"><b>Corrective Maintenance</b></h5>
               					<% if(!"IT Asset".equals(session.getAttribute("respType")) && "Y".equals(session.getAttribute("respRequestCreate")) && "Y".equals(session.getAttribute("respRequestView"))) { %>
	                                <div class="checkbox">
	                                  <label>
	                                  <input id='cm1Switch' class="toggles" checked type="checkbox">
	                                  Work Requests Summary
	                                  </label>
	                                </div>
                                <% } %>
                                <div class="checkbox">
                                  <label>
                                  <input id='cm2Switch' class="toggles" checked type="checkbox">
                                  Work Order Status Summary
                                  </label>
                                </div>
                                <div class="checkbox">
                                  <label>
                                  <input id='cm3Switch' class="toggles" checked type="checkbox">
                                  Outstanding Work Orders
                                  </label>
                                </div>
                            </div>

                            <div class="col-md-6">
                            	<h5 class="text-default"><b>Preventive Maintenance</b></h5>
                                <div class="checkbox">
                                  <label>
                                  <input id='pm1Switch' class="toggles" checked type="checkbox">
                                  Work Order Summary
                                  </label>
                                </div>
                                <div class="checkbox">
                                  <label>
                                  <input id='pm2Switch' class="toggles" checked type="checkbox">
                                  Work Order Status Summary
                                  </label>
                                </div>
                                <div class="checkbox">
                                  <label>
                                  <input id='pm3Switch' class="toggles" checked type="checkbox">
                                  Outstanding Work Order
                                  </label>
                                </div>
                            </div>          
                        </div>
	                  <div class="modal-footer">
	                  		<button type="button" class="btn btn-primary" id="saveSettingsBTN">Save</button>
	                    	<button type="button" class="btn btn-default" data-dismiss="modal" id="cancelSettingsBTN">Cancel</button>
	                  </div>
                	</div>
            	</div>
        	</div>
        </div>
        
    </body>
</html>
