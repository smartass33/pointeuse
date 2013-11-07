
<%@ page import="pointeuse.Vacation" %>
<!DOCTYPE html>
<html>
	<head>
	
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'vacation.label', default: 'Vacation')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-vacation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-vacation" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="counter" title="${message(code: 'vacation.counter.label', default: 'Counter')}" />
					
						<th><g:message code="vacation.employee.label" default="Employee" /></th>
					
						<g:sortableColumn property="loggingTime" title="${message(code: 'vacation.loggingTime.label', default: 'Logging Time')}" />
					
						<th><g:message code="vacation.user.label" default="Logging User" /></th>
					
						<g:sortableColumn property="period" title="${message(code: 'vacation.period.label', default: 'Period')}" />
					
						<g:sortableColumn property="type" title="${message(code: 'vacation.type.label', default: 'Type')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${vacationInstanceList}" status="i" var="vacationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${vacationInstance.id}">${fieldValue(bean: vacationInstance, field: "counter")}</g:link></td>
					
						<td>${fieldValue(bean: vacationInstance, field: "employee")}</td>
					
						<td><g:formatDate date="${vacationInstance.loggingTime}" /></td>
					
						<td>${fieldValue(bean: vacationInstance, field: "user")}</td>
					
						<td>${vacationInstance.year.period}</td>
					
						<td>${fieldValue(bean: vacationInstance, field: "type")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			

			
			<div class="pagination">
				<g:paginate total="${vacationInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
