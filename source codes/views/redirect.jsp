<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
	<title>Session Expired</title>
    <script>
		var seconds = 6;
		var url= '${param.redirect_to}';
		
		function redirect(){
			 if (seconds <=1){
			 	window.location = url;
			 }
			 else{
				seconds--;
			 	document.getElementById('seconds').innerHTML = seconds;
			 	setTimeout("redirect()", 1000);
			 }
		}
	</script>
</head>
<body>
	<div id="pageInfo">
		<h2>Your Session Has Expired</h2>
		<p>Redirecting to login in <span id="seconds"></span> seconds. <a href='${param.redirect_to}'>Click here</a> to login now.</p>
	</div>
	
	<script>
		redirect();
	</script>
	</body>
</html>