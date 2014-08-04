<%@ page import="pointeuse.Reason" %>

<h1>
	<g:formatDate format="E dd MMM yyyy'" date="${Calendar.instance.time}" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span id='clock'><g:formatDate format="HH:mm:ss" date="${new Date()}"/></span>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<g:message code="employee.daily.time" default="Last Name" />
	:
	${humanTime}
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<g:message code="employee.sup.time" default="Last Name" />
	:
	${dailySupp}
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

	
	<BR>
	<div class="standardNav">
		<ul>
			<li>				
				<g:if test="${entranceStatus}">
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="exitbutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:if> 
				<g:else>
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="entrybutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:else>
			</li>
			<li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</li>
			<li >
				<g:link class="displayButton" controller="employee" action="reportLight" params="[userId:employee.id]">${message(code: 'employee.monthly.report.label', default: 'Report')}</g:link>
			</li>
			<li >
				<a href="#join_form" id="join_pop" class="addElementButton">Ajouter un élement</a>
				<a href="#x" class="overlay" id="join_form" style="background: transparent;"></a>
				<div id="popup" class="popup">
					<h2>Creer Entrée/Sortie</h2>
					<p>Renseignez les informations pour créer un nouvel évènement</p>
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
						<g:hiddenField name="fromReport" value="${fromReport}" />
						<g:submitToRemote class="listButton"
							onLoading="document.getElementById('spinner').style.display = 'inline';"
				                		onComplete="document.getElementById('spinner').style.display = 'none';"						
							update="currentDay"
							onSuccess="closePopup()"
							url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
					</g:form>
					<a class="close" id="closeId" href="#close"></a>
				</div>

			</li>
			<li>
				<g:link class="logoutButton"  url="${grailsApplication.config.serverURL}/${grailsApplication.config.context}">${message(code: 'employee.disconnect.label', default: 'Sortie')}</g:link>
			</li>			
		</ul>
	</div>