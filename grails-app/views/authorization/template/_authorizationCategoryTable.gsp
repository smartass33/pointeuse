<%@ page import="pointeuse.Authorization" %>
<%@ page import="pointeuse.Category" %>
<%@ page import="pointeuse.SubCategory" %>


<script>
  $(document).ready(function(){
       $("select[id ^= timepicker]").change(function (){
         	var name = jQuery(this).attr('name');
          	var val = document.getElementById(name).value;
			jQuery.ajax({
				type:'POST',
	        	data : { name:name,val:val},
	        	success:function(response) {
							 $("#authorizationDiv").html(response);},
	        	url:'${createLink(controller:'authorization',action: 'updateTime')}',
	        	error:function(XMLHttpRequest,textStatus,errorThrown){}
	    	});
       });
    });

</script>
<html>
	<body>
		<div id="list-authorization" class="content scaffold-list" role="main" >
			<h1><g:message code="default.authorizations.label" /> <g:if test="${employeeInstance != null}"> pour ${employeeInstance.firstName} ${employeeInstance.lastName}</g:if></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<g:each in="${Category.findAll()}" status="i" var="category">
					<tr >
						<td>${category.name}</td>
							<g:if test="${Authorization.findByCategoryAndEmployee(category,employeeInstance) != null}">
								<td>
									<g:checkBox name='category' value="${Authorization.findByCategoryAndEmployee(category,employeeInstance).isAuthorized}"
										onclick="${remoteFunction(controller:'authorization', action:'updateAuthorization', id:category.id, update:'authorizationDiv',
										params:' \'type=\'+\'category\'+  \'&completed=\' + this.checked  + \'&employeeInstanceId=\' +\'' + employeeInstance.id + '\'    '
										)}"
									/>
								</td>
								<td>
									<g:if test="${Authorization.findByCategoryAndEmployee(category,employeeInstance).startDate != null}">
										<g:datePicker id='timepicker_category_${category.name}_start_${employeeInstance.id}' name="timepicker_category_${category.name}_start_${employeeInstance.id}" precision="day"  value="${Authorization.findByCategoryAndEmployee(category,employeeInstance).startDate}"  />
									</g:if>
								
								</td>
								<td>
									<g:if test="${Authorization.findByCategoryAndEmployee(category,employeeInstance).endDate != null}">
										<g:datePicker id='timepicker_category_${category.name}_stop_${employeeInstance.id}' name="timepicker_category_${category.name}_stop_${employeeInstance.id}" precision="day"  value="${Authorization.findByCategoryAndEmployee(category,employeeInstance).endDate}"  />
									</g:if>
								
								</td>
							</g:if>
							<g:else>
								<td>
									<g:checkBox name='category' value="${false}"
										onclick="${remoteFunction(controller:'authorization', action:'updateAuthorization', id:category.id, update:'authorizationDiv',
										params:' \'type=\'+\'category\'+  \'&completed=\' + this.checked  + \'&employeeInstanceId=\' +\'' + employeeInstance.id + '\'    '
										)}"
									/>
								</td>
							</g:else>
					</tr>	
						<g:each in="${SubCategory.findAllByCategory(category)}" status="j" var="subCategory">
							<tr style='text-indent: 30px;'>
								<td >${subCategory.name}</td>
									<g:if test="${Authorization.findBySubCategoryAndEmployee(subCategory,employeeInstance) != null}">
										<td>
											<g:checkBox name='subCategory' value="${Authorization.findBySubCategoryAndEmployee(subCategory,employeeInstance).isAuthorized}"
												onclick="${remoteFunction(controller:'authorization', action:'updateAuthorization', id:subCategory.id, update:'authorizationDiv',
												params:' \'type=\'+\'subCategory\'+  \'&completed=\' + this.checked  + \'&employeeInstanceId=\' +\'' + employeeInstance.id + '\'    '
												)}"
											/>
										</td>
										<td>
											<g:if test="${Authorization.findBySubCategoryAndEmployee(subCategory,employeeInstance).startDate != null}">
												<g:datePicker id="timepicker_subcategory_${subCategory.name}_start_${employeeInstance.id}" name="timepicker_subcategory_${subCategory.name}_start_${employeeInstance.id}" precision="day"  value="${Authorization.findBySubCategoryAndEmployee(subCategory,employeeInstance).startDate}"  />											
											</g:if>								
										</td>
										<td>
											<g:if test="${Authorization.findBySubCategoryAndEmployee(subCategory,employeeInstance).endDate != null}">
												<g:datePicker id="timepicker_subcategory_${subCategory.name}_stop_${employeeInstance.id}" name="timepicker_subcategory_${subCategory.name}_stop_${employeeInstance.id}" precision="day"  value="${Authorization.findBySubCategoryAndEmployee(subCategory,employeeInstance).endDate}"  />											
											</g:if>
										
										</td>
									</g:if>
									<g:else>
										<td>
											<g:checkBox name='subCategory' value="${false}"
												onclick="${remoteFunction(controller:'authorization', action:'updateAuthorization', id:subCategory.id, update:'authorizationDiv',
												params:' \'type=\'+\'subCategory\'+  \'&completed=\' + this.checked  + \'&employeeInstanceId=\' +\'' + employeeInstance.id + '\'    '
												)}"
											/>
										</td>
									</g:else>						
							</tr>
						</g:each>
				</g:each>
			</table>
		</div>
	</body>
</html>
