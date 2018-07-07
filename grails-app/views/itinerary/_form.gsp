<%@ page import="pointeuse.Itinerary" %>


<div class="fieldcontain ${hasErrors(bean: itineraryInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="itinerary.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${itineraryInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: itineraryInstance, field: 'deliveryBoy', 'error')} required">
	<label for="deliveryBoy">
		<g:message code="itinerary.deliveryBoy.label" default="Delivery Boy" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="deliveryBoy" name="deliveryBoy.id" from="${pointeuse.Employee.list()}" optionKey="id" optionValue="lastName" required="" value="${itineraryInstance?.deliveryBoy?.id}" class="many-to-one"/>
</div>




<div class="fieldcontain ${hasErrors(bean: itineraryInstance, field: 'actions', 'error')} ">
	<label for="actions">
		<g:message code="itinerary.actions.label" default="Actions" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${itineraryInstance?.actions?}" var="a">
    <li><g:link controller="action" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="action" action="create" params="['itinerary.id': itineraryInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'action.label', default: 'Action')])}</g:link>
</li>
</ul>

</div>