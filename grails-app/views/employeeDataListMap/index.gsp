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
		<g:javascript library="jquery" plugin="jquery" />
		<r:require module="report"/>
		<g:javascript src="/jquery/jquery.min.js" />
		<g:javascript src="/jquery/jquery-ui.custom.min.js" />
		<script type="text/javascript">
			function closePopup ( ){
				window.location = $('#closeId').attr('href');
			}
		</script>

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
				<!--li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li-->
			</ul>
		</div>
		<div id="list-employeeDataListMap" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<div id='employeeDataDiv'>

				<g:employeeDataListTable/>
			</div>
			<g:form method="post">			
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
							<td><div id="newEmployeeDataButton"><a href="#"><g:message code="employeeDataListMap.add.label" default="Field name" /></a></div></td>
						</tr>					
				    	<tr id="newEmployeeDataForm">
				    		<td><input type="text" class="code" id="fieldName"  value="" name="fieldname" /></td>
				    		<td><g:select name="type" from="${EmployeeDataType.values()}"  /></td> 
				    		<td ><a href="#" id="cancelEmployeeDataCreation">${message(code: 'default.button.cancel.label', default: 'cancel')}</a></td>
				    		<td><input type="submit" class="listButton" value="${message(code: 'default.button.add.label', default: 'cancel')}" name="_action_addNewEmployeeData"></td>
				    	</tr>
					</tbody>		
				</table>
			</g:form>			
		</div>
	</body>
</html>
