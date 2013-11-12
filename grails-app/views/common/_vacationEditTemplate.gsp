<%@ page import="pointeuse.Vacation" %>

<body>
	<h1>${message(code: 'vacation.total.label', default: 'Period')}</h1>	
	<table>
		<thead>		
			<tr>					
				<g:sortableColumn property="period" title="${message(code: 'vacation.period.label', default: 'Period')}" />					
				<g:sortableColumn property="type" title="${message(code: 'vacation.type.label', default: 'Type')}" />					
				<g:sortableColumn property="counter" title="${message(code: 'vacation.counter.label', default: 'Counter')}" />					
			</tr>
		</thead>
		<tbody>
		<g:each in="${Vacation.findAllByEmployee(employeeInstance,[sort:'period.year',order:'asc'])}" status="i" var="vacationInstance">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">				
				<td>${vacationInstance.period.year}</td>					
				<td>${fieldValue(bean: vacationInstance, field: "type")}</td>				
				<td>				
			        <div>
	            		<input type="number" name="name"          		
		           			onchange="${remoteFunction(action:'changeValue', controller:'vacation', 
									  	params:'\'userId=' + employeeInstance.id 						  
											+ '&vacationId=' + vacationInstance.id
									  		+ '&counter=\' + this.value'								  )}"
		                    name="absenceType" 
		                    value="${vacationInstance.counter}" 
							min="0" max="32"	
		            	/>
	        		</div>
				</td>														
			</tr>
		</g:each>
		</tbody>
	</table>
</body>


