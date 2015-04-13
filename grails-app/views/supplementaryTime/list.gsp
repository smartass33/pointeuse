<%@ page import="pointeuse.SupplementaryTime" %>
<!DOCTYPE html>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery" />
		<g:set var="calendar" value="${Calendar.instance}"/>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'supplementary.time.label', default: 'Supplement')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-suptime" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="TBD">A CABLER</g:link></li>
			</ul>
		</div>
		<div id="list-suptime" class="content scaffold-list" role="main">
			<h1><g:message code="employee.sup.time.label" /> pour le salarié ${employee.firstName} ${employee.lastName}</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<div id='supTimeList'>
				<g:employeeSupTimeManagement/>
			</div>
			<div class="pagination" id="pagination">
				<g:paginate total="${supplementaryTimeTotal}" />
			</div>
		</div>
	</body>
</html>
