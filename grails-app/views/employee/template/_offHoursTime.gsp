<table style="width:560px;border:0">
	<tr>
		<td style="witdh: 460px;" class="annualReportTitleTD">${message(code: 'employee.annual.timeOffHours', default: 'report')}:</td>
		<td style="witdh: 60px;text-align:right;" class="annualReportFiguresTD">
			<g:if test="${annualOffHoursTime != null}">
				<my:humanTimeTD id="annualOffHoursTime"  name="annualOffHoursTime" value="${annualOffHoursTime}"/> ou ${annualOffHoursTimeDecimal}
			</g:if>
			<g:else>${message(code: 'ajax.loading.label', default: 'report')}</g:else>
		</td>
	</tr>	
</table>