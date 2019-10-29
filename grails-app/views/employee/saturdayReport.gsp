<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
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
				
				
				th.rotate {
				  /* Something you can count on */
				  height: 90px;
				  white-space: nowrap;
				}

				th.rotate > div {
				  transform: 
				    /* Magic Numbers */
				    translate(25px, 51px)
				    /* 45 is really 360 - 45 */
				    rotate(270deg);
				 	 width: 15px;
				}
				th.rotate > div > span {
				  border-bottom: 1px solid #ccc;
				  padding: 0px 0px;
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
				   $("#from_date_picker").datepicker({dateFormat: "dd/mm/yy"}).datepicker("setDate", "${currentDate}");
				});

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
					   $("#to_date_picker").datepicker({dateFormat: "dd/mm/yy"}).datepicker("setDate", "${currentDate}");
					});
				
			</script>
			<script>
			     google.charts.load('current', {'packages':['timeline']});
	     	</script>	
	</head>
	<body>
		<div class="nav" id="nav">
			<g:headerMenu />
		</div>	
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>	
		<div id="daily-list" class="standardNav">
			<h1>
				<g:message code="saturday.recap.label"/>
				<br>
				<g:if test="${site!=null}"><g:message code="site.label" style="vertical-align: middle;"/>:${site.name}</g:if>
				<br>
				<g:form method="POST" url="[controller:'employee', action:'saturdayTotalPDF']">
					<ul>	
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /> </li>
					<li>	
						<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':site.name]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':'-']}" optionKey="id" optionValue="name"/>
						</g:else>	
					</li>
					<li><g:message code="saturday.from.date.label" style="vertical-align: middle;" />: <input type="text" id="from_date_picker" name="from_date_picker" /></li>
					<li><g:message code="saturday.to.date.label" style="vertical-align: middle;" />: <input type="text" id="to_date_picker" name="to_date_picker" /></li>
					
					<li>	
						<g:submitToRemote class="displayButton"
							value="Rapport"
							update="saturdayTime" 
							onLoading="document.getElementById('spinner').style.display = 'inline';"
			                onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'employee', action:'saturdayReport']"
						/>	
					</li>	
					<li>
						<g:actionSubmit class='excelButton' value="export"  action="saturdayExcelExport"/>	
					</li>								
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
		<div id="saturdayTime">
			<g:listSaturdayTime/>
		</div>

	</body>
</html>
