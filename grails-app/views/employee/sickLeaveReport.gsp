<%@ page import="pointeuse.Period"%>
<%@ page import="pointeuse.Mileage" %>
<%@ page import="pointeuse.Site" %>
<!DOCTYPE html>
<html>
	<head>
		<g:javascript library="application"/> 		
		<r:require module="report"/>
		<r:layoutResources/>	
		<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />	
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		
		
		<script type="text/javascript">

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
		<a href="#list-milage" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li style="vertical-align: middle;"><g:link class="list" action="report" params="${[isAdmin:isAdmin,siteId:siteId,back:true,userId:employeeId]}">${message(code: 'employee.monthly.report.back.label', default: 'List')}</g:link>
				</li>
			</ul>
		</div>
		<div id="sickLeaveTableDiv">
			<g:sickLeave/>	
		</div>
	</body>
</html>
