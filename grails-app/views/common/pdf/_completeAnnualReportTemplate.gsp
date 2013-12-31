<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">



<html>
<head>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

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
				
				h1{
				 	font-size:80%;
				}
				
				
				
			</style>

</head>
<h1>
	<g:message code="yearly.recap.label"/> ${lastYear} / ${thisYear} pour <g:if test="${employee != null }">${employee.firstName} ${employee.lastName}</g:if>
	<g:set var="calendar" value="${Calendar.instance}"/>
	
</h1>


<h1>SUIVI MENSUEL</h1>
<table id="annual-report-table">
	<thead>

		<th><g:message code="annual.report.month.label"/></th>	
		<th><g:message code="annual.report.year.label"/></th>
		<th><g:message code="annual.report.theoritical.label"/></th>
		<th><g:message code="annual.report.elapsed.label"/></th>
		<!--th><g:message code="annual.report.working.days.label"/></th-->	
		<th><g:message code="annual.report.holidays.label"/></th>
		<th><g:message code="annual.report.RTT.label"/></th>
		<th><g:message code="annual.report.CSS.label"/></th>
		<th><g:message code="annual.report.sickness.label"/></th>
		<th><g:message code="annual.report.supplementary.label"/></th>
		<th><g:message code="annual.report.complementary.label"/></th>
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${yearMonthMap}"  status="i" var="cartouche">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<%= calendar.set(Calendar.MONTH,cartouche.key-1) %>
				<td style="vertical-align: middle;">${calendar.time.format('MMMM') }</td>
				<td style="vertical-align: middle;">${yearMap.get(cartouche.key)}</td>
				<td style="vertical-align: middle;">
				
					<g:if
                		test="${cartouche.value.getAt('monthTheoriticalHuman') !=null && ((cartouche.value.getAt('monthTheoriticalHuman')).get(0)>0 ||(cartouche.value.getAt('monthTheoriticalHuman')).get(1)>0)}">
                    	${(cartouche.value.getAt('monthTheoriticalHuman')).get(0)}H${(cartouche.value.getAt('monthTheoriticalHuman')).get(1)==0?'':(cartouche.value.getAt('monthTheoriticalHuman')).get(1)} 
               		</g:if>
               		<g:else>
               			0H0
               		</g:else>
				</td>
				<td style="vertical-align: middle;">
					<g:if
                		test="${yearTotalMap.get(cartouche.key) !=null && ((yearTotalMap.get(cartouche.key)).get(0)>0 ||(yearTotalMap.get(cartouche.key)).get(1)>0)}">
                    	${(yearTotalMap.get(cartouche.key)).get(0)}H${(yearTotalMap.get(cartouche.key)).get(1)==0?'':(yearTotalMap.get(cartouche.key)).get(1)} 
               		</g:if>
               		<g:else>
               			0H0
               		</g:else>
				</td>
				<td>${cartouche.value.getAt('holidays')}</td>
				<td>${cartouche.value.getAt('rtt')}</td>
				<td>${cartouche.value.getAt('sansSolde')}</td>
				<td>${cartouche.value.getAt('sickness')}</td>

				<td>
					<g:if
               			test="${yearMonthlySupTime.get(cartouche.key) !=null && ((yearMonthlySupTime.get(cartouche.key)).get(0)>0 ||(yearMonthlySupTime.get(cartouche.key)).get(1)>0)}">
                   		${(yearMonthlySupTime.get(cartouche.key)).get(0)}H${(yearMonthlySupTime.get(cartouche.key)).get(1)==0?'':(yearMonthlySupTime.get(cartouche.key)).get(1)} 
              		</g:if>
              		<g:else>
              			0H0
              		</g:else>
				</td>			
				<td>
					<g:if
               			test="${yearMonthlyCompTime.get(cartouche.key) !=null && ((yearMonthlyCompTime.get(cartouche.key)).get(0)>0 ||(yearMonthlyCompTime.get(cartouche.key)).get(1)>0)}">
                   		${(yearMonthlyCompTime.get(cartouche.key)).get(0)}H${(yearMonthlyCompTime.get(cartouche.key)).get(1)==0?'':(yearMonthlyCompTime.get(cartouche.key)).get(1)} 
              		</g:if>
              		<g:else>
              			0H0
              		</g:else>	
				</td>							
			</tr>
		</g:each>
		<tr>
			<td></td>
			<td></td>
			<td>
				<g:if
               		test="${annualTheoritical !=null && (annualTheoritical.get(0)>0 ||annualTheoritical.get(1)>0)}">
               		${annualTheoritical.get(0)}H${annualTheoritical.get(1)==0?'':annualTheoritical.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>
			</td>
			<td>
				<g:if
               		test="${annualTotal !=null && (annualTotal.get(0)>0 ||annualTotal.get(1)>0)}">
               		${annualTotal.get(0)}H${annualTotal.get(1)==0?'':annualTotal.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>
		</td>
			<td>${annualHoliday}</td>
			<td>${annualRTT}</td>
			<td>${annualCSS}</td>
			<td>${annualSickness}</td>
			<td>
				<g:if
               		test="${annualPayableSupTime !=null && (annualPayableSupTime.get(0)>0 ||annualPayableSupTime.get(1)>0)}">
               		${annualPayableSupTime.get(0)}H${annualPayableSupTime.get(1)==0?'':annualPayableSupTime.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>	
			</td>
			<td>
				<g:if
               		test="${annualPayableCompTime !=null && (annualPayableCompTime.get(0)>0 ||annualPayableCompTime.get(1)>0)}">
               		${annualPayableCompTime.get(0)}H${annualPayableCompTime.get(1)==0?'':annualPayableCompTime.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           		</g:else>
           	</td>
			<td></td>

		</tr>
	</tbody>
</table>

<h1>SYNTHESE ANNUELLE</h1>


<table id='jours' style="width:800px;text-align:center">
		<th style="width:200px;">Nombre de jours ouvrés</th>
		<th>Jours Travaillés</th>
		<th>Quota Annuel de base</th>
		<th>Solde Jours congés</th>
	<tr id='values'>
		<td style="text-align:center">${annualWorkingDays}</td>
		<td style="text-align:center">${annualEmployeeWorkingDays}</td>
		<td style="text-align:center">
			<g:if
          		test="${annualTheoritical !=null && (annualTheoritical.get(0)>0 ||annualTheoritical.get(1)>0)}">
               		${annualTheoritical.get(0)}H${annualTheoritical.get(1)==0?'':annualTheoritical.get(1)} 
           	</g:if>	
           	<g:else>
           			0H0
           	</g:else>
        </td>
        <td style="text-align:center">${remainingCA}</td>
	</tr>
</table>
<table id='heures_sup'>
	<th>Total Heures Sup/An</th>
		<th>Total Heures Comp/An</th>
		<th>Quota Annuel avec H Sup et HC</th>
		<th></th>
	<tr>
		<td style="text-align:center">				
			<g:if
               		test="${annualPayableSupTime !=null && (annualPayableSupTime.get(0)>0 ||annualPayableSupTime.get(1)>0)}">
               		${annualPayableSupTime.get(0)}H${annualPayableSupTime.get(1)==0?'':annualPayableSupTime.get(1)} 
           		</g:if>
           		<g:else>
           			0H0
           </g:else>
        </td>
		<td style="text-align:center">
			<g:if
           		test="${annualPayableCompTime !=null && (annualPayableCompTime.get(0)>0 ||annualPayableCompTime.get(1)>0)}">
               		${annualPayableCompTime.get(0)}H${annualPayableCompTime.get(1)==0?'':annualPayableCompTime.get(1)} 
           	</g:if>
           	<g:else>
           			0H0
           	</g:else>
        </td>

		<td style="text-align:center">0</td>
<td></td>
	</tr>
</table>

</html>