<%@ page import="pointeuse.Itinerary" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>

<table style="float: left;">
	<thead>
		<th>${message(code: 'action.nature.label')}</th>
		<th>${message(code: 'laboratory.label')}</th>
		<th>${message(code: 'action.time.real')}</th>
	</thead>
	<tbody>
		<g:each in="${actionsList}" var='actionItem' status="j">
			<tr class="${(j % 2) == 0 ? 'even' : 'odd'}">
				<td>${actionItem.nature}</td>
				<td>${actionItem.site.name}</td>
				<td>${actionItem.date.format('kk:mm')}</td>
			</tr>
		</g:each>
	</tbody>
</table>
