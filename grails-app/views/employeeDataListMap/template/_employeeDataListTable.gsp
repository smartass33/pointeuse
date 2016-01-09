<%@ page import="pointeuse.EmployeeDataListRank"%>

<g:if test="${flash.message}">
	<div class="message" role="status">${flash.message}</div>
</g:if>		

<script type="text/javascript">
    
    $(document).ready(
        function(){
            $("#sortlistEquipe").sortable({
                axis: 'y',
                opacity: 0.6,
                update: function (sorted){
                            var order = $(this).sortable('serialize');
	 						jQuery.ajax({
								type:'POST',
						        data : order,
						        url:'${createLink(action: 'changeRank')}',
						        error:function(XMLHttpRequest,textStatus,errorThrown){}
						    });
                        }
            });
        }
    );

</script>
<div id="test">
	<ul id="sortlistEquipe" style="list-style-type:none;">
		<table>
			<thead>
				<tr>	
					<th><g:message code="employeeDataListMap.fieldName.label" default="Field name" /></th>					
					<th><g:message code="employeeDataListMap.fieldType.label" default="Field type" /></th>		
					<th><g:message code="default.button.delete.label" default="Field type" /></th>															
				</tr>
			</thead>
	     </table>
	         
	         
	   	<g:each in="${dataListRank}" status="i" var="rank">
				
				<li id="Element_${i}" >
					<table>
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td>${rank.fieldName} </td>
							<td>${(employeeDataListMapInstance.fieldMap).get(rank.fieldName)}</td>
							<td>
					    		<g:remoteLink action="trashEmployeeData" controller="employeeDataListMap" id="trashEmployeeData" params="[fieldMap:rank.fieldName]"
				                    	update="employeeDataDiv"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';"
				                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
				                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
				                </g:remoteLink>							
							</td>	
						</tr>	
					</table>
					
				</li>
				
		</g:each>     
	         

	</ul>
</div>

	
	<div class="shoppingList">
		<script type="text/javascript">
		 $(document).ready(function() {
			$(".shoppingList ul").shoppingList();
		});

	</script>
	
	<div class="reponse"></div>		
		<ul>
		<g:each in="${dataListRank}" status="i" var="rank">
				
				<li id="Element_${i}" >
					${rank.fieldName} 
				</li>
				
		</g:each>
		</ul>

	</div>
		
	

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
				<td>
					${fieldMap.key}
					</td>
				<!--td>
				
				<  g:remoteField action="modify" update="titleDiv" 
               			name="title" value="${fieldMap.key}" 
               			params="\'newKey=\'+this.value+\'&oldKey=\'+\'${fieldMap.key}+\'"
               		/>
				</td-->							
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