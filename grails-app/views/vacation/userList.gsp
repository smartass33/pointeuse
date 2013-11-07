
<%@ page import="pointeuse.Vacation" %>
<!DOCTYPE html>
<html>
	<head>		
		<g:javascript library="jquery" plugin="jquery" />
		<g:javascript src="/jquery/jquery.table.addrow.js" />
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'vacation.label', default: 'Vacation')}" />
		<title><g:message code="default.list.label" args="[entityName]" /> </title>
	</head>
	<body>
		<a href="#list-vacation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create" params="${[isAdmin:isAdmin,userId:userId]}"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-vacation" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>					
						<g:sortableColumn property="period" title="${message(code: 'vacation.period.label', default: 'Period')}" />					
						<g:sortableColumn property="type" title="${message(code: 'vacation.type.label', default: 'Type')}" />					
						<g:sortableColumn property="counter" title="${message(code: 'vacation.counter.label', default: 'Counter')}" />					
						<th><g:message code="vacation.employee.label" default="Employee" /></th>				
					</tr>
				</thead>
				<tbody>
				<g:each in="${vacationInstanceList}" status="i" var="vacationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">				
						<td>${vacationInstance.year.period}</td>					
						<td>${fieldValue(bean: vacationInstance, field: "type")}</td>				
						<td>
		        <div>
            		<input type="number" name="name"          		
	           			onchange="${remoteFunction(action:'changeValue', controller:'vacation', 
								  	params:'\'userId=' + userId 						  
										+ '&vacationId=' + vacationInstance.id
								  		+ '&counter=\' + this.value'								  )}"
	                    name="absenceType" 
	                    value="${vacationInstance.counter}" 
						min="0" max="30"	
	            	/>
        		</div>
				</td>					
						<td>${vacationInstance.employee.firstName} ${vacationInstance.employee.lastName}</td>									
					</tr>
				</g:each>
				</tbody>
			</table>
				
			<div class="pagination">
				<g:paginate total="${vacationInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
