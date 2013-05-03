<%@ page import="pointeuse.Site" %>



<div class="fieldcontain ${hasErrors(bean: siteInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="site.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${siteInstance?.name}"/>
</div>
