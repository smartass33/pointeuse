<%@ page import="java.util.Calendar"%>
<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.ItineraryNature" %>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'itinerary.label', default: 'Itinerary')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<g:javascript library="application"/> 		
		<resource:tooltip />
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

			function timePickerLaunch (datePickerId,type){										
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
	
				$.timepicker.setDefaults($.timepicker.regional['fr']);
				$.datepicker.setDefaults($.datepicker.regional['fr']);		
	
				if (type == 'time'){
					$( "#"+datePickerId ).timepicker({});		
				}
				if (type == 'date'){
					$( "#"+datePickerId ).datetimepicker({});		
				}
			}
		</script>
		
		
		
	<style type="text/css">
         div#tooltip a span {
         	display: none;
         }
         div#tooltip a:hover span {
         	display: block;
            	position: relative; width: 125px;
            	padding: 5px; margin: 10px; z-index: 100;
            	color: black; background-color:#FFFFCC; border: 1px solid #ccc;
           	font: 10px Verdana, sans-serif; text-align: center;
          }
         div#tooltip a {
           	position:relative;
         }
         div#tooltip a span {
          display:none;
          border: 0px;
         }
         div#tooltip a:hover span {
          display:block;
          position:absolute; width: 100px;
          color: black; background-color:#FFFFCC; border: 0px solid #ccc;
          font: 10px Verdana, sans-serif; text-align: center;
         }
         div#tooltip a:hover {
         	text-indent:0;
         }
         #tooltip button {
         	border:0em;
         	background-color:#FFFFFF;
         }
}

	            
	            
	            
	</style>
	</head>
	
	<g:set var="myYellow" 			value="#fefb00"/>
	<g:set var="myOrange" 			value="#74F3FE"/>
	<g:set var="myRed" 				value="#FF4500"/>
	<body>
		<a href="#show-itinerary" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.feminine.label" args="[entityName]" /></g:link></li>
				<li>
					<a  class='itinereraryLegendTable' id="legend" title="
					<table  id='itinereraryLegendTable'>
						<tr><td style='color : ${myYellow};font-weight: bold;'><g:message code='itinerary.sup.15' default='Régul' /></td></tr>
						<tr><td style='color : ${myOrange};font-weight: bold;'><g:message code='itinerary.sup.30' default='Régul' /></td></tr>
						<tr><td style='color : ${myRed}   ;font-weight: bold;'><g:message code='itinerary.sup.60' default='Régul' /></td></tr>						
						</table>"><g:message code='legend.label' default='Régul' /></a> <richui:tooltip id="legend" />
				</li>
			</ul>
		</div>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>
		<div id="actions" class="standardNav">		
			<ul>
				<g:form method="POST">
					<li><g:message code="employee.site.label" default="Search" style="vertical-align: middle;" /></li>
					<li>
						<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="siteId" from="${Site.list([sort:'name'])}"
								noSelection="${['':itinerary.name]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="siteId" from="${Site.list([sort:'name'])}"
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
							update="itinerarySiteReportTemplate" 
							onLoading="document.getElementById('spinner').style.display = 'inline';"
			                onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'itinerary', action:'showItineraryActions',id:'dailyViewBySite']"/>	
					</li>		
					<li>
						<g:submitToRemote class="displayButton"
							value="${message(code:'absence.report.monthly.view')}"
							update="itinerarySiteReportTemplate" 
							onLoading="document.getElementById('spinner').style.display = 'inline';"
			                onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'itinerary', action:'showItineraryActions',id:'monthlyViewBySite']"/>	
					</li> 	
					<li>
						<g:actionSubmit value="PDF" action="itineraryPDF" class="pdfButton" />
						<g:hiddenField name="id" value="monthlyViewBySite"/>

					</li>
				</g:form>
			</ul>
		</div>
		<div id="itinerarySiteReportTemplate">
			<g:itinerarySiteReportTemplate/>
		</div>
	</body>
</html>
