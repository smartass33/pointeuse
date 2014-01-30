<%@ page import="pointeuse.Vacation" %>
<!DOCTYPE html>
<html>
	<head>		
		<g:javascript library="jquery" plugin="jquery" />
		<g:javascript src="/jquery/jquery.table.addrow.js" />
		<modalbox:modalIncludes />	
		
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'vacation.label', default: 'Vacation')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
				<style type="text/css">
			body {
				font-family: Verdana, Arial, sans-serif;
				font-size: 0.9em;
			}
			table {
				border-collapse: collapse;
			}
			thead {
				background-color: #DDD;
			}
			td {
				padding: 2px 4px 2px 4px;
				text-align:center;
			}
			th {
				padding: 2px 4px 2px 4px;
			}
		</style>
		
	</head>
	<body>
		<a href="#list-vacation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li style="vertical-align: middle;"><g:link class="list" action="list"
							params="${[isAdmin:isAdmin,siteId:siteId,back:true]}">
							${message(code: 'employee.back.to.list.label', default: 'List')}
						</g:link></li>
			</ul>
		</div>
		<div id="list-vacation" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /> pour ${employeeInstance.firstName } ${employeeInstance.lastName } </h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>					
						<g:sortableColumn property="period" title="${message(code: 'vacation.period.label', default: 'Period')}" />					
						<g:sortableColumn property="reference_vacation" title="${message(code: 'vacation.reference.label', default: 'Type')}" />					
						<g:sortableColumn property="taken_vacation" title="${message(code: 'vacation.taken.label', default: 'Type')}" />					
						<g:sortableColumn property="remainder_vacation" title="${message(code: 'vacation.remainder.label', default: 'Counter')}" />					
						<g:sortableColumn property="reference_rtt" title="${message(code: 'rtt.reference.label', default: 'Type')}" />					
						<g:sortableColumn property="taken_rtt" title="${message(code: 'rtt.taken.label', default: 'Type')}" />					
						<g:sortableColumn property="remainder_rtt" title="${message(code: 'rtt.remainder.label', default: 'Counter')}" />	
						<g:sortableColumn property="taken_sickness" title="${message(code: 'sickness.taken.label', default: 'Counter')}" />					
					</tr>
				</thead>
				<tbody>
				<g:each in="${yearMap}" var="year">
					<th colspan="8" >${year.value}
					</th>
					<tr>	
						<td/>	
						<td>${initialCAMap.getAt(year.key)}</td>
						<td>${takenCAMap.getAt(year.key)}</td>
						<td>${remainingCAMap.getAt(year.key)}</td>
						<td>${initialRTTMap.getAt(year.key)}</td>
						<td>${takenRTTMap.getAt(year.key)}</td>
						<td>${remainingRTTMap.getAt(year.key)}</td>
						<td>${takenSicknessMap.getAt(year.key)}</td>
					</tr>
				</g:each>
				</tbody>
			</table>

		</div>
	</body>
</html>
