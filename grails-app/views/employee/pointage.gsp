<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="pointeuse.InAndOut" %>
<html>
	<head>
		<g:javascript library="prototype" />
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
		  			<table>
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
	  			
	  				<h1>
	  					<g:formatDate format="E dd MMM yyyy'" date="${Calendar.instance.time}"/>
	  					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  					<span id="clock"><g:formatDate format="HH:mm:ss" date="${new Date()}"/></span>
	  					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
	  					<g:message code="employee.daily.time" default="Last Name" />: ${humanTime.get(0)}H${humanTime.get(1)}
	  					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
	  					<g:message code="employee.sup.time" default="Last Name" />: ${dailySupp.get(0)}H${dailySupp.get(1)}
	  				</h1>
	  			<g:if test="${flash.message}">
	  			
				<div class="message" role="status">${flash.message}</div>
				</g:if>
				
				<g:if test="${inAndOuts==null || inAndOuts.size()}">
				<table>
					<thead>
						<g:each in="${inAndOuts}" var="inAndOut">
							<th>${inAndOut.type}</th>
						</g:each>
					</thead>
					<tbody>
						<tr>
							<g:each in="${inAndOuts}" var="inAndOut">						
								<g:if test="${inAndOut.regularization|| inAndOut.systemGenerated==true}">
									<td bgcolor="#cccccc"><font color="red"><g:formatDate format="H:mm:s'" date="${inAndOut.time}"/></font></td>
								</g:if>
								<g:else>
									<td><g:formatDate format="H:mm:s'" date="${inAndOut.time}"/></td>
								</g:else>
							</g:each>
						</tr>
					</tbody>	
				</table>
				</g:if>
				<g:else>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Pas d'évenement pour le jour en cours <BR><BR></g:else>			
				<g:form>
					<g:hiddenField name="userId" value="${employee?.id}" />
					<g:if test="${employee?.status}">
						<g:if test="${!entranceStatus}">
							<g:hiddenField name="type" value="E"/>
							<%entryName='Entrer'%>
						</g:if>
						<g:else>
							<g:hiddenField name="type" value="S"/> 
							<%entryName='Sortir'%>
						</g:else>	
					</g:if>
					<g:else>
						<g:hiddenField name="type" value="E"/> <%entryName='Entrer'%>
					</g:else>			

					<table border="0">
						<tr>
							<td>
								<g:if test="${entryName.equals('Entrer')}">
									<g:link class="entrybutton" action="addingEventToEmployee" params="[userId:employee?.id,type:entryName]">${entryName}</g:link>
								</g:if>
								<g:else>
									<g:link class="exitbutton" action="addingEventToEmployee" params="[userId:employee?.id,type:entryName]">${entryName}</g:link>								
								</g:else>
								</td>
							<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
							
							<td>
								<g:link class="loginbutton" controller="employee" action="reportLight" params="[userId:employee.id]">rapport</g:link>
							</td>									
							<td>					
								<g:link class="loginbutton" url="/">${message(code: 'employee.disconnect.label', default: 'Sortie')}</g:link>
							
							</td>
							<td></td>
							<td><modalbox:createLink controller="inAndOut" action="create" id="${employee.id}" css="loginbutton" title="Ajouter un évenement oublié" width="500"><g:message code="inAndOut.regularization" default="Régul" /></modalbox:createLink></td>						
						</tr>
					</table>
				</g:form>	
				
						
			</div>
	  </div>

				
	</body>
</html>