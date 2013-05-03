
<%@ page import="pointeuse.BankHoliday" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'bankHoliday.label', default: 'BankHoliday')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-bankHoliday" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-bankHoliday" class="content scaffold-list" role="main">
			<h1><g:message code="bankHoliday.calendar.label"/></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>

				<tbody>
				<% def currentYear=maxYear %>
				<tr>
					<td>Année: ${currentYear}</td>
					<td>nombre de jours fériés dans l'année: ${yearlyCounts.get(currentYear)}</td>
				</tr>
				<g:each in="${bankHolidayInstanceList}" status="i" var="bankHolidayInstance">
					<g:if test="${bankHolidayInstance.year != currentYear}">
						<% currentYear=bankHolidayInstance.year %>
						<tr>
							<td>Année: ${currentYear}</td>
							<td>nombre de jours fériés dans l'année: ${yearlyCounts.get(currentYear)}</td>
							
						</tr>
						
					</g:if>

					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${bankHolidayInstance.id}">${bankHolidayInstance.calendar.getTime().format('dd-MMM-yyyy')}</g:link></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${bankHolidayInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
