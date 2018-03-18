<%@ page import="pointeuse.Vacation" %>
<%@ page import="pointeuse.VacationType" %>
<%@ page import="pointeuse.Period" %>

<body>
	<h1>${message(code: 'vacation.total.label', default: 'Period')}</h1>	
	<table>
		<thead>		
			<tr>					
				<g:sortableColumn property="period" title="${message(code: 'vacation.period.label', default: 'Period')}" />					
				<g:sortableColumn property="counter" title="${message(code: 'CA.type.label', default: 'Counter')}" />					
				<g:sortableColumn property="counter" title="${message(code: 'RTT.type.label', default: 'Counter')}" />					
				<g:sortableColumn property="paidHS" title="${message(code: 'paid.HS.label', default: 'Counter')}" />					
				<g:sortableColumn property="paidHS" title="${message(code: 'paid.HC.label', default: 'Counter')}" />								
			</tr>
		</thead>
		<tbody>
		<%
			def firstYear = (Period.findAll("from Period as p order by year asc",[max:1])).year
			def iterator = firstYear
			def sameLine = true
		%>
		<g:if test="${orderedCAMap != null &&  orderedRTTMap != null}">
			<g:each in="${periodList}" status="i" var="period">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<td>${period.year}/${period.year + 1}</td>
					<td>
						<g:if test="${orderedCAMap.get(period) != null}">							
							<div>
			            		<input type="number" name="name"          		
				           			onchange="${remoteFunction(action:'changeValue', controller:'vacation', 
											  	params:'\'userId=' + employeeInstance.id 						  
													+ '&vacationId=' + orderedCAMap.get(period).id
											  		+ '&counter=\' + this.value')}"
				                    value="${orderedCAMap.get(period).counter}" 
									min="0"
				            	/>
			        		</div>
						</g:if>
					</td>
					<td>
						<g:if test="${orderedRTTMap.get(period) != null}">							
							<div>
			            		<input type="number" name="name"          		
				           			onchange="${remoteFunction(action:'changeValue', controller:'vacation', 
											  	params:'\'userId=' + employeeInstance.id 						  
													+ '&vacationId=' + orderedRTTMap.get(period).id
											  		+ '&counter=\' + this.value')}"
				                    value="${orderedRTTMap.get(period).counter}" 
									min="0"
				            	/>
			        		</div>
						</g:if>
					</td>
				</tr>
			</g:each>
		</g:if>
		</tbody>
	</table>
</body>


