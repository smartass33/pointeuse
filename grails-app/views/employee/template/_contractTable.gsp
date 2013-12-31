<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Service" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.Function" %>

<div>
<script>
$(document).ready(function(){
    $("#addCF").click(function(){
		$("#customFields").append('<tr valign="top"><th scope="row"><label for="customFieldName">Custom Field</label></th><td><input type="text" class="code" id="datepicker" name="contract_info" value="" /> &nbsp; <input type="number" class="code" name="contract_info" value="" placeholder="Input Value" /> &nbsp; <a href="javascript:void(0);" class="remCF">Remove</a> &nbsp;<input type="submit" class="listButton" value="export PDF" name="_action_someAction"></td></tr>');
     	
       // $( "#datepicker" ).datepicker();

$("#datepicker").datepicker();

     });

    $("#customFields").on('click', '.remCF', function(){
        $(this).parent().parent().remove();
    });

});


</script>

<table class="form-table" id="customFields">
<g:form method="POST"
					url="[controller:'employee', action:'someAction']">
<tr valign="top">
    <th scope="row"><label for="customFieldName"><g:message code="employee.weeklyContractTime.label" default="weeklyContractTime" /></label></th>
    <td>
    	<richui:dateChooser id="customFieldName" name="customFieldName[]" format="dd/MM/yyyy" value="${employeeInstance ? employeeInstance.arrivalDate : new Date()}" locale="fr" firstDayOfWeek="Mo"/>
        <input type="number" class="code" id="customFieldName" name="customFieldName[]" value="${employeeInstance ? employeeInstance.weeklyContractTime : 35}" /> &nbsp;
        <a href="javascript:void(0);" id="addCF">Add</a> &nbsp;
        
    </td>
</tr>
</g:form>

</table>


</div>