<!DOCTYPE html>
<html>
	<body>
		<div id="grailsLogo" role="banner"><a href="${grailsApplication.config.serverURL}/${grailsApplication.config.context}"><asset:image src="biolab3.png"/></a></div>		
		<div><br><br></div>
		<div>${message(code: 'user.email.body.greeting',args:[user.firstName])}</div>
		<div>${message(code: 'user.email.body.sentence',args:[site.name,date])}</div>
		<div><br><br></div>
		<div>${message(code: 'user.email.signature')}</div>
		<div><br><br></div>		
	</body>
</html>
