
<%@ page import="pointeuse.Anomaly" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'anomaly.label', default: 'Anomaly')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-anomaly" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-anomaly" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="creationDate" title="${message(code: 'anomaly.creationDate.label', default: 'Creation Date')}" />
					
						<th><g:message code="anomaly.user.label" default="User" /></th>
					
						<g:sortableColumn property="RAS" title="${message(code: 'anomaly.RAS.label', default: 'RAS')}" />
					
						<th><g:message code="anomaly.type.label" default="Type" /></th>
					
						<g:sortableColumn property="description" title="${message(code: 'anomaly.description.label', default: 'Description')}" />
					
						<g:sortableColumn property="name" title="${message(code: 'anomaly.name.label', default: 'Name')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${anomalyInstanceList}" status="i" var="anomalyInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${anomalyInstance.id}">${fieldValue(bean: anomalyInstance, field: "creationDate")}</g:link></td>
					
						<td>${fieldValue(bean: anomalyInstance, field: "user")}</td>
					
						<td><g:formatBoolean boolean="${anomalyInstance.RAS}" /></td>
					
						<td>${fieldValue(bean: anomalyInstance, field: "type")}</td>
					
						<td>${fieldValue(bean: anomalyInstance, field: "description")}</td>
					
						<td>${fieldValue(bean: anomalyInstance, field: "name")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${anomalyInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
