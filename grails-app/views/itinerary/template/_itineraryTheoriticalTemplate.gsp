<%@ page import="pointeuse.Itinerary" %>
<%@ page import="pointeuse.ItineraryNature" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>

<g:if test="${!siteTemplate}">
	<g:if test="${theoriticalActionsList != null }">
	
	<g:set var="nextActionItem" value="${theoriticalActionsList.get(1)}"/>
	<g:set var="hasNext" value="${true}"/>
	
		<h1><g:message code="itinerary.theoritical.label"/></h1>
	</g:if>
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<g:each in="${theoriticalActionsList}" var='actionItem' status="j">
					<td class="itineraryReportTD">
						${actionItem.site.name}
						<br> 
						<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
							<font color="red">${actionItem.date.format('kk:mm')} </font>
						</g:if>
						<g:else>
							<font color="green">${actionItem.date.format('kk:mm')} </font>
						</g:else>
					</td>
				</g:each>
			</tr>
		</tbody>
	</table>
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<g:each in="${theoriticalSaturdayActionsList}" var='actionItem' status="j">
					<td class="itineraryReportTD">
						${actionItem.site.name}
						<br> 
						<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
							<font color="red">${actionItem.date.format('kk:mm')} </font>
						</g:if>
						<g:else>
							<font color="green">${actionItem.date.format('kk:mm')} </font>
						</g:else>
					</td>			
				</g:each>
			</tr>
		</tbody>
	</table>
</g:if>
<g:else>
	<g:if test="${theoriticalActionsList != null }">
		<h1><g:message code="itinerary.theoritical.label"/> du site: ${site.name}</h1>
	</g:if>
		<table style="table-layout:fixed;">
		<tbody>
			<tr><td class="itineraryReportTD">${message(code: 'itinerary.weekday', default: 'Report')}</td>
				<g:each in="${theoriticalActionsList}" var='actionItem' status="j">
					<td class="itineraryReportTD">
						${actionItem.itinerary.name}<br>
						<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
							<font color="red">
						</g:if>
						<g:else>
							<font color="green">
						</g:else>		
						${actionItem.date.format('kk:mm')}</font>
					</td>					
				</g:each>
			</tr>
		</tbody>
	</table>
	
	<table style="table-layout:fixed;">
		<tbody>
			<tr>
				<td class="itineraryReportTD">${message(code: 'itinerary.saturday', default: 'Report')}</td>
				<g:each in="${theoriticalSaturdayActionsList}" var='actionItem' status="k">
					<td class="itineraryReportTD">
						${actionItem.itinerary.name}<br>
						<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
							<font color="red">
						</g:if>
						<g:else>
							<font color="green">
						</g:else>
						${actionItem.date.format('kk:mm')}</font>
					</td>
				</g:each>
			</tr>
		</tbody>
	</table>
</g:else>
