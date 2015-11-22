<%@ page import="pointeuse.Site" %>
<!DOCTYPE html>
<html>
	<head>
	<gvisualization:apiImport/> 
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'site.label', default: 'Site')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>		
	</head>
	<body>
		<a href="#list-site" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="map"><g:message code="site.map.button"/></g:link></li>			
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-site" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

			<table>
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'site.name.label', default: 'Name')}" />			
						<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">
							<th>${message(code: 'creation.date.label', default: 'Name')}</th>
							<th>${message(code: 'administrator.label', default: 'Name')}</th>
						</sec:ifAnyGranted>
						<th>${message(code: 'address.label', default: 'Name')}</th>
						<th>${message(code: 'site.town.label', default: 'Name')}</th>
						<th>${message(code: 'site.postCode.label', default: 'Name')}</th>
						
					</tr>
				</thead>
				

				<tbody>
					<g:each in="${siteInstanceList}" status="i" var="siteInstance">
						<g:set var="siteAdmin" value="0" />
					
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">			
							<td><g:link action="show" id="${siteInstance.id}">${fieldValue(bean: siteInstance, field: "name")}</g:link></td>				
							<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">
								<td>${siteInstance.loggingDate.format('dd/MM/yyyy')}</td>
								<td>
									<g:each in="${siteInstance.users}" status="j" var="user">
										<%siteAdmin++%>
										<g:if test="${siteAdmin.toString().equals((siteInstance.users.size()).toString())}">									
											${user.firstName} ${user.lastName}
										</g:if>
										<g:else>
											${user.firstName} ${user.lastName},								
										</g:else>
									</g:each>
								</td>
							</sec:ifAnyGranted>
							<td>${siteInstance.address}</td>				
							<td>${siteInstance.town}</td>
							<td>${siteInstance.postCode}</td>
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
