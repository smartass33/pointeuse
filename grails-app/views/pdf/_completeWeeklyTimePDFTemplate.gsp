<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Employee"%>
		<%@ page import="pointeuse.InAndOut"%>
		<%@ page import="pointeuse.AbsenceType"%>
		<%@ page import="pointeuse.MonthlyTotal"%>
		
		<g:set var="counter" value="${1}"/>
		<g:set var="j" value="${1}"/>
		<g:set var="eventColor" 	value=""/>
		<g:set var="calendarMonday" value="${Calendar.instance}"/>
		<g:set var="calendarSaturday" value="${Calendar.instance}"/>
		<g:if test="${firstYear != null}">
			<%
				calendarMonday.set(Calendar.YEAR,firstYear)
				calendarSaturday.set(Calendar.YEAR,firstYear)	
			 %>
		</g:if>
		
		<style  type="text/css">
			@page {
			   size: 297mm 210mm;
			   margin: 0px 0px 4px 0px;
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
			  padding:3px 2px 4px 0px;
			  color: #CC00FF;
			  text-transform: uppercase;
			}
			thead, tfoot {
				background:url(bg1.png) repeat-x;
				text-align:left;
				height:30px;
			}
			thead th, tfoot th {
				padding:2px;
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
			}
			
		.weeklyTable {
			  width: 5%;
			  float: left;
			}
			
		.weeklyTable tr { line-height: 6px; border: 1px solid black; }
		.weeklyTable tr td { line-height: 6px;width: 3px; }
		.weeklyTable tr td:first-child { line-height: 6px;width: 3px; }
		
		}
		</style>	
	</head>
	<body>
		<h1>		
			<g:message code="daily.recap.label"/> du ${currentDate.format('dd/MM/yyyy')}
				<br/>
				<br/>
				<g:if test="${site != null}"><g:message code="employee.site.label"/>: ${site.name}</g:if>
				<br/>
		</h1>
		<g:each in="${dayList}" var="day" status="i">			
			<table class="weeklyTable">
				<tr>
					<g:if test="${i == 0 || i == 3}">
						<th align="center"  colspan="${dayModelList[i].get('employeeSiteList').size()+1}">${day.format('EEE dd')}</th>
					</g:if>
					<g:else>
						<th align="center"  colspan="${dayModelList[i].get('employeeSiteList').size()}">${day.format('EEE dd')}</th>	
					</g:else>
				</tr>
				<tr >
					<g:if test="${i == 0 || i == 3}">
					<td></td>
					</g:if>
					<g:each in="${dayModelList[i].get('employeeSiteList')}" var="employee">
						<td style="font-size: xx-small;">${employee.lastName.take(2)}</td>
					</g:each>
				</tr>
				<g:each in="${dayModelList[i].get('statusMapByTime')}" var="items">
					<tr>	
						<g:if test="${i == 0 || i == 3}">
							<td style="font-size: xx-small;">${items.key.format('HH:mm')}</td>
						</g:if>
						<g:each in="${items.value}" var="listItem">
							<% eventColor = (listItem) ? '#00FF00' : '#D3D3D3' %>
							<td valign="middle" bgcolor="${eventColor}" style="color:${eventColor};vertical-align:middle;">.</td>
						</g:each>
					</tr>
				</g:each>			
			</table>
		</g:each>
	</body>
</html>