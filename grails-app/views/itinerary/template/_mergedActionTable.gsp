<%@ page import="pointeuse.Itinerary" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>

<table>
	<thead>
		<th>${message(code: 'action.nature.label')}</th>
		<th>${message(code: 'laboratory.label')}</th>
		<th>${message(code: 'action.time.theoritical')}</th>
		<th>${message(code: 'action.time.real')}</th>
	</thead>
	<tbody>
		<g:each in="${actionsList}" var='actionItem' status="i">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td>${actionItem.nature}</td>
				<td>${actionItem.site.name}</td>
				<td>${theoriticalActionsList.get(i).date.format('kk:mm')}</td>
				<g:if test="${(timeDiffMap.get(i).minutes * 60 + timeDiffMap.get(i).minutes) > -(30 * 60)}">
					<td bgcolor="98FB98">${actionItem.date.format('kk:mm')}</td>
				</g:if>
				<g:else>
					<td bgcolor="#FFC0CB">${actionItem.date.format('kk:mm')}</td>				
				</g:else>
			</tr>
		</g:each>
	</tbody>
</table>