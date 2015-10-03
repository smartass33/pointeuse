<%@ page import="pointeuse.User" %>
<!DOCTYPE html>
<html>
	<head>		
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>  
		<r:require module="fileuploader" />		
	</head>
	<body>
		<a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>				
				<li><uploader:uploader id="yourUploaderId"  url="${[controller:'user', action:'importUserList']}" /></li>
			</ul>
		</div>
		<div id="list-user" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="firstname" title="${message(code: 'user.firstname.label', default: 'First Name')}" />					
						<g:sortableColumn property="lastname" title="${message(code: 'user.lastname.label', default: 'Last Name')}" />					
						<g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />
						<g:sortableColumn property="email" title="${message(code: 'user.email.label', default: 'Username')}" />
						<g:sortableColumn property="phoneNumber" title="${message(code: 'user.phone.number.label', default: 'Username')}" />
						<g:sortableColumn property="sites" title="${message(code: 'user.sites.label', default: 'Username')}" />

					</tr>
				</thead>
				<tbody>
					<g:each in="${userInstanceList}" status="i" var="userInstance">
						<g:set var="siteCounter" value="0" />				
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">					
							<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "firstName")}</g:link></td>				
							<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "lastName")}</g:link></td>					
							<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>		
							<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "email")}</g:link></td>									
							<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "phoneNumber")}</g:link></td>	
							<td>
								<g:if test="${userSiteMap.get(userInstance) != null && userSiteMap.get(userInstance).size() > 0}">
									<g:each  in="${userSiteMap.get(userInstance)}" status="j" var="userSite">
										<%siteCounter++%>
										<g:if test="${siteCounter.toString().equals((userSiteMap.get(userInstance).size()).toString())}">
											${userSite}
										</g:if>
										<g:else>
											 ${userSite},
										</g:else>
									</g:each>
								</g:if>							
							</td>	
						</tr>
					</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${userInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
