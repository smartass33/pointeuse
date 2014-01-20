<%@ page import="pointeuse.EventLog" %>



<div class="fieldcontain ${hasErrors(bean: eventLogInstance, field: 'source', 'error')} required">
	<label for="source">
		<g:message code="eventLog.source.label" default="Source" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="source" cols="40" rows="5" maxlength="255" required="" value="${eventLogInstance?.source}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventLogInstance, field: 'message', 'error')} required">
	<label for="message">
		<g:message code="eventLog.message.label" default="Message" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="message" cols="40" rows="5" maxlength="1000" required="" value="${eventLogInstance?.message}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventLogInstance, field: 'details', 'error')} ">
	<label for="details">
		<g:message code="eventLog.details.label" default="Details" />
		
	</label>
	<g:textArea name="details" cols="40" rows="5" maxlength="4000" value="${eventLogInstance?.details}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventLogInstance, field: 'cleared', 'error')} ">
	<label for="cleared">
		<g:message code="eventLog.cleared.label" default="Cleared" />
		
	</label>
	<g:checkBox name="cleared" value="${eventLogInstance?.cleared}" />
</div>

