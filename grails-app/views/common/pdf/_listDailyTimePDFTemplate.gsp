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
			<g:sortableColumn property="lastName" style="width:120px;text-align:left"
				title="${message(code: 'employee.lastName.label', default: 'Last Name')}" />
			<g:sortableColumn property="firstName" style="width:120px;text-align:left"
				title="${message(code: 'employee.firstName.label', default: 'First Name')}" />	
			<g:sortableColumn property="site" style="width:120px;text-align:left"
				title="${message(code: 'employee.site.label', default: 'Site')}" />
			<g:sortableColumn property="total jour" style="text-align:left"
				title="Total Jour"/>	
			<g:sortableColumn property="Heures supplementaires" style="text-align:left"
				title="Heures supplementaires"/>	
			<th  style="text-align:left" colspan="10"
				title="Evènements">Evènements</th>
		</tr>
	</thead>

	<tbody id='body_update' style="border:1px;">
		<g:each in="${dailyMap}" status="i" var="entry">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:120px;text-align:left">${entry.key.lastName}</td>
				<td style="width:120px;text-align:left">${entry.key.firstName}</td>
				<td style="width:120px;text-align:left">${entry.key.site.name}</td>
				<td style="width:120px;text-align:left">
					<g:if test='${entry.value.get(0)<10}'>0</g:if>${entry.value.get(0)} :
					<g:if test='${entry.value.get(1)<10}'> 0</g:if>${entry.value.get(1)} 
				</td>
				<td style="width:120px;text-align:left">
					<g:if test='${(dailySupMap.get(entry.key)).get(0)<10}'>0</g:if>${(dailySupMap.get(entry.key)).get(0)} :
					<g:if test='${(dailySupMap.get(entry.key)).get(1)<10}'> 0</g:if>${(dailySupMap.get(entry.key)).get(1)} 
				</td>
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