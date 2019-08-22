<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Service" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.Function" %>

<div>
	<script>		
	$(document).ready(
    function() {
        $("#newContractButton").click(function() {
            $("#newContractForm").toggle();
        });
       	$("#cancelContract").click(function() {
            $("#newContractForm").toggle();
        });
        
        $("#newStartDate").datepicker();
		$("#newEndDate").datepicker();
        
    });
	</script>

	<g:if test="${flash.message2}">
		<div class="message" role="status">${flash.message2}</div>
	</g:if>


	<table class="form-table" id="customFields">
		<thead>
			<th>${message(code: 'contract.start.date', default: 'Report')}</th>
			<th>${message(code: 'contract.end.date', default: 'Report')}</th>
			<th>${message(code: 'employee.weeklyContractTime.label', default: 'Report')}</th>
			<th>${message(code: 'employee.status.label', default: 'Report')}</th>	
			<th>${message(code: 'default.button.delete.label', default: 'Report')}</th>								
		</thead>
		<tbody>
			<g:if test='${previousContracts != null}'>
				<g:each in="${previousContracts}"  status="i" var="previousContract">
					<tr valign="top">
						<script>
							jQuery(function($){		   
					   			$("#startDate_${i}").datepicker();
					   			$("#endDate_${i}").datepicker();
					   			('#startDate_${i}').change = "alert('ffff');"
								$( "#startDate_${i}" ).change(function() {				
								   ${remoteFunction(
										action:"changeContractParams",
										controller:"employee",
										update:"contractTable",								
										params:"\'newDate=\'+ this.value+\'&userId=\'+\'${employeeInstance.id}+\'+\'&contractId=\'+\'${previousContract.id}+\'+\'&type=\'+this.id"								
									)};			  
								});			   			
					   			$( "#endDate_${i}").change(function() {				
								   ${remoteFunction(
										action:"changeContractParams",
										controller:"employee",
										update:"contractTable",
										params:"\'newDate=\'+ this.value+\'&userId=\'+\'${employeeInstance.id}+\'+\'&contractId=\'+\'${previousContract.id}+\'+\'&type=\'+this.id"								
									)};			  
								});	
					   		
						   		$( "#previousContractLength_${i}").change(function() {				
								   ${remoteFunction(
										action:"changeContractParams",
										controller:"employee",
										update:"contractTable",									
										params:"\'newValue=\'+ this.value+\'&userId=\'+\'${employeeInstance.id}+\'+\'&contractId=\'+\'${previousContract.id}+\'+\'&type=\'+this.id"								
									)};						
								});	
					   		
					   		});
						</script>
		
					    <g:if test="${previousContract.endDate == null}">
					    	<td style='background-color:green;'><input type="text" class="code" id="startDate_${i}" name="startDate_${i}" format="dd/MM/yyyy" value="${previousContract.startDate.format('dd/MM/yyyy')}" /></td>    
		   			    	<td style='background-color:green;'><input type="text" class="code" id="endDate_${i}" name="endDate_${i}" format="dd/MM/yyyy"  /></td>		    
					    	<td style='background-color:green;'><input type="number" class="code" id="previousContractLength_${i}" name="previousContractLength_${i}" value="${previousContract ? previousContract.weeklyLength : 35}" /> &nbsp;</td>			    
					    	<td style='background-color:green;'>${message(code: 'contract.ongoing', default: 'Create')}</td>			    	
					 		<td style='background-color:green;'>
						    	<g:remoteLink action="trashContract" controller="employee" id="${previousContract}" params="[contractId:previousContract.id]"
				                    	update="contractTable"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';"
				                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
				                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
				                </g:remoteLink>
					    	</td>   	
					    </g:if>
					    <g:else>
					    	<td style='background-color:grey;'><input type="text" class="code" id="startDate_${i}" name="startDate_${i}" format="dd/MM/yyyy" value="${previousContract.startDate.format('dd/MM/yyyy')}" /></td>    
					       	<td style='background-color:grey;'><input type="text" class="code" id="endDate_${i}" name="endDate_${i}" format="dd/MM/yyyy" value="${previousContract.endDate.format('dd/MM/yyyy')}" /></td>			    
					   		<td style='background-color:grey;'><input type="number" step="any" class="code" id="previousContractLength_${i}" name="previousContractLength_${i}" value="${previousContract ? previousContract.weeklyLength : 35}" /> &nbsp;</td>
					    	<td style='background-color:grey;'>${message(code: 'contract.terminated', default: 'Report')}</td>
					 		<td style='background-color:grey;'>
						    	<g:remoteLink action="trashContract" controller="employee" id="${previousContract}" params="[contractId:previousContract.id]"
				                    	update="contractTable"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';"
				                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
				                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
				                </g:remoteLink>
					    	</td>  
					    </g:else>
		
					</tr>
				</g:each>
			</g:if>
			<tr>		
				<td><div id="newContractButton"><a href="#">${message(code: 'contract.add.label', default: 'Report')}</a></div></td>
			</tr>	
	
	    	<tr id="newContractForm">
	    		<td><input type="text" class="code" id="newStartDate"  value="" name="newStartDate" /></td>
	    		<td><input type="text" class="code" id="newEndDate" name="newEndDate"  value="" /></td> 
	    		<td><input type="number" class="code" name="newContractValue" value="" placeholder="valeur hebdo" /></td> 
	    		<td ><a href="#" id="cancelContract">${message(code: 'default.button.cancel.label', default: 'Report')}</a></td>
	    		<td><input type="submit" class="listButton" value="Ajouter" name="_action_addNewContract"></td>
	    	</tr>
		</tbody>		
	</table>
	<g:if test='${previousContracts != null}'>	
		<table>
			<tr>
				<g:each in="${previousContracts.reverse()}"  status="i" var="previousContract">
					<g:if test="${previousContract.endDate != null }">
						<td class="${(i % 2) == 0 ? 'even' : 'odd'}">${previousContract.startDate.format('MMM yyyy')} - ${previousContract.endDate.format('MMM yyyy')}</td>
					</g:if>
					<g:else>
						<td class="${(i % 2) == 0 ? 'even' : 'odd'}">${previousContract.startDate.format('MMM yyyy')} - ? </td>
					</g:else>
				</g:each>
			</tr>
			<tr>
				<g:each in="${previousContracts.reverse()}"  status="i" var="previousContract">
					<td style="text-align:center;" class="${(i % 2) == 0 ? 'even' : 'odd'}">${previousContract.weeklyLength}</td>
				</g:each>
			</tr>
		</table>
	</g:if>
</div>