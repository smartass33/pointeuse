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


<style>
.listButton {
	-moz-box-shadow:inset 0px 1px 0px 0px #d9fbbe;
	-webkit-box-shadow:inset 0px 1px 0px 0px #d9fbbe;
	box-shadow:inset 0px 1px 0px 0px #d9fbbe;
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #048802), color-stop(1, #a5cc52) );
	background:-moz-linear-gradient( center top, #048802 5%, #a5cc52 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#048802', endColorstr='#a5cc52');
	background-color:#048802;
	-moz-border-radius:6px;
	-webkit-border-radius:6px;
	border-radius:6px;
	border:1px solid #83c41a;
	display:inline-block;
	color:#ffffff;
	font-family:arial;
	font-size:15px;
	font-weight:bold;
	padding:2px 12px;
	text-decoration:none;
	text-shadow:1px 1px 0px #86ae47;
}.listButton:hover {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #a5cc52), color-stop(1, #048802) );
	background:-moz-linear-gradient( center top, #a5cc52 5%, #048802 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#a5cc52', endColorstr='#048802');
	background-color:#a5cc52;
}.listButton:active {
	position:relative;
	top:1px;
}

</style>

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
				

		<g:remoteField id="q" action="search" update="divId" onclick="if (this.value=='chercher un salarié') {this.value = '';}"
               name="q" value="${params.q ?: 'chercher un salarié'}" paramName="q" style="vertical-align: middle;"/>


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
