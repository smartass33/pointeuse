<%@ page import="pointeuse.AnomalyType" %>



<div class="fieldcontain ${hasErrors(bean: anomalyTypeInstance, field: 'creatingDate', 'error')} ">
	<label for="creatingDate">
		<g:message code="anomalyType.creatingDate.label" default="Creating Date" />
		
	</label>
	<g:datePicker name="creatingDate" precision="day"  value="${anomalyTypeInstance?.creatingDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyTypeInstance, field: 'creatingUser', 'error')} ">
	<label for="creatingUser">
		<g:message code="anomalyType.creatingUser.label" default="Creating User" />
		
	</label>
	<g:select id="creatingUser" name="creatingUser.id" from="${pointeuse.User.list()}" optionKey="id" value="${anomalyTypeInstance?.creatingUser?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anomalyTypeInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="anomalyType.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${anomalyTypeInstance?.name}"/>
</div>

