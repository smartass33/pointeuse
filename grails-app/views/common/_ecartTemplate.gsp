<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>


<table id="employee-table" border="1">
	<tbody id='body_update' style="border:1px;">
		<tr>
			<td/>
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
				<g:if test="${month_th>5}">
					<td>${yearInf}</td>
				</g:if>
				<g:else>
					<td>${yearSup}</td>		
				</g:else>
			</g:each>
		</tr>
		<tr>
			<td/>
			<td>Juin</td>
			<td>Juil</td>
			<td>Aout</td>			
			<td>Sept</td>
			<td>Oct</td>
			<td>Nov</td>	
			<td>Déc</td>
			<td>Jan</td>
			<td>Fév</td>	
			<td>Mar</td>
			<td>Avrl</td>
			<td>Mai</td>
		</tr>										
		<g:each in="${employeeInstanceList}" status="i" var="employee">
			<th colspan='14'>${employee.firstName} ${employee.lastName}</th>
			<tr>
				<td>Total H Théorique</td>	
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_theoritical'>	
					<g:if test="${monthlyTheoriticalByEmployee.get(employee)!=null}">
						<g:if test="${monthList.contains(month_theoritical)}">
							<td>${monthlyTheoriticalByEmployee.get(employee).get(month_theoritical)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>

				</g:each>		
			</tr>
			<tr>
				<td>Total H réalisées</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_actual'>						
					<g:if test='${monthlyActualByEmployee.get(employee)!=null}'>
						<g:if test="${monthList.contains(month_actual)}">
							<td>${monthlyActualByEmployee.get(employee).get(month_actual)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>		
			<tr>
				<td>Ecart HR - HT</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_ecart'>									
					<g:if test='${ecartByEmployee.get(employee)!=null}'>
						<g:if test="${monthList.contains(month_ecart)}">
							<td>${ecartByEmployee.get(employee).get(month_ecart)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>
			<tr>
				<td>RTT restant</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
					<g:if test='${rttByEmployee.get(employee)!=null}'>
						<g:if test="${monthList.contains(month_rtt)}">				
							<td>${rttByEmployee.get(employee).get(month_rtt)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>														
		</g:each>
	</tbody>
</table>
	<g:if test="${employeeInstanceTotal!=null && employeeInstanceTotal>20}">
		<div class="pagination" id="pagination">
			<g:hiddenField name="isAdmin" value="${isAdmin}" />
			<g:paginate total="${employeeInstanceTotal}"
				params="${[isAdmin:isAdmin]}" />
		</div>
	</g:if>
