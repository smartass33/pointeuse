<%@ page import="pointeuse.StatusType" %>


<script>
	jQuery(function($){		    			
  			$("#suspensionDate").datepicker();
  			$("#suspensionDate").change = "alert('ffff');"		
  			$( "#suspensionDate" ).change(function() {				
				${remoteFunction(
									action:"changeContractStatus",
									controller:"employee",
									update:"contractStatus",								
									params:"\'newDate=\'+ this.value+\'&employeeId=\'+\'${employeeInstance.id}+\'"								
																	)};
			});
  		});
</script>



<div id='contractStatus' class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="employee.status.label" default="Status" />
	</label>
	<g:if test='${employeeInstance.status != null }'>
		<g:select 
		 	onchange="${remoteFunction(action:'changeContractStatus', update:'contractStatus', 
								  params:'\'employeeId=' + employeeInstance.id 		
				 					+ '&updatedSelection=\' + this.value')}"
			name="status"
	        from="${StatusType.values()}"
	        value="${employeeInstance.status.type}" optionValue="key"
	     />        
	     <g:if test="${employeeInstance.status.date != null}" >
	     	<input type="text" class="code" id="suspensionDate" value="${employeeInstance.status.date.format('dd/MM/yyyy')}" name="suspensionDate" /></td>
	     </g:if> 
	</g:if>
	<g:else>
		${StatusType.ACTIF.key}
	</g:else>
</div>