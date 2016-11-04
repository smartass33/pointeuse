<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Vacation"%>
<%@ page import="pointeuse.Period"%>
<%@ page import="pointeuse.AbsenceType"%>



<!doctype html>
<html>
<head>
	<g:javascript library="jquery" plugin="jquery" />
	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />
	<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
	<title><g:message code="absence.followup"/></title>
	<style type="text/css">
		body {
			font-family: Verdana, Arial, sans-serif;
			font-size: 0.9em;
		}
		table {
			border-collapse: collapse;
		}

	</style>
</head>
<body>
	<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>
	<div class="nav" id="nav">
		<g:headerMenu />
	</div>
	<div id="list-employee" class="standardNav">
		<h1>
			<g:message code="vacation.followup" /> <g:if test="${site}">pour le site ${site.name}</g:if>
		
			<br>
			<br>
			<g:form method="POST" url="[controller:'employee', action:'vacationFollowup']">
				<ul>
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
					<li>
						<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':site.name]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':(site?site.name:'-')]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />						
						</g:else>				
					</li>
					<li style="vertical-align: bottom;" class="datePicker">
						 <g:datePicker 
							name="myDate" value="${myDate ? myDate : new Date()}" relativeYears="[-3..5]"
							precision="month" noSelection="['':'-Choose-']"
							style="vertical-align: middle;" /> 
					</li>
					<li><g:actionSubmit class='displayButton' value="vue mensuelle"  action="monthlyVacationFollowup"/></li>		
					<li><g:actionSubmit class='excelButton' value="export excel"  action="vacationExcelExport"/></li>		

					<g:hiddenField name="isAdmin" value="${isAdmin}" />
					<g:if test="${site!=null}">	
						<g:hiddenField name="site.id" value="${site.id}" />		
					</g:if>
					<g:hiddenField name="siteId" value="${siteId}" />
					<g:if test="${period!=null}">	
						<g:hiddenField name="year" value="${period.id}" />	
					</g:if>
				</ul>	
			</g:form>
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>
	<br>
	<div id="monthlyVacationList">
		<table style="width:100%;"  class="vacation-table-rotate">
			<thead>
				<tr>
					<th class="rotate-45" style="width:50px;"><div><span>SALARIE</span></div></th>
					<% def currentDay = Calendar.instance
						currentDay.time = myDate
						currentDay.set(Calendar.DAY_OF_MONTH,1)
					 %>					
				 	<g:each var="i" in="${ (1..<lastDay+1) }">
				 		<th class="rotate-45"><div><span>${currentDay.time.format('E dd MMM')}</span></div></th>
  						<%  currentDay.roll(Calendar.DAY_OF_MONTH,1)%>		
  					</g:each>
  					<th class="rotate-45"><div><span>${AbsenceType.VACANCE}</span></div></th>
  					<th class="rotate-45"><div><span>${AbsenceType.RTT}</span></div></th>
 					<th class="rotate-45"><div><span>${AbsenceType.AUTRE}</span></div></th>
					<th class="rotate-45"><div><span>${AbsenceType.EXCEPTIONNEL}</span></div></th>
					<th class="rotate-45"><div><span>${AbsenceType.PATERNITE}</span></div></th>
  					<th class="rotate-45"><div><span>${AbsenceType.CSS}</span></div></th>
  					<th class="rotate-45"><div><span>${AbsenceType.INJUSTIFIE}</span></div></th>
  					<th class="rotate-45"><div><span>${AbsenceType.DIF}</span></div></th>  					
  	  				<th class="rotate-45"><div><span>${AbsenceType.GROSSESSE}</span></div></th> 	
  					<th class="rotate-45"><div><span>${AbsenceType.MALADIE}</span></div></th>
  					<th class="rotate-45"><div><span>${AbsenceType.FORMATION}</span></div></th>
  					
   				</tr>
  			</thead>
  			<tbody>
  				<g:each in="${employeeDailyMap}"  status="j" var="employee">
  					<tr>
  						<td  class="vacationTD" style="width:50px;text-align:left;">${employee.key.lastName}</td>
  						<g:each in="${employee.value}"  status="k" var="situation">
  							<g:if test='${situation.value.equals("F")}'>
  								<td class="vacationTD" style="background-color:#FFC0CB;">${situation.value}</td>  						
  							</g:if>
  							<g:else>
  								<g:if test='${situation.value.equals("OK")}'>
  									<td class="vacationTD" style="background-color:#98FB98;"></td>
  								</g:if>
  								<g:else>
  									<g:if test='${situation.value.equals("n/a")}'>
  										<td class="vacationTD" style="background-color:grey;"></td>
  									</g:if>
  									<g:else>
  										<td class="vacationTD">${situation.value}</td>
  									</g:else>
  								</g:else>					
  							</g:else>
  						</g:each>
  						<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.VACANCE) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.VACANCE)}</g:else>  							
  						</td>
  						<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.RTT) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.RTT)}</g:else>  							
  						</td>
  						
   						<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.AUTRE) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.AUTRE)}</g:else>  							
  						</td> 						
    					<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.EXCEPTIONNEL) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.EXCEPTIONNEL)}</g:else>  							
   						</td> 
    					<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.PATERNITE) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.PATERNITE)}</g:else>  							
  						</td> 						
  					    <td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.CSS) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.CSS)}</g:else>  							
  						</td> 		
  					    <td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.INJUSTIFIE) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.INJUSTIFIE)}</g:else>  							
  						</td> 
    					<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.DIF) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.DIF)}</g:else>  							
  						</td> 	
     					<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.GROSSESSE) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.GROSSESSE)}</g:else>  							
  						</td>  						  						
   						<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.MALADIE) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.MALADIE)}</g:else>  							
  						</td> 	
  						<td class="vacationTD">
  							<g:if test="${employeeAbsenceMap.get(employee.key).get(AbsenceType.FORMATION) == null }">0</g:if> 							
  							<g:else>${employeeAbsenceMap.get(employee.key).get(AbsenceType.FORMATION)}</g:else>  							
  						</td> 					
  					</tr>
  				</g:each>
  			</tbody>
		</table>
	
	</div>
	<g:if test="${employeeInstanceTotal!=null}">
		<div class="pagination" id="pagination">
			<g:hiddenField name="isAdmin" value="${isAdmin}" />
			<g:paginate total="${employeeInstanceTotal}" params="${[isAdmin:isAdmin,siteId:siteId]}" />
		</div>
	</g:if>
</body>
</html>