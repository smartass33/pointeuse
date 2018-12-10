<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.ItineraryNature" %>
<%@ page import="groovy.time.TimeCategory" %>




<g:set var="realCal" 			value="${Calendar.instance}"/>
<g:set var="theoriticalCal" 	value="${Calendar.instance}"/>
<g:set var="tdColor" 			value=""/>
<g:set var="timeDiff" 			value=""/>
<g:set var="actionItemColor" 	value=""/>

<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>



<div id="theoriticalTableDiv">
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
													update="itinerarySiteReportTemplate"
													onSuccess="closePopup()"
													url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
												</g:submitToRemote>
												<BR>
												<g:submitToRemote class="trash"
							                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
							                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
													update="itinerarySiteReportTemplate"
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
												update="itinerarySiteReportTemplate"
												onSuccess="closePopup()"
												url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
											</g:submitToRemote>
											<BR>
											<g:submitToRemote class="trash"
						                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
						                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
												update="itinerarySiteReportTemplate"
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
</div>
<BR><BR>	



<g:if test="${viewType.equals('monthlyViewBySite')}">
	<div id="monthlyViewBySite">
		<h1>${message(code: 'itinerary.real.label', default: 'Report')} <g:if test="${site != null}"> du site: ${site.name}</g:if></h1>
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
								<g:if test="${site != null}">${site.name}</g:if>				
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
				<g:if test="${site != null}"><g:hiddenField name="siteId" value="${site.id}" /></g:if>				
				<g:submitToRemote class="listButton"
		        		onLoading="document.getElementById('spinner').style.display = 'inline';"
		               onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
					update="itinerarySiteReportTemplate"
					onSuccess="closePopup()"
					url="[controller:'action', action:'regularizeAction',id:viewType]" value="${message(code: 'mileage.create.validation', default: 'Report')}">
				</g:submitToRemote>
			</g:form>
			<a class="close" id="closeId" href="#close"></a>
		</div>
		

		
		
		<g:if test="${actionListMap == null || actionListMap.size() == 0}">${message(code: 'action.list.empty', default: 'Report')}</g:if>
		<g:else>
			<table style="table-layout:fixed;">
				<tbody>
					<g:if test="${theoriticalActionsList != null}">
						<tr>
							<td class="itineraryReportDateTD" style="font:12px;">${message(code: 'itinerary.WEEK', default: 'Report')}</td>
							<g:each in="${theoriticalActionsList}" var='actionItem' status="a">
								<%
									if (actionItem.nature.equals(ItineraryNature.ARRIVEE)){
										actionItemColor='red'
									}else{
										actionItemColor='green'
									}
								 %>
								<td class="itineraryReportTD"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></td>
							</g:each>
						</tr>
					</g:if>
					<g:if test="${theoriticalSaturdayActionsList != null}">
						<tr>
							<td class="itineraryReportDateTD" style="font:12px;">${message(code: 'itinerary.SATURDAY', default: 'Report')}</td>
							<g:each in="${theoriticalSaturdayActionsList}" var='actionItem' status="b">
								<%
									if (actionItem.nature.equals(ItineraryNature.ARRIVEE)){
										actionItemColor='red'
									}else{
										actionItemColor='green'
									}
								 %>
								<td class="itineraryReportTD"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></td>
							</g:each>
						</tr>
					</g:if>
				
					<g:each in="${actionListMap}" var='actionListItem' status="m">
						<tr>
							<td class="itineraryReportDateTD">${(actionListItem.key).format('EEE dd')}</td>
							<g:each in="${actionListItem.value}" var='actionItem' status="n">
							
								<g:if test="${actionItem.date.getAt(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY}">
									<g:if test="${theoriticalActionsList.size() >= n}">										
										<% 
											theoriticalCal.time = actionItem.date
											realCal.time = actionItem.date
											theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
											theoriticalCal.set(Calendar.MINUTE,theoriticalActionsList[n].date.getAt(Calendar.MINUTE))
											use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
											if (timeDiff.minutes <= 15){
												tdColor = '#FFFFFF'
											}
											if (timeDiff.minutes > 15){
												tdColor = '#FFD700'
											}
											if (timeDiff.minutes > 30){
												tdColor = '#FF8C00'
											} 
											if (timeDiff.minutes > 60){
												tdColor = '#FF4500'
											}
											%>
									</g:if>			
								</g:if>
								<g:else>
									<g:if test="${theoriticalSaturdayActionsList.size() >= n}">
										<% 
											theoriticalCal.time = actionItem.date
											realCal.time = actionItem.date
											theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalSaturdayActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
											theoriticalCal.set(Calendar.MINUTE,theoriticalSaturdayActionsList[n].date.getAt(Calendar.MINUTE))
											use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
											if (timeDiff.minutes <= 15){
												tdColor = '#FFFFFF'
											}
											if (timeDiff.minutes > 15){
												tdColor = '#FFD700'
											}
											if (timeDiff.minutes > 30){
												tdColor = '#FF8C00'
											} 
											if (timeDiff.minutes > 60){
												tdColor = '#FF4500'
											}
											%>								
									</g:if>			
								</g:else>
									<%
									if (actionItem.nature.equals(ItineraryNature.ARRIVEE)){
										actionItemColor='red'
									}else{
										actionItemColor='green'
									}
								 %>
	
									<td class="itineraryReportTD" style="background-color:${tdColor};">
									
									<div id="tooltip">
										<a href="#itinerary_action_monthly_form_${m}_${n}" id="itinerary_action_monthly_pop_${m}_${n}"> 
											<button id="button" style="background-color:${tdColor};"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></button>
	                							<span>${timeDiff}</span>	            							
            							</a>
									</div>
									
									
									<a href="#x" class="overlay" id="itinerary_action_monthly_form_${m}_${n}" style="background: transparent;"></a>
									<div id="itinerary_action_monthly_popup_${m}_${n}" class="popup">
										<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
										<p>${message(code: 'action.create.info', default: 'Report')}</p>
										<g:form action="create">
											<input type="text" name="date_action_picker_monthly_${m}_${n}" id="date_action_picker_monthly_${m}_${n}" value="${actionItem.date.format('kk:mm')}"/>
											<script type="text/javascript">
												timePickerLaunch('date_action_picker_monthly_${m}_${n}','time');
											</script>	
												<BR>${message(code: 'inAndOut.create.event', default: 'Report')}:									
											<g:select
												name="actionType" from="${['DEP','ARR']}"
												valueMessagePrefix="itinerary.name"
												noSelection="['':'-Choisissez votre élément-']" />
											<g:submitToRemote class="listButton"
						                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
						                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
												update="itinerarySiteReportTemplate"
												onSuccess="closePopup()"
												url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
											</g:submitToRemote>
											<BR>
											<g:submitToRemote class="trash"
						                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
						                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
												update="itinerarySiteReportTemplate"
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
	</div>
</g:if>

<g:if test="${viewType.equals('dailyViewBySite')}">
	<div id="dailyViewBySite">
		<h1>${message(code: 'itinerary.real.label', default: 'Report')} <g:if test="${site != null}"> du site: ${site.name}</g:if></h1>
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
						update="itinerarySiteReportTemplate"
						onSuccess="closePopup()"
						url="[controller:'action', action:'regularizeAction',id:viewType]" value="${message(code: 'mileage.create.validation', default: 'Report')}">
					</g:submitToRemote>
				</g:form>
				<a class="close" id="closeId" href="#close"></a>
			</div>
		<g:if test="${dailyActionMap == null || dailyActionMap.size() == 0}">
			${message(code: 'action.list.empty', default: 'Report')}
		</g:if>
		<g:else>
			<table style="table-layout:fixed;">
				<tbody>
					<tr>
						<g:each in="${dailyActionMap}" var='actionListItem' status="m">
							<g:if test="${actionListItem.value != null && actionListItem.value.size() > 0 })">
								<tr>
									<td class="itineraryReportDateTD">${actionListItem.key.name}</td>
									<g:each in="${actionListItem.value}" var='actionItem' status="n">
										<td class="itineraryReportTD" >
											<a href="#itinerary_action_form_${m}_${n}" id="itinerary_action_pop_${m}_${n}">		
		
											<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
												<font color="red">
											</g:if>
											<g:else>
												<font color="green">
											</g:else>				
												${actionItem.date.format('kk:mm')}
											</font>
											<a href="#x" class="overlay" id="itinerary_action_form_${m}_${n}" style="background: transparent;"></a>
											<div id="itinerary_action_popup_${m}_${n}" class="popup">
												<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
												<p>${message(code: 'action.create.info', default: 'Report')}</p>
												<g:form action="create">	
													<input type="text" name="date_action_picker_${m}_${n}" id="date_action_picker_${m}_${n}" value="${actionItem.date.format('kk:mm')}"/>
													<script type="text/javascript">
														timePickerLaunch ("date_action_picker_${m}_${n}","time");						
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
														update="itinerarySiteReportTemplate"
														onSuccess="closePopup()"
														url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
													</g:submitToRemote>
													<BR>
													<g:submitToRemote class="trash"
								                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
								                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
														update="itinerarySiteReportTemplate"
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
		</g:else>
	</div>
</g:if>