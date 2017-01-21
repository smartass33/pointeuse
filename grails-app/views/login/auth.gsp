<g:set var='securityConfig' value='${applicationContext.springSecurityService.securityConfig}'/>
<html>
	<head>
		<meta name="layout" content="main"/>
		<s2ui:title messageCode='spring.security.ui.login.title'/>
		<asset:stylesheet src='spring-security-ui-auth.css'/>
	</head>
	<body>
		<p/>
		<div id='login' style='margin: auto;'>
			<div class="login s2ui_center ui-corner-all" style='margin: auto;'>
				<div class="login-inner">
					<s2ui:form type='login' focus='username'  id='loginForm'>
						<div class="sign-in">
							<h2><g:message code='springSecurity.login.header'/></h2>
							<table>
								<tr>
									<td><label for="username"><g:message code='springSecurity.login.username.label'/></label></td>
									<td><input type="text" name="${securityConfig.apf.usernameParameter}" id="username" class='formLogin' size="20"/></td>
								</tr>
								<tr>
									<td><label for="password"><g:message code='springSecurity.login.password.label'/></label></td>
									<td><input type="password" name="${securityConfig.apf.passwordParameter}" id="password" class="formLogin" size="20"/></td>
								</tr>
								<tr>
									<td colspan='2'>
										<input type="checkbox" class="checkbox" name="${securityConfig.rememberMe.parameter}" id="remember_me" checked="checked"/>
										<label for='remember_me'><g:message code='springSecurity.login.remember.me.label'/></label> |
										<span class="forgot-link">
											<g:link controller='register' action='forgotPassword'><g:message code='spring.security.ui.login.forgotPassword'/></g:link>
										</span>
									</td>
								</tr>
								<tr>
									<td colspan='2'>
										<input type='submit' style='margin: auto;'class="adminLogin" id="submit" value='${message(code: "springSecurity.login.button")}'/>	
									</td>
								</tr>
							</table>
						</div>
					</s2ui:form>
				</div>
			</div>
		</div>
	</body>
</html>

