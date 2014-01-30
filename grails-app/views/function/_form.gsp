<%@ page import="pointeuse.Function" %>



<div class="fieldcontain ${hasErrors(bean: functionInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="function.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${functionInstance?.name}"/>
</div>

