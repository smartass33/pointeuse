
<%@ page import="pointeuse.AuthorizationNature" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'authorizationNature.label', default: 'AuthorizationNature')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-authorizationNature" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-authorizationNature" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list authorizationNature">
			

			
				<g:if test="${authorizationNatureInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="authorizationNature.creationDate.label" default="Creation Date" /></span>
					
						<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${authorizationNatureInstance?.creationDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${authorizationNatureInstance?.nature}">
				<li class="fieldcontain">
					<span id="nature-label" class="property-label"><g:message code="authorizationNature.nature.label" default="Nature" /></span>
					
						<span class="property-value" aria-labelledby="nature-label"><g:fieldValue bean="${authorizationNatureInstance}" field="nature"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${authorizationNatureInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="authorizationNature.user.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label"><g:link controller="user" action="show" id="${authorizationNatureInstance?.user?.id}">${authorizationNatureInstance?.user?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:authorizationNatureInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${authorizationNatureInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
