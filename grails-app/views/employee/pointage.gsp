 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Reason"%>
<%@ page import="java.util.Calendar"%>

<html>
	<head>
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">
		<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<script src="${grailsApplication.config.context}/js/jquery-ui-timepicker-addon.js"></script>
		<meta name="layout" content="main" />
		<title><g:message code="daily.pointeuse.label" /></title>
		
		<g:set var="calendar" value="${Calendar.instance}"/>
		
		<script type="text/javascript">

			function closePopup ( ){
				window.location = $('#closeId').attr('href');
			}
		
	
			function startTime(){
				var today=new Date();
				var h=today.getHours();
				var m=today.getMinutes();
				var s=today.getSeconds();
				// add a zero in front of numbers<10
				m=checkTime(m);
				s=checkTime(s);
				document.getElementById('clock').innerHTML=h+":"+m+":"+s;
				t=setTimeout(function(){startTime()},500);
			}
			
			function checkTime(i){
				if (i<10){
			  		i="0" + i;
			  	}
				return i;
			}

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

						$( "#date_mileage_picker" ).datetimepicker({
							defaultDate:new Date(${calendar.time.getAt(Calendar.YEAR)},${calendar.time.getAt(Calendar.MONTH)},1)
						});				
			}
			
	
			var t;
			window.onload=resetTimer;
			document.onmousemove=resetTimer;
			function logout()
			{
				location.href='${grailsApplication.config.serverURL}/${grailsApplication.config.context}' 
			}
			function resetTimer(){
				startTime();
				clearTimeout(t);
				t=setTimeout(logout,60000) //logs out in 1 min
			}
		</script>
	</head>
	<body>
		<g:javascript>window.onload = startTime();</g:javascript>
		<g:form>	
			<div id='title' style='padding:10px;'>
				<h1>
					<font size="5"> 
						<g:message code="employee.label" default="Last Name" />: 
						<g:fieldValue bean="${employee}" field="firstName" /> 
						<g:fieldValue bean="${employee}" field="lastName" />	
					</font>
				</h1>			
			</div>
			<div id="last5days" style='padding:10px;'>
				<g:last5days />
			</div>					
			<g:hiddenField name="userId" value="${userId}" />
			<g:hiddenField name="fromReport" value="${false}" />
		</g:form>
	</body>
</html>