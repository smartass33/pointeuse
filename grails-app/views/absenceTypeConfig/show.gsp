
<%@ page import="pointeuse.AbsenceTypeConfig" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'absenceTypeConfig.label', default: 'AbsenceTypeConfig')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-absenceTypeConfig" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-absenceTypeConfig" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list absenceTypeConfig">
			
				<g:if test="${absenceTypeConfigInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="absenceTypeConfig.creationDate.label" default="Creation Date" /></span>
					
						<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${absenceTypeConfigInstance?.creationDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceTypeConfigInstance?.isPorportional}">
				<li class="fieldcontain">
					<span id="isPorportional-label" class="property-label"><g:message code="absenceTypeConfig.isPorportional.label" default="Is Porportional" /></span>
					
						<span class="property-value" aria-labelledby="isPorportional-label"><g:formatBoolean boolean="${absenceTypeConfigInstance?.isPorportional}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceTypeConfigInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="absenceTypeConfig.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${absenceTypeConfigInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceTypeConfigInstance?.shortName}">
				<li class="fieldcontain">
					<span id="shortName-label" class="property-label"><g:message code="absenceTypeConfig.shortName.label" default="Short Name" /></span>
					
						<span class="property-value" aria-labelledby="shortName-label"><g:fieldValue bean="${absenceTypeConfigInstance}" field="shortName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceTypeConfigInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="absenceTypeConfig.user.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label"><g:link controller="user" action="show" id="${absenceTypeConfigInstance?.user?.id}">${absenceTypeConfigInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${absenceTypeConfigInstance?.weight}">
				<li class="fieldcontain">
					<span id="weight-label" class="property-label"><g:message code="absenceTypeConfig.weight.label" default="Weight" /></span>
					
						<span class="property-value" aria-labelledby="weight-label"><g:fieldValue bean="${absenceTypeConfigInstance}" field="weight"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:absenceTypeConfigInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${absenceTypeConfigInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
