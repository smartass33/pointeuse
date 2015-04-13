<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Site"%>
<!doctype html>
<html>
	<head>
		<g:javascript library="jquery" plugin="jquery" />
		<meta name="layout" content="main" id="mainLayout">
		<g:set var="isNotSelected" value="true" />
		<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
		<link rel="shortcut icon" href="../favicon.ico"> 
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
			
			#detailSelector { display: none; }
			th.admin { display: none; } td.admin { display: none; }
	
			table.showDetail th.admin { display: table-cell;  } 
			table.showDetail th.principal { display: none; } 
			table.showDetail td.admin { display: table-cell;  }
			table.showDetail td.principal { display: none; }
			table.hideDetail th.hidePrincipal { display: none; } table.hidePrincipal td.hidePrincipal  { display: none;}	
		</style>
	</head>
	<body>
		<g:hiddenField name="detail" value="0" />
		
		<script>
		function showVal() {
			if (document.getElementById("detail").value == "1"){
			    document.getElementById('employee-table').className='hideDetail';
			    document.getElementById('detailSelector').style.display = 'none';
			    document.getElementById('principalSelector').style.display = 'block';
			    document.getElementById("detail").value = "0";
		    }else{
		    	document.getElementById('employee-table').className='showDetail';
				document.getElementById('detailSelector').style.display = 'block';
				document.getElementById('principalSelector').style.display = 'none';
				document.getElementById("detail").value = "1";		
		    }
		}
		</script>
		<div class="nav" id="nav">
			<g:headerMenu />
		</div>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>
		
		<div id="list-employee" class="standardNav">
			<h1>
				<g:message code="default.list.label" args="[entityName]" />
				<br>
				<br>
				<g:form method="POST" url="[controller:'employee', action:'getSitePDF']">
				<ul>
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /></li>
					<li>
						<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':site.name]}" optionKey="id" optionValue="name"
								onChange="${remoteFunction(action:'list', params:'\'site=\'+this.value+\'&isAdmin=\'+\'' + isAdmin + '\'',update:'divId',onLoading:"document.getElementById('spinner').style.display = 'inline';",onComplete:"document.getElementById('spinner').style.display = 'none';")}"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="site.id" from="${Site.list([sort:'name'])}"
								noSelection="${['':'-']}" optionKey="id" optionValue="name"
								onChange="${remoteFunction(action:'list', params:'\'site=\'+this.value+\'&isAdmin=\'+\'' + isAdmin + '\'',update:'divId',onLoading:"document.getElementById('spinner').style.display = 'inline';",onComplete:"document.getElementById('spinner').style.display = 'none';")}" />
						</g:else>		
					</li>
					<li><g:remoteField id="q" action="search" update="divId"
						onclick="if (this.value=='chercher un salarié') {this.value = '';}"
						onLoading="document.getElementById('spinner').style.display = 'inline';"
				        onComplete="document.getElementById('spinner').style.display = 'none';"
						name="q" value="${params.q ?: 'chercher un salarié'}" paramName="q"
						style="vertical-align: middle;"
						params="\'q=\'+this.value+\'&isAdmin=\'+\'${isAdmin}+\'"/>
					</li>
					<li class="datePicker"><g:datePicker
								name="myDate" value="${period ? period : new Date()}"  relativeYears="[-3..5]"
								precision="month" noSelection="['':'-Choisissez-']" style="vertical-align: middle;"/>
					</li>
					
					<li style="vertical-align: middle;">
						<g:actionSubmit disabled="${period!=null}"  value="PDF" action="getSitePDF" class="${period!=null ? 'pdfButtonDisabled':'pdfButton'}" />
					</li>	
					
					
					<g:hiddenField name="isAdmin" value="${isAdmin}" />
					<g:hiddenField name="siteId" value="${siteId}" />
				</ul>
				</g:form>		
			</h1>
			<g:if test="${flash.message}">
				<div class="message" id="flash">
					${flash.message}
				</div>
			</g:if>
		</div>
		<br>
		<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>
		<div id="divId"><g:listEmployee /></div>
		<g:if test="${employeeInstanceTotal!=null}">
			<div class="pagination" id="pagination">
				<g:hiddenField name="isAdmin" value="${isAdmin}" />
				<g:paginate total="${employeeInstanceTotal}" params="${[isAdmin:isAdmin]}" />
			</div>
		</g:if>
	</body>
</html>
