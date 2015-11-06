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
		<title><g:message code="default.create.label" args="[entityName]" /></title>
				<script>
			jQuery(function($){
			   $.datepicker.regional['fr'] = {
			      closeText: 'Fermer',
			      prevText: '<Préc',
			      nextText: 'Suiv>',
			      currentText: 'Courant',
			      monthNames: ['Janvier','Février','Mars','Avril','Mai','Juin',
			      'Juillet','Août','Septembre','Octobre','Novembre','Décembre'],
			      monthNamesShort: ['Jan','Fév','Mar','Avr','Mai','Jun',
			      'Jul','Aoû','Sep','Oct','Nov','Déc'],
			      dayNames: ['Dimanche','Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi'],
			      dayNamesShort: ['Dim','Lun','Mar','Mer','Jeu','Ven','Sam'],
			      dayNamesMin: ['Di','Lu','Ma','Me','Je','Ve','Sa'],
			      weekHeader: 'Sm',
			      dateFormat: 'dd/mm/yy',
			      firstDay: 1,
			      isRTL: false,
			      showMonthAfterYear: false,
			      yearSuffix: ''};
			   $.datepicker.setDefaults($.datepicker.regional['fr']);

			});
		</script>
	</head>
	<body>
		<a href="#create-employeeDataListMap" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="create-employeeDataListMap" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${employeeDataListMapInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${employeeDataListMapInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			
			<g:if test="${EmployeeDataListMap.list() != null && (EmployeeDataListMap.list()).size()>0}">
				<g:each in="${EmployeeDataListMap.list()}" status="i" var="employeeDataListMapInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">				
						<td><g:link action="show" id="${employeeDataListMapInstance.id}">${fieldValue(bean: employeeDataListMapInstance, field: "creationDate")}</g:link></td>
						<g:each in="${employeeDataListMapInstance.fieldMap}" var="fieldMap">
							<td>${fieldMap.key}</td>
							<td>${fieldMap.value}</td>
						</g:each>					
						<td>${employeeDataListMapInstance.creationUser.lastName} ${employeeDataListMapInstance.creationUser.firstName}</td>				
						<td><g:formatDate date="${employeeDataListMapInstance.lastModification}" /></td>					
					</tr>
				</g:each>
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
