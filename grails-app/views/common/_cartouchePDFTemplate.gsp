<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 <html>
<head>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>
	<sec:ifAllGranted roles="ROLE_SUPER_ADMIN">	
		<style  type="text/css">
			@page {
			   size: 297mm 210mm;
			 }
			table {
			  border-collapse: collapse;
			  border: 1px solid #666666;
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
			tbody th, tbody td {
			 	text-align:center;
			}
		</style>
	</sec:ifAllGranted>

</head>
<body>
	
	<font size="2">
		<table width="100%" class="cartoucheTable" cellspacing="1" cellpadding="1" >
		<thead></thead>
		<tbody>
			<tr>
				<td width="20%">
					<table width="100%"  cellspacing="1" cellpadding="1">
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
					<table width="100%" valign="center" class="cartouche">						
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
	</font>
</body>	
</html>