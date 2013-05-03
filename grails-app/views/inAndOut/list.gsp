
<%@ page import="pointeuse.InAndOut" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'inAndOut.label', default: 'InAndOut')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-inAndOut" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-inAndOut" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th><g:message code="inAndOut.employee.label" default="Employee" /></th>
					
						<g:sortableColumn property="timeIn" title="${message(code: 'inAndOut.timeIn.label', default: 'Time In')}" />
					
						<g:sortableColumn property="timeOut" title="${message(code: 'inAndOut.timeOut.label', default: 'Time Out')}" />
					
						<g:sortableColumn property="type" title="${message(code: 'inAndOut.type.label', default: 'Type')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${inAndOutInstanceList}" status="i" var="inAndOutInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${inAndOutInstance.id}">${fieldValue(bean: inAndOutInstance, field: "employee")}</g:link></td>
					
						<td><g:formatDate date="${inAndOutInstance.timeIn}" /></td>
					
						<td><g:formatDate date="${inAndOutInstance.timeOut}" /></td>
					
						<td>${fieldValue(bean: inAndOutInstance, field: "type")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${inAndOutInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
