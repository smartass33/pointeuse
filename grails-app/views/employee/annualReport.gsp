<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Absence"%>
<%@ page import="java.util.Calendar"%>



<!doctype html>
<html>
<head>
<meta name="layout" content="main">

<title>
	${message(code: 'employee.report.label', default: 'Report')}
</title>
 <link href="main.css" rel="stylesheet" type="text/css">
	<g:set var="calendar" value="${Calendar.instance}"/>

</head>
<body>
	<a href="#list-employee" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>

	<div id="list-employee" class="content scaffold-list">
		<h1>
			${message(code: 'employee.global.report.label', default: 'Report')}
		</h1>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li><g:link class="list" action="list">
						${message(code: 'employee.back.to.list.label', default: 'List')}
					</g:link></li>
					
				<form>
					<li>${message(code: 'default.period.label', default: 'List')}: <g:datePicker name="myDate" value="${new Date()}" precision="year" noSelection="['':'-Choose-']"/></li>
					<li><g:actionSubmit value="report" action="annualReport"/></li>
					<g:hiddenField name="userId" value="${userId}" />
					
				</form>
			</ul>
		</div>

		<g:if test="${flash.message}">
			<div class="message">
				${flash.message}
			</div>
		</g:if>
			<h1><g:message code="employee.annual.report" default="report"/> ${employee.firstName} pour l'annnée ${period}</h1>
			<table border="1" cellpadding="0" cellspacing="2" >
				<thead>
					<th width="50px" align="center">Semaine</th>
					<th width="50px">Total constaté</th>
					<th width="50px">Total théorique</th>	
								
				</thead>
				<tbody >	
					<g:each in="${mapByWeek}" var="weekNumber">
						<tr>
							<td>semaine ${weekNumber.key}
							<%calendar.set(Calendar.WEEK_OF_YEAR,weekNumber.key);calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)%> 
							 (${calendar.time.format('dd MMM')} -
							 <%calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY)%>
							 ${calendar.time.format('dd MMM')})
							 </td>
							 <td>${weekNumber.value.get(0)} h ${weekNumber.value.get(1)} min ${weekNumber.value.get(2)} s </td>
						</tr>
					</g:each>

				</tbody>
			</table>
	</div>
</body>
</html>
