<%@ page import="pointeuse.BankHoliday" %>



<div class="fieldcontain ${hasErrors(bean: bankHolidayInstance, field: 'calendar', 'error')} required">
	<label for="calendar">
		<g:message code="bankHoliday.calendar.label" default="Calendar" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="calendar" precision="day"  value="${bankHolidayInstance?.calendar}"  />
</div>

