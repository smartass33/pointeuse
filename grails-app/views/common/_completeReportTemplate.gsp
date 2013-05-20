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
				   margin: 0px 0px 13px 0px;
				 }
				table {
				  font: normal 11px verdana, arial, helvetica, sans-serif;
				  color: #363636;
				  background: #f6f6f6;
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
					height:30px;
				}
				thead th, tfoot th {
					padding:5px;
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
				 	font-size:95%;
				}
				
				tbody th{
				 	text-align:center;
				 	height:5px;
				 	width:250px;
				}
				

				
			</style>
	</head>
	<body>
	<table>
		<thead></thead>
		<tbody>
			<tr>
				<td width="20%">
					<table width="100%" >
						<tr>
							<td style="font-weight: bold" >${firstName} ${lastName}</td>
						</tr>
						<tr>
							<td>Horaire Hebdomadaire: ${weeklyContractTime}</td>
						</tr>
						<tr>
							<td>matricule: ${matricule}</td>
						</tr>
					</table> 
				</td>
				<td width="80%">
					<table width="100%" >						
						<tbody>		
							<tr><td style="font-weight: bold" colspan="2" align="center"><g:formatDate format="MMMMM yyyy" date="${period}"/></td>
							<td style="font-weight: bold">${message(code: 'employee.cumul', default: 'report')} ${yearInf}/${yearSup}</td>
							</tr>			
							<tr>
							
								<td width="35%" >${message(code: 'employee.vacances.count', default: 'report')} :</td>	
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
							</tr>
							<tr>
								<td>${message(code: 'employee.rtt.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
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
							</tr>
							<tr>
								<td>${message(code: 'employee.sickness.count', default: 'report')} :</td>
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
								<td>${message(code: 'employee.sanssolde.count', default: 'report')} :</td>
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
								<td>${message(code: 'employee.yearly.theorical.time', default: 'report')} :</td>							
								<td>
	        						<table border="1"  >
	        							<thead></thead>
	        							<tr><td>${monthTheoritical.get(0)}H${monthTheoritical.get(1)==0?'00':monthTheoritical.get(1)}</td></tr>
	        						</table>
	        					</td>	
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlyTheoritical.get(0)}H${yearlyTheoritical.get(1)==0?'00':yearlyTheoritical.get(1)}</td></tr>
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
	        							<tr><td>${yearlyActualTotal.get(0)}H${yearlyActualTotal.get(1)==0?'00':yearlyActualTotal.get(1)}</td></tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td>${message(code: 'employee.peculiar.time', default: 'report')} :</td>							
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	        							<tr><td>${pregnancyCredit.get(0)}H${pregnancyCredit.get(1)==0?'00':pregnancyCredit.get(1)}</td></tr>
	        						</table>
	        					</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${yearlyPregnancyCredit.get(0)}H${yearlyPregnancyCredit.get(1)==0?'00':yearlyPregnancyCredit.get(1)}</td></tr>
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
							<g:if test="${payableCompTime!=null}">						
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
		<table border="1" id="reportTable">
		    <thead style="border-top:5px solid red;">
		      <th style="border:5px solid red; width:92px;text-align:center">date</th>
		      <th style="border:5px solid red;width:62px;text-align:center">total jour</th>
		      <th style="border:5px solid red;width:32px;text-align:center">HS</th>
		      <th style="border:5px solid red;width:48px;text-align:center">absence</th>
		      <th style="border:5px solid red;width:400px;text-align:center">${message(code: 'events.label', default: 'report')}</th>
		    </thead>
		    
		</table>	
		
		        <g:each in="${weeklyAggregate}" status="k" var="week">
		        <table border="1">
		        	<tbody>
		          <g:each in="${week}" status="l" var="day">
		            <g:each in="${day.value}" status="m" var="entries">
		              <g:if test="${entries!=null}">
		                <tr>
		                  <td style="border:5px solid red;width:100px"><i>${entries.key.format('E dd MMM yyyy')}</i></td>
		                  <g:if test="${dailyTotalMap.get(entries.key) !=null && (dailyTotalMap.get(entries.key).get(0)>0 || dailyTotalMap.get(entries.key).get(1)>0 || dailyTotalMap.get(entries.key).get(2)>0)}">
		                  	<td style="border:5px solid red;width:70px">${(dailyTotalMap.get(entries.key)).get(0)}:${(dailyTotalMap.get(entries.key)).get(1)}:${(dailyTotalMap.get(entries.key)).get(2)}</td>
		                  </g:if>
		                  <g:else>
		                    <td style="border:5px solid red;width:70px">00:00:00</td>
		                  </g:else>
		                  	<g:if test="${weeklySupTotal.get(employee) != null && weeklySupTotal.get(employee).get(day.key) !=null}">
		                    	<g:if test="${dailySupTotalMap.get(entries.key) !=null && (dailySupTotalMap.get(entries.key).get(0)>0 ||dailySupTotalMap.get(entries.key).get(1)>0)}">
		                            <td style="border:5px solid red;width:40px">${dailySupTotalMap.get(entries.key).get(0)}H${dailySupTotalMap.get(entries.key).get(1)==0?'':dailySupTotalMap.get(entries.key).get(1)}</td>
		                         </g:if>
			                    <g:else>
		                    		<td style="border:5px solid red;width:40px">-</td>
		                   		 </g:else>
		                    </g:if>
			                <g:else>
		                    	<td style="border:5px solid red;width:40px">-</td>
		                   	</g:else>

		                  	<g:if test="${holidayMap.get(entries.key) != null}">
		                   		<td style="border:5px solid red;width:55px">${holidayMap.get(entries.key).type}</td>
		                    </g:if>
		                    <g:else>
		                    	<td style="border:5px solid red;width:55px">-</td>
		                    </g:else> 
		                  <g:each in="${entries.value}" var="inOrOut">
	                  		<g:if test="${inOrOut.type.equals('E')}">
                       			<td bgcolor="98FB98" style="height: 1px;text-align:center;">${inOrOut.time.format('H:mm')}</td>
                     			</g:if>
                     			 <g:else>
                       			<td bgcolor="#FFC0CB" style="height: 1px;text-align:center;">${inOrOut.time.format('H:mm')}</td>
                    			 </g:else> 
		                  </g:each>
		                </tr>
		              </g:if>
		            </g:each>
		            </tbody>
		           	</table>
		           	 <table border="1" >
		           	<tr>
		        		<td style="width: 200px;overflow: hidden;text-overflow: ellipsis;">
		              ${day.key}
		              <g:if test="${weeklyTotal.get(employee) != null && weeklyTotal.get(employee).get(day.key) !=null && (weeklyTotal.get(employee).get(day.key).get(2)>0 || weeklyTotal.get(employee).get(day.key).get(1)>0 || weeklyTotal.get(employee).get(day.key).get(0)>0)}">
		                  ${message(code: 'weekly.total.label', default: 'Report')}: ${(weeklyTotal.get(employee).get(day.key)).get(0)}H${(weeklyTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyTotal.get(employee).get(day.key)).get(1)}
		                  <g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null}">
		                  ${message(code: 'which.sup.time', default: 'Report')} ${(weeklySupTotal.get(employee).get(day.key)).get(0)}H${(weeklySupTotal.get(employee).get(day.key)).get(1)==0?'':(weeklySupTotal.get(employee).get(day.key)).get(1)}
		                  	<g:if test="${employee.weeklyContractTime!=35 && weeklyCompTotal != null && weeklyCompTotal.get(employee) != null && weeklyCompTotal.get(employee).get(week.key) != null}">
		                       ${message(code: 'which.comp.time', default: 'Report')} ${(weeklyCompTotal.get(employee).get(day.key)).get(0)}H${(weeklyCompTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyCompTotal.get(employee).get(day.key)).get(1)}                                                                            
		                    </g:if>
		                    <g:else>
		                      ${message(code: 'which.comp.time', default: 'Report')} 0H
		                    </g:else>
		                  </g:if>
		                  <g:else>
		                    <g:if test="${employee.weeklyContractTime!=35 && weeklyCompTotal != null && weeklyCompTotal.get(employee) != null && weeklyCompTotal.get(employee).get(week.key) != null}">
		                       ${message(code: 'which.comp.time', default: 'Report')} ${(weeklyCompTotal.get(employee).get(day.key)).get(0)}H${(weeklyCompTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyCompTotal.get(employee).get(day.key)).get(1)}                                                                        
		                    </g:if>
		                    <g:else>
		                      ${message(code: 'which.comp.time', default: 'Report')} 0H
		                    </g:else>  
		                  </g:else>                            
		              </g:if>
		              <g:else>
		                Total semaine: 00H00
		              </g:else>
		              </td>
		            </tr>  
		          </g:each>
		          
		          </table>
		        </g:each>
	</body>
</html>