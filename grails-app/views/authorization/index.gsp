
<%@ page import="pointeuse.Authorization" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'authorization.label', default: 'Authorization')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-authorization" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create" params="${[employeeInstanceId:employeeInstanceId]}"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-authorization" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /> <g:if test="${employeeInstance != null}"> pour ${employeeInstance.firstName} ${employeeInstance.lastName}</g:if></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>					
						<g:if test="${employeeInstance == null}"><th><g:message code="employee.label" default="Employee" /></th></g:if>			
						<g:sortableColumn property="startDate" title="${message(code: 'authorization.startDate.label', default: 'End Date')}" />					
						<g:sortableColumn property="isAuthorized" title="${message(code: 'authorization.isAuthorized.label', default: 'Is Authorized')}" />					
						<g:sortableColumn property="startDate" title="${message(code: 'authorization.endDate.label', default: 'Start Date')}" />					
						<g:sortableColumn property="type" title="${message(code: 'authorization.type.label', default: 'Type')}" />				
					</tr>
				</thead>
				<tbody>
					<g:each in="${authorizationInstanceList}" status="i" var="authorizationInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">					
							<g:if test="${employeeInstance == null}"><td><g:link controller="employee" action="edit" id="${authorizationInstance.employee.id}">${authorizationInstance.employee.firstName} ${authorizationInstance.employee.lastName}</g:link></td>	</g:if>			
							<td><g:formatDate date="${authorizationInstance.startDate}" format="dd/MM/yyyy" /></td>											
							<td>
								<g:if test="${authorizationInstance.isAuthorized}">
									<div style="padding:2px 20px 2px 20px; "><img alt="tick" src="../images/skin/tick.png" style="align:middle;"></div>
									
								</g:if>
								<g:else>
									<div style="padding:2px 20px 2px 20px; "><img alt="cross" src="../images/skin/cross.png" style="align:middle;"></div>
								</g:else>
							</td>
							<td><g:formatDate date="${authorizationInstance.endDate}" format="dd/MM/yyyy"/></td>					
							<td>${authorizationInstance.type.type}</td>					
						</tr>
					</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${authorizationInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
