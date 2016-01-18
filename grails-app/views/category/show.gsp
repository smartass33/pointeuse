
<%@ page import="pointeuse.Category" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'category.label', default: 'Category')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-category" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.feminine.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-category" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list category">
			
				<g:if test="${categoryInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="category.creationDate.label" default="Creation Date" /></span>
					
						<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${categoryInstance?.creationDate}" format="dd/MM/yyyy" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${categoryInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="category.name.label" default="Name" /></span>					
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${categoryInstance}" field="name"/></span>					
				</li>
				</g:if>
			
				<g:if test="${categoryInstance?.subCategories}">
				<li class="fieldcontain">
					<span id="subCategories-label" class="property-label"><g:message code="category.subCategories.label" default="Sub Categories" /></span>
					
						<g:each in="${categoryInstance.subCategories}" var="subCategory">
						<span class="property-value" aria-labelledby="subCategories-label"><g:link controller="subCategory" action="show" id="${subCategory.id}">${subCategory?.name}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${categoryInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="user.username.label" default="User" /></span>
					
						<span class="property-value" aria-labelledby="user-label">${categoryInstance?.user?.firstName} ${categoryInstance?.user?.lastName}</span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:categoryInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${categoryInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
