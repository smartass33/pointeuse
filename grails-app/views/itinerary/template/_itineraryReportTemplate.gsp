<%@ page import="pointeuse.Itinerary" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>

<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>

<g:if test="${!hasDiscrepancy}">
	<g:if test="${actionsList != null && actionsList.size() > 0}">
		<g:mergedActionTable/>
	</g:if>
	<g:else>
		${message(code: 'itinerary.actionList.is.empty', default: 'Create')}
	</g:else>
</g:if>
<g:else>
	le nombre d'éléments enregistrés ne correspond pas aux nombres d'éléments de la tournée théorique. Veuillez corriger
	
	<g:if test="${actionsList != null && actionsList.size() > 0}">
		<div style="float:left;">
			<g:loggedActionTable/>
		</div>
	</g:if>
	<g:if test="${theoriticalActionsList != null && theoriticalActionsList.size() > 0}">
		<div id='theoriticalActionTableDiv' style="float:left;">
			<g:theoriticalActionTable/>
		</div>
	</g:if>
</g:else>