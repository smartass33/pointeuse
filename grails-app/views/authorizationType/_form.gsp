<%@ page import="pointeuse.AuthorizationType" %>


<div class="fieldcontain ${hasErrors(bean: authorizationTypeInstance, field: 'nature', 'error')} required">
	<label for="nature">
		<g:message code="authorizationType.nature.label" default="Nature" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="nature" name="nature.id" from="${pointeuse.AuthorizationNature.list()}" optionKey="id" required="" optionValue="nature" value="${authorizationTypeInstance?.nature?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: authorizationTypeInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="authorizationType.type.label" default="Type" />	
	</label>
	<g:textField name="type" value="${authorizationTypeInstance?.type}"/>
</div>



