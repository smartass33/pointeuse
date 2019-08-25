<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Employee"%>
		<style  type="text/css">
			@page {
			   size: 210mm 297mm;
			   margin: 5px 5px 5px 5px;
			 }
			table {
			  font: normal 11px verdana, arial, helvetica, sans-serif;
			  color: #363636;
			  background: #f6f6f6;
			  background-color: white;
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
				height:20px;
			}
			thead th, tfoot th {
				padding:3px;
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
			 	font-size:80%;
			}			
			tbody th{
			 	text-align:center;
			 	height:5px;
			 	width:250px;
			}			
			.cartoucheValues{	
				border-collapse: collapse;
			 	border-width:1px; 
			 	border-style:solid; 
			 	border-color:black;
			}
			.cartoucheValues td{
				width: 100px;
				text-align: center;	
			}

		</style>
	</head>
	<body>
		<h1 style="text-align:center;font-size:130%">${message(code: 'employee.mileage.report.label')} du <g:formatDate format="dd MMMM yyyy" date="${minDate}"/> au <g:formatDate format="dd MMMM yyyy" date="${maxDate}"/></h1>
		<h1 style="text-align:center;font-size:130%">SALARIE: ${employee.lastName} ${employee.firstName}</h1>
		<BR/>
		<div style="position:absolute;width:98%;margin:1%;">
			<table  id="reportTable" style='border:1px solid black;width:400px;text-align:left;margin-left:auto;margin-right:auto;font-size: 120%;'>
		     	<thead>
				      <th style="border:1px; solid black; width:100px;text-align:center">${message(code: 'report.table.date.label', default: 'report')}</th>
				      <th style="border:1px;width:200px;text-align:center">${message(code: 'mileage.label', default: 'report')}</th>
				</thead>	    
				<tbody>
			        <g:each in="${mileageMap}" status="i" var="day">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td class="eventTD">${day.key.format('dd/MM/yyyy')}</td> 
							<td class="eventTD">${day.value}</td>
		          		</tr> 
		      		</g:each> 
		      			<tr>
		      				<td style="font-weight:bold;"><span style='font-weight:bold;text-transform: uppercase;'><B>${message(code: 'mileage.total')}:</B></span></td>
		      				<td style="font-weight:bold;">${totalPeriodMileage}</td>
		      			</tr>
		      	</tbody> 
	 
			</table>   
			<BR/>

			<div>
				<table>
					<thead></thead>
					<tr><td></td></tr>
					<tr><td></td></tr>	
					<tr><td></td></tr>
					<tr><td></td></tr>
							
					<tr>
						<td>${message(code: 'report.verification.date.label', default: 'Report')}: ${new Date().format('EEEE dd MMM yyyy')}</td>
					</tr>
					<tr>
						<td>${message(code: 'report.employee.signature.label', default: 'Report')}:</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						
						<td>${message(code: 'report.employer.signature.label', default: 'Report')}:</td>				
					</tr>
				</table>	  
			</div>
		</div>
		
		
	
	</body>
</html>