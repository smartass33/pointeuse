<script type="text/javascript">
	$(function(){$('#timeOffHours').load('${createLink(controller:'employee', action:'getAjaxOffHoursTime',params:[id:employee?.id,year:lastYear])}');});
</script>	

<h1 style="padding:15px">
	<g:message code="yearly.recap.label"/> ${lastYear} / ${thisYear} : <g:if test="${employee != null }">${employee.firstName} ${employee.lastName}</g:if>
	<g:set var="calendar" value="${Calendar.instance}"/>
</h1>

<table id="table-header-rotated" style="width:100%;"class="table table-striped table-header-rotated" >
	<BR>
	<thead>
		<th class='rotate-45'  style="width:100px"><div><span><font size="1"><g:message code="annual.report.month.label"/></font></span></div></th>	
		<th class='rotate-45'  style="width:60px"><div><span><font size="1"><g:message code="annual.report.opened.days"/></font></span></div></th>	
		<th class='rotate-45'  style="width:60px"><div><span><font size="1"><g:message code="annual.report.working.days"/></font></span></div></th>			
		<th class='rotate-45'  style="width:60px"><div><span><font size="1"><g:message code="annual.report.theoritical.abreviation.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:60px"><div><span><font size="1"><g:message code="annual.report.elapsed.label.abr"/></font></span></div></th>
		<th class='rotate-45'  style="width:45px"><div><span><font size="1"><g:message code="annual.report.holidays.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:55px"><div><span><font size="1"><g:message code="annual.report.remaining.holidays.label"/></font></span></div></th>	
		<th class='rotate-45'  style="width:43px"><div><span><font size="1"><g:message code="annual.report.RTT.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:45px"><div><span><font size="1"><g:message code="annual.report.CSS.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:45px"><div><span><font size="1"><g:message code="annual.report.DIF.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:45px"><div><span><font size="1"><g:message code="annual.report.DON.label"/></font></span></div></th>		
		<th class='rotate-45'  style="width:50px"><div><span><font size="1"><g:message code="annual.report.INJUSTIFIE.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:50px"><div><span><font size="1"><g:message code="annual.report.sickness.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:45px"><div><span><font size="1"><g:message code="annual.report.maternite.abreviation.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:50px"><div><span><font size="1"><g:message code="annual.report.exceptionnel.abreviation.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:55px"><div><span><font size="1"><g:message code="annual.report.paternite.abreviation.label"/></font></span></div></th>		
		<th class='rotate-45'  style="width:55px"><div><span><font size="1"><g:message code="annual.report.parental.abreviation.label"/></font></span></div></th>		
		<th class='rotate-45'  style="width:50px"><div><span><font size="1"><g:message code="annual.report.formation.label"/></font></span></div></th>		
		<th class='rotate-45'  style="width:55px"><div><span><font size="1"><g:message code="annual.report.supplementary.abreviation.label"/></font></span></div></th>
		<th class='rotate-45'  style="width:55px"><div><span><font size="1"><g:message code="annual.report.sunday.time.label.abr"/></font></span></div></th>
		<th class='rotate-45'  style="width:55px"><div><span><font size="1"><g:message code="annual.report.bank.holiday.time.label.abr"/></font></span></div></th>
	</thead>
	
	<tbody id='body_update' style="border:1px;">
		<g:each in="${yearMonthMap}"  status="i" var="cartouche">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<%= calendar.set(Calendar.MONTH,cartouche.key-1) %>
				<td style="vertical-align: middle;text-align:center;">${calendar.time.format('MMM') } ${yearMap.get(cartouche.key)}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('workingDays')}</td>
				<td style="vertical-align: middle;text-align:center;">${monthlyWorkingDays.get(cartouche.key)}</td>	
				<td style="vertical-align: middle;text-align:right;">			
					<g:if test="${cartouche.value.getAt('monthTheoriticalHuman')}">
 						${cartouche.value.getAt('monthTheoriticalHuman')}
               		</g:if>
               		<g:else>
           				00:00
               		</g:else>
				</td>
				<td style="vertical-align: middle;width:100px;text-align:right;">
					<g:if test="${yearTotalMap.get(cartouche.key) !=null}">
						${yearTotalMap.get(cartouche.key)}
               		</g:if>
               		<g:else>
           				00:00
               		</g:else>
				</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('holidays')}</td>
				<td style="vertical-align: middle;text-align:center;">${monthlyTakenHolidays.get(cartouche.key)}</td>				
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('rtt')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('sansSolde')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('dif')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('don')}</td>		
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('injustifie')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('sickness')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('maternite')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('exceptionnel')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('paternite')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('parental')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('formation')}</td>			
				<td style="vertical-align: middle;text-align:right;">
					<g:if test="${yearMonthlySupTime.get(cartouche.key) !=null}"><my:humanTimeTD id="yearMonthlySupTime"  name="yearMonthlySupTime" value="${yearMonthlySupTime.get(cartouche.key)} "/></g:if>
				</td>	
				<td style="vertical-align: middle;text-align:right;">${yearSundayMap.get(cartouche.key)}</td>
				<td style="vertical-align: middle;text-align:right;">${yearBankHolidayMap.get(cartouche.key)}</td>							
			</tr>
		</g:each>
		<tr class='even' style='font-weight:bold;'>
			<td style="vertical-align: middle;text-align:center;"><g:message code="annual.total"/></td>
			<td  style="text-align:center;">${annualWorkingDays}</td>
			<td style="text-align:center;">${annualEmployeeWorkingDays}</td>
					
			<td style="vertical-align: middle;text-align:right;">
				<g:if test="${annualTheoritical !=null}">
	              		<my:humanTimeTD id="annualTheoritical"  name="annualTheoritical" value="${annualTheoritical}"/>
	          	</g:if>
			</td>
			<td style="vertical-align: middle;text-align:right;">
				<g:if test="${annualTotal !=null}">
	              		<my:humanTimeTD id="annualTotal"  name="annualTotal" value="${annualTotal}"/>
	          	</g:if>
		</td>
		<td style="vertical-align: middle;text-align:center;">${annualHoliday}</td>
		<td style="vertical-align: middle;text-align:center;">${remainingCA}</td>
		<td style="vertical-align: middle;text-align:center;">${annualRTT}</td>
		<td style="vertical-align: middle;text-align:center;">${annualCSS}</td>
		<td style="vertical-align: middle;text-align:center;">${annualDIF}</td>
		<td style="vertical-align: middle;text-align:center;">${annualDON}</td>
		<td style="vertical-align: middle;text-align:center;">${annualINJUSTIFIE}</td>
		<td style="vertical-align: middle;text-align:center;">${annualSickness}</td>
		<td style="vertical-align: middle;text-align:center;">${annualMaternite}</td>
		<td style="vertical-align: middle;text-align:center;">${annualExceptionnel}</td>
		<td style="vertical-align: middle;text-align:center;">${annualPaternite}</td>
		<td style="vertical-align: middle;text-align:center;">${annualParental}</td>
		<td style="vertical-align: middle;text-align:center;">${annualFormation}</td>
		<td style="vertical-align: middle;text-align:right;">
		<g:if test="${annualMonthlySupTime !=null}">
			<my:humanTimeTD id="annualMonthlySupTime"  name="annualMonthlySupTime" value="${annualMonthlySupTime}"/>
         	</g:if>
		</td>
		<td style="vertical-align: middle;text-align:right;"><my:humanTimeTD id="sundayTime"  name="sundayTime" value="${annualSundayTime}"/></td>
		<td style="vertical-align: middle;text-align:right;"><my:humanTimeTD id="bankTime"  name="bankTime" value="${annualBankHolidayTime}"/></td>
		</tr>
	</tbody>
</table>
<BR>

<div>
	<table >
		<tr>
			<td style="witdh: 300px;" class="annualReportTitleTD"><a id="HRA" href="#" style="text-decoration: none;color:#666;" title="${message(code: "annual.HRA.tooltip")}"><g:message code="annual.actual.intial"/>:</td>
			<td style="witdh: 50px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="annualTotal"  name="annualTotal" value="${annualTotal}"/></td>		
		</tr>	
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><g:message code="annual.report.bank.sunday.holiday.time.label"/>:</td>
			<g:if test="${annualSundayTime != null &&  annualBankHolidayTime != null}">
				<td style="witdh: 50px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="sundayBank"  name="sundayBank" value="${annualSundayTime + annualBankHolidayTime}"/></td>				
			</g:if>
			<g:else>
				<td style="witdh: 50px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="sundayBank"  name="sundayBank" value="${0}"/></td>							
			</g:else>
		</tr>			
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><a id="HTA" href="#" style="text-decoration: none;color:#666;" title="${message(code: "annual.HTA.tooltip")}"><g:message code="annual.theoritical.intial"/>:</td>
			<td style="witdh: 50px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="annualTheoritical"  name="annualTheoritical" value="${annualTheoritical}"/></td>		
		</tr>	
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><a id="HSQ" href="#" style="text-decoration: none;color:#666;" title="${message(code: "annual.HSQ.tooltip")}"><g:message code="annual.HS.above.quota"/>:</td>
			<td style="witdh: 50px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="annualPayableSupTime"  name="annualPayableSupTime" value="${annualPayableSupTime}"/></td>
		</tr>
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><a id="HTA.recomputed" href="#" style="text-decoration: none;color:#666;" title="${message(code: "annual.HTA.recomputed.tooltip")}"><g:message code="annual.computed.quota"/>:</a><richui:tooltip id="annualQuota" /></td>
			<td style="witdh: 60px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="annualTheoriticalIncludingExtra"  name="annualTheoriticalIncludingExtra" value="${annualTheoriticalIncludingExtra}"/></td>
		</tr>
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><a id="QA" href="#" style="text-decoration: none;color:#666;" title="${message(code: "annual.QA.tooltip")}"><g:message code="annual.other.HS.quota"/>:</a><richui:tooltip id="annualHS" /></td>
			<td style="witdh: 60px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="annualSupTimeAboveTheoritical"  name="annualSupTimeAboveTheoritical" value="${annualSupTimeAboveTheoritical}"/></td>
		</tr>
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><a id="HSP" href="#" style="text-decoration: none;color:#666;" title="${message(code: "annual.HSP.tooltip")}"><g:message code="annual.total.HS.to.pay.no.sunday"/>:</td>
			<%
				if (annualGlobalSupTimeToPay == null){ annualGlobalSupTimeToPay = 0}
				if (annualSundayTime == null){ annualSundayTime = 0}
				if (annualBankHolidayTime == null){ annualBankHolidayTime = 0}
				if (annualPaidHS == null){ annualPaidHS = 0}
			 %>
			<td style="witdh: 60px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="paidHSNoSunday"  name="paidHSNoSunday" value="${annualGlobalSupTimeToPay - annualSundayTime - annualBankHolidayTime}"/></td>
		</tr>	
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><g:message code="supplementary.time.already.paid"/>:</td>
			<td style="witdh: 60px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="paidHS"  name="paidHS" value="${annualPaidHS}"/></td>
		</tr>	
		<tr>
			<td style="witdh: 200px;" class="annualReportTitleTD"><g:message code="supplementary.time.to.pay"/>:</td>
			<td style="witdh: 60px;text-align:right;" class="annualReportFiguresTD"><my:humanTimeTD id="HStoPay"  name="HStoPay" value="${annualGlobalSupTimeToPay - (annualSundayTime + annualBankHolidayTime + annualPaidHS)}"/> ou <my:humanTimeDecimalTD id="HStoPayWithDecimal"  name="HStoPayWithDecimal" value="${annualGlobalSupTimeToPay - (annualSundayTime + annualBankHolidayTime + annualPaidHS)}"/></td>
		</tr>
	</table>
</div>
<div id="timeOffHours">  
 		<g:offHoursTime/>
 </div>	
