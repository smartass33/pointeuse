
<%@ page import="pointeuse.Action" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'action.label', default: 'Action')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-action" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-action" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="date" title="${message(code: 'action.date.label', default: 'Date')}" />
					
						<g:sortableColumn property="day" title="${message(code: 'action.day.label', default: 'Day')}" />
					
						<th><g:message code="action.employeeLogger.label" default="Employee Logger" /></th>
					
						<g:sortableColumn property="isTheoritical" title="${message(code: 'action.isTheoritical.label', default: 'Is Theoritical')}" />
					
						<th><g:message code="action.itinerary.label" default="Itinerary" /></th>
					
						<g:sortableColumn property="month" title="${message(code: 'action.month.label', default: 'Month')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${actionInstanceList}" status="i" var="actionInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${actionInstance.id}">${fieldValue(bean: actionInstance, field: "date")}</g:link></td>
					
						<td>${fieldValue(bean: actionInstance, field: "day")}</td>
					
						<td>${fieldValue(bean: actionInstance, field: "employeeLogger")}</td>
					
						<td><g:formatBoolean boolean="${actionInstance.isTheoritical}" /></td>
					
						<td>${fieldValue(bean: actionInstance, field: "itinerary")}</td>
					
						<td>${fieldValue(bean: actionInstance, field: "month")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${actionInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
