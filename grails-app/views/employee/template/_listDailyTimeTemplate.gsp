<%@ page import="pointeuse.Employee"%>

	<table id="employee-table" style="font-family: Helvetica;font-size: 14px;">
	<thead>
		<tr>
			<th style="width:90px;text-align:center">${message(code: 'employee.lastName.label', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.site.localisation', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.function.label', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'service.label', default: 'Report')}</th>
			<th style="width:90px;text-align:center">${message(code: 'employee.daily.time.short', default: 'Report')}</th>
			<th  style="width:800px;text-align:center" title="Evènements">${message(code: 'events.label', default: 'Site')}</th>
		</tr>
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${dailyMap}" status="i" var="entry">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:90px;text-align:center;valign:middle;">${entry.key.lastName}<BR>${entry.key.firstName}</td>
				<td style="width:120px;text-align:center;valign:middle;">${(localisationMap.get(entry.key))}</td>
				<td style="width:120px;text-align:center;valign:middle;">${entry.key.function.name}</td>
				<td style="width:120px;text-align:center;valign:middle;">${entry.key.service.name}</td>
				<td style="width:90px;text-align:center"><my:humanTimeTD id="dailyTime"  name="dailyTime" value="${entry.value}"/><BR>(HS:<my:humanTimeTD id="supTime"  name="supTime" value="${dailySupMap.get(entry.key)}"/>)</td>				
				<g:if test="${maxSize != null && maxSize > 0 }">
					<td style="height: 25px;valign:middle;">
						<g:if test="${inAndOutsForEmployeeMap.get(entry.key) != null}">
							<div id="timeline-${entry.key.id}"  class="time-slider"></div>
							
							<script>
					        var current_time = (new Date()).getTime() + ((new Date()).getTimezoneOffset() * 60 * 1000 * -1);
								$(document).ready(function () {
									(function () {
						 				var container = document.getElementById('timeline-${entry.key.id}');																					
										var inAndOutsForEmployeeTable_${entry.key.id} = ${inAndOutsForEmployeeMap.get(entry.key)};
										var init_cells_${entry.key.id} = [];
										var item;
										var momentDate;
										var rowCount = 0;
										
										var start_date = moment(${startDate}).toDate();
										start_date.setHours(5);
										start_date.setMinutes(0);
										//alert('inAndOutsForEmployeeTable_${entry.key.id}')
										for (j = 0; j < (inAndOutsForEmployeeTable_${entry.key.id}).length;j++){
											
										   if (inAndOutsForEmployeeTable_${entry.key.id}[j] !== null){
										   	
										   		momentDate = moment.utc(inAndOutsForEmployeeTable_${entry.key.id}[j+1]);	
										   		if ( (j % 2) === 0){
										   												
										   				if (moment(inAndOutsForEmployeeTable_${entry.key.id}[j+1]) !== null){
												   			item = {
												   				'_id':'c'+j,
												   			 	'start':(moment.utc(inAndOutsForEmployeeTable_${entry.key.id}[j])).toDate().getTime(),
												   			 	'stop':(moment.utc(inAndOutsForEmployeeTable_${entry.key.id}[j+1])).toDate().getTime()
												   			 	};		
												   		}else{
												   			item = {
												   				'_id':'c'+j,
												   			 	'start':(moment(inAndOutsForEmployeeTable_${entry.key.id}[j])).toDate().getTime(),
												   			 	};													   		
												   		}
									   				init_cells_${entry.key.id}.push(item);		
										   		}		  
										   }
										} 
						
										$(container).TimeSlider({
											start_timestamp: start_date.getTime(),
											timecell_enable_move:false,
											timecell_enable_resize:false,
											ruler_enable_move:false,
											hours_per_ruler:20,
											distance_between_gtitle:40,
											graduation_step:20,
											init_cells: init_cells_${entry.key.id}
										});
								    })();
								});
							</script>
						</g:if>
					</td>
				</g:if>
			</tr>
		</g:each>
		<g:each in="${sickEmployeeList}" status="i" var="sickEmployee">
			<tr>
				<td style="width:90px;text-align:center;valign:middle;">${sickEmployee.lastName}<BR>${sickEmployee.firstName}</td>
				<td style="width:120px;text-align:center;valign:middle;">-</td>
				<td style="width:120px;text-align:center;valign:middle;">${sickEmployee.function.name}</td>
				<td style="width:120px;text-align:center;valign:middle;">${sickEmployee.service.name}</td>
				<td>ARRET MALADIE</td>
				<td>n/a</td>
			</tr>
		</g:each>
	</tbody>
</table>

