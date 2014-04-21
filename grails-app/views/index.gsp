<%@ page import="pointeuse.User"%>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main" />
		<title>Pointeuse BIOLAB33</title>
		<style type="text/css" media="screen">
	
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
	
.classname {
	-moz-box-shadow:inset 0px 1px 0px 0px #ffffff;
	-webkit-box-shadow:inset 0px 1px 0px 0px #ffffff;
	box-shadow:inset 0px 1px 0px 0px #ffffff;
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ededed), color-stop(1, #bdbdbd) );
	background:-moz-linear-gradient( center top, #ededed 5%, #bdbdbd 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#ededed', endColorstr='#bdbdbd');
	background-color:#ededed;
	-moz-border-radius:6px;
	-webkit-border-radius:6px;
	border-radius:6px;
	border:1px solid #dcdcdc;
	display:inline-block;
	color:#333233;
	font-family:arial;
	font-size:15px;
	font-weight:bold;
	padding:6px 24px;
	text-decoration:none;
	text-shadow:1px 1px 0px #ffffff;
}.classname:hover {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #bdbdbd), color-stop(1, #ededed) );
	background:-moz-linear-gradient( center top, #bdbdbd 5%, #ededed 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#bdbdbd', endColorstr='#ededed');
	background-color:#bdbdbd;
}.classname:active {
	position:relative;
	top:1px;
}
/* This imageless css button was generated by CSSButtonGenerator.com */
	/* css for timepicker */
.ui-timepicker-div dl{ text-align: left; }
.ui-timepicker-div dl dt{ height: 25px; }
.ui-timepicker-div dl dd{ margin: -25px 0 10px 65px; }
	</style>

	  
	<script type="text/javascript">

function startTime()
{
var today=new Date();
var h=today.getHours();
var m=today.getMinutes();
var s=today.getSeconds();
// add a zero in front of numbers<10
m=checkTime(m);
s=checkTime(s);
document.getElementById('clock').innerHTML=h+":"+m+":"+s;
t=setTimeout(function(){startTime()},500);
}

function checkTime(i)
{
if (i<10)
  {
  i="0" + i;
  }
return i;
}


	</script>

	
	</head>
	<body>
	    <g:javascript>window.onload = startTime();document.getElementById('mytextfield').focus();</g:javascript>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;" /></a>
		<div id="status" role="complementary">
			<h2><g:message code="admin.menu" default="Last Name" /></h2>
			<sec:ifLoggedIn>
			<ul>
				<li class="controller"><g:link controller="employee" action='list' params="[isAdmin:false,max:20]" style="text-decoration: none;"><g:message code="employee.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="employee" action='list' params="[isAdmin:true,max:20]" style="text-decoration: none;"><g:message code="employee.admin.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="user" style="text-decoration: none;"><g:message code="admin.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="bankHoliday" style="text-decoration: none;"><g:message code="bank.holiday.list" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="site" style="text-decoration: none;"><g:message code="site.list" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="service" style="text-decoration: none;"><g:message code="service.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="function" style="text-decoration: none;"><g:message code="function.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="employee" action='dailyReport' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="daily.followup" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="employee" action='vacationFollowup' params="[isAdmin:false,max:20]" style="text-decoration: none;"><g:message code="absence.followup" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="employee" action='ecartFollowup' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="ecart.followup" default="Last Name" /></g:link></li>	

				<li class="controller"><g:link controller="reason" style="text-decoration: none;"><g:message code="regularization.reasons" default="Last Name" /></g:link></li>		
				<li><BR></li>
				<li class="controller"><g:link controller="logout" class="adminLogout"><g:message code="admin.logout.label" default="Last Name" /></g:link></li>							
			</ul>
			</sec:ifLoggedIn>
			<sec:ifNotLoggedIn>
				<g:link controller="login" class="adminLogin"><g:message code="admin.login.label" default="Last Name" /></g:link>
			</sec:ifNotLoggedIn>					
		</div>
		<div class="standardNav" role="main">
			<h1><font size="5"><g:message code="welcome.label" default="Welcome"/>, il est: <span id='clock'><g:formatDate format="HH:mm:ss" date="${new Date()}"/></span>
			</font></h1>
			<BR>
			<p><g:message code="explanation.label" default="Welcome"/></p>
						
			<BR>
			<g:if test="${flash.message}">
	        	<div class="message">${flash.message}</div>
	      	</g:if>      	
	  		<form id="myform" method="POST" action="employee/pointage" controller="employee">
		  		<ul >
		  			<li><font size="5">	${message(code: 'employee.id', default: 'Create')}: <g:textField id="mytextfield" name="username" autofocus="true" style="vertical-align: middle;" />  	</font></li>	  				  						
					<li><g:actionSubmit value="${message(code: 'default.button.login.label', default: 'Create')}" action="pointage" class="indexLoginButton" style="vertical-align: middle;"/></li>
				</ul>
			</form>		
		</div>
	</body>
</html>
