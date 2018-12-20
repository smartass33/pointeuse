<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Itinerary" %>
		<%@ page import="pointeuse.Site" %>
		<%@ page import="pointeuse.ItineraryNature" %>
		<%@ page import="java.util.Calendar"%>
		<%@ page import="groovy.time.TimeCategory" %>
		

		<style  type="text/css">
			@page {
			   size: 297mm 210mm ;
			   margin: 2px 2px 20px 2px;
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
				height:5px;
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
	
	<g:set var="realCal" 			value="${Calendar.instance}"/>
	<g:set var="theoriticalCal" 	value="${Calendar.instance}"/>
	<g:set var="tdColor" 			value=""/>
	<g:set var="timeDiff" 			value=""/>
	<g:set var="actionItemColor" 	value=""/>
	<g:set var="myYellow" 			value="#fefb00"/>
	<g:set var="myOrange" 			value="#74F3FE"/>
	<g:set var="myRed" 				value="#FF8E79"/>
	<body>
		<h1 style="text-align:center;font-size:130%">${message(code: 'itinerary.site.monthly.report.label')} ${site.name} <g:formatDate format="MMMM yyyy" date="${currentDate}"/></h1>
		<div id="actionMap">
			<g:if test="${actionListMap == null || actionListMap.size() == 0}">${message(code: 'action.list.empty', default: 'Report')}</g:if>
			<g:else>
				<table >
					<tbody>
						<g:each in="${actionListMap}" var='actionListItem' status="m">
							<g:if test="${(actionListItem.key).getAt(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY}">
								<g:if test="${actionListItem.value == null || actionListItem.value.size() == 0}"></g:if>
								<g:else>
								<tr style="border:1px;border-style:solid;border-color:black;">
									<td style="width:60px;border:1px;border-style:solid;border-color:black;">${(actionListItem.key).format('EEE dd/MM/yyyy')}</td>
									<g:each in="${actionListItem.value}" var='actionItem' status="n">
										<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">									
											<td style="width:60px;border:1px;border-style:solid;border-color:black;">
												${actionItem.itinerary.name}
												<div style="color:red;">${actionItem.date.format('kk:mm')}</div>
											</td>
										</g:if>
										<g:else>
											<td style="width:60px;border:1px;border-style:solid;border-color:black;">
												${actionItem.itinerary.name}<BR/>
												<div style="color:green;">${actionItem.date.format('kk:mm')}</div>
											</td>
										</g:else>
									</g:each>
								</tr>
								</g:else>
							</g:if>
							<g:else>													
								<tr style="border:1px;border-style:solid;border-color:black;">
									<td style="width:60px;border:1px;border-style:solid;border-color:black;">${(actionListItem.key).format('EEE dd/MM/yyyy')}</td>
									<g:each in="${actionListItem.value}" var='actionItem' status="n">
										<% actionItemColor = (actionItem.nature.equals(ItineraryNature.ARRIVEE)) ? 'red' : 'green' %> 
										<g:if test="${actionItem.date.getAt(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY}">
											<g:if test="${theoriticalActionsList.size() >= n && theoriticalActionsList[n] != null}">										
												<% 
													theoriticalCal.time = actionItem.date
													realCal.time = actionItem.date
													theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
													theoriticalCal.set(Calendar.MINUTE,theoriticalActionsList[n].date.getAt(Calendar.MINUTE))
													use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
													if ((timeDiff.minutes + timeDiff.hours*60) <= 15){
														tdColor = '#FFFFFF'
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 15){
														tdColor = myYellow
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 30){
														tdColor = myOrange
													} 
													if ((timeDiff.minutes + timeDiff.hours*60) > 60){
														tdColor = myRed
													}
													%>												
											</g:if>			
										</g:if>
										<g:else>
											<g:if test="${theoriticalSaturdayActionsList.size() >= n && theoriticalSaturdayActionsList[n] != null}">
												<% 
													theoriticalCal.time = actionItem.date
													realCal.time = actionItem.date
													theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalSaturdayActionsList[n].date.getAt(Calendar.HOUR_OF_DAY))
													theoriticalCal.set(Calendar.MINUTE,theoriticalSaturdayActionsList[n].date.getAt(Calendar.MINUTE))
													use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
													if ((timeDiff.minutes + timeDiff.hours*60) <= 15){
														tdColor = '#FFFFFF'
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 15){
														tdColor = myYellow
													}
													if ((timeDiff.minutes + timeDiff.hours*60) > 30){
														tdColor = myOrange
													} 
													if ((timeDiff.minutes + timeDiff.hours*60) > 60){
														tdColor = myRed
													}
													%>								
											</g:if>			
										</g:else>
										<td bgcolor='${tdColor}' style="width:60px;border:1px;border-style:solid;border-color:black;">
											${actionItem.itinerary.name}<BR/>
											<div style="color:${actionItemColor};">${actionItem.date.format('kk:mm')}</div>
										</td>
									</g:each>
								</tr>
							</g:else>
						</g:each>
					</tbody>
				</table>
			</g:else>		
		</div>
	</body>
</html>