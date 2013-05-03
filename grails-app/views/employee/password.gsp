
<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.InAndOut" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>MOT DE PASSE</title>
	</head>
	<body>
		<div id="show-employee" class="content scaffold-show" role="main">
			<h1>ENTRER VOTRE MOT DE PASSE</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table border="0">
			<tr>
				<g:if test="${employeeInstance?.firstName}">				
					<g:if test="${employeeInstance?.lastName}">
						<td><g:fieldValue bean="${employeeInstance}" field="lastName"/>
					</g:if>
					<td><g:fieldValue bean="${employeeInstance}" field="firstName"/></td>
				</g:if>
				<g:form>
					<g:hiddenField name="id" value='${userId}' />
					<td><g:passwordField name="password" value="${password}"/></td>
					<td><g:actionSubmit value="${message(code: 'password.label', default: 'pwd')}" action="validatePassword"/></td>
				</g:form>
				</tr>
			</table>
		</div>
	</body>
</html>
