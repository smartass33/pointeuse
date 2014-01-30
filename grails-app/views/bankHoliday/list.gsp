
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
				<th style="width:150px">Jour</th>
				<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">
					<th style="width:150px;text-align:center">Date de création</th>
					<th style="width:150px;text-align:center">Administrateur</th>
				</sec:ifAnyGranted>
			</table>
				<g:each in="${yearlyCounts}" status="i" var="days">
				<table>
					<th colspan="3" style="text-align:center">${days.key}, jours fériés: ${days.value.size()}</th>
					<g:each in="${days.value}" status="j" var="bankHoliday">
						<tr class="${(j % 2) == 0 ? 'even' : 'odd'}">
							<td style="width:150px;text-align:center"><g:link action="show" id="${bankHoliday.id}">${bankHoliday.calendar.time.format('dd MMMM yyyy')}</g:link></td>
							<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">
								<td style="width:150px;text-align:center">${bankHoliday.loggingDate.format('dd/mm/yyyy') }</td>
								<td style="width:150px;text-align:center">${bankHoliday.user.firstName} ${bankHoliday.user.lastName}</td>
							</sec:ifAnyGranted>				
						</tr>
					</g:each>
					</table>
				</g:each>
			<div class="pagination">
				<g:paginate total="${bankHolidayInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
