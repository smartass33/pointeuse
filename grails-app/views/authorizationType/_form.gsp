<%@ page import="pointeuse.AuthorizationType" %>

<div class="fieldcontain ${hasErrors(bean: authorizationTypeInstance, field: 'nature', 'error')} required">
	<label for="nature">
		<g:message code="authorizationType.nature.label" default="Nature" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="nature" name="nature.id" from="${pointeuse.AuthorizationNature.list()}" optionKey="id" required="" optionValue="name" value="${authorizationTypeInstance?.nature?.id}" class="many-to-one"/>
</div>
<div class="fieldcontain ${hasErrors(bean: authorizationTypeInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="authorizationType.type.label" default="Type" />	
	</label>
	<g:textField name="name" value="${authorizationTypeInstance?.name}"/>
</div>



