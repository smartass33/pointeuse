
<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.InAndOut" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
		<g:set var="entryName" value=""/>
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-employee" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list" params="${[isAdmin:isAdmin,siteId:siteId]}"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-employee" class="content scaffold-show" role="main">

				<h1><g:message code="employee.label" default="Last Name" />: <g:fieldValue bean="${employeeInstance}" field="firstName"/> <g:fieldValue bean="${employeeInstance}" field="lastName"/></h1>
				<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
				</g:if>
				
				<ol class="property-list employee">
			
				<g:if test="${employeeInstance?.firstName}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="employee.firstName.label" default="First Name" /></span>	
							<span class="property-value" aria-labelledby="firstname-label">${employeeInstance?.firstName}</span>		
					</li>
				</g:if>

				<g:if test="${employeeInstance?.lastName}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="employee.lastName.label" default="Last Name" /></span>	
							<span class="property-value" aria-labelledby="lastname-label">${employeeInstance?.lastName}</span>		
					</li>
				</g:if>

				<g:if test="${employeeInstance?.userName}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="employee.username.label" default="User Name" /></span>	
							<span class="property-value" aria-labelledby="username-label">${employeeInstance?.userName}</span>		
					</li>
				</g:if>
				<g:if test="${employeeInstance?.matricule}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="employee.matricule.label" default="Matricule" /></span>	
							<span class="property-value" aria-labelledby="matricule-label">${employeeInstance?.matricule}</span>		
					</li>
				</g:if>	

				<g:if test="${employeeInstance?.weeklyContractTime}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="employee.weeklyContractTime.label" default="weeklyContractTime" /></span>	
							<span class="property-value" aria-labelledby="weekly-contract-label">
							<g:formatNumber number="${employeeInstance?.weeklyContractTime}" type="number" format="###.##"/>
							</span>		
					</li>
				</g:if>	
				
				<g:if test="${employeeInstance?.arrivalDate}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="employee.arrivalDate.label" default="arrivalDate" /></span>	
							<span class="property-value" aria-labelledby="arrivalDate-label"><!--g:formatDate format="dd MM yyyy" value="${employeeInstance.arrivalDate}"/-->${employeeInstance?.arrivalDate.format('dd/MM/yyyy')}</span>									
					</li>
				</g:if>		
				
				<g:if test="${employeeInstance?.service}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="service.label" default="service" /></span>	
							<span class="property-value" aria-labelledby="service-label">${employeeInstance?.service.name}</span>		
					</li>
				</g:if>						
				<g:if test="${employeeInstance?.site}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="employee.site.label" default="site" /></span>	
							<span class="property-value" aria-labelledby="site-label">${employeeInstance?.site.name}</span>		
					</li>
				</g:if>				
				<g:if test="${employeeInstance?.function}">
					<li class="fieldcontain">
						<span id="calendar-label" class="property-label"><g:message code="function.label" default="function" /></span>	
							<span class="property-value" aria-labelledby="function-label">${employeeInstance?.function.name}</span>		
					</li>
				</g:if>									
			
			</ol>
				
				
			<g:form>
				<g:hiddenField name="userId" value="${employeeInstance?.id}" />
				<g:hiddenField name="siteId" value="${siteId}" />   	      							
				<fieldset class="buttons">				
					<g:link class="edit" action="edit"  id="${employeeInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
