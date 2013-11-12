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
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-site" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

<%
				
			def mapData2=[       
          [44.89156, -0.70898, 'Saint-Médard <BR> address'],
          [44.86548, -0.66613, 'Le Haillan'],
          [44.85891, -0.51520, 'Cenon'],
          [44.88016, -0.52311, 'Lormont'],       
          [44.78527, -0.49689, 'Latresne'],
          [45.19254, -0.74464, 'Pauillac'],
          [44.88395, -0.63419, 'Eysines'],
          [44.83345, -0.52929, 'Floirac'],
          [44.73173, -0.60013, 'Leognan'],
          [44.68911, -0.51577, 'La Brède'],
 		  [44.63227, -1.14408, 'La Teste'],         
          [44.92655, -0.48949, 'Ambares']
        ]
      

				 %>
				<div>
      				<gvisualization:map elementId="map" columns="${mapColumns}" data="${mapData}" showTip="${true}" zoomLevel="${80}" enableScrollWheel="${true}"/>
                	<div id="map" style="width: 533px; height: 400px"></div>
				</div>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'site.name.label', default: 'Name')}" />			
						<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">
							<th>${message(code: 'creation.date.label', default: 'Name')}</th>
							<th>${message(code: 'administrator.label', default: 'Name')}</th>
						</sec:ifAnyGranted>
						<th>${message(code: 'address.label', default: 'Name')}</th>
						
					</tr>
				</thead>
				

				<tbody>
					<g:each in="${siteInstanceList}" status="i" var="siteInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">			
							<td><g:link action="show" id="${siteInstance.id}">${fieldValue(bean: siteInstance, field: "name")}</g:link></td>				
							<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">
								<td>${siteInstance.loggingDate.format('dd/MM/yyyy')}</td>
								<td>${siteInstance.user.firstName} ${siteInstance.user.lastName}</td>
							</sec:ifAnyGranted>
							<td>${siteInstance.address}</td>				
							
						</tr>
					</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${siteInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
