
<%@ page import="pointeuse.EmployeeDataListMap" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'employeeDataListMap.label', default: 'EmployeeDataListMap')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-employeeDataListMap" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-employeeDataListMap" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list employeeDataListMap">
			
				<g:if test="${employeeDataListMapInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="employeeDataListMap.creationDate.label" default="Creation Date" /></span>
					
						<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${employeeDataListMapInstance?.creationDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${employeeDataListMapInstance?.creationUser}">
				<li class="fieldcontain">
					<span id="creationUser-label" class="property-label"><g:message code="employeeDataListMap.creationUser.label" default="Creation User" /></span>
					
						<span class="property-value" aria-labelledby="creationUser-label"><g:link controller="user" action="show" id="${employeeDataListMapInstance?.creationUser?.id}">${employeeDataListMapInstance?.creationUser?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${employeeDataListMapInstance?.lastModification}">
				<li class="fieldcontain">
					<span id="lastModification-label" class="property-label"><g:message code="employeeDataListMap.lastModification.label" default="Last Modification" /></span>
					
						<span class="property-value" aria-labelledby="lastModification-label"><g:formatDate date="${employeeDataListMapInstance?.lastModification}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${employeeDataListMapInstance?.modificationUser}">
				<li class="fieldcontain">
					<span id="modificationUser-label" class="property-label"><g:message code="employeeDataListMap.modificationUser.label" default="Modification User" /></span>
					
						<span class="property-value" aria-labelledby="modificationUser-label"><g:link controller="user" action="show" id="${employeeDataListMapInstance?.modificationUser?.id}">${employeeDataListMapInstance?.modificationUser?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:employeeDataListMapInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${employeeDataListMapInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
