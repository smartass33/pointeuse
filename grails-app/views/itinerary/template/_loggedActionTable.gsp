<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.ItineraryNature" %>


<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>
<g:set var="outActionItem"/>


<g:if test="${viewType.equals('dailyView')}">
	<h1>Tournée Réelle</h1>
<BR>
	<!--  
		<thead>
			<th>${message(code: 'action.nature.label')}</th>
			<th>${message(code: 'laboratory.label')}</th>
			<th>${message(code: 'action.time.real')}</th>
		</thead>
	-->	
	<g:if test="${actionsList == null || actionsList.size() == 0}">
		pas d'information renseignée pour le jour indiqué
	</g:if>
	<g:else>
		<table style="width: 100%;">
			<tbody>
				<tr>
					<g:each in="${actionsList}" var='actionItem' status="j">
						<g:if test="${outActionItem != null && outActionItem.site.equals(actionItem.site)}">
							<td class="itineraryReportTD" >${actionItem.site.name}<br>	
								<a href="#itinerary_out_action_form_${j}" id="itinerary_out_action_pop_${j}"><font color="green">${outActionItem.date.format('kk:mm')}</font></a>
								<a href="#x" class="overlay" id="itinerary_out_action_form_${j}" style="background: transparent;"></a>
								<div id="itinerary_out_action_popup_${j}" class="popup">
									<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
									<p>${message(code: 'action.create.info', default: 'Report')}</p>
									<g:form action="create">
										<input type="text" name="date_out_action_picker_${j}" id="date_out_action_picker_${j}" value="${outActionItem.date.format('kk:mm')}"/> 
										<script type="text/javascript">
											datePickerLaunch(${j});
										</script>	
										<g:submitToRemote class="listButton"
					                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
					                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
											update="itineraryReportTemplate"
											onSuccess="closePopup()"
											url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
										</g:submitToRemote>
										<g:hiddenField name="viewType" value="${viewType}" />
										<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${j}" value="${itineraryInstance.id}" /></g:if>
										<g:if test="${outActionItem != null}"><g:hiddenField name="outActionItemId_${j}" value="${outActionItem.id}" /></g:if>
									</g:form>
									<a class="close" id="closeId" href="#close"></a>
								</div>

								 - 
								 
								<a href="#itinerary_in_action_form_${j}" id="itinerary_in_action_pop_${j}"><font color="red">${actionItem.date.format('kk:mm')}</font></a>
								<a href="#x" class="overlay" id="itinerary_in_action_form_${j}" style="background: transparent;"></a>
								<div id="itinerary_in_action_popup_${j}" class="popup">
									<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
									<p>${message(code: 'action.create.info', default: 'Report')}</p>
									<g:form action="create">
										<input type="text" name="date_in_action_picker_${j}" id="date_in_action_picker_${j}" value="${actionItem.date.format('kk:mm')}"/> 
										<script type="text/javascript">
											datePickerLaunch(${j});
										</script>	
										<g:submitToRemote class="listButton"
					                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
					                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
											update="itineraryReportTemplate"
											onSuccess="closePopup()"
											url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
										</g:submitToRemote>
										<g:hiddenField name="viewType" value="${viewType}" />
										<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${j}" value="${itineraryInstance.id}" /></g:if>
										<g:if test="${actionItem != null}"><g:hiddenField name="inActionItemId_${j}" value="${actionItem.id}" /></g:if>
									</g:form>
									<a class="close" id="closeId" href="#close"></a>
								</div>
							</td>
						</g:if>
						<g:else>
							<g:if test="${outActionItem == null}">
								<td class="itineraryReportTD">${actionItem.site.name}<br>
									<a href="#itinerary_action_form_${j}" id="itinerary_action_pop_${j}"><font color="red">${actionItem.date.format('kk:mm')}</font></a>
									<a href="#x" class="overlay" id="itinerary_action_form_${j}" style="background: transparent;"></a>
									<div id="itinerary_action_popup_${j}" class="popup">
										<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
										<p>${message(code: 'action.create.info', default: 'Report')}</p>
										<g:form action="create">
											<input type="text" name="date_action_picker_${j}" id="date_action_picker_${j}" value="${actionItem.date.format('kk:mm')}"/> 
											<script type="text/javascript">
												dateFirstPickerLaunch(${j});
											</script>	
											<g:submitToRemote class="listButton"
						                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
						                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
												update="itineraryReportTemplate"
												onSuccess="closePopup()"
												url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
											</g:submitToRemote>
											<g:hiddenField name="viewType" value="${viewType}" />
											<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${j}" value="${itineraryInstance.id}" /></g:if>
											<g:if test="${actionItem != null}"><g:hiddenField name="inActionItemId_${j}" value="${actionItem.id}" /></g:if>
										</g:form>
										<a class="close" id="closeId" href="#close"></a>
									</div>
								</td>					
							</g:if>
							<%
								outActionItem = actionItem;
						 	%>			
						</g:else>
					</g:each>
				</tr>
			</tbody>
		</table>
	</g:else>
</g:if>

