<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Period"%>
<%@ page import="java.util.Calendar"%>

<!doctype html>
<html>
	<head>
	
	<g:javascript>
		function ajaxCall(){
			if (document.getElementById("siteSelect") != null && document.getElementById("siteSelect") !== undefined && document.getElementById("periodSelect") != null && document.getElementById("periodSelect") !== undefined){
				site = document.getElementById("siteSelect").value;
				period = document.getElementById("periodSelect").value;
				alert("site is not null: "+site);
				$(function(){$('#details').load('${createLink(controller:'site', action:'getAjaxSiteData',params:[year:year,month:month])}&site='+site+'&period='+period);});
			}
		}
	</g:javascript>
		<g:javascript library="jquery" plugin="jquery" />
		<meta name="layout" content="main" id="mainLayout">
		<g:set var="isNotSelected" value="true" />	
		<title><g:message code="site.total.time" /></title>
	</head>
	<body>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>	
		<div class="nav" id="nav">
			<ul>
				<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" /></a></li>
				<li><g:link class="logout" action="" controller="logout"><g:message code='admin.logout.label' default='Régul' />  </g:link></li>
			</ul>
		</div>
		<div id="site-followup" class="standardNav">
			<h1>	
				<g:if test="${site}"><g:message code="site.followup" /> ${site.name}</g:if>
				<g:else>Suivi des Sites</g:else>
				<br>
				<br>
				<g:form method="POST" url="[controller:'site', action:'siteTotalTime']">
					<ul>
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
					<li>	<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':site.name]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="site.id" from="${Site.list([sort:'name'])}" id='siteSelect'
								noSelection="${['':(site?site.name:'-')]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />						
						</g:else>
					</li>							
					<li class='datePicker'>	
						<g:select name="periodId" from="${Period.list([sort:'year'])}" id='periodSelect'
							value="${period}"
							noSelection="${['':(period?period:'-')]}" optionKey="id" 
							style="vertical-align: middle;" />
					</li>	
					<li>	
						<g:submitToRemote class='displayButton' 
							update="monthlyTotalDiv"
							onLoading="document.getElementById('spinner').style.display = 'inline';"  
							onComplete="document.getElementById('spinner').style.display = 'none';ajaxCall();"
							url="[controller:'site', action:'completeSiteReport']" value="${message(code: 'default.search.label')}">
						</g:submitToRemote>
					<li>	<g:actionSubmit class='pdfButton' value="PDF"  action="completeSiteReportPDF"/>				</li>			
						<g:if test="${site!=null}">	
							<g:hiddenField name="site.id" value="${site.id}" />			
						</g:if>							
						<g:hiddenField name="siteId" value="${siteId}" />
						<g:hiddenField name="isAdmin" value="${isAdmin}" />			
						<g:if test="${period2!=null}">	
							<g:hiddenField name="year" value="${period2.id}" />			
						</g:if>			
					</ul>
				</g:form>		
			</h1>

		</div>
		<br>

		<div id='monthlyTotalDiv'>
			<g:siteMonthlyTotal/>
		</div>

	</body>
</html>
