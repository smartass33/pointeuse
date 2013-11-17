<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<table id="employee-table">
	<thead>
		<tr>
			<g:sortableColumn property="lastName" style="width:150px;text-align:center"
				title="${message(code: 'employee.lastName.label', default: 'Last Name')}" />
			<g:sortableColumn property="firstName" style="width:100px;text-align:center"
				title="${message(code: 'employee.firstName.label', default: 'First Name')}" />	
				<g:sortableColumn property="weeklyContractTime" style="width:90px;text-align:center"
					title="${message(code: 'employee.weeklyContractTime.short.label', default: 'weeklyContractTime')}" />
				<g:sortableColumn property="arrivalDate" style="text-align:center"
					title="${message(code: 'employee.arrivalDate.short.label', default: 'arrivalDate')}" />
				<g:sortableColumn property="service" style="text-align:center"
					title="${message(code: 'employee.service.label', default: 'service')}" />
				<g:sortableColumn property="matricule" style="text-align:center"
					title="${message(code: 'employee.matricule.label', default: 'matricule')}" />
		</tr>
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${employeeInstanceList}" status="i" var="employeeInstance">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:120px"><g:link action="edit" controller='employee' id="${employeeInstance.id}"
						params="${[isAdmin:isAdmin,siteId:siteId,fromSite:fromSite]}">
						${fieldValue(bean: employeeInstance, field: "lastName")}
					</g:link></td>
				<td style="width:120px"><g:link action="edit" controller='employee' id="${employeeInstance.id}"
						params="${[isAdmin:isAdmin,siteId:siteId,fromSite:fromSite]}">
						${fieldValue(bean: employeeInstance, field: "firstName")}
					</g:link></td>
				<g:if test="${employeeInstance?.weeklyContractTime}">
					<td>${fieldValue(bean: employeeInstance, field: "weeklyContractTime")}</td>
				</g:if>
				<g:else>
					<td />
				</g:else>
				<g:if test="${employeeInstance?.arrivalDate}">
					<td>${employeeInstance?.arrivalDate.format('dd/MM/yyyy')}</td>
				</g:if>
				<g:else>
					<td />
				</g:else>
				<g:if test="${employeeInstance?.service}">
					<td>${employeeInstance?.service.name}</td>
				</g:if>
				<g:else>
					<td />
				</g:else>
				<g:if test="${employeeInstance?.matricule}">
					<td>${employeeInstance?.matricule}</td>
				</g:if>
				<g:else>
					<td />
				</g:else>
			</tr>
		</g:each>
	</tbody>
</table>

