<%@ page import="pointeuse.Vacation" %>
<%@ page import="pointeuse.VacationType" %>
<%@ page import="pointeuse.Period" %>
<%@ page import="pointeuse.SupplementaryTime" %>


<body>
	<h1>${message(code: 'paid.HS.total.label', default: 'Period')}</h1>	
	<table>
		<thead>		
			<tr>					
				<g:sortableColumn property="period" title="${message(code: 'vacation.period.label', default: 'Period')}" />								
				<g:sortableColumn property="paidHS" title="${message(code: 'paid.HS.label', default: 'Counter')}" />	
				<g:sortableColumn property="remainingHS" title="${message(code: 'remaining.HS.label', default: 'Counter')}" />					
				<g:sortableColumn property="paidHC" title="${message(code: 'paid.HC.label', default: 'Counter')}" />	
				<g:sortableColumn property="remainingHC" title="${message(code: 'remaining.HC.label', default: 'Counter')}" />												
			</tr>
		</thead>
		
		<tbody>
		<g:each in="${Period.findAll(sort:'year',order:'asc') }"  status="p" var="period">
			<tr>
				<td>${period}</td>
				<!--td>${SupplementaryTime.findAllByEmployeeAndPeriod(employeeInstance,period)}</td-->
				<td>${orderedSupTimeList}</td>
				<td>
					<g:remoteField id="value" action="payHS" update="divId"
					name="value" value="${params.value ?: '0'}" paramName="value"
					style="vertical-align: middle;"
					params="\'value=\'+this.value+\'&userId=\'+\'${employeeInstance.id}+\'+\'&period=\'+\'${period.year}+\'"/>
				</td>
			<tr>
		
		</g:each>
		</tbody>

	</table>
</body>


