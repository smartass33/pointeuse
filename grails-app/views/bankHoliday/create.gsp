<%@ page import="pointeuse.BankHoliday" %>
<!DOCTYPE html>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery" />
		<resource:calendarMonthView />
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'bankHoliday.label', default: 'BankHoliday')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#create-bankHoliday" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="create-bankHoliday" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${bankHolidayInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${bankHolidayInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="save" >
			<!--richui:calendarMonthView items="${appointments}" createLink="true" 
constraintDateFields="['startDate-endDate', 'startDate', 'endDate']" displayField="subject" teaser="true" teaserLength="20" weekOfYear="true" weekAction="week" dayAction="day" month="${month}" action="show" /-->
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
