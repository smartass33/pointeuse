<%@ page import="pointeuse.Itinerary" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'itinerary.label', default: 'Itinerary')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>		
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">
		<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
		<script src="${grailsApplication.config.context}/js/jquery-ui-timepicker-addon.js"></script>
		
		<script type="text/javascript">
		function datePickerLaunch (){										
					$.timepicker.regional['fr'] = {
						timeOnlyTitle: 'Horaire',
						timeText: 'Horaire',
						hourText: 'Heure',
						minuteText: 'Minute',
						secondText: 'Seconde',
						millisecText: 'Milliseconde',
						timezoneText: 'Fuseau Horaire',
						currentText: 'Horaire Actuel',
						closeText: 'Fermer',
						timeFormat: 'HH:mm',
						amNames: ['AM', 'A'],
						pmNames: ['PM', 'P'],
						isRTL: false
					};
					
					$.timepicker.setDefaults($.timepicker.regional['fr']);
					$( "#time_picker" ).timepicker({
					});	
			
		}

		</script>

		<style type="text/css">
			#newActionForm {
    			display: none;
			}
		</style>
	</head>
	<body>
		<a href="#edit-itinerary" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="edit-itinerary" class="content scaffold-edit" role="main">
			<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${itineraryInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${itineraryInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form url="[resource:itineraryInstance, action:'update']" method="PUT" >
				<g:hiddenField name="version" value="${itineraryInstance?.version}" />
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				</fieldset>
			</g:form>
		</div>
		<g:form url="[resource:itineraryInstance, controller:'itinerary' ,action:'addTheoriticalAction']" method="POST">
			<g:theoriticalActionForm/>
		
			<div id="theoriticalActionTableDiv">
				<g:theoriticalActionTable/>
			</div>
			<g:hiddenField name="itineraryId" value="${itineraryInstance?.id}" />			
		</g:form>
	</body>
</html>
