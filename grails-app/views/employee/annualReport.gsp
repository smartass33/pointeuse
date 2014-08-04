<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Absence"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="pointeuse.Period"%>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />
		<g:javascript library="jquery" plugin="jquery" />
		<resource:tooltip />
		<title>${message(code: 'employee.report.label', default: 'Report')}</title>
		<link href="main.css" rel="stylesheet" type="text/css">	
		<g:set var="calendar" value="${Calendar.instance}"/>
		<g:set var="weeklyRecap" value="0" />
		<g:set var="takenCA" value="0" />		
		<style type="text/css">
			.rotate-45{
			  height: 80px;
			  width: 40px;
			  min-width: 40px;
			  max-width: 40px;
			  position: relative;
			  vertical-align: bottom;
			  padding: 0;
			  font-size: 12px;
			  line-height: 0.8;
			}
		</style>
	</head>
	<body>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>	
		<div class="nav" id="nav">
			<ul>
			<g:form method="POST">
				<li><a class="home" href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li><g:link class="list" action="list"
						params="${[isAdmin:isAdmin]}"><g:message code="employee.list.annualReport.label"/>
					</g:link></li>
				<g:if test="${username != null}">
					<li><g:link class="list" action="" controller="logout">
							${message(code: 'admin.logout.label', default: 'Logout')}
						</g:link></li>
				</g:if>
				<li class="datePicker">
					<g:select name="year" from="${Period.list([sort:'year'])}"
						value="${period}"
						noSelection="${['':(period?period:'-')]}" optionKey="id" 		
						onchange="${remoteFunction(action: 'annualReport',
	                  		update: 'monthlyTable',
	                  		params: 
							'\'isAjax=' + true 	
							+ '&userId=' + userId
							+ '&periodId=\' + this.value',
							onLoading: "document.getElementById('spinner').style.display = 'inline';",
							onComplete: "document.getElementById('spinner').style.display = 'none';"
							)}"				
						style="vertical-align: middle;" />					
				</li>				
				<li style="vertical-align: middle;">				
					<g:actionSubmit value="PDF" action="annualTotalPDF" class="pdfButton" />
							<g:hiddenField name="isAdmin" value="${isAdmin}" />
							<g:hiddenField name="siteId" value="${siteId}" />
							<g:hiddenField name="userId" value="${userId}" />
							<g:hiddenField name="periodId" value="${period}" />							
							<g:hiddenField name="isAjax" value="true" />	
				</li>
				</g:form>				
			</ul>	
		</div>
		<div id="list-employee" class="content scaffold-list">
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