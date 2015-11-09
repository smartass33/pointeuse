<%@ page import="pointeuse.Authorization" %>
<!DOCTYPE html>
<html>
	<head>
		<g:javascript library="application"/> 	
		<r:require module="report"/>
		<script type="text/javascript">
			function closePopup ( ){
				window.location = $('#closeId').attr('href');
			}
		</script>	
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'default.authorization.label', default: 'Authorization')}" />
		<title><g:message code="authorization.management"  /></title>
	</head>
	<body>
		<a href="#list-authorization" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create" params="${[employeeInstanceId:employeeInstanceId]}"><g:message code="default.feminine.new.label" args="[entityName]" /></g:link></li>
				<li><g:authorizationPopup fromReport="${false}" fromEditEmployee="${false}" showEmployee="${true}"/></li>
			</ul>
		</div>		
		<div id="authorizationDiv">
			<g:authorizationTable showEmployee="${true}"/>
		</div>

	</body>
</html>
