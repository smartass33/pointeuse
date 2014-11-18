<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>


<g:if test="${flash.message}">
	<div class="message" id="flash">
		${flash.message}
	</div>
</g:if>  

<table  style="width:90%;"class="table  site-table-header-rotated" >
	<BR>
	<thead>
		<th class='rotate-45'style="width:95px;"><div><span><g:message code="employee.site.label"/></span></div></th>	
		<th class='rotate-45' style="width:115px;"><div><span><g:message code="employee.label"/></span></div></th>	
		<th class='rotate-45' style="width:40px;"><div><span><g:message code="annual.report.working.days.label"/></span></div></th>			
		<th class='rotate-45' style="width:40px;"><div><span><g:message code="annual.report.working.days"/></span></div></th>			
		<th class='rotate-45' style="width:85px;"><div><span><g:message code="annual.report.theoritical.label"/></span></div></th>
		<th class='rotate-45' style="width:85px;"><div><span><g:message code="annual.report.elapsed.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;"><div><span><g:message code="annual.report.holidays.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;"><div><span><g:message code="annual.report.remaining.holidays.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;"><div><span><g:message code="annual.report.RTT.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;"><div><span><g:message code="annual.report.CSS.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;"><div><span><g:message code="annual.report.DIF.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;"><div><span><g:message code="annual.report.sickness.label"/></span></div></th>		
		<th class='rotate-45' style="width:45px;"><div><span><g:message code="employee.exceptionnel.count"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.HS.above.quota.short"/></span></div></th>
		<th class='rotate-45' style="width:85px;"><div><span><g:message code="annual.computed.quota.short"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.other.HS.quota.short"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.total.HS.to.pay"/></span></div></th>
	</thead>
	<tbody>
		<g:if test='${employeeList != null }'>
			<g:each in="${employeeList}"  status="i" var="employee">
				<tr>
					<g:if test="${site !=null}">
						<td>${site.name}</td>
					</g:if>
					<g:else>
						<td></td>
					</g:else>
					<td>${employee.lastName }</td>
					<td style="width:50px;">${(annualReportMap.get(employee)).get('annualWorkingDays')}</td>
					<td style="width:50px;">${(annualReportMap.get(employee)).get('annualEmployeeWorkingDays')}</td>
					<td style="text-align:right;">${(annualReportMap.get(employee)).get('annualTheoritical')}</td>
					<td style="text-align:right;">${(annualReportMap.get(employee)).get('annualTotal')}</td>
					<td style="width:45px;">${(annualReportMap.get(employee)).get('annualHoliday')}</td>
					<td style="width:45px;">${(annualReportMap.get(employee)).get('remainingCA')}</td>
					<td style="width:45px;">${(annualReportMap.get(employee)).get('annualRTT')}</td>
					<td style="width:45px;">${(annualReportMap.get(employee)).get('annualCSS')}</td>
					<td style="width:45px;">${(annualReportMap.get(employee)).get('annualDIF')}</td>
					<td style="width:45px;">${(annualReportMap.get(employee)).get('annualSickness')}</td>
					<td style="width:45px;">${(annualReportMap.get(employee)).get('annualExceptionnel')}</td>
					<td style="text-align:right;">${(annualReportMap.get(employee)).get('annualPayableSupTime')}</td>
					<td style="text-align:right;">${(annualReportMap.get(employee)).get('annualTheoriticalIncludingExtra')}</td>
					<td style="text-align:right;">${(annualReportMap.get(employee)).get('annualSupTimeAboveTheoritical')}</td>
					<td style="text-align:right;width:50px;">${(annualReportMap.get(employee)).get('annualGlobalSupTimeToPay')}</td>
				</tr>
			</g:each>
			<g:if test="${site !=null}">
				<tr style='font-weight: bold;'>
					<td><g:message code="annual.total"/></td>
					<td></td>
					<td>n/a</td>
					<td style="width:50px;">${siteAnnualEmployeeWorkingDays}</td>
					<td style="text-align:right;">${siteAnnualTheoritical}</td>
					<td style="text-align:right;">${siteAnnualTotal}</td>
					<td style="width:50px;">${siteAnnualHoliday}</td>
					<td style="width:50px;">${siteRemainingCA}</td>
					<td style="width:50px;">${siteAnnualRTT}</td>
					<td style="width:50px;">${siteAnnualCSS}</td>
					<td style="width:50px;">${siteAnnualDIF}</td>
					<td style="width:50px;">${siteAnnualSickness}</td>
					<td style="width:50px;">${siteAnnualExceptionnel}</td>
					<td style="text-align:right;">${siteAnnualPayableSupTime}</td>
					<td style="text-align:right;">${siteAnnualTheoriticalIncludingExtra}</td>
					<td style="text-align:right;">${siteAnnualSupTimeAboveTheoritical}</td>
					<td style="text-align:right;width:50px;">${siteAnnualGlobalSupTimeToPay}</td>
				</tr>	
			</g:if>				
		</g:if>
	</tbody>
</table>