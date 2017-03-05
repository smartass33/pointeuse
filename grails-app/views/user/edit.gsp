<%@ page import="pointeuse.Site" %>

<sec:ifNotSwitched>
	<sec:ifAllGranted roles='${securityConfig.ui.switchUserRoleName}'>
	<g:set var='username' value='${uiPropertiesStrategy.getProperty(user, 'username')}'/>
	<g:if test='${username}'>
	<g:set var='canRunAs' value='${true}'/>
	</g:if>
	</sec:ifAllGranted>
</sec:ifNotSwitched>
<html>
	<head>
		<meta name="layout" content="${layoutUi}"/>
		<s2ui:title messageCode='default.edit.label' entityNameMessageCode='user.label' entityNameDefault='User'/>
	</head>
	<body>
		<h3><g:message code='default.edit.label' args='[entityName]'/></h3>
		<s2ui:form type='update' beanName='user' focus='username' class='button-style'>
			<s2ui:tabs elementId='tabs' height='375' data='${tabData}'>
				<s2ui:tab name='userinfo' height='275'>
					<table>
					<tbody>
						<s2ui:textFieldRow name='username' labelCodeDefault='${message(code: 'springSecurity.login.username.label', default: 'Create')}'/>
						<s2ui:textFieldRow name='firstName' labelCodeDefault='${message(code: 'user.firstname.label', default: 'Create')}'/>
						<s2ui:textFieldRow name='lastName' labelCodeDefault='${message(code: 'user.lastname.label', default: 'Create')}'/>
				
						<s2ui:passwordFieldRow name='password' labelCodeDefault='${message(code: 'springSecurity.login.password.label', default: 'Create')}'/>
						<s2ui:textFieldRow name='email' labelCodeDefault='${message(code: 'user.email.label', default: 'Create')}'/>		
						<g:message code="user.receive.site.mail.label" default="Username" />
							<g:checkBox name="hasMail" value="${user.hasMail}" checked="${user.hasMail}"/>
						<s2ui:textFieldRow name='phoneNumber' labelCodeDefault='${message(code: 'user.phone.number.label', default: 'Create')}'/>		
							<g:message code="user.report.send.day.label" default="Username" /><g:select id="reportSendDay" name="reportSendDay" from="${1..30}" value="${user.reportSendDay}" />
						<s2ui:checkboxRow name='enabled' labelCodeDefault='${message(code: 'user.enabled.label', default: 'Create')}'/>
						<s2ui:checkboxRow name='accountExpired' labelCodeDefault='${message(code: 'user.accountExpired.label', default: 'Create')}'/>
						<s2ui:checkboxRow name='accountLocked' labelCodeDefault='${message(code: 'user.accountLocked.label', default: 'Create')}'/>
						<s2ui:checkboxRow name='passwordExpired' labelCodeDefault='${message(code: 'user.passwordExpired.label', default: 'Create')}'/>
					</tbody>
					</table>
					<BR>
					<h1><g:message code="site.list" /></h1>
					<table>
						<g:each var="site" in="${Site.list([sort:'name'])}">
							   <tr>
							   	<td class='eventTD'>${site.name}</td>
							   	<g:if test="${site.users.contains(user)}">
							   		<td class='eventTD'><g:checkBox name="siteId" value="${site.id}" checked="${true}"> </g:checkBox></td>
								</g:if>
								<g:else>
									<td class='eventTD'><g:checkBox name="siteId" value="${site.id}" checked="${false}"> </g:checkBox></td>
								</g:else>
							   </tr>
						</g:each>
					</table>
				</s2ui:tab>
				<s2ui:tab name='roles' height='275'>
					<g:each var='entry' in='${roleMap}'>
					<g:set var='roleName' value='${uiPropertiesStrategy.getProperty(entry.key, 'authority')}'/>
					<div>
						<g:checkBox name='${roleName}' value='${entry.value}'/>
						<g:link controller='role' action='edit' id='${entry.key.id}'>${roleName}</g:link>
					</div>
					</g:each>
				</s2ui:tab>				

				
		</s2ui:tabs>
		<div style="float:left; margin-top: 10px;">
		<s2ui:submitButton/>
		<g:if test='${user}'>
		<s2ui:deleteButton/>
		</g:if>
		<g:if test='${canRunAs}'>
		<a id="runAsButton">${message(code:'spring.security.ui.runas.submit')}</a>
		</g:if>
		</div>
		</s2ui:form>
		<g:if test='${user}'>
		<s2ui:deleteButtonForm instanceId='${user.id}'/>
		</g:if>
		<g:if test='${canRunAs}'>
		<form name="runAsForm" action="${request.contextPath}${securityConfig.switchUser.switchUserUrl}" method='post'>
			<g:hiddenField name='${securityConfig.switchUser.usernameParameter}' value='${username}'/>
			<input type="submit" class="s2ui_hidden_button"/>
		</form>
		</g:if>
	<s2ui:documentReady>
	$("#runAsButton").button();
	$('#runAsButton').bind('click', function() {
		document.forms.runAsForm.submit();
	});
	</s2ui:documentReady>
	</body>
</html>
