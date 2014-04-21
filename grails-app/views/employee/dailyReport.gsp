<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<!doctype html>
<html>
<head>
	<g:javascript library="application"/> 		
	<r:require module="report"/>		<r:layoutResources/>		
	
  	<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">

	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />
	
	<g:set var="entityName"
		value="${message(code: 'employee.label', default: 'Employee')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
		<style type="text/css">
			body {
				font-family: Verdana, Arial, sans-serif;
				font-size: 0.9em;
			}
			table {
				border-collapse: collapse;
			}
			thead {
				background-color: #DDD;
			}
			td {
				padding: 2px 4px 2px 4px;
			}
			th {
				padding: 2px 4px 2px 4px;
			}

		</style>
		
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
		</script>
		
</head>
<body>

	<div class="nav" id="nav">
		<g:headerMenu />
	</div>
	<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}" alt="Patientez pendant le traitement de la requète..." width="16" height="16" />Patientez pendant le traitement de la requête...</div>
	
	<div id="list-employee" class="nav">
		<h1>
			<g:message code="daily.recap.label"/>
			<br>
			<g:if test="${site!=null}"><g:message code="site.label"/>:${site.name}</g:if>
			<br>

			<g:form method="POST" url="[controller:'employee', action:'pdf']">
				<ul>
				
				<li>	<g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /> </li>
				<li>	<g:if test="${siteId != null && !siteId.equals('')}">
						<g:select name="site.id" from="${Site.list([sort:'name'])}"
							noSelection="${['':site.name]}" optionKey="id" optionValue="name"
							style="vertical-align: middle;" />
					</g:if>
					<g:else>
						<g:select name="site.id" from="${Site.list([sort:'name'])}"
							noSelection="${['':'-']}" optionKey="id" optionValue="name"/>
					</g:else>	
				</li>
				<li>	<input type="text" id="date_picker" name="date_picker" /></li>
				<li>	
					<g:submitToRemote class="displayButton"
						value="Rapport"
						update="dailyTable" 
						onLoading="document.getElementById('spinner').style.display = 'inline';"
		                onComplete="document.getElementById('spinner').style.display = 'none';"
						url="[controller:'employee', action:'dailyReport']"
					/>	
				</li>		
				<li>	<g:actionSubmit value="PDF" action="dailyTotalPDF" controller="employee"  class="pdfButton"/>	</li>				
					<g:hiddenField name="isAdmin" value="${isAdmin}" />
					<g:hiddenField name="siteId" value="${siteId}" />
				</ul>
			</g:form>
			
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>	
	
	<div id="dailyTable">
		<g:listDailyTime/>
	</div>
</body>
</html>
