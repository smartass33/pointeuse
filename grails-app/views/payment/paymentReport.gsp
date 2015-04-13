<%@ page import="pointeuse.Employee"%>
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
			<ul>
				<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" /></a></li>
				<li><g:link class="logout" action="" controller="logout"><g:message code='admin.logout.label' default='Régul' />  </g:link></li>
				<g:if test="${fromAnnualReport && employee != null}">
						<li  style="vertical-align: middle;"><g:link controller="employee" action='annualReport' class="backButton" id="${employee.id}" params="${[userId:employee.id,siteId:siteId,periodId:periodId]}">${message(code: 'employee.annual.report.back.label', default: 'Report')}</g:link></li>				
				</g:if>
			</ul>
		</div>
		<div id="list-employee" class="standardNav">
			<h1>
				<g:message code="supplementary.time.label" /> 			
				<br>
				<br>
				<g:form method="GET" url="[controller:'payment', action:'paymentReport']">
					<ul>
						<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
						<li>	
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
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
								update="paymentTableDiv"
								onLoading="document.getElementById('spinner').style.display = 'inline';"  
								onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'payment', action:'paymentReport']" value="${message(code: 'default.search.label', default: 'List')}">
							</g:submitToRemote>
						<li><g:actionSubmit class='pdfButton' value="PDF"  action="getPaymentPDF"/></li>	
						
						<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">
							<li><g:actionSubmit class='pdfButton' value="PDF pour tous les sites"  action="getAllSitesPaymentPDF"/></li>
						</sec:ifAnyGranted>
									
					</ul>
				</g:form>		
			</h1>
		</div>
		<br>
		<div id="paymentTableDiv">
			<my:paymentManagementTable/>
		</div>
	</body>
</html>
