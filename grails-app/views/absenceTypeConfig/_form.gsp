<%@ page import="pointeuse.AbsenceTypeConfig" %>



<div class="fieldcontain ${hasErrors(bean: absenceTypeConfigInstance, field: 'creationDate', 'error')} required">
	<label for="creationDate">
		<g:message code="absenceTypeConfig.creationDate.label" default="Creation Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="creationDate" precision="day"  value="${absenceTypeConfigInstance?.creationDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: absenceTypeConfigInstance, field: 'isPorportional', 'error')} ">
	<label for="isPorportional">
		<g:message code="absenceTypeConfig.isPorportional.label" default="Is Porportional" />
		
	</label>
	<g:checkBox name="isPorportional" value="${absenceTypeConfigInstance?.isPorportional}" />
</div>

<div class="fieldcontain ${hasErrors(bean: absenceTypeConfigInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="absenceTypeConfig.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${absenceTypeConfigInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: absenceTypeConfigInstance, field: 'shortName', 'error')} ">
	<label for="shortName">
		<g:message code="absenceTypeConfig.shortName.label" default="Short Name" />
		
	</label>
	<g:textField name="shortName" value="${absenceTypeConfigInstance?.shortName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: absenceTypeConfigInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="absenceTypeConfig.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${pointeuse.User.list()}" optionKey="id" required="" value="${absenceTypeConfigInstance?.user?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: absenceTypeConfigInstance, field: 'weight', 'error')} required">
	<label for="weight">
		<g:message code="absenceTypeConfig.weight.label" default="Weight" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="weight" value="${fieldValue(bean: absenceTypeConfigInstance, field: 'weight')}" required=""/>
</div>

