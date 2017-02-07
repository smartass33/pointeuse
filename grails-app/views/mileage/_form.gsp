<%@ page import="pointeuse.Milage" %>



<div class="fieldcontain ${hasErrors(bean: milageInstance, field: 'employee', 'error')} required">
	<label for="employee">
		<g:message code="milage.employee.label" default="Employee" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="employee" name="employee.id" from="${pointeuse.Employee.list()}" optionKey="id" required="" value="${milageInstance?.employee?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: milageInstance, field: 'loggingTime', 'error')} required">
	<label for="loggingTime">
		<g:message code="milage.loggingTime.label" default="Logging Time" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="loggingTime" precision="day"  value="${milageInstance?.loggingTime}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: milageInstance, field: 'month', 'error')} required">
	<label for="month">
		<g:message code="milage.month.label" default="Month" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="month" type="number" value="${milageInstance.month}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: milageInstance, field: 'period', 'error')} required">
	<label for="period">
		<g:message code="milage.period.label" default="Period" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="period" name="period.id" from="${pointeuse.Period.list()}" optionKey="id" required="" value="${milageInstance?.period?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: milageInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="milage.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${pointeuse.User.list()}" optionKey="id" required="" value="${milageInstance?.user?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: milageInstance, field: 'value', 'error')} required">
	<label for="value">
		<g:message code="milage.value.label" default="Value" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="value" type="number" value="${milageInstance.value}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: milageInstance, field: 'year', 'error')} required">
	<label for="year">
		<g:message code="milage.year.label" default="Year" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="year" type="number" value="${milageInstance.year}" required=""/>
</div>

