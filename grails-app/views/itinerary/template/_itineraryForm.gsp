
<script>
//Make the DIV element draggagle:
	dragElement(document.getElementById("${hrefName}_popup_${row}_${column}"));
	
	function dragElement(elmnt) {
	var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
	if (document.getElementById(elmnt.id + "header")) {
	  /* if present, the header is where you move the DIV from:*/
	  document.getElementById(elmnt.id + "header").onmousedown = dragMouseDown;
	} else {
	  /* otherwise, move the DIV from anywhere inside the DIV:*/
	  elmnt.onmousedown = dragMouseDown;
	}
	
	function dragMouseDown(e) {
	  e = e || window.event;
	  e.preventDefault();
	  // get the mouse cursor position at startup:
	  pos3 = e.clientX;
	  pos4 = e.clientY;
	  document.onmouseup = closeDragElement;
	  // call a function whenever the cursor moves:
	  document.onmousemove = elementDrag;
	}
	
	function elementDrag(e) {
	  e = e || window.event;
	  e.preventDefault();
	  // calculate the new cursor position:
	  pos1 = pos3 - e.clientX;
	  pos2 = pos4 - e.clientY;
	  pos3 = e.clientX;
	  pos4 = e.clientY;
	  // set the element's new position:
	  elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
	  elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
	}
	
	function closeDragElement() {
	  /* stop moving when mouse button is released:*/
	  document.onmouseup = null;
	  document.onmousemove = null;
	}
	}
</script>

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
				<!--tr class="prop">
					<td>${message(code: 'itinerary.comment', default: 'Report')}:</td>
					<td>
						<textarea name="commentary" id="commentary" >${actionItem != null ? actionItem.commentary :'' }</textarea>
					</td>
				</tr-->
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

