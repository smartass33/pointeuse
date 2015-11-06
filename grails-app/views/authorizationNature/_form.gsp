<%@ page import="pointeuse.AuthorizationNature" %>


<div class="fieldcontain ${hasErrors(bean: authorizationNatureInstance, field: 'nature', 'error')} ">
	<label for="nature">
		<g:message code="authorizationNature.nature.label" default="Nature" />
		
	</label>
	<g:textField name="nature" value="${authorizationNatureInstance?.nature}"/>
</div>
