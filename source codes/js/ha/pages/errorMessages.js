/* --------------------------------------
    File Name messageUI.js
    Author Frankie Lee (PCCW)
    Date January ‎5, ‎2018
    Description
    Define the message used in front-end Javascript
   -------------------------------------- -*/
  
  
$(document).ready(function(){
	var err_messages = {};
});
 


function generateError(alertId, errorObj){
	alert('entered generateError fxn');
	alert('err_messages:' + err_messages);
	$.ajax({
		type : "POST",
		url : "./getErrorMessages",
		data : JSON.stringify(errorObj),
		contentType: 'application/json',
		success: function(data){
	
			$('#' + alertId).removeClass("alert-success");
			$('#' + alertId).removeClass("alert-warning");
			$('#' + alertId).addClass("alert-danger");	
			var errors = ""
			for(var i = 0; i < data.length; i++){
				errors += "<strong><span class='glyphicon glyphicon-exclamation-sign'></span> Error!</strong> " + data[i] + "<br>";
			}
			$('#' + alertId).html(errors);	
		}
	});
	err_messages = {};
}

function addErrorParams(errorCode, applicationName){
	var err = err_messages.push({'errorCode' : errorCode, 'applicationName' : applicationName});
	alert(JSON.stringify(err));
	return true;
}