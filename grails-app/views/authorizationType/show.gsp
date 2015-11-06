
<%@ page import="pointeuse.AuthorizationType" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'authorizationType.label', default: 'AuthorizationType')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-authorizationType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-authorizationType" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list authorizationType">
			
				<g:if test="${authorizationTypeInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="authorizationType.creationDate.label" default="Creation Date" /></span>
					
						<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${authorizationTypeInstance?.creationDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${authorizationTypeInstance?.nature}">
				<li class="fieldcontain">
					<span id="nature-label" class="property-label"><g:message code="authorizationType.nature.label" default="Nature" /></span>
					
						<span class="property-value" aria-labelledby="nature-label"><g:link controller="authorizationNature" action="show" id="${authorizationTypeInstance?.nature?.id}">${authorizationTypeInstance?.nature?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${authorizationTypeInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="authorizationType.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${authorizationTypeInstance}" field="type"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${authorizationTypeInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="authorizationType.user.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label"><g:link controller="user" action="show" id="${authorizationTypeInstance?.user?.id}">${authorizationTypeInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:authorizationTypeInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${authorizationTypeInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
