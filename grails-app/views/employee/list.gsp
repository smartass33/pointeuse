<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<!doctype html>
<html>
<head>
<g:javascript library="prototype" plugin="prototype" />
<resource:autoComplete skin="default" />
	<meta http-equiv="X-UA-Compatible" content="ie=9"/>
	<meta http-equiv='cache-control' content='no-cache'>
	<meta http-equiv='expires' content='0'>
	<meta http-equiv='pragma' content='no-cache'>

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

			/* Classes used for hiding more detail, by default */
			th.moreDetail {
				display: none;
			}			
			td.moreDetail {
				display: none;
			}
			
			/* Classes used for showing more detail */
			table.showDetail th.moreDetail {
				display: table-cell;
				background-color: #E4E4A2;
			}
			table.showDetail td.moreDetail {
				display: table-cell;
				background-color: #E4E4A2;
			}
			/* Style rule for IE 6 */
			* html table.showDetail .moreDetail {
				display: block;
			}
#donate {
    margin:4px;

    float:left;
}

#donate label {
    float:left;
    width:170px;
    margin:4px;
    background-color:#EFEFEF;
    border-radius:4px;
    border:1px solid #D0D0D0;
    overflow:auto;

}

#donate label span {
    text-align:center;
    font-size: 32px;
    padding:13px 0px;
    display:block;
}

#donate label input {
    position:absolute;
    top:-20px;
}

#donate input:checked + span {
    background-color:#404040;
    color:#F7F7F7;
}

#donate .yellow {
    background-color:#FFCC00;
    color:#333;
}

#donate .blue {
    background-color:#00BFFF;
    color:#333;
}

#donate .pink {
    background-color:#FF99FF;
    color:#333;
}

#donate .green {
    background-color:#A3D900;
    color:#333;
}
#donate .purple {
    background-color:#B399FF;
    color:#333;
}


.toggle {
    margin:4px;
    background-color:#EFEFEF;
    border-radius:4px;
    border:1px solid #D0D0D0;
    overflow:auto;
    float:left;
}

.toggle label {
    float:left;
    width:2.0em;
}

.toggle label span {
    text-align:center;
    padding:3px 0px;
    display:block;
    cursor: pointer;
}

.toggle label input {
    position:absolute;
    top:-20px;
}

.toggle .input-checked /*, .bounds input:checked + span works for firefox and ie9 but breaks js for ie8(ONLY) */ {
    background-color:#404040;
    color:#F7F7F7;
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
				url="[controller:'employee', action:'pdf']">
				<g:message code="laboratory.label" default="Search"
					style="vertical-align: middle;" />
				<g:if test="${siteId != null && !siteId.equals('')}">
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':site.name]}" optionKey="id" optionValue="name"
						onChange="${remoteFunction(action:'list', params:'\'site=\'+this.value+\'&isAdmin=\'+\'' + isAdmin + '\'',update:'divId')}"
						style="vertical-align: middle;" />

				</g:if>
				<g:else>
					<g:select name="site.id" from="${Site.list([sort:'name'])}"
						noSelection="${['':'-']}" optionKey="id" optionValue="name"
						onChange="${remoteFunction(action:'list', params:'\'site=\'+this.value+\'&isAdmin=\'+\'' + isAdmin + '\'',update:'divId')}" />
				</g:else>		
				
				<g:remoteField id="q" action="search" update="divId"
					onclick="if (this.value=='chercher un salarié') {this.value = '';}"
					name="q" value="${params.q ?: 'chercher un salarié'}" paramName="q"
					params="[isAdmin:isAdmin]" style="vertical-align: middle;" />
						${message(code: 'default.period.label', default: 'List')}: <g:datePicker
							name="myDate" value="${period ? period : new Date()}" 
							precision="month" noSelection="['':'-Choose-']" style="vertical-align: middle;"/>
							
		
						<g:actionSubmit class='listButton' value="pdf"  action="pdf"/>		
				<g:hiddenField name="isAdmin" value="${isAdmin}" />
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
	<div id="divId"><g:listEmployee /></div>
	<g:if test="${employeeInstanceTotal!=null}">
	<div class="pagination" id="pagination">
		<g:hiddenField name="isAdmin" value="${isAdmin}" />
		<g:paginate total="${employeeInstanceTotal}"
			params="${[isAdmin:isAdmin]}" />
	</div>
	</g:if>
</body>
</html>
