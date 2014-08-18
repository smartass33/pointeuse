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
				<th  style="width:80px;"><div><span><g:message code="annual.report.sickness.label"/></span></div></th>		
				<th  style="width:80px;"><div><span><g:message code="employee.exceptionnel.count"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.HS.above.quota.short"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.computed.quota.short"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.other.HS.quota.short"/></span></div></th>
				<th  style="width:120px;"><div><span><g:message code="annual.total.HS.to.pay"/></span></div></th>
			</thead>
			<tbody>
				
				<g:each in="${employeeList}"  status="i" var="employee">
					<tr>
						<g:if test="${site !=null}">
							<td style="width:120px;">${site.name}</td>
						</g:if>
						<g:else>
							<td style="width:120px;"></td>
						</g:else>
						<td style="width:120px;">${employee.lastName }</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('annualWorkingDays')}</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('annualEmployeeWorkingDays')}</td>
						<td>${(annualReportMap.get(employee)).get('annualTheoritical')}</td>
						<td>${(annualReportMap.get(employee)).get('annualTotal')}</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('annualHoliday')}</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('remainingCA')}</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('annualRTT')}</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('annualCSS')}</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('annualSickness')}</td>
						<td style="width:80px;">${(annualReportMap.get(employee)).get('annualExceptionnel')}</td>
						<td style="width:120px;">${(annualReportMap.get(employee)).get('annualPayableSupTime')}</td>
						<td style="width:120px;">${(annualReportMap.get(employee)).get('annualTheoriticalIncludingExtra')}</td>
						<td style="width:120px;">${(annualReportMap.get(employee)).get('annualSupTimeAboveTheoritical')}</td>
						<td style="width:120px;">${(annualReportMap.get(employee)).get('annualGlobalSupTimeToPay')}</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</body>
</html>