<!doctype html>
<html class="no-js" lang="">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="x-ua-compatible" content="ie=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<s2ui:stylesheet src='spring-security-ui-register.css'/>
<g:layoutHead/>
	</head>
	<body>
		<div id="grailsLogo" role="banner"><a href="${grailsApplication.config.serverURL}/${grailsApplication.config.context}"><asset:image src="${grailsApplication.config.laboratory.logo}"/></a></div>		
		<p/>
		<g:layoutBody/>
		<asset:javascript src='spring-security-ui-register.js'/>
		<s2ui:showFlash/>
		<s2ui:deferredScripts/>
		<div class="footer" role="contentinfo" id="footer"></div>
		<div id="spinner" class="spinner" style="display: none;"><asset:image src="spinner.gif" width="16" height="16"/><g:message code="spinner.loading.label"/></div>
	</body>
</html>
