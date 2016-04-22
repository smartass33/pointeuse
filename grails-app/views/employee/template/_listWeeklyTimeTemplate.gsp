<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.WeeklyTotal"%>
<%@ page import="java.util.Calendar"%>

<g:set var="calendarMonday" value="${Calendar.instance}"/>
<g:set var="calendarFriday" value="${Calendar.instance}"/>
<g:if test="${firstYear != null}">
	<%
		calendarMonday.set(Calendar.YEAR,firstYear)
		calendarFriday.set(Calendar.YEAR,firstYear)	
	 %>
</g:if>

<g:if test="${weekList != null &&  employeeInstanceList != null}">
	<table id="weekly-table" style="font-family: Helvetica;font-size: 14px;">
		<thead>
			<th>Semaine</th>
			<g:each in="${employeeInstanceList}" var="employee">			
				<th>${employee.lastName}</th>
			</g:each>
		</thead>
		<tbody id='body_table' style="border:1px;">
			<g:each in="${weekList}" status="i" var="weekNumber">	
				<%
					calendarMonday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)		
					calendarMonday.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
					calendarFriday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)
					calendarFriday.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY)
							
					if (weekNumber == 1){
						calendarMonday.set(Calendar.YEAR,lastYear)	
						calendarFriday.set(Calendar.YEAR,lastYear)				
					}
				%>	
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<td style="vertical-align: middle;text-align:center;">
						 ${calendarMonday.time.format('EE dd/MM/yyyy')}-${calendarFriday.time.format('EE dd/MM/yyyy')}					  
					</td>
	
					<g:each in="${employeeInstanceList}" var="currentEmployee">	
						<td style="vertical-align: middle;text-align:center;width:10px;">								
							<my:humanTimeTD id="weeklyTotalByEmployee" name="weeklyTotalByEmployee" value="${weeklyTotalsByWeek.get(weekNumber).get(currentEmployee)}"/>
						</td>
					</g:each>
					
				</tr>
			</g:each>		
		</tbody>
	</table>
</g:if>
