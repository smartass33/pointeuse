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
			   margin: 5px 10px 10px 10px;
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
	<h1 style="text-align:center;font-size:150%">${message(code: 'employee.monthly.report.label')} <g:formatDate format="MMMM yyyy" date="${period}"/></h1>
	<table>
		<thead></thead>
		<tbody>
			<tr>
				<td width="20%">
					<table width="100%" >
						<tr>
							<td style="font-weight: bold;text-align:left;" >${fieldValue(bean: employee, field: "firstName")} ${fieldValue(bean: employee, field: "lastName")}</td>
						</tr>
						<tr/><tr/><tr/><tr/><tr/><tr/><tr/><tr/>						
						<tr>
							<td style="text-align:left;">${message(code: 'employee.weeklyContractTime.short.label', default: 'report')}: ${currentContract.weeklyLength}</td>
						</tr>
						<tr>
							<td style="text-align:left;">${message(code: 'employee.matricule.label', default: 'report')}: ${fieldValue(bean: employee, field: "matricule")}</td>
						</tr>
					</table> 
				</td>
				<td width="80%">
					<table width="100%" >						
						<tbody>		
							<tr>					
								<td width="35%" style="text-align:left;" ></td>	
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
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>	        							
	        							<tr><td>${(initialRTT - yearlyRtt) as float}</td></tr>
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
								<td style="text-align:left;">${message(code: 'employee.yearly.theorical.time', default: 'report')} :</td>							
								<td>
	        						<table border="1" class="cartoucheValues" >
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
								<td style="text-align:left;">${message(code: 'employee.yearly.actual.time', default: 'report')} :</td>							
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
								<td style="text-align:left;">${message(code: 'employee.peculiar.time', default: 'report')} :</td>							
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
								<td style="text-align:left;">${message(code: 'default.monthly.open.day', default: 'report')} :</td>				
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold">${workingDays}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<g:if test="${payableSupTime!=null}">
								<tr>
									<td style="text-align:left;">${message(code: 'employee.monthly.sup.time', default: 'report')} :</td>				
									<td>
		        						<table border="1" class="cartoucheValues" >
		        							<thead></thead>
		        							<tr><td style="font-weight:bold">${payableSupTime.get(0)}H${payableSupTime.get(1)==0?'00':payableSupTime.get(1)}</td><td style="font-weight:bold">${(payableSupTime.get(0)+payableSupTime.get(1)/60).setScale(2,2)}H</td></tr>
		        						</table>
		        					</td>
								</tr>
							</g:if>
							<g:if test="${employee.weeklyContractTime!=35}">
								<g:if test="${payableCompTime!=null}">						
									<tr>
										<td style="text-align:left;">${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
										<td>
			        						<table border="1" class="cartoucheValues" >
			        							<thead></thead>
			        							<tr><td style="font-weight:bold">${payableCompTime.get(0)}H${payableCompTime.get(1)==0?'00':payableCompTime.get(1)}</td><td style="font-weight:bold">${(payableCompTime.get(0)+payableCompTime.get(1)/60).setScale(2,2)}H</td></tr>
			        						</table>
			        					</td>
									</tr>
								</g:if>		
							</g:if>					
						</tbody>
					</table> 
				</td>
			</tr>
			</tbody>
		</table>
		<BR/><BR/>
		<table  id="reportTable" style="border:1px solid black;width:690px;text-align:left;font-weight:bold;">
		    <thead>
		      <th style="border:1px; solid black; width:92px;text-align:center">${message(code: 'report.table.date.label', default: 'report')}</th>
		      <th style="border:1px;width:62px;text-align:center">${message(code: 'report.table.daily.total.label', default: 'report')}</th>
		      <th style="border:1px;width:32px;text-align:center">${message(code: 'report.table.HS.label', default: 'report')}</th>
		      <th style="border:1px;width:48px;text-align:center">${message(code: 'report.table.absence.label', default: 'report')}</th>
		      <th style="border:1px;width:400px;text-align:center">${message(code: 'events.label', default: 'report')}</th>
		    </thead>
		    
		</table>	
		
		        <g:each in="${weeklyAggregate}" status="k" var="week">
		        <table >
			        <tbody>
				        <g:each in="${week}" status="l" var="day">
				            <g:each in="${day.value}" status="m" var="entries">
				              <g:if test="${entries!=null}">
				                <tr>
				                  	<g:if test="${dailyBankHolidayMap.get(entries.key) }"><td style="border:1px; width:100px; color:red; text-align:left;"><i>${entries.key.format('E dd MMM yyyy')}</i></td></g:if>
									<g:else>
						                <g:if test="${entries.key.getAt(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY}"><td style="border:1px;width:100px;font-weight:bold;text-align:left;"><i>${entries.key.format('E dd MMM yyyy')}</i></td></g:if>
						                <g:else><td style="border:1px;width:100px;text-align:left;"><i>${entries.key.format('E dd MMM yyyy')}</i></td></g:else>		                
						          	</g:else>          		
					                <g:if test="${dailyTotalMap.get(entries.key) !=null}">		
						                <td style="border:1px;width:70px">          
											${dailyTotalMap.get(entries.key)}                  	
								         </td>
					                </g:if>
				                	<g:else><td style="border:1px;width:70px">00 : 00</td></g:else>
				                  	<g:if test="${weeklySupTotal.get(employee) != null && weeklySupTotal.get(employee).get(day.key) !=null}">
				                    	<g:if test="${dailySupTotalMap.get(entries.key) !=null && (dailySupTotalMap.get(entries.key).get(0)>0 ||dailySupTotalMap.get(entries.key).get(1)>0)}">
				                            <td style="border:1px;width:40px">${dailySupTotalMap.get(entries.key).get(0)}H${dailySupTotalMap.get(entries.key).get(1)==0?'':dailySupTotalMap.get(entries.key).get(1)}</td>
				                         </g:if>
					                    <g:else>
				                    		<td style="border:1px;width:40px">-</td>
				                   		 </g:else>
				                    </g:if>
					            	<g:else><td style="border:1px;width:40px">-</td></g:else>
				                  	<g:if test="${holidayMap.get(entries.key) != null}"><td style="border:1px;width:55px">${holidayMap.get(entries.key).type}</td></g:if>
				                    <g:else><td style="border:1px;width:55px">-</td></g:else> 
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
		           	<table>
		           	<tr>
		        		<td style="border:1px solid black;width:690px;text-align:left;font-weight:bold;">
		              		Total ${day.key}:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		              <g:if test="${weeklyTotal.get(employee) != null && weeklyTotal.get(employee).get(day.key) !=null && (weeklyTotal.get(employee).get(day.key).get(2)>0 || weeklyTotal.get(employee).get(day.key).get(1)>0 || weeklyTotal.get(employee).get(day.key).get(0)>0)}">
		              	<g:if test='${(weeklyTotal.get(employee).get(day.key)).get(0)<10}'>0${(weeklyTotal.get(employee).get(day.key)).get(0)}</g:if>
						<g:else>${(weeklyTotal.get(employee).get(day.key)).get(0)}</g:else>
						<g:if test='${(weeklyTotal.get(employee).get(day.key)).get(1)<10}'> : 0${(weeklyTotal.get(employee).get(day.key)).get(1)}</g:if>
						<g:else>: ${(weeklyTotal.get(employee).get(day.key)).get(1)}</g:else>	   
		                  
		                  <g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null}">		                  		                  
		                  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; dont ${message(code: 'which.sup.time', default: 'Report')}
		                  	<g:if test='${(weeklySupTotal.get(employee).get(day.key)).get(0)<10}'>0${(weeklySupTotal.get(employee).get(day.key)).get(0)}</g:if>
							<g:else>${(weeklySupTotal.get(employee).get(day.key)).get(0)}</g:else>
							<g:if test='${(weeklySupTotal.get(employee).get(day.key)).get(1)<10}'> : 0${(weeklySupTotal.get(employee).get(day.key)).get(1)}</g:if>
							<g:else>: ${(weeklySupTotal.get(employee).get(day.key)).get(1)}</g:else>	
		                  </g:if>  
		                  <g:else>
		                 </g:else>                         
		              </g:if>
		              <g:else>
		                00 : 00 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; dont ${message(code: 'which.sup.time', default: 'Report')} 00 : 00
		              </g:else>
		              </td>
		            </tr>  
		          </g:each>
		          
		          </table>
		        </g:each>
		         <br/><br/>
		         
  				<table  >
					<thead></thead>
					<tr>
						<td>${message(code: 'report.verification.date.label', default: 'Report')}: ${new Date().format('EEEE dd MMM yyyy')}</td>
					</tr>
					<tr><td></td></tr>
					<tr><td></td></tr>
					<tr><td></td></tr>

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