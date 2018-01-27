<table border="1" style="table-layout: fixed;width:100%;" >
	<tbody>
		<g:each in="${mileageBySiteMap}"  var="mileageBySite">
			<tr>
				<td style="vertical-align: middle;text-align: left;width:10px; text-transform: uppercase; background-color: #abbf78;" class="eventTD">${mileageBySite.key}</td>
				<g:each in="${infYear..supYear}" var="currentYear">
  					<td style="vertical-align: middle;text-align: left;width:10px; background-color: #abbf78;" class="eventTD" >${currentYear}</td>
				</g:each>
			</tr>
			<g:each status="i" in="${mileageBySite.value}" var="employeeMileageYearMap">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<td style="vertical-align: middle;text-align: left;width:10px;" class="eventTD">${employeeMileageYearMap.key.firstName} ${employeeMileageYearMap.key.lastName}</td>
					<g:each in="${employeeMileageYearMap.value}" var="employeeYearlyMileage">
						<g:each in="${employeeYearlyMileage}" var="mileage" >
							<td style="vertical-align: middle;text-align: left;width:10px;" class="eventTD">${mileage.value}</td>
						</g:each>
					</g:each>
				</tr>
			</g:each>
		</g:each>
	</tbody>
</table>

