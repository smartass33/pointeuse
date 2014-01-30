<%@ page import="pointeuse.Site" %>



<div class="fieldcontain ${hasErrors(bean: siteInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="site.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${siteInstance?.name}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: siteInstance, field: 'address', 'error')} ">
	<label for=address>
		<g:message code="site.address.label" default="Address" />
	</label>
	<g:textField name="address" required="" value="${siteInstance?.address}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: siteInstance, field: 'town', 'error')} ">
	<label for=address>
		<g:message code="site.town.label" default="Address" />
	</label>
	<g:textField name="town" required="" value="${siteInstance?.town}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: siteInstance, field: 'postCode', 'error')} ">
	<label for=address>
		<g:message code="site.postCode.label" default="Address" />
	</label>
	<g:textField name="postCode" required="" value="${siteInstance?.postCode}"/>
</div>
