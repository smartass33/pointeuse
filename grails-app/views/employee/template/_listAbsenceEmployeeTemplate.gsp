<g:set var="maxCal" value="${Calendar.instance}"/>
<g:set var="lastDayOfMonth" value="${0}"/>


<g:if test="${isDailyView}">
	<h1>${message(code: 'absence.report.daily.label', default: 'Employee')}: ${currentDate.format('dd/MM/yyyy')}</h1>
	<table style="width:100%;" >
		<thead>
			<th>${message(code: 'employee.lastName.label', default: 'Employee')}</th>			
			<th style="width:90px;">${message(code: 'employee.site.label', default: 'Employee')}</th>
			<th style="width:90px;">${message(code: 'employee.function.label', default: 'Employee')}</th>	
			<th>${message(code: 'absence.report.absence.type', default: 'Employee')}</th>
		</thead>
		<tbody>
			<g:each in="${absenceList}" var="absence" status='i'>
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
					<td>${absence.employee.lastName}</td>
					<td>${absence.employee.site.name}</td>
					<td>${absence.employee.function.name}</td>
					<td>${absence.type}</td>
				</tr>
			</g:each>
		</tbody>	
	</table>
</g:if>

<g:if test="${isMonthlyView}">
	<h1>${message(code: 'absence.report.monthly.label', default: 'Employee')}: ${currentDate.format('MMMM yyyy')}</h1>
	<% 
		maxCal.time = currentDate
		lastDayOfMonth = maxCal.getActualMaximum(Calendar.DAY_OF_MONTH)
	%>
	<table style="width:100%;" >
		<thead>
			<th style="width:180px;">${message(code: 'employee.lastName.label', default: 'Employee')}</th>
			<th style="width:90px;">${message(code: 'employee.site.label', default: 'Employee')}</th>
			<th style="width:90px;">${message(code: 'employee.function.label', default: 'Employee')}</th>		
			<th colspan="${lastDayOfMonth}">${message(code: 'absence.report.absence.type', default: 'Employee')}</th>
		</thead>
		<tbody>
			<tr>
				<td style="width:180px;" class="eventTD" colspan="3" >
					${currentDate.format('MMMM yyyy')}
				</td>
				<g:each in="${1..lastDayOfMonth}" var="day" >
					<td class="eventTD" >
						${day}
					</td>			
				</g:each>		
			</tr>
			<g:each in="${absenceMapByEmployee}" var="item" status='i'>
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
					<td style="width:180px;">${item.key.lastName}</td>
					<td style="width:90px;">${item.key.site.name}</td>
					<td style="width:90px;">${item.key.function.name}</td>		
					<g:each in="${1..lastDayOfMonth}" var="day" >
						<td class="eventTD" style="font-size: 80%;vertical-align:middle;">
								<g:if test="${absenceMapByEmployee.get(item.key).get(day) != null}">
									${absenceMapByEmployee.get(item.key).get(day).type}
								</g:if>
								<g:else>
									-
								</g:else>
						</td>
					</g:each>		
				</tr>
			</g:each>
		</tbody>	
	</table>
</g:if>

<g:if test="${isYearlyView}">
	<h1>${period.year} / ${period.year + 1}</h1>
	<% 
		maxCal.time = currentDate
		lastDayOfMonth = maxCal.getActualMaximum(Calendar.DAY_OF_MONTH)
	%>
	<table style="width:100%;" >
		<thead>
			<th style="width:180px;">${message(code: 'employee.lastName.label', default: 'Employee')}</th>
			<th style="width:180px;">${message(code: 'employee.site.label', default: 'Employee')}</th>
			<th style="width:180px;">${message(code: 'employee.function.label', default: 'Employee')}</th>
			<th colspan="${lastDayOfMonth}">${message(code: 'absence.report.absence.type', default: 'Employee')}</th>
		</thead>
		<tbody>
			<tr>
				<td style="width:180px;" class="eventTD" colspan="3">
					${period.year} / ${period.year + 1}
				</td>					
				<g:each in="${dayList}" var="day" >
					<td class="eventTD" >
						${day}
					</td>			
				</g:each>		
			</tr>
			<g:each in="${absenceMapByEmployee}" var="item" status='i'>
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
					<td style="width:180px;">${item.key.lastName}</td>
					<td style="width:180px;">${item.key.site.name}</td>
					<td style="width:180px;">${item.key.function.name}</td>
					<g:each in="${dayList}" var="day" >
						<td class="eventTD" style="font-size: 80%;vertical-align:middle;">
								<g:if test="${absenceMapByEmployee.get(item.key).get(day) != null}">
									<g:if test="${!(absenceMapByEmployee.get(item.key).get(day)).equals('-')}">
										${absenceMapByEmployee.get(item.key).get(day).type}
									</g:if>
									<g:else>
										${absenceMapByEmployee.get(item.key).get(day)}
									</g:else>
									
								</g:if>
								<g:else>
									-
								</g:else>
						</td>
					</g:each>		
				</tr>
			</g:each>
		</tbody>	
	</table>
</g:if>
