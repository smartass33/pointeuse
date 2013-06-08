<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pointeuse.InAndOut" %>
<%@ page import="pointeuse.Employee" %>
<html>
	<head>
		<g:javascript library="jquery" />
		<modalbox:modalIncludes/>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
		<meta name="layout" content="main"/>
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
		</script >
		
		<script type="text/javascript">
			var t;
			window.onload=resetTimer;
			document.onmousemove=resetTimer;
			function logout()
			{
				location.href='/${grailsApplication.config.pointeuse.context}' 
			}
			function resetTimer()
			{
				clearTimeout(t);
				t=setTimeout(logout,60000) //logs out in 1 min
			}
		</script>
				
	</head>
	<body>
		<g:javascript>window.onload = updateClock();window.onload=resetTimer();</g:javascript>	
	  	<div class="body">
	  		<div id="show-employee" class="content scaffold-show" role="main">
	  			<h1>
	  			<font size="5">
	  			<g:message code="employee.label" default="Last Name" />: <g:fieldValue bean="${employee}" field="firstName"/> 
	  			<g:fieldValue bean="${employee}" field="lastName"/>	  				  
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
				  					<td><g:formatDate format="E dd MMM yyyy'" date="${day.key}"/></td>
				  					<g:if test="${totalByDay.get(day.key)!=null}">
				  						<td>${totalByDay.get(day.key).get(0)}H${totalByDay.get(day.key).get(1)}m${totalByDay.get(day.key).get(2)}s</td>
				  					</g:if>
				  					<g:each in="${day.value}" status="l" var="lastInOrOut">	
				  					<g:if test="${lastInOrOut.type.equals('E')}">								
				  						<td bgcolor="98FB98"><g:formatDate format="HH:mm" date="${lastInOrOut.time}"/></td>
				  					</g:if>
				  					<g:else>
				  						<td bgcolor="#FFC0CB"><g:formatDate format="HH:mm" date="${lastInOrOut.time}"/></td>
				  					</g:else>
				  					</g:each>
				  				</tr>
				  			</g:each>
		  				</tbody>
		  			</table>
	  			</div>
	  			<g:form method="POST">
	  			
	  			<div id='currentDay'>
	  				<g:currentDay/>
	  			</div>
	  			</g:form>
			</div>
	  </div>			
	</body>
</html>