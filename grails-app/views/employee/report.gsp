<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>
<%@ page import="pointeuse.Reason"%>

<head>
	<g:javascript library="application"/> 		
	<resource:tooltip />
	<r:require module="report"/>
	<r:layoutResources/>
	<meta name="layout" content="main">
	<g:set var="weeklyRecap" value="0" />
	<title>${message(code: 'employee.report.label', default: 'Report')}</title>
	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">

<style>
	 th
	{
	    font:15px Georgia, serif;
	
		color: rgb(102, 102, 102);
	    border-style:solid;
	    border-width:1px;
	    border-color:#E7EFE0;
	    font-weight:bold;
	    padding:0px;
	    text-align:center;
	    vertical-align:top;
	}
	
	th:last-child {
	      border-color:#E7EFE0;
	}
	 
	tr
	{
	    color:#000000;
	    border-top-style: solid;
	    border-bottom-style: solid;
	    border-width:1px;
	    border-color:#E7EFE0;
	    font-weight:normal;
	}
	
	td
	{
	    border-style:solid;
	    border-width:1px;
	    border-color:#000000;
	    padding:0px;
	    text-align:left;
	    vertical-align:top;
	}

</style>

	<script type="text/javascript">

	function closePopup ( ){
		window.location = $('#closeId').attr('href');
	}
	
	 $(document).ready(function() {
	   $('#cartouche_toggle').click( function() {
	    $('#cartouche_div').slideToggle(400);
	   });

	});
	
	function datePickerLaunch (){
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
				$( "#date_picker" ).datetimepicker({
					defaultDate:new Date(${period.getAt(Calendar.YEAR)},${period.getAt(Calendar.MONTH)},1)
				});					
	}
	</script>
</head>

<body>
	<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}" alt="Patientez pendant le traitement de la requète..." width="16" height="16" />Patientez pendant le traitement de la requête...</div>
	<a href="#list-employee" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>
	<div class="nav" id="nav" style="font-family: Verdana,Arial,sans-serif;font-size: 14.4px;">
		<ul>
			<g:form method="post" controller="employee" params="[siteId:siteId]">
				<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" /></a></li>
				<li style="vertical-align: middle;"><g:link class="list" action="list" params="${[isAdmin:isAdmin,siteId:siteId,back:true]}">${message(code: 'employee.back.to.list.label', default: 'List')}</g:link></li>
				<li style="vertical-align: middle;">
					${message(code: 'default.period.label', default: 'List')}: <g:datePicker
						name="myDate" value="${period ? period : new Date()}" relativeYears="[-3..5]"
						precision="month" noSelection="['':'-Choose-']"
						style="vertical-align: middle;" /> 
				</li>
				<li style="vertical-align: middle;"><g:actionSubmit value="afficher" action="report" class="listButton" /></li>	
				<li style="vertical-align: middle;"><g:actionSubmitImage value="pdf" action="userPDF" controller="employee"  src="${resource(dir: 'images', file: 'filetype_pdf.png')}" class="imageButton"/></li>
							
				<li>
					<g:inAndOutPopup/>
				</li>
			</g:form>
			<li>
				<a id="legend" title="
				<table>
					<tr><td ><g:message code='legend.NORMAL_EVENT' default='Régul' /></td></tr>
					<tr><td style='color : red;font-weight: bold;'><g:message code='legend.INITIALE_SALARIE' default='Régul' /></td></tr>
					<tr><td style='color : orange;font-weight: bold;'><g:message code='legend.MODIFIEE_SALARIE' default='Régul' /></td></tr>
					<tr><td style='color : blue;font-weight: bold;'><g:message code='legend.INITIALE_ADMIN' default='Régul' /></td></tr>
					<tr><td style='color : green;font-weight: bold;'><g:message code='legend.MODIFIEE_ADMIN' default='Régul' /></td></tr>
					<tr><td style='font-weight: bold;'><g:message code='legend.SYSTEM_GENERATED' default='Régul' /></td></tr>
					</table>">Légende</a> <richui:tooltip id="legend" />
			</li>		
		</ul>
		<g:if test="${flash.message}"><div class="message">${flash.message}</div></g:if>
	</div>
	<div id='cartouche_input_image'>
		<button type='button' id="cartouche_toggle" ><img alt="toggle" src="${grailsApplication.config.context}/images/glyphicons_190_circle_plus.png"></button>
		Récapitulatifs mensuels et annuels
	</div>
	<div id="cartouche_div">
		<g:cartouche />
	</div> 
	<BR>
		
	<div id="report_table_div">
		<g:reportTable />
	</div>
</body>
