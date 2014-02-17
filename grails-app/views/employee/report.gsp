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
	 
	  // $('#report_table_toggle').click( function() {
	  //  $('#report_table_div').slideToggle(400);
	  // });

	   $('#cartouche_toggle').click( function() {
	    $('#cartouche_div').slideToggle(400);
	   });

	});
	
	
	function datePickerLaunch (){
	
	options = {
    pattern: 'mmm yyyy', // Default is 'mm/yyyy' and separator char is not mandatory
    selectedYear: 2014,
    startYear: 2008,
    finalYear: 2020,
    monthNames: ['Janvier', 'Fevrier', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Aout', 'September', 'Octobre', 'Novembre', 'Décembre']
};
	
	
	$('#month_picker').monthpicker(options);
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
					//altField: "#alt_example_1_alt"
					defaultDate:new Date(${period.getAt(Calendar.YEAR)},${period.getAt(Calendar.MONTH)},1)
				});					
	

	}
	
	</script>
</head>

<body>


<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}" alt="Patientez pendant le traitement de la requète..." width="16" height="16" />Patientez pendant le traitement de la requète...</div>

	<a href="#list-employee" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>
	<div id="list-employee" class="content scaffold-list">
		<div class="nav" role="navigation" id="nav">
			<ul>
				<g:form method="post" >
				<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" /></a></li>
				<li style="vertical-align: middle;"><g:link class="list" action="list" params="${[isAdmin:isAdmin,siteId:siteId,back:true]}">${message(code: 'employee.back.to.list.label', default: 'List')}</g:link></li>
				<li style="vertical-align: middle;">
					${message(code: 'default.period.label', default: 'List')}: <g:datePicker
						name="myDate" value="${period ? period : new Date()}" relativeYears="[-3..5]"
						precision="month" noSelection="['':'-Choose-']"
						style="vertical-align: middle;" /> <g:hiddenField name="userId"
						value="${userId}" /> <g:hiddenField name="siteId"
						value="${siteId}" /> <g:hiddenField name="currentMonth"
						value='${period ? period.format("M") : (new Date()).format("M")}' />
					<g:hiddenField name="currentYear" value='${period ? period.format("yyyy") : (new Date()).format("yyyy")}' />
				</li>
				<li style="vertical-align: middle;"><g:actionSubmit value="afficher" action="report" class="listButton" /></li>				
				<li style="vertical-align: middle;"><g:actionSubmitImage value="pdf" action="userPDF"  src="${resource(dir: 'images', file: 'filetype_pdf.png')}" class="imageButton"/></li>
			</g:form>
				<li>
					<a href="#join_form" id="join_pop" class="addTimeButton">Ajouter un élement</a>
					<a href="#x" class="overlay" id="join_form"></a>
					<div id="popup" class="popup">
						<h2>Creer Entrée/Sortie</h2>
						<p>Renseignez les informations pour creer un nouvel évènement</p>
						<g:form action="create">
							<table>
								<tbody>
									<tr class="prop">
										<td class="eventTD" valign="top">choisissez la date:</td>
										<td class="eventTD" valign="top"><input type="text" name="date_picker" id="date_picker" /> 
											<script type="text/javascript">
												datePickerLaunch();
											</script>
										</td>
									</tr>
									<tr class="prop">
										<td class="eventTD" valign="top">Evènement:</td>
										<td class="eventTD" valign="top">
											<g:select
												name="event.type" from="${['E','S']}"
												valueMessagePrefix="entry.name"
												noSelection="['':'-Choisissez votre élément-']" />
										</td>
									</tr>
									<tr class="prop">
										<td class="eventTD" valign="top">Raison:</td>
										<td class="eventTD" valign="top">
											<g:select name="reason.id"
												from="${Reason.list([sort:'name'])}"
												noSelection="['':'-Ajouter une raison-']" optionKey="id"
												optionValue="name" />
										</td>
									</tr>
								</tbody>
							</table>
							<g:hiddenField name="userId" value="${userId}" />
							<g:hiddenField name="fromReport" value="${true}" />
							<g:submitToRemote class="listButton"
								onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    		onComplete="document.getElementById('spinner').style.display = 'none';"						
								update="report_table_div"
								onSuccess="closePopup()"
								url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
						</g:form>
						<a class="close" id="closeId" href="#close"></a>
					</div>
				</li>

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
			<BR />
			<g:if test="${flash.message}">
				<div class="message">
					${flash.message}
				</div>
			</g:if>
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

	</div>
</body>
