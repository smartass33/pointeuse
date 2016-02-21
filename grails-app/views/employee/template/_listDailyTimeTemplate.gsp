<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>




	<table id="employee-table">
	<thead>
		<tr>
			<th style="width:180px;text-align:center">${message(code: 'employee.lastName.label', default: 'Report')}</th>
			<th style="width:180px;text-align:center">${message(code: 'employee.firstName.label', default: 'Report')}</th>		
			<th style="width:120px;text-align:center">${message(code: 'employee.function.label', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.site.label', default: 'Report')}</th>
			<th style="width:90px;text-align:center">${message(code: 'employee.daily.time', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.monthly.sup.time', default: 'Report')}</th>
			<th  style="text-align:left" colspan="20" title="Evènements">${message(code: 'events.label', default: 'Site')}</th>
		</tr>
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${dailyMap}" status="i" var="entry">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:180px">${entry.key.lastName}</td>
				<td style="width:180px">${entry.key.firstName}</td>
				<td style="width:120px;text-align:center">${entry.key.function.name}</td>
				<td style="width:120px;text-align:center">${entry.key.site.name}</td>
				<td style="width:90px;text-align:center"><my:humanTimeTD id="dailyTime"  name="dailyTime" value="${entry.value}"/></td>
				<td style="width:120px;text-align:center"><my:humanTimeTD id="supTime"  name="supTime" value="${dailySupMap.get(entry.key)}"/></td>		
				
				<g:if test="${maxSize != null && maxSize > 0 }">
					<td>
						<div id="timeline-${entry.key.id}" style="height: 40px;"></div>
						<script>
						     google.charts.setOnLoadCallback(drawChart);
						     function drawChart() {
						      // var id = '${entry.key.id}';
						       var container = document.getElementById('timeline-${entry.key.id}');
						       var chart = new google.visualization.Timeline(container);
						       var dataTable_${entry.key.id} = new google.visualization.DataTable();
							   var inAndOutsForEmployeeTable_${entry.key.id} = ${inAndOutsForEmployeeMap.get(entry.key)};
								//alert(inAndOutsForEmployeeTable_${entry.key.id});
	
							   
							   //(dataTable_${entry.key.id}).addColumn({ type: 'string', id: 'employee' });
							   (dataTable_${entry.key.id}).addColumn({ type: 'string', id: 'Content' });
							   (dataTable_${entry.key.id}).addColumn({ type: 'date', id: 'Start' });
							   (dataTable_${entry.key.id}).addColumn({ type: 'date', id: 'End'});
							  
							   
								//alert((inAndOutsForEmployeeTable_${entry.key.id}).length);
						     //  for (i = 0; i < (inAndOutsForEmployeeTable_${entry.key.id}).length; i++) { 
						     //  		if ( (i%2) === 0){						       	
						     //  			(dataTable_${entry.key.id}).addColumn({ type: 'date', id: 'Start'+i });
						     //  		}else{
						     //  			(dataTable_${entry.key.id}).addColumn({ type: 'date', id: 'End'+i });
						     //  		}
						       		
							//	}
								
								//alert((dataTable_${entry.key.id}).getNumberOfColumns());
								//var employee_name = '${entry.key.lastName}';
								//(datesForEmployeeTable_${entry.key.id}).push('${entry.key.lastName}');
								//(dataTable_${entry.key.id}).addRows(1);
								//(dataTable_${entry.key.id}).setValue(0,0,'${entry.key.lastName}');
						
								//alert((inAndOutsForEmployeeTable_${entry.key.id}).length);
								var rowCount = 0;
								for (j = 0; j < (inAndOutsForEmployeeTable_${entry.key.id}).length;j++){
									
								   if (inAndOutsForEmployeeTable_${entry.key.id}[j] !== null){

								   		var momentDate = moment(inAndOutsForEmployeeTable_${entry.key.id}[j]);
								   		
								   		if ( (j % 2) === 0){
								   			(dataTable_${entry.key.id}).addRows(1);
								   			(dataTable_${entry.key.id}).setValue(rowCount,1,momentDate.toDate());							   			   			
								   			(dataTable_${entry.key.id}).setValue(rowCount,0,'t');	
								   			
								   			
								   		}else{
								   			(dataTable_${entry.key.id}).setValue(rowCount,2,momentDate.toDate());							   			
								   			rowCount++;
								   				
								   		}		  
								   }

								} 

						       chart.draw(dataTable_${entry.key.id});
				
							}
						</script>
					</td>
				</g:if>
				<g:each in="${dailyInAndOutMap.get(entry.key)}" var="inOrOut">
					<g:if test="${inOrOut.type.equals('E')}">
						<td bgcolor="98FB98">${inOrOut.time.format('HH:mm')}</td>
					</g:if>
					<g:else>
						<td bgcolor="#FFC0CB">${inOrOut.time.format('HH:mm')}</td>
					</g:else>
				</g:each>
			</tr>
		</g:each>
	</tbody>
</table>

