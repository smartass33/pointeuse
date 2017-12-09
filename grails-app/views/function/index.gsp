
<%@ page import="pointeuse.Function" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'function.label', default: 'Function')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-function" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.feminine.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-function" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'function.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="ranking" title="${message(code: 'function.ranking.label', default: 'Ranking')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${functionInstanceList}" status="i" var="functionInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${functionInstance.id}">${fieldValue(bean: functionInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: functionInstance, field: "ranking")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${functionInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
