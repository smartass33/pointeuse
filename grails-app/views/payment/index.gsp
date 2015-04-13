
<%@ page import="pointeuse.Payment" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'payment.label', default: 'Payment')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-payment" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-payment" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="amountPaid" title="${message(code: 'payment.amountPaid.label', default: 'Amount Paid')}" />
					
						<th><g:message code="payment.employee.label" default="Employee" /></th>
					
						<g:sortableColumn property="loggingTime" title="${message(code: 'payment.loggingTime.label', default: 'Logging Time')}" />
					
						<g:sortableColumn property="month" title="${message(code: 'payment.month.label', default: 'Month')}" />
					
						<th><g:message code="payment.period.label" default="Period" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${paymentInstanceList}" status="i" var="paymentInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${paymentInstance.id}">${fieldValue(bean: paymentInstance, field: "amountPaid")}</g:link></td>
					
						<td>${fieldValue(bean: paymentInstance, field: "employee")}</td>
					
						<td><g:formatDate date="${paymentInstance.loggingTime}" /></td>
					
						<td>${fieldValue(bean: paymentInstance, field: "month")}</td>
					
						<td>${fieldValue(bean: paymentInstance, field: "period")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${paymentInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
