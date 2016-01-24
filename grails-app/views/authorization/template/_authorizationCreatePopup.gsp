<a href="#join_form" id="join_pop" class="addElementButton"><g:message code="add.authorization.label"/></a>
<a href="#x" class="overlay" id="join_form" style="background: transparent;"></a>
<div id="popup" class="popup">
	<h2><g:message code="authorization.create.label" /></h2>
	<p><g:message code="authorization.create.info" /></p>
	<g:form>		
		<fieldset class="form">
			<g:render template="/authorization/form"/>
		</fieldset>
		<g:hiddenField name="fromReport" value="${fromReport}" />
		<g:hiddenField name="fromEditEmployee" value="${fromEditEmployee}" />
		<g:hiddenField name="showEmployee" value="${showEmployee}" />	
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
