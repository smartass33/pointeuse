<%@ page import="java.util.Calendar"%>
<%@ page import="pointeuse.Itinerary" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'itinerary.label', default: 'Itinerary')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<g:javascript library="application"/> 		
 		<r:require module="report"/>		
		<r:layoutResources/>	
		<g:set var="calendar" value="${Calendar.instance}"/>
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
			   $("#date_picker").datepicker({dateFormat: "dd/mm/yy"}).datepicker("setDate", "${currentDate}");
			});
			
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
					

						$( "#date_in_action_picker_"+datePickerId ).timepicker({
							format: 'LT'
						});	
						$( "#date_out_action_picker_"+datePickerId ).timepicker({
							format: 'LT'
						});		
			
			}

			function dateFirstPickerLaunch (datePickerId,hour,minute){
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

						$( "#date_action_picker_"+datePickerId ).timepicker();	
			}

		</script>
	</head>
	<body>
		<a href="#show-itinerary" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>
		
		<div id="actions" class="standardNav">
			<g:form method="POST" url="[controller:'action', action:'showItineraryActions']">
			<ul>
						<li><g:message code="itinerary.label" default="Search" style="vertical-align: middle;" /></li>
						<li>
							<g:if test="${itineraryId != null && !itineraryId.equals('')}">
								<g:select name="itineraryId" from="${Itinerary.list([sort:'name'])}"
									noSelection="${['':itinerary.name]}" optionKey="id" optionValue="name"
									style="vertical-align: middle;" />
							</g:if>
							<g:else>
								<g:select name="itineraryId" from="${Itinerary.list([sort:'name'])}"
									noSelection="${['':(itinerary?itinerary.name:'-')]}" optionKey="id" optionValue="name"
									style="vertical-align: middle;" />						
							</g:else>				
						</li>
						<li>
							<input type="text" id="date_picker" name="date_picker" />
						</li>
						<li>	
							<g:submitToRemote class="displayButton"
								value="${message(code:'absence.report.daily.view')}"
								update="itineraryReportTemplate" 
								onLoading="document.getElementById('spinner').style.display = 'inline';"
				                onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'itinerary', action:'showItineraryActions',id:'dailyView']"
							/>	
						</li>		
						<li>
							<g:submitToRemote class="displayButton"
								value="${message(code:'absence.report.monthly.view')}"
								update="itineraryReportTemplate" 
								onLoading="document.getElementById('spinner').style.display = 'inline';"
				                onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'itinerary', action:'showItineraryActions',id:'monthlyView']"
							/>	
						</li>		
					</ul>
			</g:form>
		</div>
		<div id="itineraryReportTemplate">
			<g:itineraryReportTemplate/>
		</div>
	</body>
</html>
