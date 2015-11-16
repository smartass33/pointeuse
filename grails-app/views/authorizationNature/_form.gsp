<%@ page import="pointeuse.AuthorizationNature" %>

<div class="fieldcontain ${hasErrors(bean: authorizationNatureInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="authorizationNature.label" default="Nature" />	
	</label>
	<g:textField name="name" value="${authorizationNatureInstance?.name}"/>
</div>
