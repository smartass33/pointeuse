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
		<th class='rotate-45'><div><span><g:message code="annual.report.complementary.label"/></span></div></th>
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
				<td style="vertical-align: middle;text-align:right;">
					<g:if
               			test="${yearMonthlyCompTime.get(cartouche.key) !=null && ((yearMonthlyCompTime.get(cartouche.key)).get(0)>0 ||(yearMonthlyCompTime.get(cartouche.key)).get(1)>0)}">
	 	               	<g:if test='${(yearMonthlyCompTime.get(cartouche.key)).get(0)<10}'>
		                	0${(yearMonthlyCompTime.get(cartouche.key)).get(0)} :
		                </g:if>	
		                <g:else>
		                	${(yearMonthlyCompTime.get(cartouche.key)).get(0)} :
		                </g:else>            
		                <g:if test='${(yearMonthlyCompTime.get(cartouche.key)).get(1)<10}'>
		                	0${(yearMonthlyCompTime.get(cartouche.key)).get(1)}
		                </g:if>	
		                <g:else>
		                	${(yearMonthlyCompTime.get(cartouche.key)).get(1)}
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
				<g:if
               		test="${annualTheoritical !=null && (annualTheoritical.get(0)>0 ||annualTheoritical.get(1)>0)}">
               		
	 	               	<g:if test='${annualTheoritical.get(0)<10}'>
		                	0${annualTheoritical.get(0)} :
		                </g:if>	
		                <g:else>
		                	${annualTheoritical.get(0)} :
		                </g:else>            
		                <g:if test='${annualTheoritical.get(1)<10}'>
		                	0${annualTheoritical.get(1)}
		                </g:if>	
		                <g:else>
		                	${annualTheoritical.get(1)}
		                </g:else> 
           		</g:if>
           		<g:else>
           			00 : 00
           		</g:else>
			</td>
			<td style="vertical-align: middle;text-align:right;">
				<g:if test="${annualTotal !=null && (annualTotal.get(0)>0 ||annualTotal.get(1)>0)}">
	               	<g:if test='${annualTotal.get(0)<10}'>
	                	0${annualTotal.get(0)} :
	                </g:if>	
	                <g:else>
	                	${annualTotal.get(0)} :
	                </g:else>
	                
	                <g:if test='${annualTotal.get(1)<10}'>
	                	0${annualTotal.get(1)}
	                </g:if>	
	                <g:else>
	                	${annualTotal.get(1)}
	                </g:else>
               		
               		
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
			<g:if test="${annualPayableSupTime !=null && (annualPayableSupTime.get(0)>0 ||annualPayableSupTime.get(1)>0)}">
				
				<g:if test='${annualPayableSupTime.get(0)<10}'>
                	0${annualPayableSupTime.get(0)} :
                </g:if>	
                <g:else>
                	${annualPayableSupTime.get(0)} :
                </g:else>
                
                <g:if test='${annualPayableSupTime.get(1)<10}'>
                	0${annualPayableSupTime.get(1)}
                </g:if>	
                <g:else>
                	${annualPayableSupTime.get(1)}
                </g:else>
			
          	</g:if>
          	<g:else>
          		00 : 00
          	</g:else>	
		</td>
		
		<td style="vertical-align: middle;text-align:right;">
			<g:if test="${annualPayableCompTime !=null && (annualPayableCompTime.get(0)>0 ||annualPayableCompTime.get(1)>0)}">
            	
            	<g:if test='${annualPayableCompTime.get(0)<10}'>
                	0${annualPayableCompTime.get(0)} :
                </g:if>	
                <g:else>
                	${annualPayableCompTime.get(0)} :
                </g:else>
                
                <g:if test='${annualPayableCompTime.get(1)<10}'>
                	0${annualPayableCompTime.get(1)}
                </g:if>	
                <g:else>
                	${annualPayableCompTime.get(1)}
                </g:else>	
          	</g:if>
          	<g:else>
          		00 : 00
          	</g:else>	
		</td>		
		<td></td>
		</tr>
	</tbody>
</table>


