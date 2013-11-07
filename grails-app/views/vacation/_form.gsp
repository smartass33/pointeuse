<%@ page import="pointeuse.Vacation" %>
<%@ page import="pointeuse.Year" %>



<div class="fieldcontain ${hasErrors(bean: vacationInstance, field: 'counter', 'error')} required">
	<label for="counter">
		<g:message code="vacation.counter.label" default="Counter" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="counter" type="number" value="${vacationInstance.counter}" required=""/>
</div>


<!--div class="fieldcontain ${hasErrors(bean: vacationInstance, field: 'employee', 'error')} required">
	<label for="employee">
		<g:message code="employee.label" default="Employee" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="employee" value="${vacationInstance.employee}" required=""/>
</div-->



<div class="fieldcontain ${hasErrors(bean: vacationInstance, field: 'period', 'error')} required">
	<label for="period">
		<g:message code="vacation.period.label" default="Period" />
		<span class="required-indicator">*</span>
	</label>
		<g:select name="year.id"
	          from="${Year.list([sort:'period',order:'desc'])}"
	          noSelection="${['':'-']}"    
	          value="${year?.period}"      
	          optionKey="id" optionValue="period"
	          />
</div>

<div class="fieldcontain ${hasErrors(bean: vacationInstance, field: 'type', 'error')} required">
	<label for="type">
		<g:message code="vacation.type.label" default="Type" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="type" from="${pointeuse.VacationType?.values()}" keys="${pointeuse.VacationType.values()*.name()}" required="" value="${vacationInstance?.type?.name()}"/>
</div>

