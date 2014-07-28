<html>
	<head>
		<meta name='layout' content='main'/>
		<title><g:message code="springSecurity.login.title"/></title>
	</head>
	<body>
		<div id='login'>
			<div class='inner'>
				<div class='fheader'><g:message code="springSecurity.login.header"/></div>
				<g:if test='${flash.message}'>
					<div class='login_message'>${flash.message}</div>
				</g:if>
				<form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
					<p>
						<label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
						<input type='text' class='text_' name='j_username' id='username'/>
					</p>
					<p>
						<label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
						<input type='password' class='text_' name='j_password' id='password'/>
					</p>
					<p id="remember_me_holder">		
						<label for='remember_me'>
							<input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>   <g:message code="springSecurity.login.remember.me.label"/>
							<BR><BR>
							
							<input type='submit'  class="adminLogin" id="submit" value='${message(code: "springSecurity.login.button")}'/>
						</label>
					</p>
	
				</form>
			</div>
		</div>
	</body>
</html>
