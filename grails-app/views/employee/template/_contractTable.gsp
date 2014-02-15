<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Service" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.Function" %>

<div>
<script>
$(document).ready(function(){
    $("#addCF").click(function(){
		$("#customFields").append('<tr valign="top"><th scope="row"><label for="previousContract">contrat précédent</label></th><td><input type="text" class="code" id="datepicker" name="contract_info" value="" /> &nbsp; <input type="number" class="code" name="contract_info" value="" placeholder="Input Value" /> &nbsp;</td> <td><a href="javascript:void(0);" class="remCF">Annuler</a> &nbsp;<input type="submit" class="listButton" value="Confirmer l\'ajout" name="_action_someAction"></td></tr>');
		$("#datepicker").datepicker();
     });
    $("#customFields").on('click', '.remCF', function(){
        $(this).parent().parent().remove();
    });
});  
</script>

<table class="form-table" id="customFields">
	<g:form method="POST" url="[controller:'employee', action:'someAction']">
		<g:each in="${previousContracts}"  status="i" var="previousContract">
			<tr valign="top">
			    <th scope="row"><label for="previousContract">contrat précédent</label></th>
			    <td><richui:dateChooser id="previousDate" name="previousDate" format="dd/MM/yyyy" value="${previousContract ? previousContract.date : new Date()}" locale="fr" firstDayOfWeek="Mo"/></td>
			    <td><input type="number" class="code" id="previousContractLength" name="previousContractLength" value="${previousContract ? previousContract.weeklyLength : 35}" /> &nbsp;</td>
			</tr>
		</g:each>
		<tr>
			<td><a href="javascript:void(0);" id="addCF">Ajouter de nouvelles valeurs de contrat</a> &nbsp;</td>     	
		</tr>		
	</g:form>
</table>


</div>