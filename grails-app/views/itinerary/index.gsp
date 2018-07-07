
<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.Site" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'itinerary.label', default: 'Itinerary')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<g:javascript library="application"/> 		
 		<r:require module="report"/>		
		<r:layoutResources/>	
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
		<a href="#list-itinerary" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
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
								update="absenceList" 
								onLoading="document.getElementById('spinner').style.display = 'inline';"
				                onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'itinerary', action:'showItineraryActions',id:'dailyView']"
							/>	
						</li>		
						<li>
							<g:submitToRemote class="displayButton"
								value="${message(code:'absence.report.monthly.view')}"
								update="absenceList" 
								onLoading="document.getElementById('spinner').style.display = 'inline';"
				                onComplete="document.getElementById('spinner').style.display = 'none';"
								url="[controller:'itinerary', action:'showItineraryActions',id:'monthlyView']"
							/>	
						</li>		
					</ul>
			</g:form>
		</div>
		<div id="list-itinerary" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'itinerary.name.label', default: 'Name')}" />
						<th><g:message code="itinerary.creationUser.label" default="Creation User" /></th>
						<th><g:message code="itinerary.deliveryBoy.label" default="Delivery Boy" /></th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${itineraryInstanceList}" status="i" var="itineraryInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">					
						<td><g:link action="show" id="${itineraryInstance.id}">${fieldValue(bean: itineraryInstance, field: "name")}</g:link></td>
						<td>${itineraryInstance.creationUser.lastName} ${itineraryInstance.creationUser.firstName}</td>				
						<td>${itineraryInstance.deliveryBoy.lastName} ${itineraryInstance.deliveryBoy.firstName}</td>							
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${itineraryInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
