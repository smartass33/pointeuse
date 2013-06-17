<%@ page import="pointeuse.Year" %>



<div class="fieldcontain ${hasErrors(bean: yearInstance, field: 'period', 'error')} ">
	<label for="period">
		<g:message code="year.period.label" default="Period" />
		
	</label>
	<g:textField name="period" value="${yearInstance?.period}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: yearInstance, field: 'year', 'error')} required">
	<label for="year">
		<g:message code="year.year.label" default="Year" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="year" type="number" value="${yearInstance.year}" required=""/>
</div>

