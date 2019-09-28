<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>

<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue" %> 

<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="weeklyRecap" value="0" />
		<title>${message(code: 'employee.report.label', default: 'Report')}</title>
		<link href="main.css" rel="stylesheet" type="text/css">
		<g:javascript library="prototype" />
		<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />
		<resource:dateChooser />
		
		<style type="text/css">
		

		</style>
	</head>
	<body>
		<a href="#list-employee" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;" /></a>
		<div id="list-employee" class="content scaffold-list">
			<div>
			<h1 style="text-transform:uppercase;">${grailsApplication.config.laboratory.name}
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</h1>
	
			</div>
			<div class="nav" id="nav" style="font-family: Verdana,Arial,sans-serif;font-size: 14.4px;">
				<ul>
					<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" /></a></li>
					<li style="vertical-align: middle;"><g:link class="list" action="pointage" params="${[isAdmin:isAdmin,siteId:siteId,id:userId]}">retour pointage</g:link></li>					
					<form method="post" controller="employee" params="[siteId:siteId,userId:userId]" > 				
						<li style="vertical-align: bottom;"  class="datePicker">
							<g:datePicker name="myDate" value="${period ? period : new Date()}"
							relativeYears="[-3..5]"
							 precision="month" noSelection="['':'-Choisissez-']"/></li>
						<li style="vertical-align: middle;"><g:actionSubmit class="displayButton" value="afficher" action="reportLight"/></li>
					</form>					
				</ul>
			</div>			
			<g:if test="${flash.message}">
				<div class="message">
					${flash.message}
				</div>
			</g:if>
			<div id="cartouche_div">		
			<font size="2">
				<table width="100%" class="cartoucheLeftTD" >
					<thead></thead>
					<tbody>
						<tr>
							<td class="cartoucheLeftTD"  width="30%">
								<table width="100%" >
									<tr>
										<td style="font-weight: bold" >
											<g:if test="${employee != null}">
												${employee.firstName} ${employee.lastName}
											</g:if>
										</td>
									</tr>
									<tr>
										<td>${message(code: 'employee.weeklyContractTime.short.label', default: 'report')}: <g:if test="${currentContract != null}">${currentContract.weeklyLength}</g:if></td>
									</tr>
									<tr>
										<td>${message(code: 'employee.matricule.label', default: 'report')}: <g:if test="${employee != null}">${employee.matricule}</g:if></td>
									</tr>
								</table> 
							</td>
						</tr>
					</tbody>
				</table>
			</font>
			</div>		
			<BR/>
			<table  style="table-layout: fixed;"  id="reportTable">
				<thead>
					<th width="120px" style="text-align:center">${message(code: 'report.table.date.label', default: 'report')}</th>
					<th width="60px" style="text-align:center">${message(code: 'report.table.daily.total.label', default: 'report')}</th>
					<th width="50px" style="text-align:center">${message(code: 'report.table.absence.label', default: 'report')}</th>
					<th width="50px" style="text-align:center">${message(code: 'mileage.mini.label', default: 'report')}</th>		
     				<th style="text-align:center" colspan="80">${message(code: 'events.label', default: 'report')}</th>
				</thead>
				<tbody >	
					<g:each in="${weeklyAggregate}" status="k" var="week">
							<form controller="employee">
								<g:each in="${week}" status="l" var="day">									
									<g:each in="${day.value}" status="m" var="entries">
										<g:if test="${entries != null}">
											<tr>
												<td class="eventTD" width="120px"><font size="2"><i>${entries.key.format('E dd MMM yyyy')}</i></font></td>								
												<td class="eventTD" ><font size="2"><my:humanTimeTD id="dailyTotal" name="dailyTotal" value="${dailyTotalMap.get(entries.key)}"/></font></td>
												<td class="eventTD" >		
													<g:if test="${holidayMap != null && holidayMap.get(entries.key) != null}"><font size="2">${holidayMap.get(entries.key).type}</font></g:if>
													<g:else><font size="2">-</font></g:else>
												</td>
												<td class="eventTD" >		
													<g:if test="${mileageMapByDay != null && mileageMapByDay.get(entries.key) != null}"><font size="2">${mileageMapByDay.get(entries.key).value}</font></g:if>
													<g:else><font size="2">0</font></g:else>
												</td>										
												
												<g:each in="${entries.value}" var="inOrOut">
														<font size="2">
																<g:if test="${inOrOut.type.equals('E')}">
																	<g:if test="${inOrOut.regularization==true || inOrOut.systemGenerated==true}">
																		<td class="eventTDEntry" bgcolor="98FB98" style="font-weight: bold;background-color:#98FB98">${inOrOut.time.format('H:mm')}</td>																
																	</g:if>
																	<g:else>
																		<td class="eventTDEntry" bgcolor="98FB98" style="background-color:#98FB98;">${inOrOut.time.format('H:mm')}</td>																	
	 																</g:else>
																</g:if>
																<g:else>
																	<g:if test="${inOrOut.regularization==true || inOrOut.systemGenerated==true}">
																		<td class="eventTDExit" bgcolor="#FFC0CB" style="font-weight: bold;background-color:#FFC0CB">${inOrOut.time.format('H:mm')}</td>																
																	</g:if>
																	<g:else>
																		<td class="eventTDExit" bgcolor="#FFC0CB" style="background-color:#FFC0CB;">${inOrOut.time.format('H:mm')}</td>																	
	 																</g:else>
	 															</g:else>
	 													</font>
												</g:each>
											</tr>
										</g:if>																			
									</g:each>
									<tr>
										<th class="eventTD">${day.key}</th>
										<th class="eventTD" colspan="15" scope="colgroup">${message(code: 'weekly.total.label', default: 'Report')} : 
											<g:if test="${weeklyTotal.get(employee) != null }">
												<my:humanTimeTD id="weeklyTotal" name="weeklyTotal" value="${weeklyTotal.get(employee).get(day.key)}"/>
											</g:if>
											<g:else>
												<my:humanTimeTD id="weeklyTotal" name="weeklyTotal" value="${0}"/>
											</g:else>
										</th>
									</tr>	
								</g:each>
							</form>
					</g:each>
				</tbody>
			</table>
		</div>
	</body>
</html>
