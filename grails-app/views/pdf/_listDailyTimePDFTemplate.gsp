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
					<th style="width:180px;text-align:center">${message(code: 'employee.lastName.label', default: 'Report')}</th>
					<th style="width:180px;text-align:center">${message(code: 'employee.firstName.label', default: 'Report')}</th>		
					<th style="width:120px;text-align:center">${message(code: 'employee.function.label', default: 'Report')}</th>
					<th style="width:120px;text-align:center">${message(code: 'employee.site.label', default: 'Report')}</th>
					<th style="width:90px;text-align:center">${message(code: 'employee.daily.time', default: 'Report')}</th>
					<th style="width:120px;text-align:center">${message(code: 'employee.monthly.sup.time', default: 'Report')}</th>
					<th  style="text-align:left" colspan="10" title="Evènements">${message(code: 'events.label', default: 'Site')}</th>
				</tr>
			</thead>
			<tbody id='body_update' style="border:1px;">
				<g:each in="${dailyMap}" status="i" var="entry">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td style="width:180px">${entry.key.lastName}</td>
						<td style="width:180px">${entry.key.firstName}</td>
						<td style="width:120px;text-align:center">${entry.key.function.name}</td>
						<td style="width:120px;text-align:center">${entry.key.site.name}</td>
						<td style="width:90px;text-align:center"><my:humanTimeTD id="entry_value" name="entry_value" value=" ${entry.value}"/></td>
						<td style="width:120px;text-align:center"><my:humanTimeTD id="entry_value" name="entry_value" value=" ${dailySupMap.get(entry.key)}"/></td>
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
	</body>
</html>