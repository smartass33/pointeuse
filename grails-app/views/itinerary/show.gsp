
<%@ page import="pointeuse.Itinerary" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'itinerary.label', default: 'Itinerary')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-itinerary" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-itinerary" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list itinerary">
				<g:if test="${itineraryInstance?.actions}">
					<li class="fieldcontain">
						<span id="actions-label" class="property-label"><g:message code="itinerary.actions.label" default="Actions" /></span>
						
							<g:each in="${itineraryInstance.actions}" var="a">
							<span class="property-value" aria-labelledby="actions-label"><g:link controller="action" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
							</g:each>
						
					</li>
				</g:if>
				<g:if test="${itineraryInstance?.creationDate}">

				</g:if>
			
				<g:if test="${itineraryInstance?.creationUser}">
				<li class="fieldcontain">
					<span id="creationUser-label" class="property-label"><g:message code="itinerary.creationUser.label" default="Creation User" /></span>
					
						<span class="property-value" aria-labelledby="creationUser-label">${itineraryInstance?.creationUser?.lastName} ${itineraryInstance?.creationUser?.firstName}</span>
					
				</li>
				</g:if>
			
				<g:if test="${itineraryInstance?.deliveryBoy}">
				<li class="fieldcontain">
					<span id="deliveryBoy-label" class="property-label"><g:message code="itinerary.deliveryBoy.label" default="Delivery Boy" /></span>
					
						<span class="property-value" aria-labelledby="deliveryBoy-label"><g:link controller="employee" action="show" id="${itineraryInstance?.deliveryBoy?.id}">${itineraryInstance?.deliveryBoy?.lastName} ${itineraryInstance?.deliveryBoy?.firstName}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${itineraryInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="itinerary.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${itineraryInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:itineraryInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${itineraryInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
