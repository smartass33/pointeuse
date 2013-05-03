<%@ page import="pointeuse.Reason" %>



<div class="fieldcontain ${hasErrors(bean: reasonInstance, field: 'name', 'error')} ">
	<label for="reasonName">
		<g:message code="reason.name.label" default="Reason Name" />
		
	</label>
	<g:textField name="name" value="${reasonInstance?.name}"/>
</div>

