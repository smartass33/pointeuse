<a href="#x" class="overlay" id="${hrefName}_form_${row}_${column}" style="background: transparent;"></a>
<div id="${hrefName}_popup_${row}_${column}" class="popup">
	<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
	<p>${message(code: 'action.create.info', default: 'Report')}</p>
	<g:form action="create">
	<table>
		
		
			<tr class="prop">
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
		</table>	
		<g:hiddenField name="viewType" value="${viewType}" />
		<g:if test="${itineraryInstance != null}"><g:hiddenField name="itineraryId_${j}" value="${itineraryInstance.id}" /></g:if>
		<g:if test="${actionItem != null}"><g:hiddenField name="ActionItemId_${j}" value="${actionItem.id}" /></g:if>
	</g:form>
	<a class="close" id="closeId" href="#close"></a>
</div>