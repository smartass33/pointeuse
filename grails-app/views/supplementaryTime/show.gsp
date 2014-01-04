
<%@ page import="pointeuse.SupplementaryTime" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'supplementaryTime.label', default: 'SupplementaryTime')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-supplementaryTime" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-supplementaryTime" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list supplementaryTime">
			
				<g:if test="${supplementaryTimeInstance?.employee}">
				<li class="fieldcontain">
					<span id="employee-label" class="property-label"><g:message code="supplementaryTime.employee.label" default="Employee" /></span>
					
						<span class="property-value" aria-labelledby="employee-label"><g:link controller="employee" action="show" id="${supplementaryTimeInstance?.employee?.id}">${supplementaryTimeInstance?.employee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${supplementaryTimeInstance?.loggingTime}">
				<li class="fieldcontain">
					<span id="loggingTime-label" class="property-label"><g:message code="supplementaryTime.loggingTime.label" default="Logging Time" /></span>
					
						<span class="property-value" aria-labelledby="loggingTime-label"><g:formatDate date="${supplementaryTimeInstance?.loggingTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${supplementaryTimeInstance?.period}">
				<li class="fieldcontain">
					<span id="period-label" class="property-label"><g:message code="supplementaryTime.period.label" default="Period" /></span>
					
						<span class="property-value" aria-labelledby="period-label"><g:link controller="period" action="show" id="${supplementaryTimeInstance?.period?.id}">${supplementaryTimeInstance?.period?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${supplementaryTimeInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="supplementaryTime.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${supplementaryTimeInstance}" field="type"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${supplementaryTimeInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="supplementaryTime.user.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label"><g:link controller="user" action="show" id="${supplementaryTimeInstance?.user?.id}">${supplementaryTimeInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${supplementaryTimeInstance?.value}">
				<li class="fieldcontain">
					<span id="value-label" class="property-label"><g:message code="supplementaryTime.value.label" default="Value" /></span>
					
						<span class="property-value" aria-labelledby="value-label"><g:fieldValue bean="${supplementaryTimeInstance}" field="value"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${supplementaryTimeInstance?.id}" />
					<g:link class="edit" action="edit" id="${supplementaryTimeInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
