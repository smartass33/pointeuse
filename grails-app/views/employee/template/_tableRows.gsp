<g:each in="${1..loopCount}" var="idx">
	<tr valign="top">
	    <th scope="row"><label for="contract"><g:message code="employee.weeklyContractTime.label" default="weeklyContractTime" /></label></th>
	    <td>
	    	<richui:dateChooser id="startDate" name="startDate"format="dd/MM/yyyy" value="${period ? period : new Date()}" locale="fr" firstDayOfWeek="Mo"/>
	        <input type="number" class="code" id="contractTime" name="contractTime" value="" placeholder="35" /> &nbsp;
	    </td>
	</tr>
</g:each>