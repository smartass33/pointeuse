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
			<g:sortableColumn property="site"
				title="${message(code: 'employee.site.label', default: 'Site')}" />
			<g:if test="${!isAdmin}">
				<th style="width:70px;text-align:center">${message(code: 'employee.annualReport.label', default: 'Report')}</th>
				<th style="width:70px;text-align:center">${message(code: 'employee.monthly.report.label', default: 'Report')}</th>
				<th style="width:70px;text-align:center">${message(code: 'vacation.report.label', default: 'Report')}</th>

				<g:sortableColumn property="status" style="text-align:center"
					title="${message(code: 'employee.entry.status', default: 'Entry')}" />
				<th style="text-align:center">${message(code: 'employee.lastTime.label', default: 'Entry')}</th>
				<g:sortableColumn class="moreDetail"  property="hasError" style="width:60px;text-align:center"
					title="${message(code: 'employee.hasErrors', default: 'Errors')}" />
			</g:if>
			<g:else>
				<g:sortableColumn property="lastName" style="width:150px;text-align:center"
					title="${message(code: 'employee.username.label', default: 'User Name')}" />
				<g:sortableColumn property="weeklyContractTime" style="width:90px;text-align:center"
					title="${message(code: 'employee.weeklyContractTime.short.label', default: 'weeklyContractTime')}" />
				<g:sortableColumn property="arrivalDate" style="text-align:center"
					title="${message(code: 'employee.arrivalDate.short.label', default: 'arrivalDate')}" />
				<g:sortableColumn property="service" style="text-align:center"
					title="${message(code: 'employee.service.label', default: 'service')}" />
				<g:sortableColumn property="matricule" style="text-align:center"
					title="${message(code: 'employee.matricule.label', default: 'matricule')}" />
				<!--g:sortableColumn property="vacations" style="text-align:center"
					title="${message(code: 'vacation.access.label', default: 'matricule')}" /-->	
			</g:else>
		</tr>
	</thead>

	<tbody id='body_update' style="border:1px;">
		<g:each in="${employeeInstanceList}" status="i" var="employeeInstance">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:120px">
				
				<g:link action="edit" id="${employeeInstance.id}"
						params="${[isAdmin:isAdmin,siteId:siteId]}">
						${fieldValue(bean: employeeInstance, field: "lastName")}
					</g:link>

					</td>
				<td style="width:120px"><g:link action="edit" id="${employeeInstance.id}"
						params="${[isAdmin:isAdmin,siteId:siteId]}">
						${fieldValue(bean: employeeInstance, field: "firstName")}
					</g:link></td>
				<td><g:if test="${employeeInstance?.site != null}">
						${employeeInstance?.site.name}
					</g:if></td>
				<g:if test="${!isAdmin}">
					<td style="border:1px;width:200px"><g:link controller="employee" action='annualReport' class="listButton" id="${employeeInstance.id}" params="${[userId:employeeInstance?.id,siteId:siteId,isAjax:false]}">${message(code: 'employee.annualReport.label', default: 'Report')}</g:link></td>
					<td style="border:1px;width:200px"><g:link controller="employee" action='report' class="listButton" id="${employeeInstance.id}" params="${[userId:employeeInstance?.id,siteId:siteId]}">${message(code: 'employee.monthly.report.label', default: 'Report')}</g:link></td>
					<td style="border:1px;width:200px"><g:link controller="employee" action='vacationDisplay' class="listButton" id="${employeeInstance.id}" params="${[userId:employeeInstance?.id,siteId:siteId]}">${message(code: 'vacation.report.label', default: 'Report')}</g:link></td>
				<g:form controller="employee">
						<g:hiddenField name="userId" value="${employeeInstance?.id}" />
						<td><g:if test="${employeeInstance?.status}">
								<g:if
									test="${InAndOut.findAllByEmployee(employeeInstance).size()==0}">
									${message(code: 'employee.out.status', default: 'out')}
								</g:if>
								<g:else>
									${message(code: 'employee.in.status', default: 'In')}
								</g:else>
							</g:if> <g:else>
								${message(code: 'employee.out.status', default: 'out')}
							</g:else></td>
						<g:if test="${InAndOut.findByEmployee(employeeInstance) != null}">
							<td>
								${InAndOut.findByEmployee(employeeInstance, [sort:'time',order:'desc']).time.format('H:mm d-M-yyyy')}
							</td>
						</g:if>
						<g:else>
							<td>
								N/A
							</td>						
						</g:else>
						<g:if test="${!employeeInstance?.hasError}">
							
							<td class="moreDetail" align="center" style="text-align:middle;" ondblclick="this.style.display = 'none';">
								<img alt="tick" src="../images/skin/tick.png" style="vertical-align: middle;horizontal-align:middle;">
							</td>
						</g:if>
						<g:else>
							
							<td class="moreDetail" align="center" style="text-align:middle;" ondblclick="this.style.display = 'none';">
								<img alt="cross" src="../images/skin/cross.png" style="vertical-align: middle;horizontal-align:middle;">
							</td>
						</g:else>
					</g:form>
				</g:if>
				<g:else>
					<g:if test="${employeeInstance?.userName}">
						<td>
							${fieldValue(bean: employeeInstance, field: "userName")}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.weeklyContractTime}">
						<td>
							${fieldValue(bean: employeeInstance, field: "weeklyContractTime")}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.arrivalDate}">
						<td>
							${employeeInstance?.arrivalDate.format('dd/MM/yyyy')}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.service}">
						<td>
							${employeeInstance?.service.name}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.matricule}">
						<td>
							${employeeInstance?.matricule}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
				</g:else>
			</tr>
		</g:each>
	</tbody>
</table>

