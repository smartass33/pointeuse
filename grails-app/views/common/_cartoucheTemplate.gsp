<body>
	<BR>
		<table style="float: left;">
			<tr>
				<td style="font-weight: bold;" class="cartoucheLeftTD" >
						<g:link action="edit" id="${employee.id}" style="text-decoration: none;"
							params="${[isAdmin:isAdmin,siteId:siteId,myDate:period.format('MM/yyyy'),back:true]}">
							${fieldValue(bean: employee, field: "firstName")} ${fieldValue(bean: employee, field: "lastName")}
						</g:link>
				</td>
			</tr>
			<tr>
				<td class="cartoucheLeftTD" >${message(code: 'employee.weeklyContractTime.short.label', default: 'report')}: ${currentContract.weeklyLength}</td>
			</tr>
			<tr>
				<td class="cartoucheLeftTD" >${message(code: 'employee.matricule.label', default: 'report')}: ${fieldValue(bean: employee, field: "matricule")}</td>
			</tr>
		</table> 
				
		<table >						
			<tbody>		
				<tr>
					<td class="cartoucheRightFiguresTD" />
					<td class="cartoucheRightFiguresTD" style="font-weight: bold"  align="center"><g:formatDate format="MMMMM yyyy" date="${period}"/></td>
					<td style="font-weight: bold" class="cartoucheRightFiguresTD">${message(code: 'employee.cumul', default: 'report')} ${period2}</td>
					<td style="font-weight: bold" class="cartoucheRightFiguresTD">${message(code: 'employee.remaining', default: 'report')} ${period2}</td>
				</tr>			
				<tr>		
					<td class="cartoucheRightTitleTD" >${message(code: 'employee.vacances.count', default: 'report')} :</td>	
					<td class="cartoucheRightFiguresTD">${holiday as java.lang.Float}</td>        					
	     			<td class="cartoucheRightFiguresTD">${yearlyHoliday as java.lang.Float}</td>
				</tr>
				<tr>
					<td class="cartoucheRightTitleTD" >${message(code: 'employee.rtt.count', default: 'report')} :</td>
					<td class="cartoucheRightFiguresTD">${rtt as java.lang.Float}</td>
	     			<td class="cartoucheRightFiguresTD">${yearlyRtt as java.lang.Float}</td>
				</tr>
				<tr>
					<td class="cartoucheRightTitleTD" >${message(code: 'employee.sickness.count', default: 'report')} :</td>
					<td class="cartoucheRightFiguresTD">${sickness as java.lang.Float}</td>
	     			<td class="cartoucheRightFiguresTD">${yearlySickness as java.lang.Float}</td>   						
				</tr>
				<tr>
					<td class="cartoucheRightTitleTD" >${message(code: 'employee.sanssolde.count', default: 'report')} :</td>
					<td class="cartoucheRightFiguresTD">${sansSolde as java.lang.Float}</td>
	    			<td class="cartoucheRightFiguresTD">${yearlySansSolde as java.lang.Float}</td>
				</tr>					
				<tr>
					<td class="cartoucheRightTitleTD" >${message(code: 'employee.yearly.theorical.time', default: 'report')} :</td>							
	    			<g:if test="${monthTheoritical!=null}">
	    				<td class="cartoucheRightFiguresTD">${monthTheoritical.get(0)}:${monthTheoritical.get(1)}</td>
	    			</g:if>
					<g:else>
						<td class="cartoucheRightFiguresTD">00:00</td>
					</g:else>
   					<g:if test="${yearlyTheoritical!=null}">
   						<td class="cartoucheRightFiguresTD">${yearlyTheoritical.get(0)}:${yearlyTheoritical.get(1)}</td>
   					</g:if>
   					<g:else>
   						<td class="cartoucheRightFiguresTD">00:00</td>
   					</g:else>
				</tr>
				<tr>
					<td class="cartoucheRightTitleTD" >${message(code: 'employee.yearly.actual.time', default: 'report')} :</td>								 						
					<g:if test="${monthlyTotalRecapAsString!=null}">
						<td class="cartoucheRightFiguresTD">${monthlyTotalRecapAsString.get(0)}:${monthlyTotalRecapAsString.get(1)}</td>
					</g:if>
					<g:else>
						<td class="cartoucheRightFiguresTD">00:00</td>			
					</g:else>
					<g:if test="${yearlyActualTotal!=null}">
						<td class="cartoucheRightFiguresTD">${yearlyActualTotal.get(0)}:${yearlyActualTotal.get(1)}</td>
					</g:if>
					<g:else>
						<td class="cartoucheRightFiguresTD">00:00</td>			
					</g:else>											
				</tr>
				<tr>
					<td class="cartoucheRightTitleTD" >${message(code: 'employee.peculiar.time', default: 'report')} :</td>							
					<g:if test="${pregnancyCredit!=null}">  
						<td class="cartoucheRightFiguresTD">${pregnancyCredit.get(0)}:${pregnancyCredit.get(1)}</td>
					</g:if>
					<g:else>
						<td class="cartoucheRightFiguresTD">00:00</td>			
					</g:else>	        								
					<g:if test="${yearlyPregnancyCredit!=null}">
						<td class="cartoucheRightFiguresTD">${yearlyPregnancyCredit.get(0)}:${yearlyPregnancyCredit.get(1)}</td>		
					</g:if>
					<g:else>
						<td class="cartoucheRightFiguresTD">00:00</td>			
					</g:else>											
				</tr>
				<tr>
					<td class="cartoucheRightTitleTD" >${message(code: 'default.monthly.open.day', default: 'report')} :</td>				
    					<td style="font-weight:bold" class="cartoucheRightFiguresTD">${workingDays}<g:if test="${isCurrentMonth}">(en cours)</g:if>
    					
    					</td>
    					<td class="cartoucheRightFiguresTD"/>
				</tr>
				<g:if test="${payableSupTime!=null}">
					<tr>
						<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.sup.time', default: 'report')} :</td>				
      					<td style="font-weight:bold" class="cartoucheRightFiguresTD">
      					<g:if test="${payableSupTime.get(0)>9}">${payableSupTime.get(0)}:</g:if>
      					<g:else>0${payableSupTime.get(0)}:</g:else>
      					<g:if test="${payableSupTime.get(1)>9}">${payableSupTime.get(1)}</g:if>
      					<g:else>${payableSupTime.get(1)}0</g:else>
      					 ou ${(payableSupTime.get(0)+payableSupTime.get(1)/60).setScale(2,2)}</td>        					
						<td class="cartoucheRightFiguresTD" />	
					</tr>
				</g:if>
				<g:if test="${payableCompTime!=null && employee.weeklyContractTime != 35}">						
					<tr>
						<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
						<td class="cartoucheRightFiguresTD">${payableCompTime.get(0)}:${payableCompTime.get(1)} ou ${(payableCompTime.get(0)+payableCompTime.get(1)/60).setScale(2,2)}</td>
						<td class="cartoucheRightFiguresTD"/>
					</tr>
				</g:if>							
			</tbody>
		</table>
</body>	
