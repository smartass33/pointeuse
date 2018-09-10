<%@ page import="pointeuse.Itinerary" %>

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
						<td>${theoriticalActionItem.date.format('kk:mm')}</td>
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