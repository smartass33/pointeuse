


<body>
	<font size="2">		
		<table width="30%" style='float:left;font-size=2' cellspacing="1" cellpadding="1" id="table1" class="table1">
			<tr>
				<td style="font-weight: bold" >${fieldValue(bean: employee, field: "firstName")} ${fieldValue(bean: employee, field: "lastName")}</td>
			</tr>
			<tr>
				<td>${message(code: 'employee.weeklyContractTime.short.label', default: 'report')}: ${fieldValue(bean: employee, field: "weeklyContractTime")}</td>
			</tr>
			<tr>
				<td>${message(code: 'employee.matricule.label', default: 'report')}: ${fieldValue(bean: employee, field: "matricule")}</td>
			</tr>
		</table> 
		
		
		<table width="50%"  valign="center" id="cartoucheTable" class="table2">						
			<tbody>		
				<tr>
					<td/>
					<td style="font-weight: bold"  align="center"><g:formatDate format="MMMMM yyyy" date="${period}"/></td>
					<td style="font-weight: bold">${message(code: 'employee.cumul', default: 'report')} ${period2}</td>
				</tr>			
				<tr>		
					<td>${message(code: 'employee.vacances.count', default: 'report')} :</td>	
					<td>${holiday as java.lang.Float}</td>        					
	     			<td>${yearlyHoliday as java.lang.Float}</td>
				</tr>
				<tr>
					<td>${message(code: 'employee.rtt.count', default: 'report')} :</td>
					<td>${rtt as java.lang.Float}</td>
	     			<td>${yearlyRtt as java.lang.Float}</td>
				</tr>
				<tr>
					<td>${message(code: 'employee.sickness.count', default: 'report')} :</td>
					<td>${sickness as java.lang.Float}</td>
	     			<td>${yearlySickness as java.lang.Float}</td>   						
				</tr>
				<tr>
					<td>${message(code: 'employee.sanssolde.count', default: 'report')} :</td>
					<td>${sansSolde as java.lang.Float}</td>
	    			<td>${yearlySansSolde as java.lang.Float}</td>
				</tr>					
				<tr>
					<td>${message(code: 'employee.yearly.theorical.time', default: 'report')} :</td>							
	    			<g:if test="${monthTheoritical!=null}">
	    				<td>${monthTheoritical.get(0)} : ${monthTheoritical.get(1)==0?'00':monthTheoritical.get(1)}</td>
	    			</g:if>
					<g:else>
						<td>00 : 00</td>
					</g:else>
   					<g:if test="${yearlyTheoritical!=null}">
   						<td>${yearlyTheoritical.get(0)} : ${yearlyTheoritical.get(1)==0?'00':yearlyTheoritical.get(1)}</td>
   					</g:if>
   					<g:else>
   						<td>00 : 00</td>
   					</g:else>
				</tr>
				<tr>
					<td>${message(code: 'employee.yearly.actual.time', default: 'report')} :</td>								 						
					<g:if test="${monthlyTotalRecap!=null}">
						<td>${monthlyTotalRecap.get(0)} : ${monthlyTotalRecap.get(1)==0?'00':monthlyTotalRecap.get(1)}</td>
					</g:if>
					<g:else>
						<td>00 : 00</td>			
					</g:else>
					<g:if test="${yearlyActualTotal!=null}">
						<td>${yearlyActualTotal.get(0)} : ${yearlyActualTotal.get(1)==0?'00':yearlyActualTotal.get(1)}</td>
					</g:if>
					<g:else>
						<td>00 : 00</td>			
					</g:else>											
				</tr>
				<tr>
					<td>${message(code: 'employee.peculiar.time', default: 'report')} :</td>							
					<g:if test="${pregnancyCredit!=null}">  						
						<td>${pregnancyCredit.get(0)} : ${pregnancyCredit.get(1)==0?'00':pregnancyCredit.get(1)}</td>
					</g:if>
					<g:else>
						<td>00 : 00</td>			
					</g:else>	        								
					<g:if test="${yearlyPregnancyCredit!=null}">  											
						<td>${yearlyPregnancyCredit.get(0)} : ${yearlyPregnancyCredit.get(1)==0?'00':yearlyPregnancyCredit.get(1)}</td>
					</g:if>
					<g:else>
						<td>00 : 00</td>			
					</g:else>											
				</tr>
				<tr>
					<td>${message(code: 'default.monthly.open.day', default: 'report')} :</td>				
    					<td style="font-weight:bold">${workingDays}</td>
    					<td/>
				</tr>
				<g:if test="${payableSupTime!=null}">
					<tr>
						<td>${message(code: 'employee.monthly.sup.time', default: 'report')} :</td>				
      					<td style="font-weight:bold">${payableSupTime.get(0)} : ${payableSupTime.get(1)==0?'00':payableSupTime.get(1)} ou ${(payableSupTime.get(0)+payableSupTime.get(1)/60).setScale(2,2)}</td>        					
						<td/>	
					</tr>
				</g:if>
				<g:if test="${payableCompTime!=null && employee.weeklyContractTime != 35}">						
					<tr>
						<td>${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
						<td>${payableCompTime.get(0)} : ${payableCompTime.get(1)==0?'00':payableCompTime.get(1)} ou  ${(payableCompTime.get(0)+payableCompTime.get(1)/60).setScale(2,2)}</td>
						<td/>
					</tr>
				</g:if>							
			</tbody>
		</table>
	</font>
</body>	
