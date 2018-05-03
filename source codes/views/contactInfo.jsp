
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="hk.org.ha.eam.model.ContactInfoAddress"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>

<head>
	<script src="<spring:url value="/resources/js/moment.js?v=${initParam.buildTimeStamp}" /> " type="text/javascript"></script>
	<script src="<spring:url value="/resources/js/jquery.js?v=${initParam.buildTimeStamp}" />"></script>        
	<script src="<spring:url value="/resources/js/jquery-ui.js?v=${initParam.buildTimeStamp}" />"></script>        
	<script src="<spring:url value="/resources/js/datatables/datatables.min.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
	<script src="<spring:url value="/resources/js/datatables/DataTables-1.10.15/js/dataTables.bootstrap.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
	<script src="<spring:url value="/resources/js/datatables/colResize.js?v=${initParam.buildTimeStamp}" />" type="text/javascript"></script>
	<script src="<spring:url value="/resources/js/ha/pages/messageUI.js?v=${initParam.buildTimeStamp}" />"></script>
	<script src="<spring:url value="/resources/js/ha/pages/commonFunctions.js?v=${initParam.buildTimeStamp}" />"></script>
	<script src="<spring:url value="/resources/js/ha/pages/contactInfo.js?v=${initParam.buildTimeStamp}" />"></script>
	<script src="<spring:url value="/resources/js/datatables/tableProcessing.js?v=${initParam.buildTimeStamp}" />"></script>
	<link rel="stylesheet" href="<spring:url value="/resources/css/bootstrap.css?v=${initParam.buildTimeStamp}" />">
	<link rel="stylesheet" href="<spring:url value="/resources/css/custom.css?v=${initParam.buildTimeStamp}" />">
	<link rel="stylesheet" href="<spring:url value="/resources/css/jquery-ui.css?v=${initParam.buildTimeStamp}" />">
	<link rel="stylesheet" href="<spring:url value="/resources/css/bootstrapTable.css?v=${initParam.buildTimeStamp}" />">
	<link rel="stylesheet" href="<spring:url value="/resources/js/datatables/Select-1.2.2/css/select.bootstrap.css?v=${initParam.buildTimeStamp}" />">
	<link href="<spring:url value="/resources/js/datatables/DataTables-1.10.15/css/dataTables.bootstrap.css?v=${initParam.buildTimeStamp}" />" rel="stylesheet" type="text/css"/>
	<script>
		$(document).ready(function(){
			<% if(request.getParameterMap().isEmpty()){ %> 
				$('#addressTable .forDisabling').css({'pointer-events': 'none', 'cursor': 'default'});
			<% } %> 
		});
	</script>

    <style>
		[contentEditable=true]:empty:not(:focus):before{
			content:attr(data-text);
			color:gray
		}
		
		select::-ms-expand {
			display: none;
		}
		select{
			-webkit-appearance: none;
			-moz-appearance: none;      
			appearance: none;
			padding: 2px 30px 2px 2px;
			border: none; 
			background-color:white; 
		}
		.centerElement{
			margin-right: auto;
			margin-left: auto;
			color: #337ab7;
		}
		.empAdd, .empAddDate{
			background-color:white; 
			border: none;		
			width: 100%;
			box-sizing: border-box;
		}
		
    </style>
	
</head>

<!-- Employee Address -->

<div class="alert alert-danger " role="alert" id="errorAlert" style="display: none;"></div>            
<div class="panel panel-primary">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#employeeAddressContent" id="employeeAddress">Employee Address</a>
		</h4>
	</div>
		
	<div id="employeeAddressContent" class="panel-collapse collapse in">
		<div class="panel-body" style="padding: 8px 0px;">
			<div class="container-fluid col-md-12">	
				<div class="table-responsive">
				  <table class="table table-bordered" style="max-width: 100%" id="addressTable">
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
							<th>Primary</th>
							<th>Type</th>
							<th>From</th>
							<th>To</th>
							<th>Address</th>
							<th>District</th>
							<th>Area</th>
							<th>Country</th>
						</tr>
					</thead>
					<tbody id="addressTBody">
					<form:form class="employeeAddressForm" action="processEmpAddress" method="POST" modelAttribute="contactInfoAddress">  
						<tr class="addressTRow">
							<td>
								<a onclick="insertAddressLine()" href="#"><span class="glyphicon glyphicon-plus centerElement"/></a>
							</td>
							<td>
								<a class="forDisabling updateAddressRec" href="#"><span class="glyphicon glyphicon-edit centerElement"/></a>
							</td>
							<td>
								<a class="forDisabling deleteRec" href="#"><span class="glyphicon glyphicon-remove centerElement"/></a>
							</td>
							<td>
								<center><input type="checkbox" path="addrPrimary" class="addrPrimary" disabled="true"/></center>
							</td>
							<td><div class="addrTypeDiv"></div>
								<select path="addrType" class="form-control empAdd addrTypeSelect selectpicker" id="addrType" style="display:none"/>
							</td>
							<td><div class="addrFromDiv" style="display:none"></div>
								<form:input path="addrFrom"  class="form-control empAddDate dateType fromDate" id="addrFrom" disabled="true"/>
							</td>
							<td><div class="addrToDiv" style="display:none"></div>
								<form:input path="addrTo" class="form-control empAddDate dateType toDate" id="addrTo" disabled="true"/>
							</td>
							<td><div class="addrAddressDiv" style="display:none"></div>
								<form:input path="addrAddress"  class="form-control empAdd addressText" id="addrAddress"  maxlength="240" disabled="true"/>
							</td>
							<td><div class="addrDistrictDiv" style="display:none"></div>
								<form:input path="addrDistrict" class="form-control empAdd districtText" id="addrDistrict" maxlength="30" disabled="true"/>
							</td>
							<td><div class="addrAreaDiv" style="display:none"></div>
								<form:select path="addrArea" class="form-control empAdd selectpicker  areaSelect" id="addrArea" disabled="true">
									<form:option value="" label=" " />
									<form:options items="${addrAreaList}" itemValue="name" itemLabel="desc"/>
								</form:select>
							</td>
							<td><div class="addrCountryDiv" style="display:none"></div>
								<form:select path="addrCountry" class="form-control empAdd selectpicker countrySelect" id="addrCountry" disabled="true">
									<form:option value="" label=" " />
									<form:options items="${addrCountryList}" itemValue="name" itemLabel="desc"/>
								</form:select>
							</td>
						</tr>
					</form:form>
					</tbody>
				  </table>
				 </div>
			</div>
		</div>
	</div>
 </div> 
 
 
 <!-- Employee Phone Details -->
<form:form id="employeePhoneDetailsForm" action="processEmpPhoneDetails" method="POST" modelAttribute="empPhoneDetails">                    
<div class="panel panel-primary">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#employeePhoneDetailsContent" id="employeePhoneDetails">Employee Phone Details</a>
		</h4>
	</div>
	
	<div id="employeePhoneDetailsContent" class="panel-collapse collapse in">
		<div class="panel-body" style="padding: 8px 0px;">
			<div class="container-fluid col-md-12">		
				<div class="row">
					<div class="container-fluid col-md-10">	
						<div class="table-responsive">
						  <table class="table table-bordered">
							<thead>
								<tr>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<th>Type</th>
									<th>Phone Number</th>
									<th>From</th>
									<th>To</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td><a class="insertIcon" href="#"><span class="glyphicon glyphicon-plus" style="color:#337ab7;"/></a></td>
									<td><span class="glyphicon glyphicon-edit" style="color:#337ab7;"/></td>
									<td><span class="glyphicon glyphicon-remove" style="color:#337ab7;"/></td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td><input value="" class="empAdd" id="empAddCountry" style="background-color:white; border: none"/></td>
								</tr>
							</tbody>
						  </table>
						 </div>
					</div>
				</div>
				<div class="row">
					<div class="container-fluid col-md-10">
					</div>
					<div class="container-fluid col-md-2">.
						<button type="button" class="btn btn-primary" style="margin-right:100px;" id="addContact">Add Phone</button>
					</div>
				</div>
			</div>
		</div>
	</div>
 </div> 
 </form:form>
 
 
 
 <!-- Family and Emergency Contacts -->
<form:form id="famAndEmerContactsForm" action="processFamAndEmerContacts" method="POST" modelAttribute="famAndEmerContacts">                    
<div class="panel panel-primary">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#famAndEmerContactsContent" id="familyAndEmerContacts">Family and Emergency Contacts</a>
		</h4>
	</div>
	
	<div id="famAndEmerContactsContent" class="panel-collapse collapse in">
		<div class="panel-body" style="padding: 8px 0px;">
			<div class="container-fluid col-md-12">		
				<div class="row">
					<div class="container-fluid col-md-10">		
						<div class="table-responsive">
						  <table class="table table-bordered">
							<thead>
								<tr>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<th>Relationship</th>
									<th>From</th>
									<th>To</th>
									<th>Name</th>
									<th>Date of Birth</th>
									<th>Marital Status</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td><span class="glyphicon glyphicon-plus" style="color:#337ab7;"/></td>
									<td><span class="glyphicon glyphicon-edit" style="color:#337ab7;"/></td>
									<td><span class="glyphicon glyphicon-remove" style="color:#337ab7;"/></td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</tbody>
						  </table>
						 </div>
					</div>
				</div>
				<div class="row">
					<div class="container-fluid col-md-10">
					</div>
					<div class="container-fluid col-md-2">.
						<button type="button" class="btn btn-primary" style="margin-right:100px;" id="addContact">Add Contact</button>
					</div>
				</div>
			</div>
		</div>
	</div>
 </div> 
 </form:form>