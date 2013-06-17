<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<h1>
	<g:message code="yearly.recap.label"/> ${lastYear} / ${thisYear}
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
		<th><g:message code="annual.report.year.label"/></th>
		<th><g:message code="annual.report.month.label"/></th>	
		<th><g:message code="annual.report.working.days.label"/></th>	
		<th><g:message code="annual.report.holidays.label"/></th>
		<th><g:message code="annual.report.RTT.label"/></th>
		<th><g:message code="annual.report.CSS.label"/></th>
		<th><g:message code="annual.report.sickness.label"/></th>
		<th><g:message code="annual.report.theoritical.label"/></th>
		<th><g:message code="annual.report.elapsed.label"/></th>
		<th><g:message code="annual.report.supplementary.label"/></th>
		<th><g:message code="annual.report.complementary.label"/></th>
		
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${yearMonthMap}"  status="i" var="cartouche">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td>${yearMap.get(cartouche.key)}</td>			
				<td>${cartouche.key}</td>
				<td>${cartouche.value.get(3)}</td>
				<td>${cartouche.value.get(4)}</td>
				<td>${cartouche.value.get(5)}</td>
				<td>${cartouche.value.get(6)}</td>
				<td>${cartouche.value.get(7)}</td>
				<td>${cartouche.value.get(18)}</td>
				<td>${yearTotalMap.get(cartouche.key)}</td>
				<td><g:if test="${yearMonthlySupTime }">${yearMonthlySupTime.get(cartouche.key)}</g:if></td>			
				<td><g:if test="${yearMonthlySupTime }">${yearMonthlySupTime.get(cartouche.key)}</g:if></td>							
			</tr>
		</g:each>
		<tr>
			<td></td>
			<td></td>
			<td>${annualWorkingDays}</td>
			<td>${annualHoliday}</td>
			<td>${annualRTT}</td>
			<td>${annualCSS}</td>
			<td>${annualSickness}</td>
			<td>${annualTheoritical}</td>
			<td>${annualTotal}</td>
			<td>${annualPayableSupTime}</td>
			<td>${annualPayableCompTime}</td>		
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
		<td>${annualTheoritical}</td>
	</tr>
	<tr>
		<td>Total Heures Sup/An</td>
		<td>${annualPayableSupTime}</td>
	</tr>
	<tr>
		<td>Total Heures Comp/An</td>
		<td>${annualPayableCompTime}</td>
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

