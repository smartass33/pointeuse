<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<g:set var="calendar" value="${Calendar.instance}"/>

<table border="1" style="table-layout: fixed;width:100%;"  >
	<thead>
		<th class="eventTD" />
		<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
			<g:if test="${month_th>5}">
				<th class="eventTD" style="width:50px">${period.year}</th>
			</g:if>
			<g:else>
				<th class="eventTD" style="width:50px">${period.year + 1}</th>		
			</g:else>
		</g:each>		
	</thead>
	<tbody id='body_update' style="border:1px;">
		<tr>
			<td class="eventTD" />		
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th_2'>		
				<%= calendar.set(Calendar.MONTH,month_th_2-1) %>
				<td class="eventTD" style="vertical-align: middle;">${calendar.time.format('MMM') }</td>
			</g:each>
		</tr>										
		<g:each in="${employeeInstanceList}" status="i" var="employee">
			<th colspan='13' class="eventTD" style="text-align:left;font-weight:bold;">${employee.firstName} ${employee.lastName}</th>
			<tr>
				<td class="eventTD" style="text-align:left;">&Sigma; théorique</td>	
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_theoritical'>	
					<g:if test="${monthlyTheoriticalByEmployee.get(employee)!=null}">
						<g:if test="${monthList.contains(month_theoritical)}">
							<td class="eventTD">${monthlyTheoriticalByEmployee.get(employee).get(month_theoritical)}</td>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>		
			</tr>
			<tr>
				<td class="eventTD" style="text-align:left;">&Sigma; réalisées</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_actual'>						
					<g:if test='${monthlyActualByEmployee.get(employee)!=null}'>
						<g:if test="${monthList.contains(month_actual)}">
							<td class="eventTD">${monthlyActualByEmployee.get(employee).get(month_actual)}</td>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>		
			<tr>
				<td class="eventTD" style="text-align:left;">&Delta; HR - HT</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_ecart'>									
					<g:if test='${ecartByEmployee.get(employee)!=null}'>
						<g:if test="${monthList.contains(month_ecart)}">
							<td class="eventTD">${ecartByEmployee.get(employee).get(month_ecart)}</td>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>
			<tr>
				<td class="eventTD" style="text-align:left;">RTT restant</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
					<g:if test='${rttByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
							<td class="eventTD">${rttByEmployee.get(employee).get(month_rtt)}</td>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
					<g:else>
						<td class="eventTD" style="background-color:#CCCCCE"/>
					</g:else>
				</g:each>
			</tr>
			<tr>
				<td class="eventTD" style="text-align:left;">&Delta; corrigé des RTT</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
					<g:if test='${ecartMinusRTTByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
							<td class="eventTD">${ecartMinusRTTByEmployee.get(employee).get(month_rtt)}</td>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
					<g:else>
						<td class="eventTD" style="background-color:#CCCCCE"/>
					</g:else>
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
