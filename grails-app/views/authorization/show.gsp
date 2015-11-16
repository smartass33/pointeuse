
<%@ page import="pointeuse.Authorization" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'authorization.label', default: 'Authorization')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-authorization" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-authorization" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list authorization">
			
				<g:if test="${authorizationInstance?.employee}">
				<li class="fieldcontain">
					<span id="employee-label" class="property-label"><g:message code="authorization.employee.label" default="Employee" /></span>
					
						<span class="property-value" aria-labelledby="employee-label"><g:link controller="employee" action="show" id="${authorizationInstance?.employee?.id}">${authorizationInstance?.employee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			

			
				<g:if test="${authorizationInstance?.isAuthorized}">
				<li class="fieldcontain">
					<span id="isAuthorized-label" class="property-label"><g:message code="authorization.isAuthorized.label" default="Is Authorized" /></span>
					
						<span class="property-value" aria-labelledby="isAuthorized-label"><g:formatBoolean boolean="${authorizationInstance?.isAuthorized}" /></span>
					
				</li>
				</g:if>
			

			
				<g:if test="${authorizationInstance?.startDate}">
				<li class="fieldcontain">
					<span id="startDate-label" class="property-label"><g:message code="authorization.startDate.label" default="Start Date" /></span>
					
						<span class="property-value" aria-labelledby="startDate-label"><g:formatDate date="${authorizationInstance?.startDate}" /></span>
					
				</li>
				</g:if>
				
				<g:if test="${authorizationInstance?.endDate}">
				<li class="fieldcontain">
					<span id="endDate-label" class="property-label"><g:message code="authorization.endDate.label" default="End Date" /></span>
					
						<span class="property-value" aria-labelledby="endDate-label"><g:formatDate date="${authorizationInstance?.endDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${authorizationInstance?.name}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="authorization.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${authorizationInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:authorizationInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${authorizationInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
