
<%@ page import="pointeuse.Action" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'action.label', default: 'Action')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-action" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-action" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list action">
			
				<g:if test="${actionInstance?.date}">
				<li class="fieldcontain">
					<span id="date-label" class="property-label"><g:message code="action.date.label" default="Date" /></span>
					
						<span class="property-value" aria-labelledby="date-label"><g:formatDate date="${actionInstance?.date}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.day}">
				<li class="fieldcontain">
					<span id="day-label" class="property-label"><g:message code="action.day.label" default="Day" /></span>
					
						<span class="property-value" aria-labelledby="day-label"><g:fieldValue bean="${actionInstance}" field="day"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.employeeLogger}">
				<li class="fieldcontain">
					<span id="employeeLogger-label" class="property-label"><g:message code="action.employeeLogger.label" default="Employee Logger" /></span>
					
						<span class="property-value" aria-labelledby="employeeLogger-label"><g:link controller="employee" action="show" id="${actionInstance?.employeeLogger?.id}">${actionInstance?.employeeLogger?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.isTheoritical}">
				<li class="fieldcontain">
					<span id="isTheoritical-label" class="property-label"><g:message code="action.isTheoritical.label" default="Is Theoritical" /></span>
					
						<span class="property-value" aria-labelledby="isTheoritical-label"><g:formatBoolean boolean="${actionInstance?.isTheoritical}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.itinerary}">
				<li class="fieldcontain">
					<span id="itinerary-label" class="property-label"><g:message code="action.itinerary.label" default="Itinerary" /></span>
					
						<span class="property-value" aria-labelledby="itinerary-label"><g:link controller="itinerary" action="show" id="${actionInstance?.itinerary?.id}">${actionInstance?.itinerary?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.month}">
				<li class="fieldcontain">
					<span id="month-label" class="property-label"><g:message code="action.month.label" default="Month" /></span>
					
						<span class="property-value" aria-labelledby="month-label"><g:fieldValue bean="${actionInstance}" field="month"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.nature}">
				<li class="fieldcontain">
					<span id="nature-label" class="property-label"><g:message code="action.nature.label" default="Nature" /></span>
					
						<span class="property-value" aria-labelledby="nature-label"><g:fieldValue bean="${actionInstance}" field="nature"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.site}">
				<li class="fieldcontain">
					<span id="site-label" class="property-label"><g:message code="action.site.label" default="Site" /></span>
					
						<span class="property-value" aria-labelledby="site-label"><g:link controller="site" action="show" id="${actionInstance?.site?.id}">${actionInstance?.site?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${actionInstance?.year}">
				<li class="fieldcontain">
					<span id="year-label" class="property-label"><g:message code="action.year.label" default="Year" /></span>
					
						<span class="property-value" aria-labelledby="year-label"><g:fieldValue bean="${actionInstance}" field="year"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:actionInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${actionInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
