<%@ page import="pointeuse.Mileage" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.Period" %>

<!DOCTYPE html>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery" />
	
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'mileage.label', default: 'Milage')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<ui:resources includeJQuery="true"/>
		
	</head>
	<body>
		<a href="#list-milage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="logout" action="" controller="logout"><g:message code='admin.logout.label' default='Régul' />  </g:link></li>
				
			</ul>
		</div>
	<div id="site-mileage" class="standardNav">
		<h1>	
				<g:if test="${site}"><g:message code="site..followup" /> ${site.name}</g:if>
				<g:else><g:message code="mileage.report.label" /></g:else>
				<br>
				<br>
				<g:form method="POST" url="[controller:'mileage', action:'siteMileagePDF']">
					<ul>
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
					<li>	
						 <ui:multiSelect 
						 	name="sites"
		                     multiple="yes"
		                     from="${Site.list()}" 
		                     optionValue="name"
		                     isLeftAligned="true"
		                     noSelection="['':'Selection']" />
					</li>							
					<li style="vertical-align: bottom;" class="datePicker">
						<g:datePicker name="infYear" value="${period ? period : new Date()}"
						relativeYears="[-4..0]"
						 precision="year" noSelection="['':'-Choisissez-']"/>
					</li>
					
					<li style="vertical-align: bottom;" class="datePicker">
						<g:datePicker name="supYear" value="${period ? period : new Date()}"
						relativeYears="[-4..0]"
						 precision="year" noSelection="['':'-Choisissez-']"/>
					</li>
					
					<li>	
						<g:submitToRemote class='displayButton' 
							update="mileageFollowupDiv"
							onLoading="document.getElementById('spinner').style.display = 'inline';"  
							onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'mileage', action:'index']" value="${message(code: 'default.search.label')}">
						</g:submitToRemote>
					<li>	
						<g:actionSubmit class='pdfButton' value="PDF" controller="mileage" action="siteMileagePDF"/>				
					</li>	
					<li>
						<g:actionSubmit class='excelButton' value="excel"  action="siteMileageEXCEL"/>	
						
					</li>		
					<g:if test="${site != null}">	
						<g:hiddenField name="site.id" value="${site.id}" />			
					</g:if>							
					<g:hiddenField name="siteId" value="${siteId}" />
					<g:hiddenField name="isAdmin" value="${isAdmin}" />		
					<g:hiddenField name="fromMileagePage" value="${true}" />					
					</ul>
				</g:form>		
			</h1>
		</div>
		
		<div id="mileageFollowupDiv">
			<my:mileageSiteTable/>
		</div>
	</body>
</html>
