
<%@ page import="pointeuse.EventLog" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'eventLog.label', default: 'EventLog')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-eventLog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-eventLog" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="source" title="${message(code: 'eventLog.source.label', default: 'Source')}" />
					
						<g:sortableColumn property="message" title="${message(code: 'eventLog.message.label', default: 'Message')}" />
					
						<g:sortableColumn property="details" title="${message(code: 'eventLog.details.label', default: 'Details')}" />
					
						<g:sortableColumn property="cleared" title="${message(code: 'eventLog.cleared.label', default: 'Cleared')}" />
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'eventLog.dateCreated.label', default: 'Date Created')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${eventLogInstanceList}" status="i" var="eventLogInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${eventLogInstance.id}">${fieldValue(bean: eventLogInstance, field: "source")}</g:link></td>
					
						<td>${fieldValue(bean: eventLogInstance, field: "message")}</td>
					
						<td>${fieldValue(bean: eventLogInstance, field: "details")}</td>
					
						<td><g:formatBoolean boolean="${eventLogInstance.cleared}" /></td>
					
						<td><g:formatDate date="${eventLogInstance.dateCreated}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${eventLogInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
