
<%@ page import="pointeuse.AbsenceTypeConfig" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'absenceTypeConfig.label', default: 'AbsenceTypeConfig')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-absenceTypeConfig" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-absenceTypeConfig" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="creationDate" title="${message(code: 'absenceTypeConfig.creationDate.label', default: 'Creation Date')}" />
					
						<g:sortableColumn property="isPorportional" title="${message(code: 'absenceTypeConfig.isPorportional.label', default: 'Is Porportional')}" />
					
						<g:sortableColumn property="name" title="${message(code: 'absenceTypeConfig.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="shortName" title="${message(code: 'absenceTypeConfig.shortName.label', default: 'Short Name')}" />
					
						<th><g:message code="absenceTypeConfig.user.label" default="User" /></th>
					
						<g:sortableColumn property="weight" title="${message(code: 'absenceTypeConfig.weight.label', default: 'Weight')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${absenceTypeConfigInstanceList}" status="i" var="absenceTypeConfigInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${absenceTypeConfigInstance.id}">${fieldValue(bean: absenceTypeConfigInstance, field: "creationDate")}</g:link></td>
					
						<td><g:formatBoolean boolean="${absenceTypeConfigInstance.isPorportional}" /></td>
					
						<td>${fieldValue(bean: absenceTypeConfigInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: absenceTypeConfigInstance, field: "shortName")}</td>
					
						<td>${fieldValue(bean: absenceTypeConfigInstance, field: "user")}</td>
					
						<td>${fieldValue(bean: absenceTypeConfigInstance, field: "weight")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${absenceTypeConfigInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
