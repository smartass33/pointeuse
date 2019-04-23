<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.WeeklyTotal"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="grails.converters.JSON"%>

<g:set var="j" value="${1}"/>
<g:set var="calendarMonday" value="${Calendar.instance}"/>
<g:set var="calendarSaturday" value="${Calendar.instance}"/>
<g:if test="${firstYear != null}">
	<%
		calendarMonday.set(Calendar.YEAR,firstYear)
		calendarSaturday.set(Calendar.YEAR,firstYear)	
	 %>
</g:if>

POUET
<table >
	<tr>
		<g:each in="${ (630..< 2000) }" var="index">
			<g:if test="${(index % 5) == 0 }">
			<td bgcolor="#00FF00" style="width:1px;
				border-width:1px;
 				border-style:solid; 
 				border-color:red;
 				transform: scale(0.5, 1);" id="${index}">
 			1</td>
 			</g:if>
		</g:each>
	</tr>
	
</table>
