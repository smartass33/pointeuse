<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.ItineraryNature" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>

<g:if test="${!siteTemplate}">
	<g:if test="${theoriticalActionsList != null }">
		<h1><g:message code="itinerary.theoritical.label"/></h1>
	</g:if>
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<g:each in="${theoriticalActionsList}" var='actionItem' status="j">
					<g:if test="${outActionItem != null && outActionItem.site.equals(actionItem.site)}">
						<td class="itineraryReportTD" >${actionItem.site.name}<br>	
							<a href="#itinerary_out_action_form_${j}" id="itinerary_out_action_pop_${j}">	
							<g:if test="${outActionItem.nature.equals(ItineraryNature.ARRIVEE)}">
								<font color="red">
							</g:if>
							<g:else>
								<font color="green">
							</g:else>
							${outActionItem.date.format('kk:mm')}</font></a>
							<a href="#x" class="overlay" id="itinerary_out_action_form_${j}" style="background: transparent;"></a>
							<div id="itinerary_out_action_popup_${j}" class="popup">
								<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
								<p>${message(code: 'action.create.info', default: 'Report')}</p>
								<g:form action="create">
									<input type="text" name="date_out_action_picker_${j}" id="date_out_action_picker_${j}" value="${outActionItem.date.format('kk:mm')}"/> 
									<script type="text/javascript">
										timePickerLaunch ("date_out_action_picker_${j}","time");
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
							 -  <br>					 
							<a href="#itinerary_in_action_form_${j}" id="itinerary_in_action_pop_${j}">
							
							<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
								<font color="red">
							</g:if>
							<g:else>
								<font color="green">
							</g:else>
							
							${actionItem.date.format('kk:mm')}</font></a>
							<a href="#x" class="overlay" id="itinerary_in_action_form_${j}" style="background: transparent;"></a>
							<div id="itinerary_in_action_popup_${j}" class="popup">
								<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
								<p>${message(code: 'action.create.info', default: 'Report')}</p>
								<g:form action="create">
									<input type="text" name="date_in_action_picker_${j}" id="date_in_action_picker_${j}" value="${actionItem.date.format('kk:mm')}"/> 
									<script type="text/javascript">
										timePickerLaunch ("date_in_action_picker_${j}","time");
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
											timePickerLaunch ("date_action_picker_${j}","time");
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
						<% outActionItem = actionItem;%>			
					</g:else>
				</g:each>
			</tr>
		</tbody>
	</table>
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<g:each in="${theoriticalSaturdayActionsList}" var='actionItem' status="j">
					<g:if test="${outActionItem != null && outActionItem.site.equals(actionItem.site)}">
						<td class="itineraryReportTD" >${actionItem.site.name}<br>	
							<a href="#itinerary_out_action_form_${j}" id="itinerary_out_action_pop_${j}">	
							<g:if test="${outActionItem.nature.equals(ItineraryNature.ARRIVEE)}">
								<font color="red">
							</g:if>
							<g:else>
								<font color="green">
							</g:else>
							${outActionItem.date.format('kk:mm')}</font></a>
							<a href="#x" class="overlay" id="itinerary_out_action_form_${j}" style="background: transparent;"></a>
							<div id="itinerary_out_action_popup_${j}" class="popup">
								<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
								<p>${message(code: 'action.create.info', default: 'Report')}</p>
								<g:form action="create">
									<input type="text" name="date_out_action_picker_${j}" id="date_out_action_picker_${j}" value="${outActionItem.date.format('kk:mm')}"/> 
									<script type="text/javascript">
										timePickerLaunch ("date_out_action_picker_${j}","time");
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
							 - 	<br>				 
							<a href="#itinerary_in_action_form_${j}" id="itinerary_in_action_pop_${j}">
							
							<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
								<font color="red">
							</g:if>
							<g:else>
								<font color="green">
							</g:else>
							
							${actionItem.date.format('kk:mm')}</font></a>
							<a href="#x" class="overlay" id="itinerary_in_action_form_${j}" style="background: transparent;"></a>
							<div id="itinerary_in_action_popup_${j}" class="popup">
								<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
								<p>${message(code: 'action.create.info', default: 'Report')}</p>
								<g:form action="create">
									<input type="text" name="date_in_action_picker_${j}" id="date_in_action_picker_${j}" value="${actionItem.date.format('kk:mm')}"/> 
									<script type="text/javascript">
										timePickerLaunch ("date_in_action_picker_${j}","time");
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
											timePickerLaunch ("date_action_picker_${j}","time");
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
						<% outActionItem = actionItem;%>			
					</g:else>
				</g:each>
			</tr>
		</tbody>
	</table>
</g:if>
<g:else>
	<g:if test="${theoriticalActionsList != null }">
		<h1><g:message code="itinerary.theoritical.label"/> du site: ${site.name}</h1>
	</g:if>
		<table style="table-layout:fixed;">
		<tbody>
			<tr><td class="itineraryReportTD">${message(code: 'itinerary.weekday', default: 'Report')}</td>
				<g:each in="${theoriticalActionsList}" var='actionItem' status="j">
					<td class="itineraryReportTD">
						${actionItem.itinerary.name}<br>
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
									timePickerLaunch ('date_action_picker_${j}','time');
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

				</g:each>
			</tr>
		</tbody>
	</table>
	
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<td class="itineraryReportTD">${message(code: 'itinerary.saturday', default: 'Report')}</td>
				<g:each in="${theoriticalSaturdayActionsList}" var='actionItem' status="k">
					<td class="itineraryReportTD">
						${actionItem.itinerary.name}<br>
						<a href="#itinerary_action_form_${k}" id="itinerary_action_pop_${k}">
						<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
							<font color="red">
						</g:if>
						<g:else>
							<font color="green">
						</g:else>
						
						${actionItem.date.format('kk:mm')}</font></a>
						<a href="#x" class="overlay" id="itinerary_action_form_${k}" style="background: transparent;"></a>
						<div id="itinerary_action_popup_${k}" class="popup">
							<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
							<p>${message(code: 'action.create.info', default: 'Report')}</p>
							<g:form action="create">
								<input type="text" name="date_action_picker_${k}" id="date_action_picker_${k}" value="${actionItem.date.format('kk:mm')}"/> 
								<script type="text/javascript">
									timePickerLaunch ("date_action_picker_${k}","time");
								</script>	
								<g:submitToRemote class="listButton"
			                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
			                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
									update="itineraryReportTemplate"
									onSuccess="closePopup()"
									url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
								</g:submitToRemote>
								<g:hiddenField name="viewType" value="${viewType}" />
								<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${k}" value="${itineraryInstance.id}" /></g:if>
								<g:if test="${actionItem != null}"><g:hiddenField name="inActionItemId_${k}" value="${actionItem.id}" /></g:if>
							</g:form>
							<a class="close" id="closeId" href="#close"></a>
						</div>
					</td>
				</g:each>
			</tr>
		</tbody>
	</table>
</g:else>
