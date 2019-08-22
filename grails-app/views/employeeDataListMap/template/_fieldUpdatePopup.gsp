<a href="#join_form_${fieldName}" id="join_pop_${fieldName}" style="text-decoration: none;">${fieldName}</a>
<a href="#x_${fieldName}" class="overlay" id="join_form_${fieldName}" style="background: transparent;"></a>
<div id="popup_${fieldName}" class="popup">
	<h2>Modifier le champ</h2>
	<p>Renseignez les informations pour modifier le champ</p>
	<g:form action="create" >
		<table>
			<tbody>
				<tr class="prop">
					<td class="eventTD" valign="top">Nom du champ:</td>
					<td class="eventTD" valign="top"><g:textField name="fieldName" value="${fieldName}"/></td>
				</tr>

				<tr class="prop">
					<td class="eventTD" valign="top">Type:</td>
					<td class="eventTD" valign="top">
						<g:select name="type" from="${EmployeeDataType.values()}"  value="${type}" noSelection="${['-':type]}"/>
					</td>
				</tr>
			</tbody>
		</table>
		<g:hiddenField name="oldFieldName" value="${fieldName}" />
		<g:submitToRemote class="listButton"
			onLoading="document.getElementById('spinner').style.display = 'inline';"
                		onComplete="document.getElementById('spinner').style.display = 'none';"						
			update="dataListTableDiv"
			onSuccess="closePopup()"
			url="[controller:'employeeDataListMap', action:'modify']" value="Modifier"></g:submitToRemote>
	</g:form>
	<a class="close" id="closeId" href="#close"></a>
</div>
