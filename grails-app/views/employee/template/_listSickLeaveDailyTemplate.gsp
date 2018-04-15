<%@ page import="pointeuse.Employee"%>

	<table id="employee-table" style="font-family: Helvetica;font-size: 14px;">
	<thead>
		<tr>
			<th style="width:90px;text-align:center">${message(code: 'employee.lastName.label', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.function.label', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'service.label', default: 'Report')}</th>
		</tr>
	</thead>
	<tbody id='body_update' style="border:1px;">
	rrrr
	${sickEmployee}
		<g:each in="${sickEmployee}" status="i" var="employee">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:90px;text-align:center;valign:middle;">${employee.key.lastName}<BR>${employee.key.firstName}</td>
				<td style="width:120px;text-align:center;valign:middle;">${employee.key.function.name}</td>
				<td style="width:120px;text-align:center;valign:middle;">${employee.key.service.name}</td>
			</tr>
		</g:each>
	</tbody>
</table>

