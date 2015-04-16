<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>


<g:if test="${flash.message}">
	<div class="message" id="flash">
		${flash.message}
	</div>
</g:if>  

<table  style="width:100%;"class="table  site-table-header-rotated" >
	<BR>
	<thead>
		<th class='rotate-45'style="width:95px;text-align:center;"><div><span><g:message code="employee.site.label"/></span></div></th>	
		<th class='rotate-45' style="width:115px;text-align:center;"><div><span><g:message code="employee.label"/></span></div></th>	
		<th class='rotate-45' style="width:40px;text-align:center;"><div><span><g:message code="annual.report.working.days.label"/></span></div></th>			
		<th class='rotate-45' style="width:40px;text-align:center;"><div><span><g:message code="annual.report.working.days"/></span></div></th>			
		<th class='rotate-45' style="width:80px;text-align:center;"><div><span><g:message code="annual.report.theoritical.label"/></span></div></th>
		<th class='rotate-45' style="width:80px;text-align:center;"><div><span><g:message code="annual.report.elapsed.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;text-align:center;"><div><span><g:message code="annual.report.holidays.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;text-align:center;"><div><span><g:message code="annual.report.remaining.holidays.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;text-align:center;"><div ><span><g:message code="annual.report.RTT.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;text-align:center;"><div><span><g:message code="annual.report.CSS.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;text-align:center;"><div><span><g:message code="annual.report.DIF.label"/></span></div></th>
		<th class='rotate-45' style="width:45px;text-align:center;"><div><span><g:message code="annual.report.sickness.label"/></span></div></th>		
		<th class='rotate-45' style="width:45px;text-align:center;"><div><span><g:message code="employee.exceptionnel.count"/></span></div></th>
		<th class='rotate-45' style="width:45px;text-align:center;"><div><span><g:message code="employee.paternite.count"/></span></div></th>
		<th class='rotate-45' style="width:60px;text-align:center;"><div><span><g:message code="annual.HS.above.quota.short"/></span></div></th>
		<th class='rotate-45' style="width:85px;text-align:center;"><div><span><g:message code="annual.computed.quota.short"/></span></div></th>
		<th class='rotate-45' style="width:60px;text-align:center;"><div><span><g:message code="annual.other.HS.quota.short"/></span></div></th>
		<th class='rotate-45' style="width:60px;text-align:center;"><div><span><g:message code="annual.total.HS.to.pay"/></span></div></th>
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
					<td style="width:45px;">${(annualReportMap.get(employee)).get('annualPaternite')}</td>
					<td style="text-align:right;width:50px;">${(annualReportMap.get(employee)).get('annualPayableSupTime')}</td>
					<td style="text-align:right;width:50px;"><my:humanTimeTD id="annualTheoriticalIncludingExtra"  name="annualTheoriticalIncludingExtra" value="${(annualReportMap.get(employee)).get('annualTheoriticalIncludingExtra')}"/></td>
					<td style="text-align:right;width:50px;"><my:humanTimeTD id="annualSupTimeAboveTheoritical"  name="annualSupTimeAboveTheoritical" value="${(annualReportMap.get(employee)).get('annualSupTimeAboveTheoritical')}"/></td>
					<td style="text-align:right;width:50px;"><my:humanTimeTD id="annualGlobalSupTimeToPay"  name="annualGlobalSupTimeToPay" value="${(annualReportMap.get(employee)).get('annualGlobalSupTimeToPay')}"/></td>
				</tr>
			</g:each>
			<g:if test="${site !=null}">
				<tr style='font-weight: bold;'>
					<td><g:message code="annual.total"/></td>
					<td></td>
					<td>n/a</td>
					<td style="width:50px;">${siteAnnualEmployeeWorkingDays}</td>
					<td style="text-align:right;"><my:humanTimeTD id="siteAnnualTheoritical"  name="siteAnnualTheoritical" value="${siteAnnualTheoritical}"/></td>
					<td style="text-align:right;"><my:humanTimeTD id="siteAnnualTotal"  name="siteAnnualTotal" value="${siteAnnualTotal}"/></td>
					<td style="width:50px;">${siteAnnualHoliday}</td>
					<td style="width:50px;">${siteRemainingCA}</td>
					<td style="width:50px;">${siteAnnualRTT}</td>
					<td style="width:50px;">${siteAnnualCSS}</td>
					<td style="width:50px;">${siteAnnualDIF}</td>
					<td style="width:50px;">${siteAnnualSickness}</td>
					<td style="width:50px;">${siteAnnualExceptionnel}</td>
					<td style="width:50px;">${siteAnnualPaternite}</td>
					<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualPayableSupTime"  name="siteAnnualPayableSupTime" value="${siteAnnualPayableSupTime}"/></td>
					<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualTheoriticalIncludingExtra"  name="siteAnnualTheoriticalIncludingExtra" value="${siteAnnualTheoriticalIncludingExtra}"/></td>
					<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualSupTimeAboveTheoritical"  name="siteAnnualSupTimeAboveTheoritical" value="${siteAnnualSupTimeAboveTheoritical}"/></td>
					<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualGlobalSupTimeToPay"  name="siteAnnualGlobalSupTimeToPay" value="${siteAnnualGlobalSupTimeToPay}"/></td>				
					</tr>	
			</g:if>				
		</g:if>
	</tbody>
</table>