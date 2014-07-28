<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<g:set var="calendar" value="${Calendar.instance}"/>

<table border="1" style="table-layout: fixed;padding:15px;width:100%;" id="reportTable">
	<thead>
		<th style='width:125px;'/>
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
				<g:if test="${month_th>5}">
					<th>${period.year}</th>
				</g:if>
				<g:else>
					<th>${period.year + 1}</th>		
				</g:else>
			</g:each>
		</thead>

	<tbody id='body_update' style="border:1px;">

		<tr>
			<td/>
			
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th_2'>
			
				<%= calendar.set(Calendar.MONTH,month_th_2-1) %>
				<td style="vertical-align: middle;">${calendar.time.format('MMM') }</td>
			</g:each>

		</tr>										
		<g:each in="${employeeInstanceList}" status="i" var="employee">
			<th colspan='13'>${employee.firstName} ${employee.lastName}</th>
			<tr>
				<td style="padding-left: 0em;padding: 2px 2px;">&Sigma; théorique</td>	
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
				<td>&Sigma; réalisées</td>
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
				<td>&Delta; HR - HT</td>
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
						<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
							<td>${rttByEmployee.get(employee).get(month_rtt)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
					<g:else>
						<td style="background-color:#CCCCCE"/>
					</g:else>
				</g:each>
			</tr>
			<tr>
				<td>&Delta; corrigé des RTT</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
					<g:if test='${ecartMinusRTTByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
							<td>${ecartMinusRTTByEmployee.get(employee).get(month_rtt)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
					<g:else>
						<td style="background-color:#CCCCCE"/>
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
