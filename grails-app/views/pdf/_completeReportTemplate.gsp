<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Employee"%>
		<%@ page import="pointeuse.InAndOut"%>
		<%@ page import="pointeuse.AbsenceType"%>
		<%@ page import="pointeuse.MonthlyTotal"%>
		<style  type="text/css">
			@page {
			   size: 210mm 297mm;
			   margin: 5px 5px 5px 5px;
			 }
			table {
			  font: normal 11px verdana, arial, helvetica, sans-serif;
			  color: #363636;
			  background: #f6f6f6;
			  background-color: white;
			  text-align:left;
			  }
			caption {
			  text-align: center;
			  font: bold 16px arial, helvetica, sans-serif;
			  background: transparent;
			  padding:6px 4px 8px 0px;
			  color: #CC00FF;
			  text-transform: uppercase;
			}
			thead, tfoot {
				background:url(bg1.png) repeat-x;
				text-align:left;
				height:20px;
			}
			thead th, tfoot th {
				padding:3px;
			}
			table a {
				color: #333333;
				text-decoration:none;
			}
			table a:hover {
				text-decoration:underline;
			}
			tr.odd {
				background: #f1f1f1;
			}
			tbody td {
			 	text-align:center;
			 	height:5px;
			 	width:90px;
			 	font-size:80%;
			}			
			tbody th{
			 	text-align:center;
			 	height:5px;
			 	width:250px;
			}			
			.cartoucheValues{	
				border-collapse: collapse;
			 	border-width:1px; 
			 	border-style:solid; 
			 	border-color:black;
			}
			.cartoucheValues td{
				width: 100px;
				text-align: center;	
			}

		</style>
	</head>
	<body>
	<h1 style="text-align:center;font-size:130%">${message(code: 'employee.monthly.report.label')} <g:formatDate format="MMMM yyyy" date="${period}"/></h1>
	<table>
		<tbody>  
			<tr>
				<td width="20%">
					<table width="100%" >
						<tr>
							<td style="font-weight: bold;text-align:left;" >${fieldValue(bean: employee, field: "firstName")} ${fieldValue(bean: employee, field: "lastName")}</td>
						</tr>
						<tr/><tr/><tr/><tr/>						
						<tr>
							<g:if test="${currentContract!=null}">		
								<td style="text-align:left;">${message(code: 'employee.weeklyContractTime.short.label', default: 'report')}: ${currentContract.weeklyLength}</td>
							</g:if>
							<g:else>
								<td style="text-align:left;">${message(code: 'employee.weeklyContractTime.short.label', default: 'report')}: N/A</td>
							</g:else>
						</tr>
						<tr>
							<td style="text-align:left;">${message(code: 'employee.matricule.label', default: 'report')}: ${fieldValue(bean: employee, field: "matricule")}</td>
						</tr>
						<tr>
							<td style="text-align:left;" >${message(code: 'employee.arrivalDate.short.label', default: 'report')}: <g:formatDate format="dd/MM/yyyy" date="${employee.arrivalDate}"/></td>
						</tr>				
					</table> 
				</td>
				<td width="85%">
					<table width="120%" >						
						<tbody>		
							<tr>					
								<td width="40%" pstyle="text-align:left;" ></td>	
	 							<td>
	 								<table>
	 									<thead></thead>
	 									<tr><td style="font-weight: bold" align="center"><g:formatDate format="MMMM yyyy" date="${period}"/></td></tr>
	        						</table>
	        					</td>
	        					<td>
	 								<table>
	 									<thead></thead>
	 									<tr><td style="font-weight: bold">${message(code: 'employee.cumul', default: 'report')} ${period2.year}/${period2.year + 1}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	 								<table>
	 									<thead></thead>
	 									<tr><td style="font-weight: bold">${message(code: 'employee.remaining', default: 'report')} ${period2.year}/${period2.year + 1}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td width="35%" style="text-align:left;" >${message(code: 'employee.vacances.count', default: 'report')} :</td>	
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${holiday as float}</td></tr>
	        						</table>
	        					</td>
	        					
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${yearlyHoliday as float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${(initialCA - yearlyHoliday) as float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td style="text-align:left;">${message(code: 'employee.rtt.count', default: 'report')} :</td>
	 							<td>
	 								<table  border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${rtt as float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlyRtt as float}</td></tr>
	        						</table>
	        					</td>	        					
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${(initialRTT - yearlyRtt) as float}</td></tr>
	        						</table>
	        					</td>	        					
							</tr>
							<tr>
								<td style="text-align:left;">${message(code: 'employee.sickness.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${sickness as float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlySickness as float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td style="text-align:left;">${message(code: 'employee.maternite.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${maternite as float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlyMaternite as float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td style="text-align:left;">${message(code: 'employee.sanssolde.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${sansSolde as float}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlySansSolde as float}</td></tr>
	        						</table>
	        					</td>
							</tr>	
							
							<tr>
								<td width="35%" style="text-align:left;" >${message(code: 'employee.exceptionnel.count', default: 'report')} :</td>	
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${exceptionnel as float}</td></tr>
	        						</table>
	        					</td>
	        					
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${yearlyExceptionnel as float}</td></tr>
	        						</table>
	        					</td>
							</tr>							
							<tr>
								<td width="35%" style="text-align:left;" >${message(code: 'employee.paternite.count', default: 'report')} :</td>	
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${paternite as float}</td></tr>
	        						</table>
	        					</td>
	        					
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${yearlyPaternite as float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td width="35%" style="text-align:left;" >${message(code: 'employee.parental.count', default: 'report')} :</td>	
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${parental as float}</td></tr>
	        						</table>
	        					</td>
	        					
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${yearlyParental as float}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td width="35%" style="text-align:left;" >${message(code: 'employee.dif.count', default: 'report')} :</td>	
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>${dif as float}</td></tr>
	        						</table>
	        					</td>
	        					
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${yearlyDif as float}</td></tr>
	        						</table>
	        					</td>
							</tr>											
							<tr>
								<td style="text-align:left;">${message(code: 'employee.yearly.theorical.time', default: 'report')} :</td>							
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${monthlyTheoriticalAsString.get(0)}:${monthlyTheoriticalAsString.get(1)}</td></tr>
	        						</table>
	        					</td>	
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlyTheoritical.get(0)}:${yearlyTheoritical.get(1)}</td></tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td style="text-align:left;">${message(code: 'employee.yearly.actual.time', default: 'report')} :</td>							
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr>
	 									<g:if test="${monthlyTotalRecapAsString!=null}">
	 										<td>${monthlyTotalRecapAsString.get(0)}:${monthlyTotalRecapAsString.get(1)}</td>
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
	        							<tr><td>${yearlyActualTotal.get(0)}:${yearlyActualTotal.get(1)}</td></tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td style="text-align:left;">${message(code: 'employee.peculiar.time', default: 'report')} :</td>							
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	        							<tr><td>${pregnancyCredit.get(0)}:${pregnancyCredit.get(1)}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlyPregnancyCredit.get(0)}:${yearlyPregnancyCredit.get(1)}</td></tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td style="text-align:left;">${message(code: 'default.monthly.open.day', default: 'report')} :</td>				
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${workingDays}</td>
	        							</tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearOpenDays}</td></tr>
	        						</table>
	        					</td>	
							</tr>
							<g:if test="${monthlySupTime!=null}">						
								<tr>
									<td style="text-align:left;">${message(code: 'employee.monthly.sup.above.42.time', default: 'report')} :</td>				
									<td>
		        						<table border="1" class="cartoucheValues" >
		        							<thead></thead>
		        							<tr>
		        								<td style="font-weight:bold">	  					
													<my:humanTimeTD id="monthlySupTime" name="monthlySupTime" value="${monthlySupTime}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="monthlySupTimeDecimal"  name="monthlySupTimeDecimal" value="${monthlySupTime}"/>
							  					</td>
							  				</tr>
		        						</table>

		        					</td>
									<td>
		        						<table border="1" class="cartoucheValues" >
		        							<thead></thead>
		        							<tr><td style="font-weight:bold"><my:humanTimeTD id="ajaxYearlySupTime" name="ajaxYearlySupTime" value="${ajaxYearlySupTime}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="ajaxYearlySupTimeDecimal"  name="ajaxYearlySupTimeDecimal" value="${ajaxYearlySupTime}"/></td></tr>
		        						</table>
		        					</td>
								</tr>
							</g:if>	
							

							<g:if test="${employee.weeklyContractTime != 35}">
								<g:if test="${payableCompTime != null}">						
									<tr>
										<td style="text-align:left;">${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
										<td>
			        						<table border="1" class="cartoucheValues" >
			        							<thead></thead>
			        							<tr><td style="font-weight:bold"><my:humanTimeDecimalTD id="payableCompTime"  name="payableCompTime" value="${payableCompTime}"/></td></tr>
			        						</table>
			        					</td>
			        					
									</tr>
								</g:if>		
							</g:if>
							
							<tr>
								<td style="text-align:left;">${message(code: 'employee.monthly.timeBefore7', default: 'report')} :</td>				
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold"><my:humanTimeTD id="timeBefore7" name="timeBefore7" value="${timeBefore7}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="timeBefore7"  name="timeBefore7" value="${timeBefore7}"/></td></tr>
	        						</table>
	        					</td>
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold"><my:humanTimeTD id="yearTimeBefore7" name="yearTimeBefore7" value="${yearTimeBefore7}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="yearTimeBefore7Decimal"  name="yearTimeBefore7Decimal" value="${yearTimeBefore7}"/></td></tr>
	        						</table>
	        					</td>	        					
	        					
							</tr>	
							<tr>
								<td style="text-align:left;">${message(code: 'employee.monthly.timeAfter20', default: 'report')} :</td>				
								<td> 
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold"><my:humanTimeTD id="timeAfter20"  name="timeAfter20" value="${timeAfter20}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="timeAfter20Decimal"  name="timeAfter20Decimal" value="${timeAfter20}"/></td></tr>
	        						</table>
	        					</td>
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold"><my:humanTimeTD id="yearTimeAfter20"  name="yearTimeAfter20" value="${yearTimeAfter20}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="yearTimeAfter20Decimal"  name="yearTimeAfter20Decimal" value="${yearTimeAfter20}"/></td></tr>
	        						</table>
	        					</td>
							</tr>	
							<tr>
								<td style="width:25%;text-align:left;">${message(code: 'employee.monthly.timeOffHours', default: 'report')} :</td>				
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold"><my:humanTimeTD id="timeOffHours"  name="timeOffHours" value="${timeOffHours}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="timeOffHours"  name="timeOffHours" value="${timeOffHours}"/></td></tr>
	        						</table>
	        					</td>
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold"><my:humanTimeTD id="yearTimeOffHours"  name="yearTimeOffHours" value="${yearTimeOffHours}"/> ${message(code: 'common.word.or', default: 'report')} <my:humanTimeDecimalTD id="yearTimeOffHoursDecimal"  name="yearTimeOffHoursDecimal" value="${yearTimeOffHours}"/></td></tr>
	        						</table>
	        					</td>
							</tr>																											
						</tbody>
					</table> 
				</td>
			</tr>
			</tbody>
		</table>
		<BR/><BR/>
		<table  id="reportTable" style="border:1px solid black;width:750px;text-align:left;font-weight:bold;">
		    <thead>
		      <th style="border:1px; width:120px;text-align:center">${message(code: 'report.table.date.label', default: 'report')}</th>
		      <th style="border:1px; width:150px;text-align:center">${message(code: 'employee.site.label', default: 'report')}</th>
		      <th style="border:1px; width:70px;text-align:center">${message(code: 'report.table.daily.total.label', default: 'report')}</th>    
		      <th style="border:1px; width:40px;text-align:center">${message(code: 'report.table.HS.label', default: 'report')}</th>
		      <th style="border:1px; width:55x;text-align:center">${message(code: 'report.table.absence.label', default: 'report')}</th>
			  <th style="border:1px; width:40px;text-align:center">${message(code: 'mileage.mini.label', default: 'report')}</th>
		      <th style="border:1px; width:400px;text-align:center">${message(code: 'events.label', default: 'report')}</th>
		    </thead>	   
		</table>		
		        <g:each in="${weeklyAggregate}" status="k" var="week">
		        <table >
			        <tbody>
				        <g:each in="${week}" status="l" var="day">
				            <g:each in="${day.value}" status="m" var="entries">
				              <g:if test="${entries!=null}">
				                <tr>
				                  	<g:if test="${dailyBankHolidayMap.get(entries.key) }">
				                  		<td><i>${entries.key.format('E dd MMM yyyy')}</i></td>
				                  	</g:if>
									<g:else>
						                <g:if test="${entries.key.getAt(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY}">
						                	<td style="border:1px; width:100px;font-weight:bold;text-align:left;"><i>${entries.key.format('E dd MMM yyyy')}</i></td>
						                </g:if>
						                <g:else>
						                	<td style="border:1px; width:100px;text-align:left;"><i>${entries.key.format('E dd MMM yyyy')}</i></td>
						                </g:else>		                
						          	</g:else>          		
					                <g:if test="${dailyTotalMap.get(entries.key) !=null}">	
					                	<g:if test="${dailyTotalMap.get(entries.key).site != null}">
							                <td style="border:1px;width:80px;text-align:center;">  
							                	${dailyTotalMap.get(entries.key).site}      											                  	
									         </td>		
									     </g:if>
								         <g:else>
							                <td style="border:1px; width:120px;text-align:center;">  
							                	${employee.site.name}        											                  	
									         </td>								         
								         </g:else>			                
						                <td style="border:1px;width:60px;text-align:center;"> 
						                	${dailyTotalTextMap.get(entries.key)} 											                  	
								         </td>
					                </g:if>
				                	<g:else>
				                		<td style="border:1px; width:120px;text-align:center;">${employee.site.name}</td>
				                		<td style="border:1px; width:60px;text-align:center;">00:00</td>
				                	</g:else>
				                  	<g:if test="${weeklySupTotal.get(employee) != null && weeklySupTotal.get(employee).get(day.key) !=null}">
				                    	<g:if test="${dailySupTotalTextMap.get(entries.key) !=null}">
				                            <td style="border:1px;width:40px;text-align:center;">
				                            	${dailySupTotalTextMap.get(entries.key)}
				                            </td>
				                         </g:if>
					                    <g:else>
				                    		<td style="border:1px;width:40px;text-align:center;">-</td>
				                   		 </g:else>
				                    </g:if>
					            	<g:else><td style="border:1px;width:40px;text-align:center;">-</td></g:else>
				                  	<g:if test="${holidayMap.get(entries.key) != null}"><td style="border:1px;width:55px;text-align:center;">${holidayMap.get(entries.key).type}</td></g:if>
				                    <g:else><td style="border:1px;width:55px;text-align:center;">-</td></g:else> 
					               	<g:if test="${mileageMapByDay.get(entries.key) != null}">
	                  					<td style="border:1px; width:35px;text-align:center;">${mileageMapByDay.get(entries.key).value}</td>
	                  				</g:if>
	                  				<g:else>
	                  					<td style="border:1px; width:35px;text-align:center;">0</td>
	                  				</g:else>				                    
				                    
					                  <g:each in="${entries.value}" var="inOrOut">
				                  		<g:if test="${inOrOut.type.equals('E')}">
			                       			<td bgcolor="98FB98" style="height: 1px;text-align:center; width:40px">${inOrOut.time.format('HH:mm')}</td>
			                     			</g:if>
			                     			 <g:else>
			                       			<td bgcolor="#FFC0CB" style="height: 1px;text-align:center;width:40px">${inOrOut.time.format('HH:mm')}</td>
			                    			 </g:else> 
					                  </g:each>

				                    
					                </tr>
					              </g:if>
				            </g:each>
			            </tbody>
		           	</table>
		           	<table  style="border-width:1px; border-style:solid; border-color:black; width:91%">
	            	<tr>
	            	<td  colspan="45" style="font-style: bold;">
	              <i>${day.key}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	              <g:if test="${weeklyTotal.get(employee) != null && weeklyTotal.get(employee).get(day.key) !=null && !(weeklyTotal.get(employee).get(day.key)).equals('00:00')}">
	                  	${weeklyTotal.get(employee).get(day.key)}
	                  <g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null && !(weeklySupTotal.get(employee).get(day.key)).equals('00:00')}">
	                   &nbsp;&nbsp;&nbsp;dont ${message(code: 'which.sup.time', default: 'Report')} ${(weeklySupTotal.get(employee)).get(day.key)}  
	                  </g:if>
	              </g:if>
	              <g:else>
	              	00:00
	              	<g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null  && weeklySupTotal.get(employee).get(day.key) != null && !(weeklySupTotal.get(employee).get(day.key)).equals('00:00')}">
	                   &nbsp;&nbsp;&nbsp;dont ${message(code: 'which.sup.time', default: 'Report')} ${(weeklySupTotal.get(employee)).get(day.key)}  
	                </g:if>
	              </g:else>
	              </i>
	              </td>
	            </tr> 
		       </g:each>        
		     </table>
		 </g:each>		         
		<table>
			<thead></thead>
			<tr><td></td></tr>
			<tr>
				<td style="text-align:left;" >${message(code: 'mileage.month.label', default: 'report')}: 
					<g:if test="${monthlyPeriodValue}">
	 					${monthlyPeriodValue} KM
     						 </g:if>	    
			        <g:else>
			        	 ${0} KM
			        </g:else>
				</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
			
			<tr>
				<td>${message(code: 'report.verification.date.label', default: 'Report')}: ${new Date().format('EEEE dd MMM yyyy')}</td>
			</tr>
			<tr><td>&nbsp;</td></tr>		
			<tr>
				<td>${message(code: 'report.employee.signature.label', default: 'Report')}:</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>${message(code: 'report.employer.signature.label', default: 'Report')}:</td>				
			</tr>
		</table>			   
	</body>
</html>