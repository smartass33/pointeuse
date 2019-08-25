<%@ page import="pointeuse.Itinerary" %>

<g:set var="realCal" 		value="${Calendar.instance}"/>
<g:set var="theoriticalCal" value="${Calendar.instance}"/>

<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>

<div id="theoriticalTableDiv">
	<g:itineraryTheoriticalTemplate/>
</div>
<BR><BR>
<div id="realActionsDiv">
	<g:loggedActionTable/>
</div>
