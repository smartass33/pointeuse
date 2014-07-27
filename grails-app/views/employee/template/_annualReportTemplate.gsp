<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<h1>
	<g:message code="yearly.recap.label"/> ${lastYear} / ${thisYear} : <g:if test="${employee != null }">${employee.firstName} ${employee.lastName}</g:if>
	<g:set var="calendar" value="${Calendar.instance}"/>
	
</h1>


<table id="table-header-rotated" style="width:100%;"class="table table-striped table-header-rotated" >
<BR>
	<thead>
		<th class='rotate-45' style="width:100px"><div><span><g:message code="annual.report.month.label"/></span></div></th>	
		<th class='rotate-45'><div><span>Jours ouvrés</span></div></th>	
		<th class='rotate-45'><div><span>Jours travaillés</span></div></th>			
		<th class='rotate-45'><div><span><g:message code="annual.report.theoritical.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.elapsed.label"/></span></div></th>
		<th class='rotate-45'><div><span><g:message code="annual.report.holidays.label"/></span></div></th>
		<th class='rotate-45'><div><span>CA restant</span></div></th>	
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
					<g:if
                		test="${cartouche.value.getAt('monthTheoriticalHuman') !=null && ((cartouche.value.getAt('monthTheoriticalHuman')).get(0)>0 ||(cartouche.value.getAt('monthTheoriticalHuman')).get(1)>0)}">
 	 	               	<g:if test='${(cartouche.value.getAt('monthTheoriticalHuman')).get(0)<10}'>
		                	0${(cartouche.value.getAt('monthTheoriticalHuman')).get(0)} :
		                </g:if>	
		                <g:else>
		                	${(cartouche.value.getAt('monthTheoriticalHuman')).get(0)} :
		                </g:else>            
		                <g:if test='${(cartouche.value.getAt('monthTheoriticalHuman')).get(1)<10}'>
		                	0${(cartouche.value.getAt('monthTheoriticalHuman')).get(1)}
		                </g:if>	
		                <g:else>
		                	${(cartouche.value.getAt('monthTheoriticalHuman')).get(1)}
		                </g:else> 
               		</g:if>
               		<g:else>
           			00 : 00
               		</g:else>
				</td>
				<td style="vertical-align: middle;width:100px;text-align:right;">
					<g:if
                		test="${yearTotalMap.get(cartouche.key) !=null && ((yearTotalMap.get(cartouche.key)).get(0)>0 ||(yearTotalMap.get(cartouche.key)).get(1)>0)}">
	 	               	<g:if test='${(yearTotalMap.get(cartouche.key)).get(0)<10}'>
		                	0${(yearTotalMap.get(cartouche.key)).get(0)} :
		                </g:if>	
		                <g:else>
		                	${(yearTotalMap.get(cartouche.key)).get(0)} :
		                </g:else>            
		                <g:if test='${(yearTotalMap.get(cartouche.key)).get(1)<10}'>
		                	0${(yearTotalMap.get(cartouche.key)).get(1)}
		                </g:if>	
		                <g:else>
		                	${(yearTotalMap.get(cartouche.key)).get(1)}
		                </g:else>  

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
					<g:if
               			test="${yearMonthlySupTime.get(cartouche.key) !=null && ((yearMonthlySupTime.get(cartouche.key)).get(0)>0 ||(yearMonthlySupTime.get(cartouche.key)).get(1)>0)}">
                   		
	 	               	<g:if test='${(yearMonthlySupTime.get(cartouche.key)).get(0)<10}'>
		                	0${(yearMonthlySupTime.get(cartouche.key)).get(0)} :
		                </g:if>	
		                <g:else>
		                	${(yearMonthlySupTime.get(cartouche.key)).get(0)} :
		                </g:else>            
		                <g:if test='${(yearMonthlySupTime.get(cartouche.key)).get(1)<10}'>
		                	0${(yearMonthlySupTime.get(cartouche.key)).get(1)}
		                </g:if>	
		                <g:else>
		                	${(yearMonthlySupTime.get(cartouche.key)).get(1)}
		                </g:else>                  		                                  		
              		</g:if>
              		<g:else>
           			00 : 00
              		</g:else>
				</td>								
			</tr>
		</g:each>
		<tr class='even' style='font-weight:bold;'>
			<td>TOTAL</td>
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
	<table style="width:70%">
		<tr>
			<td style="witdh: 450px;" class="cartoucheRightTitleTD">HS/An (Heures supérieures aux quotas journalier ou hebdomadaire): </td>
			<td style="witdh: 50px;text-align:right;" class="cartoucheRightFiguresTD">${annualPayableSupTime}</td>
		</tr>
		<tr>
			<td style="witdh: 450px;" class="cartoucheRightTitleTD"><a id="annualQuota" href="#" style="text-decoration: none;color:#666;" title="[theorique annuel + heures sup]">Quota annuel avec HS et HC (recalculé): </a><richui:tooltip id="annualQuota" /></td>
			<td style="witdh: 60px;text-align:right;" class="cartoucheRightFiguresTD">${annualTheoriticalIncludingExtra}</td>
		</tr>
		<tr>
			<td style="witdh: 450px;" class="cartoucheRightTitleTD"><a id="annualHS" href="#" style="text-decoration: none;color:#666;" title="[réalisé annuel - theorique annuel - HS annuel]">HS annuelle (hors heures supplémentaires supérieures aux quotas journaliers ou hebdo): </a><richui:tooltip id="annualHS" /></td>
			<td style="witdh: 60px;text-align:right;" class="cartoucheRightFiguresTD">${annualSupTimeAboveTheoritical}</td>
		</tr>
		<tr>
			<td style="witdh: 450px;" class="cartoucheRightTitleTD">Heures supplémentaires à payer: </td>
			<td style="witdh: 60px;text-align:right;" class="cartoucheRightFiguresTD">${annualGlobalSupTimeToPay}</td>
		</tr>				
	</table>

</div>
