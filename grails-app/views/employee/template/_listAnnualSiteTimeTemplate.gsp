<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.WeeklyTotal"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="grails.converters.JSON"%>

<g:set var="j" value="${1}"/>
<g:set var="calendarMonday" value="${Calendar.instance}"/>
<g:set var="calendarSaturday" value="${Calendar.instance}"/>
<g:if test="${firstYear != null}">
	<%
		calendarMonday.set(Calendar.YEAR,firstYear)
		calendarSaturday.set(Calendar.YEAR,firstYear)	
	 %>
</g:if>

<%
	def simpleFuntionCheckBoxesMap = '';
	funtionCheckBoxesMap.each{ k, v -> 
		simpleFuntionCheckBoxesMap += k 
		simpleFuntionCheckBoxesMap += '-' 
		simpleFuntionCheckBoxesMap += v 
		simpleFuntionCheckBoxesMap += '-' 
	 }	
%>
<g:if test="${annualSiteReportMap != null}">
	<table id="annuale-sites-table" style="font-family: Helvetica;font-size: 10px;">
		<thead>
			<th style="vertical-align: middle;text-align:center;">${message(code: 'laboratory.label', default: 'Report')}</th>
			<g:if test="${siteFunctionList != null}">
				<g:each in="${siteFunctionList}" var="siteFunction">
					<th style="vertical-align: middle;text-align:center;">${siteFunction.name} <br>
						<g:if test="${funtionCheckBoxesMap.get(siteFunction.name)}">
							<input id="checkBox_${siteFunction}" type="checkbox" checked="checked" 
								onclick="${remoteFunction(
									controller:'employee', 
									action:'annualSitesReport', 
									update:'annualSiteTable', 
									onLoading:"document.getElementById('spinner').style.display = 'inline';",
									onComplete:"document.getElementById('spinner').style.display = 'none';",
									params:'  \'siteFunction=\'+\'' +siteFunction.name + '\'+  \'&siteValue=\' + this.checked  + \'&periodId=\' +\'' + period.id + '\' + \'&simpleFuntionCheckBoxesMap=\' +\'' + simpleFuntionCheckBoxesMap + '\'   '										
									)}">
						</g:if>
						<g:else>
							<input id="checkBox_${siteFunction}" type="checkbox" value="${true}"
								onclick="${
									remoteFunction(controller:'employee', 
									action:'annualSitesReport',
									update:'annualSiteTable',
									onLoading:"document.getElementById('spinner').style.display = 'inline';",
									onComplete:"document.getElementById('spinner').style.display = 'none';",
									params:'  \'siteFunction=\'+\'' +siteFunction.name + '\'+  \'&siteValue=\' + this.checked  + \'&periodId=\' +\'' + period.id + '\' + \'&simpleFuntionCheckBoxesMap=\' +\'' + simpleFuntionCheckBoxesMap + '\'   '
									)}">
						</g:else>
					</th>
				</g:each>
			</g:if>
			<th style="vertical-align: middle;text-align:center;">${message(code: 'weekly.particularism.1')}</th>	
			<th style="vertical-align: middle;text-align:center;">${message(code: 'weekly.particularism.2')}</th>	
			<th style="vertical-align: middle;text-align:center;">${message(code: 'sub.total')} 	
				<g:form>
					<g:actionSubmit class='excelButton' value="export"  action="annualSitesReportExcelExport"/>	
					<g:if test="${funtionCheckBoxesMap != null}"><g:hiddenField name="funtionCheckBoxesMap" id="funtionCheckBoxesMap" value="${funtionCheckBoxesMap as JSON} " /></g:if>
					<g:if test="${period != null}"><g:hiddenField name="periodId" id="periodId" value="${period.id} " /></g:if>			
				</g:form >				
			</th>
			<th style="vertical-align: middle;text-align:center;">${message(code: 'weekly.case')}</th>	
			<th style="vertical-align: middle;text-align:center;">${message(code: 'weekly.home.assistance')}</th>	
			<th style="vertical-align: middle;text-align:center;">${message(code: 'ratio.employees.cases')}</th>	
		</thead>
		
		
			<tbody id='body_table' style="border:1px;">
				<g:each in="${annualSiteReportMap}" status="i" var="siteReport">	
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td style="vertical-align: middle;text-align:center;font-size: 12px;">${siteReport.key}</td>					
						<g:if test="${siteFunctionList != null}">
							
								<g:each in="${siteFunctionList}" var="siteFunction">
									<td style="vertical-align: middle;text-align:center;width:25px;font-size: 12px;">
										<g:if test="${siteReport.value.get('yearlyTotalsByFunction') != null && siteReport.value.get('yearlyTotalsByFunction').get(siteFunction) != null}"><my:humanTimeTD id="yearlyTotalsByFunction" name="yearlyTotalsByFunction" value="${siteReport.value.get('yearlyTotalsByFunction').get(siteFunction)}"/></g:if>
										<g:else><my:humanTimeTD id="yearlyTotalsByFunction" name="yearlyTotalsByFunction" value="${0}"/></g:else>
									</td>
								</g:each>					
						</g:if>
						<td style="vertical-align: middle;text-align:center;font-size: 12px;">
							<g:if test="${siteReport.value.get('yearlyParticularity1') != null}"><my:humanTimeTD id="yearlyParticularity1" name="yearlyParticularity1" value="${siteReport.value.get('yearlyParticularity1')}"/></g:if>
							<g:else><my:humanTimeTD id="yearlyParticularity1" name="yearlyParticularity1" value="${0}"/></g:else>
						</td>
						<td style="vertical-align: middle;text-align:center;font-size: 12px;">
							<g:if test="${siteReport.value.get('yearlyParticularity2') != null}"><my:humanTimeTD id="yearlyParticularity2" name="yearlyParticularity2" value="${siteReport.value.get('yearlyParticularity2')}"/></g:if>
							<g:else><my:humanTimeTD id="yearlyParticularity2" name="yearlyParticularity2" value="${0}"/></g:else>
						</td>
						<td style="vertical-align: middle;text-align:center;font-size: 12px;">
							<g:if test="${siteReport.value.get('yearlySubTotals') != null}"><my:humanTimeTD id="yearlySubTotals" name="yearlySubTotals" value="${siteReport.value.get('yearlySubTotals')}"/></g:if>
							<g:else><my:humanTimeTD id="yearlySubTotals" name="yearlySubTotals" value="${0}"/></g:else>
						</td>
						<td style="vertical-align: middle;text-align:center;font-size: 12px;">
							<g:if test="${siteReport.value.get('yearlyCases') != null}">${siteReport.value.get('yearlyCases')}</g:if>
							<g:else>${0}</g:else>
						</td>						
						<td style="vertical-align: middle;text-align:center;font-size: 12px;">
							<g:if test="${siteReport.value.get('yearlyHomeAssistance') != null}">${siteReport.value.get('yearlyHomeAssistance')}</g:if>
							<g:else>${0}</g:else>
						</td>		
						<td style="vertical-align: middle;text-align:center;font-size: 12px;">
							<g:if test="${((siteReport.value.get('yearlyHomeAssistance') != null && siteReport.value.get('yearlyHomeAssistance') != 0) || (siteReport.value.get('yearlyCases') != null) && siteReport.value.get('yearlyCases') != 0) && (siteReport.value.get('yearlySubTotals') != null && siteReport.value.get('yearlySubTotals') != 0)}">
								<g:formatNumber number="${((siteReport.value.get('yearlySubTotals') as long) / 60)/ ((siteReport.value.get('yearlyHomeAssistance') as long) + (siteReport.value.get('yearlyCases') as long))}" type="number" maxFractionDigits="2" />
							</g:if>
							<g:else>${0}
							</g:else>
						</td>					
					</tr>
				</g:each>
			</tbody>
		
	</table>
</g:if>
