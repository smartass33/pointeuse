<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<!doctype html>
<html>
<head>
	<g:javascript library="jquery" plugin="jquery" />
	<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />

	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />
	
	<g:set var="entityName"
		value="${message(code: 'employee.label', default: 'Employee')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
		<style type="text/css">
			body {
				font-family: Verdana, Arial, sans-serif;
				font-size: 0.9em;
			}
			table {
				border-collapse: collapse;
			}
			thead {
				background-color: #DDD;
			}
			td {
				padding: 2px 4px 2px 4px;
			}
			th {
				padding: 2px 4px 2px 4px;
			}

		</style>
</head>
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
				<g:if test="${siteId != null && !siteId.equals('')}">
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':site.name]}" optionKey="id" optionValue="name"
						style="vertical-align: middle;" />
				</g:if>
				<g:else>
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':'-']}" optionKey="id" optionValue="name"/>
				</g:else>	
				<richui:dateChooser name="currentDate" format="dd/MM/yyyy" value="${period ? period : new Date()}" locale="fr" firstDayOfWeek="Mo"/>					
				
				<g:submitToRemote class="listButton"
					value="rapport"
					update="dailyTable" 
					url="[controller:'employee', action:'dailyReport']"
					/>
				<g:actionSubmit class="listButton" value="export PDF" action="dailyTotalPDF"/>
				<g:hiddenField name="isAdmin" value="${isAdmin}" />
				<g:hiddenField name="siteId" value="${siteId}" />
			</g:form>

			
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>	
	
	<div id="dailyTable">
		<g:listDailyTime/>
	</div>
</body>
</html>
