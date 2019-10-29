<%@ page import="pointeuse.Employee"%>

<table id="employee-table" style="font-family: Helvetica;font-size: 14px;">
<thead>
	<tr>
		<th style="width:90px;text-align:center">${message(code: 'employee.site.label', default: 'Report')}</th>
		<th style="width:90px;text-align:center">${message(code: 'employee.lastName.label', default: 'Report')}</th>
		<th style="width:90px;text-align:center">${message(code: 'employee.firstName.label', default: 'Report')}</th>
		<th style="width:30px;text-align:center">${message(code: 'employee.daily.time.short', default: 'Report')}</th>
		
		<g:each in="${saturdayList}" status="i" var="saturday">
			<th>${saturday.format('dd/MM/yyyy')}</th>
		</g:each>
		
	</tr>
</thead>
<tbody id='body_update' style="border:1px;">	
		<g:each in="${employeeList}" status="j" var="employee">
			<tr>
				<td>${employee.site.name}</td>	
				<td>${employee.lastName}</td>
				<td>${employee.firstName}</td>
				<td>${employeeMap.get(employee)}</td>
				<g:each in="${saturdayList}" status="i" var="saturdayIter">
						<td>
							<g:if test="${monthlyMap.get(saturdayIter).get(employee)}">
								P
							</g:if>
							<g:else>
								-
							</g:else>
						
						</td>
						
		
					
				</g:each>
			</tr>
		</g:each>
	</tbody>
</table>

