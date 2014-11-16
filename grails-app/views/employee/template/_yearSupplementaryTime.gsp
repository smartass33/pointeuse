<table style="float: left; border-style:none;">
		<tr>
			<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.sup.time', default: 'report')} :</td>				
			<g:if test="${monthlySupTime!=null}">
 					<td style="font-weight:bold" class="cartoucheRightFiguresTD">
	  					<g:if test="${monthlySupTime.get(0)>9}">${monthlySupTime.get(0)} :</g:if>
	  					<g:else>0${monthlySupTime.get(0)} :</g:else>
	  					<g:if test="${monthlySupTime.get(1)>9}">${monthlySupTime.get(1)}</g:if>
	  					<g:else>0${monthlySupTime.get(1)}</g:else>
	  					 ou ${(monthlySupTime.get(0)+monthlySupTime.get(1)/60).setScale(2,2)}
 					 </td>  	 
 				</g:if>
 					 
 			 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${ajaxYearlySupTime != null}">
					${ajaxYearlySupTime}
				</g:if>
				<g:else>
					chargement...
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
	  					${timeBefore7}
	  			</g:if>
				<g:else>
					chargement...
				</g:else>	  		
			</td>
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${yearTimeBefore7 != null}">
	  					${yearTimeBefore7}
	  			</g:if>
				<g:else>
					chargement...
				</g:else>	  		
		</td>								
	</tr>
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.timeAfter20', default: 'report')} :</td>		
		
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${timeAfter20 != null}">
	  					${timeAfter20}
	  			</g:if>
				<g:else>
					chargement...
				</g:else>	  		
		</td>	
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${yearTimeAfter20 != null}">
	  					${yearTimeAfter20}
	  			</g:if>
				<g:else>
					chargement...
				</g:else>	  		
		</td>							
	</tr>
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.timeOffHours', default: 'report')} :</td>		
		
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${timeOffHours != null}">
	  					${timeOffHours}
	  			</g:if>
				<g:else>
					chargement...
				</g:else>	  		
			</td>	
			
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
				<g:if test="${yearTimeOffHours != null}">
	  					${yearTimeOffHours}
	  			</g:if>
				<g:else>
					chargement...
				</g:else>	  		
		</td>						
	</tr>	
</table>