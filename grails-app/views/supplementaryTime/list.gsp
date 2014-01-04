
<%@ page import="pointeuse.SupplementaryTime" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'supplementaryTime.label', default: 'SupplementaryTime')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-supplementaryTime" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-supplementaryTime" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th><g:message code="supplementaryTime.employee.label" default="Employee" /></th>
					
						<g:sortableColumn property="loggingTime" title="${message(code: 'supplementaryTime.loggingTime.label', default: 'Logging Time')}" />
					
						<th><g:message code="supplementaryTime.period.label" default="Period" /></th>
					
						<g:sortableColumn property="type" title="${message(code: 'supplementaryTime.type.label', default: 'Type')}" />
					
						<th><g:message code="supplementaryTime.user.label" default="User" /></th>
					
						<g:sortableColumn property="value" title="${message(code: 'supplementaryTime.value.label', default: 'Value')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${supplementaryTimeInstanceList}" status="i" var="supplementaryTimeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${supplementaryTimeInstance.id}">${fieldValue(bean: supplementaryTimeInstance, field: "employee")}</g:link></td>
					
						<td><g:formatDate date="${supplementaryTimeInstance.loggingTime}" /></td>
					
						<td>${fieldValue(bean: supplementaryTimeInstance, field: "period")}</td>
					
						<td>${fieldValue(bean: supplementaryTimeInstance, field: "type")}</td>
					
						<td>${fieldValue(bean: supplementaryTimeInstance, field: "user")}</td>
					
						<td>${fieldValue(bean: supplementaryTimeInstance, field: "value")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${supplementaryTimeInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
