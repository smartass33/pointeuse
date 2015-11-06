<%@ page import="pointeuse.AuthorizationType"%>


<a href="#join_form" id="join_pop" class="addElementButton"><g:message code="add.authorization.label"/></a>
<a href="#x" class="overlay" id="join_form" style="background: transparent;"></a>
<div id="popup" class="popup">
	<h2>Creer une habilitation</h2>
	<p>Renseignez les informations pour créer une nouvelle habilitation</p>
	<g:form action="create">		
		<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'startDate', 'error')} required">
			<label for="startDate">
				<g:message code="authorization.startDate.label" default="Start Date" />
				<span class="required-indicator">*</span>
			</label>
			<g:datePicker name="startDate" precision="day"  value="${authorizationInstance?.startDate}"  relativeYears="[-10..20]" />
		</div>
		<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'endDate', 'error')} required">
			<label for="endDate">
				<g:message code="authorization.endDate.label" default="End Date" />
				<span class="required-indicator">*</span>
			</label>
			<g:datePicker name="endDate" precision="day"  value="${authorizationInstance?.endDate}"  relativeYears="[-10..20]"/>
		</div>
		<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'isAuthorized', 'error')} ">
			<label for="isAuthorized">
				<g:message code="authorization.isAuthorized.label" default="Is Authorized" />		
			</label>
			<g:checkBox name="isAuthorized" value="${authorizationInstance?.isAuthorized}" />
		</div>
		<div class="fieldcontain ${hasErrors(bean: authorizationInstance, field: 'type', 'error')} required">
			<label for="type">
				<g:message code="authorization.type.label" default="Type" />
				<span class="required-indicator">*</span>
			</label>
			<g:select id="type" name="type" from="${AuthorizationType.findAll()}" optionKey="id" required="" optionValue="type" value="${authorizationTypeInstance?.nature?.id}" class="many-to-one"         />
			
		</div>
		<g:hiddenField name="fromReport" value="${fromReport}" />
		<g:hiddenField name="employeeInstanceId" value="${employeeInstanceId}" />
		<g:submitToRemote class="listButton"
			onLoading="document.getElementById('spinner').style.display = 'inline';"
            onComplete="document.getElementById('spinner').style.display = 'none';"						
			update="authorizationDiv"
			onSuccess="closePopup()"
			url="[controller:'authorization', action:'save']" value="Creer">
		</g:submitToRemote>
	</g:form>
	<a class="close" id="closeId" href="#close"></a>
</div>
