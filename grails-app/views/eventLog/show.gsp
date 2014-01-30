
<%@ page import="pointeuse.EventLog" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'eventLog.label', default: 'EventLog')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-eventLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-eventLog" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list eventLog">
			
				<g:if test="${eventLogInstance?.source}">
				<li class="fieldcontain">
					<span id="source-label" class="property-label"><g:message code="eventLog.source.label" default="Source" /></span>
					
						<span class="property-value" aria-labelledby="source-label"><g:fieldValue bean="${eventLogInstance}" field="source"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${eventLogInstance?.message}">
				<li class="fieldcontain">
					<span id="message-label" class="property-label"><g:message code="eventLog.message.label" default="Message" /></span>
					
						<span class="property-value" aria-labelledby="message-label"><g:fieldValue bean="${eventLogInstance}" field="message"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${eventLogInstance?.details}">
				<li class="fieldcontain">
					<span id="details-label" class="property-label"><g:message code="eventLog.details.label" default="Details" /></span>
					
						<span class="property-value" aria-labelledby="details-label"><g:fieldValue bean="${eventLogInstance}" field="details"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${eventLogInstance?.cleared}">
				<li class="fieldcontain">
					<span id="cleared-label" class="property-label"><g:message code="eventLog.cleared.label" default="Cleared" /></span>
					
						<span class="property-value" aria-labelledby="cleared-label"><g:formatBoolean boolean="${eventLogInstance?.cleared}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${eventLogInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="eventLog.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${eventLogInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:eventLogInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${eventLogInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
