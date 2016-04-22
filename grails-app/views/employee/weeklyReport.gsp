<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Period"%>
<%@ page import="pointeuse.Site"%>
<!doctype html>
<html>
<head>
	
	<g:javascript library="application"/> 		
	<r:require module="report"/>		
	<r:layoutResources/>		

  	
	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />	
	<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
	<title><g:message code="weekly.recap" /></title>
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
	<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>	
	<div id="daily-list" class="standardNav">
		<h1>
			<g:message code="weekly.recap"/>
			<br>
			<g:if test="${site!=null}"><g:message code="site.label"/>:${site.name}</g:if>
			<br>
			<g:form method="POST" url="[controller:'employee', action:'weeklyReport']">
				<ul>	
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /> </li>
					<li>	
						<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="siteId" from="${Site.list([sort:'name'])}"
								noSelection="${['':site.name]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="siteId" from="${Site.list([sort:'name'])}"
								noSelection="${['':'-']}" optionKey="id" optionValue="name"/>
						</g:else>	
					</li>
					<li class='datePicker'>	
						<g:select name="periodId" from="${Period.list([sort:'year'])}" id='periodSelect'
							value="${period}"
							noSelection="${['':(period?period:'-')]}" optionKey="id" 
							style="vertical-align: middle;" />
					</li>	
					<li>	
						<g:submitToRemote class="displayButton"
							value="Rapport"
							update="weeklyTable" 
							onLoading="document.getElementById('spinner').style.display = 'inline';"
			                onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'employee', action:'weeklyReport']"
						/>	
					</li>						
				</ul>
			</g:form>
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>	
	<div id="weeklyTable">
		<g:weeklyTime/>
	</div>
</body>
</html>
