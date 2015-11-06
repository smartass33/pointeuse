
<%@ page import="pointeuse.AuthorizationType" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'authorizationType.label', default: 'AuthorizationType')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-authorizationType" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-authorizationType" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="creationDate" title="${message(code: 'authorizationType.creationDate.label', default: 'Creation Date')}" />
					
						<th><g:message code="authorizationType.nature.label" default="Nature" /></th>
					
						<g:sortableColumn property="type" title="${message(code: 'authorizationType.type.label', default: 'Type')}" />
					
						<th><g:message code="authorizationType.user.label" default="User" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${authorizationTypeInstanceList}" status="i" var="authorizationTypeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${authorizationTypeInstance.id}">${fieldValue(bean: authorizationTypeInstance, field: "creationDate")}</g:link></td>
					
						<td>${fieldValue(bean: authorizationTypeInstance, field: "nature")}</td>
					
						<td>${fieldValue(bean: authorizationTypeInstance, field: "type")}</td>
					
						<td>${fieldValue(bean: authorizationTypeInstance, field: "user")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${authorizationTypeInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
