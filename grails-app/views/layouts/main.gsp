<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><g:layoutTitle default="Grails"/></title>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">		
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<asset:stylesheet src="main.css"/>
		<asset:stylesheet src="mobile.css"/>
		<g:layoutHead/>
		<r:layoutResources />
	</head>
	<body>
		<div id="grailsLogo" role="banner"><a href="${grailsApplication.config.serverURL}/${grailsApplication.config.context}"><asset:image src="biolab3.png"/></a></div>		
		<g:layoutBody/>
		<div class="footer" role="contentinfo" id="footer"></div>
		<div id="spinner" class="spinner" style="display: none;"><asset:image src="spinner.gif" width="16" height="16"/><g:message code="spinner.loading.label"/></div>
		<g:javascript library="application"/>
		<r:layoutResources />
	</body>
</html>
