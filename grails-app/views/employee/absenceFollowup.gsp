<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Vacation"%>
<%@ page import="pointeuse.Period"%>

<!doctype html>
<html>
<head>
	<g:javascript src="moment.js" />
	
	<g:javascript library="application"/> 		
	<r:require module="report"/>		
	<r:layoutResources/>		
  	<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
	<link href="${grailsApplication.config.context}/css/timeslider.css" rel="stylesheet">

	<script src="${grailsApplication.config.context}/js/timeslider.js"></script>
  	
	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />	
	<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
	<title><g:message code="absence.followup" /></title>
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
		<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
		
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
		<script>
		     google.charts.load('current', {'packages':['timeline']});

     	</script>
		
</head>
	<body>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>
		<div class="nav" id="nav">
			<g:headerMenu />
		</div>
		<div id="list-employee" class="standardNav">
			<h1>
				<g:message code="vacation.followup" /> <g:if test="${site}">pour le site ${site.name}</g:if>
			
				<br>
				<br>
				<g:form method="POST" url="[controller:'employee', action:'absenceFollowup']">
					<ul>
						<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
						<li>
							<g:if test="${siteId != null && !siteId.equals('')}">
								<g:select name="site.id" from="${Site.list([sort:'name'])}"
									noSelection="${['':site.name]}" optionKey="id" optionValue="name"
									style="vertical-align: middle;" />
							</g:if>
							<g:else>
								<g:select name="site.id" from="${Site.list([sort:'name'])}"
									noSelection="${['':(site?site.name:'-')]}" optionKey="id" optionValue="name"
									style="vertical-align: middle;" />						
							</g:else>				
						</li>
						
						<li>
							<input type="text" id="date_picker" name="date_picker" />
						</li>
						<li>	
							<g:submitToRemote class="displayButton"
								value="${message(code:'absence.report.daily.view')}"
								update="absenceList" 
								onLoading="document.getElementById('spinner').style.display = 'inline';"
				                onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'employee', action:'absenceFollowup',id:'dailyView']"
							/>	
						</li>		
						<li>
							<g:submitToRemote class="displayButton"
								value="${message(code:'absence.report.monthly.view')}"
								update="absenceList" 
								onLoading="document.getElementById('spinner').style.display = 'inline';"
				                onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'employee', action:'absenceFollowup',id:'monthlyView']"
							/>	
						</li>		
						<li>
							<g:actionSubmit id='yearlyView' class='excelButton' value="${message(code:'absence.report.annual.view')}"  action="vacationFollowAnnualExcel"/>	

						</li>			
	
						<g:hiddenField name="isAdmin" value="${isAdmin}" />
						<g:if test="${period!=null}">	
							<g:hiddenField name="year" value="${period.id}" />	
						</g:if>
					</ul>	
				</g:form>
			</h1>
			<g:if test="${flash.message}">
				<div class="message" id="flash">
					${flash.message}
				</div>
			</g:if>
		</div>
		<br>
		<div id="absenceList">
			<g:listAbsences/>
		</div>
		<g:if test="${employeeInstanceTotal!=null}">
			<div class="pagination" id="pagination">
				<g:hiddenField name="isAdmin" value="${isAdmin}" />
				<g:paginate total="${employeeInstanceTotal}" params="${[isAdmin:isAdmin,siteId:siteId]}" />
			</div>
		</g:if>
	</body>
</html>