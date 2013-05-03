<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta name="layout" content="main" />
<title>Récapitulatif annuel</title>
</head>
<body>
	<!--div class="body" -->
		<div>
		<!-- Main Table(You can define padding accordingly) -->
		<table width="100%" class="cartoucheTable" cellspacing="1" cellpadding="1" >
		<thead></thead>
		<tbody>
			<tr>
				<td width="30%">
					<!-- Table on left side -->
					<table width="100%"  cellspacing="1" cellpadding="1">
						<tr>
							<td style="font-weight: bold" >${fieldValue(bean: employee, field: "firstName")} ${fieldValue(bean: employee, field: "lastName")}</td>
						</tr>
						<tr>
							<td>Horaire Hebdomadaire: ${fieldValue(bean: employee, field: "weeklyContractTime")}</td>
						</tr>
						<tr>
							<td>matricule: ${fieldValue(bean: employee, field: "matricule")}</td>
						</tr>
					</table> <!-- END -->
				</td>
				<td width="70%">
					<!-- Table on right side -->
					<table width="100%" valign="center" class="cartouche">						
						<tbody>					
							<tr>
								<td width="35%" >${message(code: 'employee.vacances.count', default: 'report')} :</td>	
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>0,0</td></tr>
	        						</table>
	        					</td>
	        					<td>${message(code: 'employee.cumul', default: 'report')} :</td>
	        					<td>
	        						<table border="1" class="cartoucheValues">
	        							<thead></thead>
	        							<tr><td>${holiday}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td>${message(code: 'employee.rtt.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>0,0</td></tr>
	        						</table>
	        					</td>
	        					<td>${message(code: 'employee.cumul', default: 'report')} :</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${rtt}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td>${message(code: 'employee.sickness.count', default: 'report')} :</td>
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>0,0</td></tr>
	        						</table>
	        					</td>
	        					<td>${message(code: 'employee.cumul', default: 'report')} :</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>${sickness}</td></tr>
	        						</table>
	        					</td>
							</tr>
							<tr>
								<td>${message(code: 'employee.yearly.theorical.time', default: 'report')} :</td>							
								<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>0,0</td></tr>
	        						</table>
	        					</td>						
							</tr>

							<tr>
								<td>${message(code: 'employee.yearly.actual.time', default: 'report')} :</td>							
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>0,0</td></tr>
	        						</table>
	        					</td>
	        					<td>${message(code: 'employee.cumul', default: 'report')} :</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>0,0</td></tr>
	        						</table>
	        					</td>					
							</tr>
							<tr>
								<td>${message(code: 'employee.peculiar.time', default: 'report')} :</td>							
	 							<td>
	 								<table border="1" class="cartoucheValues" >
	 									<thead></thead>
	 									<tr><td>0,0</td></tr>
	        						</table>
	        					</td>
	        					<td>${message(code: 'employee.cumul', default: 'report')} :</td>
	        					<td>
	        						<table border="1" class="cartoucheValues" >
	        							<thead></thead>
	        							<tr><td>0,0</td></tr>
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
						</tbody>
					</table> <!-- END -->
				</td>
			</tr>
			</tbody>
		</table>
	</div>
		<!-- END OF MAIN TABLE -->
	<!--/div-->
</body>
</html>