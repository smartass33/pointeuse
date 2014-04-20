<%@ page import="pointeuse.Reason"%>

<a href="#join_form" id="join_pop" class="addElementButton">Ajouter un élement</a>
<a href="#x" class="overlay" id="join_form" style="background: transparent;"></a>
<div id="popup" class="popup">
	<h2>Creer Entrée/Sortie</h2>
	<p>Renseignez les informations pour créer un nouvel évènement</p>
	<g:form action="create">
		<table>
			<tbody>
				<tr class="prop">
					<td class="eventTD" valign="top">choisissez la date:</td>
					<td class="eventTD" valign="top"><input type="text" name="date_picker" id="date_picker" /> 
						<script type="text/javascript">
							datePickerLaunch();
						</script>
					</td>
				</tr>
				<tr class="prop">
					<td class="eventTD" valign="top">Evènement:</td>
					<td class="eventTD" valign="top">
						<g:select
							name="event.type" from="${['E','S']}"
							valueMessagePrefix="entry.name"
							noSelection="['':'-Choisissez votre élément-']" />
					</td>
				</tr>
				<tr class="prop">
					<td class="eventTD" valign="top">Raison:</td>
					<td class="eventTD" valign="top">
						<g:select name="reason.id"
							from="${Reason.list([sort:'name'])}"
							noSelection="['':'-Ajouter une raison-']" optionKey="id"
							optionValue="name" />
					</td>
				</tr>
			</tbody>
		</table>
		<g:hiddenField name="userId" value="${userId}" />
		<g:hiddenField name="fromReport" value="${true}" />
		<g:submitToRemote class="listButton"
			onLoading="document.getElementById('spinner').style.display = 'inline';"
                		onComplete="document.getElementById('spinner').style.display = 'none';"						
			update="report_table_div"
			onSuccess="closePopup()"
			url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
	</g:form>
	<a class="close" id="closeId" href="#close"></a>
</div>
