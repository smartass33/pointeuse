
<%@ page import="pointeuse.Mileage" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'mileage.label', default: 'Milage')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-milage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-milage" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list milage">
			
				<g:if test="${milageInstance?.employee}">
				<li class="fieldcontain">
					<span id="employee-label" class="property-label"><g:message code="milage.employee.label" default="Employee" /></span>
					
						<span class="property-value" aria-labelledby="employee-label"><g:link controller="employee" action="show" id="${milageInstance?.employee?.id}">${milageInstance?.employee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${milageInstance?.loggingTime}">
				<li class="fieldcontain">
					<span id="loggingTime-label" class="property-label"><g:message code="milage.loggingTime.label" default="Logging Time" /></span>
					
						<span class="property-value" aria-labelledby="loggingTime-label"><g:formatDate date="${milageInstance?.loggingTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${milageInstance?.month}">
				<li class="fieldcontain">
					<span id="month-label" class="property-label"><g:message code="milage.month.label" default="Month" /></span>
					
						<span class="property-value" aria-labelledby="month-label"><g:fieldValue bean="${milageInstance}" field="month"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${milageInstance?.period}">
				<li class="fieldcontain">
					<span id="period-label" class="property-label"><g:message code="milage.period.label" default="Period" /></span>
					
						<span class="property-value" aria-labelledby="period-label"><g:link controller="period" action="show" id="${milageInstance?.period?.id}">${milageInstance?.period?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${milageInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="milage.user.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label"><g:link controller="user" action="show" id="${milageInstance?.user?.id}">${milageInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${milageInstance?.value}">
				<li class="fieldcontain">
					<span id="value-label" class="property-label"><g:message code="milage.value.label" default="Value" /></span>
					
						<span class="property-value" aria-labelledby="value-label"><g:fieldValue bean="${milageInstance}" field="value"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${milageInstance?.year}">
				<li class="fieldcontain">
					<span id="year-label" class="property-label"><g:message code="milage.year.label" default="Year" /></span>
					
						<span class="property-value" aria-labelledby="year-label"><g:fieldValue bean="${milageInstance}" field="year"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:milageInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${milageInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
