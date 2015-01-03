
<%@ page import="pointeuse.CardTerminal" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'cardTerminal.label', default: 'CardTerminal')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-cardTerminal" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-cardTerminal" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list cardTerminal">
			
				<g:if test="${cardTerminalInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="cardTerminal.creationDate.label" default="Creation Date" /></span>
					
						<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${cardTerminalInstance?.creationDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${cardTerminalInstance?.hostname}">
				<li class="fieldcontain">
					<span id="hostname-label" class="property-label"><g:message code="cardTerminal.hostname.label" default="Hostname" /></span>
					
						<span class="property-value" aria-labelledby="hostname-label"><g:fieldValue bean="${cardTerminalInstance}" field="hostname"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${cardTerminalInstance?.ip}">
				<li class="fieldcontain">
					<span id="ip-label" class="property-label"><g:message code="cardTerminal.ip.label" default="Ip" /></span>
					
						<span class="property-value" aria-labelledby="ip-label"><g:fieldValue bean="${cardTerminalInstance}" field="ip"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${cardTerminalInstance?.lastKeepAlive}">
				<li class="fieldcontain">
					<span id="lastKeepAlive-label" class="property-label"><g:message code="cardTerminal.lastKeepAlive.label" default="Last Keep Alive" /></span>
					
						<span class="property-value" aria-labelledby="lastKeepAlive-label"><g:formatDate date="${cardTerminalInstance?.lastKeepAlive}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${cardTerminalInstance?.site}">
				<li class="fieldcontain">
					<span id="site-label" class="property-label"><g:message code="cardTerminal.site.label" default="Site" /></span>
					
						<span class="property-value" aria-labelledby="site-label"><g:link controller="site" action="show" id="${cardTerminalInstance?.site?.id}">${cardTerminalInstance?.site?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:cardTerminalInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${cardTerminalInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
