<%-- 
    Document   : sidebarHeader
    About      : Contains the sidebar, the header and any modals used by more than one pages
    Created on : Jun 14, 2017, 10:52:35 AM
    Author     : Carmen
    Last update: Jun 14, 2017, 3:24 PM
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@page import="hk.org.ha.eam.util.EBizUtil"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script src="<spring:url value="/resources/js/moment.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/jQueryUI.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/tether.js?v=${initParam.buildTimeStamp}" /> "></script>
        <script src="<spring:url value="/resources/js/bootstrap.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/bootstrapTable/bootstrapTable.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/bootstrapTable/reorderColumn.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/bootstrapTable/tableExport.js?v=${initParam.buildTimeStamp}" />"></script>
        <script src="<spring:url value="/resources/js/bootstrapTable/resizeColumn.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/resizeCol.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        <script src="<spring:url value="/resources/js/validator.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
        
        <script> 
            $(document).ready(function(){
                sidebarOpen = true;
                $("#sideBarBTN").click(function(){
                    if(!sidebarOpen){
                        $("#sidebar").animate({left:"0px"}, 500 );
                        $("#content").animate({"margin-left":"200px"}, 500 );
                        sidebarOpen = true;
                    }
                    else{
                        $("#sidebar").animate({left:"-200px"}, 500 );
                        $("#content").animate({"margin-left":"0px"}, 500 );
                        sidebarOpen = false;
                    }

                });
    
                $('#' + page).addClass("sidebarActive");     
                
                $(window).resize(function(){
                    if($( window ).width() < 768){
                        $("#sidebar").animate({left:"-200px"}, 500 );
                        $("#content").animate({"margin-left":"0px"}, 500 );
                        sidebarOpen = false;
                    }
                    else {
                        $("#sidebar").animate({left:"0px"}, 500 );
                        $("#content").animate({"margin-left":"200px"}, 500 );
                        sidebarOpen = true;
                    }
                });
            });
        </script>
    </head>
    <body>
        <div id="sidebarHeader">
            <!-- Side Bar -->
            <nav class="navbar navbar-default navbar-fixed-top" id="sidebar" role="navigation">
                <ul class="nav">
                    <li id='sidebarDashboard'>
                        <a href="dashBoard" class="sidebarLink">Dashboard<span class="glyphicon glyphicon-dashboard pull-right"></span></a>
                    </li>
                    <% if("Y".equals(session.getAttribute("respRequestCreate")) && "Y".equals(session.getAttribute("respRequestView"))){ %>
                    
                    	<li id="sideBarRequestTitle"><br>
	                         <span class="sideBarTitles">Search</span>
	                    </li>
	                    <li id='sidebarSearchWr'>
	                        <a href="searchPeople" class="sidebarLink">Search People<span class="glyphicon glyphicon-search pull-right"></span></a>
	                    </li>
	                    
                    <% } else if("Y".equals(session.getAttribute("respRequestCreate"))) { %>
                    	<li id="sideBarRequestTitle"><br>
	                         <span class="sideBarTitles">Work Requests</span>
	                    </li>
	                    <li id='sidebarCreateWr'>
	                        <a href="initCreateWorkRequest" class="sidebarLink">Create Requests<span class="glyphicon glyphicon-edit pull-right"></span></a>
	                    </li>
                    <% } else if("Y".equals(session.getAttribute("respRequestView"))) { %>
                    	<li id="sideBarRequestTitle"><br>
	                         <span class="sideBarTitles">Work Requests</span>
	                    </li>
	                    <li id='sidebarSearchWr'>
	                        <a href="searchWorkRequest" class="sidebarLink">Search Requests<span class="glyphicon glyphicon-search pull-right"></span></a>
	                    </li>
                    <% } %>
                    
                    <% if("Y".equals(session.getAttribute("respOrderCreate")) && "Y".equals(session.getAttribute("respOrderView"))){ %>
                    
                    	<li id="sideBarOrderTitle"><br>
                        	<span class="sideBarTitles">Employee</span>
	                    </li>
	                    <li id='sidebarCreateWo'>
	                        <a href="initCreateWorkOrder" class="sidebarLink">Basic Information<span class="glyphicon glyphicon-edit pull-right"></span></a>
	                    </li>
	                    <li id='sidebarCreateWo'>
	                        <a href="initCreateWorkOrder" class="sidebarLink">Other Information<span class="glyphicon glyphicon-edit pull-right"></span></a>
	                    </li>
	                    <li id='sidebarCreateWo'>
	                        <a href="initCreateWorkOrder" class="sidebarLink">Historical Information(HRPS)<span class="glyphicon glyphicon-edit pull-right"></span></a>
	                    </li>
	                    <li id='sidebarCreateWo'>
	                        <a href="initCreateWorkOrder" class="sidebarLink">EC Details<span class="glyphicon glyphicon-edit pull-right"></span></a>
	                    </li>
	                    
                    	<li id="sideBarOrderTitle"><br>
                        	<span class="sideBarTitles">Report</span>
	                    </li>
	                    <li id='sidebarCreateWo'>
	                        <a href="initCreateWorkOrder" class="sidebarLink">Person Search Report<span class="glyphicon glyphicon-edit pull-right"></span></a>
	                    </li>
	                    
	                    
                    <% } else if("Y".equals(session.getAttribute("respOrderCreate"))) { %>
                    	<li id="sideBarOrderTitle"><br>
                        	<span class="sideBarTitles">Work Orders</span>
	                    </li>
	                    <li id='sidebarCreateWo'>
	                        <a href="initCreateWorkOrder" class="sidebarLink">Create Orders<span class="glyphicon glyphicon-edit pull-right"></span></a>
	                    </li>
                    <% } else if("Y".equals(session.getAttribute("respOrderView"))) { %>
                    	<li id="sideBarOrderTitle"><br>
                        	<span class="sideBarTitles">Work Orders</span>
	                    </li>
	                    <li id='sidebarSearchWo'>
	                        <a href="searchWorkOrder" class="sidebarLink">Search Orders<span class="glyphicon glyphicon-search pull-right"></span></a>
	                    </li>
                    <% } %>
                </ul>
            </nav>
         
            <!-- Header -->
            <nav class="navbar navbar-inverse navbar-fixed-top">
               <div class='container-fluid'>
                  <div class="navbar-header">
                        <button type="button" class="navbar-toggle" id="menu-toggle" data-toggle="collapse" data-target="#topNavbar">
                          Menu                        
                        </button>
                      <button id='sideBarBTN' type="button" class="navbar-toggle" style='float: left; margin: 8px 10px 8px 0px'>
                          <span class="icon-bar"></span>
                          <span class="icon-bar"></span>
                          <span class="icon-bar"></span> 
                      </button>
                      <a class="navbar-brand nonLink" href="#">                      	  
                          <img src="<spring:url value="/resources/images/ha_logo.gif" /> ">
                      </a>
                  </div>
                  <div class="collapse navbar-collapse" id="topNavbar">
                  <ul class="nav navbar-nav navbar-right">
                  	<li><a class="nonLink" href="#"><span class="glyphicon glyphicon-user"></span> ${sessionScope.ebsRespName}</a></li>
                    <li><a href="<%=(EBizUtil.getEBizInstance().getAppsServletAgent() + "OA.jsp?OAFunc=OAHOMEPAGE")%>"><span class="glyphicon glyphicon-home"></span> Home</a></li>
                    <li><a class="nonLink" href="#" id="loggedInUser"><span class="glyphicon glyphicon-user"></span> ${sessionScope.ebsFullName}</a></li>
                    <li><a href="logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
                  </ul>
                  </div>
               </div>
            </nav>
        </div>
    </body>
    
    <!-- ******* MODALS ******* -->
    
    <!-- Search Lov Modal -->
    <div class="modal" id="lovSearch" role="dialog">
        <div class="modal-dialog  modal-md">
            <div class="modal-content">
              <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title" id="lovSearchTitle"></h4>
              </div>
                <div class="modal-body">
                    <div class="container-fluid">
                        <div class="alert alert-warning" role="alert" id="multipleAlert" style="display:none"></div>
                        <div class="alert alert-danger" role="alert" id="noValueAlert" style="display:none"><strong><span class='glyphicon glyphicon-exclamation-sign'></span> Error! </strong><spring:message code="HA_WARNING_GENERAL_INVALIDSEARCHVALUE" /></div>
                        
                        <div class="form-group col-md-5">
                            <label  class="control-label" for="lovSearchType">Search By</label>
                            <select class="form-control selectpicker" id="lovSearchType" name="lovSearchType">
                            </select>
                        </div>

                        <div class="form-group col-md-5">
                            <label  class="control-label" for="lovSearchValue">Search Value</label>
                            <input type="text" class="form-control col-md-3" id="lovSearchValue" name="lovSearchValue">
                        </div>

                        <div class="form-group col-md-2">
                            <input type="button" class="btn btn-primary" value="Search" style="position: absolute; bottom: -47px" id="lovSearchBtn" />
                        </div>

                        <table class="table-condensed" id="searchLovResults" data-height="400" data-toggle="table" data-search="true">
                         <thead>
                         <tr>
							<th data-field='contract_num' data-visible="false">Contract Number</th>
                            <th data-field='vendor_number' data-visible="false">Vendor Number</th>
                            <th data-field='vendor_name' data-visible="false">Vendor Name</th>
                            <th data-field='locationcode' data-visible="false" data-width="140">Location Code</th>
                            <th data-field='description1' data-visible="false">Description</th>
                            <th data-field='address' data-visible="false">Address</th>
                            <th data-field='assetowner'  data-visible="false">Asset Owner</th>
                            <th data-field='description2' data-visible="false">Description</th>
                            <th data-field='department_code' data-visible="false">Department Code</th>
                            <th data-field='description3' data-visible="false">Description</th>
                            <th data-field='user_name' data-visible="false">User Name</th>
                            <th data-field='full_name' data-visible="false">Employee Name</th>
                            <th data-field='assetNumber' data-visible="false">Asset Number</th>
                            <th data-field='assetDescription' data-visible="false">Asset Description</th>
                         </tr>   
                         </thead>
                         <tbody>
                         	<tr>
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
    
    <!-- Show table cell full text modal -->
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
    
    <!--Asset Details Modal -->
    <div class="modal" id="assetDetailsModal" role="dialog">
        <div class="modal-dialog  modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title" id="assetInfoTitle"></h4>
                </div>
                <div class="modal-body">
                    <div class="panel panel-primary">
                        <div class="panel-heading">Asset Details</div>
                        <div class="panel-body">
                            <form class="form-horizontal">
                                <div class="form-group border">
                                    <label class="control-label col-md-2" for='aDescription'>Asset Description</label>
                                    <div class="col-md-6">
                                        <p class="control-label" id="aDescription"></p>
                                    </div>
                                    <label class="control-label col-md-2" for='aItemCode'>Item Code</label>
                                    <div class="col-md-2">
                                        <p class="control-label" id="aItemCode"></p>
                                    </div>
                                </div>
                                
                                <% if("IT Asset".equals(session.getAttribute("respType"))){  %>
	                                <div class="form-group border">
	                                    <label class="control-label col-md-2" for='aGroup'>Asset Group</label>
	                                    <div class="col-md-6">
	                                        <p class="control-label" id="aGroup"></p>
	                                    </div>
	                                </div>
                                <% } %>

                                <div class="form-group border">
                                    <label class="control-label col-md-2" for='aStatus'>Asset Status</label>
                                    <div class="col-md-2">
                                        <p class="control-label" id="aStatus"></p>
                                    </div>

                                    <label class="control-label col-md-2" for='dob'>Date of Birth</label>
                                    <div class="col-md-2">
                                        <p class="control-label" id="dob"></p>
                                    </div>

                                    <label class="control-label col-md-2" for='serialnumber'>Serial Number</label>
                                    <div class="col-md-2">
                                        <p class="control-label" id="serialnumber"></p>
                                    </div>
                                </div>

                                <div class="form-group border">   
                                    <label class="control-label col-md-2" for='aLocation'>Location Code</label>
                                    <div class="col-md-2" style="margin-top: 7px;">
                                        <a class="control-label" id="aLocation" href="#" data-toggle="tooltip" data-placement="bottom" title=""></a>
                                    </div>

                                   <label class="control-label col-md-2" for='aOwner'>Asset Owner</label>
                                   <div class="col-md-2" style="margin-top: 7px;">
                                       <a class="control-label" id="aOwner" href="#" data-toggle="tooltip" data-placement="bottom" title=""></a>
                                   </div>

                                   <label class="control-label col-md-2" for='owingDept'>Owning Department</label>
                                   <div class="col-md-2" style="margin-top: 7px;">
                                       <a class="control-label" id="owingDept" href="#" data-toggle="tooltip" data-placement="bottom" title=""></a>
                                   </div>
                                </div>

                                <div class="form-group border">
                                   <label class="control-label col-md-2" for='manufacturer'>Manufacturer</label>
                                   <div class="col-md-2">
                                       <p class="control-label" id="manufacturer"></p>
                                   </div>

                                   <label class="control-label col-md-2" for='brand'>Brand</label>
                                   <div class="col-md-2">
                                       <p class="control-label" id="brand"></p>
                                   </div>

                                   <label class="control-label col-md-2" for='model'>Model</label>
                                   <div class="col-md-2">
                                       <p class="control-label" id="model"></p>
                                   </div>
                                </div>

                                <div class="form-group border">
                                   <label class="control-label col-md-2" for='purchasePrice'>Purchase Price</label>
                                   <div class="col-md-2">
                                       <p class="control-label" id="purchasePrice"></p>
                                   </div>

                                   <label class="control-label col-md-2" for='aSupplier'>Asset Supplier</label>
                                    <div class="col-md-6">
                                        <p class="control-label" id="aSupplier"></p>
                                    </div>
                                </div>

                                <div class="form-group border">
                                   <label class="control-label col-md-2" for='riskLevel'>Risk Level</label>
                                   <div class="col-md-2">
                                       <p class="control-label" id="riskLevel"></p>
                                   </div>

                                   <label class="control-label col-md-2" for='parentAsset'>Parent Asset Number</label>
                                   <div class="col-md-2">
                                       <p class="control-label" id="parentAsset"></p>
                                   </div>
                                   
                                   <label class="control-label col-md-2" for='assetOrgModal'>Asset Organization</label>
                                   <div class="col-md-2">
                                       <p class="control-label" id="assetOrgModal"></p>
                                   </div>
                                </div>
                            </form>
                        </div>
                    </div>

                <div class="panel panel-primary" id="maintenanceDetailsPopUp">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            Maintenance Details
                        </h4>
                    </div>

                    <div class="panel-body">
                        <form class="form-horizontal">
                            <div class="form-group border">
                                <label class="control-label col-md-3" for="mBody">Maintenance Body</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mBody"></p>
                                </div>

                                <label class="control-label col-md-3" for="mBodyType">Maintenance Body Type</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mBodyType"></p>
                                </div>
                            </div>
                            
                            <div class="form-group border">
                                <label class="control-label col-md-3" for="contractNumber">Contract Number</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="contractNumber"></p>
                                </div>

                                <label class="control-label col-md-3" for="autoSend">Auto Send WO to Supplier</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="autoSend"></p>
                                </div>
                            </div>
                            
                            <div class="form-group border">
                                <label class="control-label col-md-3" for="supplierNumber">Supplier Agreement No.</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="supplierNumber"></p>
                                </div>

                                <label class="control-label col-md-3" for="mContactPerson">Maintenance Contact Person</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mContactPerson"></p>
                                </div>
                            </div>

                            <div class="form-group border">
                                <label class="control-label col-md-3" for="mPlan">Maintenance Plan</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mPlan"></p>
                                </div>

                                <label class="control-label col-md-3" for="mContactPhone">Maintenance Contact Phone</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mContactPhone"></p>
                                </div>
                            </div>
                            
                            <div class="form-group border">
                                <label class="control-label col-md-3" for="mJoinDate">Maintenance Join Date</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mJoinDate"></p>
                                </div>

                                <label class='control-label col-md-3' for="mContactFax">Maintenance Contact Fax</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mContactFax"></p>
                                </div>
                            </div>
                            
                            <div class="form-group border">
                                <label class="control-label col-md-3" for="mExpiryDate">Maintenance Expiry Date</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mExpiryDate"></p>
                                </div>

                                <label class='control-label col-md-3' for="mContactEmail">Maintenance Contact Email</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mContactEmail"></p>
                                </div>
                            </div>

                            <div class="form-group border">
                                <label class='control-label col-md-3' for="mInterval">Maintenance Interval (Months)</label>
                                <div class="col-md-3">
                                       <p class="control-label" id="mInterval"></p>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Confirmation modal -->
	<div class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" id="confirmationModal" data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog modal-sm">
		    <div class="modal-content">
		    	<div class="modal-header">
		        	<h4 class="modal-title">Continue?</h4>
		    	</div>
		    	<div class="modal-body" id="confirmationMsg"></div>
		    	<div class="modal-footer">
		        	<button type="button" class="btn btn-primary" id="confirmYesBTN">Yes</button>
		        	<button type="button" class="btn btn-default" id="confirmNoBTN">No</button>
		    	</div>
		    </div>
		</div>
	</div>
	
	<!-- Notification modal -->
	<div class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" id="notificationModal" data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog modal-sm">
		    <div class="modal-content">
		    	<div class="modal-header">
		        	<h4 class="modal-title">Note</h4>
		    	</div>
		    	<div class="modal-body" id="notificationMsg"></div>
		    	<div class="modal-footer">
		        	<button type="button" class="btn btn-primary" id="notifyOkay">OK</button>
		    	</div>
		    </div>
		</div>
	</div>
</html>