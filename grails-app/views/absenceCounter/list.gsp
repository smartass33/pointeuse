
<%@ page import="pointeuse.AbsenceCounter" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'absenceCounter.label', default: 'AbsenceCounter')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-absenceCounter" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-absenceCounter" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="logging_time" title="${message(code: 'absenceCounter.logging_time.label', default: 'Loggingtime')}" />
					
						<g:sortableColumn property="type" title="${message(code: 'absenceCounter.type.label', default: 'Type')}" />
					
						<th><g:message code="absenceCounter.employee.label" default="Employee" /></th>
					
						<g:sortableColumn property="year" title="${message(code: 'absenceCounter.year.label', default: 'Year')}" />
					
						<g:sortableColumn property="counter" title="${message(code: 'absenceCounter.counter.label', default: 'Counter')}" />
					
						<th><g:message code="absenceCounter.user.label" default="User" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${absenceCounterInstanceList}" status="i" var="absenceCounterInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${absenceCounterInstance.id}">${fieldValue(bean: absenceCounterInstance, field: "logging_time")}</g:link></td>
					
						<td>${fieldValue(bean: absenceCounterInstance, field: "type")}</td>
					
						<td>${fieldValue(bean: absenceCounterInstance, field: "employee")}</td>
					
						<td>${fieldValue(bean: absenceCounterInstance, field: "year")}</td>
					
						<td>${fieldValue(bean: absenceCounterInstance, field: "counter")}</td>
					
						<td>${fieldValue(bean: absenceCounterInstance, field: "user")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${absenceCounterInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
