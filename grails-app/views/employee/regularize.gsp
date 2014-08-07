<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="pointeuse.InAndOut"%>
<html>
	<head>
		<g:javascript library="jquery" />
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<meta name="layout" content="main" />
		<title>Régularisation des sorties oubliées</title>
	</head>
	<body>
	<div>
			<h1>
				<font size="5"> <g:message code="employee.label"
						default="Last Name" />: <g:fieldValue bean="${employee}"
						field="firstName" /> <g:fieldValue bean="${employee}"
						field="lastName" />
				</font>						
			</h1>
			<g:form method="post" action="modifyTime">
				<div id="last5days">
					<h1>
						<g:message code="event.regularization.label" default="Last Name" />
					</h1>
					<g:if test="${flash.message}">
						<div class="message">
							${flash.message}
						</div>
					</g:if>
					<table border="1">
						<thead>
							<th><g:message code="events.label" default="Last Name" /></th>
							<th><g:actionSubmit value="appliquer" class="listButton" action="modifyTime" /></th>
						</thead>
						<tbody>
							<g:each in="${systemGeneratedEvents}" status="k" var="inOrOut">
								<tr>
									<td><g:formatDate format="E dd MMM yyyy'" date="${inOrOut.time}" /></td>
									<td><g:textField id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center" style="font-weight: bold" /></td>
								</tr>
								<g:hiddenField name="inOrOutId" value="${inOrOut.id}" />
								<g:hiddenField name="time"
									value="${inOrOut.time.format('yyyy-M-d H:mm:ss')}" />
								<g:hiddenField name="day" value="${inOrOut.day}" />
								<g:hiddenField name="month" value="${inOrOut.month}" />
								<g:hiddenField name="year" value="${inOrOut.year}" />
							</g:each>
						</tbody>
					</table>
				</div>
				<g:hiddenField name="userId" value="${employee.id}" />
				<g:hiddenField name="employee.id" value="${employee.id}" />
				<g:hiddenField name="fromRegularize" value="${true}" />
			</g:form>
		</div>
	</body>
</html>