
<%@ page import="pointeuse.InAndOut" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'inAndOut.label', default: 'InAndOut')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-inAndOut" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-inAndOut" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list inAndOut">
		
			
				<g:if test="${inAndOutInstance?.time}">
				<li class="fieldcontain">
					<span id="time-label" class="property-label"><g:message code="inAndOut.time.label" default="Time In" /></span>
						<span class="property-value" aria-labelledby="time-label"><g:formatDate date="${inAndOutInstance?.time.format('H:mm d-M-yyyy')}" /></span>
				</li>
				</g:if>
			
				<g:if test="${inAndOutInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="inAndOut.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label">
							<g:if test="${inAndOutInstance?.type.equals('S')}">${message(code: 'employee.out.label', default: 'Sortie')}</g:if>		
							<g:else>${message(code: 'employee.in.label', default: 'Entree')}</g:else>
						</span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${inAndOutInstance?.id}" />
					<g:link class="edit" action="edit" id="${inAndOutInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
