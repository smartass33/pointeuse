<%@ page import="pointeuse.Authorization" %>


<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'startDate', 'error')} required">
	<label for="startDate">
		<g:message code="authorization.startDate.label" default="Start Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="startDate" precision="day"  value="${authorizationInstance?.startDate}"  />
</div>
<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'endDate', 'error')} required">
	<label for="endDate">
		<g:message code="authorization.endDate.label" default="End Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="endDate" precision="day"  value="${authorizationInstance?.endDate}"  />
</div>
<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'isAuthorized', 'error')} ">
	<label for="isAuthorized">
		<g:message code="authorization.isAuthorized.label" default="Is Authorized" />
		
	</label>
	<g:checkBox name="isAuthorized" value="${authorizationInstance?.isAuthorized}" />
</div>
<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'type', 'error')} required">
	<label for="type">
		<g:message code="authorization.type.label" default="Type" />
		<span class="required-indicator">*</span>
	</label>
</div>

