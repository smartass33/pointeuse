<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
	<head>
	<%@ page import="pointeuse.Site"%>
	<%@ page import="pointeuse.Employee"%>
	<%@ page import="pointeuse.InAndOut"%>
	
		<style  type="text/css">
			@page {
			   size: 210mm 297mm;
			   margin: 0px 0px 13px 0px;
			 }
			table {
			  font: normal 11px verdana, arial, helvetica, sans-serif;
			  color: #363636;
			  background: #f6f6f6;
			  text-align:left;
			  padding:15px;
			  }

			thead, tfoot {
				background:url(bg1.png) repeat-x;
				text-align:left;
				width:180px;			
				height:30px;
			}
			thead th, tfoot th {
				padding:5px;
			}

			tr.odd {
				background: #f1f1f1;
			}
			tbody td {
			 	text-align:left;
			 	height:5px;
			 	width:180px;
			 	font-size:95%;
			}
			
			tbody th{
			 	text-align:left;
			 	height:5px;
			 	width:250px;
			}
			h1{
				font: bold 16px arial, helvetica, sans-serif;
				padding:15px;				
			}	
		</style>
	
	</head>
		
	<h1 style="text-align:center;font-size:150%">
		<g:message code="yearly.recap.label"/> ${lastYear} / ${thisYear} : <g:if test="${employee != null }">${employee.firstName} ${employee.lastName}</g:if>
		<g:set var="calendar" value="${Calendar.instance}"/>
	</h1>
	<h1 style="text-transform:uppercase;"><g:message code="annual.monthly.report.part"/></h1>
	<table id="annual-report-table" >
		<thead>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.month.label"/></th>	
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.theoritical.abreviation.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.elapsed.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.holidays.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.RTT.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.CSS.label"/></th>
			<th class='rotate-45'  style="width:45px"><div><span><g:message code="annual.report.DIF.label"/></span></div></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.INJUSTIFIE.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.sickness.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.maternite.abreviation.label"/></th>
			<th style="vertical-align: middle;text-align:center;width:"><g:message code="annual.report.exceptionnel.abreviation.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.paternite.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.supplementary.abreviation.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.sunday.time.label"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.report.bank.holiday.time.label"/></th>
		</thead>
		<tbody id='body_update' style="border:1px;">
			<g:each in="${yearMonthMap}"  status="i" var="cartouche">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<%= calendar.set(Calendar.MONTH,cartouche.key-1) %>
					<td style="vertical-align: middle;">${calendar.time.format('MMM')} ${yearMap.get(cartouche.key)}</td>
					<td style="vertical-align: middle;text-align:right;">
						<g:if test="${cartouche.value.getAt('monthTheoriticalHuman')}">
	                    	${cartouche.value.getAt('monthTheoriticalHuman')} 
	               		</g:if>
	               		<g:else>
	               			00:00
	               		</g:else>
					</td>
					<td style="vertical-align: middle;text-align:right;">
						<g:if test="${yearTotalMap.get(cartouche.key) !=null}">
	                    	${yearTotalMap.get(cartouche.key)} 
	               		</g:if>
	               		<g:else>
	               			00:00
	               		</g:else>
					</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('holidays')}</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('rtt')}</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('sansSolde')}</td>
					<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('dif')}</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('injustifie')}</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('sickness')}</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('maternite')}</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('exceptionnel')}</td>
					<td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('paternite')}</td>
					<!--  td style="vertical-align: middle;text-align:right;">${cartouche.value.getAt('formation')}</td-->

					<td style="vertical-align: middle;text-align:right;">
						<g:if test="${yearMonthlySupTime.get(cartouche.key) !=null}"><my:humanTimeTD id="yearMonthlySupTime"  name="yearMonthlySupTime" value="${yearMonthlySupTime.get(cartouche.key)} "/></g:if>
					</td>					
					<td style="vertical-align: middle;text-align:right;">${yearSundayMap.get(cartouche.key)}</td>
					<td style="vertical-align: middle;text-align:right;">${yearBankHolidayMap.get(cartouche.key)}</td>										
				</tr>
			</g:each>
			<tr class='even'>			
				<td style="vertical-align: middle;font-weight:bold;"><g:message code="annual.total"/></td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">
					<g:if test="${annualTheoritical !=null}"><my:humanTimeTD id="annualTheoritical"  name="annualTheoritical" value="${annualTheoritical}"/></g:if>
				</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">
					<g:if test="${annualTotal !=null}"><my:humanTimeTD id="annualTotal"  name="annualTotal" value="${annualTotal}"/></g:if>
				</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualHoliday}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualRTT}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualCSS}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualDIF}</td>	
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualINJUSTIFIE}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualSickness}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualMaternite}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualExceptionnel}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">${annualPaternite}</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;">
					<g:if test="${annualPayableSupTime !=null}"><my:humanTimeTD id="annualPayableSupTime"  name="annualPayableSupTime" value="${annualPayableSupTime}"/></g:if>
				</td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;"><my:humanTimeTD id="sunday"  name="sunday" value="${annualSundayTime}"/></td>
				<td style="vertical-align: middle;text-align:right;font-weight:bold;"><my:humanTimeTD id="bank"  name="bank" value="${annualBankHolidayTime}"/></td>				
			</tr>
		</tbody>
	</table>
	<h1 style="text-transform:uppercase;"><g:message code="annual.annual.report.part"/></h1>
	<table id='jours' style="width:95%">
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.opened.days"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.working.days"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.basic.quota"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.initial.CA"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.taken.CA"/></th>
			<th style="vertical-align: middle;text-align:center;"><g:message code="annual.remaining.CA"/></th>		
			<tr id='values' class='odd'>
				<td style="vertical-align: middle;text-align:center;">${annualWorkingDays}</td>
				<td style="vertical-align: middle;text-align:center;">${annualEmployeeWorkingDays}</td>
				<td style="vertical-align: middle;text-align:center;">
					<g:if test="${annualTheoritical !=null}">
		               	<my:humanTimeTD id="annualTheoritical"  name="annualTheoritical"  value="${annualTheoritical}"/> 
		           	</g:if>	
		           	<g:else>
						<my:humanTimeTD id="annualTheoritical"  name="annualTheoritical"  value="0"/>    	
			        </g:else>
		        </td>
		        <td style="vertical-align: middle;text-align:center;">${initialCA}</td>
		        <td style="vertical-align: middle;text-align:center;">${takenCA}</td>        
		        <td style="vertical-align: middle;text-align:center;">${remainingCA}</td>
			</tr>
	</table>
	<table style="width:600px;">
		<tr>
			<td style="width: 350px;font-weight:bold;" class="cartoucheRightTitleTD"><g:message code="annual.actual.intial"/>:</td>
			<td style="width: 50px;text-align:right;font-weight:bold;" class="cartoucheRightFiguresTD"><my:humanTimeTD id="annualTotal"  name="annualTotal" value="${annualTotal}"/></td>		
		</tr>
		<tr>
			<td style="width: 350px;font-weight:bold;" class="cartoucheRightTitleTD"><g:message code="annual.report.bank.sunday.holiday.time.label"/>:</td>
			<g:if test="${annualSundayTime != null &&  annualBankHolidayTime != null}">
				<td style="width: 50px;text-align:right;font-weight:bold;" class="cartoucheRightFiguresTD"><my:humanTimeTD id="sundayBank"  name="sundayBank" value="${annualSundayTime + annualBankHolidayTime}"/></td>				
			</g:if>
			<g:else>
				<td style="width:50px;text-align:right;font-weight:bold;" class="cartoucheRightFiguresTD"><my:humanTimeTD id="sundayBank"  name="sundayBank" value="${0}"/></td>							
			</g:else>
		</tr>	
		<tr>
			<td style="width: 350px;font-weight:bold;" class="cartoucheRightTitleTD"><g:message code="annual.theoritical.intial"/>:</td>
			<td style="width: 50px;text-align:right;font-weight:bold;" class="cartoucheRightFiguresTD"><my:humanTimeTD id="annualTheoritical"  name="annualTheoritical" value="${annualTheoritical}"/></td>		
		</tr>		
		<tr>
			<td style="width: 350px;font-weight:bold;"><g:message code="annual.HS.above.quota"/>:</td>
			<td style="width: 50px;text-align:right;font-weight:bold;" ><my:humanTimeTD id="annualPayableSupTime"  name="annualPayableSupTime" value="${annualPayableSupTime}"/></td>
		</tr>
		<tr>
			<td style="width: 350px;font-weight:bold;" ><g:message code="annual.computed.quota"/>:</td>
			<td style="width: 60px;text-align:right;font-weight:bold;" ><my:humanTimeTD id="annualTheoriticalIncludingExtra"  name="annualTheoriticalIncludingExtra" value="${annualTheoriticalIncludingExtra}"/></td>
		</tr>
		<tr>
			<td style="width: 350px;font-weight:bold;" ><g:message code="annual.other.HS.quota"/>:</td>
			<td style="width: 60px;text-align:right;font-weight:bold;" ><my:humanTimeTD id="annualSupTimeAboveTheoritical"  name="annualSupTimeAboveTheoritical" value="${annualSupTimeAboveTheoritical}"/> </td>
		</tr>
		<tr>
			<td style="width: 350px;font-weight:bold;" class="cartoucheRightTitleTD"><g:message code="annual.total.HS.to.pay.no.sunday"/>:</td>
			<%
				if (annualGlobalSupTimeToPay == null){ annualGlobalSupTimeToPay = 0}
				if (annualSundayTime == null){ annualSundayTime = 0}
				if (annualBankHolidayTime == null){ annualBankHolidayTime = 0}
				if (annualPaidHS == null){ annualPaidHS = 0}
			 %>
			<td style="width: 60px;text-align:right;font-weight:bold;" class="cartoucheRightFiguresTD"><my:humanTimeTD id="paidHSNoSunday"  name="paidHSNoSunday" value="${annualGlobalSupTimeToPay - annualSundayTime - annualBankHolidayTime}"/></td>
		</tr>	
		<tr>
			<td style="width: 350px;font-weight:bold;" class="cartoucheRightTitleTD"><g:message code="supplementary.time.already.paid"/>:</td>
			<td style="width: 60px;text-align:right;font-weight:bold;" class="cartoucheRightFiguresTD"><my:humanTimeTD id="paidHS"  name="paidHS" value="${annualPaidHS}"/></td>
		</tr>	
		<tr>
			<td style="width: 350px;font-weight:bold;" class="annualReportTitleTD"><g:message code="supplementary.time.to.pay"/>:</td>
			<td style="width: 150px;text-align:right;font-weight:bold;" class="annualReportFiguresTD"><my:humanTimeTD id="HStoPay"  name="HStoPay" value="${annualGlobalSupTimeToPay - (annualSundayTime + annualBankHolidayTime + annualPaidHS)}"/> ou <my:humanTimeDecimalTD id="HStoPayWithDecimal"  name="HStoPayWithDecimal" value="${annualGlobalSupTimeToPay - (annualSundayTime + annualBankHolidayTime + annualPaidHS)}"/></td>
		</tr>					
		<tr>
			<td style="width: 350px;font-weight:bold;" class="annualReportTitleTD"><g:message code="employee.annual.timeOffHours"/>:</td>
			<td style="width: 60px;text-align:right;font-weight:bold;" class="annualReportFiguresTD"><my:humanTimeTD id="annualOffHoursTime" name="annualOffHoursTime" value="${annualOffHoursTime}"/> ou ${annualOffHoursTimeDecimal}</td>
		</tr>		
	</table>	
	<table style="width:95%">
		<thead></thead>
		<tr>
			<td style="width:100px;font-style:oblique">${message(code: 'report.verification.date.label', default: 'Report')}: ${new Date().format('EEEE dd MMM yyyy')}</td>
		</tr>
		<tr><td></td></tr>
		<tr>
			<td style="width:200px;">${message(code: 'report.employee.signature.label', default: 'Report')}:</td>
			<td style="width:100px;">${message(code: 'report.employer.signature.label', default: 'Report')}:</td>	
		</tr>
	</table>
</html>