
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
				<li><g:link class="create" action="create"><g:message code="default.feminine.new.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" action="itinerarySiteView"><g:message code="itinerary.site.report" args="[entityName]" /></g:link></li>
			</ul>
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
						<g:sortableColumn property="description" title="${message(code: 'itinerary.description.label', default: 'Name')}" />
						<th><g:message code="itinerary.creationUser.label" default="Creation User" /></th>
						<th><g:message code="itinerary.deliveryBoy.label" default="Delivery Boy" /></th>
						<th>action<th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${itineraryInstanceList}" status="i" var="itineraryInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">					
						<td><g:link action="show" id="${itineraryInstance.id}">${fieldValue(bean: itineraryInstance, field: "name")}</g:link></td>
						<td><g:link action="show" id="${itineraryInstance.id}">${fieldValue(bean: itineraryInstance, field: "description")}</g:link></td>
						<td>${itineraryInstance.creationUser.lastName} ${itineraryInstance.creationUser.firstName}</td>				
						<td>${itineraryInstance.deliveryBoy.lastName} ${itineraryInstance.deliveryBoy.firstName}</td>	
						<td><g:link class="create" action="itineraryReport">Obtenir le rapport</g:link></td>						
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
