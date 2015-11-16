<%@ page import="pointeuse.Authorization" %>
<!DOCTYPE html>
<html>
	<body>
		<div id="list-authorization" class="content scaffold-list" role="main" >
			<h1><g:message code="default.authorizations.label" /> <g:if test="${employeeInstance != null}"> pour ${employeeInstance.firstName} ${employeeInstance.lastName}</g:if></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:if test="${showEmployee}"><th><g:message code="employee.label" default="Employee" /></th></g:if>					
						<g:sortableColumn property="startDate" title="${message(code: 'authorization.startDate.label', default: 'Start Date')}" />					
						<g:sortableColumn property="isAuthorized" title="${message(code: 'authorization.isAuthorized.label', default: 'Is Authorized')}" />					
						<g:sortableColumn property="endDate" title="${message(code: 'authorization.endDate.label', default: 'End Date')}" />					
						<g:sortableColumn property="type" title="${message(code: 'authorization.type.label', default: 'Type')}" />				
						<g:sortableColumn property="trash" title="${message(code: 'authorization.trash.label', default: 'Type')}" />				
					</tr>
				</thead>
				<tbody>
					<g:each in="${authorizationInstanceList}" status="i" var="authorizationInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
							<g:if test="${showEmployee}"><td><g:link controller="employee" action="edit" id="${authorizationInstance.employee.id}">${authorizationInstance.employee.firstName} ${authorizationInstance.employee.lastName}</g:link></td></g:if>															
							<td style="align:center;"><g:formatDate date="${authorizationInstance.startDate}" format="dd/MM/yyyy" /></td>											
							<td style="align:center;">
								<g:if test="${authorizationInstance.isAuthorized}">
									<div style="padding:2px 20px 2px 20px; "><img alt="tick" src="${grailsApplication.config.serverURL}/${grailsApplication.config.context}/images/skin/tick.png" style="align:middle;"></div>
								</g:if>
								<g:else>
									<div style="padding:2px 20px 2px 20px; "><img alt="cross" src="${grailsApplication.config.serverURL}/${grailsApplication.config.context}/images/skin/cross.png" style="align:middle;"></div>
								</g:else>
							</td>							
							<td style="align:center;"><g:formatDate date="${authorizationInstance.endDate}" format="dd/MM/yyyy"/></td>					
							<td style="align:center;">${authorizationInstance.type.name}</td>	
							<td style="align:center;">
						    	<g:remoteLink action="trashAuthorization" controller="authorization" id="trashAuthorization" params="[authorizationInstanceId:authorizationInstance.id,showEmployee:showEmployee,fromEditEmployee:fromEditEmployee]"
				                    	update="authorizationDiv"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';"
				                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
				                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
				                </g:remoteLink>
					    	</td> 				
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
