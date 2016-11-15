<table style="float: left; border-style:none;">
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.sup.above.42.time', default: 'report')} :</td>				
		<g:if test="${monthlySupTime!=null}">
			<td style="font-weight:bold" class="cartoucheRightFiguresTD">
				<my:humanTimeTD id="monthlySupTime"  name="monthlySupTime" value="${monthlySupTime} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="monthlySupTimeDecimal"  name="monthlySupTimeDecimal" value="${monthlySupTime} "/>
			 </td>  	 
		</g:if>
					 
		<td style="font-weight:bold; text-align: center;border-width:0px;" class="cartoucheRightFiguresTD">  
			<g:if test="${ajaxYearlySupTime != null}">
				<my:humanTimeTD id="ajaxYearlySupTime"  name="ajaxYearlySupTime" value="${ajaxYearlySupTime} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="ajaxYearlySupTimeDecimal"  name="ajaxYearlySupTimeDecimal" value="${ajaxYearlySupTime} "/>
			</g:if>
			<g:else>
				${message(code: 'ajax.loading.label', default: 'report')}
			</g:else>	  		
		</td>			
	</tr>
	
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.HS.HT', default: 'report')} :</td>				
		<g:if test="${monthlySupTime!=null && monthTheoritical != null}">
			<td style="font-weight:bold" class="cartoucheRightFiguresTD">
				<my:humanTimeTD id="monthlySupTime"  name="monthlySupTime" value="${monthlySupTime+monthTheoritical} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="HSHT"  name="HSHT" value="${monthlySupTime+monthTheoritical} "/>
			 </td>  	 
		</g:if>
					 
		<td style="font-weight:bold; text-align: center;border-width:0px;" class="cartoucheRightFiguresTD">  
			<g:if test="${ajaxYearlySupTime != null && yearTheoritical!= null}">
				<my:humanTimeTD id="ajaxYearlySupTime"  name="ajaxYearlySupTime" value="${ajaxYearlySupTime + yearTheoritical} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="YHSHT"  name="YHSHT" value="${ajaxYearlySupTime + yearTheoritical} "/>
			</g:if>
			<g:else>
				${message(code: 'ajax.loading.label', default: 'report')}
			</g:else>	  		
		</td>			
	</tr>
	
	<g:if test="${payableCompTime!=null && employee.weeklyContractTime != 35}">						
		<tr>
			<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
			<td class="cartoucheRightFiguresTD"><my:humanTimeTD id="payableCompTime"  name="payableCompTime" value="${payableCompTime} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="payableCompTimeDE"  name="payableCompTimeDE" value="${monthlySupTime+payableCompTime} "/>
		</tr>
	</g:if>	
	<tr>
		<td class="cartoucheRightTitleTD" >${message(code: 'employee.monthly.timeBefore7', default: 'report')} :</td>		
		
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
			<g:if test="${timeBefore7 != null}">
				<my:humanTimeTD id="timeBefore7"  name="timeBefore7" value="${timeBefore7} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="timeBefore7Decimal"  name="timeBefore7Decimal" value="${timeBefore7} "/>
  			</g:if>
			<g:else>
				${message(code: 'ajax.loading.label', default: 'report')}	
			</g:else>	  		
			</td>
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
			<g:if test="${yearTimeBefore7 != null}">
					<my:humanTimeTD id="yearTimeBefore7"  name="yearTimeBefore7" value="${yearTimeBefore7} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="yearTimeBefore7Decimal"  name="yearTimeBefore7Decimal" value="${yearTimeBefore7} "/>	  			
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
				<my:humanTimeTD id="timeAfter20"  name="timeAfter20" value="${timeAfter20} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="timeAfter20Decimal"  name="timeAfter20Decimal" value="${timeAfter20} "/>	  			
  			</g:if>
			<g:else>
				${message(code: 'ajax.loading.label', default: 'report')}
			</g:else>	  		
		</td>	
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
			<g:if test="${yearTimeAfter20 != null}">
				<my:humanTimeTD id="yearTimeAfter20"  name="yearTimeAfter20" value="${yearTimeAfter20} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="yearTimeAfter20"  name="timeAfter20Decimal" value="${yearTimeAfter20} "/>	  			
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
				<my:humanTimeTD id="timeOffHours"  name="timeOffHours" value="${timeOffHours} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="timeOffHours"  name="timeOffHours" value="${timeOffHours} "/>	  			
  			</g:if>
			<g:else>
				${message(code: 'ajax.loading.label', default: 'report')}
			</g:else>	  		
		</td>	
			
		 <td style="font-weight:bold; text-align: center;" class="cartoucheRightFiguresTD">  
			<g:if test="${yearTimeOffHours != null}">
				<my:humanTimeTD id="yearTimeOffHours"  name="yearTimeOffHours" value="${yearTimeOffHours} "/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="yearTimeOffHours"  name="yearTimeOffHours" value="${yearTimeOffHours} "/>	  			
	  		</g:if>
			<g:else>
				${message(code: 'ajax.loading.label', default: 'report')}
			</g:else>	  		
		</td>
	</tr>	
</table>