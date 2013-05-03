<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<g:javascript library="jquery" plugin="jquery" />

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<meta name="layout" content="main" />
<title>Modifier les évenements du jour</title>

<script type="text/javascript">
function removeElements()
{
	alert('toto');
	
	var elem = document.getElementById('elemTable');
	elem.parentNode.removeChild(elem);
	
	}

$('.deleteThisRow').live('click',function(){
     var rowLength = $('.row').length;
         //this line makes sure that we don't ever run out of rows.
      if(rowLength > 1){
   deleteRow(this);
  }else{
   $('.kikoo tr:last').after(appendRow);
   deleteRow(this);
  }
 });

 function deleteRow(currentNode){
  $(currentNode).parent().parent().remove();
 }
 });

</script>

</head>
<body>
	<div class="body">
		<table>
			<th colspan='5'>évènements du: ${day+'/'+month+'/'+year}</th>
		</table>
		<table id="elemTable">
			<div id="inAndOutListDiv">		
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
			</div>
		</table>
	</div>
</body>
</html>