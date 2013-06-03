<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Absence"%>
<%@ page import="java.util.Calendar"%>



<!doctype html>
<html>
<head>
<meta name="layout" content="main">
	<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />
	<g:javascript library="jquery" plugin="jquery" />

<title>
	${message(code: 'employee.report.label', default: 'Report')}
</title>
 <link href="main.css" rel="stylesheet" type="text/css">
	<g:set var="calendar" value="${Calendar.instance}"/>

</head>
<body>
<body>

	<div class="nav" id="nav">
		<g:headerMenu />
	</div>
	<div id="list-employee" class="content scaffold-list">
		<h1>
			<g:message code="daily.recap.label"/>
			<br>
			<g:if test="${site!=null}"><g:message code="site.label"/>:${site.name}</g:if>
			<br>
			<g:form method="POST"
				url="[controller:'employee', action:'pdf']">
				<g:message code="laboratory.label" default="Search"
					style="vertical-align: middle;" />	
				<richui:dateChooser name="currentDate" format="dd/MM/yyyy" value="${period ? period : new Date()}" locale="fr" firstDayOfWeek="Mo"/>					
				
				<g:submitToRemote class="listButton"
					value="rapport"
					update="monthlyTable" 
					url="[controller:'employee', action:'annualReport']"
					/>
				<g:actionSubmit class="listButton" value="export PDF" action="annualTotalPDF"/>
				<g:hiddenField name="isAdmin" value="${isAdmin}" />
				<g:hiddenField name="siteId" value="${siteId}" />
				<g:hiddenField name="userId" value="${userId}" />
				
			</g:form>

			
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>	
	
	<div id="monthlyTable">
		<g:annualReportTable/>
	
	</div>

</body>
</html>
