<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Period" %>

<div id='absenceStatus'>
	
	
	<h1>
		<g:if test="${employeeInstance == null}">
			${message(code: 'sickness.total.label', default: 'Period')}
		</g:if>
		<g:else>
			${message(code: 'sickness.total.label.for', default: 'Period')} ${employeeInstance.firstName} ${employeeInstance.lastName}
		</g:else>
	</h1>	
	
	<script>		
	$(document).ready(
    function() {

        $("#sickStartDate").datepicker();
		$("#sickEndDate").datepicker();
        
    });
	</script>

	<g:if test="${flash.message3}">
		<div class="message" role="status">${flash.message3}</div>
	</g:if>


	<g:form method="POST" url="[controller:'employee', action:'addSickLeave']" >
		<div id="sickLeaveId">
			<table class="form-table" id="sickLeaveFields">
				<tbody>
			    	<tr id="newSickLeaveForm">
			    		<td><input type="text" class="code" id="sickStartDate"  value="" name="sickStartDate" /></td>
			    		<td><input type="text" class="code" id="sickEndDate"    value="" name="sickEndDate"   /></td> 	    		
			    		<td><input type="submit" class="listButton" value="Ajouter" name="addSickLeave"></td>
			    	</tr>
				</tbody>		
			</table>
			
			

			<g:if test="${leaveList != null && leaveList.size() > 0}">	
				<table class="form-table" id="sickLeaveTable">
					<thead>
						<th>${message(code: 'sickness.start.date', default: 'Period')}</th>
						<th>${message(code: 'sickness.end.date', default: 'Period')}</th>
						<th>${message(code: 'sickness.period.count', default: 'Period')}</th>
					</thead>
					
						<tbody>	
							<g:each in="${leaveList}" status="j" var="leaveCouple">
								<tr class="${(j % 2) == 0 ? 'even' : 'odd'}">
									<td>${leaveCouple.get(0).date.format('dd/MM/yyyy')}</td>
									<td>${leaveCouple.get(1).date.format('dd/MM/yyyy')}</td>
									<td style="text-align:center;">${(leaveCouple.get(1).date - leaveCouple.get(0).date) + 1}</td>
								</tr>
							</g:each>
						</tbody>
				</table>
			</g:if>
			<g:else>
				<div style='text-transform:uppercase'>${message(code: 'sickness.no.input', default: 'Period')}</div>
			</g:else>
		</div>
		<g:hiddenField name="isAdmin" value="${isAdmin}" />
		<g:hiddenField name="siteId" value="${siteId}" />		
		<g:hiddenField name="periodId" value="${periodId}" />			
		<g:hiddenField name="employeeId" value="${employeeId}" />			
	</g:form>
</div>