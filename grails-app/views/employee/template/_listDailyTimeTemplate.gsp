<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>


<table id="employee-table">
	<thead>
		<tr>
			<th style="width:180px;text-align:center">${message(code: 'employee.lastName.label', default: 'Report')}</th>
			<th style="width:180px;text-align:center">${message(code: 'employee.firstName.label', default: 'Report')}</th>		
			<th style="width:120px;text-align:center">${message(code: 'employee.function.label', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.site.label', default: 'Report')}</th>
			<th style="width:90px;text-align:center">${message(code: 'employee.daily.time', default: 'Report')}</th>
			<th style="width:120px;text-align:center">${message(code: 'employee.monthly.sup.time', default: 'Report')}</th>
			<th  style="text-align:left" colspan="10" title="Evènements">${message(code: 'events.label', default: 'Site')}</th>
		</tr>
	</thead>
<!--my:humanTimeTD id="sundayTime"  name="sundayTime" value="${annualSundayTime}"/-->
	<tbody id='body_update' style="border:1px;">
		<g:each in="${dailyMap}" status="i" var="entry">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:180px">${entry.key.lastName}</td>
				<td style="width:180px">${entry.key.firstName}</td>
				<td style="width:120px;text-align:center">${entry.key.function.name}</td>
				<td style="width:120px;text-align:center">${entry.key.site.name}</td>
				<td style="width:90px;text-align:center"><my:humanTimeTD id="dailyTime"  name="dailyTime" value="${entry.value}"/></td>
				<td style="width:120px;text-align:center"><my:humanTimeTD id="supTime"  name="supTime" value="${dailySupMap.get(entry.key)}"/></td>
				<g:each in="${dailyInAndOutMap.get(entry.key)}" var="inOrOut">
					<g:if test="${inOrOut.type.equals('E')}">
						<td bgcolor="98FB98">${inOrOut.time.format('HH:mm')}</td>
					</g:if>
					<g:else>
						<td bgcolor="#FFC0CB">${inOrOut.time.format('HH:mm')}</td>
					</g:else>
				</g:each>
			</tr>
		</g:each>
	</tbody>
</table>

