<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Vacation"%>
<%@ page import="pointeuse.Period"%>

<!doctype html>
<html>
<head>
	<g:javascript library="jquery" plugin="jquery" />
	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />
	<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
	<title><g:message code="absence.followup"/></title>
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
	<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>
	<div class="nav" id="nav">
		<g:headerMenu />
	</div>
	<div id="list-employee" class="standardNav">
		<h1>
			<g:message code="vacation.followup" /> <g:if test="${site}">pour le site ${site.name}</g:if>
		
			<br>
			<br>
			<g:form method="POST" url="[controller:'employee', action:'vacationFollowup']">
				<ul>
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
					<li>
						<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':site.name]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':(site?site.name:'-')]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />						
						</g:else>				
					</li>
					<li class="datePicker">
						<g:select name="year" from="${Period.list([sort:'year'])}"
							value="${period}"
							noSelection="${['':(period?period:'-')]}" optionKey="id" 
							style="vertical-align: middle;" />					
					</li>
					<li><g:actionSubmit class='displayButton' value="${message(code: 'default.search.label', default: 'List')}"  action="vacationFollowup"/></li>		
					<li><g:actionSubmit class='displayButton' value="vue mensuelle"  action="monthlyVacationFollowup"/></li>		

					<g:hiddenField name="isAdmin" value="${isAdmin}" />
					<g:if test="${period!=null}">	
						<g:hiddenField name="year" value="${period.id}" />	
					</g:if>
				</ul>	
			</g:form>
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>
	<br>
	<div id="vacationList"><g:listVacationEmployee/></div>
	<g:if test="${employeeInstanceTotal!=null}">
		<div class="pagination" id="pagination">
			<g:hiddenField name="isAdmin" value="${isAdmin}" />
			<g:paginate total="${employeeInstanceTotal}" params="${[isAdmin:isAdmin,siteId:siteId]}" />
		</div>
	</g:if>
</body>
</html>