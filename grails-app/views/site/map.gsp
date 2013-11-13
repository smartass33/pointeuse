<%@ page import="pointeuse.Site" %>
<!DOCTYPE html>
<html>
	<head>
	<gvisualization:apiImport/> 
	    <script type="text/javascript" src="https://www.google.com/jsapi"></script>




	
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'site.label', default: 'Site')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>		
	</head>
	<body>
		<a href="#list-site" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		
		<div id="search">
		
		<!-- form id="myform" method="POST" action="geocode" controller="site">
	  			<g:textField id="address" name="address" autofocus="true" style="vertical-align: middle;" />  			  						
				<input type="submit" class="classname" value="${message(code: 'default.button.login.label', default: 'Create')}" style="vertical-align: middle;">
			</form-->	
		</div>
		<div id="list-site" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

				<div>
      				<gvisualization:map elementId="map" columns="${mapColumns}" data="${mapData}" showTip="${true}" zoomLevel="${80}" enableScrollWheel="${true}"/>
                	<div id="map" style="width: 1100px; height: 400px"></div>
				</div>

		</div>
	</body>
</html>
