<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
 <html>
	<head>
	
		<%@ page import="pointeuse.Site"%>
		<%@ page import="pointeuse.Employee"%>
		<%@ page import="pointeuse.Period"%>
		
		
		<g:set var="calendar" value="${Calendar.instance}"/>
		<style  type="text/css">
			@page {
			   size: 297mm 210mm;
			   margin: 5px 10px 10px 10px;
	
			}
			.eventTD {
			    font:14px Georgia, serif;
			    padding:2px 2px;
			    text-align:center;
			    border: 1px solid #E7EFE0;
			    -moz-border-radius:2px;
			    -webkit-border-radius:2px;
			    border-radius:2px;
			    color:#666;
			    text-shadow:1px 1px 1px #fff;
			    width:80px;
			}
			
			.myh1 {
			    font:20px Georgia, serif;
			    color:#666;

			}
				
		</style>
		<g:set var="calendar" value="${Calendar.instance}"/>
		<g:if test="${ period != null}">
			<g:set var="currentYear" value="${period.year}"/>
		</g:if>
		<g:else>
			<g:set var="currentYear" value="${calendar.get(Calendar.YEAR)}"/>
		</g:else>
	</head>
	<body>
		<h1 class="myh1">
			<g:message code='site.payment.management' default='NONE' args="[site.name,period]" />
		
		</h1>
		<table style="table-layout: fixed;width:100%;"  >
			<thead>
				<th class="eventTD" style="vertical-align: middle;text-align: left;width:120px;"/>
				<g:each in="${[6,7,8,9,10,11,12,1,2,3,4,5]}" var='month_th'>
					<th class="eventTD" style="width:50px">
						<% calendar.set(Calendar.MONTH,month_th - 1) 
							if (month_th == 1){currentYear +=1}
						%>
						${calendar.time.format('MMM')} ${currentYear}
					</th>
				</g:each>		
			</thead>
			<tbody id='body_update' style="border:1px;">
				<g:each in="${employeeInstanceList}" var='employee'>
					<tr style="height:25px;">
						<td style="vertical-align: middle;text-align: left;width:120px;" class="eventTD" >${employee.lastName} ${employee.firstName}</td>
						<g:each in="${paymentMapByEmployee.get(employee)}" var='paymentMap'>
							<g:each in="${paymentMap}" var="payment" >		
								<td style="vertical-align: middle;text-align:center;" class="eventTD" >
									<my:humanTimeTD id="humanTD"  name="humanTD" value="${payment.value}"/>						
								</td>						
							</g:each>
						</g:each>
					</tr>
				</g:each>
			</tbody>
		</table>
	</body>
</html>