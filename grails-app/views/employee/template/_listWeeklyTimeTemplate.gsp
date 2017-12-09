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

<g:if test="${weekList != null &&  employeeInstanceList != null}">
	<table id="weekly-table" style="font-family: Helvetica;font-size: 10px;">
		<thead>
			<th style="vertical-align: middle;text-align:center;">${message(code: 'default.week', default: 'Report')}</th>
			<g:each in="${employeeInstanceList}" var="employee">			
				<th style="vertical-align: middle;text-align:center;" class="employee">${employee.lastName.take(4)}.</th>
			</g:each>
			<g:if test="${siteFunctionMap != null}">
				<g:each in="${siteFunctionMap}" var="siteFunction">
					<th style="vertical-align: middle;text-align:center;">${siteFunction.key} <br>
						<g:if test="${funtionCheckBoxesMap.get(siteFunction.key)}">
							<input id="checkBox_${siteFunction.key}" type="checkbox" checked="checked" 
								onclick="${remoteFunction(
									controller:'employee', 
									action:'weeklyReport', 
									update:'weeklyTable', 
									onLoading:"document.getElementById('spinner').style.display = 'inline';",
									onComplete:"document.getElementById('spinner').style.display = 'none';",
									params:'  \'siteFunction=\'+\'' +siteFunction.key + '\'+  \'&siteValue=\' + this.checked  + \'&periodId=\' +\'' + period.id + '\' + \'&siteId=\' +\'' + site.id + '\' + \'&simpleFuntionCheckBoxesMap=\' +\'' + simpleFuntionCheckBoxesMap + '\'   '										
									)}">
						</g:if>
						<g:else>
							<input id="checkBox_${siteFunction.key}" type="checkbox" value="${true}"
								onclick="${
									remoteFunction(controller:'employee', 
									action:'weeklyReport', update:'weeklyTable',
									onLoading:"document.getElementById('spinner').style.display = 'inline';",
									onComplete:"document.getElementById('spinner').style.display = 'none';",
									params:'  \'siteFunction=\'+\'' +siteFunction.key + '\'+  \'&siteValue=\' + this.checked  + \'&periodId=\' +\'' + period.id + '\' + \'&siteId=\' +\'' + site.id + '\' + \'&simpleFuntionCheckBoxesMap=\' +\'' + simpleFuntionCheckBoxesMap + '\'   '
									)}">
						</g:else>
					</th>
				</g:each>
			</g:if>
			<th style="vertical-align: middle;text-align:center;width:90px;">${message(code: 'weekly.particularism.1')}</th>	
			<th style="vertical-align: middle;text-align:center;width:90px;">${message(code: 'weekly.particularism.2')}</th>	
			<th style="vertical-align: middle;text-align:center;width:60px;">${message(code: 'sub.total')} 	
				<g:form>
					<g:actionSubmit class='excelButton' value="export"  action="weeklyReportExcelExport"/>	
					<g:hiddenField name="funtionCheckBoxesMap" id="funtionCheckBoxesMap" value="${funtionCheckBoxesMap as JSON} " />
					<g:if test="${site != null}"><g:hiddenField name="siteId" id="siteId" value="${site.id} " /></g:if>
					<g:if test="${period != null}"><g:hiddenField name="periodId" id="periodId" value="${period.id} " /></g:if>			
				</g:form >				
			</th>
			<th style="vertical-align: middle;text-align:center;width:60px;" class="domicile">${message(code: 'weekly.case')}</th>	
			<th style="vertical-align: middle;text-align:center;width:60px;" class="domicile">${message(code: 'weekly.home.assistance')}</th>	

		</thead>
		
		<tbody id='body_table' style="border:1px;">
			<g:each in="${weekList}" status="i" var="weekNumber">	
				<%
					calendarMonday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)		
					calendarMonday.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
					calendarSaturday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)
					calendarSaturday.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY)
							
					if (weekNumber == 1){
						calendarMonday.set(Calendar.YEAR,lastYear)	
						calendarSaturday.set(Calendar.YEAR,lastYear)				
					}
				%>	
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<% j = i %>
					<td style="vertical-align: middle;text-align:center; width:35px;">
						 ${calendarMonday.time.format('EE dd/MM/yy')}-${calendarSaturday.time.format('EE dd/MM/yy')}					  
					</td>
	
					<g:each in="${employeeInstanceList}" var="currentEmployee">	
						<td style="vertical-align: middle;text-align:center;width:10px;font-size: 12px;" class="employee">	
							<g:if test="${weeklyTotalsByWeek.get(weekNumber)!= null && weeklyTotalsByWeek.get(weekNumber).get(currentEmployee) != null}">
								<my:humanTimeTD id="weeklyTotalByEmployee" name="weeklyTotalByEmployee" value="${weeklyTotalsByWeek.get(weekNumber).get(currentEmployee)}"/>	
							</g:if>				
							<g:else>
								<my:humanTimeTD id="weeklyTotalByEmployee" name="weeklyTotalByEmployee" value="0"/>							
							</g:else>			
						</td>
					</g:each>
					<g:each in="${siteFunctionMap}" var="siteFunction">
						<td style="vertical-align: middle;text-align:center;width:10px;font-size: 12px;">	
							<g:if test="${weeklyFunctionTotalMap != null && weeklyFunctionTotalMap.get(weekNumber)!= null && weeklyFunctionTotalMap.get(weekNumber).get(siteFunction.value) != null}">
								<my:humanTimeTD id="weekTimeByFunction" name="weekTimeByFunction" value="${weeklyFunctionTotalMap.get(weekNumber).get(siteFunction.value)}"/>	

							</g:if>				
							<g:else>
								<my:humanTimeTD id="weekTimeByFunction" name="weekTimeByFunction" value="0"/>							
							</g:else>				
						</td>
					</g:each>
					
					<td style="vertical-align:middle;text-align:center;font-size:12px;">
						<g:textField id="${'particularity1-'+calendarMonday.time.format('yyyyMMdd')}" name="cases" class='weeklyTextField' value="${weeklyCasesMap != null && weeklyCasesMap.get(weekNumber) != null  && (weeklyCasesMap.get(weekNumber)).particularity1 != null ? weeklyCasesMapParticularity1Text.get(weekNumber) :'00:00'}" align="center" style="vertical-align: middle;width:50px"/>
		                <g:remoteLink 
		                		action="modifyCases" 
		                		controller="employee"
		                		before="WeekJSClass.setParams('particularity1',${calendarMonday.time.format('yyyyMMdd')},${site.id},${period.id},'${simpleFuntionCheckBoxesMap}')" 
				                params="WeekJSClass.dynamicParams"		         
		                    	update="weeklyTable"
		                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
		                    	onComplete="document.getElementById('spinner').style.display = 'none';
											document.getElementById('weekly-table').className='showDomicile';
											document.getElementById('domicileSelector').style.display = 'block';
											document.getElementById('employeeSelector').style.display = 'none';
											document.getElementById('detail').value = '1';">
		                    	<g:img dir="images" file="skin/refresh.png" width="16" height="16" style="vertical-align: middle;"/>
		                    </g:remoteLink>	
		            		
					</td>
					<td style="vertical-align: middle;text-align:center;font-size: 12px;">
						<g:textField id="${'particularity2-'+calendarMonday.time.format('yyyyMMdd')}" name="cases" class='weeklyTextField' value="${weeklyCasesMap != null && weeklyCasesMap.get(weekNumber) != null  && (weeklyCasesMap.get(weekNumber)).particularity2 != null ? weeklyCasesMapParticularity2Text.get(weekNumber) :'00:00'}" align="center" style="vertical-align: middle;width:50px"/>
		                <g:remoteLink 
		                		action="modifyCases" 
		                		controller="employee"
		                		before="WeekJSClass.setParams('particularity2',${calendarMonday.time.format('yyyyMMdd')},${site.id},${period.id},'${simpleFuntionCheckBoxesMap}')" 
				                params="WeekJSClass.dynamicParams"		         
		                    	update="weeklyTable"
		                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
		                    	onComplete="document.getElementById('spinner').style.display = 'none';											
		                    				document.getElementById('weekly-table').className='showDomicile';
											document.getElementById('domicileSelector').style.display = 'block';
											document.getElementById('employeeSelector').style.display = 'none';
											document.getElementById('detail').value = '1';">
		                    	<g:img dir="images" file="skin/refresh.png" width="16" height="16" style="vertical-align: middle;"/>
		                    </g:remoteLink>			
					</td>
					
					<td style="vertical-align: middle;text-align:center;width:10px;font-size: 12px;">
						<g:if test="${weeklySubTotalsByWeek != null && weeklySubTotalsByWeek.get(weekNumber) != null}">
							<my:humanTimeTD id="weeklyGrandTotal" name="weeklyGrandTotal" value="${weeklySubTotalsByWeek.get(weekNumber)}"/>
						</g:if>
						<g:else>
							<my:humanTimeTD id="weeklyGrandTotal" name="weeklyGrandTotal" value="${0}"/>		
						</g:else>

					</td>	
					<td style="vertical-align: middle;text-align:center;width:10px;font-size: 12px;" class="domicile">
						<g:textField id="${'cases-'+calendarMonday.time.format('yyyyMMdd')}" name="cases" class='weeklyTextField' value="${weeklyCasesMap != null && weeklyCasesMap.get(weekNumber) != null  && (weeklyCasesMap.get(weekNumber)).cases != null ? weeklyCasesMap.get(weekNumber).cases :'0'}" align="center" style="vertical-align: middle;width:30px"/>
		                <g:remoteLink 
		                		action="modifyCases" 
		                		controller="employee"
		                		before="WeekJSClass.setParams('cases',${calendarMonday.time.format('yyyyMMdd')},${site.id},${period.id},'${simpleFuntionCheckBoxesMap}')" 
				                params="WeekJSClass.dynamicParams"		         
		                    	update="weeklyTable"
		                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
		                    	onComplete="document.getElementById('spinner').style.display = 'none';
											document.getElementById('weekly-table').className='showDomicile';
											document.getElementById('domicileSelector').style.display = 'block';
											document.getElementById('employeeSelector').style.display = 'none';
											document.getElementById('detail').value = '1';">
		                    	<g:img dir="images" file="skin/refresh.png" width="16" height="16" style="vertical-align: middle;"/>
		           		</g:remoteLink>			
					</td>
					
					<td style="vertical-align: middle;text-align:center;width:25px;font-size: 12px;" class="domicile">
						<g:textField id="${'home_assistance-'+calendarMonday.time.format('yyyyMMdd')}" name="home_assistance" class='weeklyTextField' value="${weeklyCasesMap != null && weeklyCasesMap.get(weekNumber) != null  && (weeklyCasesMap.get(weekNumber)).home_assistance != null ? weeklyCasesMap.get(weekNumber).home_assistance :'0'}" align="center" style="vertical-align: middle;width:20px"/>
		                <g:remoteLink 
		                		action="modifyCases" 
		                		controller="employee"
		                		before="WeekJSClass.setParams('home_assistance',${calendarMonday.time.format('yyyyMMdd')},${site.id},${period.id},'${simpleFuntionCheckBoxesMap}')" 
				                params="WeekJSClass.dynamicParams"		         
		                    	update="weeklyTable"
		                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
		                    	onComplete="document.getElementById('spinner').style.display = 'none';
		                    				document.getElementById('weekly-table').className='showDomicile';
											document.getElementById('domicileSelector').style.display = 'block';
											document.getElementById('employeeSelector').style.display = 'none';
											document.getElementById('detail').value = '1';">
		                    	<g:img dir="images" file="skin/refresh.png" width="16" height="16" style="vertical-align: middle;"/>
		           		</g:remoteLink>			
					</td>	
				</tr>
			</g:each>		
			<tr id='totals' class="${((j) % 2) == 0 ? 'even' : 'odd'}" style='font-weight: bold;'>
				<td>${message(code: 'weekly.report.totals')}</td>
				<g:each in="${employeeInstanceList}" var="currentEmployee">	
					<td style="vertical-align: middle;text-align:center;width:25px;font-size: 12px;">
						<g:if test="${yearlyTotalsByEmployee != null && yearlyTotalsByEmployee.get(currentEmployee) != null}"><my:humanTimeTD id="yearlyTotalsByEmployee" name="yearlyTotalsByEmployee" value="${yearlyTotalsByEmployee.get(currentEmployee)}"/></g:if>
						<g:else><my:humanTimeTD id="yearlyTotalsByEmployee" name="yearlyTotalsByEmployee" value="${0}"/></g:else>
					</td>	
				</g:each>
				<g:each in="${siteFunctionMap}" var="siteFunction">
					<td style="vertical-align: middle;text-align:center;width:25px;font-size: 12px;">
						<g:if test="${yearlyTotalsByFunction != null && yearlyTotalsByFunction.get(siteFunction.value) != null}"><my:humanTimeTD id="yearlyTotalsByFunction" name="yearlyTotalsByFunction" value="${yearlyTotalsByFunction.get(siteFunction.value)}"/></g:if>
						<g:else><my:humanTimeTD id="yearlyTotalsByFunction" name="yearlyTotalsByFunction" value="${0}"/></g:else>
					</td>
				</g:each>
				<td style="vertical-align: middle;text-align:center;width:60px;font-size: 12px;"><my:humanTimeTD id="yearlyParticularity1" name="yearlyParticularity1" value="${yearlyParticularity1}"/></td>
				<td style="vertical-align: middle;text-align:center;width:60px;font-size: 12px;"><my:humanTimeTD id="yearlyParticularity2" name="yearlyParticularity2" value="${yearlyParticularity2}"/></td>
				<td style="vertical-align: middle;text-align:center;width:60px;font-size: 12px;"><my:humanTimeTD id="yearlySubTotals" name="yearlySubTotals" value="${yearlySubTotals}"/></td>
				<td style="vertical-align: middle;text-align:center;width:60px;font-size: 12px;" class="domicile">${yearlyCases}</td>
				<td style="vertical-align: middle;text-align:center;width:60px;font-size: 12px;" class="domicile">${yearlyHomeAssistance}</td>
			</tr>
		</tbody>
	</table>
</g:if>
