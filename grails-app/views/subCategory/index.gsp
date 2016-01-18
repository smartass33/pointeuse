
<%@ page import="pointeuse.SubCategory" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'subCategory.label', default: 'SubCategory')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-subCategory" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.feminine.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-subCategory" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>					
						<th><g:message code="category.label" default="Category" /></th>					
						<g:sortableColumn property="name" title="${message(code: 'subCategory.name.label', default: 'Name')}" />											
						<g:sortableColumn property="creationDate" title="${message(code: 'subCategory.creationDate.label', default: 'Creation Date')}" />					
						<th><g:message code="user.username.label" default="User" /></th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${subCategoryInstanceList}" status="i" var="subCategoryInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">				
						<td><g:link action="show" id="${subCategoryInstance.id}">${subCategoryInstance.category.name}</g:link></td>
						<td>${fieldValue(bean: subCategoryInstance, field: "name")}</td>					
						<td><g:formatDate date="${subCategoryInstance.creationDate}" format='dd/MM/yyyy' /></td>											
						<td>${subCategoryInstance.user.firstName} ${subCategoryInstance.user.lastName}</td>					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${subCategoryInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
