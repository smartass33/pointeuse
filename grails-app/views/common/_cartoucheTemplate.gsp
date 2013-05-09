<<<<<<< HEAD
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
	 									<g:if test="${monthlyTotalRecap!=null}">
	 										<td>${monthlyTotalRecap.get(0)}H${monthlyTotalRecap.get(1)==0?'00':monthlyTotalRecap.get(1)}</td>
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
=======
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<table id="employee-table">
	<thead>
		<tr>
			<g:sortableColumn property="lastName"
				title="${message(code: 'employee.lastName.label', default: 'Last Name')}" />
			<g:sortableColumn property="firstName"
				title="${message(code: 'employee.firstName.label', default: 'First Name')}" />	
			<g:sortableColumn property="site"
				title="${message(code: 'employee.site.label', default: 'Site')}" />
			<g:if test="${!isAdmin}">
				<g:sortableColumn property="annualReport"
					title="${message(code: 'employee.annualReport.label', default: 'Report')}" />
				<g:sortableColumn property="report"
					title="${message(code: 'employee.monthly.report.label', default: 'Report')}" />
				<g:sortableColumn property="entry"
					title="${message(code: 'employee.entry.status', default: 'Entry')}" />
				<g:sortableColumn property="lastTime"
					title="${message(code: 'employee.lastTime.label', default: 'Entry')}" />
				<g:sortableColumn property="error"
					title="${message(code: 'employee.hasErrors', default: 'Errors')}" />
			</g:if>
			<g:else>
				<g:sortableColumn property="lastName"
					title="${message(code: 'employee.username.label', default: 'User Name')}" />
				<g:sortableColumn property="weeklyContractTime"
					title="${message(code: 'employee.weeklyContractTime.short.label', default: 'weeklyContractTime')}" />
				<g:sortableColumn property="arrivalDate"
					title="${message(code: 'employee.arrivalDate.short.label', default: 'arrivalDate')}" />
				<g:sortableColumn property="service"
					title="${message(code: 'employee.service.label', default: 'service')}" />
				<g:sortableColumn property="matricule"
					title="${message(code: 'employee.matricule.label', default: 'matricule')}" />
			</g:else>
		</tr>
	</thead>

	<tbody id='body_update'>
		<g:each in="${employeeInstanceList}" status="i" var="employeeInstance">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td><g:link action="edit" id="${employeeInstance.id}"
						params="${[isAdmin:isAdmin,siteId:siteId]}">
						${fieldValue(bean: employeeInstance, field: "lastName")}
					</g:link></td>
				<td><g:link action="edit" id="${employeeInstance.id}"
						params="${[isAdmin:isAdmin,siteId:siteId]}">
						${fieldValue(bean: employeeInstance, field: "firstName")}
					</g:link></td>
				<td><g:if test="${employeeInstance?.site != null}">
						${employeeInstance?.site.name}
					</g:if></td>
				<g:if test="${!isAdmin}">
					<g:form>
						<g:hiddenField name="userId" value="${employeeInstance?.id}" />
						<g:hiddenField name="siteId" value="${siteId}" />
						<td><g:actionSubmit
								value="${message(code: 'employee.annualReport.label', default: 'Report')}"
								action="annualReport" /></td>
						<td><g:actionSubmit
								value="${message(code: 'employee.monthly.report.label', default: 'Report')}"
								action="report" /></td>
					</g:form>
					<g:form controller="employee">
						<g:hiddenField name="userId" value="${employeeInstance?.id}" />
						<td><g:if test="${employeeInstance?.status}">
								<g:if
									test="${InAndOut.findAllByEmployee(employeeInstance).size()==0}">
									${message(code: 'employee.out.status', default: 'out')}
								</g:if>
								<g:else>
									${message(code: 'employee.in.status', default: 'In')}
								</g:else>
							</g:if> <g:else>
								${message(code: 'employee.out.status', default: 'out')}
							</g:else></td>
						<g:if test="${InAndOut.findByEmployee(employeeInstance) != null}">
							<td>
								${InAndOut.findByEmployee(employeeInstance, [sort:'time',order:'desc']).time.format('H:mm d-M-yyyy')}
							</td>
						</g:if>
						<g:if test="${employeeInstance?.hasError}">
							<td>
								${message(code: 'default.yes.label', default: 'Yes')}
							</td>
						</g:if>
						<g:else>
							<td>
								${message(code: 'default.no.label', default: 'No')}
							</td>
						</g:else>
					</g:form>
				</g:if>
				<g:else>
					<g:if test="${employeeInstance?.userName}">
						<td>
							${fieldValue(bean: employeeInstance, field: "userName")}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.weeklyContractTime}">
						<td>
							${fieldValue(bean: employeeInstance, field: "weeklyContractTime")}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.arrivalDate}">
						<td>
							${employeeInstance?.arrivalDate.format('dd/MM/yyyy')}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.service}">
						<td>
							${employeeInstance?.service.name}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
					<g:if test="${employeeInstance?.matricule}">
						<td>
							${employeeInstance?.matricule}
						</td>
					</g:if>
					<g:else>
						<td />
					</g:else>
				</g:else>
			</tr>
		</g:each>
	</tbody>
</table>

>>>>>>> adding taglib/template
