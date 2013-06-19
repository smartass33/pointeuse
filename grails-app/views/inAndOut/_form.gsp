<%@ page import="pointeuse.InAndOut" %>
<%@ page import="pointeuse.Reason" %>

<div class="fieldcontain ${hasErrors(bean: inAndOutInstance, field: 'timeOut', 'error')} required">
	<joda:timePicker name="myTime" value="${inAndOutInstance?.time}" precision="minute" />
</div>


<div class="fieldcontain ${hasErrors(bean: inAndOutInstance, field: 'timeOut', 'error')} required">
		<g:select name="event.type" from="${['E','S']}" valueMessagePrefix="entry.name" value="${inAndOutInstance?.type}" noSelection="['':'-Choisissez votre élément-']"/>	
</div>

<div class="fieldcontain ${hasErrors(bean: inAndOutInstance, field: 'reason', 'error')} required">
			<g:select name="reason.id"
	          from="${Reason.list([sort:'name'])}"
	          noSelection="['':'-Ajouter une raison-']"          
	          optionKey="id" optionValue="name"
	          />
</div>

