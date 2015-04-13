
<%@ page import="pointeuse.Payment" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'payment.label', default: 'Payment')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-payment" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-payment" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list payment">
			
				<g:if test="${paymentInstance?.amountPaid}">
				<li class="fieldcontain">
					<span id="amountPaid-label" class="property-label"><g:message code="payment.amountPaid.label" default="Amount Paid" /></span>
					
						<span class="property-value" aria-labelledby="amountPaid-label"><g:fieldValue bean="${paymentInstance}" field="amountPaid"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${paymentInstance?.employee}">
				<li class="fieldcontain">
					<span id="employee-label" class="property-label"><g:message code="payment.employee.label" default="Employee" /></span>
					
						<span class="property-value" aria-labelledby="employee-label"><g:link controller="employee" action="show" id="${paymentInstance?.employee?.id}">${paymentInstance?.employee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${paymentInstance?.loggingTime}">
				<li class="fieldcontain">
					<span id="loggingTime-label" class="property-label"><g:message code="payment.loggingTime.label" default="Logging Time" /></span>
					
						<span class="property-value" aria-labelledby="loggingTime-label"><g:formatDate date="${paymentInstance?.loggingTime}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${paymentInstance?.month}">
				<li class="fieldcontain">
					<span id="month-label" class="property-label"><g:message code="payment.month.label" default="Month" /></span>
					
						<span class="property-value" aria-labelledby="month-label"><g:fieldValue bean="${paymentInstance}" field="month"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${paymentInstance?.period}">
				<li class="fieldcontain">
					<span id="period-label" class="property-label"><g:message code="payment.period.label" default="Period" /></span>
					
						<span class="property-value" aria-labelledby="period-label"><g:link controller="period" action="show" id="${paymentInstance?.period?.id}">${paymentInstance?.period?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:paymentInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${paymentInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
