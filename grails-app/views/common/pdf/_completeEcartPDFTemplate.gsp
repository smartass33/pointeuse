<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 <html>
<head>

	<%@ page import="pointeuse.Site"%>
	<%@ page import="pointeuse.Employee"%>
	<%@ page import="pointeuse.InAndOut"%>
	<g:set var="calendar" value="${Calendar.instance}"/>
		<style  type="text/css">
				@page {
				   size: 297mm 210mm;
				   margin: 0px 0px 13px 0px;
				 }
				table {
				  font: normal 11px verdana, arial, helvetica, sans-serif;
				  color: #363636;
				  background: #f6f6f6;
				  text-align:center;
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
					text-align:center;
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

<h1>SITE: ${site.name }  - PERIODE: ${period}</h1>

<table id="employee-table" border="1">
	<thead></thead>


	<tbody id='body_update' style="border:1px;">
		<tr>
			<td/>
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
				<g:if test="${month_th>5}">
					<td>${period.year}</td>
				</g:if>
				<g:else>
					<td>${period.year + 1}</td>		
				</g:else>
			</g:each>
		</tr>
		<tr>
			<td/>
			
			<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th_2'>
			
				<%= calendar.set(Calendar.MONTH,month_th_2-1) %>
				<td style="vertical-align: middle;">${calendar.time.format('MMMM') }</td>
			</g:each>

		</tr>										
		<g:each in="${employeeInstanceList}" status="i" var="employee">
			<th colspan='14'>${employee.firstName} ${employee.lastName}</th>
			<tr>
				<td>Total H Théorique</td>	
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_theoritical'>	
					<g:if test="${monthlyTheoriticalByEmployee.get(employee)!=null}">
						<g:if test="${monthList != null && monthList.contains(month_theoritical)}">
							<td>${monthlyTheoriticalByEmployee.get(employee).get(month_theoritical)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>

				</g:each>		
			</tr>
			<tr>
				<td>Total H réalisées</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_actual'>						
					<g:if test='${monthlyActualByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_actual)}">
							<td>${monthlyActualByEmployee.get(employee).get(month_actual)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>		
			<tr>
				<td>Ecart HR - HT</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_ecart'>									
					<g:if test='${ecartByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_ecart)}">
							<td>${ecartByEmployee.get(employee).get(month_ecart)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>
			<tr>
				<td>RTT restant</td>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_rtt'>									
					<g:if test='${rttByEmployee.get(employee)!=null}'>
						<g:if test="${monthList != null && monthList.contains(month_rtt)}">				
							<td>${rttByEmployee.get(employee).get(month_rtt)}</td>
						</g:if>
						<g:else>
							<td style="background-color:#CCCCCE"/>
						</g:else>
					</g:if>
				</g:each>
			</tr>														
		</g:each>
	</tbody>
</table>
</body>
</html>