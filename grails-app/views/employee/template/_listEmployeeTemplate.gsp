<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>


<table id="employee-table" >
	<thead>
		<tr>
			<g:sortableColumn property="lastName" style="width:150px;text-align:center" title="${message(code: 'employee.lastName.label', default: 'Last Name')}" />
			<g:sortableColumn property="site" style="text-align:center" title="${message(code: 'employee.site.label', default: 'Site')}" />
			<th style="text-align:center" class="principal">${message(code: 'employee.function.label', default: 'Site')}</th>		
			<th style="width:70px;text-align:center" class="principal">${message(code: 'employee.annualReport.label', default: 'Report')}</th>
			<th style="width:70px;text-align:center" class="principal">${message(code: 'employee.monthly.report.label', default: 'Report')}</th>
			<th style="width:70px;text-align:center" class="principal">${message(code: 'vacation.report.label', default: 'Report')}</th>
			<g:sortableColumn property="status" style="text-align:center" class="principal"
				title="${message(code: 'employee.entry.status', default: 'Entry')}" />
			<th style="text-align:center" class="principal">${message(code: 'employee.lastTime.label', default: 'Entry')}</th>
			<g:sortableColumn property="lastName" style="width:150px;text-align:center" class="admin"
				title="${message(code: 'employee.username.label', default: 'User Name')}" />
			<g:sortableColumn property="weeklyContractTime" style="width:90px;text-align:center" class="admin"
				title="${message(code: 'employee.weeklyContractTime.short.label', default: 'weeklyContractTime')}" />
			<g:sortableColumn property="arrivalDate" style="text-align:center" class="admin"
				title="${message(code: 'employee.arrivalDate.short.label', default: 'arrivalDate')}" />
			<g:sortableColumn property="service" style="text-align:center" class="admin"
				title="${message(code: 'employee.service.label', default: 'service')}" />
			<g:sortableColumn property="matricule" style="text-align:center" class="admin"
				title="${message(code: 'employee.matricule.label', default: 'matricule')}" />
		</tr>
	</thead>

	<tbody id='body_update' style="border:1px;">
		<g:each in="${employeeInstanceList}" status="i" var="employeeInstance">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:200px" >
					<g:link action="edit" id="${employeeInstance.id}"
							params="${[isAdmin:isAdmin,siteId:siteId]}"
							style="text-decoration: none;">
							${fieldValue(bean: employeeInstance, field: "lastName")}		
					</g:link>
					<g:link action="edit" id="${employeeInstance.id}" style="text-decoration: none;"
						params="${[isAdmin:isAdmin,siteId:siteId]}">
						${fieldValue(bean: employeeInstance, field: "firstName")}
					</g:link>
				</td>

				<td><g:if test="${employeeInstance?.site != null}">
						${employeeInstance?.site.name}
					</g:if>
				</td>					
				<td class="principal"><g:if test="${employeeInstance?.function != null}">
						${employeeInstance?.function.name}
					</g:if>
				</td>				
				<td style="border:1px;width:70px;padding:2px 5px 2px 15px;" class="principal"><g:link controller="employee" action='annualReport' class="listButton" id="${employeeInstance.id}" params="${[userId:employeeInstance?.id,siteId:siteId,isAjax:false]}">${message(code: 'employee.annualReport.label', default: 'Report')}</g:link></td>
				<td style="border:1px;width:70px;padding:2px 5px 2px 15px;" class="principal"><g:link controller="employee" action='report' class="listButton" id="${employeeInstance.id}" params="${[userId:employeeInstance?.id,siteId:siteId]}">${message(code: 'employee.monthly.report.label', default: 'Report')}</g:link></td>
				<td style="border:1px;width:70px;padding:2px 5px 2px 15px;" class="principal"><g:link controller="employee" action='vacationDisplay' class="listButton" id="${employeeInstance.id}" params="${[userId:employeeInstance?.id,siteId:siteId]}">${message(code: 'vacation.report.label', default: 'Report')}</g:link></td>
					<g:form controller="employee">
						<g:hiddenField name="userId" value="${employeeInstance?.id}" />
						<td class="principal"><g:if test="${employeeInstance?.status}">
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
							<td class="principal">${InAndOut.findByEmployee(employeeInstance, [sort:'time',order:'desc']).time.format('H:mm d-M-yyyy')}</td>
						</g:if>
						<g:else>
							<td class="principal">N/A</td>						
						</g:else>
					</g:form>
					<g:if test="${employeeInstance?.userName}">
						<td class="admin">
							${fieldValue(bean: employeeInstance, field: "userName")}
						</td>
					</g:if>
					<g:else>
						<td class="admin"/>
					</g:else>
					<g:if test="${employeeInstance?.weeklyContractTime}">
						<td class="admin">
							${fieldValue(bean: employeeInstance, field: "weeklyContractTime")}
						</td>
					</g:if>
					<g:else>
						<td class="admin"/>
					</g:else>
					<g:if test="${employeeInstance?.arrivalDate}">
						<td class="admin">
							${employeeInstance?.arrivalDate.format('dd/MM/yyyy')}
						</td>
					</g:if>
					<g:else>
						<td class="admin"/>
					</g:else>
					<g:if test="${employeeInstance?.service}">
						<td class="admin">
							${employeeInstance?.service.name}
						</td >
					</g:if>
					<g:else>
						<td class="admin"/>
					</g:else>
					<g:if test="${employeeInstance?.matricule}">
						<td class="admin">
							${employeeInstance?.matricule}
						</td>
					</g:if>
					<g:else>
						<td class="admin"/>
					</g:else>
			</tr>
		</g:each>
	</tbody>
</table>

