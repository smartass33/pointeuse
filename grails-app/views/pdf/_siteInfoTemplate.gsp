<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Employee"%>
		<style  type="text/css">
			@page {
			   size: 297mm 210mm;
			   margin: 0px 0px 13px 0px;
			 }
				table {
			  font: normal 11px verdana, arial, helvetica, sans-serif;
			  color: #363636;
			  background: #f6f6f6;
			  background-color: white;
			  text-align:right;
			  }
		
		</style>	
	</head>
	<body>
		<h1 style="text-align:center;font-size:150%"><g:if test="${site != null}"><g:message code="site.followup"/></g:if>
</h1>
		<table id="employee-table" >
			<thead>
				<tr>
					<th property="lastName" style="width:150px;text-align:center" title="${message(code: 'employee.lastName.label', default: 'Last Name')}" />
					<th property="site" style="text-align:center" title="${message(code: 'employee.site.label', default: 'Site')}" />
					<th style="text-align:center" class="principal">${message(code: 'employee.function.label', default: 'Site')}</th>		
					<th property="status" style="text-align:center" class="principal" title="${message(code: 'employee.entry.status', default: 'Entry')}" />
					<th property="lastName" style="width:150px;text-align:center"  title="${message(code: 'employee.username.label', default: 'User Name')}" />
					<th property="weeklyContractTime" style="width:90px;text-align:center"  title="${message(code: 'employee.weeklyContractTime.short.label', default: 'weeklyContractTime')}" />
					<th property="arrivalDate" style="text-align:center"  title="${message(code: 'employee.arrivalDate.short.label', default: 'arrivalDate')}" />
					<th property="service" style="text-align:center"  title="${message(code: 'employee.service.label', default: 'service')}" />
					<th property="matricule" style="text-align:center"  title="${message(code: 'employee.matricule.label', default: 'matricule')}" />
				</tr>
			</thead>
			<tbody id='body_update' style="border:1px;">
				<g:each in="${employeeInstanceList}" status="i" var="employeeInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td style="width:200px" >${employeeInstance.lastName} ${employeeInstance.firstName}</td>
						<td><g:if test="${employeeInstance?.site != null}">${employeeInstance?.site.name}</g:if></td>					
						<td><g:if test="${employeeInstance?.function != null}">${employeeInstance?.function.name}</g:if></td>				
						<td><g:if test="${employeeInstance?.userName}">${employeeInstance.userName}</g:if></td>
						<td><g:if test="${employeeInstance?.weeklyContractTime}">${employeeInstance.weeklyContractTime }</g:if></td>
						<td><g:if test="${employeeInstance?.arrivalDate}">${employeeInstance.arrivalDate.format('dd/MM/yyyy')}</g:if></td>
						<td><g:if test="${employeeInstance?.service}">${employeeInstance.service.name}</g:if></td>
						<td><g:if test="${employeeInstance?.matricule}">${employeeInstance.matricule}</g:if></td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</body>
</html>