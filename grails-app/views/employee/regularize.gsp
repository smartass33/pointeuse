<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pointeuse.InAndOut" %>
<html>
	<head>
		<g:javascript library="prototype" />
		<modalbox:modalIncludes/>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
		<meta name="layout" content="main"/>
		<title>Régularisation des sorties oubliées</title>
	</head>
	<body>
	  	<div class="body">
	  		<div id="show-employee" class="content scaffold-show" role="main">
	  			<h1>
	  			<font size="5">
	  			<g:message code="employee.label" default="Last Name" />: <g:fieldValue bean="${employee}" field="firstName"/> 
	  			<g:fieldValue bean="${employee}" field="lastName"/>	  				  
	  			</h1>

	  			</font>
	  			<g:form method="post" action="timeModification"  >
	  			
	  			<div id="last5days">
	  				<h1>Evènements oubliés</h1>
		  			<table border="1">
		  				<thead>
		  				<th>Evenements</th>
		  				<th>
		  					<g:actionSubmit value="appliquer"  action="timeModification"/>
		  				</th>
		  				</thead>
		  				<tbody>
			  				<g:each in="${systemGeneratedEvents}" status="k" var="inOrOut">
				  				<tr>
				  					<td><g:formatDate format="E dd MMM yyyy'" date="${inOrOut.time}"/></td>
				  					<td><g:textField id="myinput" name="cell"
															value="${inOrOut.time.format('H:mm')}" align="center"
															style="font-weight: bold" /></td>
				  					
				  				</tr>
				  														<g:hiddenField name="inOrOutId" value="${inOrOut.id}" />
										<g:hiddenField name="time" value="${inOrOut.time.format('yyyy-M-d H:mm:ss')}" /> 
										<g:hiddenField name="day" value="${inOrOut.day}" /> 
										<g:hiddenField name="month" value="${inOrOut.month}" /> 
										<g:hiddenField name="year" value="${inOrOut.year}" /> 
				  			</g:each>
		  				</tbody>
		  			</table>
	  			</div
	  				<g:hiddenField name="userId" value="${employee.id}" />
	  				<g:hiddenField name="employee.id" value="${employee.id}" />
			  		<g:hiddenField name="fromRegularize" value="${true}" />
		
				
	  			</g:form>
	
	</body>
</html>