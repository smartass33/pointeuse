<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Employee"%>
		<%@ page import="pointeuse.InAndOut"%>
		<%@ page import="pointeuse.AbsenceType"%>
		<%@ page import="pointeuse.MonthlyTotal"%>
		<style  type="text/css">
			@page {
			   size: 297mm 210mm;
			   margin: 0px 0px 13px 0px;
			 }
			table {
			  font: normal 11px verdana, arial, helvetica, sans-serif;
			  color: #363636;
			  background: #f6f6f6;
			  text-align:left;
			  }
			caption {
			  text-align: center;
			  font: bold 16px arial, helvetica, sans-serif;
			  background: transparent;
			  padding:6px 4px 8px 0px;
			  color: #CC00FF;
			  text-transform: uppercase;
			}
			thead, tfoot {
				background:url(bg1.png) repeat-x;
				text-align:left;
				height:30px;
			}
			thead th, tfoot th {
				padding:5px;
			}
			table a {
				color: #333333;
				text-decoration:none;
			}
			table a:hover {
				text-decoration:underline;
			}
			tr.odd {
				background: #f1f1f1;
			}
			tbody td {
			 	text-align:center;
			 	height:5px;
			 	width:90px;
			 	font-size:95%;
			}
			
			tbody th{
			 	text-align:center;
			 	height:5px;
			 	width:250px;
			}

		
		</style>	
		

	<link href="${grailsApplication.config.context}/css/timeslider.css" rel="stylesheet"/>

	<script src="${grailsApplication.config.context}/js/timeslider.js"></script>
  	
	</head>
	<body>
		<h1>		
			<g:message code="daily.recap.label"/> du ${currentDate.format('dd/MM/yyyy')}
				<br/>
				<br/>
				<g:if test="${site!=null}"><g:message code="employee.site.label"/>: ${site.name}</g:if>
				<br/>
		</h1>
		<table id="employee-table">
	<thead>
		<tr>
			<th style="width:90px;text-align:center">${message(code: 'employee.lastName.label', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.function.label', default: 'Report')}</th>
			<th  style="width:800px;text-align:center" title="Evènements">${message(code: 'events.label', default: 'Site')}</th>
		</tr>
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${dailyMap}" status="i" var="entry">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:90px;text-align:center;valign:middle;">${entry.key.lastName} <BR/>${entry.key.firstName}</td>
				<td style="width:120px;text-align:center;valign:middle;">${entry.key.function.name}</td>
				
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
	</tbody>

		</table>
	</body>
</html>