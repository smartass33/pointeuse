<%@ page import="pointeuse.User"%>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main" />
		<resource:tabView />		
		<title>${grailsApplication.config.laboratory.name}</title>
		<script type="text/javascript">	
			function closePopup ( ){
				window.location = $('#closeId').attr('href');
			}
		
			function startTime(){
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
				
			function checkTime(i){
				if (i<10){i="0" + i;}
				return i;
			}
		</script>		
	</head>
	<body>
	    <g:javascript>window.onload = startTime();document.getElementById('mytextfield').focus();</g:javascript>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;" /></a>
		<div id=indexLeftMenu>
			<richui:tabView id="tabView">
	   			<richui:tabLabels>
			        <richui:tabLabel selected="true" title="${message(code: 'admin.menu', default: 'Create')}" />
			        <richui:tabLabel title="${message(code: 'config.menu', default: 'Create')}" />
	   	 		</richui:tabLabels>
	    		<richui:tabContents>
	        		<richui:tabContent>
						<sec:ifLoggedIn>
							<ul>
								<li class="controller"><g:link controller="employee" action='list' params="[isAdmin:false,max:20]" style="text-decoration: none;"><g:message code="employee.list" default="Last Name" /></g:link></li>
								<li class="controller"><g:link controller="bankHoliday" style="text-decoration: none;"><g:message code="bank.holiday.list" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="site" style="text-decoration: none;"><g:message code="site.list" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="site" action='siteTotalTime' style="text-decoration: none;"><g:message code="sites.followup" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="employee" action='dailyReport' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="daily.followup" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="employee" action='weeklyReport' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="weekly.followup" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="employee" action='absenceFollowup' params="[isAdmin:false,max:20]" style="text-decoration: none;"><g:message code="absence.report.followup" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="employee" action='vacationFollowup' params="[isAdmin:false,max:20]" style="text-decoration: none;"><g:message code="absence.followup" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="employee" action='ecartFollowup' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="ecart.followup" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="mileage" action='index' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="mileage.followup" default="Last Name" /></g:link></li>	
								<li class="controller"><g:link controller="payment" action='paymentReport' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="payment.management" default="Last Name" /></g:link></li>	
								<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">								
									<li class="controller"><g:link controller="authorization" action='index' params="[isAdmin:false,max:20,fromIndex:true]" style="text-decoration: none;"><g:message code="authorization.management" default="Last Name" /></g:link></li>	
								</sec:ifAnyGranted>
								<li><BR></li>
								<li class="controller"><g:link controller="logout" class="adminLogout"><g:message code="admin.logout.label" default="Last Name" /></g:link></li>							
							</ul>
						</sec:ifLoggedIn>
						<sec:ifNotLoggedIn>
							<g:link controller="login" class="adminLogin"><g:message code="admin.login.label" default="Last Name" /></g:link>
						</sec:ifNotLoggedIn>		
					</richui:tabContent>
					<richui:tabContent>
						<sec:ifLoggedIn>
							<ul>
								<li class="controller"><g:link controller="user" style="text-decoration: none;"><g:message code="admin.list" default="Last Name" /></g:link></li>						
								<li class="controller"><g:link controller="reason" style="text-decoration: none;"><g:message code="regularization.reasons" default="Last Name" /></g:link></li>			
								<li class="controller"><g:link controller="service" style="text-decoration: none;"><g:message code="service.list" default="Last Name" /></g:link></li>
								<li class="controller"><g:link controller="function" style="text-decoration: none;"><g:message code="function.list" default="Last Name" /></g:link></li>

								<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">		
									<li class="controller"><g:link controller="employeeDataListMap" style="text-decoration: none;"><g:message code="employeeDataListMap.management" default="Last Name" /></g:link></li>																							
									<li class="controller"><g:link controller="category" style="text-decoration: none;"><g:message code="authorizationType.management.label" default="Last Name" /></g:link></li>			
									<li class="controller"><g:link controller="subCategory" style="text-decoration: none;"><g:message code="authorizationNature.management.label" default="Last Name" /></g:link></li>			
								</sec:ifAnyGranted>
								<li><BR></li>
								<li class="controller"><g:link controller="logout" class="adminLogout"><g:message code="admin.logout.label" default="Last Name" /></g:link></li>							
							</ul>
						</sec:ifLoggedIn>
						<sec:ifNotLoggedIn>
							<g:link controller="login" class="adminLogin"><g:message code="admin.login.label" default="Last Name" /></g:link>
						</sec:ifNotLoggedIn>		
					</richui:tabContent>				
				</richui:tabContents>
			</richui:tabView>
		</div>
		<div class="standardNav" role="main">
			<h1><font size="5"><g:message code="welcome.label" default="Welcome"/> ${grailsApplication.config.laboratory.name}, il est: <span id='clock'><g:formatDate format="HH:mm:ss" date="${new Date()}"/></span>
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
