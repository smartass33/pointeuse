<%@ page import="pointeuse.InAndOut"%>

<g:each in="${inAndOutList}" var="inOrOut">
	<tr id="${inOrOut.id}">
  		<g:if test="${inOrOut.type.equals('E')}">
			<td bgcolor="98FB98" style="height:1px">
		</g:if>
		<g:else>
			<td bgcolor="#FFC0CB" style="height:1px">
		</g:else>
  			${inOrOut.time.format('HH:mm')}
  			</td>
	  		<td> 
	  			<g:submitToRemote 
	  				update="inAndOutListDiv"
	  				before="return confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')"
	  				after="var elem = document.getElementById('elemTable');elem.parentNode.removeChild(elem);"
	                url="[action:'trash',id:inOrOut.id]"
	                value="${message(code: 'inAndOut.delete.element', default: 'Sortie')}" /> 
	        </td> 
  	</tr>
 </g:each>
