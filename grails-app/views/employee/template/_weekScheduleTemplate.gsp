<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.WeeklyTotal"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="grails.converters.JSON"%>


<g:set var="j" value="${1}"/>
<g:set var="eventColor" 	value=""/>
<g:set var="calendarMonday" value="${Calendar.instance}"/>
<g:set var="calendarSaturday" value="${Calendar.instance}"/>
<g:if test="${firstYear != null}">
	<%
		calendarMonday.set(Calendar.YEAR,firstYear)
		calendarSaturday.set(Calendar.YEAR,firstYear)	
	 %>
</g:if>



<table class="weeklyTable">
	<tr>
		<g:each in="${employeeSiteList}" var="employee">
			<th>${employee.lastName}</th>
		</g:each>
	</tr>
	<g:each in="${statusMapByTime}" var="items">
		<tr>
			<td><span style="font-size: xx-small;">${items.key.format('HH:mm')}</span></td>
			
			<g:each in="${items.value}" var="listItem">
				<% eventColor = (listItem) ? '#00FF00' : 'grey' %>
				<td 
					bgcolor="${eventColor}" 
					style="
					border-width:1px;
	 				border-style:solid; 
	 				border-color:white;">			
				</td>
			</g:each>
		</tr>
	</g:each>
</table>
