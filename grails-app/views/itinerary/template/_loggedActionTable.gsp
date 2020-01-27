<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.ItineraryNature" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="groovy.time.TimeCategory" %>


<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>
<g:set var="outActionItem"/>
<g:set var="tdColor" 			value=""/>
<g:set var="timeDiff" 			value=""/>
<g:set var="actionItemColor" 	value=""/>
<g:set var="myYellow" 			value="#fefb00"/>
<g:set var="myOrange" 			value="#74F3FE"/>
<g:set var="myRed" 				value="#FF8E79"/>
<g:set var="anomalyList" 		value="${[]}"/>
<g:set var="anomalyThList" 		value="${[]}"/>
<g:set var="anomalyEcartList" 	value="${[]}"/>

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
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'action.ne.label')}:</td>
							<td>
								<g:checkBox name="chkBoxNE" />																
							</td>	
							</tr>	
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'action.relais.label')}:</td>
							<td>
								<g:checkBox name="chkBoxRELAY"/>																
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
							<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %>
							<g:if test="${(actionItem.date).getAt(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY}">
								<g:if test="${theoriticalActionsList.size() >= j && theoriticalActionsList[j] != null}">										
									<% 
										theoriticalCal.time = actionItem.date
										realCal.time = actionItem.date
										theoriticalCal.set(Calendar.HOUR_OF_DAY,(theoriticalActionsList[j].date).getAt(Calendar.HOUR_OF_DAY))
										theoriticalCal.set(Calendar.MINUTE,(theoriticalActionsList[j].date).getAt(Calendar.MINUTE))
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
								<g:if test="${theoriticalSaturdayActionsList.size() >= j && theoriticalSaturdayActionsList[j] != null}">
									<% 
										theoriticalCal.time = actionItem.date
										realCal.time = actionItem.date
										theoriticalCal.set(Calendar.HOUR_OF_DAY,(theoriticalSaturdayActionsList[j].date).getAt(Calendar.HOUR_OF_DAY))
										theoriticalCal.set(Calendar.MINUTE,(theoriticalSaturdayActionsList[j].date).getAt(Calendar.MINUTE))
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
							<td class="itineraryReportTD" style="background-color:${tdColor};">${actionItem.site.name}<BR>		
							<div id="tooltip">
								<a href="#itinerary_action_form_${j}" id="itinerary_action_pop_${j}"> 
									<button id="button" style="background-color:${tdColor};"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></button>
								    <span>${actionItem.itinerary.name}<BR>${timeDiff}</span>	            							
							   	</a>
							</div>	
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
											<BR>
											${message(code: 'action.relais.label')}:
											<g:checkBox name="chkBoxNE" />			
											<BR>
											${message(code: 'action.ne.label')}:
											<g:checkBox name="chkBoxRELAY" />															

										<BR>
										${message(code: 'itinerary.label', default: 'Report')}:	<g:select
											name="itinerary.name" from="${Itinerary.list([sort:'name'])}"
											
											valueMessagePrefix="itinerary.name"
											noSelection="['':'-Choisissez votre tournée-']" />																	
										<BR>
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
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'action.ne.label')}:</td>
							<td>
								<g:checkBox name="chkBoxNE" />																
							</td>	
						</tr>	
						<tr class="prop">
							<td class="eventTD" valign="top">${message(code: 'action.relais.label')}:</td>
							<td>
								<g:checkBox name="chkBoxRELAY"/>																
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
						<td class="itineraryReportDateTD">${(actionListItem.key).format('dd/MM/yyyy')}<BR>ordre reel</td>
						<g:each in="${(actionListNotOrderedMap.get(actionListItem.key))}" var="noOrderItem">
							<td class="itineraryReportDateTD">${noOrderItem.site.name}<BR>${noOrderItem.date.format('kk:mm')}</td>
						</g:each>
					</tr>
					<tr>
						<td class="itineraryReportDateTD">${(actionListItem.key).format('dd/MM/yyyy')}</td>
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
							<td class="itineraryReportTD" style="background-color:${tdColor};">${actionItem.site.name}<BR>		
							<div id="tooltip">
								<a href="#itinerary_action_monthly_form_${m}_${n}" id="itinerary_action_monthly_pop_${m}_${n}"> 
									<button id="button" style="background-color:${tdColor};"><font color="${actionItemColor}">${actionItem.date.format('kk:mm')}</font></button>
								    <span>${actionItem.itinerary.name}<BR>${timeDiff}</span>	            							
							   	</a>
							</div>	
							<g:itineraryForm hrefName="itinerary_action" actionPicker="date_action_picker" row="${m}" column="${n}" viewType="${viewType}" actionItem="${actionItem}" itinerary="${itineraryInstance}"/>																
						</g:each>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:else>
</g:if>