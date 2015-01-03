
<%@ page import="pointeuse.CardTerminal" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'cardTerminal.label', default: 'CardTerminal')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-cardTerminal" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-cardTerminal" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="creationDate" title="${message(code: 'cardTerminal.creationDate.label', default: 'Creation Date')}" />
					
						<g:sortableColumn property="hostname" title="${message(code: 'cardTerminal.hostname.label', default: 'Hostname')}" />
					
						<g:sortableColumn property="ip" title="${message(code: 'cardTerminal.ip.label', default: 'Ip')}" />
					
						<g:sortableColumn property="lastKeepAlive" title="${message(code: 'cardTerminal.lastKeepAlive.label', default: 'Last Keep Alive')}" />
					
						<th><g:message code="cardTerminal.site.label" default="Site" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${cardTerminalInstanceList}" status="i" var="cardTerminalInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${cardTerminalInstance.id}">${fieldValue(bean: cardTerminalInstance, field: "creationDate")}</g:link></td>
					
						<td>${fieldValue(bean: cardTerminalInstance, field: "hostname")}</td>
					
						<td>${fieldValue(bean: cardTerminalInstance, field: "ip")}</td>
					
						<td><g:formatDate date="${cardTerminalInstance.lastKeepAlive}" /></td>
					
						<td>${fieldValue(bean: cardTerminalInstance, field: "site")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${cardTerminalInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
