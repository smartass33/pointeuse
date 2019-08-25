<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Period"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="grails.converters.JSON"%>

<!doctype html>
<html>
<head>
	<g:javascript library="application"/> 		
	<r:require module="report"/>		
	<r:layoutResources/>		
	<g:set var="funtionCheckBoxesMap" value="${[:]}"/>
	<%
		funtionCheckBoxesMap.put('Technicien',true)
		funtionCheckBoxesMap.put('Infirmier',true)
		funtionCheckBoxesMap.put('Secrétaire',true)
		funtionCheckBoxesMap.put('Coursier',false)
		funtionCheckBoxesMap.put('Agent entretien',false)
	%>
  	
	<meta name="layout" content="main" id="mainLayout">
	<g:set var="isNotSelected" value="true" />	
	<g:set var="entityName" value="${message(code: 'employee.label', default: 'Employee')}" />
	<title><g:message code="weekly.recap" /></title>
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
			th.domicile { display: none; } td.domicile { display: none; }	
			table.showDomicile th.domicile { display: table-cell;  } 
			table.showDomicile th.employee { display: none; } 
			table.showDomicile td.domicile { display: table-cell;  }
			table.showDomicile td.employee { display: none; }
			table.hideDomicile th.domicile { display: none; } 
			table.hideDomicile td.domicile  { display: none;}	
		</style>
		
	<script type="text/javascript">

		var WeekJSClass = {
				  setParams: function(type,id,siteId,periodId,simpleFuntionCheckBoxesMap) {
					  var res = type.concat('-', id);
					  val = document.getElementById(res);
					  if(val === null){
						  WeekJSClass.dynamicParams = {value:null,myDate:id,siteId:siteId,type:type,periodId:periodId,simpleFuntionCheckBoxesMap:simpleFuntionCheckBoxesMap};  
					  }else{
						  WeekJSClass.dynamicParams = {value:val.value,myDate:id,siteId:siteId,type:type,periodId:periodId,simpleFuntionCheckBoxesMap:simpleFuntionCheckBoxesMap};   		
						}		
					  }   		      
				  }
				
	</script>		

</head>
<body>



	<div class="nav" id="nav">
		<g:headerMenu />
	</div>	
	<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>	
	<div id="daily-list" class="standardNav">
		<h1>
			<g:message code="weekly.recap"/>
			<br>
			<g:if test="${site!=null}"><g:message code="site.label"/>:${site.name}</g:if>
			<br>
			<g:form method="POST" url="[controller:'employee', action:'weeklyReport']">
				<ul>	
					<li><g:message code="laboratory.label" default="Search" style="vertical-align: middle;" /> </li>
					<li>	
						<g:if test="${siteId != null && !siteId.equals('')}">
							<g:select name="siteId" from="${Site.list([sort:'name'])}"
								noSelection="${['':site.name]}" optionKey="id" optionValue="name"
								style="vertical-align: middle;" />
						</g:if>
						<g:else>
							<g:select name="siteId" from="${Site.list([sort:'name'])}"
								noSelection="${['':'-']}" optionKey="id" optionValue="name"/>
						</g:else>	
					</li>
					<li class='datePicker'>	
						<g:select name="periodId" from="${Period.list([sort:'year'])}" id='periodSelect'
							value="${period}"
							noSelection="${['':(period?period:'-')]}" optionKey="id" 
							style="vertical-align: middle;" />
					</li>	
					<li>	
						<g:submitToRemote class="displayButton"
							value="Rapport"
							update="weeklyTable" 
							onLoading="document.getElementById('spinner').style.display = 'inline';"
			                onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'employee', action:'weeklyReport']"/>	
					</li>	
					<li>
						<g:link class="displayButton" action="annualSitesReport" controller="employee" params="[fromWeeklyReport:true]"><g:message code="annual.sites.report"/></g:link>
					</li>	
					<li>	
						<div id='weekly_report_buttons'>
							<input type="button" id='domicileSelector' class='domicileSelector' value="${message(code: 'cases.button.hide')}" onclick="showVal();" />
							<input type="button" id='employeeSelector' class='employeeSelector' value="${message(code: 'cases.button.show')}" onclick="showVal();" />
						</div>						
					</li>					
				</ul>
				<g:if test="${funtionCheckBoxesMap != null}"><g:hiddenField name="funtionCheckBoxesMap" id="funtionCheckBoxesMap" value="${funtionCheckBoxesMap as JSON}" /></g:if>
				<g:if test="${site != null}"><g:hiddenField name="siteId" id="siteId" value="${site.id} " /></g:if>
				<g:if test="${period != null}"><g:hiddenField name="periodId" id="periodId" value="${period.id} " /></g:if>		
				<g:hiddenField name="fromWeeklyReport" value="${true}" />
			</g:form>
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>	
	<div id="weeklyTable">
		<g:weeklyTime/>
	</div>
	
	<g:hiddenField name="detail" value="1" />		
	<script>
		function showVal() {
			if (document.getElementById("detail").value == "1"){
				if (!!document.getElementById('weekly-table'))
			    	document.getElementById('weekly-table').className='hideDomicile';
			    document.getElementById('domicileSelector').style.display = 'none';
			    document.getElementById('employeeSelector').style.display = 'block';
			    document.getElementById("detail").value = "0";
		    }else{
		    	if (!!document.getElementById('weekly-table'))
		    		document.getElementById('weekly-table').className='showDomicile';
				document.getElementById('domicileSelector').style.display = 'block';
				document.getElementById('employeeSelector').style.display = 'none';
				document.getElementById("detail").value = "1";		
		    }
		}
		showVal();
	</script>
</body>

</html>
