<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.ItineraryNature" %>
<%@ page import="pointeuse.Site" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>
<g:set var="outActionItem"/>

<g:if test="${viewType.equals('dailyView')}">
	<h1>${message(code: 'itinerary.real.label', default: 'Report')}</h1>
	<BR>
		<a href="#itinerary_action_form_daily" id="itinerary_action_pop_daily" class="addElementButton">${message(code: 'action.create.button.tiny', default: 'Report')}</a>
		<a href="#x" class="overlay" id="itinerary_action_form_daily" style="background: transparent;"></a>
		<div id="itinerary_action_popup_daily" class="popup">
			<h2>${message(code: 'action.create.button', default: 'Report')}</h2>
			<p>${message(code: 'action.create.info', default: 'Report')}</p>
			<g:form action="create">
				<table>
					<tbody>
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'itinerary.label', default: 'Report')}:</td>
							<td>
								<g:select name="itineraryId"
						          from="${Itinerary.list([sort:'name'])}"
						          noSelection="${['':'-']}"          
						          optionKey="id" optionValue="${{it.description != null ? it.name+' '+it.description : it.name}}"
						          />
							</td>	
						</tr>
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'employee.site.label', default: 'Report')}:</td>
							<td>
								<g:select name="siteId"
						          from="${Site.list([sort:'name'])}"
						          noSelection="${['':'-']}"          
						          optionKey="id" optionValue="name"
						          />					
							</td>	
						</tr>
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'action.nature.label', default: 'Report')}:</td>
							<td>
								<g:select
									name="action.type" from="${['DEP','ARR']}"
									valueMessagePrefix="action.name"
									noSelection="['':'-Choisissez votre élément-']" />																	
							</td>	
						</tr>
						<tr>
							<td class="eventTD" valign="top">${message(code: 'action.time', default: 'Report')}:</td>
							<td><input type="text" name="date_action_picker_daily" id="date_action_picker_daily" value="${null}"/> </td>
							<script type="text/javascript">
								timePickerLaunch ("date_action_picker_daily","date");
							</script>	
						</tr>
					</tbody>
				</table>
				<g:hiddenField name="fromReport" value="${fromReport}" />
				<g:hiddenField name="viewType" value="${viewType}" />				
				<g:submitToRemote class="listButton"
	                  	onLoading="document.getElementById('spinner').style.display = 'inline';"
	                  	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
					update="itineraryReportTemplate"
					onSuccess="closePopup()"
					url="[controller:'action', action:'regularizeAction',id:viewType]" value="${message(code: 'mileage.create.validation', default: 'Report')}">
				</g:submitToRemote>
			</g:form>
			<a class="close" id="closeId" href="#close"></a>
		</div>
	<g:if test="${actionsList == null || actionsList.size() == 0}">
		${message(code: 'action.list.empty', default: 'Report')}
	</g:if>
	<g:else>
		<table style="table-layout:fixed;">
			<tbody>
				<tr><td class="itineraryReportTD">${actionsList.get(0).date.format('dd/MM/yyyy')}</td>
					<g:each in="${actionsList}" var='actionItem' status="j">
							<td class="itineraryReportTD" >${actionItem.site.name}<br>	
								<a href="#itinerary_action_form_${j}" id="itinerary_action_pop_${j}">		
								<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
									<font color="red">
								</g:if>
								<g:else>
									<font color="green">
								</g:else>
								${actionItem.date.format('kk:mm')}</font></a>
								<a href="#x" class="overlay" id="itinerary_action_form_${j}" style="background: transparent;"></a>
								<div id="itinerary_action_popup_${j}" class="popup">
									<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
									<p>${message(code: 'action.create.info', default: 'Report')}</p>
									<g:form action="create">	
										<input type="text" name="date_action_picker_${j}" id="date_action_picker_${j}" value="${actionItem.date.format('kk:mm')}"/>
										<script type="text/javascript">
											timePickerLaunch ("date_action_picker_${j}","date");						
										</script>	
											<BR>	
										${message(code: 'inAndOut.create.event', default: 'Report')}:				
										<g:select
											name="actionType" from="${['DEP','ARR']}"
											valueMessagePrefix="itinerary.name"
											noSelection="['':'-Choisissez votre élément-']" />
										<g:submitToRemote class="listButton"
					                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
					                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
											update="itineraryReportTemplate"
											onSuccess="closePopup()"
											url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
										</g:submitToRemote>
										<BR>
										<g:submitToRemote class="trash"
					                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
					                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
											update="itineraryReportTemplate"
											onSuccess="closePopup()"
											url="[controller:'action', action:'trash']" value="${message(code: 'action.delete.label', default: 'Report')}">
										</g:submitToRemote>
										<g:hiddenField name="viewType" value="${viewType}" />
										<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${j}" value="${itineraryInstance.id}" /></g:if>
										<g:if test="${actionItem != null}"><g:hiddenField name="ActionItemId_${j}" value="${actionItem.id}" /></g:if>
									</g:form>
									<a class="close" id="closeId" href="#close"></a>
								</div>
							</td>
					</g:each>
				</tr>
			</tbody>
		</table>
	</g:else>
</g:if>
<g:if test="${viewType.equals('monthlyView')}">
	<h1>${message(code: 'itinerary.real.label', default: 'Report')}</h1>
	<BR>
	<a href="#itinerary_action_form_monthly" id="itinerary_action_pop_monthly" class="addElementButton">${message(code: 'action.create.button.tiny', default: 'Report')}</a>
		<a href="#x" class="overlay" id="itinerary_action_form_monthly" style="background: transparent;"></a>
		<div id="itinerary_action_popup_monthly" class="popup">
			<h2>${message(code: 'action.create.button', default: 'Report')}</h2>
			<p>${message(code: 'action.create.info', default: 'Report')}</p>
			<g:form action="create">
				<table>
					<tbody>
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'itinerary.label', default: 'Report')}:</td>
							<td>
								<g:select name="itineraryId"
						          from="${Itinerary.list([sort:'name'])}"
						          noSelection="${['':'-']}"          
						          optionKey="id" optionValue="${{it.description != null ? it.name+' '+it.description : it.name}}"
						          />
							</td>	
						</tr>
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'employee.site.label', default: 'Report')}:</td>
							<td>
								<g:select name="siteId"
						          from="${Site.list([sort:'name'])}"
						          noSelection="${['':'-']}"          
						          optionKey="id" optionValue="name"
						          />					
							</td>	
						</tr>
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'action.nature.label', default: 'Report')}:</td>
							<td>
								<g:select
									name="action.type" from="${['DEP','ARR']}"
									valueMessagePrefix="action.name"
									noSelection="['':'-Choisissez votre élément-']" />																	
							</td>	
						</tr>
						<tr>
							<td class="eventTD" valign="top">${message(code: 'action.time', default: 'Report')}:</td>
							<td><input type="text" name="date_action_picker_monthly" id="date_action_picker_monthly" value="${null}"/> </td>
							<script type="text/javascript">
								timePickerLaunch ("date_action_picker_monthly","date");							
							</script>	
						</tr>
					</tbody>
				</table>
				<g:hiddenField name="fromReport" value="${fromReport}" />
				<g:hiddenField name="viewType" value="${viewType}" />				
				<g:submitToRemote class="listButton"
	         		onLoading="document.getElementById('spinner').style.display = 'inline';"
	                onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
					update="itineraryReportTemplate"
					onSuccess="closePopup()"
					url="[controller:'action', action:'regularizeAction',id:viewType]" value="${message(code: 'mileage.create.validation', default: 'Report')}">
				</g:submitToRemote>
			</g:form>
			<a class="close" id="closeId" href="#close"></a>
		</div>
	<g:if test="${actionListMap == null || actionListMap.size() == 0}">
		${message(code: 'action.list.empty', default: 'Report')}
	</g:if>
	<g:else>
		<table style="table-layout:fixed;">
			<tbody>
				<g:each in="${actionListMap}" var='actionListItem' status="m">
					<tr>
						<td class="itineraryReportDateTD">${(actionListItem.key).format('dd/MM/yyyy')}</td>
						<g:each in="${actionListItem.value}" var='actionItem' status="n">
							<td class="itineraryReportTD" >${actionItem.site.name}<br>
								<a href="#itinerary_action_form_${m}_${n}" id="itinerary_action_pop_${m}_${n}">							
								<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
									<font color="red">
								</g:if>
								<g:else>
									<font color="green">
								</g:else>				
								${actionItem.date.format('kk:mm')}</font></a>
								<a href="#x" class="overlay" id="itinerary_action_form_${m}_${n}" style="background: transparent;"></a>
								<div id="itinerary_action_popup_${m}_${n}" class="popup">
									<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
									<p>${message(code: 'action.create.info', default: 'Report')}</p>
									<g:form action="create">
										<input type="text" name="date_action_picker_${m}_${n}" id="date_action_picker_${m}_${n}" value="${actionItem.date.format('kk:mm')}"/>
										<script type="text/javascript">
											timePickerLaunch ("date_action_picker_${m}_${n}","time");
										</script>	
											<BR>${message(code: 'inAndOut.create.event', default: 'Report')}:									
										<g:select
											name="actionType" from="${['DEP','ARR']}"
											valueMessagePrefix="itinerary.name"
											noSelection="['':'-Choisissez votre élément-']" />
										<g:submitToRemote class="listButton"
					                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
					                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
											update="itineraryReportTemplate"
											onSuccess="closePopup()"
											url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
										</g:submitToRemote>
										<BR>
										<g:submitToRemote class="trash"
					                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
					                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
											update="itineraryReportTemplate"
											onSuccess="closePopup()"
											url="[controller:'action', action:'trash']" value="${message(code: 'action.delete.label', default: 'Report')}">
										</g:submitToRemote>
										<g:hiddenField name="viewType" value="${viewType}" />
										<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${j}" value="${itineraryInstance.id}" /></g:if>
										<g:if test="${actionItem != null}"><g:hiddenField name="ActionItemId_${j}" value="${actionItem.id}" /></g:if>
									</g:form>
									<a class="close" id="closeId" href="#close"></a>
								</div>
						</g:each>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:else>
</g:if>