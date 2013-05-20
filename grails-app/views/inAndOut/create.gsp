<%@ page import="pointeuse.InAndOut" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'inAndOut.label', default: 'InAndOut')}" />
		<title>Ajouter un nouvel evenement</title>
	</head>
	<body>
		<a href="#create-inAndOut" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="create-inAndOut" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
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
			<g:form action="save" >
				<g:hiddenField name="userId" value="${inAndOutInstance?.employee?.id}" />				
				<g:hiddenField name="fromReport" value="${reportRedirect}" />				

				<fieldset class="form">
					<g:if test="${complete}">
						<div class="fieldcontain ${hasErrors(bean: inAndOutInstance, field: 'timeOut', 'error')} required">
  					  		choisissez la date: <richui:dateChooser name="inOrOutDate" format="dd/MM/yyyy" value="${period ? period : new Date()}" locale="fr" firstDayOfWeek="Mo"/>
  					 	</div>
				</g:if>
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton class="save" name="create" value="${message(code: 'default.button.create.label', default: 'Create')}" onclick="return confirm('${message(code: 'inAndOut.confirmation.button', default: 'Create')}?')"  />		
				</fieldset>
			</g:form>
		</div>
					<!--a href="/employee/report/?userId=32" title="Close window" onclick="Modalbox.hide({afterHide: function() { alert('Modalbox is now hidden') } }); return false;">Close</a-->
		
	</body>
</html>
