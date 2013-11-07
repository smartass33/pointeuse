
<%@ page import="pointeuse.Vacation" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'vacation.label', default: 'Vacation')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-vacation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-vacation" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list vacation">
			
				<g:if test="${vacationInstance?.counter}">
				<li class="fieldcontain">
					<span id="counter-label" class="property-label"><g:message code="vacation.counter.label" default="Counter" /></span>
					
						<span class="property-value" aria-labelledby="counter-label"><g:fieldValue bean="${vacationInstance}" field="counter"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vacationInstance?.employee}">
				<li class="fieldcontain">
					<span id="employee-label" class="property-label"><g:message code="vacation.employee.label" default="Employee" /></span>
					
						<span class="property-value" aria-labelledby="employee-label"><g:link controller="employee" action="show" id="${vacationInstance?.employee?.id}">${vacationInstance?.employee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${vacationInstance?.loggingTime}">
				<li class="fieldcontain">
					<span id="loggingTime-label" class="property-label"><g:message code="vacation.loggingTime.label" default="Logging Time" /></span>
					
						<span class="property-value" aria-labelledby="loggingTime-label"><g:formatDate date="${vacationInstance?.loggingTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${vacationInstance?.user}">
				<li class="fieldcontain">
					<span id="loggingUser-label" class="property-label"><g:message code="vacation.user.label" default="Logging User" /></span>
					
						<span class="property-value" aria-labelledby="loggingUser-label"><g:link controller="user" action="show" id="${vacationInstance?.user?.id}">${vacationInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${vacationInstance?.year}">
				<li class="fieldcontain">
					<span id="period-label" class="property-label"><g:message code="vacation.period.label" default="Period" /></span>
					
						<span class="property-value" aria-labelledby="period-label">${vacationInstance?.year?.period}</span>
					
				</li>
				</g:if>
			
				<g:if test="${vacationInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="vacation.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${vacationInstance}" field="type"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${vacationInstance?.id}" />
					<g:link class="edit" action="edit" id="${vacationInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
