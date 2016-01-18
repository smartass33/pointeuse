<%@ page import="pointeuse.Authorization" %>
<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.AuthorizationType" %>

<g:if test="${employeeInstance == null}">
	<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'employee', 'error')} required">
		<label for="employee" style="width:160px;">
			<g:message code="employee.label" default="Type" />
			<span class="required-indicator">*</span>
		</label>
		<g:select id="employee" name="employee" from="${Employee.findAll()}" optionKey="id" required="" optionValue="lastName" value="${authorizationTypeInstance?.employee?.id}" class="many-to-one"         />
	</div>
</g:if>
<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'startDate', 'error')} required">
	<label for="startDate" style="width:160px;">
		<g:message code="authorization.startDate.label" default="Start Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="startDate" precision="day"  value="${authorizationInstance?.startDate}"  />
</div>
<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'endDate', 'error')} required">
	<label for="endDate" style="width:160px;">
		<g:message code="authorization.endDate.label" default="End Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="endDate" precision="day"  value="${authorizationInstance?.endDate}"  />
</div>
<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'isAuthorized', 'error')} ">
	<label for="isAuthorized" style="width:160px;">
		<g:message code="authorization.isAuthorized.label" default="Is Authorized" />
		
	</label>
	<g:checkBox name="isAuthorized" value="${authorizationInstance?.isAuthorized}" />
</div>
<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'type', 'error')} required">
	<label for="type" style="width:160px;">
		<g:message code="authorizationType.type.label" default="Type" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="cat" name="type" from="${AuthorizationType.findAll()}" optionKey="id" required="" optionValue="name" value="${authorizationTypeInstance?.name?.id}" class="many-to-one"         />
</div>
