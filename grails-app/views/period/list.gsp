<%@ page import="pointeuse.Period" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'period.label')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-year" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.feminine.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-year" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>				
						<g:sortableColumn property="period" title="${message(code: 'period.period.label', default: 'Period')}" />				
					</tr>
				</thead>
				<tbody>
				<g:each in="${periodInstanceList}" status="i" var="periodInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${periodInstance.id}">${periodInstance.year}/${periodInstance.year+1}</g:link></td>				
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${periodInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
