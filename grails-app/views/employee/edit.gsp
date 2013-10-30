<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Vacation" %>

<!doctype html>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery" />
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#edit-employee" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list" params="${[isAdmin:isAdmin,siteId:siteId]}"><g:message code="default.list.label" args="[entityName]" /></g:link></li>			
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="userList" controller="vacation" params="${[isAdmin:isAdmin,userId:employeeInstance.id]}"><g:message code="vacation.access.label" default="Site" /></g:link></li>
			</ul>
		</div>
		<div id="edit-employee" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${employeeInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${employeeInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form method="post" >
				<g:hiddenField name="id" value="${employeeInstance?.id}" />
				<g:hiddenField name="isAdmin" value="${isAdmin}" />	
				<g:hiddenField name="siteId" value="${siteId}" />			
				<g:hiddenField name="version" value="${employeeInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
			
			
			<table>
				<thead>
					<tr>				
						<g:sortableColumn property="counter" title="${message(code: 'vacation.counter.label', default: 'Counter')}" />
						<g:sortableColumn property="period" title="${message(code: 'vacation.period.label', default: 'Period')}" />				
						<g:sortableColumn property="type" title="${message(code: 'vacation.type.label', default: 'Type')}" />				
					</tr>
				</thead>
				<tbody>

				<g:each in="${vacationList}" status="i" var="vacationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<g:if test="${vacationInstance != null}">
							<td>${fieldValue(bean: vacationInstance, field: "counter")}</td>
							<td>${vacationInstance.year.period}</td>					
							<td>${fieldValue(bean: vacationInstance, field: "type")}</td> 		
						</g:if>					
					</tr>
				</g:each>				
				</tbody>
			</table>
			
			
		</div>
	</body>
</html>
