<%@ page import="pointeuse.SupplementaryTime" %>



<div class="fieldcontain ${hasErrors(bean: supplementaryTimeInstance, field: 'employee', 'error')} required">
	<label for="employee">
		<g:message code="supplementaryTime.employee.label" default="Employee" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="employee" name="employee.id" from="${pointeuse.Employee.list()}" optionKey="id" required="" value="${supplementaryTimeInstance?.employee?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: supplementaryTimeInstance, field: 'loggingTime', 'error')} required">
	<label for="loggingTime">
		<g:message code="supplementaryTime.loggingTime.label" default="Logging Time" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="loggingTime" precision="day"  value="${supplementaryTimeInstance?.loggingTime}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: supplementaryTimeInstance, field: 'period', 'error')} required">
	<label for="period">
		<g:message code="supplementaryTime.period.label" default="Period" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="period" name="period.id" from="${pointeuse.Period.list()}" optionKey="id" required="" value="${supplementaryTimeInstance?.period?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: supplementaryTimeInstance, field: 'type', 'error')} required">
	<label for="type">
		<g:message code="supplementaryTime.type.label" default="Type" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="type" from="${pointeuse.SupplementaryType?.values()}" keys="${pointeuse.SupplementaryType.values()*.name()}" required="" value="${supplementaryTimeInstance?.type?.name()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: supplementaryTimeInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="supplementaryTime.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${pointeuse.User.list()}" optionKey="id" required="" value="${supplementaryTimeInstance?.user?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: supplementaryTimeInstance, field: 'value', 'error')} required">
	<label for="value">
		<g:message code="supplementaryTime.value.label" default="Value" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="value" value="${fieldValue(bean: supplementaryTimeInstance, field: 'value')}" required=""/>
</div>

