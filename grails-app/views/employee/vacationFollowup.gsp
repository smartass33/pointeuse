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
				url="[controller:'employee', action:'vacationFollowup']">
				<g:message code="laboratory.label" default="Search"
					style="vertical-align: middle;" />
				<g:if test="${siteId != null && !siteId.equals('')}">
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':site.name]}" optionKey="id" optionValue="name"
						style="vertical-align: middle;" />
				</g:if>
				<g:else>
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
					
						noSelection="${['':(site?site.name:'-')]}" optionKey="id" optionValue="name"
												style="vertical-align: middle;" />						
				</g:else>				
				
				${message(code: 'default.period.label', default: 'List')}
					<g:select name="year" from="${Period.list([sort:'year'])}"
						value="${period}"
						noSelection="${['':(period?period:'-')]}" optionKey="id" 
						style="vertical-align: middle;" />
				<g:actionSubmit class='listButton' value="${message(code: 'default.search.label', default: 'List')}"  action="vacationFollowup"/>		
				<g:hiddenField name="isAdmin" value="${isAdmin}" />
				<g:if test="${site!=null}">	
					<g:hiddenField name="site.id" value="${site.id}" />		
				</g:if>
				<g:hiddenField name="siteId" value="${siteId}" />
				<g:if test="${period!=null}">	
					<g:hiddenField name="year" value="${period.id}" />
					
				</g:if>
			</g:form>
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>
	

	<br>
	<div id="vacationList"><g:listVacationEmployee/></div>

</body>
</html>
