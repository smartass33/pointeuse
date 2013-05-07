<%@ page import="pointeuse.InAndOut"%>

		<!-- after="var elem = document.getElementById('elemTable');elem.parentNode.removeChild(elem);"-->
		<div id="mainDiv" class="body">
			
				<table id="elemTable">
					<thead>
						<th colspan='5'>évènements du: ${day+'/'+month+'/'+year}</th>
					</thead>
					<tbody>
						<g:each in="${inAndOutList}" var="inOrOut">
							<tr id="${inOrOut.id}">
						  		<g:if test="${inOrOut.type.equals('E')}">
									<td bgcolor="98FB98" style="height:1px"/>
								</g:if>
								<g:else>
									<td bgcolor="#FFC0CB" style="height:1px"/>
								</g:else>
						  			${inOrOut.time.format('HH:mm')}
						  		<td> 		
						  			<g:form >
								  		<g:hiddenField name="inOrOutId" value="${inOrOut.id}"/>
									  		<g:submitToRemote 
													after="var elem = document.getElementById('${inOrOut.id}');elem.parentNode.removeChild(elem);"									                
													url="[action:'trash']"
									                before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false"
									                value="${message(code: 'inAndOut.delete.element', default: 'Sortie')}" /> 				
							        </g:form>	  		
						        </td> 
						  	</tr>
						 </g:each>
					</tbody>		
				</table>
		</div>
