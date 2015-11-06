<%@ page import="pointeuse.EmployeeDataListMap" %>
<%@ page import="pointeuse.EmployeeDataType" %>
<!DOCTYPE html>
<html>
	<head>
		<style type="text/css">
			#newEmployeeDataForm {
    			display: none;
			}
		</style>
		<g:javascript library="application"/> 	
		<r:require module="report"/>
		<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'employeeDataListMap.label', default: 'EmployeeDataListMap')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-employeeDataListMap" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-employeeDataListMap" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			<g:if test="${employeeDataListMapInstance != null}">			
				<table>
					<thead>
						<tr>					
							<th><g:message code="employeeDataListMap.fieldName.label" default="Field name" /></th>					
							<th><g:message code="employeeDataListMap.fieldType.label" default="Field type" /></th>						
						</tr>
					</thead>
					<tbody>
						<g:each in="${employeeDataListMapInstance.fieldMap}" status="i" var="fieldMap">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">								
								<td>${fieldMap.key}</td>
								<td>${fieldMap.value}</td>
							</tr>
						</g:each>
					</tbody>
				</table>
			</g:if>
			<g:form method="post" >			
				<script>		
					$(document).ready(
					    function() {
					        $("#newEmployeeDataButton").click(function() {
					            $("#newEmployeeDataForm").toggle();
					        });
					       	$("#cancelEmployeeDataCreation").click(function() {
					            $("#newEmployeeDataForm").toggle();
					        });   
					   });
				</script>
				<table>
					<tbody>	
						<tr>
							<td><div id="newEmployeeDataButton"><a href="#">Ajouter un champ</a></div></td>
						</tr>	
				
				    	<tr id="newEmployeeDataForm">
				    		<td><input type="text" class="code" id="fieldName"  value="" name="fieldname" /></td>
				    		<td><g:select name="type" from="${EmployeeDataType.values()}"  /></td> 
				    		<td ><a href="#" id="cancelEmployeeDataCreation">${message(code: 'default.button.cancel.label', default: 'cancel')}</a></td>
				    		<td><input type="submit" class="listButton" value="Ajouter" name="_action_addNewEmployeeData"></td>
				    	</tr>
					</tbody>		
				</table>
			</g:form>			

		</div>
	</body>
</html>
