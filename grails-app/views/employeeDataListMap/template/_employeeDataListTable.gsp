<%@ page import="pointeuse.EmployeeDataListRank"%>

<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>		

<script type="text/javascript">
    
    $(document).ready(
        function(){
            $("#dataListTable").sortable({
                axis: 'y',
                opacity: 0.6,
                update: function (sorted){
                            var order = $(this).sortable('serialize');
	 						jQuery.ajax({
								type:'POST',
						        data : order,
						        success:function(response) {
   									 $("#dataListTableDiv").html(response);
								},
						        url:'${createLink(action: 'changeRank')}',
						        error:function(XMLHttpRequest,textStatus,errorThrown){}
						    });
                        }
            });
        }
    );

</script>
<div id="dataListTableDiv">
	<ul id="dataListTable" style="list-style-type:none;">
		<table>
			<thead>
				<tr>	
					<th style="width:70px;text-align: center;">Position</th>					
					<th style="width:200px;text-align: center;"><g:message code="employeeDataListMap.fieldName.label" default="Field name" /></th>					
					<th style="width:100px;text-align: center;"><g:message code="employeeDataListMap.fieldType.label" default="Field type" /></th>		
					<th style="width:80px;text-align: center;"><g:message code="default.button.delete.label" default="Field type" /></th>
					<th style="width:250px;text-align: center;">employés utilisant ce champ</th>																				
																									
				</tr>
			</thead>
	     </table>
	         
	         
	   	<g:each in="${dataListRank}" status="i" var="rank">	
				<li id="Element_${i}" >
					<table>
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td style="width:70px">${rank.rank}</td>
							<td style="width:200px;text-decoration: none;"><g:fieldUpdatePopup id="${rank.rank}" fieldName="${rank.fieldName}" type="${(employeeDataListMapInstance.fieldMap).get(rank.fieldName)}"/></td>			
							<td style="width:100px">${(employeeDataListMapInstance.fieldMap).get(rank.fieldName)}</td>
							<td style="width:80px;text-align: center;">
					    		<g:remoteLink action="trashEmployeeData" controller="employeeDataListMap" id="trashEmployeeData" params="[fieldMap:rank.fieldName]"
				                    	update="dataListTableDiv"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';"
				                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
				                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
				                </g:remoteLink>							
							</td>	
							<td style="width:250px;text-align: center;">${hasEmployeeMap.get(rank.fieldName)}</td>
						</tr>	
					</table>
					
				</li>		
		</g:each>     
	         

	</ul>
</div>

