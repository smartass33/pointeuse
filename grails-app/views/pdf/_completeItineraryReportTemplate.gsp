<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Itinerary" %>
		<%@ page import="pointeuse.Site" %>
		<%@ page import="pointeuse.ItineraryNature" %>
		<style  type="text/css">
			@page {
			   size: 297mm 210mm ;
			   margin: 2px 2px 2px 2px;
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
		<h1 style="text-align:center;font-size:130%">${message(code: 'itinerary.monthly.report.label')} ${itinerary.name} <g:formatDate format="MMMM yyyy" date="${currentDate}"/></h1>
		<g:if test="${theoriticalActionsList != null }">
		   <!--div id="theoriticalActionsList">
				<h3>${message(code: 'itinerary.weekday', default: 'Report')}</h3>
				<table style="table-layout:fixed;">
					<tbody>
						<tr>
							<g:each in="${theoriticalActionsList}" var='actionItem' status="j">
								<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
									<td style="width:80px;">
										${actionItem.site.name}<br/>
										<div style="color: red;">${actionItem.date.format('kk:mm')}</div>
									</td>							
								</g:if>
								
								<g:else>
									<td style="width:80px;">
										${actionItem.site.name}<br/>
										<div style="color: green;">${actionItem.date.format('kk:mm')}</div>
									</td>							

								</g:else>
							</g:each>	
						</tr>
					</tbody>
				</table>			
			</div-->
		</g:if>
		<BR/>
		<g:if test="${theoriticalSaturdayActionsMap != null }">
		   <!--div id="theoriticalSaturdayActionsMap">
				<h3>${message(code: 'itinerary.saturday', default: 'Report')}</h3>
				<table>
					<tbody style="border:1px;">
						<g:each in="${theoriticalSaturdayActionsMap}" var='thSatActionListItem' status="m">
							<tr>	
								<td>${thSatActionListItem.key.name}</td>
								<g:each in="${thSatActionListItem.value}" var='thSatActionItem' status="n">
									<g:if test="${thSatActionItem.nature.equals(ItineraryNature.ARRIVEE)}">
										<td style="color: red;">${thSatActionItem.date.format('kk:mm')}</td>
									</g:if>
									<g:else>
										<td style="color: green;">${thSatActionItem.date.format('kk:mm')}</td>
									</g:else>
								</g:each>
							</tr>
						</g:each>
					</tbody>
				</table>
			</div-->
		</g:if>
		<div id="actionMap">
			<g:if test="${actionsList == null || actionsList.size() == 0}">${message(code: 'action.list.empty', default: 'Report')}</g:if>
			<g:else>
				<h3>${message(code: 'itinerary.real.label', default: 'Report')}</h3>
				<table style="border-width:1px;">
					<tbody>
						<g:each in="${actionListMap}" var='actionListItem' status="m">
							<tr>
								<td>${(actionListItem.key).format('EEE dd/MM/yyyy')}</td>
								<g:each in="${actionListItem.value}" var='actionItem' status="n">
									<g:if test="${actionItem.nature.equals(ItineraryNature.ARRIVEE)}">
										<td style="width:80px;">
											${actionItem.site.name}<br/>
											<div style="color: red;">${actionItem.date.format('kk:mm')}</div>
										</td>
									</g:if>
									<g:else>
										<td style="width:80px;">
											${actionItem.site.name}<br/>
											<div style="color: green;">${actionItem.date.format('kk:mm')}</div>
										</td>
									</g:else>								
								</g:each>
							</tr>
						</g:each>
					</tbody>
				</table>
			</g:else>
		</div>
	</body>
</html>