
				<!-- Post ID for Medical Staff panel -->
                <form:form id="assignForm" action="processWorkRequest" method="POST" modelAttribute="workRequest">                    
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#postID4MedicalStaffContent" id="postID4MedicalStaff">Post ID for Medical Staff</a>
                        </h4>
                    </div>
                    
                    <div id="postID4MedicalStaffContent" class="panel-collapse collapse in">
                        <div class="panel-body" style="padding: 8px 0px;">
                        	<div class="container-fluid">
                        		<div class="table-responsive">
                        		  <table class="table table-bordered">
    								<thead>
    									<tr>
    										<th>Post ID</th>
    										<th>Start Date</th>
    										<th>End Date</th>
    									</tr>
    								</thead>
    								<tbody>
    									<tr>
    										<td>&nbsp;</td>
    										<td>&nbsp;</td>
    										<td>&nbsp;</td>
    									</tr>
    								</tbody>
    							  </table>
    							 </div>
                        	</div>
                        </div>
                    </div>
                 </div> 
                 </form:form>
                 
                    <!-- Primary Assignment panel -->
                 <form:form id="assignForm" action="processWorkRequest" method="POST" modelAttribute="workRequest">
                 <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#primyAssignContent" id="prmyAssign" style="margin-right:100px;">Primary Assignments</a>
                            <small style="color:yellow;">Displays important fields of the Assignment in the table.</small>
                        </h4>
                    </div>
                    
                    <div id="primyAssignContent" class="panel-collapse collapse in">
                       <div class="panel-body" style="padding: 8px 0px;">
                        	<div class="container-fluid">
                        		<div class="row" style="margin:10px;text-align:right;">
                        			<div class="form-inline">
	                        			Show &nbsp;
	                        			<select id="title" name="title" class="form-control selectpicker col-md-6">
	   										<option value="NONE">10</option> 
										</select>
										&nbsp;entries &nbsp;&nbsp;
										<a href="#export"><span class="glyphicon glyphicon-export" style="color:black;"></span>Export</a>
                        			</div>
                        		</div>
                        		<div class="table-responsive">
                        		  <table class="table table-bordered">
                        		  <thead>
                        		  	<tr>
                        		  		<th>&nbsp;</th>
                        		  		<th>&nbsp;</th>
                        		  		<th>&nbsp;</th>
                        		  		<th>Assg No</th>
                        		  		<th>Start Date</th>
                        		  		<th>End Date</th>
                        		  		<th>Position</th>
                        		  		<th>Organization</th>
                        		  		<th>Grade</th>
                        		  		<th>Staff Group</th>
                        		  		<th>Emp Cat</th>
                        		  		<th>Loc</th>
                        		  	</tr>
                        		  </thead>
                        		  <tbody>
                        		  	<tr>
                        		  		<td><input type="radio"></td>
                        		  		<td><span class="glyphicon glyphicon-edit" style="color:blue;"></span></td>
                        		  		<td><span class="glyphicon glyphicon-remove" style="color:red;"></span></td>
                        		  		<td>&nbsp;</td>
                        		  		<td>&nbsp;</td>
                        		  		<td>&nbsp;</td>
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
                    </div>
                </div>
                </form:form>
                
                
                 <!-- Secondary Assignment panel -->
                 <form:form id="assignForm" action="processWorkRequest" method="POST" modelAttribute="workRequest">
                 <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#secAssignContent" id="secAssign" style="margin-right:100px;">Secondary Assignments</a>
                        </h4>
                    </div>
                    
                    <div id="secAssignContent" class="panel-collapse collapse in">
                       <div class="panel-body" style="padding: 8px 0px;">
                        	<div class="container-fluid">
                        		<div class="table-responsive">
                        		  <table class="table table-bordered">
                        		  <thead>
                        		  	<tr>
                        		  		<th>&nbsp;</th>
                        		  		<th>&nbsp;</th>
                        		  		<th>&nbsp;</th>
                        		  		<th>Assg No</th>
                        		  		<th>Start Date</th>
                        		  		<th>End Date</th>
                        		  		<th>Position</th>
                        		  		<th>Organization</th>
                        		  		<th>Grade</th>
                        		  		<th>Staff Group</th>
                        		  		<th>Emp Cat</th>
                        		  		<th>Loc</th>
                        		  	</tr>
                        		  </thead>
                        		  <tbody>
                        		  	<tr>
                        		  		<td><input type="radio"></td>
                        		  		<td><span class="glyphicon glyphicon-edit" style="color:blue;"></span></td>
                        		  		<td><span class="glyphicon glyphicon-remove" style="color:red;"></span></td>
                        		  		<td>&nbsp;</td>
                        		  		<td>&nbsp;</td>
                        		  		<td>&nbsp;</td>
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
                    </div>
                </div>
                </form:form>
                
				<!-- Term of Appointment panel -->
                <form:form id="assignForm" action="processWorkRequest" method="POST" modelAttribute="workRequest">                    
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a data-toggle="collapse" class="accordion-toggle" data-parent="#accordion" href="#termAppmtContent" id="termAppmt">Term of Appointment</a>
                        </h4>
                    </div>
                    
                    <div id="termAppmtContent" class="panel-collapse collapse in">
                        <div class="panel-body" style="padding: 8px 0px;">
                        	<div class="container-fluid">
                        		<div class="table-responsive">
                        		  <table class="table table-bordered">
    								<thead>
    									<tr>
    										<th>Term</th>
    										<th>Start Date</th>
    										<th>End Date</th>
    									</tr>
    								</thead>
    								<tbody>
    									<tr>
    										<td>&nbsp;</td>
    										<td>&nbsp;</td>
    										<td>&nbsp;</td>
    									</tr>
    								</tbody>
    							  </table>
    							</div>
                        	</div>
                        </div>
                    </div>
                 </div> 
                 </form:form>
                
                