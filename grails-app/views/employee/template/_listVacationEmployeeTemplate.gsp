<%@ page import="pointeuse.Period"%>
<%@ page import="pointeuse.Site"%>

<table id="table-header-rotated" style="width:100%;"class="table table-striped table-header-rotated" >
	<thead>
			<th class='rotate-45' ><div><span><font size="1"><g:message code="employee.lastName.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="vacation.reference.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="vacation.taken.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="vacation.remainder.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="rtt.reference.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="rtt.taken.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="rtt.remainder.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="css.taken.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="sickness.taken.label"/></font></span></div></th>		
			<th class='rotate-45'><div><span><font size="1"><g:message code="other.vacation.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="exceptionnal.vacation.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="paternite.vacation.label"/></font></span></div></th>
			<th class='rotate-45'><div><span><font size="1"><g:message code="parental.vacation.label"/></font></span></div></th>
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
					<td>${takenCSSMap.get(employee)}</td>						
					<td>${takenSicknessMap.get(employee)}</td>	
					<td>${takenAutreMap.get(employee)}</td>			
					<td>${takenExceptionnelMap.get(employee)}</td>	
					<td>${takenPaterniteMap.get(employee)}</td>	
					<td>${takenParentalMap.get(employee)}</td>	
				</g:if>
			</tr>
		</g:each>
	</g:if>
	</tbody>
</table>