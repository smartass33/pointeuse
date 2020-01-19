<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.ItineraryNature" %>
<%@ page import="groovy.time.TimeCategory" %>


<g:set var="realCal" 			value="${Calendar.instance}"/>
<g:set var="theoriticalCal" 	value="${Calendar.instance}"/>
<g:set var="tdColor" 			value=""/>
<g:set var="timeDiff" 			value=""/>
<g:set var="actionItemColor" 	value=""/>
<g:set var="myYellow" 			value="#fefb00"/>
<g:set var="myOrange" 			value="#74F3FE"/>
<g:set var="myRed" 				value="#FF8E79"/>
<g:set var="anomalyList" 		value="${[]}"/>
<g:set var="anomalyThList" 		value="${[]}"/>
<g:set var="anomalyEcartList" 	value="${[]}"/>

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
										<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %>
									
										<div id="tooltip">
											<a href="#itinerary_theoritical_form_${m}_${n}" id="itinerary_theoritical_pop_${m}_${n}"> 
												<button id="button" style="background-color:${tdColor};"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></button>
										    	<span>${actionItem.itinerary.name}<BR>${timeDiff}</span>	            							
									   		</a>
										</div>	
										<g:itineraryTheoriticalForm hrefName="itinerary_theoritical" actionPicker="th_date_action_picker" row="${m}" column="${n}" viewType="${viewType}" actionItem="${actionItem}" itinerary="${itineraryInstance}"/>									
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
									<td class="itineraryReportTD">
										<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %>
									
										<div id="tooltip">
											<a href="#itinerary_theoritical_saturday_form_${x}_${y}" id="itinerary_theoritical_saturday_pop_${x}_${y}"> 
												<button id="button" style="background-color:${tdColor};"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></button>
										    	<span>${actionItem.itinerary.name}<BR>${timeDiff}</span>	            							
									   		</a>
										</div>	
										<g:itineraryTheoriticalForm hrefName="itinerary_theoritical_saturday" actionPicker="sat_date_action_picker" row="${x}" column="${y}" viewType="${viewType}" actionItem="${actionItem}" itinerary="${itineraryInstance}"/>									
								</g:each>
							</tr>
						</g:if>
					</g:each>
				</tr>	
			</tbody>
		</table>
	</g:if>
</div>
<g:if test="${viewType.equals('anomalyViewBySite')}">
	<BR>
	<div id="anomalyViewBySite">
		<g:if test="${actionListMap == null || actionListMap.size() == 0}">${message(code: 'action.list.empty', default: 'Report')}</g:if>
		<g:else>
			<g:each in="${actionListMap}" var='actionListItem' status="m">				
					<g:each in="${actionListItem.value}" var='actionItem' status="n">
						<g:if test="${actionItem.date.getAt(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY}">
							<g:if test="${theoriticalActionsList.size() >= n && theoriticalActionsList[n] != null}">										
								<% 
									theoriticalCal.time = actionItem.date
									realCal.time = actionItem.date
									theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
									theoriticalCal.set(Calendar.MINUTE,theoriticalActionsList[n].date.getAt(Calendar.MINUTE))
									use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
									if ((timeDiff.minutes + timeDiff.hours*60) > 30){
										anomalyList.add(actionItem)
										anomalyThList.add(theoriticalActionsList[n])
										anomalyEcartList.add(timeDiff)
									} 
								%>
							</g:if>			
						</g:if>
						<g:else>
							<g:if test="${theoriticalSaturdayActionsList.size() >= n && theoriticalSaturdayActionsList[n] != null}">
								<% 
									theoriticalCal.time = actionItem.date
									realCal.time = actionItem.date
									theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalSaturdayActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
									theoriticalCal.set(Calendar.MINUTE,theoriticalSaturdayActionsList[n].date.getAt(Calendar.MINUTE))
									use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
									if ((timeDiff.minutes + timeDiff.hours*60) > 30){
										anomalyList.add(actionItem)
										anomalyThList.add(theoriticalSaturdayActionsList[n])	
										anomalyEcartList.add(timeDiff)
									}
								%>								
							</g:if>			
						</g:else>							
					</g:each>
			</g:each>
			<g:if test="${anomalyList != null}">
				<table style="table-layout:fixed;">
					<thead>
						<tr>
							<th>${message(code: 'report.table.date.label')}</th>						
							<th>${message(code: 'action.time.real')}</th>
							<th>${message(code: 'action.time.theoritical')}</th>
							<th style="width:90px;">${message(code: 'action.ecart.label')}</th>
							<th>${message(code: 'itinerary.comment')}</th>
							<th>${message(code: 'action.fnc')}</th>
							<th>${message(code: 'action.other')}</th>
						</tr>
					</thead>
					<tbody>
						<g:each in="${anomalyList}" var="anomaly" status="s">
							<tr>
								<td class="itineraryReportDateTD">${anomaly.date.format('EEE dd MMM') }</td>								
								<td class="itineraryReportDateTD">${anomaly.date.format('kk:mm') }</td>
								<td class="itineraryReportDateTD">${anomalyThList[s].date.format('kk:mm') }</td>
								<td class="itineraryReportDateTD">${anomalyEcartList[s]}</td>								
								<td class="itineraryReportDateTD">${anomaly.commentary}</td>
								<td class="itineraryReportDateTD">${anomaly.fnc}</td>
								<td class="itineraryReportDateTD">${anomaly.other}</td>
							</tr>
						</g:each>
					</tbody>
				</table>
			</g:if>
		</g:else>
	</div>
</g:if>

<g:if test="${viewType.equals('monthlyViewBySite')}">
	<div id="monthlyViewBySite">
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
				
					<tr>
						<td class="itineraryReportDateTD" style="font:12px;">${message(code: 'itinerary.WEEK', default: 'Report')}</td>
						<g:each in="${theoriticalActionsList}" var='actionItem' status="m">
							<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %>
							<td class="itineraryReportDateTD">${actionItem.itinerary.name}<BR> <font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></td>
						</g:each>
					</tr>
					<tr>
						<td class="itineraryReportDateTD" style="font:12px;">${message(code: 'itinerary.SATURDAY', default: 'Report')}</td>
						<g:each in="${theoriticalSaturdayActionsList}" var='actionItem' status="m">
							<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %>
							<td class="itineraryReportDateTD">${actionItem.itinerary.name}<BR> <font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></td>
						</g:each>
					</tr>
					

					
					<g:each in="${actionListMap}" var='actionListItem' status="m">
					
					<tr>
						<td class="itineraryReportDateTD">${(actionListItem.key).format('dd/MM/yyyy')}<BR>ordre reel</td>
						<g:each in="${(actionListNotOrderedMap.get(actionListItem.key))}" var="noOrderItem">
							<td class="itineraryReportDateTD">${noOrderItem.date.format('kk:mm')}</td>
						</g:each>
					</tr>
					
						<tr>
							<td class="itineraryReportDateTD">${(actionListItem.key).format('EEE dd')}</td>
							<g:each in="${actionListItem.value}" var='actionItem' status="n">
								<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %>
								<g:if test="${actionItem.date.getAt(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY}">
									<g:if test="${theoriticalActionsList.size() >= n && theoriticalActionsList[n] != null}">										
										<% 
											theoriticalCal.time = actionItem.date
											realCal.time = actionItem.date
											theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
											theoriticalCal.set(Calendar.MINUTE,theoriticalActionsList[n].date.getAt(Calendar.MINUTE))
											use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
											if ((timeDiff.minutes + timeDiff.hours) <= 15){
												tdColor = '#FFFFFF'
											}
											if ((timeDiff.minutes + timeDiff.hours*60) > 15){
												tdColor = myYellow
											}
											if ((timeDiff.minutes + timeDiff.hours*60) > 30){
												tdColor = myOrange
											} 
											if ((timeDiff.minutes + timeDiff.hours*60) > 60){
												tdColor = myRed
											}
										%>
									</g:if>			
								</g:if>
								<g:else>
									<g:if test="${theoriticalSaturdayActionsList.size() >= n && theoriticalSaturdayActionsList[n] != null}">
										<% 
											theoriticalCal.time = actionItem.date
											realCal.time = actionItem.date
											theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalSaturdayActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
											theoriticalCal.set(Calendar.MINUTE,theoriticalSaturdayActionsList[n].date.getAt(Calendar.MINUTE))
											use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
											if (timeDiff.minutes <= 15){
												tdColor = '#FFFFFF'
											}
											if ((timeDiff.minutes + timeDiff.hours*60) > 15){
												tdColor = myYellow
											}
											if ((timeDiff.minutes + timeDiff.hours*60) > 30){
												tdColor = myOrange
											} 
											if ((timeDiff.minutes + timeDiff.hours*60) > 60){
												tdColor = myRed
											}
											%>								
									</g:if>			
								</g:else>
								<td class="itineraryReportTD" style="background-color:${tdColor};">		
								<div id="tooltip">
									<a href="#itinerary_action_monthly_form_${m}_${n}" id="itinerary_action_monthly_pop_${m}_${n}"> 
										<button id="button" style="background-color:${tdColor};"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></button>
									    <span>${actionItem.itinerary.name}<BR>${timeDiff}</span>	            							
								   	</a>
								</div>				
								<g:itineraryForm hrefName="itinerary_action_monthly" actionPicker="date_action_picker_monthly" row="${m}" column="${n}" viewType="${viewType}" actionItem="${actionItem}" itinerary="${itineraryInstance}"/>									
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
										<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %>
										<g:if test="${actionItem.date.getAt(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY}">
											<g:if test="${theoriticalActionsMap == null && theoriticalActionsMap.get(actionListItem.key).size() >= n && theoriticalActionsMap.get(actionListItem.key)[n] != null}">										
												<% 
													theoriticalCal.time = actionItem.date
													realCal.time = actionItem.date
													theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalActionsMap.get(actionListItem.key)[n].date.getAt(Calendar.HOUR_OF_DAY))
													theoriticalCal.set(Calendar.MINUTE,theoriticalActionsMap.get(actionListItem.key)[n].date.getAt(Calendar.MINUTE))
													use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
													if ((timeDiff.minutes + timeDiff.hours) <= 15){
														tdColor = '#FFFFFF'
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 15){
														tdColor = myYellow
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 30){
														tdColor = myOrange
													} 
													if ((timeDiff.minutes + timeDiff.hours*60) > 60){
														tdColor = myRed
													}
												%>
											</g:if>			
										</g:if>
										<g:else>
											<g:if test="${theoriticalSaturdayActionsMap == null && theoriticalSaturdayActionsMap.get(actionListItem.key).size() >= n && theoriticalSaturdayActionsMap.get(actionListItem.key)[n] != null}">
												<% 													
													theoriticalCal.time = actionItem.date
													realCal.time = actionItem.date
													theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalSaturdayActionsMap.get(actionListItem.key)[n].date.getAt(Calendar.HOUR_OF_DAY))
													theoriticalCal.set(Calendar.MINUTE,theoriticalSaturdayActionsMap.get(actionListItem.key)[n].date.getAt(Calendar.MINUTE))
													use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
													if (timeDiff.minutes <= 15){
														tdColor = '#FFFFFF'
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 15){
														tdColor = myYellow
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 30){
														tdColor = myOrange
													} 
													if ((timeDiff.minutes + timeDiff.hours*60) > 60){
														tdColor = myRed
													}
												%>								
											</g:if>			
										</g:else>
									<td class="itineraryReportTD" style="background-color:${tdColor};">						
									<div id="tooltip">
										<a href="#itinerary_action_monthly_form_${m}_${n}" id="itinerary_action_monthly_pop_${m}_${n}"> 
											<button id="button" style="background-color:${tdColor};"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></button>
										    <span>${actionItem.itinerary.name}<BR>${timeDiff}</span>	            							
									   	</a>
									</div>
									<g:itineraryForm hrefName='itinerary_action_monthly' actionPicker="date_action_picker_daily" row="${m}" column="${n}" viewType="${viewType}" actionItem="${actionItem}" itinerary="${itineraryInstance}"/>
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