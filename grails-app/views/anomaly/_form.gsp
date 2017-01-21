<%@ page import="pointeuse.Anomaly" %>



<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'creationDate', 'error')} ">
	<label for="creationDate">
		<g:message code="anomaly.creationDate.label" default="Creation Date" />
		
	</label>
	<g:datePicker name="creationDate" precision="day"  value="${anomalyInstance?.creationDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'user', 'error')} ">
	<label for="user">
		<g:message code="anomaly.user.label" default="User" />
		
	</label>
	<g:select id="user" name="user.id" from="${pointeuse.User.list()}" optionKey="id" value="${anomalyInstance?.user?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'RAS', 'error')} ">
	<label for="RAS">
		<g:message code="anomaly.RAS.label" default="RAS" />
		
	</label>
	<g:checkBox name="RAS" value="${anomalyInstance?.RAS}" />
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="anomaly.type.label" default="Type" />
		
	</label>
	<g:select id="type" name="type.id" from="${pointeuse.AnomalyType.list()}" optionKey="id" value="${anomalyInstance?.type?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="anomaly.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${anomalyInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="anomaly.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${anomalyInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'employee', 'error')} required">
	<label for="employee">
		<g:message code="anomaly.employee.label" default="Employee" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="employee" name="employee.id" from="${pointeuse.Employee.list()}" optionKey="id" required="" value="${anomalyInstance?.employee?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyInstance, field: 'site', 'error')} required">
	<label for="site">
		<g:message code="anomaly.site.label" default="Site" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="site" name="site.id" from="${pointeuse.Site.list()}" optionKey="id" required="" value="${anomalyInstance?.site?.id}" class="many-to-one"/>
</div>

