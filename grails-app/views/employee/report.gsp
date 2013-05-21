 	<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
	<%@ page import="pointeuse.Employee"%>
	<%@ page import="pointeuse.InAndOut"%>
	<%@ page import="pointeuse.AbsenceType"%>
	<%@ page import="pointeuse.MonthlyTotal"%>
<head>
	<g:javascript library="prototype" />

 	<resource:include components="autoComplete, dateChooser"/>
	<resource:dateChooser />
	<modalbox:modalIncludes />	
	<meta name="layout" content="main">
	<g:set var="weeklyRecap" value="0" />
	<title>
		${message(code: 'employee.report.label', default: 'Report')}
	</title>
	<link href="main.css" rel="stylesheet" type="text/css">

	<!--style type="text/css" media="screen">
	input.reportButton {
   	-webkit-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	-moz-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	border-bottom-color:#333;
	border:1px solid #61c4ea;
	background-color:#7cceee;
	border-radius:5px;
	-moz-border-radius:5px;
	-webkit-border-radius:5px;
	color:#333;
	font-family:'Verdana',Arial,sans-serif;
	font-size:14px;
	text-shadow:#b2e2f5 0 1px 0;
	padding:5px
   }
input.reportButton:hover {
   border-top-color: #929ca3;
   background: #929ca3;
   color: #fffaff;
   }
input.reportButton:active {
   border-top-color: #1b435e;
   background: #1b435e;
   }
		
			a.reportButton {
   	-webkit-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	-moz-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	border-bottom-color:#333;
	border:1px solid #61c4ea;
	background-color:#7cceee;
	border-radius:5px;
	-moz-border-radius:5px;
	-webkit-border-radius:5px;
	color:#333;
	font-family:'Verdana',Arial,sans-serif;
	font-size:14px;
	text-shadow:#b2e2f5 0 1px 0;
	padding:5px
   }
a.reportButton:hover {
   border-top-color: #929ca3;
   background: #929ca3;
   color: #fffaff;
   }
a.reportButton:active {
   border-top-color: #1b435e;
   background: #1b435e;
   }
		
	
	</style-->
<style type="text/css">
.reportButton {
	-moz-box-shadow:inset 0px 1px 0px 0px #d9fbbe;
	-webkit-box-shadow:inset 0px 1px 0px 0px #d9fbbe;
	box-shadow:inset 0px 1px 0px 0px #d9fbbe;
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #048802), color-stop(1, #a5cc52) );
	background:-moz-linear-gradient( center top, #048802 5%, #a5cc52 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#048802', endColorstr='#a5cc52');
	background-color:#048802;
	-moz-border-radius:6px;
	-webkit-border-radius:6px;
	border-radius:6px;
	border:1px solid #83c41a;
	display:inline-block;
	color:#ffffff;
	font-family:arial;
	font-size:15px;
	font-weight:bold;
	padding:3px 12px;
	text-decoration:none;
	text-shadow:1px 1px 0px #86ae47;
}.reportButton:hover {
	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #a5cc52), color-stop(1, #048802) );
	background:-moz-linear-gradient( center top, #a5cc52 5%, #048802 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#a5cc52', endColorstr='#048802');
	background-color:#a5cc52;
}.reportButton:active {
	position:relative;
	top:1px;
}

/*
#nav {
  position: absolute;
  top: 0;
  margin-top: 60px;
  margin-bottom: 300px;
  
  border-top: 1px solid purple;
  padding-top: 0px;
  padding-bottom: 0px;
}

#nav.fixed {
  position: fixed;
  top: 0;
}

*/
</style>
</head>
<body>
<!--script type="text/javascript">
$(document).ready(function () {  
  var top = $('#nav').offset().top - parseFloat($('#nav').css('marginTop').replace(/auto/, 0));
  $(window).scroll(function (event) {
    // what the y position of the scroll is
    var y = $(this).scrollTop();
  
    // whether that's below the form
    if (y >= top) {
      // if so, ad the fixed class
      $('#nav').addClass('fixed');
    } else {
      // otherwise remove it
      $('#nav').removeClass('fixed');
    }
  });
});
</script-->
<g:form method="post" >
	<a href="#list-employee" class="skip" tabindex="-1"><g:message
			code="default.link.skip.label" default="Skip to content&hellip;" /></a>
	<div id="list-employee" class="content scaffold-list">
		<div>
			<h1>BIOLAB33
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</h1>
		</div>
		<div class="nav" role="navigation" id="nav">
			<ul>
				<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li style="vertical-align: middle;"><g:link class="list" action="list"
						params="${[isAdmin:isAdmin,siteId:siteId,back:true]}">
						${message(code: 'employee.back.to.list.label', default: 'List')}
					</g:link></li>
				<li style="vertical-align: middle;">
					${message(code: 'default.period.label', default: 'List')}: <g:datePicker
						name="myDate" value="${period ? period : new Date()}" 
						precision="month" noSelection="['':'-Choose-']" style="vertical-align: middle;"/>
						<g:hiddenField name="userId" value="${userId}" />
						<g:hiddenField name="siteId" value="${siteId}" />	
				</li>
				<li style="vertical-align: middle;">
					<g:actionSubmit value="afficher" action="report" class="reportButton"/>						
				</li>
				<li style="vertical-align: middle;">
					<g:actionSubmit value="appliquer"  action="modifyTime" class="reportButton"/>		
				</li>
				<li style="vertical-align: middle;">
					<g:actionSubmit value="pdf"  action="pdf" class="reportButton"/>		
				</li>	
								<li style="vertical-align: middle;">
					<g:actionSubmit value="sendMail"  action="sendMail" class="reportButton"/>		
				</li>	
				
			
				<li style="vertical-align: middle;"><modalbox:createLink controller="inAndOut" action="create"
						css="reportButton" id="${userId}"
						title="Ajouter un évenement oublié" width="500"
						params="[complete:true,report:true]">
						<g:message code="inAndOut.regularization" default="Régul" />
					</modalbox:createLink></li>
			</ul>
			
						<BR/>
		<g:if test="${flash.message}">
			<div class="message">
				${flash.message}
			</div>
			

		</g:if>
		
	
	</div>
				<div id="updateDiv2" >
				<g:cartouche/>
			</div>
	<div id="updateDiv3" >
		<g:reportTable/>
	</div>
			
		</div>

	</g:form>
	</body>
