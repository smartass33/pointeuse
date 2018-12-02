<%@ page import="pointeuse.Reason" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.Itinerary" %>



<div id="last5days" >
	<h1><g:message code="daily.last.3.events" default="Last Name" /></h1>
		<table border="1" style="padding:15px;">
			<thead>
				<th class="eventTD" style="width:150px;"><g:message code="employee.date" default="Last Name" /></th>
				<th class="eventTD"><g:message code="daily.total.label" default="Last Name" /></th>
				<th colspan="11" class="eventTD"><g:message code="events.label" default="Last Name" /></th>
			</thead>
			<tbody>
				<g:each in="${mapByDay}" status="k" var="day">
					<tr>
						<td class="eventTD"><g:formatDate format="E dd MMM yyyy'" date="${day.key}" /></td>
						<g:if test="${totalByDay.get(day.key)!=null}">
							<td class="eventTD" >
								${totalByDay.get(day.key)}
							</td>
						</g:if>
						<g:each in="${day.value}" status="l" var="lastInOrOut">
							<g:if test="${lastInOrOut.type.equals('E')}">
								<td bgcolor="98FB98" class="eventTD"><g:formatDate format="HH:mm"
										date="${lastInOrOut.time}" /></td>
							</g:if>
							<g:else>
								<td bgcolor="#FFC0CB" class="eventTD"><g:formatDate format="HH:mm"
										date="${lastInOrOut.time}" /></td>
							</g:else>
						</g:each>
					</tr>
				</g:each>
			</tbody>
		</table>
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
				<g:if test="${inAndOut.regularization|| inAndOut.systemGenerated==true}">
					<th class="eventTD">
						<g:if test="${inAndOut.type.equals('E') }">
							<font color="red">${message(code: 'inAndOut.in.label')}</font>
						</g:if>
						<g:else>
							<font color="red">${message(code: 'inAndOut.out.label')}</font>
						</g:else>
					</th>
				</g:if>
				<g:else>				
					<th class="eventTD">
						<g:if test="${inAndOut.type.equals('E') }">
							${message(code: 'inAndOut.in.label')}
						</g:if>
						<g:else>
							${message(code: 'inAndOut.out.label')}
						</g:else>
					</th>			
				</g:else>
			</g:each>
		</thead>
		<tbody>
			<tr>
				<g:each in="${inAndOuts}" var="inAndOut">
					<g:if test="${inAndOut.regularization|| inAndOut.systemGenerated==true}">					
						<td bgcolor="#cccccc" class="eventTD"><font color="red"><g:formatDate format="H:mm:s'" date="${inAndOut.time}" /></font></td>
					</g:if>
					<g:else>
						<g:if test="${inAndOut.type.equals('E')}">
							<td bgcolor="98FB98"  class="eventTDEntry" ><g:formatDate format="H:mm:s'" date="${inAndOut.time}" /></td>
						</g:if>
						<g:else>
							<td bgcolor="#FFC0CB"  class="eventTDExit" ><g:formatDate format="H:mm:s'" date="${inAndOut.time}" /></td>
						</g:else>
					</g:else>
				</g:each>
			</tr>
		</tbody>
	</table>
</g:if>
<g:else>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${message(code: 'inAndOut.no.element')}<BR>
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
					<g:remoteLink action="addingEventToEmployee" update="last5days"
						onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    onComplete="document.getElementById('spinner').style.display = 'none';"	
						class="exitbutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:if> 
				<g:else>
					<g:remoteLink action="addingEventToEmployee" update="last5days"
						onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    onComplete="document.getElementById('spinner').style.display = 'none';"	
						class="entrybutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:else>
			</li>
			<li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</li>
			<li>	
				<g:link class="displayButton" controller="employee" action="reportLight" params="[userId:employee.id]">${message(code: 'employee.monthly.light.report.label', default: 'Report')}</g:link>
			</li>
			<li >
				<a href="#join_form" id="join_pop" class="addElementButton">${message(code: 'inAndOut.create.add.label.short', default: 'Report')}</a>
				<a href="#x" class="overlay" id="join_form" style="background: transparent;"></a>
				<div id="popup" class="popup">
					<h2>${message(code: 'inAndOut.create.button', default: 'Report')}</h2>
					<p>${message(code: 'inAndOut.create.info', default: 'Report')}</p>
					<g:form controller="inAndOut" action="create">
						<table>
							<tbody>
								<tr class="prop">
									<td class="eventTD" valign="top">${message(code: 'inAndOut.create.date.choice', default: 'Report')}:</td>
									<td class="eventTD" valign="top"><input type="text" name="date_picker" id="date_picker" /> 
										<script type="text/javascript">
											timePickerLaunch("date_picker","date");
										</script>
									</td>
								</tr>
								<tr class="prop">
									<td class="eventTD" valign="top">${message(code: 'inAndOut.create.event', default: 'Report')}:</td>
									<td class="eventTD" valign="top">
										<g:select
											name="event.type" from="${['E','S']}"
											valueMessagePrefix="entry.name"
											noSelection="['':'-Choisissez votre élément-']" />
									</td>
								</tr>
								<tr class="prop">
									<td class="eventTD" valign="top">${message(code: 'inAndOut.create.reason', default: 'Report')}:</td>
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
			                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
									update="last5days"
									onSuccess="closePopup()"
									url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
					</g:form>
					<a class="close" id="closeId" href="#close"></a>
				</div>
			</li>
			<li >
				<a href="#mileage_form" id="mileage_pop" class="addMileageButton">${message(code: 'mileage.create.add.label.short', default: 'Report')}</a>
				<a href="#x" class="overlay" id="mileage_form" style="background: transparent;"></a>
				<div id="mileage_popup" class="popup">
					<h2>${message(code: 'mileage.create.button', default: 'Report')}</h2>
					<p>${message(code: 'mileage.create.info', default: 'Report')}</p>
					<g:form action="create">
						<table>
							<tbody>
								<tr class="prop">
									<td class="eventTD" valign="top">${message(code: 'mileage.create.date.choice', default: 'Report')}:</td>
									<td class="eventTD" valign="top"><input type="text" name="date_mileage_picker" id="date_mileage_picker" /> 
										<script type="text/javascript">
											timePickerLaunch("date_mileage_picker","date");
										</script>
									</td>
								</tr>
								<tr class="prop">
									<td class="eventTD" valign="top">${message(code: 'mileage.create.value', default: 'Report')}:</td>
									<td class="eventTD" valign="top">
										<g:textField name="mileageValue" value="${mileageValue}" />
									</td>
								</tr>
							</tbody>
						</table>
						<g:hiddenField name="employeeId" value="${employee.id}" />
						<g:hiddenField name="fromReport" value="${fromReport}" />
						<g:submitToRemote class="listButton"
	                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
							update="last5days"
							onSuccess="closePopup()"
							url="[controller:'mileage', action:'addMileage']" value="${message(code: 'mileage.create.validation', default: 'Report')}">
						</g:submitToRemote>
					</g:form>
					<a class="close" id="closeId" href="#close"></a>
				</div>
			</li>
			<li >
				<a href="#itinerary_action_form" id="itinerary_action_pop" class="addElementButton">${message(code: 'action.create.add.label', default: 'Report')}</a>
				<a href="#x" class="overlay" id="itinerary_action_form" style="background: transparent;"></a>
				<div id="itinerary_action_popup" class="popup">
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
									<td class="eventTD" valign="top">${message(code: 'action.time', default: 'Report')}:</td>
									<td>	
										<input type="text" name="time_picker" id="time_picker" value="${(new Date()).format('dd/MM/yyyy HH:mm') }" /> 
										<script type="text/javascript">
											timePickerLaunch("time_picker","date");
										</script>
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
							</tbody>
						</table>
						<g:hiddenField name="employeeId" value="${employee.id}" />
						<g:hiddenField name="fromReport" value="${fromReport}" />
						<g:submitToRemote class="listButton"
	                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
							update="last5days"
							onSuccess="closePopup()"
							url="[controller:'action', action:'addAction']" value="${message(code: 'mileage.create.validation', default: 'Report')}">				
						</g:submitToRemote>
					</g:form>
					<a class="close" id="closeId" href="#close"></a>
				</div>
			</li>			
			<li>
				<g:link class="logoutButton"  url="${grailsApplication.config.serverURL}/${grailsApplication.config.context}">${message(code: 'employee.disconnect.label', default: 'Sortie')}</g:link>
			</li>			
		</ul>
	</div>
	
</div>