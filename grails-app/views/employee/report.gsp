
<%@ page
	import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>
<%@ page import="pointeuse.Reason"%>

<head>
<resource:include components="autoComplete, dateChooser" />
<resource:dateChooser />
<resource:tooltip />
<meta name="layout" content="main">
<g:set var="weeklyRecap" value="0" />
<title>
	${message(code: 'employee.report.label', default: 'Report')}
</title>
<link href="main.css" rel="stylesheet" type="text/css">


<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="/pointeuse/js/jquery-ui-timepicker-addon.js"></script>

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

.overlay {
	background-color: rgba(0, 0, 0, 0.6);
	bottom: 0;
	cursor: default;
	left: 0;
	opacity: 0;
	position: fixed;
	right: 0;
	top: 0;
	visibility: hidden;
	z-index: 1;
	-webkit-transition: opacity .5s;
	-moz-transition: opacity .5s;
	-ms-transition: opacity .5s;
	-o-transition: opacity .5s;
	transition: opacity .5s;
}

.overlay:target {
	visibility: visible;
	opacity: 1;
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
</style>
</head>

<body>




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
				<li style="vertical-align: middle;"><a class="home"
					href="${createLink(uri: '/')}"><g:message
							code="default.home.label" /></a></li>
				<li style="vertical-align: middle;"><g:link class="list"
						action="list"
						params="${[isAdmin:isAdmin,siteId:siteId,back:true]}">
						${message(code: 'employee.back.to.list.label', default: 'List')}
					</g:link></li>
				<li style="vertical-align: middle;">
					${message(code: 'default.period.label', default: 'List')}: <g:datePicker
						name="myDate" value="${period ? period : new Date()}"
						precision="month" noSelection="['':'-Choose-']"
						style="vertical-align: middle;" /> <g:hiddenField name="userId"
						value="${userId}" /> <g:hiddenField name="siteId"
						value="${siteId}" /> <g:hiddenField name="currentMonth"
						value='${period ? period.format("M") : (new Date()).format("M")}' />
					<g:hiddenField name="currentYear"
						value='${period ? period.format("yyyy") : (new Date()).format("yyyy")}' />

				</li>
				<li style="vertical-align: middle;"><g:actionSubmit
						value="afficher" action="report" class="listButton" /></li>
				
				<li style="vertical-align: middle;"><g:actionSubmit value="pdf"
						action="userPDF" class="listButton" /></li>
			</g:form>
				<li>


					<div>
						<div>
							<a href="#join_form" id="join_pop">Ajouter un élement</a>
						</div>

					</div> <a href="#x" class="overlay" id="join_form"></a>
					<div class="popup">
						<h2>Creer Entrée/Sortie</h2>
						<p>Renseignez les informations pour creer un nouvel évènement</p>
						<g:form action="create">
							<table>
								<tbody>
									<tr class="prop">
										<td class="name" valign="top">choisissez la date:</td>
										<td class="value" valign="top"><input type="text"
											name="date_picker" id="date_picker" /> <script>
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
							$("#date_picker").datetimepicker();</script></td>
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

							<g:submitToRemote oncomplete="showSpinner(false)"
								onloading="showSpinner(true)" update="c"
								url="[controller:'inAndOut', action:'save']" value="Creer"></g:submitToRemote>
						</g:form>
						<a class="close" href="#close"></a>
					</div>


				</li>
				<form method="POST">
				<li style="vertical-align: middle;"><g:actionSubmit
						value="appliquer" action="modifyTime" class="listButton" /></li>
				<li><a id="legend"
					title="
					<table >
						<tr>
							<td ><g:message code='legend.NORMAL_EVENT' default='Régul' /></td>
						</tr>
						<tr>
							<td style='color : red;font-weight: bold;'><g:message code='legend.INITIALE_SALARIE' default='Régul' /></td>
						</tr>
						<tr>
							<td style='color : orange;font-weight: bold;'><g:message code='legend.MODIFIEE_SALARIE' default='Régul' /></td>
						</tr>
						<tr>
							<td style='color : blue;font-weight: bold;'><g:message code='legend.INITIALE_ADMIN' default='Régul' /></td>
						</tr>
						<tr>
							<td style='color : green;font-weight: bold;'><g:message code='legend.MODIFIEE_ADMIN' default='Régul' /></td>
						</tr>
						<tr>
							<td style='font-weight: bold;'><g:message code='legend.SYSTEM_GENERATED' default='Régul' /></td>
						</tr>
						
						</table>"><g:message
							code='legend.label' default='Régul' /></a> <richui:tooltip
						id="legend" /></li>
						
						


			</ul>
			<BR />
			<g:if test="${flash.message}">
				<div class="message">
					${flash.message}
				</div>
			</g:if>
		</div>






		<div id="updateDiv2">
			<g:cartouche />
		</div>
		<div id="updateDiv3">
			<g:reportTable />
		</div>
	</div>
	</form>
</body>
