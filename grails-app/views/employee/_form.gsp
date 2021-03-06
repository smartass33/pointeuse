<%@ page import="pointeuse.Employee" %>
<%@ page import="pointeuse.Service" %>
<%@ page import="pointeuse.Site" %>
<%@ page import="pointeuse.Function" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="pointeuse.Title" %>

<style>
			#extra_param_div {
    			display: none;
			}
			
			#extra_param_collapse {
    			display: none;
			}
</style>

<script>
	$(document).ready(function() {
  			$('#extra_param_expand').click( function() {
	   			$('#extra_param_div').slideToggle(400);
	   			$('#extra_param_collapse').show();
	   			$('#extra_param_expand').hide();
  			});
	});
	
	$(document).ready(function() {
  			$('#extra_param_collapse').click( function() {
	   			$('#extra_param_div').slideToggle(400);
	   			$('#extra_param_collapse').hide();
	   			$('#extra_param_expand').show();
  			});
	});

</script>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'title', 'title')} ">
	<label for="title">
		<g:message code="employee.title.label" default="Title" />
		
	</label>
	<g:select name="title" from="${pointeuse.Title?.values()}" keys="${pointeuse.Title.values()*.name()}" valueMessagePrefix="enum.value" required="" value="${employeeInstance?.title?.name()}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'lastName', 'error')} ">
	<label for="lastName">
		<g:message code="employee.lastName.label" default="Last Name" />
		
	</label>
	<g:textField name="lastName" value="${employeeInstance?.lastName}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'firstName', 'error')} ">
	<label for="firstName">
		<g:message code="employee.firstName.label" default="First Name" />
		
	</label>
	<g:textField name="firstName" value="${employeeInstance?.firstName}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'birthName', 'error')} ">
	<label for="birthName">
		<g:message code="employee.birthName.label" default="Birth Name" />
		
	</label>
	<g:textField name="birthName" value="${employeeInstance?.birthName}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'userName', 'error')} ">
	<label for="userName">
		<g:message code="employee.username.label" default="User Name" />
	</label>
	<g:textField name="userName" value="${employeeInstance?.userName}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'matricule', 'error')} ">
	<label for="matricule">
		<g:message code="employee.matricule.label" default="Matricule" />
	</label>
	<g:textField name="matricule" value="${employeeInstance?.matricule}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'hasNightJob', 'error')} ">
	<label for="matricule">
		<g:message code="employee.hasNightJob.label" default="Night Job" />
	</label>
	<g:select id="hasNightJob" name="hasNightJob" from="${[true,false]}" value="${employeeInstance?.hasNightJob}" valueMessagePrefix="boolean.hasNightJob.select" />
</div>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'arrivalDate', 'error')} ">
	<label for="arrivalDate">
		<g:message code="employee.arrivalDate.label" default="arrivalDate" />
	</label>
	<g:if test="${employeeInstance?.arrivalDate != null}">
		<g:datePicker name="arrivalDate" value="${employeeInstance?.arrivalDate}" precision="day" noSelection="['':'-Choose-']" />
	</g:if>
	<g:else>
		<g:datePicker name="arrivalDate" value="${new Date()}" precision="day" noSelection="['':'-Choose-']" />
	</g:else>
</div>
<g:if test="${employeeInstance.id == null }">
	<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'weeklyContractTime', 'error')} ">
		<label for="weeklyContractTime">
			<g:message code="employee.weeklyContractTime.label" default="weeklyContractTime" />
		</label>
		<g:textField name="weeklyContractTime" value="${employeeInstance?.weeklyContractTime}"/>
	</div>
</g:if>
<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'service', 'error')} ">
	<label for="service">
		<g:message code="employee.service.label" default="Service" />
	</label>
	<g:if test="${employeeInstance?.service != null}">
		<g:select name="employee.service.id"
	          from="${Service.list([sort:'name'])}"
	          noSelection="${['':employeeInstance?.service.name]}"          
	          optionKey="id" optionValue="name"
	          />
	</g:if>
	<g:else>
			<g:select name="employee.service.id"
	          from="${Service.list([sort:'name'])}"
	          noSelection="${['':'-']}"          
	          optionKey="id" optionValue="name"
	          />
	</g:else>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'site', 'error')} ">
	<label for="site">
		<g:message code="employee.site.label" default="Site" />
	</label>
	
	<g:if test="${employeeInstance?.site != null}">
		<g:select name="employee.site.id"
	          from="${Site.list([sort:'name'])}"
	          noSelection="${['':employeeInstance?.site.name]}"          
	          optionKey="id" optionValue="name"
	          />
	</g:if>
	<g:else>
			<g:select name="employee.site.id"
	          from="${Site.list([sort:'name'])}"
	          noSelection="${['':'-']}"          
	          optionKey="id" optionValue="name"
	          />
	</g:else>
</div>

<div class="fieldcontain ${hasErrors(bean: employeeInstance, field: 'function', 'error')} ">
	<label for="function">
		<g:message code="function.label" default="Site" />
	</label>	
	<g:if test="${employeeInstance?.function != null}">
		<g:select name="employee.function.id"
	          from="${Function.list([sort:'name'])}"
	          noSelection="${['':employeeInstance?.function.name]}"          
	          optionKey="id" optionValue="name"
	          />
	</g:if>
	<g:else>
			<g:select name="employee.function.id"
	          from="${Function.list([sort:'name'])}"
	          noSelection="${['':'-']}"          
	          optionKey="id" optionValue="name"
	          />
	</g:else>
</div>
<div id='extra_param_image'>
	<button type='button' id="extra_param_expand" ><img alt="toggle" src="${grailsApplication.config.context}/images/expand.png"></button>
	<button type='button' id="extra_param_collapse" ><img alt="toggle" src="${grailsApplication.config.context}/images/collapse.png"></button>
	${message(code: 'extra.param', default: 'Update')}
</div>	
<g:if test="${employeeDataListMapInstance != null && employeeDataListMapInstance.fieldMap != null}">
	<div id=extra_param_div>
		<g:each in="${dataListRank}" status="i" var="rank">
			<div  class="fieldcontain"  id="${rank.rank}">
				<label for="${rank.fieldName}"><g:message code="${rank.fieldName}" default="${rank.fieldName}" /></label>	
				<g:if test = "${employeeInstance.extraData != null}">	
					<g:if test = "${((employeeDataListMapInstance.fieldMap).get(rank.fieldName)).equals('DATE') }">
						<g:if test="${(employeeInstance.extraData).get(rank.fieldName) != null }">
							<g:datePicker name="${rank.fieldName}" precision="day" value="${(new SimpleDateFormat("yyyyMd", Locale.ENGLISH)).parse((employeeInstance.extraData).get(rank.fieldName))}"/>		
						</g:if>
						<g:else>
							<g:datePicker name="${rank.fieldName}" precision="day" value="${new Date()}"/>		
						
						</g:else>
					</g:if>	
					<g:else>
						<g:if test = "${((employeeDataListMapInstance.fieldMap).get(rank.fieldName)).equals('NUMBER')}">
							<input type="number" class="code" id="${rank.fieldName}" name="${rank.fieldName}" value="${(employeeInstance.extraData).get(rank.fieldName)}"  /> 				
						</g:if>
						<g:else>
							<g:textField name="${rank.fieldName}" value="${(employeeInstance.extraData).get(rank.fieldName)}"/>				
						</g:else>		
					</g:else>				
				</g:if>		
				<g:else>	
					<g:textField name="${rank.fieldName}" value=""/>			
				</g:else>
			</div>
		</g:each>
	</div>
</g:if>
	          