<body>
	<font size="2">
		<table width="100%" class="cartoucheTable" cellspacing="1" cellpadding="1" >
		<thead></thead>
		<tbody>
			<tr>
				<td width="20%">
					<table width="100%"  cellspacing="1" cellpadding="1">
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
				</td>
				<td width="80%">
					<table width="100%" valign="center" class="cartouche">						
						<tbody>		
							<tr><td style="font-weight: bold" colspan="2" align="center"><g:formatDate format="MMMMM yyyy" date="${period}"/></td>
							<td style="font-weight: bold">${message(code: 'employee.cumul', default: 'report')} ${period2}</td>
							</tr>			
							<tr>
							
								<td width="35%" >${message(code: 'employee.vacances.count', default: 'report')} :</td>	
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${holiday as java.lang.Float}</td></tr>
	        						</table>
	        					</td>        					
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>
	        							
	        							<tr><td>${yearlyHoliday as java.lang.Float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td>${message(code: 'employee.rtt.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${rtt as java.lang.Float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlyRtt as java.lang.Float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td>${message(code: 'employee.sickness.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${sickness as java.lang.Float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlySickness as java.lang.Float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td>${message(code: 'employee.sanssolde.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${sansSolde as java.lang.Float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlySansSolde as java.lang.Float}</td></tr>
	        						</table>
	        					</td>
							</tr>					
							<tr>
								<td>${message(code: 'employee.yearly.theorical.time', default: 'report')} :</td>							
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr>
	        								<g:if test="${monthTheoritical!=null}">
	        									<td>${monthTheoritical.get(0)}H${monthTheoritical.get(1)==0?'00':monthTheoritical.get(1)}</td>
	        								</g:if>
	        								<g:else>
	        									<td>0H0</td>
	        								</g:else>
	        							</tr>
	        						</table>
	        					</td>	
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr>
	        								<g:if test="${yearlyTheoritical!=null}">
	        									<td>${yearlyTheoritical.get(0)}H${yearlyTheoritical.get(1)==0?'00':yearlyTheoritical.get(1)}</td>
	        								</g:if>
	        								<g:else>
	        									<td>0H0</td>
	        								</g:else>
	        							</tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td>${message(code: 'employee.yearly.actual.time', default: 'report')} :</td>							
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr>
	 									<g:if test="${monthlyTotalRecap!=null}">
	 										<td>${monthlyTotalRecap.get(0)}H${monthlyTotalRecap.get(1)==0?'00':monthlyTotalRecap.get(1)}</td>
	 									</g:if>
	 									<g:else>
	 										<td>00H00</td>			
	 									</g:else>
	 									</tr>
	 									
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr>
	        								<g:if test="${yearlyActualTotal!=null}">
	        									<td>${yearlyActualTotal.get(0)}H${yearlyActualTotal.get(1)==0?'00':yearlyActualTotal.get(1)}</td>
	        								</g:if>
		 									<g:else>
		 										<td>00H00</td>			
		 									</g:else>
	        							</tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td>${message(code: 'employee.peculiar.time', default: 'report')} :</td>							
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	        							<tr>
	        								<g:if test="${pregnancyCredit!=null}">  						
	        									<td>${pregnancyCredit.get(0)}H${pregnancyCredit.get(1)==0?'00':pregnancyCredit.get(1)}</td>
	        								</g:if>
		 									<g:else>
		 										<td>00H00</td>			
		 									</g:else>	        								
	        							</tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr>
	        								<g:if test="${yearlyPregnancyCredit!=null}">  											
	        									<td>${yearlyPregnancyCredit.get(0)}H${yearlyPregnancyCredit.get(1)==0?'00':yearlyPregnancyCredit.get(1)}</td>
											</g:if>
		 									<g:else>
		 										<td>00H00</td>			
		 									</g:else>											
	        							</tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td>${message(code: 'default.monthly.open.day', default: 'report')} :</td>				
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold">${workingDays}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<g:if test="${payableSupTime!=null}">
								<tr>
									<td>${message(code: 'employee.monthly.sup.time', default: 'report')} :</td>				
									<td>
		        						<table border="1" class="cartoucheValues" >
		        							<thead></thead>
		        							<tr><td style="font-weight:bold">${payableSupTime.get(0)}H${payableSupTime.get(1)==0?'00':payableSupTime.get(1)}</td><td style="font-weight:bold">${(payableSupTime.get(0)+payableSupTime.get(1)/60).setScale(2,2)}H</td></tr>
		        						</table>
		        					</td>
								</tr>
							</g:if>
							<g:if test="${payableCompTime!=null && employee.weeklyContractTime != 35}">						
								<tr>
									<td>${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
									<td>
		        						<table border="1" class="cartoucheValues" >
		        							<thead></thead>
		        							<tr><td style="font-weight:bold">${payableCompTime.get(0)}H${payableCompTime.get(1)==0?'00':payableCompTime.get(1)}</td><td style="font-weight:bold">${(payableCompTime.get(0)+payableCompTime.get(1)/60).setScale(2,2)}H</td></tr>
		        						</table>
		        					</td>
								</tr>
							</g:if>							
						</tbody>
					</table> 
				</td>
			</tr>
			</tbody>
		</table>
	</font>
</body>	
