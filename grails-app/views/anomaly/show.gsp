
<%@ page import="pointeuse.Anomaly" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anomaly.label', default: 'Anomaly')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-anomaly" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-anomaly" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list anomaly">
			
				<g:if test="${anomalyInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="anomaly.creationDate.label" default="Creation Date" /></span>
					
						<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${anomalyInstance?.creationDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="anomaly.user.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label"><g:link controller="user" action="show" id="${anomalyInstance?.user?.id}">${anomalyInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyInstance?.RAS}">
				<li class="fieldcontain">
					<span id="RAS-label" class="property-label"><g:message code="anomaly.RAS.label" default="RAS" /></span>
					
						<span class="property-value" aria-labelledby="RAS-label"><g:formatBoolean boolean="${anomalyInstance?.RAS}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="anomaly.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:link controller="anomalyType" action="show" id="${anomalyInstance?.type?.id}">${anomalyInstance?.type?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="anomaly.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${anomalyInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="anomaly.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${anomalyInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyInstance?.employee}">
				<li class="fieldcontain">
					<span id="employee-label" class="property-label"><g:message code="anomaly.employee.label" default="Employee" /></span>
					
						<span class="property-value" aria-labelledby="employee-label"><g:link controller="employee" action="show" id="${anomalyInstance?.employee?.id}">${anomalyInstance?.employee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyInstance?.site}">
				<li class="fieldcontain">
					<span id="site-label" class="property-label"><g:message code="anomaly.site.label" default="Site" /></span>
					
						<span class="property-value" aria-labelledby="site-label"><g:link controller="site" action="show" id="${anomalyInstance?.site?.id}">${anomalyInstance?.site?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:anomalyInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${anomalyInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
