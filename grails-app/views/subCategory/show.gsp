
<%@ page import="pointeuse.SubCategory" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'subCategory.label', default: 'SubCategory')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-subCategory" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.feminine.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-subCategory" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list subCategory">
			
				<g:if test="${subCategoryInstance?.category}">
				<li class="fieldcontain">
					<span id="category-label" class="property-label"><g:message code="category.label" default="Category" /></span>				
					<span class="property-value" aria-labelledby="category-label"><g:link controller="category" action="show" id="${subCategoryInstance?.category?.id}">${subCategoryInstance?.category?.name}</g:link></span>				
				</li>
				</g:if>
			
				<g:if test="${subCategoryInstance?.creationDate}">
				<li class="fieldcontain">
					<span id="creationDate-label" class="property-label"><g:message code="subCategory.creationDate.label" default="Creation Date" /></span>					
					<span class="property-value" aria-labelledby="creationDate-label"><g:formatDate date="${subCategoryInstance?.creationDate}" format="dd/MM/yyyy"/></span>					
				</li>
				</g:if>
			
				<g:if test="${subCategoryInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="subCategory.name.label" default="Name" /></span>				
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${subCategoryInstance}" field="name"/></span>				
				</li>
				</g:if>
			
				<g:if test="${subCategoryInstance?.user}">
				<li class="fieldcontain">
					<span id="user-label" class="property-label"><g:message code="user.username.label" default="User" /></span>			
					<span class="property-value" aria-labelledby="user-label">${subCategoryInstance?.user?.firstName} ${subCategoryInstance?.user?.lastName}</span>				
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:subCategoryInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${subCategoryInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
