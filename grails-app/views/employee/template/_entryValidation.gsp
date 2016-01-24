<%@ page contentType="text/html;charset=UTF-8"%>

<html>
	<head>
		<style>
			h1 {
				color: #48802c;
				font-weight: normal;
				font-size: 1.25em;
				font:40px Georgia, serif;
    			margin: 0px auto;
				text-align: center;
			}
			
			.eventTDEntry {
				size:100%;
			    font:24px Georgia, serif;
			    padding:20px 20px;
			   // padding-right: 40px;     
			   // padding-left: 40px;
			    text-align:center;
			    vertical-align:middle;
			    background-color:#DEF3CA;
			    border: 1px solid #E7EFE0;
			    -moz-border-radius:2px;
			    -webkit-border-radius:2px;
			    border-radius:2px;
			    color:#666;
			    text-shadow:1px 1px 1px #fff;
			    background-color:#98FB98;
			    word-wrap:break-word;
			}
			.eventTDExit {
				size:100%;
			    font:24px Georgia, serif;
			    border: 1px solid #E7EFE0;
			    vertical-align:middle;	
			    padding:20px 20px;
			    padding-right: 40px;     
			    padding-left: 40px;
			    text-align:center;
			    background-color:#DEF3CA;
			    border: 1px solid #E7EFE0;
			    -moz-border-radius:2px;
			    -webkit-border-radius:2px;
			    border-radius:2px;
			    color:#666;
			    text-shadow:1px 1px 1px #fff;
			    background-color:#FFC0CB;
			    word-wrap:break-word;		    
			}
		</style>
	</head>
	<body>
			<g:if test="${employee != null }">
				<h1 style='width:100%;align:center;'>${employee.firstName} ${employee.lastName}</h1>	
			</g:if>
			<g:if test="${flash.message}">
	        	<div class="message" style='width:100%;align:center;'>${flash.message}</div>
	      	</g:if>    
			<g:else>
				<table style="width:100%">
					<tbody>
						<tr>
							<g:if test="${inOrOut != null}">
								<g:if test="${inOrOut.type.equals('E')}">
			                       	<td style="height: 1px;text-align:center;" class="eventTDEntry"><g:message code="employee.entry.validated" default="ENTRY" /><BR><g:formatDate format="dd/MM/yyyy HH:mm'" date="${Calendar.instance.time}" /></td>
								</g:if>
								<g:else>
						        	<td style="height: 1px;text-align:center;" class="eventTDExit" ><g:message code="employee.exit.validated" default="ENTRY" /><BR><g:formatDate format="dd/MM/yyyy HH:mm'" date="${Calendar.instance.time}" /></td>			
								</g:else>	
							</g:if>				
						</tr>						
					</tbody>
				</table>
			</g:else>
		</body>
</html>