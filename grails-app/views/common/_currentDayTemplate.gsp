<%@ page import="pointeuse.Reason" %>

<h1>
	<g:formatDate format="E dd MMM yyyy'" date="${Calendar.instance.time}" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span
		id="clock"><g:formatDate format="HH:mm:ss" date="${new Date()}" /></span>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<g:message code="employee.daily.time" default="Last Name" />
	:
	${humanTime.get(0)}H${humanTime.get(1)}
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<g:message code="employee.sup.time" default="Last Name" />
	:
	${dailySupp.get(0)}H${dailySupp.get(1)}
</h1>
<g:if test="${flash.message}">

	<div class="message" role="status">
		${flash.message}
	</div>
</g:if>

<g:if test="${inAndOuts==null || inAndOuts.size()}">
	<table border="1">
		<thead>
			<g:each in="${inAndOuts}" var="inAndOut">
				<th>
					${inAndOut.type}
				</th>
			</g:each>
		</thead>
		<tbody>
			<tr>
				<g:each in="${inAndOuts}" var="inAndOut">
					<g:if
						test="${inAndOut.regularization|| inAndOut.systemGenerated==true}">
						<td bgcolor="#cccccc"><font color="red"><g:formatDate
									format="H:mm:s'" date="${inAndOut.time}" /></font></td>
					</g:if>
					<g:else>
						<g:if test="${inAndOut.type.equals('E')}">
							<td bgcolor="98FB98"><g:formatDate format="H:mm:s'"
									date="${inAndOut.time}" /></td>
						</g:if>
						<g:else>
							<td bgcolor="#FFC0CB"><g:formatDate format="H:mm:s'"
									date="${inAndOut.time}" /></td>
						</g:else>
					</g:else>
				</g:each>
			</tr>
		</tbody>
	</table>
</g:if>
<g:else>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Pas d'évenement pour le jour en cours <BR>
	<BR>
</g:else>
	
	<g:hiddenField name="userId" value="${employee?.id}" />
	<g:if test="${entranceStatus}">
			<g:hiddenField name="type" value="S" />
			<%entryName='Sortir'%>
	</g:if>
	<g:else>
		<g:hiddenField name="type" value="E" />
		<%entryName='Entrer'%>
	</g:else>

	<g:hiddenField name="userId" value="${employee?.id}" />
	<g:if test="${entranceStatus}">
			<g:hiddenField name="type" value="S" />
			<%entryName='Sortir'%>
	</g:if>
	<g:else>
		<g:hiddenField name="type" value="E" />
		<%entryName='Entrer'%>
	</g:else>
	<table border="0">
		<tr>
			<td>
				<g:if test="${entranceStatus}">
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="exitbutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:if> 
				<g:else>
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="entrybutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:else>
			</td>

			<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
			<td><g:link class="modalbox" controller="employee"
					action="reportLight" params="[userId:employee.id]">rapport</g:link>
			</td>
			<td><g:link class="modalbox" url="/">
					${message(code: 'employee.disconnect.label', default: 'Sortie')}
				</g:link></td>
			<td></td>
			<td>				<div>
						<div>
							<a href="#join_form" id="join_pop">Ajouter un élement</a>
						</div>

					</div> <a href="#x" class="overlay" id="join_form"></a>
					<div class="popup">
						<h2>Creer Entrée/Sortie</h2>
						<p>Renseignez les informations pour creer un nouvel évènement</p>
							<table>
								<tbody>
									<tr class="prop">
										<td class="name" valign="top">choisissez la date:</td>
										<td class="value" valign="top"><input type="text"
											name="date_picker" id="date_picker" /> <script>
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
							$("#date_picker").datetimepicker();</script></td>
									</tr>
									<tr class="prop">
										<td class="name" valign="top">Evènement:</td>
										<td class="value" valign="top"><g:select
												name="event.type" from="${['E','S']}"
												valueMessagePrefix="entry.name"
												noSelection="['':'-Choisissez votre élément-']" /></td>
									</tr>


									<tr class="prop">
										<td class="name" valign="top">Raison:</td>
										<td class="value" valign="top"><g:select name="reason.id"
												from="${Reason.list([sort:'name'])}"
												noSelection="['':'-Ajouter une raison-']" optionKey="id"
												optionValue="name" /></td>
										</tr>


								</tbody>
							</table>

							
							<g:submitToRemote oncomplete="showSpinner(false)"
								onloading="showSpinner(true)" update="c"
								url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
						<a class="close" href="#close"></a>
					</div>
				
		</td>
		</tr>
	</table>
