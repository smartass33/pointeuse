<html>
	<head>
		<meta name="layout" content="${layoutUi}"/>
		<s2ui:title messageCode='default.create.label' entityNameMessageCode='user.label' entityNameDefault='User'/>
	</head>
	<body>
		<h3><g:message code='default.create.label' args='[entityName]'/></h3>
		<s2ui:form type='save' beanName='user' focus='username'>
			<s2ui:tabs elementId='tabs' height='375' data='${tabData}'>
				<s2ui:tab name='userinfo' height='280'>
					<table>
					<tbody>
						<s2ui:textFieldRow name='username' labelCodeDefault='${message(code: 'springSecurity.login.username.label', default: 'Create')}'/>
						<s2ui:textFieldRow name='firstName' labelCodeDefault='${message(code: 'user.firstname.label', default: 'Create')}'/>
						<s2ui:textFieldRow name='lastName' labelCodeDefault='${message(code: 'user.lastname.label', default: 'Create')}'/>
						<s2ui:textFieldRow name='email' labelCodeDefault='${message(code: 'user.email.label', default: 'Create')}'/>				
						<s2ui:passwordFieldRow name='password' labelCodeDefault='${message(code: 'springSecurity.login.password.label', default: 'Create')}'/>
						<s2ui:checkboxRow name='enabled' labelCodeDefault='${message(code: 'user.enabled.label', default: 'Create')}'/>
						<s2ui:checkboxRow name='accountExpired' labelCodeDefault='${message(code: 'user.accountExpired.label', default: 'Create')}'/>
						<s2ui:checkboxRow name='accountLocked' labelCodeDefault='${message(code: 'user.accountLocked.label', default: 'Create')}'/>
						<s2ui:checkboxRow name='passwordExpired' labelCodeDefault='${message(code: 'user.passwordExpired.label', default: 'Create')}'/>
					</tbody>
					</table>
				</s2ui:tab>
				<s2ui:tab name='roles' height='280'>
					<g:each var='role' in='${authorityList}'>
					<div>
						<g:set var='authority' value='${uiPropertiesStrategy.getProperty(role, 'authority')}'/>
						<g:checkBox name='${authority}'/>
						<g:link controller='role' action='edit' id='${role.id}'>${authority}</g:link>
					</div>
					</g:each>
				</s2ui:tab>
			</s2ui:tabs>
			<div style='float:left; margin-top: 10px;'>
				<s2ui:submitButton/>
			</div>
		</s2ui:form>
	</body>
</html>
