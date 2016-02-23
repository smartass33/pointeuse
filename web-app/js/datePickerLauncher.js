(function ($) {
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
				});					
	}

})(jQuery);