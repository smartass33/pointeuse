<%@ page import="pointeuse.Itinerary" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>
<g:set var="outActionItem"/>

<g:if test="${theoriticalActionsList != null && theoriticalActionsList.size() > 0}">
	<div>
		<table>
			<thead>
				<th>${message(code: 'action.nature.label')}</th>
				<th>${message(code: 'laboratory.label')}</th>
				<th>${message(code: 'action.time.theoritical')}</th>
				<th>${message(code: 'action.erase')}</th>
			</thead>	
			<tbody>
				<g:each in="${theoriticalActionsList}" var='theoriticalActionItem' status="k">
					<tr class="${(k % 2) == 0 ? 'even' : 'odd'}">
						<td>${theoriticalActionItem.nature}</td>
						<td>${theoriticalActionItem.site.name}</td>		
						<td>
							<a href="#itinerary_action_form_${k}" id="itinerary_action_pop_${k}">${theoriticalActionItem.date.format('kk:mm')}</a>
							<a href="#x" class="overlay" id="itinerary_action_form_${k}" style="background: transparent;"></a>
							<div id="itinerary_action_popup_${k}" class="popup">
								<h2>${message(code: 'action.modification.button', default: 'Report')}</h2>
								<p>${message(code: 'action.create.info', default: 'Report')}</p>
								<g:form action="create">
									<input type="text" name="date_action_picker_${k}" id="date_action_picker_${k}" value="${theoriticalActionItem.date.format('kk:mm')}"/> 
									<script type="text/javascript">
										timePickerLaunch ("date_action_picker_${k}","time");
									</script>	
									<g:submitToRemote class="listButton"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';closePopup();"				
										update="theoriticalActionTableDiv"
										onSuccess="closePopup()"
										url="[controller:'action', action:'modifyTheoriticalAction']" value="${message(code: 'action.modification.validation', default: 'Report')}">
									</g:submitToRemote>
									<g:if test="${theoriticalActionItem != null}"><g:hiddenField name="actionItemId_${k}" value="${theoriticalActionItem.id}" /></g:if>
								</g:form>
								<a class="close" id="closeId" href="#close"></a>
						</td>
						<td>
							<g:remoteLink action="trash" controller="itinerary" id="${theoriticalActionItem.id}" params="[actionItemId:theoriticalActionItem.id]"
				                    	update="theoriticalActionTableDiv"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';"
				                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
				                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
				            </g:remoteLink>	
						</td>	
					</tr>
				</g:each>
			</tbody>
		</table>
	</div>
</g:if>