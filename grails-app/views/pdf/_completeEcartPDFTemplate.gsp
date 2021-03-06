<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 <html>
<head>

	<%@ page import="pointeuse.Site"%>
	<%@ page import="pointeuse.Employee"%>
	<%@ page import="pointeuse.InAndOut"%>
	
	
	<g:set var="calendar" value="${Calendar.instance}"/>
	<style  type="text/css">
		@page {
		   size: 297mm 210mm;
		   margin: 5px 10px 10px 10px;

		}
		.eventTD {
		    font:14px Georgia, serif;
		    padding:2px 2px;
		    text-align:center;
		   // background-color:#DEF3CA;
		    border: 1px solid #E7EFE0;
		    -moz-border-radius:2px;
		    -webkit-border-radius:2px;
		    border-radius:2px;
		    color:#666;
		    text-shadow:1px 1px 1px #fff;
		    width:80px;
		}
			
	</style>
	
</head>


<body>

	<h1>SITE: ${site.name }  - PERIODE: ${period}</h1>

	<table style="table-layout: fixed;width:100%;"  >
		<thead>
			<th class="eventTD"/>
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
				<g:if test="${month_th>5}">
					<th class="eventTD" style="width:50px">${period.year}</th>
				</g:if>
				<g:else>
					<th class="eventTD" style="width:50px">${period.year + 1}</th>		
				</g:else>
			</g:each>		
	
		</thead>
	
		<tbody id='body_update' style="border:1px;">
			<tr>
				<td class="eventTD" />		
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th_2'>		
					<%= calendar.set(Calendar.MONTH,month_th_2-1) %>
					<td class="eventTD" style="vertical-align: middle;">${calendar.time.format('MMM') }</td>
				</g:each>
			</tr>	
			
			<g:if test="${employeeInstanceList != null}">
				<th colspan='13' class="eventTD" style="text-align:left;font-weight:bold;text-transform: uppercase;">${message(code: 'ecart.totals', default: 'report')}</th>			
					<tr>
						<td class="eventTD" style="text-align:left;">${message(code: 'ecart.total.theoritical', default: 'report')}</td>	
						<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
							<g:if test='${totalMonthlyTheoritical != null}'>
								<g:if test="${monthList != null && monthList.contains(month_th)}">										
									<td class="eventTD"><my:humanTimeTD id="totalMonthlyTheoritical"  name="totalMonthlyTheoritical" value="${totalMonthlyTheoritical.get(month_th)}"/></td>	
								</g:if>
								<g:else>
									<td class="eventTD" style="background-color:#CCCCCE"/>						
								</g:else>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:each>
					</tr>
					<tr>
						<td class="eventTD" style="text-align:left;">${message(code: 'ecart.total.actuals', default: 'report')}</td>	
						<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
							<g:if test='${totalMonthlyActual != null}'>
								<g:if test="${monthList != null && monthList.contains(month_th)}">										
									<td class="eventTD"><my:humanTimeTD id="totalMonthlyActual"  name="totalMonthlyActual" value="${totalMonthlyActual.get(month_th)}"/></td>	
								</g:if>
								<g:else>
									<td class="eventTD" style="background-color:#CCCCCE"/>						
								</g:else>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:each>
					</tr>				
					<tr>
						<td class="eventTD" style="text-align:left;">${message(code: 'ecart.total.delta', default: 'report')}</td>	
						<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
							<g:if test='${totalEcart != null}'>
								<g:if test="${monthList != null && monthList.contains(month_th)}">										
									<td class="eventTD"><my:humanTimeTD id="totalEcart"  name="totalEcart" value="${totalEcart.get(month_th)}"/></td>	
								</g:if>
								<g:else>
									<td class="eventTD" style="background-color:#CCCCCE"/>						
								</g:else>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:each>
					</tr>	
					<tr>
						<td class="eventTD" style="text-align:left;">${message(code: 'ecart.total.rtt', default: 'report')}</td>	
						<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
							<g:if test='${totalTakenRTT != null}'>
								<g:if test="${monthList != null && monthList.contains(month_th)}">										
									<td class="eventTD">${totalTakenRTT.get(month_th)}</td>	
								</g:if>
								<g:else>
									<td class="eventTD" style="background-color:#CCCCCE"/>						
								</g:else>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:each>
					</tr>
				
					<tr>
						<td class="eventTD" style="text-align:left;">${message(code: 'ecart.total.delta.minus.rtt', default: 'report')}</td>	
						<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
							<g:if test='${totalEcartMinusRTT != null}'>
								<g:if test="${monthList != null && monthList.contains(month_th)}">										
									<td class="eventTD"><my:humanTimeTD id="totalEcartMinusRTT"  name="totalEcartMinusRTT" value="${totalEcartMinusRTT.get(month_th)}"/></td>	
								</g:if>
								<g:else>
									<td class="eventTD" style="background-color:#CCCCCE"/>						
								</g:else>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:each>
					</tr>
					<tr>
						<td class="eventTD" style="text-align:left;">${message(code: 'ecart.total.suptime', default: 'report')}</td>	
						<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
							<g:if test='${totalSupTime != null}'>
								<g:if test="${monthList != null && monthList.contains(month_th)}">										
									<td class="eventTD"><my:humanTimeTD id="totalSupTime"  name="totalSupTime" value="${totalSupTime.get(month_th)}"/></td>	
								</g:if>
								<g:else>
									<td class="eventTD" style="background-color:#CCCCCE"/>						
								</g:else>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:each>
					</tr>
					<tr>
						<td class="eventTD" style="text-align:left;">${message(code: 'ecart.total.delta.minus.rtt.suptime', default: 'report')}</td>	
						<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
							<g:if test='${totalEcartMinusRTTAndHS != null}'>
								<g:if test="${monthList != null && monthList.contains(month_th)}">										
									<td class="eventTD"><my:humanTimeTD id="totalEcartMinusRTTAndHS"  name="totalEcartMinusRTTAndHS" value="${totalEcartMinusRTTAndHS.get(month_th)}"/></td>	
								</g:if>
								<g:else>
									<td class="eventTD" style="background-color:#CCCCCE"/>						
								</g:else>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:each>
					</tr>
			</g:if>	
			
												
			<g:each in="${employeeInstanceList}" status="i" var="employee">
				<th colspan='13' class="eventTD" style="text-align:left;font-weight:bold;">${employee.firstName} ${employee.lastName}</th>
				<tr>
					<td class="eventTD" style="text-align:left;">&Sigma; théorique</td>	
					<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_theoritical'>	
						<g:if test="${monthlyTheoriticalByEmployee.get(employee)!=null}">
							<g:if test="${monthList.contains(month_theoritical)}">
								<td class="eventTD"><my:humanTimeTD id="theoritical" name="theoritical" value="${monthlyTheoriticalByEmployee.get(employee).get(month_theoritical)}"/></td>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:if>
					</g:each>		
				</tr>
				<tr>
					<td class="eventTD" style="text-align:left;">&Sigma; réalisées</td>
					<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_actual'>						
						<g:if test='${monthlyActualByEmployee.get(employee)!=null}'>
							<g:if test="${monthList.contains(month_actual)}">
								<td class="eventTD"><my:humanTimeTD id="month_actual" name="month_actual" value="${monthlyActualByEmployee.get(employee).get(month_actual)}"/></td>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:if>
					</g:each>
				</tr>		
				<tr>
					<td class="eventTD" style="text-align:left;">&Delta; HR - HT</td>
					<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_ecart'>									
						<g:if test='${ecartByEmployee.get(employee)!=null}'>
							<g:if test="${monthList.contains(month_ecart)}">
								<td class="eventTD"><my:humanTimeTD id="ecartByEmployee" name="ecartByEmployee" value="${ecartByEmployee.get(employee).get(month_ecart)}"/></td>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:if>
					</g:each>
				</tr>
				<tr>
					<td class="eventTD" style="text-align:left;">RTT restant</td>
					<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
						<g:if test='${rttByEmployee.get(employee)!=null}'>
							<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
								<td class="eventTD">${rttByEmployee.get(employee).get(month_rtt)}</td>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:each>
				</tr>
				<tr>
					<td class="eventTD" style="text-align:left;">&Delta; corrigé des RTT</td>
					<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
						<g:if test='${ecartMinusRTTByEmployee.get(employee)!=null}'>
							<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
								<td class="eventTD"><my:humanTimeTD id="ecartMinusRTTByEmployee" name="ecartMinusRTTByEmployee" value="${ecartMinusRTTByEmployee.get(employee).get(month_rtt)}"/></td>
							</g:if>
							<g:else>
								<td class="eventTD" style="background-color:#CCCCCE"/>
							</g:else>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:each>
				</tr>	
			<tr>
				<td class="eventTD" style="text-align:left;">&Sigma; HS cumulées</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
					<g:if test='${monthlySupTimeMapByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
							<td class="eventTD"><my:humanTimeTD id="monthlySupTimeMapByEmployee" name="monthlySupTimeMapByEmployee" value="${monthlySupTimeMapByEmployee.get(employee).get(month_rtt)}"/></td>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
					<g:else>
						<td class="eventTD" style="background-color:#CCCCCE"/>
					</g:else>
				</g:each>
			</tr>
			<tr>
				<td class="eventTD" style="text-align:left;">&Delta; corrigé des RTT et HS</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
					<g:if test='${ecartMinusRTTAndHSByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
							<td class="eventTD"><my:humanTimeTD id="ecartMinusRTTAndHSByEmployee" name="ecartMinusRTTAndHSByEmployee" value="${ecartMinusRTTAndHSByEmployee.get(employee).get(month_rtt)}"/></td>
						</g:if>
						<g:else>
							<td class="eventTD" style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
					<g:else>
						<td class="eventTD" style="background-color:#CCCCCE"/>
					</g:else>
				</g:each>
			</tr>					
						
			</g:each>
		</tbody>
	</table>


	</body>
</html>