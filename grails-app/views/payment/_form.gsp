<%@ page import="pointeuse.Payment" %>



<div class="fieldcontain ${hasErrors(bean: paymentInstance, field: 'amountPaid', 'error')} required">
	<label for="amountPaid">
		<g:message code="payment.amountPaid.label" default="Amount Paid" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="amountPaid" value="${fieldValue(bean: paymentInstance, field: 'amountPaid')}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: paymentInstance, field: 'employee', 'error')} required">
	<label for="employee">
		<g:message code="payment.employee.label" default="Employee" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="employee" name="employee.id" from="${pointeuse.Employee.list()}" optionKey="id" required="" value="${paymentInstance?.employee?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: paymentInstance, field: 'loggingTime', 'error')} required">
	<label for="loggingTime">
		<g:message code="payment.loggingTime.label" default="Logging Time" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="loggingTime" precision="day"  value="${paymentInstance?.loggingTime}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: paymentInstance, field: 'month', 'error')} required">
	<label for="month">
		<g:message code="payment.month.label" default="Month" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="month" type="number" value="${paymentInstance.month}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: paymentInstance, field: 'period', 'error')} required">
	<label for="period">
		<g:message code="payment.period.label" default="Period" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="period" name="period.id" from="${pointeuse.Period.list()}" optionKey="id" required="" value="${paymentInstance?.period?.id}" class="many-to-one"/>
</div>

