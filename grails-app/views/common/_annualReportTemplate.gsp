<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<h1>
	<g:message code="yearly.recap.label"/> ${lastYear} / ${thisYear}
	<g:set var="calendar" value="${Calendar.instance}"/>
	
</h1>

<table cellspacing="1px">
	<tbody>
		<tr>
			<td>Nombre de jours ouvrés:</td>
			<td>${annualWorkingDays}</td>
		</tr>
		<tr>
			
			<td><g:if test="${employee != null }">${employee.firstName} ${employee.lastName}</g:if></td>
		</tr>
		<tr>
			<td>Horaire Hebdomadaire:</td>
			<td><g:if test="${employee != null }">${employee.weeklyContractTime}</g:if></td>
		</tr>
		<tr>
			<td>Matricule:</td>
			<td><g:if test="${employee != null }">${employee.matricule}</g:if></td>
		</tr>
	</tbody>

</table>

<h1>SUIVI MENSUEL</h1>
<table id="annual-report-table">
	<thead>

		<th><g:message code="annual.report.month.label"/></th>	
		<th><g:message code="annual.report.year.label"/></th>
		<th><g:message code="annual.report.theoritical.label"/></th>
		<th><g:message code="annual.report.elapsed.label"/></th>
		<!--th><g:message code="annual.report.working.days.label"/></th-->	
		<th><g:message code="annual.report.holidays.label"/></th>
		<th><g:message code="annual.report.RTT.label"/></th>
		<th><g:message code="annual.report.CSS.label"/></th>
		<th><g:message code="annual.report.sickness.label"/></th>
		<th><g:message code="annual.report.supplementary.label"/></th>
		<th><g:message code="annual.report.complementary.label"/></th>
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${yearMonthMap}"  status="i" var="cartouche">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<%= calendar.set(Calendar.MONTH,cartouche.key-1) %>
				<td style="vertical-align: middle;">${calendar.time.format('MMMM') }</td>
				<td style="vertical-align: middle;">${yearMap.get(cartouche.key)}</td>
				<td style="vertical-align: middle;">
				
					<g:if
                		test="${cartouche.value.getAt('monthTheoriticalHuman') !=null && ((cartouche.value.getAt('monthTheoriticalHuman')).get(0)>0 ||(cartouche.value.getAt('monthTheoriticalHuman')).get(1)>0)}">
                    	${(cartouche.value.getAt('monthTheoriticalHuman')).get(0)}H${(cartouche.value.getAt('monthTheoriticalHuman')).get(1)==0?'':(cartouche.value.getAt('monthTheoriticalHuman')).get(1)} 
               		</g:if>
               		<g:else>
               			0H0
               		</g:else>
				</td>
				<td style="vertical-align: middle;">
					<g:if
                		test="${yearTotalMap.get(cartouche.key) !=null && ((yearTotalMap.get(cartouche.key)).get(0)>0 ||(yearTotalMap.get(cartouche.key)).get(1)>0)}">
                    	${(yearTotalMap.get(cartouche.key)).get(0)}H${(yearTotalMap.get(cartouche.key)).get(1)==0?'':(yearTotalMap.get(cartouche.key)).get(1)} 
               		</g:if>
               		<g:else>
               			0H0
               		</g:else>
				</td>
				<td>${cartouche.value.getAt('holidays')}</td>
				<td>${cartouche.value.getAt('rtt')}</td>
				<td>${cartouche.value.getAt('sansSolde')}</td>
				<td>${cartouche.value.getAt('sickness')}</td>

				<td>
					<g:if
               			test="${yearMonthlySupTime.get(cartouche.key) !=null && ((yearMonthlySupTime.get(cartouche.key)).get(0)>0 ||(yearMonthlySupTime.get(cartouche.key)).get(1)>0)}">
                   		${(yearMonthlySupTime.get(cartouche.key)).get(0)}H${(yearMonthlySupTime.get(cartouche.key)).get(1)==0?'':(yearMonthlySupTime.get(cartouche.key)).get(1)} 
              		</g:if>
              		<g:else>
              			0H0
              		</g:else>
				</td>			
				<td>
					<g:if
               			test="${yearMonthlyCompTime.get(cartouche.key) !=null && ((yearMonthlyCompTime.get(cartouche.key)).get(0)>0 ||(yearMonthlyCompTime.get(cartouche.key)).get(1)>0)}">
                   		${(yearMonthlyCompTime.get(cartouche.key)).get(0)}H${(yearMonthlyCompTime.get(cartouche.key)).get(1)==0?'':(yearMonthlyCompTime.get(cartouche.key)).get(1)} 
              		</g:if>
              		<g:else>
              			0H0
              		</g:else>	
				</td>							
			</tr>
		</g:each>
		<tr>
			<td></td>
			<td></td>
			<td>
				<g:if
               		test="${annualTheoritical !=null && (annualTheoritical.get(0)>0 ||annualTheoritical.get(1)>0)}">
               		${annualTheoritical.get(0)}H${annualTheoritical.get(1)==0?'':annualTheoritical.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>
			</td>
			<td>
				<g:if
               		test="${annualTotal !=null && (annualTotal.get(0)>0 ||annualTotal.get(1)>0)}">
               		${annualTotal.get(0)}H${annualTotal.get(1)==0?'':annualTotal.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>
		</td>
			<td>${annualHoliday}</td>
			<td>${annualRTT}</td>
			<td>${annualCSS}</td>
			<td>${annualSickness}</td>
			<td>
				<g:if
               		test="${annualPayableSupTime !=null && (annualPayableSupTime.get(0)>0 ||annualPayableSupTime.get(1)>0)}">
               		${annualPayableSupTime.get(0)}H${annualPayableSupTime.get(1)==0?'':annualPayableSupTime.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>	
			</td>
			<td>
				<g:if
               		test="${annualPayableCompTime !=null && (annualPayableCompTime.get(0)>0 ||annualPayableCompTime.get(1)>0)}">
               		${annualPayableCompTime.get(0)}H${annualPayableCompTime.get(1)==0?'':annualPayableCompTime.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>
           	</td>
		</tr>
	</tbody>
</table>

<h1>SYNTHESE ANNUELLE</h1>
<table>
	<tr>
		<td>Jours Travaillés</td>
		<td>${annualEmployeeWorkingDays}</td>
	</tr>
	<tr>
		<td>Quota Annuel de base</td>
		<td>
			<g:if
          		test="${annualTheoritical !=null && (annualTheoritical.get(0)>0 ||annualTheoritical.get(1)>0)}">
               		${annualTheoritical.get(0)}H${annualTheoritical.get(1)==0?'':annualTheoritical.get(1)} 
           	</g:if>	
           	<g:else>
           			0H0
           	</g:else>
        </td>
	</tr>
	<tr>
		<td>Total Heures Sup/An</td>
		<td>				
			<g:if
               		test="${annualPayableSupTime !=null && (annualPayableSupTime.get(0)>0 ||annualPayableSupTime.get(1)>0)}">
               		${annualPayableSupTime.get(0)}H${annualPayableSupTime.get(1)==0?'':annualPayableSupTime.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           </g:else>
        </td>
	</tr>
	<tr>
		<td>Total Heures Comp/An</td>
		<td>
			<g:if
           		test="${annualPayableCompTime !=null && (annualPayableCompTime.get(0)>0 ||annualPayableCompTime.get(1)>0)}">
               		${annualPayableCompTime.get(0)}H${annualPayableCompTime.get(1)==0?'':annualPayableCompTime.get(1)} 
           	</g:if>
           	<g:else>
           			0H0
           	</g:else>
        </td>
	</tr>
	<tr>
		<td>Quota Annuel avec H Sup et HC</td>
		<td>0</td>
	</tr>
	<tr>
		<td>Heures sup à payer</td>
		<td></td>
	</tr>
	<tr>
		<td>Heures comp à payer</td>
		<td></td>
	</tr>
	<tr>
		<td>Solde Jours congés</td>
		<td></td>
	</tr>
</table>

