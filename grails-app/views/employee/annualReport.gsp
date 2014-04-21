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



<script type="text/javascript">


function showSpinner() {
      $('#spinner').show();
   }
   function hideSpinner() {
      $('#spinner').hide();
   }
</script>
</head>
<body>
<body>

	<div class="nav" id="nav">
		<ul>
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
			<li>
				<g:form method="POST"
					url="[controller:'employee', action:'annualTotalPDF']">
	
					<g:actionSubmit class="listButton" value="export PDF" action="annualTotalPDF"/>
					<g:hiddenField name="isAdmin" value="${isAdmin}" />
					<g:hiddenField name="siteId" value="${siteId}" />
					<g:hiddenField name="userId" value="${userId}" />
					<g:hiddenField name="isAjax" value="true" />			
				</g:form>
			</li>
		</ul>	
	</div>
	<div id="spinner" class="spinner" style="display: none;">
    <span><g:message code="spinner.loading.label"/></span><img id="img-spinner" src="${createLinkTo(dir:'images',file:'spinner.gif')}" alt="Loading"/>
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
