<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Vacation" %>
<%@ page import="pointeuse.Absence" %>

<!doctype html>
<html>
	<head>
			
			
	<script src="${grailsApplication.config.context}/js/jquery/jquery.min.js"></script>
	<script src="${grailsApplication.config.context}/js/jquery/jquery-ui.js"></script>
	<script src="${grailsApplication.config.context}/js/moment.min.js"></script>
	<script src="${grailsApplication.config.context}/js/jquery.comiseo.daterangepicker.js"></script>
	<link href="${grailsApplication.config.context}/css/jquery-ui.min.css" rel="stylesheet"/>
	<link href="${grailsApplication.config.context}/css/jjquery.comiseo.daterangepicker.css" rel="stylesheet"/>



    
		<style type="text/css">
			body {
				font-family: Verdana, Arial, sans-serif;
				font-size: 0.9em;
			}

			#newContractForm {
    			display: none;
			}

			#authorizationDiv {
    			display: none;
			}
			
			#authorization_collapse {
    			display: none;
			}
		</style>
		<g:javascript library="application"/> 		
		<r:require module="report"/>
		<r:layoutResources/>	
		<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />		
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>		
  		<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
		<script>
			function closePopup (){
				window.location = $('#closeId').attr('href');
			}		
	
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
						
			$(document).ready(function() {
	   			$('#authorization_toggle').click( function() {
	    			$('#authorizationDiv').slideToggle(400);
	    			$('#authorization_collapse').show();
	    			$('#authorization_toggle').hide();
	    		
	   			});
			});
			
			$(document).ready(function() {
	   			$('#authorization_collapse').click( function() {
	    			$('#authorizationDiv').slideToggle(400);
	    			$('#authorization_collapse').hide();
	    			$('#authorization_toggle').show();
	    		
	   			});
			});
		</script>
	</head>
	<body style="">
		<a href="#edit-employee" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<g:if test='${fromSite}'>
					<li><g:link class="list" action="map" controller="site" params="${[isAdmin:isAdmin,siteId:siteId]}"><g:message code="default.map.site.label" /></g:link></li>			
					<li><g:link class="list" action="list" controller="site" params="${[isAdmin:isAdmin,siteId:siteId,back:true]}"><g:message code="default.list.site.label"  /></g:link></li>			
				</g:if>
				<li><g:link class="list" action="list" params="${[isAdmin:isAdmin,siteId:siteId,back:true]}"><g:message code="default.list.label" args="[entityName]" /></g:link></li>			
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="edit-employee" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${employeeInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${employeeInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form method="post" >
				<g:hiddenField name="id" value="${employeeInstance?.id}" />
				<g:hiddenField name="isAdmin" value="${isAdmin}" />	
				<g:hiddenField name="siteId" value="${siteId}" />			
				<g:hiddenField name="version" value="${employeeInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.employee.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
			<div id='authorization_input_image' style='padding-left:2.7em;'>
				<button type='button' id="authorization_toggle" ><img alt="toggle" src="${grailsApplication.config.context}/images/expand.png"></button>
				<button type='button' id="authorization_collapse" ><img alt="toggle" src="${grailsApplication.config.context}/images/collapse.png"></button>
				${message(code: 'authorization.management', default: 'Gestion des habilitations')}
			</div>	
			<sec:ifAnyGranted roles="ROLE_SUPER_ADMIN">								
				<div id="authorizationDiv" style="padding-left:1.5em;">	
					<g:authorizationCategoryTable showEmployee="${false}"/>
				</div>
			</sec:ifAnyGranted>
			<h1><g:message code="contract.and.status.management"/></H1>		
			<BR>
			<g:form method="post" >
				<g:hiddenField name="id" value="${employeeInstance?.id}" />
				<g:hiddenField name="isAdmin" value="${isAdmin}" />	
				<g:hiddenField name="siteId" value="${siteId}" />			
				<g:hiddenField name="version" value="${employeeInstance?.version}" />
				<g:if test="${employeeInstance.id != null }">
					<div id='contractTable'>
						<g:employeeContractTable/>
					</div>
				</g:if>
			</g:form>
			<g:contractStatus/>
			<g:vacationEditTable/>	
			<script type="text/javascript">
				$(function(){
					$('#paidHSDiv').load('${createLink(controller:'employee', action:'getSupplementaryTime',params:[id:employeeInstance?.id])}');
			     });
			</script>
		</div>
		<div id="sickLeave">
		    <input id="e1" name="e1">
		
		
		</div>
	</body>
</html>
