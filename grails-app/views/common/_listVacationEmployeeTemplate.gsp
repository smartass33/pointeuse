<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Vacation"%>
<%@ page import="pointeuse.Absence"%>

<table>

	<thead>
		<tr>			
			<g:sortableColumn property="last_name" title="${message(code: 'employee.lastName.label', default: 'Type')}" />						
			<g:sortableColumn property="reference_vacation" title="${message(code: 'vacation.reference.label', default: 'Type')}" />					
			<g:sortableColumn property="taken_vacation" title="${message(code: 'vacation.taken.label', default: 'Type')}" />					
			<g:sortableColumn property="remainder_vacation" title="${message(code: 'vacation.remainder.label', default: 'Counter')}" />					
			<g:sortableColumn property="reference_rtt" title="${message(code: 'rtt.reference.label', default: 'Type')}" />					
			<g:sortableColumn property="taken_rtt" title="${message(code: 'rtt.taken.label', default: 'Type')}" />					
			<g:sortableColumn property="remainder_rtt" title="${message(code: 'rtt.remainder.label', default: 'Counter')}" />	
		</tr>
	</thead>
	<tbody>
	<g:if test="${employeeList!=null}">
		<g:each in="${employeeList}" var="employee" status='i'>
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
				<g:if test="${period2!=null && employee != null}">
					<td>${employee.lastName}</td>
					<td>${initialCAMap.get(employee)}</td>
					<td>${takenCAMap.get(employee)}</td>
					<td>${remainingCAMap.get(employee)}</td>
					<td>${initialRTTMap.get(employee)}</td>
					<td>${takenRTTMap.get(employee)}</td>
					<td>${remainingRTTMap.get(employee)}</td>				
				</g:if>
			</tr>
		</g:each>
	</g:if>
	</tbody>
</table>


