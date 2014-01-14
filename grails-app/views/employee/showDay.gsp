<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<g:javascript library="prototype" />
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<meta name="layout" content="main" />
		<title>Modifier les évenements du jour</title>
	</head>
	<body>
		<g:listInAndOuts id='koko'/>
		<form method="POST">
							<g:hiddenField name="userId" value="${userId}" />
														<g:hiddenField name="monthPeriod" value="${month}" />
														<g:hiddenField name="yearPeriod" value="${year}" />
							
			<g:actionSubmit controller="employee"
						value="Retour" action="report" class="listButton" />
		</form>
	</body>
</html>