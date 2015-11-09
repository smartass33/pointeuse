<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>		
<g:if test="${employeeDataListMapInstance != null}">			
	<table>
		<thead>
			<tr>					
				<th><g:message code="employeeDataListMap.fieldName.label" default="Field name" /></th>					
				<th><g:message code="employeeDataListMap.fieldType.label" default="Field type" /></th>		
				<th><g:message code="default.button.delete.label" default="Field type" /></th>															
			</tr>
		</thead>
		<tbody>
			<g:each in="${employeeDataListMapInstance.fieldMap}" status="i" var="fieldMap">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">								
				<td>${fieldMap != null ? fieldMap.key : '-'}</td>
				<td>${fieldMap != null ? fieldMap.value : '-'}</td>
				<td>
		    		<g:remoteLink action="trashEmployeeData" controller="employeeDataListMap" id="trashEmployeeData" params="[fieldMap:fieldMap.key]"
	                    	update="employeeDataDiv"
	                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    	onComplete="document.getElementById('spinner').style.display = 'none';"
	                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
	                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
	                </g:remoteLink>							
				</td>				
			</g:each>
		</tbody>
	</table>
</g:if>