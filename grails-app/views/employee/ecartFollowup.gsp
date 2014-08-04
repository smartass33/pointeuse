<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Period"%>
<!doctype html>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery" />
		<meta name="layout" content="main" id="mainLayout">
		<g:set var="isNotSelected" value="true" />	
		<title><g:message code="ecart.followup" /></title>
	</head>
	<body>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>	
		<div class="nav" id="nav">
			<g:headerMenu />
		</div>
		<div id="list-employee" class="standardNav">
			<h1>
				<g:message code="ecart.followup" /> 			
				<g:if test="${site}">pour le site ${site.name}</g:if>
				<br>
				<br>
				<g:form method="POST" url="[controller:'employee', action:'ecartFollowup']">
					<ul>
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
					<li>	<g:if test="${siteId != null && !siteId.equals('')}">
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
					<li class='datePicker'>	
						<g:select name="year" from="${Period.list([sort:'year'])}"
							value="${period}"
							noSelection="${['':(period?period:'-')]}" optionKey="id" 
							style="vertical-align: middle;" />
					</li>
					<li>	
						<g:submitToRemote class='displayButton' 
							update="ecartTableDiv"
							onLoading="document.getElementById('spinner').style.display = 'inline';"  
							onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'employee', action:'ecartFollowup']" value="${message(code: 'default.search.label', default: 'List')}">
						</g:submitToRemote>
					<li>	<g:actionSubmit class='pdfButton' value="PDF"  action="ecartPDF"/>				</li>			
						<g:hiddenField name="isAdmin" value="${isAdmin}" />
						<g:if test="${site!=null}">	
							<g:hiddenField name="site.id" value="${site.id}" />		
						</g:if>
						<g:hiddenField name="siteId" value="${siteId}" />
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
		<div id="ecartTableDiv"><g:ecart/></div>
	</body>
</html>
