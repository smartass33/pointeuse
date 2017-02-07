<%@ page import="pointeuse.Period"%>
<%@ page import="pointeuse.Mileage" %>
<%@ page import="pointeuse.Site" %>
<!DOCTYPE html>
<html>
	<head>
		<g:javascript library="jquery"/> 		
		
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'mileage.label', default: 'Mileage')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-milage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-milage" class="content scaffold-list" role="main">		
		<div id="ecart-list" class="standardNav">
			<h1>
				<g:message code="mileage.followup" /> 			
				<br>
				<br>
				<g:form method="POST" url="[controller:'mileage', action:'employeeMileage']" >
					<ul>
						<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
						<li>	
							<g:select name="siteId" from="${Site.list([sort:'name'])}"
								value="${site}"
								noSelection="${['':(site?site.name:'-')]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />						
						</li>
						<li class='datePicker'>	
							<g:select name="year" from="${Period.list([sort:'year'])}"
								value="${period}"
								noSelection="${['':(period?period:'-')]}" optionKey="id" 
								style="vertical-align: middle;" />
						</li>
						<li>	
							<g:submitToRemote class='displayButton' 
								update="mileageTableDiv"
								onLoading="document.getElementById('spinner').style.display = 'inline';"  
								onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'mileage', action:'list']" value="${message(code: 'default.search.label', default: 'List')}">
							</g:submitToRemote>
						<g:hiddenField name="isAdmin" value="${isAdmin}" />
						<g:if test="${site != null}">	
							<g:hiddenField name="siteId" value="${site.id}" />		
						</g:if>
						<g:if test="${period != null}">	
							<g:hiddenField name="periodId" value="${period.id}" />			
						</g:if>			
					</ul>
				</g:form>		
			</h1>
		</div>
		<br>
		<div id="mileageTableDiv">
			<my:mileageManagementTable/>
		</div>
	</body>
</html>
