<div id="popup" class="popup">
	<h2>Creer Entrée/Sortie</h2>
	<p>Renseignez les informations pour creer un nouvel évènement</p>
	<g:form action="create">
		<table>
			<tbody>
				<tr class="prop">
					<td class="name" valign="top">choisissez la date:</td>
					<td class="value" valign="top"><input type="text"
						name="date_picker" id="date_picker" /></td>
				</tr>
				<tr class="prop">
					<td class="name" valign="top">Evènement:</td>
					<td class="value" valign="top"><g:select name="event.type"
							from="${['E','S']}" valueMessagePrefix="entry.name"
							noSelection="['':'-Choisissez votre élément-']" /></td>
				</tr>
				<tr class="prop">
					<td class="name" valign="top">Raison:</td>
					<td class="value" valign="top"><g:select name="reason.id"
							from="${Reason.list([sort:'name'])}"
							noSelection="['':'-Ajouter une raison-']" optionKey="id"
							optionValue="name" /></td>
				</tr>



			</tbody>
		</table>
		<g:hiddenField name="userId" value="${userId}" />

		<g:submitToRemote oncomplete="showSpinner(false)"
			onloading="showSpinner(true)" update="c" onSuccess="closePopup ();"
			url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
	</g:form>
	<a class="close" href="#close"></a>
</div>
