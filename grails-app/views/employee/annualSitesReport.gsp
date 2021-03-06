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
	<title><g:message code="annual.recap" /></title>
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
			<g:message code="annual.recap"/>
			<br>
			<br>
			<g:form method="POST" url="[controller:'employee', action:'annualSiteReport']">
				<ul>	
					<li class='datePicker'>	
						<g:select name="periodId" from="${Period.list([sort:'year'])}" id='periodSelect'
							value="${period}"
							noSelection="${['':(period?period:'-')]}" optionKey="id" 
							style="vertical-align: middle;" />
					</li>	
					<li>	
						<g:submitToRemote class="displayButton"
							value="Rapport"
							update="annualSiteTable" 
							onLoading="document.getElementById('spinner').style.display = 'inline';"
			                onComplete="document.getElementById('spinner').style.display = 'none';"
							url="[controller:'employee', action:'annualSitesReport']"
						/>	
					</li>							
				</ul>
				<g:if test="${funtionCheckBoxesMap != null}"><g:hiddenField name="funtionCheckBoxesMap" id="funtionCheckBoxesMap" value="${funtionCheckBoxesMap as JSON}" /></g:if>
				<g:if test="${site != null}"><g:hiddenField name="siteId" id="siteId" value="${site.id} " /></g:if>
				<g:if test="${period != null}"><g:hiddenField name="periodId" id="periodId" value="${period.id} " /></g:if>		
			</g:form>
		</h1>
		<g:if test="${flash.message}">
			<div class="message" id="flash">
				${flash.message}
			</div>
		</g:if>
	</div>	
	<div id="annualSiteTable">
		<g:annualSiteTime/>
	</div>
</body>
</html>
