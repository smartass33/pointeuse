<%@ page import="pointeuse.Authorization" %>
<%@ page import="pointeuse.Employee" %>

<g:if test="${employeeInstance == null}">
	<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'employee', 'error')} required">
		<label for="employee" style="width:160px;">
			<g:message code="employee.label" default="Type" />
			<span class="required-indicator">*</span>
		</label>
		<g:select id="employee" name="employee" from="${Employee.findAll()}" optionKey="id" required="" optionValue="lastName" value="${authorizationInstance?.employee?.id}" class="many-to-one"         />
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

