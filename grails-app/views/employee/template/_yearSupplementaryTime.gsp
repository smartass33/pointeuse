<table style="float: left; border-style:none;">
		<tr>
			<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.sup.above.42.time', default: 'report')} :</td>				
			<g:if test="${monthlySupTime!=null}">
 					<td style="font-weight:bold" class="cartoucheRightFiguresTD">
						${monthlySupTime} ou ${monthlySupTimeDecimal}
 					 </td>  	 
 				</g:if>
 					 
 			 <td style="font-weight:bold; text-align: center;border-width:0px;" class="cartoucheRightFiguresTD">  
				<g:if test="${ajaxYearlySupTime != null}">
						${ajaxYearlySupTime} ou ${ajaxYearlySupTimeDecimal}
				</g:if>
				<g:else>
					${message(code: 'ajax.loading.label', default: 'report')}
				</g:else>	  		
			</td>			
		</tr>
	<g:if test="${payableCompTime!=null && employee.weeklyContractTime != 35}">						
		<tr>
			<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
			<td class="cartoucheRightFiguresTD">${payableCompTime.get(0)}:${payableCompTime.get(1)} ou ${(payableCompTime.get(0)+payableCompTime.get(1)/60).setScale(2,2)}</td>
			<td class="cartoucheRightFiguresTD"/>
		</tr>
	</g:if>	
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.timeBefore7', default: 'report')} :</td>		
		
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${timeBefore7 != null}">
						${timeBefore7} ou ${timeBefore7Decimal}
	  			</g:if>
				<g:else>
					${message(code: 'ajax.loading.label', default: 'report')}	
				</g:else>	  		
			</td>
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${yearTimeBefore7 != null}">
						${yearTimeBefore7} ou ${yearTimeBefore7Decimal}	  			
				</g:if>
				<g:else>
					${message(code: 'ajax.loading.label', default: 'report')}
				</g:else>	  		
		</td>				
	</tr>
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.timeAfter20', default: 'report')} :</td>		
		
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${timeAfter20 != null}">
						${timeAfter20} ou ${timeAfter20Decimal}	  			
	  			</g:if>
				<g:else>
					${message(code: 'ajax.loading.label', default: 'report')}
				</g:else>	  		
		</td>	
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${yearTimeAfter20 != null}">
						${yearTimeAfter20} ou ${yearTimeAfter20Decimal}	  			
	  			</g:if>
				<g:else>
					${message(code: 'ajax.loading.label', default: 'report')}
				</g:else>	  		
		</td>
	</tr>
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.timeOffHours', default: 'report')} :</td>		
		
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${timeOffHours != null}">
						${timeOffHours} ou ${timeOffHoursDecimal}	  			
	  			</g:if>
				<g:else>
					${message(code: 'ajax.loading.label', default: 'report')}
				</g:else>	  		
			</td>	
			
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${yearTimeOffHours != null}">
						${yearTimeOffHours} ou ${yearTimeOffHoursDecimal}	  			
	  			</g:if>
				<g:else>
					${message(code: 'ajax.loading.label', default: 'report')}
				</g:else>	  		
		</td>
	</tr>	
</table>