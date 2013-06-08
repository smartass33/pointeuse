<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>


<table id="annual-report-table">
	<thead>
		<th>year</th>
	
		<th>month</th>
		<th>workingDays</th>
		<th>holiday</th>
		<th>rtt</th>
		<th>sickness</th>
		<th>sansSolde</th>
		<th>monthTheoritical</th>
		<th>monthElapsed</th>	
	</thead>
	<tbody id='body_update' style="border:1px;">
		<g:each in="${lastYearMonthMap}" var="cartouche">
			<tr>
				<td>${lastYear}</td>			
				<td>${cartouche.key}</td>
				<td>${cartouche.value.get(3)}</td>
				<td>${cartouche.value.get(4)}</td>
				<td>${cartouche.value.get(5)}</td>
				<td>${cartouche.value.get(6)}</td>
				<td>${cartouche.value.get(7)}</td>
				<td>${cartouche.value.get(8)}</td>
				<td>${lastYearTotalMap.get(cartouche.key)}</td>			
			</tr>
		</g:each>
		<g:each in="${thisYearMonthMap}" var="cartouche">
			<tr>
				<td>${thisYear}</td>			
				<td>${cartouche.key}</td>
				<td>${cartouche.value.get(3)}</td>
				<td>${cartouche.value.get(4)}</td>
				<td>${cartouche.value.get(5)}</td>
				<td>${cartouche.value.get(6)}</td>
				<td>${cartouche.value.get(7)}</td>
				<td>${cartouche.value.get(8)}</td>
				<td>${thisYearTotalMap.get(cartouche.key)}</td>			
			</tr>
		</g:each>
	</tbody>
</table>

