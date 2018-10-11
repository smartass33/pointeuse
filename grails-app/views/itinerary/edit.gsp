<%@ page import="pointeuse.Itinerary" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'itinerary.label', default: 'Itinerary')}" />
		<g:javascript library="application"/> 		

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
		function closePopup ( ){
			window.location = $('#closeId').attr('href');
		}

		function datePickerLaunch (datePickerId){
			$.datepicker.regional['fr'] = {
						closeText: 'Fermer',
						prevText: '<Précédent',
						nextText: 'Suivant>',
						currentText: 'Сегодня',
						monthNames: ['Janvier','Février','Mars','Avril','Mai','Juin',
						'Juillet','Aout','Septembre','Octobre','Novembre','Décembre'],
						monthNamesShort: ['Jan','Fev','Mar','Avr','Mai','Jun',
						'Jui','Аou','Sep','Oct','Nov','Dec'],
						dayNames: ['Dimanche','Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi'],
						dayNamesShort: ['Di','Lu','Ma','Me','Je','Ve','Sa'],
						dayNamesMin: ['D','L','M','M','J','V','S'],
						weekHeader: 'Semaine',
						dateFormat: 'dd/mm/yy',
						firstDay: 1,
						isRTL: false,
						showMonthAfterYear: false,
						yearSuffix: ''
					};
											
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
					$.datepicker.setDefaults($.datepicker.regional['fr']);		
				

					$( "#date_action_picker" ).timepicker({
						format: 'LT'
					});	
					$( "#date_action_picker_"+datePickerId ).timepicker({
						format: 'LT'
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
					<div class="fieldcontain ${hasErrors(bean: itineraryInstance, field: 'name', 'error')} ">
						<label for="name">
							<g:message code="itinerary.name.label" default="Name" />
						</label>
						<g:textField name="name" value="${itineraryInstance?.name}"/>
					</div>
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				</fieldset>
			</g:form>
		</div>
		
		
		<div id="changeDeliveryBoy">
		<fieldset class="form">
			<label for="deliveryBoy">
				<g:message code="itinerary.deliveryBoy.label" default="Delivery Boy" />
				<span class="required-indicator">*</span>
			</label>
			<g:if test="${itineraryInstance != null }">
				<g:select 
					id="deliveryBoy" 
					name="deliveryBoyId" 
					from="${employeeList}" 
					optionKey="id"
					optionValue="lastName" 
					value="${itineraryInstance.deliveryBoy}" 
					noSelection="${['':itineraryInstance?.deliveryBoy.lastName]}"
					onchange="${remoteFunction(action: 'changeDeliveryBoy',
                     			  update: [success: 'changeDeliveryBoy', failure: 'ohno'],
								   params:'\'itineraryInstanceId=' + itineraryInstance.id
									 + '&deliveryBoyId=\' + this.value')}"
					class="many-to-one"/>	
			</g:if>	
			<g:else>
				<g:select 
				id="deliveryBoy" 
				name="deliveryBoyId" 
				from="${employeeList}" 
				optionKey="id"
				optionValue="lastName" 
				value="${deliveryBoy}" 
				noSelection="${['':'']}"
				class="many-to-one"/>	
			</g:else>
			</fieldset>
			<!--
			<g:message code="itinerary.delivery.boy.filter" default="Name"/>
			<g:if test="${checked}">
				<input id="checkBox" type="checkbox"  checked="checked"
					onclick="${
						remoteFunction(controller:'employee', 
						action:'expandList',
						update:'changeDeliveryBoy',
						onLoading:"document.getElementById('spinner').style.display = 'inline';",
						onComplete:"document.getElementById('spinner').style.display = 'none';",
						params:'   \'&value=\' + this.checked  '
						)}">
			</g:if>
			<g:else>
				<input id="checkBox" type="checkbox" 
					onclick="${
						remoteFunction(controller:'employee', 
						action:'expandList',
						update:'changeDeliveryBoy',
						onLoading:"document.getElementById('spinner').style.display = 'inline';",
						onComplete:"document.getElementById('spinner').style.display = 'none';",
						params:'   \'&value=\' + this.checked  '
						)}">
			</g:else>
			-->
		</div>
		
		<BR>
		
		<BR>
		
		<g:form url="[resource:itineraryInstance, controller:'itinerary' ,action:'addTheoriticalAction']" method="POST">
			<g:theoriticalActionForm/>
			<div id="theoriticalActionTableDiv">
				<g:theoriticalActionTable/>
			</div>
			<g:hiddenField name="itineraryId" value="${itineraryInstance?.id}" />			
		</g:form>
	</body>
</html>
