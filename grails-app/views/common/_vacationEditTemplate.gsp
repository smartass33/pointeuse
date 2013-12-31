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
							
			</tr>
		</thead>
		<tbody>
		<%def firstYear = (Period.findAll(sort:'year',order:'asc',max:1)).year
		def lastYear = (Period.findAll(sort:'year',order:'desc',max:1)).year 
		def iterator = firstYear.get(0) as int
		def sameLine=false
		%>
			<g:each in="${orderedVacationList}" status="i" var="vacationInstance">
				<% if (iterator==vacationInstance.period.year){
					iterator=iterator+1
					sameLine=true
				}else{
					sameLine=false
				}
				 %>
				<g:if test="${sameLine}">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">				
						<td>${vacationInstance.period}</td>					
						<td>				
					        <div>
			            		<input type="number" name="name"          		
				           			onchange="${remoteFunction(action:'changeValue', controller:'vacation', 
											  	params:'\'userId=' + employeeInstance.id 						  
													+ '&vacationId=' + vacationInstance.id
											  		+ '&counter=\' + this.value')}"
				                    value="${vacationInstance.counter}" 
									min="0" max="32"	
				            	/>
			        		</div>
						</td>		
						</g:if>	
						<g:else>
							<td>				
						        <div>
				            		<input type="number" name="name"          		
					           			onchange="${remoteFunction(action:'changeValue', controller:'vacation', 
												  	params:'\'userId=' + employeeInstance.id 						  
														+ '&vacationId=' + vacationInstance.id
												  		+ '&counter=\' + this.value')}"
					                    value="${vacationInstance.counter}" 
										min="0" max="32"	
					            	/>
				        		</div>
							</td>
					</tr>	
				</g:else>															
			</g:each>
		</tbody>
	</table>
</body>


