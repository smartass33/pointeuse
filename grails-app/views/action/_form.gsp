<%@ page import="pointeuse.Action" %>



<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'date', 'error')} required">
	<label for="date">
		<g:message code="action.date.label" default="Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="date" precision="day"  value="${actionInstance?.date}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'day', 'error')} required">
	<label for="day">
		<g:message code="action.day.label" default="Day" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="day" type="number" value="${actionInstance.day}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'employeeLogger', 'error')} required">
	<label for="employeeLogger">
		<g:message code="action.employeeLogger.label" default="Employee Logger" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="employeeLogger" name="employeeLogger.id" from="${pointeuse.Employee.list()}" optionKey="id" required="" value="${actionInstance?.employeeLogger?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'isTheoritical', 'error')} ">
	<label for="isTheoritical">
		<g:message code="action.isTheoritical.label" default="Is Theoritical" />	
	</label>
	<g:checkBox name="isTheoritical" value="${actionInstance?.isTheoritical}" />
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'itinerary', 'error')} required">
	<label for="itinerary">
		<g:message code="action.itinerary.label" default="Itinerary" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="itinerary" name="itinerary.id" from="${pointeuse.Itinerary.list()}" optionKey="id" required="" value="${actionInstance?.itinerary?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'month', 'error')} required">
	<label for="month">
		<g:message code="action.month.label" default="Month" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="month" type="number" value="${actionInstance.month}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'nature', 'error')} required">
	<label for="nature">
		<g:message code="action.nature.label" default="Nature" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="nature" from="${pointeuse.ItineraryNature?.values()}" keys="${pointeuse.ItineraryNature.values()*.name()}" required="" value="${actionInstance?.nature?.name()}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'site', 'error')} required">
	<label for="site">
		<g:message code="action.site.label" default="Site" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="site" name="site.id" from="${pointeuse.Site.list()}" optionKey="id" required="" value="${actionInstance?.site?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: actionInstance, field: 'year', 'error')} required">
	<label for="year">
		<g:message code="action.year.label" default="Year" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="year" type="number" value="${actionInstance.year}" required=""/>
</div>

