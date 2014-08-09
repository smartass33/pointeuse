<%@ page import="pointeuse.Period"%>
<%@ page import="pointeuse.Site"%>

<table id="table-header-rotated" style="width:100%;"class="table table-striped table-header-rotated" >
	<thead>
			<th class='rotate-45' ><div><span><g:message code="employee.lastName.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="vacation.reference.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="vacation.taken.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="vacation.remainder.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="rtt.reference.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="rtt.taken.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="rtt.remainder.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="other.vacation.label"/></span></div></th>
			<th class='rotate-45'><div><span><g:message code="exceptionnal.vacation.label"/></span></div></th>
	</thead>
	<tbody>
	<g:if test="${employeeInstanceList!=null}">
		<g:each in="${employeeInstanceList}" var="employee" status='i'>
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
				<g:if test="${period!=null && employee != null}">
					<td>${employee.lastName}</td>
					<td>${initialCAMap.get(employee)}</td>
					<td>${takenCAMap.get(employee)}</td>
					<td>${remainingCAMap.get(employee)}</td>
					<td>${initialRTTMap.get(employee)}</td>
					<td>${takenRTTMap.get(employee)}</td>
					<td>${remainingRTTMap.get(employee)}</td>		
					<td>${takenAutreMap.get(employee)}</td>			
					<td>${takenExceptionnelMap.get(employee)}</td>	
				</g:if>
			</tr>
		</g:each>
	</g:if>
	</tbody>
</table>