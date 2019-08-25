<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.ItineraryNature" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>

<g:if test="${theoriticalActionsMap != null }">
	<h1>
		<g:message code="itinerary.theoritical.label"/> du site: ${site.name}
	</h1>
	<h3>${message(code: 'itinerary.weekday', default: 'Report')}</h3>
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<g:each in="${theoriticalActionsMap}" var='actionListItem' status="m">
					<g:if test="${actionListItem.value != null && actionListItem.value.size() > 0 })">
						<tr>
							<td class="itineraryReportDateTD">${actionListItem.key.name}</td>
							<g:each in="${actionListItem.value}" var='actionItem' status="n">
								<td class="itineraryReportTD" >
									<a href="#itinerary_theoritical_action_form_${m}_${n}" id="itinerary_theoritical_action_pop_${m}_${n}">		

									<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
										<font color="red">
									</g:if>
									<g:else>
										<font color="green">
									</g:else>				
										${actionItem.date.format('kk:mm')}
									</font>
									<a href="#x" class="overlay" id="itinerary_theoritical_action_form_${m}_${n}" style="background: transparent;"></a>
									<div id="itinerary_theoritical_action_popup_${m}_${n}" class="popup">
										<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
										<p>${message(code: 'action.create.info', default: 'Report')}</p>
										<g:form action="create">	
											<input type="text" name="th_date_action_picker_${m}_${n}" id="th_date_action_picker_${m}_${n}" value="${actionItem.date.format('kk:mm')}"/>
											<script type="text/javascript">
												timePickerLaunch ("th_date_action_picker_${m}_${n}","time");						
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
							</g:each>
						</tr>
					</g:if>
				</g:each>
			</tr>	
		</tbody>
	</table>
</g:if>

<g:if test="${theoriticalSaturdayActionsMap != null }">
	<BR>
	<h3>${message(code: 'itinerary.saturday', default: 'Report')}</h3>
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<g:each in="${theoriticalSaturdayActionsMap}" var='actionListItem' status="x">
					<g:if test="${actionListItem.value != null && actionListItem.value.size() > 0 })">
						<tr>
							<td class="itineraryReportDateTD">${actionListItem.key.name}</td>
							<g:each in="${actionListItem.value}" var='actionItem' status="y">
								<td class="itineraryReportTD" >
									<a href="#itinerary_theoritical_saturday_action_form_${x}_${y}" id="itinerary_theoritical_saturday_action_pop_${x}_${y}">		
									<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
										<font color="red">
									</g:if>
									<g:else>
										<font color="green">
									</g:else>				
										${actionItem.date.format('kk:mm')}
									</font>
									<a href="#x" class="overlay" id="itinerary_theoritical_saturday_action_form_${x}_${y}" style="background: transparent;"></a>
									<div id="itinerary_theoritical_saturday_action_popup_${x}_${y}" class="popup">
										<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
										<p>${message(code: 'action.create.info', default: 'Report')}</p>
										<g:form action="create">	
											<input type="text" name="sat_date_action_picker_${x}_${y}" id="sat_date_action_picker_${x}_${y}" value="${actionItem.date.format('kk:mm')}"/>
											<script type="text/javascript">
												timePickerLaunch ("sat_date_action_picker_${x}_${y}","time");						
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
							</g:each>
						</tr>
					</g:if>
				</g:each>
			</tr>	
		</tbody>
	</table>
</g:if>