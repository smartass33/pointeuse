
<%@ page import="pointeuse.SupplementaryTime" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'supplementary.time.label', default: 'Supplement')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-suptime" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="TBD">A CABLER</g:link></li>
			</ul>
		</div>
		<div id="list-suptime" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'reason.name.label', default: 'Reason Name')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${reasonInstanceList}" status="i" var="reasonInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">					
						<td><g:link action="show" id="${reasonInstance.id}">${fieldValue(bean: reasonInstance, field: "name")}</g:link></td>				
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${reasonInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
