<%@ page import="pointeuse.User"%>

<!DOCTYPE html>
<html>
	<head>
	<g:javascript library="prototype" plugin="prototype"/>
		<meta name="layout" content="main" />
		<title>Pointeuse BIOLAB33</title>
		<style type="text/css" media="screen">
		
		
		input.loginbutton {
   border-top: 1px solid #c7c7c7;
   background: #e8edf0;
   background: -webkit-gradient(linear, left top, left bottom, from(#6f706f), to(#e8edf0));
   background: -webkit-linear-gradient(top, #6f706f, #e8edf0);
   background: -moz-linear-gradient(top, #6f706f, #e8edf0);
   background: -ms-linear-gradient(top, #6f706f, #e8edf0);
   background: -o-linear-gradient(top, #6f706f, #e8edf0);
   padding: 7px 14px;
   -webkit-border-radius: 11px;
   -moz-border-radius: 11px;
   border-radius: 11px;
   -webkit-box-shadow: rgba(0,0,0,1) 0 1px 0;
   -moz-box-shadow: rgba(0,0,0,1) 0 1px 0;
   box-shadow: rgba(0,0,0,1) 0 1px 0;
   text-shadow: rgba(0,0,0,.4) 0 1px 0;
   color: white;
   font-size: 20px;
   font-family: Helvetica, Arial, Sans-Serif;
   text-decoration: none;
   vertical-align: middle;
   }
input.loginbutton:hover {
   border-top-color: #929ca3;
   background: #929ca3;
   color: #fffaff;
   }
input.loginbutton:active {
   border-top-color: #1b435e;
   background: #1b435e;
   }
		
		
	
	#td {
		vertical-alogn: center;
	}
	
	#status {
		background-color: #eee;
		border: .2em solid #fff;
		margin: 2em 2em 1em;
		padding: 1em;
		width: 12em;
		float: left;
		-moz-box-shadow: 0px 0px 1.25em #ccc;
		-webkit-box-shadow: 0px 0px 1.25em #ccc;
		box-shadow: 0px 0px 1.25em #ccc;
		-moz-border-radius: 0.6em;
		-webkit-border-radius: 0.6em;
		border-radius: 0.6em;
	}
	
	.ie6 #status {
		display: inline;
		/* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
	}
	
	#status ul {
		font-size: 0.9em;
		list-style-type: none;
		margin-bottom: 0.6em;
		padding: 0;
	}
	
	#status li {
		line-height: 1.3;
	}
	
	#status h1 {
		text-transform: uppercase;
		font-size: 1.1em;
		margin: 0 0 0.3em;
	}
	
	#page-body {
		margin: 2em 1em 1.25em 18em;
	}
	
	h2 {
		margin-top: 1em;
		margin-bottom: 0.3em;
		font-size: 1em;
	}
	
	p {
		line-height: 1.5;
		margin: 0.25em 0;
	}
	
	#controller-list ul {
		list-style-position: inside;
	}
	
	#controller-list li {
		line-height: 1.3;
		list-style-position: inside;
		margin: 0.25em 0;
	}
	
	@media screen and (max-width: 480px) {
		#status {
			display: none;
		}
		#page-body {
			margin: 0 1em 1em;
		}
		#page-body h1 {
			margin-top: 0;
		}
	}
	
	#login {
		margin: 15px 0px;
		padding: 0px;
		text-align: center;
	}
	
	#login .inner {
		width: 340px;
		padding-bottom: 6px;
		margin: 60px auto;
		text-align: left;
		border: 1px solid #aab;
		background-color: #f0f0fa;
		-moz-box-shadow: 2px 2px 2px #eee;
		-webkit-box-shadow: 2px 2px 2px #eee;
		-khtml-box-shadow: 2px 2px 2px #eee;
		box-shadow: 2px 2px 2px #eee;
	}
	
	#login .inner .fheader {
		padding: 18px 26px 14px 26px;
		background-color: #f7f7ff;
		margin: 0px 0 14px 0;
		color: #2e3741;
		font-size: 18px;
		font-weight: bold;
	}
	
	#login .inner .cssform p {
		clear: left;
		margin: 0;
		padding: 4px 0 3px 0;
		padding-left: 105px;
		margin-bottom: 20px;
		height: 1%;
	}
	
	#login .inner .cssform input[type='text'] {
		width: 120px;
	}
	
	#login .inner .cssform label {
		font-weight: bold;
		float: left;
		text-align: right;
		margin-left: -105px;
		width: 110px;
		padding-top: 3px;
		padding-right: 10px;
	}
	
	#login #remember_me_holder {
		padding-left: 120px;
	}
	
	#login #submit {
		margin-left: 15px;
	}
	
	#login #remember_me_holder label {
		float: none;
		margin-left: 0;
		text-align: left;
		width: 200px
	}
	
	#login .inner .login_message {
		padding: 6px 25px 20px 25px;
		color: #c33;
	}
	
	#login .inner .text_ {
		width: 120px;
	}
	
	#login .inner .chk {
		height: 12px;
	}
	
	
	
	</style>
	<script type="text/javascript">
	
	function updateClock ( )
	{
	  var currentTime = new Date ( );
	
	  var currentHours = currentTime.getHours ( );
	  var currentMinutes = currentTime.getMinutes ( );
	  var currentSeconds = currentTime.getSeconds ( );
	
	  // Pad the minutes and seconds with leading zeros, if required
	  currentMinutes = ( currentMinutes < 10 ? "0" : "" ) + currentMinutes;
	  currentSeconds = ( currentSeconds < 10 ? "0" : "" ) + currentSeconds;
	
	  // Compose the string for display
	  var currentTimeString = currentHours + ":" + currentMinutes + ":" + currentSeconds  //+ " " + timeOfDay;
	
	  // Update the time display
	   var elem = document.getElementById('clock');
	   if (null!=elem){
	     document.getElementById('clock').firstChild.nodeValue = currentTimeString;
	   }else{
	   	setInterval('updateClock()', 1000 );
	   }
	}
	$(document).ready(function(){
	 
	        $(".slidingDiv").hide();
	        $(".show_hide").show();
	 
	    $('.show_hide').click(function(){
	    $(".slidingDiv").slideToggle();
	    });
	 
	});
	
	$(window).load(function(){
	$('#right').bind('contextmenu', function(e){
	e.preventDefault();
	alert('hi there!');
	return false;
	})
	});
	
	</script>
	
	</head>
	<body>	
	    <g:javascript>window.onload = updateClock();document.getElementById('mytextfield').focus();</g:javascript>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;" /></a>
		<div id="status" role="complementary">
			<h2>menu administrateur</h2>
			<sec:ifLoggedIn>
			<ul>
				<li class="controller"><g:link controller="employee" action='list' params="[isAdmin:false,max:20]">Liste des employés</g:link></li>
				<li class="controller"><g:link controller="employee" action='list' params="[isAdmin:true,max:20]">Liste administrative</g:link></li>
				<li class="controller"><g:link controller="user">Liste des administrateurs</g:link></li>
				<li class="controller"><g:link controller="bankHoliday">Liste des jours fériés</g:link></li>	
				<li class="controller"><g:link controller="site">Liste des sites</g:link></li>	
				<li class="controller"><g:link controller="service">Liste des services</g:link></li>
				<li class="controller"><g:link controller="reason">Raisons de régularisation</g:link></li>		
				<li><BR></li>
				<li class="controller"><g:link controller="logout">Déconnectez vous</g:link></li>							
			</ul>
			</sec:ifLoggedIn>
			<sec:ifNotLoggedIn>
				<g:link controller="login">identifiez vous</g:link>
			</sec:ifNotLoggedIn>					
		</div>
		<div id="page-body" role="main">
			<h1><font size="5"><g:message code="welcome.label" default="Welcome"/>, il est: <span id='clock'><g:formatDate format="HH:mm:ss" date="${new Date()}"/></span>
			</font></h1>
			<p><g:message code="explanation.label" default="Welcome"/></p>
			<g:if test="${flash.message}">
	        	<div class="message">${flash.message}</div>
	      	</g:if>
	  		<form id="myform" method="POST" action="employee/pointage" controller="employee">
	  			<font size="5">	${message(code: 'employee.id', default: 'Create')}: <g:textField id="mytextfield" name="username" autofocus="true" style="vertical-align: middle;" />  	</font>	  				  						
				<input type="submit" class="loginbutton" value="${message(code: 'default.button.login.label', default: 'Create')}" style="vertical-align: middle;">
			</form>		
		</div>
	</body>
</html>
