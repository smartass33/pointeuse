
<%@ page import="pointeuse.AuthorizationNature" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'authorizationNature.label', default: 'AuthorizationNature')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-authorizationNature" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-authorizationNature" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="creationDate" title="${message(code: 'creationDate.label', default: 'Creation Date')}" />					
						<g:sortableColumn property="nature" title="${message(code: 'authorizationNature.user.label', default: 'Nature')}" />					
						<th><g:message code="authorizationNature.user.label" default="User" /></th>					
					</tr>
				</thead>	
				<tbody>
					<g:each in="${authorizationNatureInstanceList}" status="i" var="authorizationNatureInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">						
							<td><g:formatDate date="${authorizationNatureInstance.creationDate}"  format="dd/MM/yyyy"/></td>						
							<td>${authorizationNatureInstance.nature}</td>						
							<td>${authorizationNatureInstance.user.firstName} ${authorizationNatureInstance.user.lastName}</td>						
						</tr>
					</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${authorizationNatureInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
