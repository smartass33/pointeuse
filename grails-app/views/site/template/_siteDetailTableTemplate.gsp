<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>

<g:if test="${flash.message}">
	<div class="message" id="flash">
		${flash.message}
	</div>
</g:if>  
<div id="details">
	<table  style="width:65%; float: right;"class="table  site-table-header-rotated" >
		<thead>
			<th class='rotate-45' style="width:76px;text-align:center;"><div><span><g:message code="annual.report.theoritical.label"/></span></div></th>
			<th class='rotate-45' style="width:76px;text-align:center;"><div><span><g:message code="annual.report.elapsed.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.holidays.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.remaining.holidays.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div ><span><g:message code="annual.report.RTT.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div ><span><g:message code="annual.report.GARDE_ENFANT.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div ><span><g:message code="annual.report.CHOMAGE.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.CSS.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.INJUSTIFIE.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.MISE_A_PIED.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.DIF.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.DON.label"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="annual.report.sickness.label"/></span></div></th>		
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="employee.exceptionnel.count"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="employee.paternite.count"/></span></div></th>
			<th class='rotate-45' style="width:43px;text-align:center;"><div><span><g:message code="employee.parental.count"/></span></div></th>
			<th class='rotate-45' style="width:55px;text-align:center;"><div><span><g:message code="annual.HS.above.quota.short"/></span></div></th>
			<th class='rotate-45' style="width:79px;text-align:center;"><div><span><g:message code="annual.computed.quota.short"/></span></div></th>
			<th class='rotate-45' style="width:57px;text-align:center;"><div><span><g:message code="annual.other.HS.quota.short"/></span></div></th>
			<th class='rotate-45' style="width:57px;text-align:center;"><div><span><g:message code="annual.total.HS.to.pay"/></span></div></th>
		</thead>
		<tbody>
			<g:if test='${employeeList != null }'>
				<g:each in="${employeeList}"  status="i" var="employee">
					<tr>
						<td style="text-align:right;" class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}"><my:humanTimeTD id="annualTheoritical"  name="annualTheoritical" value="${(annualReportMap.get(employee)).get('annualTheoritical')}"/></g:if></td>
						<td style="text-align:right;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}"><my:humanTimeTD id="annualTotal"  name="annualTotal" value="${(annualReportMap.get(employee)).get('annualTotal')}"/></g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualHoliday')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('remainingCA')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualRTT')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualGarde_enfant')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualChomage')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualCSS')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualINJUSTIFIE')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualMISE_A_PIED')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualDIF')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualDON')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualSickness')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualExceptionnel')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualPaternite')}</g:if></td>
						<td style="width:43px;text-align:center;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}">${(annualReportMap.get(employee)).get('annualParental')}</g:if></td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}"><my:humanTimeTD id="annualPayableSupTime"  name="annualPayableSupTime" value="${(annualReportMap.get(employee)).get('annualPayableSupTime')}"/></g:if></td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}"><my:humanTimeTD id="annualTheoriticalIncludingExtra"  name="annualTheoriticalIncludingExtra" value="${(annualReportMap.get(employee)).get('annualTheoriticalIncludingExtra')}"/></g:if></td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}"><my:humanTimeTD id="annualSupTimeAboveTheoritical"  name="annualSupTimeAboveTheoritical" value="${(annualReportMap.get(employee)).get('annualSupTimeAboveTheoritical')}"/></g:if></td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><g:if test="${annualReportMap != null}"><my:humanTimeTD id="annualGlobalSupTimeToPay"  name="annualGlobalSupTimeToPay" value="${(annualReportMap.get(employee)).get('annualGlobalSupTimeToPay')}"/></g:if></td>
					</tr>
				</g:each>
				<g:if test="${site != null}">
					<tr style='font-weight: bold;'>
						<td style="text-align:right;" class="cartoucheRightTitleTD"><my:humanTimeTD id="siteAnnualTheoritical"  name="siteAnnualTheoritical" value="${siteAnnualTheoritical}"/></td>
						<td style="text-align:right;"class="cartoucheRightTitleTD"><my:humanTimeTD id="siteAnnualTotal"  name="siteAnnualTotal" value="${siteAnnualTotal}"/></td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualHoliday}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteRemainingCA}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualRTT}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualGarde_enfant}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualChomage}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualCSS}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualINJUSTIFIE}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualMISE_A_PIED}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualDIF}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualDON}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualSickness}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualExceptionnel}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualPaternite}</td>
						<td style="width:50px;text-align:center;"class="cartoucheRightTitleTD">${siteAnnualParental}</td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><my:humanTimeTD id="siteAnnualPayableSupTime"  name="siteAnnualPayableSupTime" value="${siteAnnualPayableSupTime}"/></td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><my:humanTimeTD id="siteAnnualTheoriticalIncludingExtra"  name="siteAnnualTheoriticalIncludingExtra" value="${siteAnnualTheoriticalIncludingExtra}"/></td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><my:humanTimeTD id="siteAnnualSupTimeAboveTheoritical"  name="siteAnnualSupTimeAboveTheoritical" value="${siteAnnualSupTimeAboveTheoritical}"/></td>
						<td style="text-align:right;width:50px;"class="cartoucheRightTitleTD"><my:humanTimeTD id="siteAnnualGlobalSupTimeToPay"  name="siteAnnualGlobalSupTimeToPay" value="${siteAnnualGlobalSupTimeToPay}"/></td>				
					</tr>	
				</g:if>				
			</g:if>
		</tbody>
	</table>
</div>
