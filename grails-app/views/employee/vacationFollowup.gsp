<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Vacation"%>
<%@ page import="pointeuse.Period"%>

<!doctype html>
<html>
<head>
	<g:javascript library="jquery" plugin="jquery" />
	<resource:autoComplete skin="default" />
	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />
	
	<g:set var="entityName"
		value="${message(code: 'employee.label', default: 'Employee')}" />
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
			}
			th {
				padding: 2px 4px 2px 4px;
			}
		</style>
<script type="text/javascript">

$('label').click(function(){
    $(this).children('span').addClass('input-checked');
    $(this).parent('.toggle').siblings('.toggle').children('label').children('span').removeClass('input-checked');
});

   function showSpinner() {
      $('spinner').show();
   }
   function hideSpinner() {
      $('spinner').hide();
   }
   Ajax.Responders.register({
      onLoading: function() {
         showSpinner();
      },
      onComplete: function() {     
         if(!Ajax.activeRequestCount) hideSpinner();
      }
   });
   
   

</script>

</head>
<body>

<div id="spinner" class="spinner" style="display:none;">
<img src="${createLinkTo(dir:'images',file:'spinner.gif')}" alt="Spinner" />
</div>

<div id="spinner" style="display: none;">
   <img src="${createLinkTo(dir:'images',file:'spinner.gif')}" alt="Loading..." width="16" height="16" />
</div>
	<div class="nav" id="nav">
		<g:headerMenu />
	</div>
	<div id="list-employee" class="content scaffold-list">
		<h1>
			<g:message code="default.list.label" args="[entityName]" />
		
			<br>
			<br>
			<g:form method="POST"
				url="[controller:'employee', action:'updateVacationTable']">
				<g:message code="laboratory.label" default="Search"
					style="vertical-align: middle;" />
				<g:if test="${siteId != null && !siteId.equals('')}">
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':site.name]}" optionKey="id" optionValue="name"
						onChange="${remoteFunction(action:'updateVacationTable', params:'\'site=\'+this.value+\'&isAdmin=\'+\'' + isAdmin + '\'',update:'[list-employee,vacationList]',onSuccess:site=site)}"
						style="vertical-align: middle;" />
				</g:if>
				<g:else>
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':'-']}" optionKey="id" optionValue="name"
						onChange="${remoteFunction(action:'updateVacationTable', params:'\'site=\'+this.value+\'&isAdmin=\'+\'' + isAdmin + '\'',update:'vacationList')}" />
				</g:else>				
				<g:remoteField id="q" action="updateVacationTable" update="vacationList"
					onclick="if (this.value=='chercher un salarié') {this.value = '';}"
					name="q" value="${params.q ?: 'chercher un salarié'}" paramName="q"
					style="vertical-align: middle;"
					params="\'q=\'+this.value+\'&isAdmin=\'+\'${isAdmin}+\'"/>

				${message(code: 'default.period.label', default: 'List')}:

					<g:select name="myDate" from="${Period.list([sort:'year'])}"
						noSelection="${['':'-']}" optionKey="id" optionValue="year"
						onChange="${remoteFunction(action:'updateVacationTable', params:'\'myDate=\'+this.value+\'&isAdmin=\'+\'' + isAdmin + '\'',update:'vacationList')}"
						style="vertical-align: middle;" />




				<g:actionSubmit class='listButton' value="period"  action="vacationFollowup"/>		
						<!--g:actionSubmit class='listButton' value="excel"  action="excel"/-->		
						
				<g:hiddenField name="isAdmin" value="${isAdmin}" />
				<g:if test="${site!=null}">	
					toto			
					<g:hiddenField name="site" value="${site}" />		
				</g:if>
				<g:hiddenField name="siteId" value="${siteId}" />
			</g:form>

			
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>
	

	<br>
		<div id="vacationList"><g:listVacationEmployee /></div>
	
	<g:if test="${employeeInstanceTotal!=null}">
	<div class="pagination" id="pagination">
		<g:hiddenField name="isAdmin" value="${isAdmin}" />
		<g:paginate total="${employeeInstanceTotal}"
			params="${[isAdmin:isAdmin]}" />
	</div>
	</g:if>
</body>
</html>
