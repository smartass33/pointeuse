<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>



<g:if test="${flash.message}">
	<div class="message" id="flash">
		${flash.message}
	</div>
</g:if>  

<div id="general">
	<table  style="width:20%; float: left;" class="table  site-table-header-rotated" >
		<thead>
			<th class='rotate-45'style="width:90px;text-align:center;"><div><span><g:message code="employee.site.label"/></span></div></th>	
			<th class='rotate-45' style="width:100px;text-align:center;"><div><span><g:message code="employee.label"/></span></div></th>	
			<th class='rotate-45' style="width:40px;text-align:center;"><div><span><g:message code="annual.report.working.days.label"/></span></div></th>			
			<th class='rotate-45' style="width:40px;text-align:center;"><div><span><g:message code="annual.report.working.days"/></span></div></th>			
		</thead>
		<tbody>
			<g:if test='${employeeList != null }'>
				<g:each in="${employeeList}" status="i" var="employee">
					<tr>
						<g:if test="${site != null}">
							<td class="cartoucheRightTitleTD">${site.name}</td>
						</g:if>
						<g:else>
							<td></td>
						</g:else>
						<td class="cartoucheRightTitleTD">${employee.lastName}</td>
						<td style="width:50px;" class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('yearOpenDays')}</g:if></td>
						<td style="width:50px;" class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualEmployeeWorkingDays')}</g:if></td>
					</tr>
				</g:each>			
			</g:if>
			<g:if test="${site !=null}">
				<tr style='font-weight: bold;'>
					<td class="cartoucheRightTitleTD"><g:message code="annual.total"/></td>
					<td></td>
					<td class="cartoucheRightTitleTD">n/a</td>
					<td class="cartoucheRightTitleTD">n/a</td>			
				</tr>
			</g:if>
		</tbody>
	</table>
</div>
<div id="details">
	<g:siteDetailTable/>
</div>
