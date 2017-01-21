
<%@ page import="pointeuse.AnomalyType" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anomalyType.label', default: 'AnomalyType')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-anomalyType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-anomalyType" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list anomalyType">
			
				<g:if test="${anomalyTypeInstance?.creatingDate}">
				<li class="fieldcontain">
					<span id="creatingDate-label" class="property-label"><g:message code="anomalyType.creatingDate.label" default="Creating Date" /></span>
					
						<span class="property-value" aria-labelledby="creatingDate-label"><g:formatDate date="${anomalyTypeInstance?.creatingDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyTypeInstance?.creatingUser}">
				<li class="fieldcontain">
					<span id="creatingUser-label" class="property-label"><g:message code="anomalyType.creatingUser.label" default="Creating User" /></span>
					
						<span class="property-value" aria-labelledby="creatingUser-label"><g:link controller="user" action="show" id="${anomalyTypeInstance?.creatingUser?.id}">${anomalyTypeInstance?.creatingUser?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${anomalyTypeInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="anomalyType.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${anomalyTypeInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:anomalyTypeInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${anomalyTypeInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
