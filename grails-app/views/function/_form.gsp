<%@ page import="pointeuse.Function" %>



<div class="fieldcontain ${hasErrors(bean: functionInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="function.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${functionInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: functionInstance, field: 'ranking', 'error')} required">
	<label for="ranking">
		<g:message code="function.ranking.label" default="Ranking" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="ranking" type="number" value="${functionInstance.ranking}" required=""/>
</div>

