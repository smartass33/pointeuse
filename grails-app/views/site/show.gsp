
<%@ page import="pointeuse.Site" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'site.label', default: 'Site')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-site" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="map"><g:message code="site.map.button"/></g:link></li>							
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-site" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list site">
				<g:if test="${siteInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="site.name.label" default="Name" /></span>	
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${siteInstance}" field="name"/></span>		
				</li>
				</g:if>
				<g:if test="${siteInstance?.address}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="address.label" default="Name" /></span>
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${siteInstance}" field="address"/></span>				
				</li>
				</g:if>
				<g:if test="${siteInstance?.town}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="site.town.label" default="Name" /></span>
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${siteInstance}" field="town"/></span>				
				</li>
				</g:if>
				<g:if test="${siteInstance?.postCode}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="site.postCode.label" default="Name" /></span>
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${siteInstance}" field="postCode"/></span>				
				</li>
				</g:if>

			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${siteInstance?.id}" />
					<g:link class="edit" action="edit" id="${siteInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
			
			<div id="employees">
				<BR/>
				<h1><g:message code="site.employees.label"/></h1>
				<g:listSiteEmployee />
			</div>

		</div>
	</body>
</html>
