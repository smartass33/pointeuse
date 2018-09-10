<%@ page import="pointeuse.Itinerary" %>
	
<div class="fieldcontain ${hasErrors(bean: itineraryInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="itinerary.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${itineraryInstance?.name}"/>
</div>
<div id ='' class="fieldcontain ${hasErrors(bean: itineraryInstance, field: 'deliveryBoy', 'error')} required">
	<label for="deliveryBoy">
		<g:message code="itinerary.deliveryBoy.label" default="Delivery Boy" />
		<span class="required-indicator">*</span>
	</label>
	<g:select 
		id="deliveryBoy" 
		name="deliveryBoy.id" 
		from="${employeeList}" 
		optionKey="id"
		optionValue="lastName" 
		value="${itineraryInstance.deliveryBoy.id}" 
		noSelection="${['':itineraryInstance?.deliveryBoy.lastName]}"
		class="many-to-one"/>		
	<g:message code="itinerary.delivery.boy.filter" default="Name"/>
	<g:if test="${checked}">
		<input id="checkBox" type="checkbox"  checked="checked"
			onclick="${
				remoteFunction(controller:'employee', 
				action:'expandList',
				update:'itineraryForm',
				onLoading:"document.getElementById('spinner').style.display = 'inline';",
				onComplete:"document.getElementById('spinner').style.display = 'none';",
				params:'   \'&value=\' + this.checked  '
				)}">
	</g:if>
	<g:else>
		<input id="checkBox" type="checkbox" 
			onclick="${
				remoteFunction(controller:'employee', 
				action:'expandList',
				update:'itineraryForm',
				onLoading:"document.getElementById('spinner').style.display = 'inline';",
				onComplete:"document.getElementById('spinner').style.display = 'none';",
				params:'   \'&value=\' + this.checked  '
				)}">
	</g:else>
</div>