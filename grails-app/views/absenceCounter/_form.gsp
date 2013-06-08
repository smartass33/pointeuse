<%@ page import="pointeuse.AbsenceCounter" %>



<div class="fieldcontain ${hasErrors(bean: absenceCounterInstance, field: 'logging_time', 'error')} required">
	<label for="logging_time">
		<g:message code="absenceCounter.logging_time.label" default="Loggingtime" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="logging_time" precision="day"  value="${absenceCounterInstance?.logging_time}"  />
</div>


<div class="fieldcontain ${hasErrors(bean: absenceCounterInstance, field: 'employee', 'error')} required">
	<label for="employee">
		<g:message code="absenceCounter.employee.label" default="Employee" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="employee" name="employee.id" from="${pointeuse.Employee.list()}" optionKey="id" required="" value="${absenceCounterInstance?.employee?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: absenceCounterInstance, field: 'year', 'error')} required">
	<label for="year">
		<g:message code="absenceCounter.year.label" default="Year" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="year" type="number" value="${absenceCounterInstance.year}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: absenceCounterInstance, field: 'counter', 'error')} required">
	<label for="counter">
		<g:message code="absenceCounter.counter.label" default="Counter" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="counter" type="number" value="${absenceCounterInstance.counter}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: absenceCounterInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="absenceCounter.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${pointeuse.User.list()}" optionKey="id" required="" value="${absenceCounterInstance?.user?.id}" class="many-to-one"/>
</div>

