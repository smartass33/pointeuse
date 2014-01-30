<%@ page import="pointeuse.InAndOut" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'inAndOut.label', default: 'InAndOut')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#edit-inAndOut" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="edit-inAndOut" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${inAndOutInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${inAndOutInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form method="post" >
				<g:hiddenField name="id" value="${inAndOutInstance?.id}" />
				<g:hiddenField name="version" value="${inAndOutInstance?.version}" />

				<div class="fieldcontain ${hasErrors(bean: inAndOutInstance, field: 'time', 'error')}required">
					<g:datePicker name="time" value="${inAndOutInstance?.time}" precision="minute" />				
				</div>
				<div class="fieldcontain ${hasErrors(bean: inAndOutInstance, field: 'timeOut', 'error')} required">
					<g:select name="event" from="${['E','S']}" valueMessagePrefix="entry.name" value="${inAndOutInstance?.type}" noSelection="['':'-Choisissez votre élément-']"/>
				</div>

				<fieldset class="buttons">
					<g:actionSubmit class="save" action="modifyTime" controller="employee" value="${message(code: 'default.button.update.label', default: 'Update')}" />
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
