<%@ page import="pointeuse.User"%>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main" />
		<title>Pointeuse BIOLAB33</title>
		<style type="text/css" media="screen">
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
		<div id=indexLeftMenu>
			<h2><g:message code="admin.menu" default="Last Name" /></h2>
			<sec:ifLoggedIn>
			<ul>
				<li class="controller"><g:link controller="employee" action='list' params="[isAdmin:false,max:20]" style="text-decoration: none;"><g:message code="employee.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="user" style="text-decoration: none;"><g:message code="admin.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="bankHoliday" style="text-decoration: none;"><g:message code="bank.holiday.list" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="site" style="text-decoration: none;"><g:message code="site.list" default="Last Name" /></g:link></li>	

				<li class="controller"><g:link controller="service" style="text-decoration: none;"><g:message code="service.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="function" style="text-decoration: none;"><g:message code="function.list" default="Last Name" /></g:link></li>
				<li class="controller"><g:link controller="site" action='siteTotalTime' style="text-decoration: none;">Suivi des sites</g:link></li>	

				<li class="controller"><g:link controller="employee" action='dailyReport' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="daily.followup" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="employee" action='vacationFollowup' params="[isAdmin:false,max:20]" style="text-decoration: none;"><g:message code="absence.followup" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="employee" action='ecartFollowup' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="ecart.followup" default="Last Name" /></g:link></li>	
				<li class="controller"><g:link controller="payment" action='paymentReport' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="payment.management" default="Last Name" /></g:link></li>	
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
		  			<li><font size="5">	${message(code: 'employee.id', default: 'Create')}: <g:textField id="mytextfield" name="username" autofocus="true" style="vertical-align: middle;" value="" /></font></li>	  				  						
					<li><g:actionSubmit value="${message(code: 'default.button.login.label', default: 'Create')}" action="pointage" class="indexLoginButton" style="vertical-align: middle;"/></li>
				</ul>
			</form>		
		</div>
	</body>
</html>
