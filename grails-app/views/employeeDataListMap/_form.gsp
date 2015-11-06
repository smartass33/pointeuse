<%@ page import="pointeuse.EmployeeDataListMap" %>



<div class="fieldcontain ${hasErrors(bean: employeeDataListMapInstance, field: 'creationDate', 'error')} required">
	<label for="creationDate">
		<g:message code="employeeDataListMap.creationDate.label" default="Creation Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="creationDate" precision="day"  value="${employeeDataListMapInstance?.creationDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: employeeDataListMapInstance, field: 'creationUser', 'error')} required">
	<label for="creationUser">
		<g:message code="employeeDataListMap.creationUser.label" default="Creation User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="creationUser" name="creationUser.id" from="${pointeuse.User.list()}" optionKey="id" required="" value="${employeeDataListMapInstance?.creationUser?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeDataListMapInstance, field: 'lastModification', 'error')} required">
	<label for="lastModification">
		<g:message code="employeeDataListMap.lastModification.label" default="Last Modification" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="lastModification" precision="day"  value="${employeeDataListMapInstance?.lastModification}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: employeeDataListMapInstance, field: 'modificationUser', 'error')} required">
	<label for="modificationUser">
		<g:message code="employeeDataListMap.modificationUser.label" default="Modification User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="modificationUser" name="modificationUser.id" from="${pointeuse.User.list()}" optionKey="id" required="" value="${employeeDataListMapInstance?.modificationUser?.id}" class="many-to-one"/>
</div>

