<%@ page import="pointeuse.Itinerary" %>

<a href="#x" class="overlay" id="${hrefName}_form_${row}_${column}" style="background: transparent;" ></a>
<div id="${hrefName}_popup_${row}_${column}" class="popup" >
	<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
	<p>${message(code: 'action.create.info', default: 'Report')}</p>
	<g:form action="create">
	<table>
			<tbody>
				<tr>
					<td>${message(code: 'action.time', default: 'Report')}:</td>
					<td>											
						<input type="text" name="${actionPicker}_${row}_${column}" id="${actionPicker}_${row}_${column}" value="${actionItem.date.format('kk:mm')}"/>
						<script type="text/javascript">
							timePickerLaunch('${actionPicker}_${row}_${column}','time');
						</script>	
					</td>													
				</tr>
				<tr class="prop">
					<td>
						${message(code: 'inAndOut.create.event', default: 'Report')}:
					</td>
					<td>
						<g:select
							name="actionType" from="${['DEP','ARR']}"
							valueMessagePrefix="itinerary.name"
							noSelection="['':'-Choisissez votre élément-']" />
					</td>
				</tr>
				<tr>
					<td>
						${message(code: 'itinerary.label', default: 'Report')}:
					</td>
					<td>
						<g:if test="${itineraryInstance != null}">
							<g:select
								name="itineraryId" from="${Itinerary.list([sort:'name'])}"				
								optionValue="name"
								optionKey="id"
								noSelection="${['': itineraryInstance.name ]}" />
						</g:if>
						<g:else>
								<g:select
								name="itineraryId" from="${Itinerary.list([sort:'name'])}"				
								optionValue="name"
								optionKey="id"
								noSelection="['':'-Choisissez votre élément-']" />
						</g:else>

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
				<tr class="prop">
					<td>${message(code: 'itinerary.comment', default: 'Report')}:</td>
					<td>
						<textarea name="commentary" id="commentary" >${actionItem != null ? actionItem.commentary :'' }</textarea>
					</td>
				</tr>
				<tr class="prop">
					<td>${message(code: 'action.fnc')}:</td>
					<td>
						<input type="text" id="fnc" value="${actionItem != null ? actionItem.fnc :'' }" name="fnc" />
					</td>
				</tr>				
				<tr class="prop">
					<td>${message(code: 'action.other')}:</td>
					<td>
						<input type="text" id="other" value="${actionItem != null ? actionItem.other :'' }" name="other" />
					</td>
				</tr>				
				<tr class="prop">
					<td>											
						<g:submitToRemote class="listButton"
	                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
							update="itinerarySiteReportTemplate"
							onSuccess="closePopup()"
							url="[controller:'action', action:'modifyAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
						</g:submitToRemote>															
					</td>
					<td>
						<g:submitToRemote class="trash" id='trash'
	                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
							update="itinerarySiteReportTemplate"
							onSuccess="closePopup()"
							url="[controller:'action', action:'trash']" value="${message(code: 'action.delete.label', default: 'Report')}">
						</g:submitToRemote>
					</td>
				</tr>
			</tbody>
		</table>
		<g:hiddenField name="viewType" value="${viewType}" />
		<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${j}" value="${itineraryInstance.id}" /></g:if>
		<g:if test="${actionItem != null}"><g:hiddenField name="ActionItemId_${j}" value="${actionItem.id}" /></g:if>
	</g:form>
	<a class="close" id="closeId" href="#close"></a>
</div>

