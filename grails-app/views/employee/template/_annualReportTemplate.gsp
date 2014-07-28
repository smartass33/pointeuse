<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<h1 style="padding:15px">
	<g:message code="yearly.recap.label"/> ${lastYear} / ${thisYear} : <g:if test="${employee != null }">${employee.firstName} ${employee.lastName}</g:if>
	<g:set var="calendar" value="${Calendar.instance}"/>
	
</h1>


<table id="table-header-rotated" style="width:100%;"class="table table-striped table-header-rotated" >
	<BR>
	<thead>
		<th class='rotate-45' style="width:100px"><div><span><g:message code="annual.report.month.label"/></span></div></th>	
		<th class='rotate-45'><div><span><g:message code="annual.report.opened.days"/></span></div></th>	
		<th class='rotate-45'><div><span><g:message code="annual.report.working.days"/></span></div></th>			
		<th class='rotate-45'><div><span><g:message code="annual.report.theoritical.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.elapsed.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.holidays.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.remaining.holidays.label"/></span></div></th>	
		<th class='rotate-45'><div><span><g:message code="annual.report.RTT.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.CSS.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.sickness.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.supplementary.label"/></span></div></th>
		
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
           				00 : 00
               		</g:else>
				</td>
				<td style="vertical-align: middle;width:100px;text-align:right;">
					<g:if test="${yearTotalMap.get(cartouche.key) !=null}">
						${yearTotalMap.get(cartouche.key)}
               		</g:if>
               		<g:else>
           				00 : 00
               		</g:else>
				</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('holidays')}</td>
				<td style="vertical-align: middle;text-align:center;">${monthlyTakenHolidays.get(cartouche.key)}</td>				
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('rtt')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('sansSolde')}</td>
				<td style="vertical-align: middle;text-align:center;">${cartouche.value.getAt('sickness')}</td>
				<td style="vertical-align: middle;text-align:right;">
					<g:if test="${yearMonthlySupTime.get(cartouche.key) !=null}">
                   		${yearMonthlySupTime.get(cartouche.key)}              		                                  		
              		</g:if>
              		<g:else>
           				00 : 00
              		</g:else>
				</td>								
			</tr>
		</g:each>
		<tr class='even' style='font-weight:bold;'>
			<td><g:message code="annual.total"/></td>
			<td  style="text-align:center;">${annualWorkingDays}</td>
			<td style="text-align:center;">${annualEmployeeWorkingDays}</td>
					
			<td style="vertical-align: middle;text-align:right;">
				<g:if test="${annualTheoritical !=null}">
               		${annualTheoritical}
           		</g:if>
           		<g:else>
           			00 : 00
           		</g:else>
			</td>
			<td style="vertical-align: middle;text-align:right;">
				<g:if test="${annualTotal !=null}">
					${annualTotal}    		
           		</g:if>
           		<g:else>
           			00 : 00
           		</g:else>
		</td>
		<td style="vertical-align: middle;text-align:center;">${annualHoliday}</td>
		<td style="vertical-align: middle;text-align:center;">${remainingCA}</td>
		<td style="vertical-align: middle;text-align:center;">${annualRTT}</td>
		<td style="vertical-align: middle;text-align:center;">${annualCSS}</td>
		<td style="vertical-align: middle;text-align:center;">${annualSickness}</td>
		<td style="vertical-align: middle;text-align:right;">
			<g:if test="${annualPayableSupTime !=null}">
				${annualPayableSupTime}
          	</g:if>
          	<g:else>
          		00 : 00
          	</g:else>	
		</td>
		</tr>
	</tbody>
</table>
<BR>

<div>
	<table style="width:75%;">
		<tr>
			<td style="witdh: 200px;" class="cartoucheRightTitleTD"><g:message code="annual.HS.above.quota"/></td>
			<td style="witdh: 50px;text-align:right;" class="cartoucheRightFiguresTD">${annualPayableSupTime}</td>
		</tr>
		<tr>
			<td style="witdh: 200px;" class="cartoucheRightTitleTD"><a id="annualQuota" href="#" style="text-decoration: none;color:#666;" title="[theorique annuel + heures sup]"><g:message code="annual.computed.quota"/></a><richui:tooltip id="annualQuota" /></td>
			<td style="witdh: 60px;text-align:right;" class="cartoucheRightFiguresTD">${annualTheoriticalIncludingExtra}</td>
		</tr>
		<tr>
			<td style="witdh: 200px;" class="cartoucheRightTitleTD"><a id="annualHS" href="#" style="text-decoration: none;color:#666;" title="[réalisé annuel - theorique annuel - HS annuel]"><g:message code="annual.other.HS.quota"/></a><richui:tooltip id="annualHS" /></td>
			<td style="witdh: 60px;text-align:right;" class="cartoucheRightFiguresTD">${annualSupTimeAboveTheoritical}</td>
		</tr>
		<tr>
			<td style="witdh: 200px;" class="cartoucheRightTitleTD"><g:message code="annual.total.HS.to.pay"/></td>
			<td style="witdh: 60px;text-align:right;" class="cartoucheRightFiguresTD">${annualGlobalSupTimeToPay}</td>
		</tr>				
	</table>
</div>
