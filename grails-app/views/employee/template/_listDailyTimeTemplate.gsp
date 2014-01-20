<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>


<table id="employee-table">
	<thead>
		<tr>
			<g:sortableColumn property="lastName" style="width:180px;text-align:left"
				title="${message(code: 'employee.lastName.label', default: 'Last Name')}" />
			<g:sortableColumn property="firstName" style="width:180px;text-align:left"
				title="${message(code: 'employee.firstName.label', default: 'First Name')}" />	
			<g:sortableColumn property="site" style="width:120px;text-align:left"
				title="${message(code: 'employee.site.label', default: 'Site')}" />
			<g:sortableColumn property="total jour" style="text-align:left"
				title="Total Jour"/>	
			<g:sortableColumn property="Heures supplementaires" style="text-align:left"
				title="Heures supplementaires"/>	
			<th  style="text-align:left" colspan="10"
				title="Evènements">Evènements</th>
		</tr>
	</thead>

	<tbody id='body_update' style="border:1px;">
		<g:each in="${dailyMap}" status="i" var="entry">
			<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				<td style="width:180px">${entry.key.lastName}</td>
				<td style="width:180px">${entry.key.firstName}</td>
				<td style="width:120px;text-align:left">${entry.key.site.name}</td>
				<td style="width:120px;text-align:left">
					<g:if test='${entry.value.get(0)<10}'>0</g:if>${entry.value.get(0)} :
					<g:if test='${entry.value.get(1)<10}'> 0</g:if>${entry.value.get(1)} 
				</td>
				<td style="width:120px;text-align:left">
					<g:if test='${(dailySupMap.get(entry.key)).get(0)<10}'>0</g:if>${(dailySupMap.get(entry.key)).get(0)} :
					<g:if test='${(dailySupMap.get(entry.key)).get(1)<10}'> 0</g:if>${(dailySupMap.get(entry.key)).get(1)} 
				</td>
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

