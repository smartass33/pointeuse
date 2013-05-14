 	<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
	<%@ page import="pointeuse.Employee"%>
	<%@ page import="pointeuse.InAndOut"%>
	<%@ page import="pointeuse.AbsenceType"%>
	<%@ page import="pointeuse.MonthlyTotal"%>
<head>
	<g:javascript library="prototype" />
 	<resource:include components="autoComplete, dateChooser"/>
	<resource:dateChooser />
	<modalbox:modalIncludes />	
	<meta name="layout" content="main">
	<g:set var="weeklyRecap" value="0" />
	<title>
		${message(code: 'employee.report.label', default: 'Report')}
	</title>
	<link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>

<g:form method="post" >
	
	<a href="#list-employee" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>
	<div id="list-employee" class="content scaffold-list">
		<div>
			<h1>BIOLAB33
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</h1>
		</div>
		<div class="nav" role="navigation">
			<ul>
				<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li style="vertical-align: middle;"><g:link class="list" action="list"
						params="${[isAdmin:isAdmin,siteId:siteId,back:true]}">
						${message(code: 'employee.back.to.list.label', default: 'List')}
					</g:link></li>
				<li style="vertical-align: middle;">
					${message(code: 'default.period.label', default: 'List')}: <g:datePicker
						name="myDate" value="${period ? period : new Date()}" 
						precision="month" noSelection="['':'-Choose-']" style="vertical-align: middle;"/>
						<g:hiddenField name="userId" value="${userId}" />
						<g:hiddenField name="siteId" value="${siteId}" />	
				</li>
				<li style="vertical-align: middle;">
					<g:actionSubmit value="afficher" action="report" />						
				</li>
				<li style="vertical-align: middle;">
					<g:actionSubmit value="appliquer"  action="modifyTime"/>		
				</li>
					<sec:ifAllGranted roles="ROLE_SUPER_ADMIN">	
				<li style="vertical-align: middle;">
					<g:actionSubmit value="pdf"  action="pdf"/>		
				</li>	
					</sec:ifAllGranted>
				<li style="vertical-align: middle;"><modalbox:createLink controller="inAndOut" action="create"
						css="loginbutton" id="${userId}"
						title="Ajouter un évenement oublié" width="500"
						params="[complete:true,report:true]">
						<g:message code="inAndOut.regularization" default="Régul" />
					</modalbox:createLink></li>
			</ul>
		</div>
			<BR/>
		<g:if test="${flash.message}">
			<div class="message">
				${flash.message}
			</div>
		</g:if>
	</div>
	<div id="updateDiv2">
		<g:cartouche/>
	</div>
	<BR />
	<div id="updateDiv3">
		<g:reportTable/>
	</div>
	</g:form>
	</body>
