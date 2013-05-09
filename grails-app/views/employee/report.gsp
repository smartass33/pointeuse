<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>

<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>

<!doctype html>
<html>
<head>
	<g:javascript library="prototype" />
	<resource:include components="autoComplete, dateChooser"
		autoComplete="[skin: 'default']" />
	<resource:dateChooser />
	<modalbox:modalIncludes />
	<meta name="layout" content="main">
	<g:set var="weeklyRecap" value="0" />
	<title>
		${message(code: 'employee.report.label', default: 'Report')}
	</title>
	<link href="main.css" rel="stylesheet" type="text/css">

	<script type="text/javascript">
		function() { alert('Content loaded successfully') }
	</script>
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
				<li><a class="home" href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li><g:link class="list" action="list"
						params="${[isAdmin:isAdmin,siteId:siteId]}">
						${message(code: 'employee.back.to.list.label', default: 'List')}
					</g:link></li>
					<li>
						${message(code: 'default.period.label', default: 'List')}: <g:datePicker
							name="myDate" value="${period ? period : new Date()}" 
							precision="month" noSelection="['':'-Choose-']" />
							<g:hiddenField name="userId" value="${userId}" />
							<g:hiddenField name="siteId" value="${siteId}" />
							
					</li>
					<li>
						<g:actionSubmit value="afficher" action="report" />						
					</li>
					<li>
						<g:actionSubmit value="appliquer"  action="modifyTime"/>		
						</li>
					<!--li><modalbox:createLink controller="inAndOut" action="create"
							css="loginbutton" id="${userId}"
							title="Ajouter un évenement oublié" width="500"
							params="[complete:true,report:true]">
							<g:message code="inAndOut.regularization" default="Régul" />
						</modalbox:createLink></li-->
						
				<li>
				<a href="/pointeuse/inAndOut/create/2?complete=true&report=true" title="Ajouter un évenement oublié" class="loginbutton" onclick="Modalbox.show(this.href, {title: this.title,width: 500}, {beforeHide: function() { alert('Modalbox is now hidden') }}); return false;">Ajouter une heure</a>
				</li>
			</ul>
		</div>
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
</html>
