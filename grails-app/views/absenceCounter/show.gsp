
<%@ page import="pointeuse.AbsenceCounter" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'absenceCounter.label', default: 'AbsenceCounter')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-absenceCounter" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-absenceCounter" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list absenceCounter">
			
				<g:if test="${absenceCounterInstance?.logging_time}">
				<li class="fieldcontain">
					<span id="logging_time-label" class="property-label"><g:message code="absenceCounter.logging_time.label" default="Loggingtime" /></span>
					
						<span class="property-value" aria-labelledby="logging_time-label"><g:formatDate date="${absenceCounterInstance?.logging_time}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceCounterInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="absenceCounter.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${absenceCounterInstance}" field="type"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceCounterInstance?.employee}">
				<li class="fieldcontain">
					<span id="employee-label" class="property-label"><g:message code="absenceCounter.employee.label" default="Employee" /></span>
					
						<span class="property-value" aria-labelledby="employee-label"><g:link controller="employee" action="show" id="${absenceCounterInstance?.employee?.id}">${absenceCounterInstance?.employee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceCounterInstance?.year}">
				<li class="fieldcontain">
					<span id="year-label" class="property-label"><g:message code="absenceCounter.year.label" default="Year" /></span>
					
						<span class="property-value" aria-labelledby="year-label"><g:fieldValue bean="${absenceCounterInstance}" field="year"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceCounterInstance?.counter}">
				<li class="fieldcontain">
					<span id="counter-label" class="property-label"><g:message code="absenceCounter.counter.label" default="Counter" /></span>
					
						<span class="property-value" aria-labelledby="counter-label"><g:fieldValue bean="${absenceCounterInstance}" field="counter"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceCounterInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="absenceCounter.user.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label"><g:link controller="user" action="show" id="${absenceCounterInstance?.user?.id}">${absenceCounterInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${absenceCounterInstance?.id}" />
					<g:link class="edit" action="edit" id="${absenceCounterInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
