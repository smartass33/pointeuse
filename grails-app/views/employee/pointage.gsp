<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Reason"%>

<html>
<head>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<script src="${grailsApplication.config.context}/js/jquery-ui-timepicker-addon.js"></script>

<meta name="layout" content="main" />
<title>Pointeuse Journalière</title>

<script type="text/javascript">		
	function updateClock ( )
	{
	  var currentTime = new Date ( );
	
	  var currentHours = currentTime.getHours ( );
	  var currentMinutes = currentTime.getMinutes ( );
	  var currentSeconds = currentTime.getSeconds ( );
	
	  // Pad the minutes and seconds with leading zeros, if required
	  currentMinutes = ( currentMinutes < 10 ? "0" : "" ) + currentMinutes;
	  currentSeconds = ( currentSeconds < 10 ? "0" : "" ) + currentSeconds;
	
	  // Compose the string for display
	  var currentTimeString = currentHours + ":" + currentMinutes + ":" + currentSeconds  //+ " " + timeOfDay;
	
	  // Update the time display
	   var elem = document.getElementById('clock');
	   if (null!=elem){
	     document.getElementById('clock').firstChild.nodeValue = currentTimeString;
	   }else{
	   	setInterval('updateClock()', 1000 );
	   }
	}
</script>

<script type="text/javascript">
	var t;
	window.onload=resetTimer;
	document.onmousemove=resetTimer;
	function logout()
	{
		location.href='${grailsApplication.config.serverURL}/${grailsApplication.config.context}' 
	}
	function resetTimer()
	{
		clearTimeout(t);
		t=setTimeout(logout,60000) //logs out in 1 min
	}
</script>


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
</style>


<script type="text/javascript">
function closePopup ( ){
	$( ".popup" ).remove();
	window.location.reload()
}
</script>
</head>
<body>
	<g:javascript>window.onload = updateClock();window.onload=resetTimer();</g:javascript>
	<div class="body">
		<div id="show-employee" class="content scaffold-show" role="main">
			<h1>
				<font size="5"> <g:message code="employee.label"
						default="Last Name" />: <g:fieldValue bean="${employee}"
						field="firstName" /> <g:fieldValue bean="${employee}"
						field="lastName" />
			</h1>

			</font>
			<div id="last5days">
				<h1>Evènements des 3 derniers jours</h1>
				<table border="1">
					<thead>
						<th>Date</th>
						<th>Total Journalier</th>
						<th colspan="11">Evenements</th>
					</thead>
					<tbody>
						<g:each in="${mapByDay}" status="k" var="day">
							<tr>
								<td><g:formatDate format="E dd MMM yyyy'" date="${day.key}" /></td>
								<g:if test="${totalByDay.get(day.key)!=null}">
									<td>
										${totalByDay.get(day.key).get(0)}H${totalByDay.get(day.key).get(1)}m${totalByDay.get(day.key).get(2)}s
									</td>
								</g:if>
								<g:each in="${day.value}" status="l" var="lastInOrOut">
									<g:if test="${lastInOrOut.type.equals('E')}">
										<td bgcolor="98FB98"><g:formatDate format="HH:mm"
												date="${lastInOrOut.time}" /></td>
									</g:if>
									<g:else>
										<td bgcolor="#FFC0CB"><g:formatDate format="HH:mm"
												date="${lastInOrOut.time}" /></td>
									</g:else>
								</g:each>
							</tr>
						</g:each>
					</tbody>
				</table>
			</div>
			<g:form>

				<div id='currentDay'>
					<g:currentDay />
				</div>
				<g:hiddenField name="userId" value="${userId}" />

			</g:form>
		</div>
	</div>
</body>
</html>