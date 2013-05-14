<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<!doctype html>
<html>
<head>
<g:javascript library="prototype" plugin="prototype"/>
<resource:autoComplete skin="default" />

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
					<br><br>
					<g:form action="search" controller="employee" method="POST">
					<g:message code="laboratory.label" default="Search" style="vertical-align: middle;" />				
					<g:if test="${siteId != null && !siteId.equals('')}">          
					        <g:select name="site.id" from="${Site.list([sort:'name'])}"
							noSelection="${['':site.name]}" optionKey="id" optionValue="name"
							onChange="${remoteFunction(controller:'employee',action:'list',update:'divId',params:'\'category=\'+this.value')}" 
							style="vertical-align: middle;"/>
					</g:if>
					<g:else>
					        <g:select name="site.id" from="${Site.list([sort:'name'])}"
							noSelection="${['':'-']}" optionKey="id" optionValue="name"
							onChange="${remoteFunction(action:'list', params:'\'site=\' + this.value',update:'divId')}" />
					</g:else>
					<g:textField id="q" name="q" onclick="if (this.value=='chercher un salarié') {this.value = '';}"
                         value="${params.q ?: 'chercher un salarié'}" style="vertical-align: middle;"/>                              
					<input type="image"
						src="../images/skin/search.png" style="vertical-align: middle;">
					<g:hiddenField name="isAdmin" value="${isAdmin}" />   	      			
					<g:hiddenField name="siteId" value="${siteId}" />   	
										</g:form>	
					      			
		</h1>
		
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>
				<br>
	
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
