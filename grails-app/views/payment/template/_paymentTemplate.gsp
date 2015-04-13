<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>

<g:set var="calendar" value="${Calendar.instance}"/>
<g:if test="${ period != null}">
	<g:set var="currentYear" value="${period.year}"/>
</g:if>
<g:else>
	<g:set var="currentYear" value="${calendar.get(Calendar.YEAR)}"/>
</g:else>
<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>
<form method="POST" >
	<g:actionSubmit value="appliquer" action="modifyPayment" class="listButton" style="position: absolute; left: -9999px" />
	<table border="1" style="table-layout: fixed;width:100%;"  >
		<thead>
			<th class="eventTD" style="vertical-align: middle;text-align: left;width:120px;"/>
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
				<th class="eventTD" style="width:50px">
					<% calendar.set(Calendar.MONTH,month_th - 1) 
						if (month_th == 1){currentYear +=1}
					%>
					${calendar.time.format('MMM')} ${currentYear}
				</th>
			</g:each>		
		</thead>
		<tbody id='body_update' style="border:1px;">
			<g:each in="${employeeInstanceList}" var='employee'>
				<g:hiddenField name="siteId" value="${employee.site.id}" /> 				
				<tr style="height:25px;">
					<td style="vertical-align: middle;text-align: left;width:120px;" class="eventTD" >${employee.lastName} ${employee.firstName}</td>
					<g:each in="${paymentMapByEmployee.get(employee)}" var='paymentMap'>
					   <g:hiddenField name="payment" value="${paymentMap.value}" /> 
	                    <g:hiddenField name="periodList" value="${currentYear}" /> 
						<g:each in="${paymentMap}" var="payment" >		
							<g:hiddenField name="paymentIds" value="${(paymentIDMapByEmployee.get(employee)).get(payment.key)}" />                    
							<td style="vertical-align: middle;text-align:center;" class="eventTD" >
								<my:humanTimeTextField id="myinput" type = "text" name = "textField" value="${payment.value}"/>
							</td>						
						</g:each>
					</g:each>
				</tr>
			</g:each>
		</tbody>
	</table>
</form>
<g:if test="${employeeInstanceTotal!=null && employeeInstanceTotal>20}">
	<div class="pagination" id="pagination">
		<g:hiddenField name="isAdmin" value="${isAdmin}" />
		<g:paginate total="${employeeInstanceTotal}" params="${[isAdmin:isAdmin]}" />
	</div>
</g:if>
