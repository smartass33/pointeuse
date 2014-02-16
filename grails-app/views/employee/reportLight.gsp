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

<title>
	${message(code: 'employee.report.label', default: 'Report')}
</title>
 <link href="main.css" rel="stylesheet" type="text/css">
	<g:javascript library="prototype" />
	<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />
	<resource:dateChooser />
	
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
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li><g:link class="list" action="pointage" params="${[isAdmin:isAdmin,siteId:siteId,id:userId]}">retour pointage</g:link></li>
				
				<form> 
				
					<li>${message(code: 'default.period.label', default: 'List')}: <g:datePicker name="myDate" value="${period ? period : new Date()}"
						relativeYears="[-3..5]"
						 precision="month" noSelection="['':'-Choisissez-']"/></li>
					<li><g:actionSubmit class="listButton" value="afficher" action="reportLight"/></li>
					<g:hiddenField name="userId" value="${userId}" />
					
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
			
			</tr>
			</tbody>
		</table>
			</font>
		</div>
			
		<BR/>
			<table border="1" >
				<thead>
					<th width="150px" align="center">date</th>
					<th width="60px">total jour</th>
					<th width="50px">absence</th>
     				<th align="center" colspan="80">${message(code: 'events.label', default: 'report')}</th>

				</thead>
				<tbody >	
					<g:each in="${weeklyAggregate}" status="k" var="week">
							<form controller="employee">
								<g:each in="${week}" status="l" var="day">									
									<g:each in="${day.value}" status="m" var="entries">
										<g:if test="${entries!=null}">
										<tr>
											<td width="180px"><font size="2"><i>${entries.key.format('E dd MMM yyyy')}</i></font></td>
											<g:if test="${dailyTotalMap.get(entries.key) !=null && (dailyTotalMap.get(entries.key).get(0)>0 || dailyTotalMap.get(entries.key).get(1)>0 || dailyTotalMap.get(entries.key).get(2)>0)}">									
												<td><font size="2">${(dailyTotalMap.get(entries.key)).get(0)}:${(dailyTotalMap.get(entries.key)).get(1)}:${(dailyTotalMap.get(entries.key)).get(2)}</font></td>
											</g:if>
											<g:else>
												<td><font size="2">00:00:00</font></td>
											</g:else>
											
											
											<td>		
											<g:if test="${holidayMap.get(entries.key) != null}">	
													<font size="2">
													${holidayMap.get(entries.key).type}
									
														</font>
											</g:if>
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
																<g:if test="${inOrOut.regularization==true || inOrOut.systemGenerated==true}">
																	<g:textField id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center" style="font-weight: bold" />	
																</g:if>
																<g:else>
																	<g:textField  id="myinput" name="cell" value="${inOrOut.time.format('H:mm')}" align="center"/>								
																</g:else>
															</font>
													</font>
													</td>
											</g:each>
										</tr>
										</g:if>																			
									</g:each>
									<tr>
										<th>${day.key}</th>
										<g:if test="${weeklyTotal.get(employee) != null && weeklyTotal.get(employee).get(day.key) !=null && (weeklyTotal.get(employee).get(day.key).get(2)>0 || weeklyTotal.get(employee).get(day.key).get(1)>0 || weeklyTotal.get(employee).get(day.key).get(0)>0)}">									
											
											<th colspan="15" scope="colgroup">
												${message(code: 'weekly.total.label', default: 'Report')} : ${(weeklyTotal.get(employee).get(day.key)).get(0)}H${(weeklyTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyTotal.get(employee).get(day.key)).get(1)}										   												
											</th>																												
										</g:if>
										<g:else>
											<th colspan="15" scope="colgroup">Total fin de semaine: 00H00</TH>
										</g:else>
									</tr>	
								</g:each>
							</form>
					</g:each>
				</tbody>
			</table>
	</div>
</body>
					

</html>
