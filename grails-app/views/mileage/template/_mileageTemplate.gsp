<%@ page import="pointeuse.Mileage"%>
<%@ page import="pointeuse.Employee"%>

<g:set var="calendar" value="${Calendar.instance}"/>
<g:set var="minCal" value="${Calendar.instance}"/>
<g:set var="maxCal" value="${Calendar.instance}"/>

<g:if test="${ period != null}">
	<g:set var="maxYear" value="${period.year}"/>
	<g:set var="minYear" value="${period.year}"/>
	
</g:if>
<g:else>
	<g:set var="maxYear" value="${calendar.get(Calendar.YEAR)}"/>
	<g:set var="minYear" value="${calendar.get(Calendar.YEAR)}"/>
	
</g:else>
<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>
<form method="POST" >
	<g:actionSubmit value="appliquer" action="modifyMileage" class="listButton" style="position: absolute; left: -9999px" />
	<table border="1" style="table-layout: fixed;width:100%;"  >
		<thead>
			<th class="eventTD" style="vertical-align: middle;text-align: left;width:120px;"/>
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
				<th class="eventTD" style="width:65px">
					<% calendar.set(Calendar.MONTH,month_th - 1) 
						if (month_th == 1){
							maxYear +=1	
							minYear +=1
						}
						
						
						minCal.set(Calendar.MONTH,month_th - 1)
						minCal.set(Calendar.DAY_OF_MONTH,21)
						minCal.set(Calendar.YEAR,minYear)
						
						maxCal.set(Calendar.MONTH,month_th)
						maxCal.set(Calendar.DAY_OF_MONTH,20)
						maxCal.set(Calendar.YEAR,maxYear)
						
						
					%>
					${minCal.time.format('dd/MM/yy')} <BR> ${maxCal.time.format('dd/MM/yy')}
				</th>
			</g:each>		
			<th class="eventTD" style="vertical-align: middle;text-align: center;width:50px;">total</th>
			
		</thead>
		<tbody id='body_update' style="border:1px;">
			<g:each in="${employeeInstanceList}" var='employee'>
				<g:set var="total" value="${0 as long}"/>
				<g:hiddenField name="siteId" value="${employee.site.id}" /> 				
				<tr style="height:25px;">			
					<td style="vertical-align: middle;text-align: left;width:120px;" class="eventTD" ><g:link style="text-decoration: none;" controller="employee" action='annualReport'  id="${employee.id}" params="${[userId:employee?.id,siteId:siteId,isAjax:false,periodId:periodId]}">${employee.lastName} ${employee.firstName}</g:link></td>
					<g:each in="${mileageMapByEmployee.get(employee)}" var='mileageMap'>
					   <g:hiddenField name="mileage" value="${mileageMap.value}" /> 
	                    <g:hiddenField name="periodList" value="${period.year}" /> 
	                    <% 
							def counter = 5 
						%>
						<g:each in="${mileageMap}" var="mileage" >
						         <% 
								 	counter = counter + 1;
								 	total += mileage.value as long
								  %>
							<td style="vertical-align: middle;text-align:center;" class="eventTD">								
								${mileage.value ?: '0'}
							</td>						
						</g:each>
					</g:each>
					<td style="vertical-align: middle;text-align:center;" class="eventTD">${total}</td>
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
