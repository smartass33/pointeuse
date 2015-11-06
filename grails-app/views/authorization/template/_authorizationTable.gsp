<%@ page import="pointeuse.Authorization" %>
<!DOCTYPE html>
<html>
	<body>
		<div id="list-authorization" class="content scaffold-list" role="main">
			<h1><g:message code="default.authorizations.label" /> <g:if test="${employeeInstance != null}"> pour ${employeeInstance.firstName} ${employeeInstance.lastName}</g:if></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>					
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
							<td><g:formatDate date="${authorizationInstance.startDate}" format="dd/MM/yyyy" /></td>											
							<td>
								<g:if test="${authorizationInstance.isAuthorized}">
									<div style="padding:2px 20px 2px 20px; "><img alt="tick" src="../../images/skin/tick.png" style="align:middle;"></div>
									
								</g:if>
								<g:else>
									<div style="padding:2px 20px 2px 20px; "><img alt="cross" src="../../images/skin/cross.png" style="align:middle;"></div>
								</g:else>
							</td>							
							<td><g:formatDate date="${authorizationInstance.endDate}" format="dd/MM/yyyy"/></td>					
							<td>${authorizationInstance.type.type}</td>	
							<td>
						    	<g:remoteLink action="trashAuthorization" controller="authorization" id="trashAuthorization" params="[authorizationInstanceId:authorizationInstance.id]"
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
