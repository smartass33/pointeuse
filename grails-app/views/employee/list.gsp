<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<!doctype html>
<html>
<head>
<g:javascript library="prototype" plugin="prototype"/>

<meta name="layout" content="main" id="mainLayout">
<g:set var="isNotSelected" value="true" />

<g:set var="entityName"
	value="${message(code: 'employee.label', default: 'Employee')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<div class="nav" id="nav">
		<g:headerMenu/>
	</div>
	<div id="list-employee" class="content scaffold-list">
		<h1>
			<g:message code="default.list.label" args="[entityName]" />
				<g:form action="search" controller="employee">
	       			   Chercher un salarié
	        			<input type="text" name="q" value="${params.q}" />
					<input type="submit"
						style="background-image: url('../images/skin/search.png')"
						value="Recherche" />
						<g:hiddenField name="isAdmin" value="${isAdmin}" />   	      			
						<g:hiddenField name="siteId" value="${siteId}" />   	      			
	
				Laboratoire:
				<g:if test="${siteId != null && !siteId.equals('')}">          
				        <g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':site.name]}" optionKey="id" optionValue="name"
						onChange="${remoteFunction(controller:'employee',action:'list',update:'divId',params:'\'category=\'+this.value')}" />
				</g:if>
				<g:else>
				        <g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':'-']}" optionKey="id" optionValue="name"
						onChange="${remoteFunction(action:'list', params:'\'site=\' + this.value',update:'divId')}" />
				</g:else>
			</g:form>
			
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>

	<div id="divId">
		<g:listEmployee />
	</div>
	<div class="pagination" id="pagination">
		<g:hiddenField name="isAdmin" value="${isAdmin}" />
		<g:paginate total="${employeeInstanceTotal}"
			params="${[isAdmin:isAdmin]}" />
	</div>
</body>
</html>
