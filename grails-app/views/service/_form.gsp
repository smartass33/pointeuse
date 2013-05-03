<%@ page import="pointeuse.Service" %>



<div class="fieldcontain ${hasErrors(bean: serviceInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="service.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${serviceInstance?.name}"/>
</div>

