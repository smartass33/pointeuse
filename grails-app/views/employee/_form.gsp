<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Service" %>
<%@ page import="pointeuse.Site" %>



<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'firstName', 'error')} ">
	<label for="firstName">
		<g:message code="employee.firstName.label" default="First Name" />
		
	</label>
	<g:textField name="firstName" value="${employeeInstance?.firstName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'lastName', 'error')} ">
	<label for="lastName">
		<g:message code="employee.lastName.label" default="Last Name" />
		
	</label>
	<g:textField name="lastName" value="${employeeInstance?.lastName}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'userName', 'error')} ">
	<label for="userName">
		<g:message code="employee.username.label" default="User Name" />
		
	</label>
	<g:textField name="userName" value="${employeeInstance?.userName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'matricule', 'error')} ">
	<label for="matricule">
		<g:message code="employee.matricule.label" default="Matricule" />
		
	</label>
	<g:textField name="matricule" value="${employeeInstance?.matricule}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'weeklyContractTime', 'error')} ">
	<label for="weeklyContractTime">
		<g:message code="employee.weeklyContractTime.label" default="weeklyContractTime" />
	</label>
	<g:textField name="weeklyContractTime" value="${employeeInstance?.weeklyContractTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'arrivalDate', 'error')} ">
	<label for="arrivalDate">
		<g:message code="employee.arrivalDate.label" default="arrivalDate" />
	</label>
	<g:if test="${employeeInstance?.arrivalDate != null}">
		<g:datePicker name="arrivalDate" value="${employeeInstance?.arrivalDate}" precision="day"
	              noSelection="['':'-Choose-']" relativeYears="[-40..30]"/>
	</g:if>
	<g:else>
		<g:datePicker name="arrivalDate" value="${new Date()}" precision="day"
	              noSelection="['':'-Choose-']" relativeYears="[-40..30]"/>
	</g:else>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'service', 'error')} ">
	<label for="service">
		<g:message code="employee.service.label" default="Service" />
	</label>
	<g:if test="${employeeInstance?.service != null}">
		<g:select name="employee.service.id"
	          from="${Service.list([sort:'name'])}"
	          noSelection="${['':employeeInstance?.service.name]}"          
	          optionKey="id" optionValue="name"
	          />
	</g:if>
	<g:else>
			<g:select name="employee.service.id"
	          from="${Service.list([sort:'name'])}"
	          noSelection="${['':'-']}"          
	          optionKey="id" optionValue="name"
	          />
	</g:else>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'site', 'error')} ">
	<label for="site">
		<g:message code="employee.site.label" default="Site" />
	</label>
	
	<g:if test="${employeeInstance?.site != null}">
		<g:select name="employee.site.id"
	          from="${Site.list([sort:'name'])}"
	          noSelection="${['':employeeInstance?.site.name]}"          
	          optionKey="id" optionValue="name"
	          />
	</g:if>
	<g:else>
			<g:select name="employee.site.id"
	          from="${Site.list([sort:'name'])}"
	          noSelection="${['':'-']}"          
	          optionKey="id" optionValue="name"
	          />
	</g:else>
</div>



