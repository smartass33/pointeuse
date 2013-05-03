<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>

<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue" %> 

<!doctype html>
<html>
<head>
	<g:javascript library="jquery" plugin="jquery"/>
	<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />
	<resource:dateChooser />
	<modalbox:modalIncludes/>
	<meta name="layout" content="main">
	<g:set var="weeklyRecap" value="0" />
	<title>
		${message(code: 'employee.report.label', default: 'Report')}
	</title>
	<link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
	<a href="#list-employee" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>

	<div id="list-employee" class="content scaffold-list">
		<div>
		<h1>	
			BIOLAB33
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</h1>
		</div>
		<div class="nav" role="navigation" >
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li><g:link class="list" action="list" params="${[isAdmin:isAdmin,siteId:siteId]}">${message(code: 'employee.back.to.list.label', default: 'List')}</g:link></li>
				<form> 
					<li>${message(code: 'default.period.label', default: 'List')}: <g:datePicker name="myDate" value="${period ? period : new Date()}" precision="month" noSelection="['':'-Choose-']"/></li>
					<li><g:actionSubmit value="afficher" action="report"/></li>
					<g:hiddenField name="userId" value="${userId}" />
					<g:hiddenField name="siteId" value="${siteId}" />
					
				</form>
				<!--li>
					<g:pdfLink pdfController="employee" pdfAction="demo" pdfId="65432">GSP as PDF</g:pdfLink>			
				</li>
				<li>
					<g:pdfForm controller="employee" action="pdfForm" method="post" >
						<g:hiddenField name="userId" value="${userId}" />
            			<g:submitButton name="printPdf" value="pdf" />
            		</g:pdfForm>
				</li-->
				<li><modalbox:createLink controller="inAndOut" action="create" css="loginbutton" id="${userId}" title="Ajouter un évenement oublié" width="500"  params="[complete:true,report:true]"><g:message code="inAndOut.regularization" default="Régul" /></modalbox:createLink></li>							
			</ul>
	
		</div>
		


		<g:if test="${flash.message}">
			<div class="message">
				${flash.message}
			</div>
		</g:if>
		
    </div>

		<div id="updateDiv2">		
			<font size="2">
		<table width="100%" class="cartoucheTable" cellspacing="1" cellpadding="1" >
		<thead></thead>
		<tbody>
			<tr>
				<td width="30%">
					<table width="100%"  cellspacing="1" cellpadding="1">
						<tr>
							<td style="font-weight: bold" >${employee.firstName} ${employee.lastName}</td>
						</tr>
						<tr>
							<td>Horaire Hebdomadaire: ${employee.weeklyContractTime}</td>
						</tr>
						<tr>
							<td>matricule: ${employee.matricule}</td>
						</tr>
					</table> 
				</td>
				<td width="70%">
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
	 									<g:if test="${monthlyTotal.get(employee)!=null}">
	 										<td>${monthlyTotal.get(employee).get(0)}H${monthlyTotal.get(employee).get(1)==0?'00':monthlyTotal.get(employee).get(1)}</td>
	 									</g:if>
	 									<g:else>
	 										<td>0 H 0 min</td>			
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
							<tr>
								<td>${message(code: 'employee.monthly.sup.time', default: 'report')} :</td>				
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold">${payableSupTime.get(0)}H${payableSupTime.get(1)==0?'00':payableSupTime.get(1)}</td><td style="font-weight:bold">${(payableSupTime.get(0)+payableSupTime.get(1)/60).setScale(2,2)}H</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td>${message(code: 'employee.monthly.comp.time', default: 'report')} :</td>				
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td style="font-weight:bold">${payableCompTime.get(0)}H${payableCompTime.get(1)==0?'00':payableCompTime.get(1)}</td><td style="font-weight:bold">${(payableCompTime.get(0)+payableCompTime.get(1)/60).setScale(2,2)}H</td></tr>
	        						</table>
	        					</td>
							</tr>							
						</tbody>
					</table> 
				</td>
			</tr>
			</tbody>
		</table>
			</font>
		</div>
			
			

			
		<BR/>
			<table border="1" >
				<thead>
					<th width="150px" align="center">date</th>
					<th width="50px">total<BR>du jour</th>
					<th width="60px">HS</th>			
					<th width="60px">absence</th>									
					<g:each in="${(1..<7)}" var="val">
						<th align="center" width="45px" >E ${val}</th>
						<th align="center" width="45px" >S ${val}</th>
					</g:each>
				</thead>
				<tbody >	
					<g:each in="${weeklyAggregate}" status="k" var="week">
							<form controller="employee">
								<g:each in="${week}" status="l" var="day">									
									<g:each in="${day.value}" status="m" var="entries">
										<g:if test="${entries!=null}">
										<tr>
											<td width="180px"><font size="2"><i>
											<modalbox:createLink controller="employee" action="showDay"  id="${employee.id}" title="modifier des évenements" width="500"  params="[employeeId: employee.id, month: entries.key.format('MM'), year: entries.key.format('yyyy'), day: entries.key.format('dd')]">${entries.key.format('E dd MMM yyyy')}</modalbox:createLink>			
											</i></font></td>
											<g:if test="${dailyTotalMap.get(entries.key) !=null && (dailyTotalMap.get(entries.key).get(0)>0 || dailyTotalMap.get(entries.key).get(1)>0 || dailyTotalMap.get(entries.key).get(2)>0)}">									
												<td><font size="2">${(dailyTotalMap.get(entries.key)).get(0)}:${(dailyTotalMap.get(entries.key)).get(1)}:${(dailyTotalMap.get(entries.key)).get(2)}</font></td>
											</g:if>
											<g:else>
												<td><font size="2">00:00:00</font></td>
											</g:else>
											<td><font size="2">
												<g:if test="${employee.weeklyContractTime==35}">
													<g:if test="${weeklySupTotal.get(employee) != null && weeklySupTotal.get(employee).get(day.key) !=null}">
														<g:if test="${dailySupTotalMap.get(entries.key) !=null && (dailySupTotalMap.get(entries.key).get(0)>0 ||dailySupTotalMap.get(entries.key).get(1)>0)}">
																${dailySupTotalMap.get(entries.key).get(0)}H${dailySupTotalMap.get(entries.key).get(1)==0?'':dailySupTotalMap.get(entries.key).get(1)}
														</g:if>
													</g:if>
												</g:if>
												<g:else>
														<g:if test="${dailySupTotalMap.get(entries.key) !=null && (dailySupTotalMap.get(entries.key).get(0)>0 ||dailySupTotalMap.get(entries.key).get(1)>0)}">
																${dailySupTotalMap.get(entries.key).get(0)}H${dailySupTotalMap.get(entries.key).get(1)==0?'':dailySupTotalMap.get(entries.key).get(1)}
														</g:if>
												</g:else>
												</font>
											</td>
											
											<td>		
											<g:if test="${holidayMap.get(entries.key) != null}">	
												<g:hiddenField name="userId" value="${employee.id}" />
																							<font size="2">
												
												<g:select onchange="${remoteFunction(action:'modifyAbsence', update:'myUpdate', params:[updatedSelection:new JavascriptValue('this.value'),employeeId:employee.id,day:entries.key.format('dd MM yyyy')])}" 
														name="absenceType" from="${AbsenceType.values()}" 
														value="${AbsenceType}" 
														optionKey="key" 
														noSelection="${['-':holidayMap.get(entries.key).type]}"
													
														/>
														</font>
											</g:if>
											<g:else>
												<g:hiddenField name="userId" value="${employee.id}" />
											<font size="2">
												<g:select onchange="${remoteFunction(action:'modifyAbsence', update:'myUpdate',params:[updatedSelection:new JavascriptValue('this.value'),employeeId:employee.id,day:entries.key.format('dd MM yyyy')])}" 
													name="absenceType" 
													from="${AbsenceType.values()}" 
													value="${AbsenceType}" 
													optionKey="key" 
													noSelection="${['':'-']
													
													}"
													/>
													</font>
											</g:else>
										</td>
											<g:each in="${entries.value}" var="inOrOut">
													<font size="2">
															<g:if test="${inOrOut.type.equals('E')}">
																<td bgcolor="98FB98" style="height:1px">
															</g:if>
															<g:else>
																<td bgcolor="#FFC0CB" style="height:1px">
															</g:else>													
															<font size="2">
																<g:if test="${inOrOut.regularizationType!=0 || inOrOut.systemGenerated}" >
																	<g:if test="${inOrOut.systemGenerated}" >
																		<g:textField id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center" style="font-weight: bold" />	
																	</g:if>
																	<g:if test="${inOrOut.regularizationType==1}">
																		<g:textField id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center" style="color : red;font-weight: bold;" />	
																	</g:if>
																	<g:if test="${inOrOut.regularizationType==2}">
																		<g:textField id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center" style="color : blue;font-weight: bold;" />	
																	</g:if>				
																	<g:if test="${inOrOut.regularizationType==3}">
																		<g:textField id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center" style="color : green;font-weight: bold;" />	
																	</g:if>		
																</g:if>
																<g:else>
																	<g:textField id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center"/>								
																</g:else>
															</font>
													</font>
														<g:hiddenField name="inOrOutId" value="${inOrOut.id}" />
														<g:hiddenField name="time" value="${inOrOut.time.format('yyyy-M-d H:mm:ss')}" /> 
														<g:hiddenField name="day" value="${inOrOut.day}" /> 
														<g:hiddenField name="month" value="${inOrOut.month}" /> 
														<g:hiddenField name="year" value="${inOrOut.year}" /> 
														<g:hiddenField name="regul" value="${inOrOut.regularization}" /> 
														<g:hiddenField name="systemGenerated" value="${inOrOut.systemGenerated}" /> 																							
														<g:hiddenField name="employee" value="${inOrOut.employee.id}" /> 
													</td>
											</g:each>
										</tr>
										</g:if>																			
									</g:each>
									<tr>
										<th>${day.key}</th>
										<g:if test="${weeklyTotal.get(employee) != null && weeklyTotal.get(employee).get(day.key) !=null && (weeklyTotal.get(employee).get(day.key).get(2)>0 || weeklyTotal.get(employee).get(day.key).get(1)>0 || weeklyTotal.get(employee).get(day.key).get(0)>0)}">									
											
											<th colspan="11" scope="colgroup">
												${message(code: 'weekly.total.label', default: 'Report')} : ${(weeklyTotal.get(employee).get(day.key)).get(0)}H${(weeklyTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyTotal.get(employee).get(day.key)).get(1)}
											      <g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null }">
											       		${message(code: 'which.sup.time', default: 'Report')} : ${(weeklySupTotal.get(employee).get(day.key)).get(0)}H${(weeklySupTotal.get(employee).get(day.key)).get(1)==0?'':(weeklySupTotal.get(employee).get(day.key)).get(1)}
											       		<g:if test="${employee.weeklyContractTime!=35 && weeklyCompTotal != null && weeklyCompTotal.get(employee) != null && weeklyCompTotal.get(employee).get(week.key) != null}">
														 	et ${message(code: 'which.comp.time', default: 'Report')} :  ${(weeklyCompTotal.get(employee).get(day.key)).get(0)}H${(weeklyCompTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyCompTotal.get(employee).get(day.key)).get(1)}																																								
														</g:if>
														<g:else>
															et ${message(code: 'which.comp.time', default: 'Report')} : 00H00
														</g:else>
													</g:if>
													<g:else>
														<g:if test="${employee.weeklyContractTime!=35 && weeklyCompTotal != null && weeklyCompTotal.get(employee) != null && weeklyCompTotal.get(employee).get(week.key) != null}">
														 	${message(code: 'which.comp.time', default: 'Report')} : ${(weeklyCompTotal.get(employee).get(day.key)).get(0)}H${(weeklyCompTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyCompTotal.get(employee).get(day.key)).get(1)}																																								
														</g:if>
														<g:else>
															${message(code: 'which.comp.time', default: 'Report')} : 00H00
														</g:else>	
													</g:else>														
											</th>																												
										</g:if>
										<g:else>
											<th colspan="11" scope="colgroup">Total fin de semaine: 00H00</TH>
										</g:else>
										<th colspan="4" scope="colgroup"><g:actionSubmit value="appliquer" action="timeModification"/></th>
										<g:hiddenField name="employee.id" value="${employee.id}" />
										<g:hiddenField name="month" value="${period.format('MM')}" />
										<g:hiddenField name="year" value="${period.format('yyyy')}" />					
									</tr>	
								</g:each>
							</form>
					</g:each>
				</tbody>
			</table>
	</div>
</body>
					

</html>
