<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>
<%@ page import="pointeuse.Reason"%>

<head>
<r:require module="jquery"/>
<r:require module="jquery-ui"/>
<resource:tooltip />

<g:javascript library="application"/> 
<r:layoutResources/>
<meta name="layout" content="main">
<g:set var="weeklyRecap" value="0" />
<title>${message(code: 'employee.report.label', default: 'Report')}</title>
<link href="/${grailsApplication.config.context}/css/main.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="/${grailsApplication.config.context}/js/jquery-ui-timepicker-addon.js"></script>


<style>
	.main {
		background: #aaa url(../images/bg.jpg) no-repeat;
		width: 800px;
		height: 600px;
		margin: 50px auto;
	}
	
	.panel {
		background-color: #444;
		height: 34px;
		padding: 10px;
	}
	
	.panel a#login_pop,.panel a#join_pop {
		border: 2px solid #aaa;
		color: #fff;
		display: block;
		float: right;
		margin-right: 10px;
		padding: 5px 10px;
		text-decoration: none;
		text-shadow: 1px 1px #000;
		-webkit-border-radius: 10px;
		-moz-border-radius: 10px;
		-ms-border-radius: 10px;
		-o-border-radius: 10px;
		border-radius: 10px;
	}
	
	a#login_pop:hover,a#join_pop:hover {
		border-color: #eee;
	}
	
	.popup {
		background-color: #fff;
		border: 3px solid #fff;
		display: inline-block;
		left: 50%;
		opacity: 0;
		padding: 15px;
		position: fixed;
		text-align: justify;
		top: 40%;
		visibility: hidden;
		z-index: 10;
		-webkit-transform: translate(-50%, -50%);
		-moz-transform: translate(-50%, -50%);
		-ms-transform: translate(-50%, -50%);
		-o-transform: translate(-50%, -50%);
		transform: translate(-50%, -50%);
		-webkit-border-radius: 10px;
		-moz-border-radius: 10px;
		-ms-border-radius: 10px;
		-o-border-radius: 10px;
		border-radius: 10px;
		-webkit-box-shadow: 0 1px 1px 2px rgba(0, 0, 0, 0.4) inset;
		-moz-box-shadow: 0 1px 1px 2px rgba(0, 0, 0, 0.4) inset;
		-ms-box-shadow: 0 1px 1px 2px rgba(0, 0, 0, 0.4) inset;
		-o-box-shadow: 0 1px 1px 2px rgba(0, 0, 0, 0.4) inset;
		box-shadow: 0 1px 1px 2px rgba(0, 0, 0, 0.4) inset;
		-webkit-transition: opacity .5s, top .5s;
		-moz-transition: opacity .5s, top .5s;
		-ms-transition: opacity .5s, top .5s;
		-o-transition: opacity .5s, top .5s;
		transition: opacity .5s, top .5s;
	}
	
	.overlay:target+.popup {
		top: 50%;
		opacity: 1;
		visibility: visible;
	}
	
	.close {
		background-color: rgba(0, 0, 0, 0.8);
		height: 30px;
		line-height: 30px;
		position: absolute;
		right: 0;
		text-align: center;
		text-decoration: none;
		top: -15px;
		width: 30px;
		-webkit-border-radius: 15px;
		-moz-border-radius: 15px;
		-ms-border-radius: 15px;
		-o-border-radius: 15px;
		border-radius: 15px;
	}
	
	.close:before {
		color: rgba(255, 255, 255, 0.9);
		content: "X";
		font-size: 24px;
		text-shadow: 0 -1px rgba(0, 0, 0, 0.9);
	}
	
	.close:hover {
		background-color: rgba(64, 128, 128, 0.8);
	}
	
	.popup p,.popup div {
		margin-bottom: 10px;
	}
	
	.popup label {
		display: inline-block;
		text-align: left;
		width: 120px;
	}
	
	.popup input[type="text"],.popup input[type="password"] {
		border: 1px solid;
		border-color: #999 #ccc #ccc;
		margin: 0;
		padding: 2px;
		-webkit-border-radius: 2px;
		-moz-border-radius: 2px;
		-ms-border-radius: 2px;
		-o-border-radius: 2px;
		border-radius: 2px;
	}
	
	.popup input[type="text"]:hover,.popup input[type="password"]:hover {
		border-color: #555 #888 #888;
	}
	
	.ui-timepicker-div .ui-widget-header {
		margin-bottom: 8px;
	}
	
	.ui-timepicker-div dl {
		text-align: left;
	}
	
	.ui-timepicker-div dl dt {
		float: left;
		clear: left;
		padding: 0 0 0 5px;
	}
	
	.ui-timepicker-div dl dd {
		margin: 0 10px 10px 45%;
	}
	
	.ui-timepicker-div td {
		font-size: 90%;
	}
	
	.ui-tpicker-grid-label {
		background: none;
		border: none;
		margin: 0;
		padding: 0;
	}
	
	.ui-timepicker-rtl {
		direction: rtl;
	}
	
	.ui-timepicker-rtl dl {
		text-align: right;
		padding: 0 5px 0 0;
	}
	
	.ui-timepicker-rtl dl dt {
		float: right;
		clear: right;
	}
	
	.ui-timepicker-rtl dl dd {
		margin: 0 45% 10px 10px;
	}
	
	.reportTable {
		margin:0px;padding:0px;
		width:100%;
		border:1px solid #ffffff;
		
		-moz-border-radius-bottomleft:8px;
		-webkit-border-bottom-left-radius:8px;
		border-bottom-left-radius:8px;
		
		-moz-border-radius-bottomright:8px;
		-webkit-border-bottom-right-radius:8px;
		border-bottom-right-radius:8px;
		
		-moz-border-radius-topright:8px;
		-webkit-border-top-right-radius:8px;
		border-top-right-radius:8px;
		
		-moz-border-radius-topleft:8px;
		-webkit-border-top-left-radius:8px;
		border-top-left-radius:8px;
	}.reportTable table{
	    border-collapse: collapse;
	        border-spacing: 0;
		width:100%;
		height:100%;
		margin:0px;padding:0px;
	}.reportTable tr:last-child td:last-child {
		-moz-border-radius-bottomright:8px;
		-webkit-border-bottom-right-radius:8px;
		border-bottom-right-radius:8px;
	}
	.reportTable table tr:first-child td:first-child {
		-moz-border-radius-topleft:8px;
		-webkit-border-top-left-radius:8px;
		border-top-left-radius:8px;
	}
	.reportTable table tr:first-child td:last-child {
		-moz-border-radius-topright:8px;
		-webkit-border-top-right-radius:8px;
		border-top-right-radius:8px;
	}.reportTable tr:last-child td:first-child{
		-moz-border-radius-bottomleft:8px;
		-webkit-border-bottom-left-radius:8px;
		border-bottom-left-radius:8px;
	}.reportTable tr:hover td{
		
	}
	.reportTable tr:nth-child(odd){ background-color:#ffffff; }
	.reportTable tr:nth-child(even)    { background-color:#ffffff; }.reportTable td{
		vertical-align:middle;
		border:1px solid #ffffff;
		border-width:0px 1px 1px 0px;
		text-align:center;
		padding:0px;
		font-size:10px;
		font-family:Arial;
		font-weight:bold;
		color:#000000;
	}.reportTable tr:last-child td{
		border-width:0px 1px 0px 0px;
	}.reportTable tr td:last-child{
		border-width:0px 0px 1px 0px;
	}.reportTable tr:last-child td:last-child{
		border-width:0px 0px 0px 0px;
	}
	.reportTable tr:first-child td{
			background:-o-linear-gradient(bottom, #ffffff 5%, #ffffff 100%);	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ffffff), color-stop(1, #ffffff) );
		background:-moz-linear-gradient( center top, #ffffff 5%, #ffffff 100% );
		filter:progid:DXImageTransform.Microsoft.gradient(startColorstr="#ffffff", endColorstr="#ffffff");	background: -o-linear-gradient(top,#ffffff,ffffff);
	
		background-color:#ffffff;
		border:0px solid #ffffff;
		text-align:center;
		border-width:0px 0px 1px 1px;
		font-size:10px;
		font-family:Arial;
		font-weight:bold;
		color:#000000;
	}
	.reportTable tr:first-child:hover td{
		background:-o-linear-gradient(bottom, #ffffff 5%, #ffffff 100%);	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ffffff), color-stop(1, #ffffff) );
		background:-moz-linear-gradient( center top, #ffffff 5%, #ffffff 100% );
		filter:progid:DXImageTransform.Microsoft.gradient(startColorstr="#ffffff", endColorstr="#ffffff");	background: -o-linear-gradient(top,#ffffff,ffffff);
	
		background-color:#ffffff;
	}
	.reportTable tr:first-child td:first-child{
		border-width:0px 0px 1px 0px;
	}
	.reportTable tr:first-child td:last-child{
		border-width:0px 0px 1px 1px;
	}
	
	
	
	
/* Table 1 Style */
table.table1{
    font-family: "Trebuchet MS", sans-serif;
    font-size: 16px;
    font-weight: bold;
    line-height: 1.4em;
    font-style: normal;
    border-collapse:separate;
}
.table1 thead th{
    padding:15px;
    color:#fff;
    text-shadow:1px 1px 1px #568F23;
    border:1px solid #93CE37;
    border-bottom:3px solid #9ED929;
    background-color:#9DD929;
    background:-webkit-gradient(
        linear,
        left bottom,
        left top,
        color-stop(0.02, rgb(123,192,67)),
        color-stop(0.51, rgb(139,198,66)),
        color-stop(0.87, rgb(158,217,41))
        );
    background: -moz-linear-gradient(
        center bottom,
        rgb(123,192,67) 2%,
        rgb(139,198,66) 51%,
        rgb(158,217,41) 87%
        );
    -webkit-border-top-left-radius:5px;
    -webkit-border-top-right-radius:5px;
    -moz-border-radius:5px 5px 0px 0px;
    border-top-left-radius:5px;
    border-top-right-radius:5px;
}
.table1 thead th:empty{
    background:transparent;
    border:none;
}
.table1 tbody th{
    color:#fff;
    text-shadow:1px 1px 1px #568F23;
    background-color:#9DD929;
    border:1px solid #93CE37;
    border-right:3px solid #9ED929;
    padding:0px 10px;
    background:-webkit-gradient(
        linear,
        left bottom,
        right top,
        color-stop(0.02, rgb(158,217,41)),
        color-stop(0.51, rgb(139,198,66)),
        color-stop(0.87, rgb(123,192,67))
        );
    background: -moz-linear-gradient(
        left bottom,
        rgb(158,217,41) 2%,
        rgb(139,198,66) 51%,
        rgb(123,192,67) 87%
        );
    -moz-border-radius:5px 0px 0px 5px;
    -webkit-border-top-left-radius:5px;
    -webkit-border-bottom-left-radius:5px;
    border-top-left-radius:5px;
    border-bottom-left-radius:5px;
}
.table1 tfoot td{
    color: #9CD009;
    font-size:32px;
    text-align:center;
    padding:10px 0px;
    text-shadow:1px 1px 1px #444;
}
.table1 tfoot th{
    color:#666;
}
.table1 tbody td{
    padding:10px;
    text-align:center;
    background-color:#DEF3CA;
    border: 2px solid #E7EFE0;
    -moz-border-radius:2px;
    -webkit-border-radius:2px;
    border-radius:2px;
    color:#666;
    text-shadow:1px 1px 1px #fff;
}
.table1 tbody span.check::before{
    content : url(../images/check0.png)
}
/* Table 2 Style */
table.table2{
    font-family: Georgia, serif;
    font-size: 18px;
    font-style: normal;
    font-weight: normal;
    letter-spacing: -1px;
    line-height: 1.2em;
    border-collapse:collapse;
    text-align:center;
}
.table2 thead th, .table2 tfoot td{
    padding:20px 10px 40px 10px;
    color:#fff;
    font-size: 26px;
    background-color:#222;
    font-weight:normal;
    border-right:1px dotted #666;
    border-top:3px solid #666;
    -moz-box-shadow:0px -1px 4px #000;
    -webkit-box-shadow:0px -1px 4px #000;
    box-shadow:0px -1px 4px #000;
    text-shadow:1px 1px 1px #000;
}
.table2 tfoot th{
    padding:10px;
    font-size:18px;
    text-transform:uppercase;
    color:#888;
}
.table2 tfoot td{
    font-size:36px;
    color:#EF870E;
    border-top:none;
    border-bottom:3px solid #666;
    -moz-box-shadow:0px 1px 4px #000;
    -webkit-box-shadow:0px 1px 4px #000;
    box-shadow:0px 1px 4px #000;
}
.table2 thead th:empty{
    background:transparent;
    -moz-box-shadow:none;
    -webkit-box-shadow:none;
    box-shadow:none;
}
.table2 thead :nth-last-child(1){
    border-right:none;
}
.table2 thead :first-child,
.table2 tbody :nth-last-child(1){
    border:none;
}
.table2 tbody th{
    text-align:right;
    padding:10px;
    color:#333;
    text-shadow:1px 1px 1px #ccc;
    background-color:#f9f9f9;
}
.table2 tbody td{
    padding:10px;
    background-color:#f0f0f0;
    border-right:1px dotted #999;
    text-shadow:-1px 1px 1px #fff;
    text-transform:uppercase;
    color:#333;
}
.table2 tbody span.check::before{
    content : url(../images/check1.png)
}

/* Table 3 Style */
table.table3{
    font-family:Arial;
    font-size: 18px;
    font-style: normal;
    font-weight: normal;
    text-transform: uppercase;
    letter-spacing: -1px;
    line-height: 1.7em;
    text-align:center;
    border-collapse:collapse;
}
.table3 thead th{
    padding:6px 10px;
    text-transform:uppercase;
    color:#444;
    font-weight:bold;
    text-shadow:1px 1px 1px #fff;
    border-bottom:5px solid #444;
}
.table3 thead th:empty{
    background:transparent;
    border:none;
}
.table3 thead :nth-child(2),
.table3 tfoot :nth-child(2){
    background-color: #7FD2FF;
}
.table3 tfoot :nth-child(2){
    -moz-border-radius:0px 0px 0px 5px;
    -webkit-border-bottom-left-radius:5px;
    border-bottom-left-radius:5px;
}
.table3 thead :nth-child(2){
    -moz-border-radius:5px 0px 0px 0px;
    -webkit-border-top-left-radius:5px;
    border-top-left-radius:5px;
}
.table3 thead :nth-child(3),
.table3 tfoot :nth-child(3){
    background-color: #45A8DF;
}
.table3 thead :nth-child(4),
.table3 tfoot :nth-child(4){
    background-color: #2388BF;
}
.table3 thead :nth-child(5),
.table3 tfoot :nth-child(5){
    background-color: #096A9F;
}
.table3 thead :nth-child(5){
    -moz-border-radius:0px 5px 0px 0px;
    -webkit-border-top-right-radius:5px;
    border-top-right-radius:5px;
}
.table3 tfoot :nth-child(5){
    -moz-border-radius:0px 0px 5px 0px;
    -webkit-border-bottom-right-radius:5px;
    border-bottom-right-radius:5px;
}
.table3 tfoot td{
    font-size:38px;
    font-weight:bold;
    padding:15px 0px;
    text-shadow:1px 1px 1px #fff;
}
.table3 tbody td{
    padding:10px;
}
.table3 tbody tr:nth-child(4) td{
    font-size:26px;
    font-weight:bold;
}
.table3 tbody td:nth-child(even){
    background-color:#444;
    color:#444;
    border-bottom:1px solid #444;
    background:-webkit-gradient(
        linear,
        left bottom,
        left top,
        color-stop(0.39, rgb(189,189,189)),
        color-stop(0.7, rgb(224,224,224))
        );
    background:-moz-linear-gradient(
        center bottom,
        rgb(189,189,189) 39%,
        rgb(224,224,224) 70%
        );
    text-shadow:1px 1px 1px #fff;
}
.table3 tbody td:nth-child(odd){
    background-color:#555;
    color:#f0f0f0;
    border-bottom:1px solid #444;
    background:-webkit-gradient(
        linear,
        left bottom,
        left top,
        color-stop(0.39, rgb(85,85,85)),
        color-stop(0.7, rgb(105,105,105))
        );
    background:-moz-linear-gradient(
        center bottom,
        rgb(85,85,85) 39%,
        rgb(105,105,105) 70%
        );
    text-shadow:1px 1px 1px #000;
}
.table3 tbody td:nth-last-child(1){
    border-right:1px solid #222;
}
.table3 tbody th{
    color:#696969;
    text-align:right;
    padding:0px 10px;
    border-right:1px solid #aaa;
}
.table3 tbody span.check::before{
    content : url(../images/check2.png)
}
	
	
	.eventTD {
    padding:7px;
    text-align:center;
    background-color:#DEF3CA;
    border: 1px solid #E7EFE0;
    -moz-border-radius:2px;
    -webkit-border-radius:2px;
    border-radius:2px;
    color:#666;
    text-shadow:1px 1px 1px #fff;
    width:80px;
}
	
	</style>

	<script type="text/javascript">
	function closePopup ( ){
		window.location = $('#closeId').attr('href');
	}
	
	 $(document).ready(function() {
   $('#report_table_toggle').click( function() {
    $('#report_table_div').slideToggle(400);
   });

   $('#cartouche_toggle').click( function() {
    $('#cartouche_div').slideToggle(400);
   });

});
	</script>
</head>

<body>


<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}" alt="Patientez pendant le traitement de la requète..." width="16" height="16" />Patientez pendant le traitement de la requète...</div>


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
				<g:form method="post" >
				<li style="vertical-align: middle;"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" /></a></li>
				<li style="vertical-align: middle;"><g:link class="list" action="list" params="${[isAdmin:isAdmin,siteId:siteId,back:true]}">${message(code: 'employee.back.to.list.label', default: 'List')}</g:link></li>
				<li style="vertical-align: middle;">
					${message(code: 'default.period.label', default: 'List')}: <g:datePicker
						name="myDate" value="${period ? period : new Date()}"
						precision="month" noSelection="['':'-Choose-']"
						style="vertical-align: middle;" /> <g:hiddenField name="userId"
						value="${userId}" /> <g:hiddenField name="siteId"
						value="${siteId}" /> <g:hiddenField name="currentMonth"
						value='${period ? period.format("M") : (new Date()).format("M")}' />
					<g:hiddenField name="currentYear" value='${period ? period.format("yyyy") : (new Date()).format("yyyy")}' />
				</li>
				<li style="vertical-align: middle;"><g:actionSubmit value="afficher" action="report" class="listButton" /></li>				
				<li style="vertical-align: middle;"><g:actionSubmit value="pdf" action="userPDF" class="listButton" /></li>
			</g:form>
				<li>
					<div><a href="#join_form" id="join_pop" class="modalbox">Ajouter un élement</a></div>
					<a href="#x" class="overlay" id="join_form"></a>
					<div id="popup" class="popup">
						<h2>Creer Entrée/Sortie</h2>
						<p>Renseignez les informations pour creer un nouvel évènement</p>
						<g:form action="create">
							<table>
								<tbody>
									<tr class="prop">
										<td class="name" valign="top">choisissez la date:</td>
										<td class="value" valign="top"><input type="text"
											name="date_picker" id="date_picker" /> 
										<script>
											$.datepicker.regional['fr'] = {
												closeText: 'Fermer',
												prevText: '<Précédent',
												nextText: 'Suivant>',
												currentText: 'Сегодня',
												monthNames: ['Janvier','Février','Mars','Avril','Mai','Juin',
												'Juillet','Aout','Septembre','Octobre','Novembre','Décembre'],
												monthNamesShort: ['Jan','Fev','Mar','Avr','Mai','Jun',
												'Jui','Аou','Sep','Oct','Nov','Dec'],
												dayNames: ['Dimanche','Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi'],
												dayNamesShort: ['Di','Lu','Ma','Me','Je','Ve','Sa'],
												dayNamesMin: ['D','L','M','M','J','V','S'],
												weekHeader: 'Semaine',
												dateFormat: 'dd/mm/yy',
												firstDay: 1,
												isRTL: false,
												showMonthAfterYear: false,
												yearSuffix: ''
											};
																	
											$.timepicker.regional['fr'] = {
												timeOnlyTitle: 'Horaire',
												timeText: 'Horaire',
												hourText: 'Heure',
												minuteText: 'Minute',
												secondText: 'Seconde',
												millisecText: 'Milliseconde',
												timezoneText: 'Fuseau Horaire',
												currentText: 'Horaire Actuel',
												closeText: 'Fermer',
												timeFormat: 'HH:mm',
												amNames: ['AM', 'A'],
												pmNames: ['PM', 'P'],
												isRTL: false
											};
											$.timepicker.setDefaults($.timepicker.regional['fr']);
											$.datepicker.setDefaults($.datepicker.regional['fr']);							
											$("#date_picker").datetimepicker();
										</script>
										</td>
									</tr>
									<tr class="prop">
										<td class="name" valign="top">Evènement:</td>
										<td class="value" valign="top"><g:select
												name="event.type" from="${['E','S']}"
												valueMessagePrefix="entry.name"
												noSelection="['':'-Choisissez votre élément-']" /></td>
									</tr>
									<tr class="prop">
										<td class="name" valign="top">Raison:</td>
										<td class="value" valign="top"><g:select name="reason.id"
												from="${Reason.list([sort:'name'])}"
												noSelection="['':'-Ajouter une raison-']" optionKey="id"
												optionValue="name" /></td>
									</tr>
								</tbody>
							</table>
							<g:hiddenField name="userId" value="${userId}" />
							<g:hiddenField name="fromReport" value="${true}" />
							<g:submitToRemote oncomplete="showSpinner(false)"
								onloading="showSpinner(true)" update="report_table_div"
								onSuccess="closePopup()"
								url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
						</g:form>
						<a class="close" id="closeId" href="#close"></a>
					</div>
				</li>
				<form method="POST">
				<li style="vertical-align: middle;"><g:actionSubmit value="appliquer" action="modifyTime" class="listButton" /></li>
				<li>
					<a id="legend" title="
					<table>
						<tr><td ><g:message code='legend.NORMAL_EVENT' default='Régul' /></td></tr>
						<tr><td style='color : red;font-weight: bold;'><g:message code='legend.INITIALE_SALARIE' default='Régul' /></td></tr>
						<tr><td style='color : orange;font-weight: bold;'><g:message code='legend.MODIFIEE_SALARIE' default='Régul' /></td></tr>
						<tr><td style='color : blue;font-weight: bold;'><g:message code='legend.INITIALE_ADMIN' default='Régul' /></td></tr>
						<tr><td style='color : green;font-weight: bold;'><g:message code='legend.MODIFIEE_ADMIN' default='Régul' /></td></tr>
						<tr><td style='font-weight: bold;'><g:message code='legend.SYSTEM_GENERATED' default='Régul' /></td></tr>
						</table>">L</a> <richui:tooltip id="legend" />
				</li>
			</ul>
			<BR />
			<g:if test="${flash.message}">
				<div class="message">
					${flash.message}
				</div>
			</g:if>
		</div>
		<div id='cartouche_input_image'>
			<button type='button' id="cartouche_toggle" ><img alt="toggle" src="/${grailsApplication.config.context}/images/glyphicons_190_circle_plus.png"></button>
			Récapitulatifs mensuels et annuels
		</div>
		<div id="cartouche_div">
			<g:cartouche />
		</div> 
		<div id='report_input_image'>
			<button type='button' id="report_table_toggle" ><img alt="toggle" src="/${grailsApplication.config.context}/images/glyphicons_190_circle_plus.png"></button>		
			Détails mensuels
		</div>
		<div id="report_table_div">
			<g:reportTable />
		</div>

	</div>
	</form>
</body>
