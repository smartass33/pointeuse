<head>
	<g:set var="calendar" value="${Calendar.instance}"/>
</head>
<table>
	<thead>
		<tr>
			<th style="vertical-align: middle;text-align:center;"><g:message code="supplementary.time.date"/></th>	
			<th style="vertical-align: middle;text-align:center;"><g:message code="supplementary.time.value"/></th>	
			<th style="vertical-align: middle;text-align:center;"><g:message code="supplementary.time.amount.paid"/></th>	
		</tr>
	</thead>
	<tbody>
	<g:each in="${supplementaryTimeList}" status="i" var="supTimeInstance">
		<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
			<td style="vertical-align: middle;">
				<%
					def year = supTimeInstance.period.year
					calendar.set(Calendar.MONTH,supTimeInstance.month - 1)
					if (supTimeInstance.month < 6){year +=1}
				%>
				${calendar.time.format('MMM')} ${year}
			</td>	
			<td style="vertical-align: middle;">${supTimeInstance.value}</td>					
			<td style="vertical-align: middle;">
				<div>							
            		<input type="number" name="name"          		
	           			onchange="${remoteFunction(action:'updateSupTime', controller:'supplementaryTime', update:'supTimeList',
								  	params:'\'userId=' + employee.id 						  
										+ '&supTimeInstance=' + supTimeInstance.id
								  		+ '&val=\' + this.value')}"
	                    value="${supTimeInstance.amountPaid}" 
						min="0"
	            	/>
        		</div>
			</td>
		</tr>
	</g:each>
	</tbody>
</table>


