<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Employee"%>
		<%@ page import="pointeuse.InAndOut"%>
		<%@ page import="pointeuse.AbsenceType"%>
		<%@ page import="pointeuse.MonthlyTotal"%>
		<style  type="text/css">
			@page {
			   size: 297mm 210mm;
			   margin: 0px 0px 13px 0px;
			 }
				table {
			  font: normal 11px verdana, arial, helvetica, sans-serif;
			  color: #363636;
			  background: #f6f6f6;
			  background-color: white;
			  text-align:right;
			  }
		
		</style>	
	</head>
	<body>
	<h1 style="text-align:center;font-size:150%">
		<g:if test="${site != null}"><g:message code="site.followup"/> ${site.name} ${period2.year} / ${period2.year + 1}</g:if>
		<g:else>Suivi global ${period2.year} / ${period2.year + 1}</g:else>	
	</h1>
		<table id="annual-report-table" style="border:1px solid black;font-weight:bold;">
			<BR></BR>
			<thead>
				<th  style="width:120px;"><div><span><g:message code="employee.site.label"/></span></div></th>	
				<th  style="width:120px;"><div><span><g:message code="employee.label"/></span></div></th>	
				<th  style="width:80px;"><div><span><g:message code="annual.report.working.days.label"/></span></div></th>			
				<th  style="width:80px;"><div><span><g:message code="annual.report.working.days"/></span></div></th>			
				<th  style="width:120px;"><div><span><g:message code="annual.report.theoritical.label"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.report.elapsed.label"/></span></div></th>
				<th  style="width:80px;"><div><span><g:message code="annual.report.holidays.label"/></span></div></th>
				<th  style="width:80px;"><div><span><g:message code="annual.report.remaining.holidays.label"/></span></div></th>
				<th  style="width:80px;"><div><span><g:message code="annual.report.RTT.label"/></span></div></th>
				<th  style="width:80px;"><div><span><g:message code="annual.report.CSS.label"/></span></div></th>
				<th  style="width:80px;"><div><span><g:message code="annual.report.DIF.label"/></span></div></th>	
				<th  style="width:80px;"><div><span><g:message code="annual.report.sickness.label"/></span></div></th>		
				<th  style="width:80px;"><div><span><g:message code="employee.exceptionnel.count"/></span></div></th>
				<th  style="width:80px;"><div><span><g:message code="employee.paternite.count"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.HS.above.quota.short"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.computed.quota.short"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.other.HS.quota.short"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.total.HS.to.pay"/></span></div></th>
			</thead>
			<tbody>
					<g:if test='${employeeList != null }'>
					<g:each in="${employeeList}"  status="i" var="employee">
						<tr>
							<g:if test="${site !=null}">
								<td class="cartoucheRightTitleTD">${site.name}</td>
							</g:if>
							<g:else>
								<td></td>
							</g:else>
							<td>${employee.lastName}</td>
							<g:if test="${annualReportMap.get(employee) != null}">
								<td style="width:50px;">${(annualReportMap.get(employee)).get('annualWorkingDays')}</td>
								<td style="width:50px;">${(annualReportMap.get(employee)).get('annualEmployeeWorkingDays')}</td>
								<td style="text-align:right;" ><my:humanTimeTD id="annualReportMap"  name="annualReportMap" value="${(annualReportMap.get(employee)).get('annualReportMap')}"/></td>
								<td style="text-align:right;"><my:humanTimeTD id="annualTotal"  name="annualTotal" value="${(annualReportMap.get(employee)).get('annualTotal')}"/></td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('annualHoliday')}</td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('remainingCA')}</td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('annualRTT')}</td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('annualCSS')}</td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('annualDIF')}</td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('annualSickness')}</td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('annualExceptionnel')}</td>
								<td style="width:45px;text-align:center;">${(annualReportMap.get(employee)).get('annualPaternite')}</td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="annualPayableSupTime"  name="annualPayableSupTime" value="${(annualReportMap.get(employee)).get('annualPayableSupTime')}"/></td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="annualTheoriticalIncludingExtra"  name="annualTheoriticalIncludingExtra" value="${(annualReportMap.get(employee)).get('annualTheoriticalIncludingExtra')}"/></td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="annualSupTimeAboveTheoritical"  name="annualSupTimeAboveTheoritical" value="${(annualReportMap.get(employee)).get('annualSupTimeAboveTheoritical')}"/></td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="annualGlobalSupTimeToPay"  name="annualGlobalSupTimeToPay" value="${(annualReportMap.get(employee)).get('annualGlobalSupTimeToPay')}"/></td>
							</g:if>
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
								<td style="width:50px;text-align:center;">${siteAnnualHoliday}</td>
								<td style="width:50px;text-align:center;">${siteRemainingCA}</td>
								<td style="width:50px;text-align:center;">${siteAnnualRTT}</td>
								<td style="width:50px;text-align:center;">${siteAnnualCSS}</td>
								<td style="width:50px;text-align:center;">${siteAnnualDIF}</td>
								<td style="width:50px;text-align:center;">${siteAnnualSickness}</td>
								<td style="width:50px;text-align:center;">${siteAnnualExceptionnel}</td>
								<td style="width:50px;text-align:center;">${siteAnnualPaternite}</td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualPayableSupTime"  name="siteAnnualPayableSupTime" value="${siteAnnualPayableSupTime}"/></td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualTheoriticalIncludingExtra"  name="siteAnnualTheoriticalIncludingExtra" value="${siteAnnualTheoriticalIncludingExtra}"/></td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualSupTimeAboveTheoritical"  name="siteAnnualSupTimeAboveTheoritical" value="${siteAnnualSupTimeAboveTheoritical}"/></td>
								<td style="text-align:right;width:50px;"><my:humanTimeTD id="siteAnnualGlobalSupTimeToPay"  name="siteAnnualGlobalSupTimeToPay" value="${siteAnnualGlobalSupTimeToPay}"/></td>				
								</tr>	
						</g:if>							
					</g:if>
				</tbody>
		</table>
	</body>
</html>