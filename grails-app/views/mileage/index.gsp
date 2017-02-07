
<%@ page import="pointeuse.Milage" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'milage.label', default: 'Milage')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-milage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-milage" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<th><g:message code="milage.employee.label" default="Employee" /></th>
					
						<g:sortableColumn property="loggingTime" title="${message(code: 'milage.loggingTime.label', default: 'Logging Time')}" />
					
						<g:sortableColumn property="month" title="${message(code: 'milage.month.label', default: 'Month')}" />
					
						<th><g:message code="milage.period.label" default="Period" /></th>
					
						<th><g:message code="milage.user.label" default="User" /></th>
					
						<g:sortableColumn property="value" title="${message(code: 'milage.value.label', default: 'Value')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${milageInstanceList}" status="i" var="milageInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${milageInstance.id}">${fieldValue(bean: milageInstance, field: "employee")}</g:link></td>
					
						<td><g:formatDate date="${milageInstance.loggingTime}" /></td>
					
						<td>${fieldValue(bean: milageInstance, field: "month")}</td>
					
						<td>${fieldValue(bean: milageInstance, field: "period")}</td>
					
						<td>${fieldValue(bean: milageInstance, field: "user")}</td>
					
						<td>${fieldValue(bean: milageInstance, field: "value")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${milageInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
