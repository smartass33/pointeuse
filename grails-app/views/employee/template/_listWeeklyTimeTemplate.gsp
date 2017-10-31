<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.WeeklyTotal"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="grails.converters.JSON"%>

<g:set var="calendarMonday" value="${Calendar.instance}"/>
<g:set var="calendarFriday" value="${Calendar.instance}"/>
<g:if test="${firstYear != null}">
	<%
		calendarMonday.set(Calendar.YEAR,firstYear)
		calendarFriday.set(Calendar.YEAR,firstYear)	
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
				<th style="vertical-align: middle;text-align:center;">${employee.lastName}</th>
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
			<th style="vertical-align: middle;text-align:center;">${message(code: 'sub.total')} 	
				<g:form>
					<g:actionSubmit class='excelButton' value="export"  action="weeklyReportExcelExport"/>	
					<g:hiddenField name="funtionCheckBoxesMap" id="funtionCheckBoxesMap" value="${funtionCheckBoxesMap as JSON} " />
					<g:if test="${site != null}"><g:hiddenField name="siteId" id="siteId" value="${site.id} " /></g:if>
					<g:if test="${period != null}"><g:hiddenField name="periodId" id="periodId" value="${period.id} " /></g:if>			
				</g:form >				
			</th>
			<th style="vertical-align: middle;text-align:center;">${message(code: 'weekly.case')}</th>	
		</thead>
			
		<tbody id='body_table' style="border:1px;">
			<g:each in="${weekList}" status="i" var="weekNumber">	
				<%
					calendarMonday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)		
					calendarMonday.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
					calendarFriday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)
					calendarFriday.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY)
							
					if (weekNumber == 1){
						calendarMonday.set(Calendar.YEAR,lastYear)	
						calendarFriday.set(Calendar.YEAR,lastYear)				
					}
				%>	
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<td style="vertical-align: middle;text-align:center;">
						 ${calendarMonday.time.format('EE dd/MM/yy')}-${calendarFriday.time.format('EE dd/MM/yy')}					  
					</td>
	
					<g:each in="${employeeInstanceList}" var="currentEmployee">	
						<td style="vertical-align: middle;text-align:center;width:10px;font-size: 12px;">	
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
					<td style="vertical-align: middle;text-align:center;width:10px;font-size: 12px;">
						<g:if test="${weeklySubTotalsByWeek != null && weeklySubTotalsByWeek.get(weekNumber) != null}">
							<my:humanTimeTD id="weeklyGrandTotal" name="weeklyGrandTotal" value="${weeklySubTotalsByWeek.get(weekNumber)}"/>
						</g:if>
						<g:else>
							<my:humanTimeTD id="weeklyGrandTotal" name="weeklyGrandTotal" value="${0}"/>		
						</g:else>

					</td>	
					
					<td style="vertical-align: middle;text-align:center;width:10px;font-size: 12px;">
						<g:textField id="${calendarMonday.time.format('yyyyMMdd')}" name="cases" class='mileageTextField' value="${weeklyCasesMap.get(weekNumber).value ?: '0'}" align="center" style="vertical-align: middle;width:35px"/>
		                <g:remoteLink 
		                		action="modifyCases" 
		                		controller="employee"
		                		before="WeekJSClass.setParams(${calendarMonday.time.format('yyyyMMdd')},${site.id})" 
				                params="WeekJSClass.dynamicParams"		         
		                    	update="weeklyTable"
		                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
		                    	onComplete="document.getElementById('spinner').style.display = 'none';">
		                    	<g:img dir="images" file="skin/refresh.png" width="16" height="16" style="vertical-align: middle;"/>
		                    </g:remoteLink>			
					</td>					
				</tr>
			</g:each>		
		</tbody>
	</table>
</g:if>
