<h1>
	<g:formatDate format="E dd MMM yyyy'" date="${Calendar.instance.time}" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span
		id="clock"><g:formatDate format="HH:mm:ss" date="${new Date()}" /></span>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<g:message code="employee.daily.time" default="Last Name" />
	:
	${humanTime.get(0)}H${humanTime.get(1)}
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<g:message code="employee.sup.time" default="Last Name" />
	:
	${dailySupp.get(0)}H${dailySupp.get(1)}
</h1>
<g:if test="${flash.message}">

	<div class="message" role="status">
		${flash.message}
	</div>
</g:if>

<g:if test="${inAndOuts==null || inAndOuts.size()}">
	<table border="1">
		<thead>
			<g:each in="${inAndOuts}" var="inAndOut">
				<th>
					${inAndOut.type}
				</th>
			</g:each>
		</thead>
		<tbody>
			<tr>
				<g:each in="${inAndOuts}" var="inAndOut">
					<g:if
						test="${inAndOut.regularization|| inAndOut.systemGenerated==true}">
						<td bgcolor="#cccccc"><font color="red"><g:formatDate
									format="H:mm:s'" date="${inAndOut.time}" /></font></td>
					</g:if>
					<g:else>
						<g:if test="${inAndOut.type.equals('E')}">
							<td bgcolor="98FB98"><g:formatDate format="H:mm:s'"
									date="${inAndOut.time}" /></td>
						</g:if>
						<g:else>
							<td bgcolor="#FFC0CB"><g:formatDate format="H:mm:s'"
									date="${inAndOut.time}" /></td>
						</g:else>
					</g:else>
				</g:each>
			</tr>
		</tbody>
	</table>
</g:if>
<g:else>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Pas d'évenement pour le jour en cours <BR>
	<BR>
</g:else>
	
	<g:hiddenField name="userId" value="${employee?.id}" />
	<g:if test="${entranceStatus}">
			<g:hiddenField name="type" value="S" />
			<%entryName='Sortir'%>
	</g:if>
	<g:else>
		<g:hiddenField name="type" value="E" />
		<%entryName='Entrer'%>
	</g:else>

	<g:hiddenField name="userId" value="${employee?.id}" />
	<g:if test="${entranceStatus}">
			<g:hiddenField name="type" value="S" />
			<%entryName='Sortir'%>
	</g:if>
	<g:else>
		<g:hiddenField name="type" value="E" />
		<%entryName='Entrer'%>
	</g:else>
	<table border="0">
		<tr>
			<td>
				<g:if test="${entranceStatus}">
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="exitbutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:if> 
				<g:else>
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="entrybutton" params="[userId:employee?.id,type:entryName]">${entryName}</g:remoteLink>
				</g:else>
			</td>
						<td>
				<g:if test="${entranceStatus}">
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="exitbutton" params="[userId:employee?.id,type:entryName,isOutsideSite:true]">${entryName} EXT</g:remoteLink>
				</g:if> 
				<g:else>
					<g:remoteLink action="addingEventToEmployee" update="currentDay"
					class="entrybutton" params="[userId:employee?.id,type:entryName,isOutSideSite:true]">${entryName} EXT</g:remoteLink>
				</g:else>
			</td>
			
			<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
			<td><g:link class="modalbox" controller="employee"
					action="reportLight" params="[userId:employee.id]">rapport</g:link>
			</td>
			<td><g:link class="modalbox" url="/">
					${message(code: 'employee.disconnect.label', default: 'Sortie')}
				</g:link></td>
			<td></td>
			<td><modalbox:createLink controller="inAndOut" action="create"
					id="${employee.id}" css="modalbox"
					title="Ajouter un évenement oublié" width="500">
					<g:message code="inAndOut.regularization" default="Régul" />
				</modalbox:createLink></td>
		</tr>
	</table>
